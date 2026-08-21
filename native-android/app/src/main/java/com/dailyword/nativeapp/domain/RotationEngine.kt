package com.dailyword.nativeapp.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.dailyword.nativeapp.data.*
import com.dailyword.nativeapp.service.CacheRefreshWorker
import com.dailyword.nativeapp.service.RotationReceiver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RotationEngine @Inject constructor(
    private val context: Context,
    private val dao: WordDao,
    private val settings: Settings
) {
    private val alarms get() = context.getSystemService(AlarmManager::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun initialize() = withContext(Dispatchers.IO) {
        seedIfNeeded()
        catchUpAndSchedule()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily-word-cache",
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<CacheRefreshWorker>(15, TimeUnit.MINUTES).build()
        )
    }

    suspend fun catchUpAndSchedule() {
        val current = settings.flow.first()
        if (current.paused || (!current.bibleEnabled && !current.quoteEnabled)) return

        val now = System.currentTimeMillis()
        var bibleAt = current.nextBibleAt
        var quoteAt = current.nextQuoteAt

        if (current.bibleEnabled && (bibleAt == 0L || now >= bibleAt)) {
            if (isWithinActiveHours(current)) rotateBible(current)
            bibleAt = now + current.bibleMinutes * 60_000L
        }
        if (current.quoteEnabled && (quoteAt == 0L || now >= quoteAt)) {
            if (isWithinActiveHours(current)) rotateQuote(current)
            quoteAt = now + current.quoteMinutes * 60_000L
        }

        settings.update { it.copy(nextBibleAt = bibleAt, nextQuoteAt = quoteAt) }
        if (current.bibleEnabled) arm("BIBLE", bibleAt, 1001)
        if (current.quoteEnabled) arm("QUOTE", quoteAt, 1002)
    }

    suspend fun fire(kind: String) {
        val current = settings.flow.first()
        if (current.paused) return

        val now = System.currentTimeMillis()
        if (kind == "BIBLE" && current.bibleEnabled) {
            if (isWithinActiveHours(current)) rotateBible(current)
            val next = now + current.bibleMinutes * 60_000L
            settings.update { it.copy(nextBibleAt = next) }
            arm("BIBLE", next, 1001)
        } else if (kind == "QUOTE" && current.quoteEnabled) {
            if (isWithinActiveHours(current)) rotateQuote(current)
            val next = now + current.quoteMinutes * 60_000L
            settings.update { it.copy(nextQuoteAt = next) }
            arm("QUOTE", next, 1002)
        }
        DailyWordWidget.update(context)
    }

    fun skip(kind: String) = scope.launch {
        val current = settings.flow.first()
        if (kind == "BIBLE" && current.bibleEnabled) {
            rotateBible(current)
            val next = System.currentTimeMillis() + current.bibleMinutes * 60_000L
            settings.update { it.copy(nextBibleAt = next) }
            arm("BIBLE", next, 1001)
        } else if (kind == "QUOTE" && current.quoteEnabled) {
            rotateQuote(current)
            val next = System.currentTimeMillis() + current.quoteMinutes * 60_000L
            settings.update { it.copy(nextQuoteAt = next) }
            arm("QUOTE", next, 1002)
        }
        DailyWordWidget.update(context)
    }

    fun pause() = scope.launch {
        settings.update { it.copy(paused = true) }
        cancel("BIBLE"); cancel("QUOTE")
    }

    fun resume() = scope.launch {
        settings.update { it.copy(paused = false) }
        catchUpAndSchedule()
    }

    fun reschedule(oldKind: String) = scope.launch {
        val current = settings.flow.first()
        val now = System.currentTimeMillis()
        if (oldKind == "BIBLE") {
            val next = now + current.bibleMinutes * 60_000L
            settings.update { it.copy(nextBibleAt = next) }
            arm("BIBLE", next, 1001)
        } else {
            val next = now + current.quoteMinutes * 60_000L
            settings.update { it.copy(nextQuoteAt = next) }
            arm("QUOTE", next, 1002)
        }
    }

    private fun arm(kind: String, at: Long, request: Int) {
        if (at <= System.currentTimeMillis()) return
        val intent = Intent(context, RotationReceiver::class.java).putExtra("kind", kind)
        val pi = PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        if (alarms.canScheduleExactAlarms()) alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        else alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
    }

    private fun cancel(kind: String) {
        val intent = Intent(context, RotationReceiver::class.java).putExtra("kind", kind)
        val request = if (kind == "BIBLE") 1001 else 1002
        PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE).cancel()
    }

    private suspend fun rotateBible(prefs: UserSettings) {
        val now = System.currentTimeMillis()
        var cats = if (prefs.bibleCategories.isEmpty()) setOf("strength", "courage", "peace", "faith", "hope", "love", "wisdom", "success", "perseverance", "gratitude") else prefs.bibleCategories
        if (prefs.dailyThemeEnabled) {
            dailyThemeCategory()?.let { cats = cats + it }
        }
        val candidates = dao.verses().filter { it.category in cats }
        if (candidates.isEmpty()) return
        val next = pickNext(candidates)
        dao.touchVerse(next.id, now)
        dao.history(HistoryEntry(UUID.randomUUID().toString(), next.id, null, now))
    }

    private suspend fun rotateQuote(prefs: UserSettings) {
        val now = System.currentTimeMillis()
        var cats = if (prefs.quoteCategories.isEmpty()) setOf("success", "discipline", "hard_work", "career", "education", "confidence", "leadership", "resilience", "personal_growth") else prefs.quoteCategories
        if (prefs.dailyThemeEnabled) {
            dailyThemeCategory()?.let { cats = cats + it }
        }
        val candidates = dao.quotes().filter { it.category in cats }
        if (candidates.isEmpty()) return
        val next = pickNext(candidates)
        dao.touchQuote(next.id, now)
        dao.history(HistoryEntry(UUID.randomUUID().toString(), null, next.id, now))
    }

    private fun isWithinActiveHours(prefs: UserSettings): Boolean {
        if (!prefs.scheduleEnabled) return true
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.SATURDAY, Calendar.SUNDAY -> hour in 8..22
            else -> hour in 7..22
        }
    }

    private fun dailyThemeCategory(): String? {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "discipline"
            Calendar.TUESDAY -> "faith"
            Calendar.WEDNESDAY -> "courage"
            Calendar.THURSDAY -> "wisdom"
            Calendar.FRIDAY -> "success"
            Calendar.SATURDAY -> "gratitude"
            Calendar.SUNDAY -> "hope"
            else -> null
        }
    }

    private fun pickNext(items: List<com.dailyword.nativeapp.data.BibleVerse>): com.dailyword.nativeapp.data.BibleVerse {
        val unshown = items.filter { it.lastDisplayedAt == null }
        return if (unshown.isNotEmpty()) unshown.random()
        else items.minByOrNull { it.lastDisplayedAt!! } ?: items.random()
    }

    private fun pickNext(items: List<com.dailyword.nativeapp.data.MotivationalQuote>): com.dailyword.nativeapp.data.MotivationalQuote {
        val unshown = items.filter { it.lastDisplayedAt == null }
        return if (unshown.isNotEmpty()) unshown.random()
        else items.minByOrNull { it.lastDisplayedAt!! } ?: items.random()
    }

    private suspend fun seedIfNeeded() {
        if (dao.verseCount() > 0 && dao.quoteCount() > 0) return
        dao.insertVerses(BIBLE_VERSES)
        dao.insertQuotes(MOTIVATIONAL_QUOTES)
    }

    companion object {
        val BIBLE_VERSES = listOf(
            BibleVerse("php4_13", "Philippians", 4, 13, "KJV", "I can do all things through Christ which strengtheneth me.", "strength"),
            BibleVerse("jos1_9", "Joshua", 1, 9, "KJV", "Be strong and of a good courage; be not afraid, neither be thou dismayed: for the Lord thy God is with thee whithersoever thou goest.", "courage"),
            BibleVerse("psa46_10", "Psalms", 46, 10, "KJV", "Be still, and know that I am God: I will be exalted among the heathen, I will be exalted in the earth.", "peace"),
            BibleVerse("pro3_5", "Proverbs", 3, 5, "KJV", "Trust in the Lord with all thine heart; and lean not unto thine own understanding.", "faith"),
            BibleVerse("rom8_28", "Romans", 8, 28, "KJV", "And we know that all things work together for good to them that love God, to them who are the called according to his purpose.", "hope"),
            BibleVerse("mat11_28", "Matthew", 11, 28, "KJV", "Come unto me, all ye that labour and are heavy laden, and I will give you rest.", "peace"),
            BibleVerse("isa40_31", "Isaiah", 40, 31, "KJV", "But they that wait upon the Lord shall renew their strength; they shall mount up with wings as eagles; they shall run, and not be weary; and they shall walk, and not faint.", "strength"),
            BibleVerse("jer29_11", "Jeremiah", 29, 11, "KJV", "For I know the thoughts that I think toward you, saith the Lord, thoughts of peace, and not of evil, to give you an expected end.", "hope"),
            BibleVerse("1jn4_18", "1 John", 4, 18, "KJV", "There is no fear in love; but perfect love casteth out fear: because fear hath torment. He that feareth is not made perfect in love.", "love"),
            BibleVerse("psa23_1", "Psalms", 23, 1, "KJV", "The Lord is my shepherd; I shall not want.", "peace"),
            BibleVerse("mat5_16", "Matthew", 5, 16, "KJV", "Let your light so shine before men, that they may see your good works, and glorify your Father which is in heaven.", "success"),
            BibleVerse("2ti1_7", "2 Timothy", 1, 7, "KJV", "For God hath not given us the spirit of fear; but of power, and of love, and of a sound mind.", "courage"),
            BibleVerse("heb11_1", "Hebrews", 11, 1, "KJV", "Now faith is the substance of things hoped for, the evidence of things not seen.", "faith"),
            BibleVerse("pro18_10", "Proverbs", 18, 10, "KJV", "The name of the Lord is a strong tower: the righteous runneth into it, and is safe.", "wisdom"),
            BibleVerse("psa37_5", "Psalms", 37, 5, "KJV", "Commit thy way unto the Lord; trust also in him; and he shall bring it to pass.", "success"),
            BibleVerse("gal5_22", "Galatians", 5, 22, "KJV", "But the fruit of the Spirit is love, joy, peace, longsuffering, gentleness, goodness, faith.", "love"),
            BibleVerse("isa41_10", "Isaiah", 41, 10, "KJV", "Fear thou not; for I am with thee: be not dismayed; for I am thy God: I will strengthen thee; yea, I will help thee; yea, I will uphold thee with the right hand of my righteousness.", "courage"),
            BibleVerse("rom15_13", "Romans", 15, 13, "KJV", "Now the God of hope fill you with all joy and peace in believing, that ye may abound in hope, through the power of the Holy Ghost.", "hope"),
            BibleVerse("deu31_6", "Deuteronomy", 31, 6, "KJV", "Be strong and of a good courage, fear not, nor be afraid of them: for the Lord thy God, he it is that doth go with thee; he will not fail thee, nor forsake thee.", "courage"),
            BibleVerse("psa56_3", "Psalms", 56, 3, "KJV", "What time I am afraid, I will trust in thee.", "faith"),
            BibleVerse("1pe5_7", "1 Peter", 5, 7, "KJV", "Casting all your care upon him; for he careth for you.", "peace"),
            BibleVerse("pro16_3", "Proverbs", 16, 3, "KJV", "Commit thy works unto the Lord, and thy thoughts shall be established.", "success"),
            BibleVerse("heb13_5", "Hebrews", 13, 5, "KJV", "Let your conversation be without covetousness; and be content with such things as ye have: for he hath said, I will never leave thee, nor forsake thee.", "gratitude"),
            BibleVerse("eph6_10", "Ephesians", 6, 10, "KJV", "Finally, my brethren, be strong in the Lord, and in the power of his might.", "strength"),
            BibleVerse("mat6_34", "Matthew", 6, 34, "KJV", "Take therefore no thought for the morrow: for the morrow shall take thought for the things of itself. Sufficient unto the day is the evil thereof.", "peace"),
            BibleVerse("col3_23", "Colossians", 3, 23, "KJV", "And whatsoever ye do, do it heartily, as to the Lord, and not unto men.", "success"),
            BibleVerse("jam1_2", "James", 1, 2, "KJV", "My brethren, count it all joy when ye fall into divers temptations.", "perseverance"),
            BibleVerse("rom5_3", "Romans", 5, 3, "KJV", "And not only so, but we glory in tribulations also: knowing that tribulation worketh patience.", "perseverance"),
            BibleVerse("1pe5_6", "1 Peter", 5, 6, "KJV", "Humble yourselves therefore under the mighty hand of God, that he may exalt you in due time.", "humility"),
            BibleVerse("psa107_1", "Psalms", 107, 1, "KJV", "O give thanks unto the Lord, for he is good: for his mercy endureth for ever.", "gratitude"),
            BibleVerse("pro17_9", "Proverbs", 17, 9, "KJV", "He that covereth a transgression seeketh love; but he that repeateth a matter separateth very friends.", "love"),
            BibleVerse("1cor13_4", "1 Corinthians", 13, 4, "KJV", "Charity suffereth long, and is kind; charity envieth not; charity vaunteth not itself, is not puffed up.", "love"),
            BibleVerse("luk6_31", "Luke", 6, 31, "KJV", "And as ye would that men should do to you, do ye also to them likewise.", "love"),
            BibleVerse("pro4_23", "Proverbs", 4, 23, "KJV", "Keep thy heart with all diligence; for out of it are the issues of life.", "wisdom"),
            BibleVerse("jas3_17", "James", 3, 17, "KJV", "But the wisdom that is from above is first pure, then peaceable, gentle, and easy to be intreated, full of mercy and good fruits, without partiality, and without hypocrisy.", "wisdom"),
            BibleVerse("isa55_8", "Isaiah", 55, 8, "KJV", "For my thoughts are not your thoughts, neither are your ways my ways, saith the Lord.", "wisdom"),
            BibleVerse("mat7_7", "Matthew", 7, 7, "KJV", "Ask, and it shall be given you; seek, and ye shall find; knock, and it shall be opened unto you.", "faith"),
            BibleVerse("mar11_24", "Mark", 11, 24, "KJV", "Therefore I say unto you, What things soever ye desire, when ye pray, believe that ye receive them, and ye shall have them.", "faith"),
            BibleVerse("heb11_6", "Hebrews", 11, 6, "KJV", "But without faith it is impossible to please him: for he that cometh to God must believe that he is, and that he is a rewarder of them that diligently seek him.", "faith"),
            BibleVerse("psa27_1", "Psalms", 27, 1, "KJV", "The Lord is my light and my salvation; whom shall I fear? the Lord is the strength of my life; of whom shall I be afraid?", "courage"),
            BibleVerse("isa12_2", "Isaiah", 12, 2, "KJV", "Behold, God is my salvation; I will trust, and not be afraid: for the Lord Jehovah is my strength and my song; he also is become my salvation.", "faith"),
            BibleVerse("pro31_25", "Proverbs", 31, 25, "KJV", "Strength and honour are her clothing; and she shall rejoice in time to come.", "strength"),
            BibleVerse("2co12_9", "2 Corinthians", 12, 9, "KJV", "And he said unto me, My grace is sufficient for thee: for my strength is made perfect in weakness.", "strength"),
            BibleVerse("psa30_5", "Psalms", 30, 5, "KJV", "For his anger endureth but a moment; in his favour is life: weeping may endure for a night, but joy cometh in the morning.", "hope"),
            BibleVerse("lam3_22", "Lamentations", 3, 22, "KJV", "It is of the Lord's mercies that we are not consumed, because his compassions fail not.", "hope"),
            BibleVerse("rom12_2", "Romans", 12, 2, "KJV", "And be not conformed to this world: but be ye transformed by the renewing of your mind, that ye may prove what is that good, and acceptable, and perfect, will of God.", "wisdom"),
            BibleVerse("pro22_6", "Proverbs", 22, 6, "KJV", "Train up a child in the way he should go: and when he is old, he will not depart from it.", "wisdom"),
            BibleVerse("1th5_16", "1 Thessalonians", 5, 16, "KJV", "Rejoice evermore.", "gratitude"),
            BibleVerse("col4_2", "Colossians", 4, 2, "KJV", "Continue in prayer, and watch in the same with thanksgiving.", "gratitude")
        )

        val MOTIVATIONAL_QUOTES = listOf(
            MotivationalQuote("maya1", "Nothing will work unless you do.", "Maya Angelou", "discipline"),
            MotivationalQuote("helen1", "Keep your face to the sunshine and you cannot see a shadow.", "Helen Keller", "hope"),
            MotivationalQuote("roosevelt1", "Believe you can and you're halfway there.", "Theodore Roosevelt", "confidence"),
            MotivationalQuote("churchill1", "Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", "resilience"),
            MotivationalQuote("einstein1", "Strive not to be a success, but rather to be of value.", "Albert Einstein", "success"),
            MotivationalQuote("mandela1", "It always seems impossible until it's done.", "Nelson Mandela", "perseverance"),
            MotivationalQuote("jobs1", "The only way to do great work is to love what you do.", "Steve Jobs", "career"),
            MotivationalQuote("gandhi1", "The future depends on what you do today.", "Mahatma Gandhi", "success"),
            MotivationalQuote("twain1", "The secret of getting ahead is getting started.", "Mark Twain", "discipline"),
            MotivationalQuote("landis1", "The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt", "confidence"),
            MotivationalQuote("wooden1", "Don't let what you cannot do interfere with what you can do.", "John Wooden", "confidence"),
            MotivationalQuote("austin1", "The best way to predict the future is to create it.", "Peter Drucker", "leadership"),
            MotivationalQuote("sawyer1", "Twenty years from now you will be more disappointed by the things you didn't do than by the ones you did do.", "H. Jackson Brown Jr.", "personal_growth"),
            MotivationalQuote("tesla1", "The present is theirs; the future, for which I really worked, is mine.", "Nikola Tesla", "hard_work"),
            MotivationalQuote("curie1", "We must believe that we are gifted for something, and that this thing, at whatever cost, must be attained.", "Marie Curie", "personal_growth"),
            MotivationalQuote("armstrong1", "The difficulties of life are intended to make us better, not bitter.", "Dan Reeves", "resilience"),
            MotivationalQuote("disraeli1", "The secret of success is constancy to purpose.", "Benjamin Disraeli", "success"),
            MotivationalQuote("swartz1", "Hard work beats talent when talent doesn't work hard.", "Tim Notke", "hard_work"),
            MotivationalQuote("riordan1", "If you are not willing to learn, no one can help you. If you are determined to learn, no one can stop you.", "Zig Ziglar", "education"),
            MotivationalQuote("malala1", "One child, one teacher, one book, and one pen can change the world.", "Malala Yousafzai", "education"),
            MotivationalQuote("keller1", "Alone we can do so little; together we can do so much.", "Helen Keller", "leadership"),
            MotivationalQuote("carnegie1", "Teamwork is the ability to work together toward a common vision.", "Andrew Carnegie", "leadership"),
            MotivationalQuote("dearth1", "It does not matter how slowly you go as long as you do not stop.", "Confucius", "perseverance"),
            MotivationalQuote("buffett1", "The best investment you can make is in yourself.", "Warren Buffett", "personal_growth"),
            MotivationalQuote("west1", "Time is money, but money is not time.", "Mokokoma Mokhonoana", "hard_work"),
            MotivationalQuote("king1", "If you can't fly then run, if you can't run then walk, if you can't walk then crawl, but whatever you do you have to keep moving forward.", "Martin Luther King Jr.", "perseverance"),
            MotivationalQuote("stone1", "Don't be afraid to give up the good to go for the great.", "John D. Rockefeller", "success"),
            MotivationalQuote("tyson1", "Discipline is doing what needs to be done, even if you don't want to do it.", "Cus D'Amato", "discipline"),
            MotivationalQuote("riordan1", "Do not save your loving speeches for long after your friend is dead; they will not heal the wound of his absence.", "Mattie Stepanek", "resilience")
        )
    }
}

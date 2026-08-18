import type { BibleVerse } from '../types'

// All verses use KJV text (public domain).

const raw: Omit<BibleVerse, 'favorite' | 'lastDisplayedAt'>[] = [
  // ── FAITH ─────────────────────────────────────────────────────────────────
  { id: 'kjv-heb-11-1',   book: 'Hebrews',         chapter: 11, verse: 1,  translation: 'KJV', category: 'faith',        text: 'Now faith is the substance of things hoped for, the evidence of things not seen.' },
  { id: 'kjv-heb-11-6',   book: 'Hebrews',         chapter: 11, verse: 6,  translation: 'KJV', category: 'faith',        text: 'But without faith it is impossible to please him: for he that cometh to God must believe that he is, and that he is a rewarder of them that diligently seek him.' },
  { id: 'kjv-rom-10-17',  book: 'Romans',          chapter: 10, verse: 17, translation: 'KJV', category: 'faith',        text: 'So then faith cometh by hearing, and hearing by the word of God.' },
  { id: 'kjv-mark-11-24', book: 'Mark',            chapter: 11, verse: 24, translation: 'KJV', category: 'faith',        text: 'Therefore I say unto you, What things soever ye desire, when ye pray, believe that ye receive them, and ye shall have them.' },
  { id: 'kjv-mat-17-20',  book: 'Matthew',         chapter: 17, verse: 20, translation: 'KJV', category: 'faith',        text: 'If ye have faith as a grain of mustard seed, ye shall say unto this mountain, Remove hence to yonder place; and it shall remove; and nothing shall be impossible unto you.' },
  { id: 'kjv-2cor-5-7',   book: '2 Corinthians',   chapter: 5,  verse: 7,  translation: 'KJV', category: 'faith',        text: 'For we walk by faith, not by sight.' },
  { id: 'kjv-jas-2-17',   book: 'James',           chapter: 2,  verse: 17, translation: 'KJV', category: 'faith',        text: 'Even so faith, if it hath not works, is dead, being alone.' },
  { id: 'kjv-1pet-1-7',   book: '1 Peter',         chapter: 1,  verse: 7,  translation: 'KJV', category: 'faith',        text: 'That the trial of your faith, being much more precious than of gold that perisheth, though it be tried with fire, might be found unto praise and honour and glory at the appearing of Jesus Christ.' },
  { id: 'kjv-gal-2-20',   book: 'Galatians',       chapter: 2,  verse: 20, translation: 'KJV', category: 'faith',        text: 'I am crucified with Christ: nevertheless I live; yet not I, but Christ liveth in me: and the life which I now live in the flesh I live by the faith of the Son of God, who loved me, and gave himself for me.' },
  { id: 'kjv-luk-17-6',   book: 'Luke',            chapter: 17, verse: 6,  translation: 'KJV', category: 'faith',        text: 'And the Lord said, If ye had faith as a grain of mustard seed, ye might say unto this sycamine tree, Be thou plucked up by the root, and be thou planted in the sea; and it should obey you.' },
  // ── STRENGTH ──────────────────────────────────────────────────────────────
  { id: 'kjv-phi-4-13',   book: 'Philippians',     chapter: 4,  verse: 13, translation: 'KJV', category: 'strength',     text: 'I can do all things through Christ which strengtheneth me.' },
  { id: 'kjv-isa-40-31',  book: 'Isaiah',          chapter: 40, verse: 31, translation: 'KJV', category: 'strength',     text: 'But they that wait upon the Lord shall renew their strength; they shall mount up with wings as eagles; they shall run, and not be weary; and they shall walk, and not faint.' },
  { id: 'kjv-psa-46-1',   book: 'Psalms',          chapter: 46, verse: 1,  translation: 'KJV', category: 'strength',     text: 'God is our refuge and strength, a very present help in trouble.' },
  { id: 'kjv-2cor-12-9',  book: '2 Corinthians',   chapter: 12, verse: 9,  translation: 'KJV', category: 'strength',     text: 'And he said unto me, My grace is sufficient for thee: for my strength is made perfect in weakness.' },
  { id: 'kjv-eph-6-10',   book: 'Ephesians',       chapter: 6,  verse: 10, translation: 'KJV', category: 'strength',     text: 'Finally, my brethren, be strong in the Lord, and in the power of his might.' },
  { id: 'kjv-psa-28-7',   book: 'Psalms',          chapter: 28, verse: 7,  translation: 'KJV', category: 'strength',     text: 'The Lord is my strength and my shield; my heart trusted in him, and I am helped.' },
  { id: 'kjv-neh-8-10',   book: 'Nehemiah',        chapter: 8,  verse: 10, translation: 'KJV', category: 'strength',     text: 'The joy of the Lord is your strength.' },
  { id: 'kjv-1chr-16-11', book: '1 Chronicles',    chapter: 16, verse: 11, translation: 'KJV', category: 'strength',     text: 'Seek the Lord and his strength, seek his face continually.' },
  { id: 'kjv-psa-73-26',  book: 'Psalms',          chapter: 73, verse: 26, translation: 'KJV', category: 'strength',     text: 'My flesh and my heart faileth: but God is the strength of my heart, and my portion for ever.' },
  { id: 'kjv-isa-41-10',  book: 'Isaiah',          chapter: 41, verse: 10, translation: 'KJV', category: 'strength',     text: 'Fear thou not; for I am with thee: be not dismayed; for I am thy God: I will strengthen thee; yea, I will help thee.' },
  // ── HOPE ──────────────────────────────────────────────────────────────────
  { id: 'kjv-jer-29-11',  book: 'Jeremiah',        chapter: 29, verse: 11, translation: 'KJV', category: 'hope',         text: 'For I know the thoughts that I think toward you, saith the Lord, thoughts of peace, and not of evil, to give you an expected end.' },
  { id: 'kjv-rom-15-13',  book: 'Romans',          chapter: 15, verse: 13, translation: 'KJV', category: 'hope',         text: 'Now the God of hope fill you with all joy and peace in believing, that ye may abound in hope, through the power of the Holy Ghost.' },
  { id: 'kjv-rom-5-3',    book: 'Romans',          chapter: 5,  verse: 3,  translation: 'KJV', category: 'hope',         text: 'And not only so, but we glory in tribulations also: knowing that tribulation worketh patience; and patience, experience; and experience, hope.' },
  { id: 'kjv-psa-31-24',  book: 'Psalms',          chapter: 31, verse: 24, translation: 'KJV', category: 'hope',         text: 'Be of good courage, and he shall strengthen your heart, all ye that hope in the Lord.' },
  { id: 'kjv-lam-3-24',   book: 'Lamentations',    chapter: 3,  verse: 24, translation: 'KJV', category: 'hope',         text: 'The Lord is my portion, saith my soul; therefore will I hope in him.' },
  { id: 'kjv-rom-8-28',   book: 'Romans',          chapter: 8,  verse: 28, translation: 'KJV', category: 'hope',         text: 'And we know that all things work together for good to them that love God, to them who are the called according to his purpose.' },
  { id: 'kjv-psa-130-5',  book: 'Psalms',          chapter: 130,verse: 5,  translation: 'KJV', category: 'hope',         text: 'I wait for the Lord, my soul doth wait, and in his word do I hope.' },
  { id: 'kjv-pro-23-18',  book: 'Proverbs',        chapter: 23, verse: 18, translation: 'KJV', category: 'hope',         text: 'For surely there is an end; and thine expectation shall not be cut off.' },
  { id: 'kjv-isa-40-29',  book: 'Isaiah',          chapter: 40, verse: 29, translation: 'KJV', category: 'hope',         text: 'He giveth power to the faint; and to them that have no might he increaseth strength.' },
  { id: 'kjv-heb-6-19',   book: 'Hebrews',         chapter: 6,  verse: 19, translation: 'KJV', category: 'hope',         text: 'Which hope we have as an anchor of the soul, both sure and stedfast.' },
  // ── LOVE ──────────────────────────────────────────────────────────────────
  { id: 'kjv-joh-3-16',   book: 'John',            chapter: 3,  verse: 16, translation: 'KJV', category: 'love',         text: 'For God so loved the world, that he gave his only begotten Son, that whosoever believeth in him should not perish, but have everlasting life.' },
  { id: 'kjv-1cor-13-4',  book: '1 Corinthians',   chapter: 13, verse: 4,  translation: 'KJV', category: 'love',         text: 'Charity suffereth long, and is kind; charity envieth not; charity vaunteth not itself, is not puffed up.' },
  { id: 'kjv-1joh-4-8',   book: '1 John',          chapter: 4,  verse: 8,  translation: 'KJV', category: 'love',         text: 'He that loveth not knoweth not God; for God is love.' },
  { id: 'kjv-rom-8-38',   book: 'Romans',          chapter: 8,  verse: 38, translation: 'KJV', category: 'love',         text: 'Neither death, nor life, nor angels, nor principalities, nor powers, nor things present, nor things to come, nor height, nor depth, shall be able to separate us from the love of God, which is in Christ Jesus our Lord.' },
  { id: 'kjv-1joh-4-19',  book: '1 John',          chapter: 4,  verse: 19, translation: 'KJV', category: 'love',         text: 'We love him, because he first loved us.' },
  { id: 'kjv-joh-15-13',  book: 'John',            chapter: 15, verse: 13, translation: 'KJV', category: 'love',         text: 'Greater love hath no man than this, that a man lay down his life for his friends.' },
  { id: 'kjv-1cor-13-13', book: '1 Corinthians',   chapter: 13, verse: 13, translation: 'KJV', category: 'love',         text: 'And now abideth faith, hope, charity, these three; but the greatest of these is charity.' },
  { id: 'kjv-1pet-4-8',   book: '1 Peter',         chapter: 4,  verse: 8,  translation: 'KJV', category: 'love',         text: 'And above all things have fervent charity among yourselves: for charity shall cover the multitude of sins.' },
  { id: 'kjv-eph-3-17',   book: 'Ephesians',       chapter: 3,  verse: 17, translation: 'KJV', category: 'love',         text: 'That Christ may dwell in your hearts by faith; that ye, being rooted and grounded in love.' },
  { id: 'kjv-1joh-3-1',   book: '1 John',          chapter: 3,  verse: 1,  translation: 'KJV', category: 'love',         text: 'Behold, what manner of love the Father hath bestowed upon us, that we should be called the sons of God.' },
  // ── WISDOM ────────────────────────────────────────────────────────────────
  { id: 'kjv-pro-3-5',    book: 'Proverbs',        chapter: 3,  verse: 5,  translation: 'KJV', category: 'wisdom',       text: 'Trust in the Lord with all thine heart; and lean not unto thine own understanding.' },
  { id: 'kjv-jas-1-5',    book: 'James',           chapter: 1,  verse: 5,  translation: 'KJV', category: 'wisdom',       text: 'If any of you lack wisdom, let him ask of God, that giveth to all men liberally, and upbraideth not; and it shall be given him.' },
  { id: 'kjv-pro-9-10',   book: 'Proverbs',        chapter: 9,  verse: 10, translation: 'KJV', category: 'wisdom',       text: 'The fear of the Lord is the beginning of wisdom: and the knowledge of the holy is understanding.' },
  { id: 'kjv-pro-4-7',    book: 'Proverbs',        chapter: 4,  verse: 7,  translation: 'KJV', category: 'wisdom',       text: 'Wisdom is the principal thing; therefore get wisdom: and with all thy getting get understanding.' },
  { id: 'kjv-pro-2-6',    book: 'Proverbs',        chapter: 2,  verse: 6,  translation: 'KJV', category: 'wisdom',       text: 'For the Lord giveth wisdom: out of his mouth cometh knowledge and understanding.' },
  { id: 'kjv-col-2-3',    book: 'Colossians',      chapter: 2,  verse: 3,  translation: 'KJV', category: 'wisdom',       text: 'In whom are hid all the treasures of wisdom and knowledge.' },
  { id: 'kjv-pro-16-16',  book: 'Proverbs',        chapter: 16, verse: 16, translation: 'KJV', category: 'wisdom',       text: 'How much better is it to get wisdom than gold! and to get understanding rather to be chosen than silver!' },
  // ── SUCCESS ───────────────────────────────────────────────────────────────
  { id: 'kjv-jos-1-8',    book: 'Joshua',          chapter: 1,  verse: 8,  translation: 'KJV', category: 'success',      text: 'This book of the law shall not depart out of thy mouth; but thou shalt meditate therein day and night, that thou mayest observe to do according to all that is written therein: for then thou shalt make thy way prosperous, and then thou shalt have good success.' },
  { id: 'kjv-pro-16-3',   book: 'Proverbs',        chapter: 16, verse: 3,  translation: 'KJV', category: 'success',      text: 'Commit thy works unto the Lord, and thy thoughts shall be established.' },
  { id: 'kjv-psa-1-3',    book: 'Psalms',          chapter: 1,  verse: 3,  translation: 'KJV', category: 'success',      text: 'And he shall be like a tree planted by the rivers of water, that bringeth forth his fruit in his season; his leaf also shall not wither; and whatsoever he doeth shall prosper.' },
  { id: 'kjv-phi-4-19',   book: 'Philippians',     chapter: 4,  verse: 19, translation: 'KJV', category: 'success',      text: 'But my God shall supply all your need according to his riches in glory by Christ Jesus.' },
  { id: 'kjv-mat-6-33',   book: 'Matthew',         chapter: 6,  verse: 33, translation: 'KJV', category: 'success',      text: 'But seek ye first the kingdom of God, and his righteousness; and all these things shall be added unto you.' },
  { id: 'kjv-3joh-1-2',   book: '3 John',          chapter: 1,  verse: 2,  translation: 'KJV', category: 'success',      text: 'Beloved, I wish above all things that thou mayest prosper and be in health, even as thy soul prospereth.' },
  { id: 'kjv-pro-10-22',  book: 'Proverbs',        chapter: 10, verse: 22, translation: 'KJV', category: 'success',      text: 'The blessing of the Lord, it maketh rich, and he addeth no sorrow with it.' },
  // ── PEACE ─────────────────────────────────────────────────────────────────
  { id: 'kjv-joh-14-27',  book: 'John',            chapter: 14, verse: 27, translation: 'KJV', category: 'peace',        text: 'Peace I leave with you, my peace I give unto you: not as the world giveth, give I unto you. Let not your heart be troubled, neither let it be afraid.' },
  { id: 'kjv-phi-4-7',    book: 'Philippians',     chapter: 4,  verse: 7,  translation: 'KJV', category: 'peace',        text: 'And the peace of God, which passeth all understanding, shall keep your hearts and minds through Christ Jesus.' },
  { id: 'kjv-isa-26-3',   book: 'Isaiah',          chapter: 26, verse: 3,  translation: 'KJV', category: 'peace',        text: 'Thou wilt keep him in perfect peace, whose mind is stayed on thee: because he trusteth in thee.' },
  { id: 'kjv-rom-5-1',    book: 'Romans',          chapter: 5,  verse: 1,  translation: 'KJV', category: 'peace',        text: 'Therefore being justified by faith, we have peace with God through our Lord Jesus Christ.' },
  { id: 'kjv-col-3-15',   book: 'Colossians',      chapter: 3,  verse: 15, translation: 'KJV', category: 'peace',        text: 'And let the peace of God rule in your hearts, to the which also ye are called in one body; and be ye thankful.' },
  { id: 'kjv-mat-11-28',  book: 'Matthew',         chapter: 11, verse: 28, translation: 'KJV', category: 'peace',        text: 'Come unto me, all ye that labour and are heavy laden, and I will give you rest.' },
  { id: 'kjv-psa-4-8',    book: 'Psalms',          chapter: 4,  verse: 8,  translation: 'KJV', category: 'peace',        text: 'I will both lay me down in peace, and sleep: for thou, Lord, only makest me dwell in safety.' },
  // ── COURAGE ───────────────────────────────────────────────────────────────
  { id: 'kjv-jos-1-9',    book: 'Joshua',          chapter: 1,  verse: 9,  translation: 'KJV', category: 'courage',      text: 'Have not I commanded thee? Be strong and of a good courage; be not afraid, neither be thou dismayed: for the Lord thy God is with thee whithersoever thou goest.' },
  { id: 'kjv-deu-31-6',   book: 'Deuteronomy',     chapter: 31, verse: 6,  translation: 'KJV', category: 'courage',      text: 'Be strong and of a good courage, fear not, nor be afraid of them: for the Lord thy God, he it is that doth go with thee; he will not fail thee, nor forsake thee.' },
  { id: 'kjv-psa-27-1',   book: 'Psalms',          chapter: 27, verse: 1,  translation: 'KJV', category: 'courage',      text: 'The Lord is my light and my salvation; whom shall I fear? the Lord is the strength of my life; of whom shall I be afraid?' },
  { id: 'kjv-2tim-1-7',   book: '2 Timothy',       chapter: 1,  verse: 7,  translation: 'KJV', category: 'courage',      text: 'For God hath not given us the spirit of fear; but of power, and of love, and of a sound mind.' },
  { id: 'kjv-psa-56-3',   book: 'Psalms',          chapter: 56, verse: 3,  translation: 'KJV', category: 'courage',      text: 'What time I am afraid, I will trust in thee.' },
  { id: 'kjv-isa-43-1',   book: 'Isaiah',          chapter: 43, verse: 1,  translation: 'KJV', category: 'courage',      text: 'Fear not: for I have redeemed thee, I have called thee by thy name; thou art mine.' },
  { id: 'kjv-rom-8-31',   book: 'Romans',          chapter: 8,  verse: 31, translation: 'KJV', category: 'courage',      text: 'What shall we then say to these things? If God be for us, who can be against us?' },
  // ── PERSEVERANCE ──────────────────────────────────────────────────────────
  { id: 'kjv-gal-6-9',    book: 'Galatians',       chapter: 6,  verse: 9,  translation: 'KJV', category: 'perseverance', text: 'And let us not be weary in well doing: for in due season we shall reap, if we faint not.' },
  { id: 'kjv-jas-1-12',   book: 'James',           chapter: 1,  verse: 12, translation: 'KJV', category: 'perseverance', text: 'Blessed is the man that endureth temptation: for when he is tried, he shall receive the crown of life, which the Lord hath promised to them that love him.' },
  { id: 'kjv-heb-12-1',   book: 'Hebrews',         chapter: 12, verse: 1,  translation: 'KJV', category: 'perseverance', text: 'Let us lay aside every weight, and the sin which doth so easily beset us, and let us run with patience the race that is set before us.' },
  { id: 'kjv-2cor-4-17',  book: '2 Corinthians',   chapter: 4,  verse: 17, translation: 'KJV', category: 'perseverance', text: 'For our light affliction, which is but for a moment, worketh for us a far more exceeding and eternal weight of glory.' },
  { id: 'kjv-col-1-11',   book: 'Colossians',      chapter: 1,  verse: 11, translation: 'KJV', category: 'perseverance', text: 'Strengthened with all might, according to his glorious power, unto all patience and longsuffering with joyfulness.' },
  { id: 'kjv-luk-21-19',  book: 'Luke',            chapter: 21, verse: 19, translation: 'KJV', category: 'perseverance', text: 'In your patience possess ye your souls.' },
  // ── GRATITUDE ─────────────────────────────────────────────────────────────
  { id: 'kjv-1th-5-18',   book: '1 Thessalonians', chapter: 5,  verse: 18, translation: 'KJV', category: 'gratitude',   text: 'In every thing give thanks: for this is the will of God in Christ Jesus concerning you.' },
  { id: 'kjv-psa-100-4',  book: 'Psalms',          chapter: 100,verse: 4,  translation: 'KJV', category: 'gratitude',   text: 'Enter into his gates with thanksgiving, and into his courts with praise: be thankful unto him, and bless his name.' },
  { id: 'kjv-col-3-17',   book: 'Colossians',      chapter: 3,  verse: 17, translation: 'KJV', category: 'gratitude',   text: 'And whatsoever ye do in word or deed, do all in the name of the Lord Jesus, giving thanks to God and the Father by him.' },
  { id: 'kjv-phi-4-6',    book: 'Philippians',     chapter: 4,  verse: 6,  translation: 'KJV', category: 'gratitude',   text: 'Be careful for nothing; but in every thing by prayer and supplication with thanksgiving let your requests be made known unto God.' },
  { id: 'kjv-psa-107-1',  book: 'Psalms',          chapter: 107,verse: 1,  translation: 'KJV', category: 'gratitude',   text: 'O give thanks unto the Lord, for he is good: for his mercy endureth for ever.' },
  { id: 'kjv-eph-5-20',   book: 'Ephesians',       chapter: 5,  verse: 20, translation: 'KJV', category: 'gratitude',   text: 'Giving thanks always for all things unto God and the Father in the name of our Lord Jesus Christ.' },
  { id: 'kjv-psa-9-1',    book: 'Psalms',          chapter: 9,  verse: 1,  translation: 'KJV', category: 'gratitude',   text: 'I will praise thee, O Lord, with my whole heart; I will shew forth all thy marvellous works.' },
  { id: 'kjv-2cor-9-15',  book: '2 Corinthians',   chapter: 9,  verse: 15, translation: 'KJV', category: 'gratitude',   text: 'Thanks be unto God for his unspeakable gift.' },
]

export const bibleVerses: BibleVerse[] = raw.map(v => ({
  ...v,
  favorite: false,
  lastDisplayedAt: null,
}))

export default bibleVerses

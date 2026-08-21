import { AccurateTimer }                from './timer'
import { ShuffleDeck, SequentialCursor } from './shuffle'
import { bibleVerses as allVerses }      from '../data/bible-verses'
import { motivationalQuotes as allQuotes } from '../data/quotes'
import type {
  BibleVerse, MotivationalQuote, HistoryEntry,
  UserSettings, DayOfWeek,
} from '../types'
import { DEFAULT_SETTINGS } from '../types'

// ── Types ─────────────────────────────────────────────────────────────────────

export interface EngineState {
  currentVerse:    BibleVerse
  currentQuote:    MotivationalQuote
  bibleRemainingMs: number
  quoteRemainingMs: number
  isPaused:         boolean
}

export type EngineEventType = 'verse-changed' | 'quote-changed' | 'state-changed'
type EngineListener = (state: EngineState) => void

// ── Helpers ───────────────────────────────────────────────────────────────────

function todayKey(): DayOfWeek {
  const days: DayOfWeek[] = ['sunday','monday','tuesday','wednesday','thursday','friday','saturday']
  return days[new Date().getDay()]
}

function withinSchedule(s: UserSettings): boolean {
  if (!s.scheduleEnabled) return true
  const win = s.schedule[todayKey()]
  if (!win.enabled) return false
  const now  = new Date()
  const nowM = now.getHours() * 60 + now.getMinutes()
  return nowM >= win.startHour * 60 + win.startMinute &&
         nowM  < win.endHour   * 60 + win.endMinute
}

function makeId() {
  return `${Date.now()}-${Math.random().toString(36).slice(2, 7)}`
}

// ── Engine ────────────────────────────────────────────────────────────────────

export class ContentEngine {
  private settings: UserSettings

  private verses: BibleVerse[]
  private quotes: MotivationalQuote[]
  private history: HistoryEntry[] = []

  private verseDeck: ShuffleDeck<BibleVerse>
  private verseSeq:  SequentialCursor<BibleVerse>
  private quoteDeck: ShuffleDeck<MotivationalQuote>
  private quoteSeq:  SequentialCursor<MotivationalQuote>

  private currentVerse: BibleVerse | null = null
  private currentQuote: MotivationalQuote | null = null
  private prevVerseStack: BibleVerse[]  = []
  private prevQuoteStack: MotivationalQuote[] = []

  private bibleTimer: AccurateTimer
  private quoteTimer: AccurateTimer

  private listeners = new Map<EngineEventType, Set<EngineListener>>()

  constructor(settings: UserSettings = DEFAULT_SETTINGS) {
    this.settings = { ...settings }
    this.verses   = allVerses.map(v => ({ ...v }))
    this.quotes   = allQuotes.map(q => ({ ...q }))

    const vp = this.buildVersePool()
    const qp = this.buildQuotePool()

    this.verseDeck = new ShuffleDeck(vp)
    this.verseSeq  = new SequentialCursor(vp)
    this.quoteDeck = new ShuffleDeck(qp)
    this.quoteSeq  = new SequentialCursor(qp)

    this.bibleTimer = new AccurateTimer(this.settings.bibleIntervalMs,      () => this.rotateBible())
    this.quoteTimer = new AccurateTimer(this.settings.motivationIntervalMs,  () => this.rotateQuote())

    this.currentVerse = this.pickVerse()
    this.currentQuote = this.pickQuote()
  }

  // ── Lifecycle ──────────────────────────────────────────────────────────────

  start() {
    if (this.settings.isPaused) return
    if (this.settings.bibleEnabled) this.bibleTimer.start(this.settings.nextBibleFireAtEpochMs)
    if (this.settings.motivationEnabled) this.quoteTimer.start(this.settings.nextMotivationFireAtEpochMs)
    this.syncDeadlines()
  }

  destroy() {
    this.bibleTimer.destroy()
    this.quoteTimer.destroy()
    this.listeners.clear()
  }

  // ── Settings ───────────────────────────────────────────────────────────────

  applySettings(next: Partial<UserSettings>) {
    const prev = this.settings
    this.settings = { ...prev, ...next }

    if (
      JSON.stringify(prev.bibleCategories)  !== JSON.stringify(this.settings.bibleCategories) ||
      prev.bibleTranslation !== this.settings.bibleTranslation ||
      prev.dailyThemeEnabled !== this.settings.dailyThemeEnabled
    ) {
      const pool = this.buildVersePool()
      this.verseDeck.setPool(pool); this.verseSeq.setPool(pool)
    }

    if (JSON.stringify(prev.motivationCategories) !== JSON.stringify(this.settings.motivationCategories)) {
      const pool = this.buildQuotePool()
      this.quoteDeck.setPool(pool); this.quoteSeq.setPool(pool)
    }

    if (prev.bibleIntervalMs      !== this.settings.bibleIntervalMs)      this.bibleTimer.setInterval(this.settings.bibleIntervalMs)
    if (prev.motivationIntervalMs !== this.settings.motivationIntervalMs) this.quoteTimer.setInterval(this.settings.motivationIntervalMs)

    if (!prev.isPaused && this.settings.isPaused)  { this.bibleTimer.pause();  this.quoteTimer.pause()  }
    if ( prev.isPaused && !this.settings.isPaused) {
      if (this.settings.bibleEnabled)      this.bibleTimer.resume()
      if (this.settings.motivationEnabled) this.quoteTimer.resume()
    }
    if (!prev.bibleEnabled      && this.settings.bibleEnabled      && !this.settings.isPaused) this.bibleTimer.start()
    if (!prev.motivationEnabled && this.settings.motivationEnabled && !this.settings.isPaused) this.quoteTimer.start()

    this.syncDeadlines()
    this.emit('state-changed', this.getState())
  }

  // ── Controls ───────────────────────────────────────────────────────────────

  pause()  { this.settings.isPaused = true;  this.bibleTimer.pause();  this.quoteTimer.pause();  this.emit('state-changed', this.getState()) }
  resume() { this.settings.isPaused = false; if (this.settings.bibleEnabled) this.bibleTimer.resume(); if (this.settings.motivationEnabled) this.quoteTimer.resume(); this.emit('state-changed', this.getState()) }

  nextVerse() { if (this.currentVerse) this.prevVerseStack.push(this.currentVerse); this.currentVerse = this.pickVerse(); this.bibleTimer.reset(); this.syncDeadlines(); this.recordHistory(); this.emit('verse-changed', this.getState()) }
  prevVerse() { const p = this.prevVerseStack.pop(); if (p) { this.currentVerse = p; this.bibleTimer.reset(); this.emit('verse-changed', this.getState()) } }
  nextQuote() { if (this.currentQuote) this.prevQuoteStack.push(this.currentQuote); this.currentQuote = this.pickQuote(); this.quoteTimer.reset(); this.syncDeadlines(); this.recordHistory(); this.emit('quote-changed', this.getState()) }
  prevQuote() { const p = this.prevQuoteStack.pop(); if (p) { this.currentQuote = p; this.quoteTimer.reset(); this.emit('quote-changed', this.getState()) } }

  toggleFavoriteVerse(id: string): BibleVerse | undefined {
    const v = this.verses.find(x => x.id === id)
    if (v) { v.favorite = !v.favorite; if (this.currentVerse?.id === id) this.currentVerse = { ...v }; this.emit('state-changed', this.getState()) }
    return v
  }

  toggleFavoriteQuote(id: string): MotivationalQuote | undefined {
    const q = this.quotes.find(x => x.id === id)
    if (q) { q.favorite = !q.favorite; if (this.currentQuote?.id === id) this.currentQuote = { ...q }; this.emit('state-changed', this.getState()) }
    return q
  }

  // ── Queries ────────────────────────────────────────────────────────────────

  getState(): EngineState {
    return {
      currentVerse:    { ...(this.currentVerse ?? this.verses[0]) },
      currentQuote:    { ...(this.currentQuote ?? this.quotes[0]) },
      bibleRemainingMs: this.bibleTimer.getRemainingMs(),
      quoteRemainingMs: this.quoteTimer.getRemainingMs(),
      isPaused:         this.settings.isPaused,
    }
  }

  getAllVerses():       BibleVerse[]         { return this.verses.map(v => ({ ...v })) }
  getAllQuotes():       MotivationalQuote[]  { return this.quotes.map(q => ({ ...q })) }
  getFavoriteVerses(): BibleVerse[]         { return this.verses.filter(v => v.favorite).map(v => ({ ...v })) }
  getFavoriteQuotes(): MotivationalQuote[]  { return this.quotes.filter(q => q.favorite).map(q => ({ ...q })) }
  getHistory():        HistoryEntry[]       { return [...this.history] }
  getSettings(): UserSettings { return { ...this.settings } }

  hydrateFavorites(verseIds: string[], quoteIds: string[]) {
    const vs = new Set(verseIds); const qs = new Set(quoteIds)
    this.verses.forEach(v => { v.favorite = vs.has(v.id) })
    this.quotes.forEach(q => { q.favorite = qs.has(q.id) })
  }

  hydrateHistory(h: HistoryEntry[]) { this.history = [...h] }

  // ── Internals ──────────────────────────────────────────────────────────────

  private buildVersePool(): BibleVerse[] {
    let pool = this.verses.filter(v => this.settings.bibleCategories.includes(v.category))
    if (this.settings.dailyThemeEnabled) {
      const theme = this.settings.dailyThemeMap[todayKey()]
      pool = [...pool.filter(v => v.category === theme), ...pool.filter(v => v.category !== theme)]
    }
    return pool.length > 0 ? pool : this.verses
  }

  private buildQuotePool(): MotivationalQuote[] {
    const pool = this.quotes.filter(q => this.settings.motivationCategories.includes(q.category))
    return pool.length > 0 ? pool : this.quotes
  }

  private pickVerse(): BibleVerse {
    const v = this.settings.bibleRandomMode ? this.verseDeck.next() : this.verseSeq.next()
    const chosen = v ?? this.verses[0]; chosen.lastDisplayedAt = Date.now()
    return { ...chosen }
  }

  private pickQuote(): MotivationalQuote {
    const q = this.settings.motivationRandomMode ? this.quoteDeck.next() : this.quoteSeq.next()
    const chosen = q ?? this.quotes[0]; chosen.lastDisplayedAt = Date.now()
    return { ...chosen }
  }

  private rotateBible() {
    if (!withinSchedule(this.settings)) return
    if (this.currentVerse) this.prevVerseStack.push(this.currentVerse)
    this.currentVerse = this.pickVerse(); this.syncDeadlines(); this.recordHistory(); this.emit('verse-changed', this.getState())
  }

  private rotateQuote() {
    if (!withinSchedule(this.settings)) return
    if (this.currentQuote) this.prevQuoteStack.push(this.currentQuote)
    this.currentQuote = this.pickQuote(); this.syncDeadlines(); this.recordHistory(); this.emit('quote-changed', this.getState())
  }

  private recordHistory() {
    if (!this.currentVerse || !this.currentQuote) return
    this.history.unshift({ id: makeId(), bibleVerseId: this.currentVerse.id, quoteId: this.currentQuote.id, displayedAt: Date.now() })
    if (this.history.length > 500) this.history.length = 500
  }

  private syncDeadlines() {
    this.settings.nextBibleFireAtEpochMs = this.bibleTimer.getNextFireAt()
    this.settings.nextMotivationFireAtEpochMs = this.quoteTimer.getNextFireAt()
  }

  // ── Event bus ──────────────────────────────────────────────────────────────

  on(event: EngineEventType, listener: EngineListener) {
    if (!this.listeners.has(event)) this.listeners.set(event, new Set())
    this.listeners.get(event)!.add(listener)
  }

  off(event: EngineEventType, listener: EngineListener) {
    this.listeners.get(event)?.delete(listener)
  }

  private emit(event: EngineEventType, state: EngineState) {
    this.listeners.get(event)?.forEach(fn => fn(state))
  }
}

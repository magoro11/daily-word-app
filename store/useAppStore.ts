/**
 * useAppStore.ts
 *
 * Zustand store for the React Native app.
 * Mirrors the desktop store's shape so screens look familiar,
 * but replaces all browser/Electron APIs with RN equivalents.
 *
 * Hydration is async: call initStore() once in the root layout.
 */

import { create } from 'zustand'
import { Appearance } from 'react-native'
import { ContentEngine } from '../core/engine/content-engine'
import type { EngineState } from '../core/engine/content-engine'
import type { BibleVerse, MotivationalQuote, HistoryEntry, UserSettings } from '../core/types'
import { DEFAULT_SETTINGS } from '../core/types'
import {
  loadAllData,
  saveSettings,
  saveHistory,
  saveFavoriteVerseIds,
  saveFavoriteQuoteIds,
} from './storage'

// ── Singleton engine ──────────────────────────────────────────────────────────
// Created with default settings; real settings applied during hydration.
export const engine = new ContentEngine(DEFAULT_SETTINGS)

// ── Store shape ───────────────────────────────────────────────────────────────

export interface AppStore {
  // Content
  currentVerse:    BibleVerse | null
  currentQuote:    MotivationalQuote | null
  bibleRemainingMs: number
  quoteRemainingMs: number
  isPaused:         boolean

  // Settings
  settings: UserSettings

  // Favorites
  favoriteVerses: BibleVerse[]
  favoriteQuotes: MotivationalQuote[]

  // History
  history: HistoryEntry[]

  // UI
  activeTab:       'home' | 'favorites' | 'history' | 'settings'
  showOnboarding:  boolean
  isDark:          boolean
  isHydrated:      boolean   // false until AsyncStorage load completes

  // Animation keys
  verseKey: number
  quoteKey: number

  // Actions
  nextVerse:           () => void
  prevVerse:           () => void
  nextQuote:           () => void
  prevQuote:           () => void
  pause:               () => void
  resume:              () => void
  toggleFavoriteVerse: (id: string) => void
  toggleFavoriteQuote: (id: string) => void
  updateSettings:      (partial: Partial<UserSettings>) => void
  completeOnboarding:  () => void
  setActiveTab:        (tab: AppStore['activeTab']) => void
  setDark:             (dark: boolean) => void
  hydrate:             () => Promise<void>
  persistTimerTick:    (bibleMs: number, quoteMs: number) => void
}

// ── Dark mode resolver ────────────────────────────────────────────────────────

function resolveIsDark(settings: UserSettings): boolean {
  if (settings.theme === 'dark')  return true
  if (settings.theme === 'light') return false
  return Appearance.getColorScheme() === 'dark'
}

// ── Store ─────────────────────────────────────────────────────────────────────

export const useAppStore = create<AppStore>((set, get) => ({
  currentVerse:    null,
  currentQuote:    null,
  bibleRemainingMs: 0,
  quoteRemainingMs: 0,
  isPaused:         false,

  settings:      DEFAULT_SETTINGS,
  favoriteVerses: [],
  favoriteQuotes: [],
  history:        [],

  activeTab:      'home',
  showOnboarding: false,
  isDark:         Appearance.getColorScheme() === 'dark',
  isHydrated:     false,

  verseKey: 0,
  quoteKey: 0,

  // ── Hydration ───────────────────────────────────────────────────────────────
  async hydrate() {
    const data = await loadAllData()

    engine.applySettings(data.settings)
    engine.hydrateFavorites(data.favoriteVerseIds, data.favoriteQuoteIds)
    engine.hydrateHistory(data.history)
    engine.start()

    const state = engine.getState()

    set({
      currentVerse:    state.currentVerse,
      currentQuote:    state.currentQuote,
      bibleRemainingMs: state.bibleRemainingMs,
      quoteRemainingMs: state.quoteRemainingMs,
      isPaused:         state.isPaused,
      settings:         data.settings,
      favoriteVerses:   engine.getFavoriteVerses(),
      favoriteQuotes:   engine.getFavoriteQuotes(),
      history:          data.history,
      showOnboarding:   !data.settings.hasCompletedOnboarding,
      isDark:           resolveIsDark(data.settings),
      isHydrated:       true,
    })
  },

  // ── Content actions ─────────────────────────────────────────────────────────

  nextVerse() {
    engine.nextVerse()
    const s = engine.getState()
    set(st => ({ currentVerse: s.currentVerse, bibleRemainingMs: s.bibleRemainingMs, verseKey: st.verseKey + 1 }))
    flushHistory()
  },

  prevVerse() {
    engine.prevVerse()
    const s = engine.getState()
    set(st => ({ currentVerse: s.currentVerse, bibleRemainingMs: s.bibleRemainingMs, verseKey: st.verseKey + 1 }))
  },

  nextQuote() {
    engine.nextQuote()
    const s = engine.getState()
    set(st => ({ currentQuote: s.currentQuote, quoteRemainingMs: s.quoteRemainingMs, quoteKey: st.quoteKey + 1 }))
    flushHistory()
  },

  prevQuote() {
    engine.prevQuote()
    const s = engine.getState()
    set(st => ({ currentQuote: s.currentQuote, quoteRemainingMs: s.quoteRemainingMs, quoteKey: st.quoteKey + 1 }))
  },

  pause() {
    engine.pause()
    const next = { ...get().settings, isPaused: true }
    set({ isPaused: true, settings: next })
    saveSettings(next)
  },

  resume() {
    engine.resume()
    const next = { ...get().settings, isPaused: false }
    set({ isPaused: false, settings: next })
    saveSettings(next)
  },

  // ── Favorites ────────────────────────────────────────────────────────────────

  toggleFavoriteVerse(id) {
    engine.toggleFavoriteVerse(id)
    const favs = engine.getFavoriteVerses()
    set({ favoriteVerses: favs, currentVerse: engine.getState().currentVerse })
    saveFavoriteVerseIds(favs.map(v => v.id))
  },

  toggleFavoriteQuote(id) {
    engine.toggleFavoriteQuote(id)
    const favs = engine.getFavoriteQuotes()
    set({ favoriteQuotes: favs, currentQuote: engine.getState().currentQuote })
    saveFavoriteQuoteIds(favs.map(q => q.id))
  },

  // ── Settings ─────────────────────────────────────────────────────────────────

  updateSettings(partial) {
    const next = { ...get().settings, ...partial }
    engine.applySettings(next)
    set({ settings: next, isDark: resolveIsDark(next) })
    saveSettings(next)
  },

  completeOnboarding() {
    const next = { ...get().settings, hasCompletedOnboarding: true }
    engine.applySettings(next)
    set({ settings: next, showOnboarding: false })
    saveSettings(next)
  },

  // ── UI ────────────────────────────────────────────────────────────────────────

  setActiveTab: (tab) => set({ activeTab: tab }),

  setDark(dark) {
    set({ isDark: dark })
  },

  // ── Timer tick (called by interval in root layout) ────────────────────────────

  persistTimerTick(bibleMs, quoteMs) {
    set({ bibleRemainingMs: bibleMs, quoteRemainingMs: quoteMs })
  },
}))

// ── Wire engine events → store ────────────────────────────────────────────────

engine.on('verse-changed', (state: EngineState) => {
  useAppStore.setState(st => ({
    currentVerse:    state.currentVerse,
    bibleRemainingMs: state.bibleRemainingMs,
    verseKey:        st.verseKey + 1,
  }))
  flushHistory()
})

engine.on('quote-changed', (state: EngineState) => {
  useAppStore.setState(st => ({
    currentQuote:    state.currentQuote,
    quoteRemainingMs: state.quoteRemainingMs,
    quoteKey:        st.quoteKey + 1,
  }))
  flushHistory()
})

engine.on('state-changed', (state: EngineState) => {
  useAppStore.setState({
    currentVerse:    state.currentVerse,
    currentQuote:    state.currentQuote,
    bibleRemainingMs: state.bibleRemainingMs,
    quoteRemainingMs: state.quoteRemainingMs,
    isPaused:         state.isPaused,
  })
})

// ── Helpers ───────────────────────────────────────────────────────────────────

function flushHistory() {
  const h = engine.getHistory()
  useAppStore.setState({ history: h })
  saveHistory(h)
}

// ── System dark-mode listener ─────────────────────────────────────────────────

Appearance.addChangeListener(({ colorScheme }) => {
  const { settings } = useAppStore.getState()
  if (settings.theme === 'system') {
    useAppStore.setState({ isDark: colorScheme === 'dark' })
  }
})

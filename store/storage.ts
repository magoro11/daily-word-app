/**
 * storage.ts  —  AsyncStorage wrapper for React Native
 *
 * Mirrors the same key names and API shape as the desktop's localStorage
 * version so the store code reads identically.
 * All functions are async because AsyncStorage is async.
 */

import AsyncStorage from '@react-native-async-storage/async-storage'
import type { UserSettings, HistoryEntry } from '../core/types'
import { DEFAULT_SETTINGS } from '../core/types'

const KEYS = {
  SETTINGS:        'dw:settings',
  HISTORY:         'dw:history',
  FAVORITE_VERSES: 'dw:fav-verses',
  FAVORITE_QUOTES: 'dw:fav-quotes',
} as const

// ── Settings ──────────────────────────────────────────────────────────────────

export async function loadSettings(): Promise<UserSettings> {
  try {
    const raw = await AsyncStorage.getItem(KEYS.SETTINGS)
    if (!raw) return { ...DEFAULT_SETTINGS }
    return { ...DEFAULT_SETTINGS, ...JSON.parse(raw) }
  } catch {
    return { ...DEFAULT_SETTINGS }
  }
}

export async function saveSettings(s: UserSettings): Promise<void> {
  try {
    await AsyncStorage.setItem(KEYS.SETTINGS, JSON.stringify(s))
  } catch { /* ignore — storage full */ }
}

// ── History ───────────────────────────────────────────────────────────────────

export async function loadHistory(): Promise<HistoryEntry[]> {
  try {
    const raw = await AsyncStorage.getItem(KEYS.HISTORY)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export async function saveHistory(h: HistoryEntry[]): Promise<void> {
  try {
    await AsyncStorage.setItem(KEYS.HISTORY, JSON.stringify(h.slice(0, 500)))
  } catch { /* ignore */ }
}

// ── Favorites ─────────────────────────────────────────────────────────────────

export async function loadFavoriteVerseIds(): Promise<string[]> {
  try {
    const raw = await AsyncStorage.getItem(KEYS.FAVORITE_VERSES)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export async function saveFavoriteVerseIds(ids: string[]): Promise<void> {
  try {
    await AsyncStorage.setItem(KEYS.FAVORITE_VERSES, JSON.stringify(ids))
  } catch { /* ignore */ }
}

export async function loadFavoriteQuoteIds(): Promise<string[]> {
  try {
    const raw = await AsyncStorage.getItem(KEYS.FAVORITE_QUOTES)
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export async function saveFavoriteQuoteIds(ids: string[]): Promise<void> {
  try {
    await AsyncStorage.setItem(KEYS.FAVORITE_QUOTES, JSON.stringify(ids))
  } catch { /* ignore */ }
}

// ── Bulk hydration helper — called once on app boot ───────────────────────────

export interface PersistedData {
  settings:         UserSettings
  history:          HistoryEntry[]
  favoriteVerseIds: string[]
  favoriteQuoteIds: string[]
}

export async function loadAllData(): Promise<PersistedData> {
  const [settings, history, favoriteVerseIds, favoriteQuoteIds] = await Promise.all([
    loadSettings(),
    loadHistory(),
    loadFavoriteVerseIds(),
    loadFavoriteQuoteIds(),
  ])
  return { settings, history, favoriteVerseIds, favoriteQuoteIds }
}

// ─── Bible ────────────────────────────────────────────────────────────────────

export type BibleCategory =
  | 'faith'
  | 'strength'
  | 'hope'
  | 'love'
  | 'wisdom'
  | 'success'
  | 'peace'
  | 'courage'
  | 'perseverance'
  | 'gratitude'

export type BibleTranslation = 'KJV' | 'NIV' | 'ESV' | 'NKJV' | 'NLT'

export interface BibleVerse {
  id: string
  book: string
  chapter: number
  verse: number
  verseEnd?: number
  translation: BibleTranslation
  text: string
  category: BibleCategory
  favorite: boolean
  lastDisplayedAt: number | null
}

// ─── Quotes ───────────────────────────────────────────────────────────────────

export type QuoteCategory =
  | 'success'
  | 'discipline'
  | 'hard-work'
  | 'career'
  | 'education'
  | 'confidence'
  | 'leadership'
  | 'resilience'
  | 'personal-growth'

export interface MotivationalQuote {
  id: string
  text: string
  author: string
  category: QuoteCategory
  favorite: boolean
  lastDisplayedAt: number | null
}

// ─── History ──────────────────────────────────────────────────────────────────

export interface HistoryEntry {
  id: string
  bibleVerseId: string
  quoteId: string
  displayedAt: number
}

// ─── Settings ─────────────────────────────────────────────────────────────────

export type AnimationStyle = 'fade' | 'slide' | 'none'
export type BackgroundStyle =
  | 'gradient-dawn'
  | 'gradient-dusk'
  | 'gradient-sky'
  | 'gradient-forest'
  | 'gradient-ocean'
  | 'solid-light'
  | 'solid-dark'

export interface ScheduleWindow {
  enabled: boolean
  startHour: number
  startMinute: number
  endHour: number
  endMinute: number
}

export type DayOfWeek =
  | 'monday' | 'tuesday' | 'wednesday' | 'thursday'
  | 'friday' | 'saturday' | 'sunday'

export type DailyTheme = BibleCategory

export interface DailyThemeMap {
  monday:    DailyTheme
  tuesday:   DailyTheme
  wednesday: DailyTheme
  thursday:  DailyTheme
  friday:    DailyTheme
  saturday:  DailyTheme
  sunday:    DailyTheme
}

export interface UserSettings {
  // Bible
  bibleEnabled: boolean
  bibleTranslation: BibleTranslation
  bibleIntervalMs: number
  bibleCategories: BibleCategory[]
  bibleRandomMode: boolean

  // Motivation
  motivationEnabled: boolean
  motivationIntervalMs: number
  motivationCategories: QuoteCategory[]
  motivationRandomMode: boolean

  // Appearance
  theme: 'light' | 'dark' | 'system'
  backgroundStyle: BackgroundStyle
  fontSize: 'sm' | 'md' | 'lg' | 'xl'
  textOpacity: number
  animationStyle: AnimationStyle

  // Notifications
  notificationsEnabled: boolean
  notifyOnBibleChange: boolean
  notifyOnQuoteChange: boolean
  notificationDurationMs: number

  // Schedule
  scheduleEnabled: boolean
  schedule: Record<DayOfWeek, ScheduleWindow>

  // Daily Theme
  dailyThemeEnabled: boolean
  dailyThemeMap: DailyThemeMap

  // App state
  isPaused: boolean
  hasCompletedOnboarding: boolean
  nextBibleFireAtEpochMs: number
  nextMotivationFireAtEpochMs: number
}

// ─── Labels & Intervals ───────────────────────────────────────────────────────

export const BIBLE_CATEGORY_LABELS: Record<BibleCategory, string> = {
  faith:         'Faith',
  strength:      'Strength',
  hope:          'Hope',
  love:          'Love',
  wisdom:        'Wisdom',
  success:       'Success',
  peace:         'Peace',
  courage:       'Courage',
  perseverance:  'Perseverance',
  gratitude:     'Gratitude',
}

export const QUOTE_CATEGORY_LABELS: Record<QuoteCategory, string> = {
  success:          'Success',
  discipline:       'Discipline',
  'hard-work':      'Hard Work',
  career:           'Career',
  education:        'Education',
  confidence:       'Confidence',
  leadership:       'Leadership',
  resilience:       'Resilience',
  'personal-growth':'Personal Growth',
}

export const BIBLE_INTERVALS = [
  { label: '15 minutes', value: 15 * 60 * 1000 },
  { label: '30 minutes', value: 30 * 60 * 1000 },
  { label: '1 hour',     value: 60 * 60 * 1000 },
  { label: '2 hours',    value: 2 * 60 * 60 * 1000 },
  { label: '4 hours',    value: 4 * 60 * 60 * 1000 },
] as const

export const QUOTE_INTERVALS = [
  { label: '1 minute',   value: 1  * 60 * 1000 },
  { label: '5 minutes',  value: 5  * 60 * 1000 },
  { label: '10 minutes', value: 10 * 60 * 1000 },
  { label: '15 minutes', value: 15 * 60 * 1000 },
  { label: '30 minutes', value: 30 * 60 * 1000 },
] as const

// ─── Defaults ─────────────────────────────────────────────────────────────────

export const DEFAULT_DAILY_THEME_MAP: DailyThemeMap = {
  monday:    'perseverance',
  tuesday:   'faith',
  wednesday: 'courage',
  thursday:  'wisdom',
  friday:    'success',
  saturday:  'gratitude',
  sunday:    'hope',
}

export const DEFAULT_SCHEDULE_WINDOW: ScheduleWindow = {
  enabled:     true,
  startHour:   7,
  startMinute: 0,
  endHour:     22,
  endMinute:   0,
}

export const DEFAULT_SETTINGS: UserSettings = {
  bibleEnabled:      true,
  bibleTranslation:  'KJV',
  bibleIntervalMs:   60 * 60 * 1000,
  bibleCategories:   ['faith','strength','hope','love','wisdom','success','peace','courage','perseverance','gratitude'],
  bibleRandomMode:   true,

  motivationEnabled:      true,
  motivationIntervalMs:   5 * 60 * 1000,
  motivationCategories:   ['success','discipline','hard-work','career','education','confidence','leadership','resilience','personal-growth'],
  motivationRandomMode:   true,

  theme:           'system',
  backgroundStyle: 'gradient-dawn',
  fontSize:        'md',
  textOpacity:     1.0,
  animationStyle:  'fade',

  notificationsEnabled:    true,
  notifyOnBibleChange:     true,
  notifyOnQuoteChange:     false,
  notificationDurationMs:  5000,

  scheduleEnabled: false,
  schedule: {
    monday:    { ...DEFAULT_SCHEDULE_WINDOW },
    tuesday:   { ...DEFAULT_SCHEDULE_WINDOW },
    wednesday: { ...DEFAULT_SCHEDULE_WINDOW },
    thursday:  { ...DEFAULT_SCHEDULE_WINDOW },
    friday:    { ...DEFAULT_SCHEDULE_WINDOW },
    saturday:  { enabled: true, startHour: 8, startMinute: 0, endHour: 23, endMinute: 0 },
    sunday:    { enabled: true, startHour: 8, startMinute: 0, endHour: 23, endMinute: 0 },
  },

  dailyThemeEnabled: true,
  dailyThemeMap:     { ...DEFAULT_DAILY_THEME_MAP },

  isPaused:               false,
  hasCompletedOnboarding: false,
  nextBibleFireAtEpochMs: 0,
  nextMotivationFireAtEpochMs: 0,
}

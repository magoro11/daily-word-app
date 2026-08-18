/**
 * Design tokens for the Daily Word mobile app.
 * Light and dark variants for every semantic colour.
 */

export const palette = {
  indigo:  { 400: '#818cf8', 500: '#6366f1', 600: '#4f46e5', 700: '#4338ca' },
  amber:   { 300: '#fcd34d', 400: '#fbbf24', 500: '#f59e0b' },
  sky:     { 300: '#7dd3fc', 400: '#38bdf8', 500: '#0ea5e9' },
  rose:    { 400: '#fb7185', 500: '#f43f5e' },
  emerald: { 400: '#34d399', 500: '#10b981' },
  slate: {
    50:  '#f8fafc',
    100: '#f1f5f9',
    200: '#e2e8f0',
    300: '#cbd5e1',
    400: '#94a3b8',
    500: '#64748b',
    600: '#475569',
    700: '#334155',
    800: '#1e293b',
    900: '#0f172a',
    950: '#020617',
  },
  white: '#ffffff',
  black: '#000000',
} as const

export const light = {
  background:     palette.slate[50],
  surface:        palette.white,
  surfaceElevated:'#ffffff',
  border:         palette.slate[200],
  borderSubtle:   palette.slate[100],

  text:           palette.slate[900],
  textSecondary:  palette.slate[500],
  textMuted:      palette.slate[400],
  textOnGradient: palette.white,

  accent:         palette.indigo[500],
  accentMuted:    palette.indigo[400],
  verseAccent:    palette.amber[500],
  quoteAccent:    palette.sky[500],
  favorite:       palette.rose[500],

  tabBar:         palette.white,
  tabBarBorder:   palette.slate[200],
  tabActive:      palette.indigo[600],
  tabInactive:    palette.slate[400],
} as const

export const dark = {
  background:     palette.slate[950],
  surface:        palette.slate[900],
  surfaceElevated: palette.slate[800],
  border:         palette.slate[800],
  borderSubtle:   palette.slate[700],

  text:           palette.slate[50],
  textSecondary:  palette.slate[400],
  textMuted:      palette.slate[500],
  textOnGradient: palette.white,

  accent:         palette.indigo[400],
  accentMuted:    palette.indigo[500],
  verseAccent:    palette.amber[300],
  quoteAccent:    palette.sky[300],
  favorite:       palette.rose[400],

  tabBar:         palette.slate[900],
  tabBarBorder:   palette.slate[800],
  tabActive:      palette.indigo[400],
  tabInactive:    palette.slate[500],
} as const

export type ColorScheme = typeof light

// Gradient presets — arrays are valid LinearGradient color props
export const gradients = {
  'gradient-dawn': {
    colors: ['#667eea', '#764ba2', '#f093fb'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'gradient-dusk': {
    colors: ['#f6416c', '#ffcd3c', '#f093fb'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'gradient-sky': {
    colors: ['#2193b0', '#6dd5ed'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'gradient-forest': {
    colors: ['#134e5e', '#71b280'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'gradient-ocean': {
    colors: ['#1a1a2e', '#16213e', '#0f3460'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'solid-light': {
    colors: ['#f1f5f9', '#e2e8f0'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
  'solid-dark': {
    colors: ['#0f172a', '#1e293b'] as string[],
    start:  { x: 0, y: 0 },
    end:    { x: 1, y: 1 },
  },
} as const

export type GradientKey = keyof typeof gradients

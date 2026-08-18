import { Platform } from 'react-native'

export const fonts = {
  serif: Platform.select({
    ios:     'Georgia',
    android: 'serif',
    default: 'serif',
  }) as string,
  sans: Platform.select({
    ios:     'System',
    android: 'Roboto',
    default: 'sans-serif',
  }) as string,
}

// Scale multipliers per font-size setting
const scale = { sm: 0.85, md: 1, lg: 1.2, xl: 1.45 } as const
export type FontSizeSetting = keyof typeof scale

export function getVerseTextSize(setting: FontSizeSetting)  { return Math.round(26 * scale[setting]) }
export function getVerseRefSize(setting: FontSizeSetting)   { return Math.round(14 * scale[setting]) }
export function getQuoteTextSize(setting: FontSizeSetting)  { return Math.round(15 * scale[setting]) }
export function getAuthorSize(setting: FontSizeSetting)     { return Math.round(13 * scale[setting]) }

export const spacing = {
  xs:   4,
  sm:   8,
  md:   16,
  lg:   24,
  xl:   32,
  xxl:  48,
} as const

export const radius = {
  sm:   8,
  md:   12,
  lg:   20,
  xl:   28,
  full: 999,
} as const

export const shadow = {
  sm: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 3,
    elevation: 2,
  },
  md: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 6,
  },
  lg: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.25,
    shadowRadius: 16,
    elevation: 12,
  },
} as const

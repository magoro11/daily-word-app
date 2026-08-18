import { useAppStore } from '../store/useAppStore'
import { light, dark }   from './colors'

/** Returns the active colour scheme and a boolean isDark flag. */
export function useTheme() {
  const isDark = useAppStore(s => s.isDark)
  return { colors: isDark ? dark : light, isDark }
}

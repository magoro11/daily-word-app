import { useAppStore } from '../store/useAppStore'
import { formatMs }    from '../utils/format'

export function useTimer() {
  const bibleRemainingMs = useAppStore(s => s.bibleRemainingMs)
  const quoteRemainingMs = useAppStore(s => s.quoteRemainingMs)
  const isPaused         = useAppStore(s => s.isPaused)
  const bibleIntervalMs  = useAppStore(s => s.settings.bibleIntervalMs)
  const quoteIntervalMs  = useAppStore(s => s.settings.motivationIntervalMs)

  return {
    bibleRemaining:   formatMs(bibleRemainingMs),
    quoteRemaining:   formatMs(quoteRemainingMs),
    bibleRemainingMs,
    quoteRemainingMs,
    biblePct:  bibleIntervalMs  > 0 ? bibleRemainingMs  / bibleIntervalMs  : 0,
    quotePct:  quoteIntervalMs  > 0 ? quoteRemainingMs  / quoteIntervalMs  : 0,
    isPaused,
  }
}

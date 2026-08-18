import { useEffect } from 'react'
import { AppState } from 'react-native'
import { DailyWordApp } from '@/components/daily-word/DailyWordApp'
import { engine, useAppStore } from '@/store/useAppStore'
import { sendLocalNotification } from '@/utils/notifications'
import { formatVerseRef } from '@/utils/format'

export default function Index() {
  const hydrate = useAppStore(s => s.hydrate)
  const persistTimerTick = useAppStore(s => s.persistTimerTick)

  useEffect(() => {
    hydrate()
    const tick = setInterval(() => {
      const state = engine.getState()
      persistTimerTick(state.bibleRemainingMs, state.quoteRemainingMs)
    }, 1000)
    const subscription = AppState.addEventListener('change', state => {
      if (state === 'active') {
        const current = engine.getState()
        persistTimerTick(current.bibleRemainingMs, current.quoteRemainingMs)
      }
    })
    const onVerse = () => {
      const { settings, currentVerse } = useAppStore.getState()
      if (settings.notificationsEnabled && settings.notifyOnBibleChange && currentVerse) {
        sendLocalNotification('New Bible Verse', `“${currentVerse.text}”\n${formatVerseRef(currentVerse)}`)
      }
    }
    const onQuote = () => {
      const { settings, currentQuote } = useAppStore.getState()
      if (settings.notificationsEnabled && settings.notifyOnQuoteChange && currentQuote) {
        sendLocalNotification('New Motivation', `“${currentQuote.text}”`)
      }
    }
    engine.on('verse-changed', onVerse)
    engine.on('quote-changed', onQuote)
    return () => { clearInterval(tick); subscription.remove(); engine.off('verse-changed', onVerse); engine.off('quote-changed', onQuote) }
  }, [hydrate, persistTimerTick])

  return <DailyWordApp />
}

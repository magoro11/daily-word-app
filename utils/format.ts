import { format, formatDistanceToNow } from 'date-fns'
import type { BibleVerse } from '../core/types'

export function formatVerseRef(verse: BibleVerse): string {
  const end = verse.verseEnd ? `–${verse.verseEnd}` : ''
  return `${verse.book} ${verse.chapter}:${verse.verse}${end}`
}

export function formatTimestamp(ms: number): string {
  return format(new Date(ms), 'MMM d, yyyy · h:mm a')
}

export function formatRelative(ms: number): string {
  return formatDistanceToNow(new Date(ms), { addSuffix: true })
}

export function formatMs(ms: number): string {
  if (ms <= 0) return '0:00'
  const total   = Math.floor(ms / 1000)
  const hours   = Math.floor(total / 3600)
  const minutes = Math.floor((total % 3600) / 60)
  const seconds = total % 60
  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
  }
  return `${minutes}:${String(seconds).padStart(2, '0')}`
}

/** Truncate long text for previews. */
export function truncate(text: string, maxChars = 120): string {
  return text.length > maxChars ? `${text.slice(0, maxChars).trimEnd()}…` : text
}

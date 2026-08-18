/**
 * Auto-hide controls after a period of inactivity.
 * Call show() on any touch interaction.
 */
import { useState, useCallback, useRef, useEffect } from 'react'

export function useControls(timeoutMs = 3500) {
  const [visible, setVisible] = useState(true)
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null)

  const show = useCallback(() => {
    setVisible(true)
    if (timer.current) clearTimeout(timer.current)
    timer.current = setTimeout(() => setVisible(false), timeoutMs)
  }, [timeoutMs])

  const hide = useCallback(() => {
    setVisible(false)
    if (timer.current) clearTimeout(timer.current)
  }, [])

  // Show once on mount, then auto-hide after timeout
  useEffect(() => {
    show()
    return () => { if (timer.current) clearTimeout(timer.current) }
  }, [show])

  return { visible, show, hide }
}

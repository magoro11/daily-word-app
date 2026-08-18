/**
 * AccurateTimer
 *
 * Wall-clock timestamp based — survives sleep, app backgrounding, and JS
 * timer throttling. Works identically in React Native and Node/browser.
 */
export class AccurateTimer {
  private intervalMs: number
  private callback: () => void
  private startedAt: number | null = null
  private pausedAt: number | null = null
  private accumulatedMs = 0
  private handle: ReturnType<typeof setInterval> | null = null
  private readonly tickMs = 1000

  constructor(intervalMs: number, callback: () => void) {
    this.intervalMs = intervalMs
    this.callback   = callback
  }

  start() {
    if (this.startedAt !== null) return
    this.startedAt     = Date.now()
    this.accumulatedMs = 0
    this.pausedAt      = null
    this.scheduleTick()
  }

  pause() {
    if (this.pausedAt !== null || this.startedAt === null) return
    this.pausedAt       = Date.now()
    this.accumulatedMs += this.pausedAt - this.startedAt
    this.startedAt      = null
    this.clearTick()
  }

  resume() {
    if (this.pausedAt === null) return
    this.startedAt = Date.now()
    this.pausedAt  = null
    this.scheduleTick()
  }

  reset() {
    this.clearTick()
    this.startedAt     = Date.now()
    this.accumulatedMs = 0
    this.pausedAt      = null
    this.scheduleTick()
  }

  setInterval(ms: number) {
    this.intervalMs = ms
    this.reset()
  }

  destroy() {
    this.clearTick()
    this.startedAt = null
    this.pausedAt  = null
  }

  getRemainingMs(): number {
    return Math.max(0, this.intervalMs - this.getElapsedMs())
  }

  getElapsedMs(): number {
    if (this.pausedAt  !== null) return this.accumulatedMs
    if (this.startedAt !== null) return this.accumulatedMs + (Date.now() - this.startedAt)
    return 0
  }

  isPaused():  boolean { return this.pausedAt  !== null }
  isRunning(): boolean { return this.startedAt !== null }

  private scheduleTick() {
    this.clearTick()
    this.handle = setInterval(() => this.tick(), this.tickMs)
  }

  private clearTick() {
    if (this.handle !== null) { clearInterval(this.handle); this.handle = null }
  }

  private tick() {
    if (this.getElapsedMs() >= this.intervalMs) {
      this.startedAt     = Date.now()
      this.accumulatedMs = 0
      this.callback()
    }
  }
}

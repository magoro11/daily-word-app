/**
 * AccurateTimer
 *
 * Wall-clock timestamp based — survives sleep, app backgrounding, and JS
 * timer throttling. Works identically in React Native and Node/browser.
 */
export class AccurateTimer {
  private intervalMs: number
  private readonly callback: () => void
  private nextFireAt = 0
  private pausedRemainingMs: number | null = null
  private handle: ReturnType<typeof setInterval> | null = null

  constructor(intervalMs: number, callback: () => void) {
    this.intervalMs = intervalMs
    this.callback = callback
  }

  start(restoredNextFireAt?: number) {
    if (this.handle || this.pausedRemainingMs !== null) return
    this.nextFireAt = restoredNextFireAt && restoredNextFireAt > 0 ? restoredNextFireAt : Date.now() + this.intervalMs
    this.scheduleTick()
    this.tick()
  }

  pause() {
    if (this.pausedRemainingMs !== null) return
    this.pausedRemainingMs = this.getRemainingMs()
    this.clearTick()
  }

  resume() {
    if (this.pausedRemainingMs === null) return
    this.nextFireAt = Date.now() + this.pausedRemainingMs
    this.pausedRemainingMs = null
    this.scheduleTick()
  }

  reset() {
    this.clearTick()
    this.nextFireAt = Date.now() + this.intervalMs
    this.pausedRemainingMs = null
    this.scheduleTick()
  }

  setInterval(ms: number) {
    this.intervalMs = ms
    this.reset()
  }

  destroy() {
    this.clearTick()
    this.nextFireAt = 0
    this.pausedRemainingMs = null
  }

  getNextFireAt(): number { return this.pausedRemainingMs === null ? this.nextFireAt : Date.now() + this.pausedRemainingMs }
  getRemainingMs(): number { return this.pausedRemainingMs ?? Math.max(0, this.nextFireAt - Date.now()) }
  isPaused(): boolean { return this.pausedRemainingMs !== null }
  isRunning(): boolean { return this.handle !== null }

  private scheduleTick() {
    this.clearTick()
    this.handle = setInterval(() => this.tick(), 1000)
  }

  private clearTick() {
    if (this.handle !== null) { clearInterval(this.handle); this.handle = null }
  }

  private tick() {
    if (this.nextFireAt && Date.now() >= this.nextFireAt) {
      this.nextFireAt = Date.now() + this.intervalMs
      this.callback()
    }
  }
}

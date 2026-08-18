/** Fisher-Yates shuffle — returns a new array, original untouched. */
export function shuffle<T>(array: T[]): T[] {
  const arr = [...array]
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[arr[i], arr[j]] = [arr[j], arr[i]]
  }
  return arr
}

/** Shows every item once before repeating — refills with a fresh shuffle when exhausted. */
export class ShuffleDeck<T extends { id: string }> {
  private deck: string[] = []
  private pool: T[] = []

  constructor(items: T[]) {
    this.pool = items
    this.refill()
  }

  private refill() {
    this.deck = shuffle(this.pool.map(i => i.id))
  }

  setPool(items: T[]) {
    this.pool = items
    this.refill()
  }

  next(): T | undefined {
    if (this.pool.length === 0) return undefined
    if (this.deck.length === 0) this.refill()
    const id = this.deck.shift()!
    return this.pool.find(i => i.id === id)
  }

  remaining(): number { return this.deck.length }
  getPool(): T[]      { return this.pool }
}

/** Cycles through the pool in order, wrapping at the end. */
export class SequentialCursor<T extends { id: string }> {
  private index = 0
  private pool: T[] = []

  constructor(items: T[]) { this.pool = items }

  setPool(items: T[]) { this.pool = items; this.index = 0 }

  next(): T | undefined {
    if (this.pool.length === 0) return undefined
    const item = this.pool[this.index % this.pool.length]
    this.index = (this.index + 1) % this.pool.length
    return item
  }

  prev(): T | undefined {
    if (this.pool.length === 0) return undefined
    this.index = (this.index - 2 + this.pool.length) % this.pool.length
    const item = this.pool[this.index]
    this.index = (this.index + 1) % this.pool.length
    return item
  }

  getPool(): T[] { return this.pool }
}

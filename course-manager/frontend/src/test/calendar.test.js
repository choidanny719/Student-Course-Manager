import { describe, expect, it } from 'vitest'
import { monthDays, shiftMonth } from '../calendar'

describe('calendar dates', () => {
  it('includes leap day and complete Monday-to-Sunday weeks', () => {
    const days = monthDays('2028-02')
    expect(days[0]).toBe('2028-01-31')
    expect(days.at(-1)).toBe('2028-03-05')
    expect(days).toContain('2028-02-29')
    expect(days).not.toContain('2028-02-30')
    expect(new Set(days).size).toBe(35)
  })

  it('moves through year boundaries in both directions', () => {
    expect(shiftMonth('2026-12', 1)).toBe('2027-01')
    expect(shiftMonth('2026-01', -1)).toBe('2025-12')
  })
})

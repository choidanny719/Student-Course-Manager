import { describe, expect, it } from 'vitest'
import { assignmentStatus, filterQuery, readFilters } from '../dates'

describe('deadline presentation', () => {
  it('uses calendar dates to distinguish overdue from due today', () => {
    expect(assignmentStatus({ dueDate: '2026-10-01', completed: false }, '2026-10-02')).toBe(
      'Overdue',
    )
    expect(assignmentStatus({ dueDate: '2026-10-02', completed: false }, '2026-10-02')).toBe(
      'Due today',
    )
    expect(assignmentStatus({ dueDate: '2026-10-01', completed: true }, '2026-10-02')).toBe(
      'Completed',
    )
  })

  it('excludes old custom dates when a preset is selected', () => {
    expect(
      filterQuery({
        status: 'upcoming',
        days: '14',
        courseId: '',
        from: '2025-01-01',
        to: '2025-02-01',
      }),
    ).toBe('status=upcoming&days=14')
  })

  it('recovers from corrupted saved preferences', () => {
    localStorage.setItem('course-manager.filters', '{')
    expect(readFilters()).toMatchObject({ status: 'upcoming', days: '7' })
  })
})

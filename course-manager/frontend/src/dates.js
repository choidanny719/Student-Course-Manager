export function dateLabel(value) {
  return new Intl.DateTimeFormat('en-CA', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(`${value}T12:00:00`))
}

export function assignmentStatus(assignment, today) {
  if (assignment.completed) return 'Completed'
  if (assignment.dueDate < today) return 'Overdue'
  if (assignment.dueDate === today) return 'Due today'
  return 'Upcoming'
}

export const defaultFilters = { courseId: '', status: 'upcoming', days: '7', from: '', to: '' }

export function readFilters() {
  try {
    const saved = JSON.parse(localStorage.getItem('course-manager.filters'))
    if (!saved || !['all', 'pending', 'upcoming', 'overdue', 'completed'].includes(saved.status))
      return defaultFilters
    return {
      ...defaultFilters,
      status: saved.status,
      courseId:
        typeof saved.courseId === 'string' && /^\d*$/.test(saved.courseId) ? saved.courseId : '',
      days: ['3', '7', '14', '30', 'custom'].includes(saved.days) ? saved.days : '7',
      from:
        typeof saved.from === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(saved.from) ? saved.from : '',
      to: typeof saved.to === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(saved.to) ? saved.to : '',
    }
  } catch {
    return defaultFilters
  }
}

export function filterQuery(filters) {
  const query = new URLSearchParams({ status: filters.status })
  if (filters.courseId) query.set('courseId', filters.courseId)
  if (filters.status === 'upcoming' && filters.days !== 'custom') {
    query.set('days', filters.days)
  } else {
    if (filters.from) query.set('from', filters.from)
    if (filters.to) query.set('to', filters.to)
  }
  return query.toString()
}

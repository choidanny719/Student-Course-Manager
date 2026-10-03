import { useState } from 'react'
import { defaultFilters, filterQuery, readFilters } from '../dates'
import { useAssignments } from '../useWorkspace'
import AssignmentList from './AssignmentList'

export default function DeadlineView({ workspace, version, onAdd, ...actions }) {
  const [filters, setFilters] = useState(readFilters)
  const [draft, setDraft] = useState(filters)
  const [error, setError] = useState('')
  const courseId = workspace.courses.some((course) => String(course.id) === filters.courseId)
    ? filters.courseId
    : ''
  const result = useAssignments(filterQuery({ ...filters, courseId }), version)

  function change(event) {
    setDraft({ ...draft, [event.target.name]: event.target.value })
  }

  function apply(event) {
    event.preventDefault()
    const custom = draft.status !== 'upcoming' || draft.days === 'custom'
    if (custom && draft.from && draft.to && draft.from > draft.to) {
      setError('Start date must be on or before end date.')
      return
    }
    setError('')
    setFilters(draft)
    try {
      localStorage.setItem('course-manager.filters', JSON.stringify(draft))
    } catch {
      return
    }
  }

  function reset() {
    setDraft(defaultFilters)
    setFilters(defaultFilters)
    setError('')
    try {
      localStorage.removeItem('course-manager.filters')
    } catch {
      return
    }
  }

  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>Deadlines</h1>
          <p className="muted">See what’s due, and keep track of what’s done.</p>
        </div>
        <button
          className="primary"
          disabled={!workspace.courses.length}
          onClick={() => onAdd('assignment')}
        >
          Add assignment
        </button>
      </div>
      <form className="filters" onSubmit={apply}>
        <label>
          Course
          <select name="courseId" value={draft.courseId} onChange={change}>
            <option value="">All courses</option>
            {workspace.courses.map((course) => (
              <option key={course.id} value={course.id}>
                {course.courseCode}
              </option>
            ))}
          </select>
        </label>
        <label>
          Show
          <select name="status" value={draft.status} onChange={change}>
            <option value="upcoming">Upcoming</option>
            <option value="overdue">Overdue</option>
            <option value="pending">Incomplete</option>
            <option value="completed">Completed</option>
            <option value="all">All assignments</option>
          </select>
        </label>
        {draft.status === 'upcoming' && (
          <label>
            Window
            <select name="days" value={draft.days} onChange={change}>
              {[3, 7, 14, 30].map((days) => (
                <option key={days} value={days}>
                  Next {days} days
                </option>
              ))}
              <option value="custom">Custom dates</option>
            </select>
          </label>
        )}
        {(draft.status !== 'upcoming' || draft.days === 'custom') && (
          <>
            <label>
              From
              <input name="from" type="date" value={draft.from} onChange={change} />
            </label>
            <label>
              To
              <input name="to" type="date" value={draft.to} onChange={change} />
            </label>
          </>
        )}
        <button type="submit">Apply filters</button>
        <button type="button" className="quiet" onClick={reset}>
          Reset
        </button>
        {error && (
          <p className="error filter-error" role="alert">
            {error}
          </p>
        )}
      </form>
      <div className="section-heading">
        <h2>Assignments</h2>
        <span className="muted">{!result.loading && `${result.data.length} shown`}</span>
      </div>
      {result.loading ? (
        <p role="status" className="loading">
          Loading assignments…
        </p>
      ) : result.error ? (
        <p role="alert" className="error">
          {result.error}
        </p>
      ) : (
        <AssignmentList assignments={result.data} today={workspace.settings.today} {...actions} />
      )}
    </section>
  )
}

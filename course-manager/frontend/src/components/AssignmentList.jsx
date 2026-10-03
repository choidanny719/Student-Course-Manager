import { useState } from 'react'
import { assignmentStatus, dateLabel } from '../dates'

function AssignmentRow({ assignment, today, onComplete, onEdit, onDelete }) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const status = assignmentStatus(assignment, today)

  async function toggle(event) {
    setBusy(true)
    setError('')
    try {
      await onComplete(assignment, event.target.checked)
    } catch (error) {
      setError(error.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <article
      className={`assignment-row ${assignment.completed ? 'is-complete' : ''}`}
      aria-label={assignment.title}
    >
      <div className="assignment-body">
        <div className="assignment-title">
          <h3>{assignment.title}</h3>
          <span className={`status ${status === 'Overdue' ? 'late' : ''}`}>{status}</span>
        </div>
        <p className="assignment-meta">
          {assignment.courseCode} <span aria-hidden="true">·</span> Due{' '}
          {dateLabel(assignment.dueDate)}
        </p>
        {assignment.description && <p className="assignment-notes">{assignment.description}</p>}
        {error && (
          <p className="error" role="alert">
            {error}
          </p>
        )}
      </div>
      <div className="assignment-actions">
        <label className="check">
          <input
            type="checkbox"
            aria-label={`Completed: ${assignment.title}`}
            checked={assignment.completed}
            onChange={toggle}
            disabled={busy}
          />
          Completed
        </label>
        <div className="row-actions">
          <button className="quiet" onClick={() => onEdit('assignment', assignment)}>
            Edit
          </button>
          <button className="quiet" onClick={() => onDelete('assignment', assignment)}>
            Delete
          </button>
        </div>
      </div>
    </article>
  )
}

export default function AssignmentList({ assignments, ...props }) {
  if (!assignments.length)
    return (
      <div className="empty">
        <h3>No assignments here</h3>
        <p>Add an assignment or choose a different filter.</p>
      </div>
    )
  return (
    <div className="assignment-list">
      {assignments.map((assignment) => (
        <AssignmentRow key={assignment.id} assignment={assignment} {...props} />
      ))}
    </div>
  )
}

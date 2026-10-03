import { useState } from 'react'
import Dialog from './Dialog'

export default function DeleteDialog({ kind, item, onDelete, onClose }) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const name = item.title || item.name || `${item.studentName} from ${item.courseCode}`

  async function remove() {
    setBusy(true)
    try {
      await onDelete(kind, item)
      onClose()
    } catch (error) {
      setError(error.message)
      setBusy(false)
    }
  }

  return (
    <Dialog title={`Delete ${kind}`} onClose={onClose} busy={busy}>
      <p>
        Delete <strong>{name}</strong>?
      </p>
      {kind === 'course' && (
        <p className="muted">
          Its assignments and enrollments will also be deleted. Student records will remain.
        </p>
      )}
      {kind === 'student' && (
        <p className="muted">
          Their enrollments will also be deleted. Courses and assignments will remain.
        </p>
      )}
      {error && (
        <p className="error" role="alert">
          {error}
        </p>
      )}
      <div className="dialog-actions">
        <button onClick={onClose} disabled={busy}>
          Cancel
        </button>
        <button className="danger" onClick={remove} disabled={busy}>
          {busy ? 'Deleting…' : `Delete ${kind}`}
        </button>
      </div>
    </Dialog>
  )
}

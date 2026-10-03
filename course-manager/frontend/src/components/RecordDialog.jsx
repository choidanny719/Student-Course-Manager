import { useState } from 'react'
import Dialog from './Dialog'

const names = {
  course: 'course',
  assignment: 'assignment',
  student: 'student',
  enrollment: 'enrollment',
}

export default function RecordDialog({
  kind,
  item,
  courseId,
  dueDate,
  workspace,
  onSave,
  onClose,
}) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const title = `${item ? 'Edit' : 'Add'} ${names[kind]}`

  async function submit(event) {
    event.preventDefault()
    const data = Object.fromEntries(new FormData(event.currentTarget))
    if (kind === 'assignment') {
      data.completed = item?.completed ?? false
      data.courseId = Number(item?.courseId ?? data.courseId ?? courseId)
    }
    if (kind === 'enrollment') {
      data.studentId = Number(data.studentId)
      data.courseId = Number(data.courseId)
    }
    setBusy(true)
    setError('')
    try {
      await onSave(kind, item, data)
      onClose()
    } catch (error) {
      setError(error.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <Dialog title={title} onClose={onClose} busy={busy}>
      <form onSubmit={submit}>
        <fieldset disabled={busy} className="form-fields">
          {kind === 'course' && (
            <>
              <label>
                Course code
                <input
                  name="courseCode"
                  required
                  maxLength={24}
                  defaultValue={item?.courseCode ?? ''}
                  placeholder="CMPT 307"
                />
              </label>
              <label>
                Course name
                <input name="name" required maxLength={120} defaultValue={item?.name ?? ''} />
              </label>
              <label>
                Description <span className="muted">(optional)</span>
                <textarea
                  name="description"
                  maxLength={2000}
                  rows={3}
                  defaultValue={item?.description ?? ''}
                />
              </label>
            </>
          )}
          {kind === 'student' && (
            <>
              <label>
                Name
                <input name="name" required maxLength={100} defaultValue={item?.name ?? ''} />
              </label>
              <label>
                Email
                <input
                  name="email"
                  type="email"
                  required
                  maxLength={160}
                  defaultValue={item?.email ?? ''}
                />
              </label>
            </>
          )}
          {kind === 'assignment' && (
            <>
              <label>
                Course
                <select
                  name="courseId"
                  defaultValue={item?.courseId ?? courseId ?? ''}
                  required
                  disabled={Boolean(item)}
                >
                  <option value="" disabled>
                    Select a course
                  </option>
                  {workspace.courses.map((course) => (
                    <option key={course.id} value={course.id}>
                      {course.courseCode} · {course.name}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Title
                <input name="title" required maxLength={200} defaultValue={item?.title ?? ''} />
              </label>
              <label>
                Due date
                <input
                  type="date"
                  name="dueDate"
                  required
                  defaultValue={item?.dueDate ?? dueDate ?? workspace.settings.today}
                />
              </label>
              <label>
                Notes <span className="muted">(optional)</span>
                <textarea
                  name="description"
                  rows={4}
                  maxLength={5000}
                  defaultValue={item?.description ?? ''}
                />
              </label>
            </>
          )}
          {kind === 'enrollment' && (
            <>
              <label>
                Student
                <select name="studentId" defaultValue={item?.studentId ?? ''} required>
                  <option value="" disabled>
                    Select a student
                  </option>
                  {workspace.students.map((student) => (
                    <option key={student.id} value={student.id}>
                      {student.name} · {student.email}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Course
                <select name="courseId" defaultValue={item?.courseId ?? courseId ?? ''} required>
                  <option value="" disabled>
                    Select a course
                  </option>
                  {workspace.courses.map((course) => (
                    <option key={course.id} value={course.id}>
                      {course.courseCode} · {course.name}
                    </option>
                  ))}
                </select>
              </label>
            </>
          )}
        </fieldset>
        {error && (
          <p className="error" role="alert">
            {error}
          </p>
        )}
        <div className="dialog-actions">
          <button type="button" onClick={onClose} disabled={busy}>
            Cancel
          </button>
          <button className="primary" disabled={busy}>
            {busy ? 'Saving…' : item ? 'Save changes' : `Add ${names[kind]}`}
          </button>
        </div>
      </form>
    </Dialog>
  )
}

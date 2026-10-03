import { useState } from 'react'
import { api } from './api'
import { dateLabel } from './dates'
import { useWorkspace } from './useWorkspace'
import CourseView from './components/CourseView'
import DeadlineView from './components/DeadlineView'
import StudentView from './components/StudentView'
import RecordDialog from './components/RecordDialog'
import DeleteDialog from './components/DeleteDialog'
import './App.css'

const paths = {
  course: '/courses',
  student: '/students',
  assignment: '/assignments',
  enrollment: '/enrollments',
}

export default function App() {
  const { data, error, reload, version, replaceAssignment } = useWorkspace()
  const [page, setPage] = useState('Courses')
  const [selected, setSelected] = useState(null)
  const [dialog, setDialog] = useState(null)
  const [notice, setNotice] = useState('')

  function add(kind, courseId, dueDate) {
    setDialog({ kind, courseId, dueDate })
  }
  function edit(kind, item) {
    setDialog({ kind, item })
  }
  function remove(kind, item) {
    setDialog({ kind, item, deleting: true })
  }

  async function save(kind, item, input) {
    const path = item
      ? `${paths[kind]}/${item.id}`
      : kind === 'assignment'
        ? `/courses/${input.courseId}/assignments`
        : paths[kind]
    const { courseId, ...assignmentInput } = input
    const saved = await api(path, {
      method: item ? 'PUT' : 'POST',
      body: kind === 'assignment' ? assignmentInput : input,
    })
    if (kind === 'course') setSelected(saved.id)
    if (kind === 'assignment') setSelected(courseId)
    await reload()
    setNotice(`${kind[0].toUpperCase() + kind.slice(1)} ${item ? 'updated' : 'added'}.`)
  }

  async function destroy(kind, item) {
    await api(`${paths[kind]}/${item.id}`, { method: 'DELETE' })
    await reload()
    setNotice(`${kind[0].toUpperCase() + kind.slice(1)} deleted.`)
  }

  async function complete(assignment, completed) {
    const updated = await api(`/assignments/${assignment.id}/completion`, {
      method: 'PATCH',
      body: { completed },
    })
    replaceAssignment(updated)
  }

  const actions = { onAdd: add, onEdit: edit, onDelete: remove, onComplete: complete }

  return (
    <>
      <a className="skip-link" href="#main">
        Skip to content
      </a>
      <header className="site-header">
        <div className="header-inner">
          <a className="brand" href="#main" onClick={() => setPage('Courses')}>
            Course Manager
          </a>
          <nav aria-label="Main navigation">
            {['Courses', 'Deadlines', 'Students'].map((name) => (
              <button
                key={name}
                aria-current={page === name ? 'page' : undefined}
                onClick={() => setPage(name)}
              >
                {name}
              </button>
            ))}
          </nav>
        </div>
      </header>
      <main id="main" tabIndex={-1}>
        {error && (
          <div className="error-banner" role="alert">
            <span>{error}</span>
            <button onClick={reload}>Retry</button>
          </div>
        )}
        {notice && (
          <div className="notice" role="status">
            <span>{notice}</span>
            <button
              className="quiet"
              aria-label="Dismiss notification"
              onClick={() => setNotice('')}
            >
              Dismiss
            </button>
          </div>
        )}
        {!data ? (
          !error && (
            <p className="loading" role="status">
              Loading your workspace…
            </p>
          )
        ) : (
          <>
            {page === 'Courses' && (
              <CourseView
                workspace={data}
                selected={selected}
                onSelect={setSelected}
                {...actions}
              />
            )}
            {page === 'Deadlines' && (
              <DeadlineView workspace={data} version={version} {...actions} />
            )}
            {page === 'Students' && <StudentView workspace={data} {...actions} />}
          </>
        )}
      </main>
      {data && (
        <footer>
          <span>{dateLabel(data.settings.today)}</span>
          <span>Dates use {data.settings.timeZone.replaceAll('_', ' ')} time.</span>
        </footer>
      )}
      {dialog &&
        data &&
        (dialog.deleting ? (
          <DeleteDialog {...dialog} onDelete={destroy} onClose={() => setDialog(null)} />
        ) : (
          <RecordDialog
            {...dialog}
            workspace={data}
            onSave={save}
            onClose={() => setDialog(null)}
          />
        ))}
    </>
  )
}

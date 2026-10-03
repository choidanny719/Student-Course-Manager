import AssignmentList from './AssignmentList'

export default function CourseView({
  workspace,
  selected,
  onSelect,
  onAdd,
  onEdit,
  onDelete,
  onComplete,
}) {
  const course = workspace.courses.find((course) => course.id === selected) ?? workspace.courses[0]
  const assignments = workspace.assignments.filter((item) => item.courseId === course?.id)
  const enrollments = workspace.enrollments.filter((item) => item.courseId === course?.id)

  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>Courses</h1>
          <p className="muted">Your coursework, in one place.</p>
        </div>
        <button className="primary" onClick={() => onAdd('course')}>
          Add course
        </button>
      </div>
      {!course ? (
        <div className="empty welcome">
          <h2>Start with a course</h2>
          <p>Add a course, then give its assignments a title and due date.</p>
          <button onClick={() => onAdd('course')}>Add your first course</button>
        </div>
      ) : (
        <div className="course-layout">
          <aside className="course-sidebar" aria-label="Course list">
            {workspace.courses.map((item) => (
              <button
                key={item.id}
                aria-pressed={item.id === course.id}
                onClick={() => onSelect(item.id)}
              >
                <strong>{item.courseCode}</strong>
                <span>{item.name}</span>
              </button>
            ))}
          </aside>
          <div className="course-content">
            <div className="course-heading">
              <div>
                <p className="eyebrow">{course.courseCode}</p>
                <h2>{course.name}</h2>
                {course.description && <p className="muted description">{course.description}</p>}
              </div>
              <div className="row-actions">
                <button onClick={() => onEdit('course', course)}>Edit course</button>
                <button className="quiet" onClick={() => onDelete('course', course)}>
                  Delete course
                </button>
              </div>
            </div>
            <div className="section-heading">
              <h2>
                Assignments <span className="count">{assignments.length}</span>
              </h2>
              <button onClick={() => onAdd('assignment', course.id)}>Add assignment</button>
            </div>
            <AssignmentList
              assignments={assignments}
              today={workspace.settings.today}
              onEdit={onEdit}
              onDelete={onDelete}
              onComplete={onComplete}
            />
            <div className="section-heading roster-heading">
              <h2>
                Enrolled students <span className="count">{enrollments.length}</span>
              </h2>
              <button
                disabled={!workspace.students.length}
                onClick={() => onAdd('enrollment', course.id)}
              >
                Add enrollment
              </button>
            </div>
            {!workspace.students.length ? (
              <p className="muted">Add a student in the Students tab to manage enrollments.</p>
            ) : !enrollments.length ? (
              <p className="muted">No students enrolled yet.</p>
            ) : (
              <ul className="plain-list">
                {enrollments.map((item) => (
                  <li key={item.id}>
                    <div>
                      <strong>{item.studentName}</strong>
                      <span className="muted">{item.studentEmail}</span>
                    </div>
                    <div className="row-actions">
                      <button className="quiet" onClick={() => onEdit('enrollment', item)}>
                        Edit enrollment
                      </button>
                      <button className="quiet" onClick={() => onDelete('enrollment', item)}>
                        Remove
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </section>
  )
}

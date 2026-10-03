export default function StudentView({ workspace, onAdd, onEdit, onDelete }) {
  return (
    <section>
      <div className="page-heading">
        <div>
          <h1>Students</h1>
          <p className="muted">Student records and course enrollments.</p>
        </div>
        <button className="primary" onClick={() => onAdd('student')}>
          Add student
        </button>
      </div>
      {!workspace.students.length ? (
        <div className="empty">
          <h2>No students yet</h2>
          <p>Add a student, then enroll them in a course.</p>
        </div>
      ) : (
        <ul className="plain-list student-list">
          {workspace.students.map((student) => {
            const enrollments = workspace.enrollments.filter(
              (item) => item.studentId === student.id,
            )
            return (
              <li key={student.id}>
                <div>
                  <h2>{student.name}</h2>
                  <p className="muted">{student.email}</p>
                  <p className="enrollment-codes">
                    {enrollments.length
                      ? enrollments.map((item) => item.courseCode).join(' · ')
                      : 'No enrollments'}
                  </p>
                </div>
                <div className="row-actions">
                  <button onClick={() => onEdit('student', student)}>Edit student</button>
                  <button className="quiet" onClick={() => onDelete('student', student)}>
                    Delete student
                  </button>
                </div>
              </li>
            )
          })}
        </ul>
      )}
    </section>
  )
}

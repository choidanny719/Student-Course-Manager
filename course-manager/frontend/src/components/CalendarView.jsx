import { useState } from 'react'
import { monthDays, shiftMonth } from '../calendar'
import { dateLabel } from '../dates'
import AssignmentList from './AssignmentList'

export default function CalendarView({
  assignments,
  today,
  initialDate,
  canAdd,
  onAdd,
  ...actions
}) {
  const [month, setMonth] = useState((initialDate || today).slice(0, 7))
  const [selected, setSelected] = useState(initialDate || today)
  const days = monthDays(month)
  const weeks = Array.from({ length: days.length / 7 }, (_, index) =>
    days.slice(index * 7, index * 7 + 7),
  )
  const byDate = Map.groupBy(assignments, (assignment) => assignment.dueDate)
  const label = new Intl.DateTimeFormat('en-CA', { month: 'long', year: 'numeric' }).format(
    new Date(`${month}-01T12:00:00`),
  )

  function move(offset) {
    const next = shiftMonth(month, offset)
    setMonth(next)
    setSelected(`${next}-01`)
  }

  return (
    <div>
      <div className="calendar-heading">
        <h2 aria-live="polite">{label}</h2>
        <div className="row-actions">
          <button aria-label="Previous month" onClick={() => move(-1)}>
            ←
          </button>
          <button
            onClick={() => {
              setMonth(today.slice(0, 7))
              setSelected(today)
            }}
          >
            Today
          </button>
          <button aria-label="Next month" onClick={() => move(1)}>
            →
          </button>
        </div>
      </div>
      <p className="calendar-note muted">
        The calendar follows the filters above. Select a day to view its assignments.
      </p>
      <table className="calendar" aria-label={label}>
        <thead>
          <tr>
            {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day) => (
              <th key={day} scope="col">
                {day}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {weeks.map((week) => (
            <tr key={week[0]}>
              {week.map((date) => {
                const items = byDate.get(date) || []
                return (
                  <td key={date} className={date.startsWith(month) ? '' : 'outside-month'}>
                    <button
                      className="calendar-day"
                      aria-label={`${dateLabel(date)}, ${items.length} assignments`}
                      aria-pressed={selected === date}
                      aria-current={date === today ? 'date' : undefined}
                      onClick={() => setSelected(date)}
                    >
                      <span className="day-number">{Number(date.slice(-2))}</span>
                      {items.slice(0, 2).map((item) => (
                        <span
                          key={item.id}
                          className={`day-assignment ${item.completed ? 'done' : ''}`}
                        >
                          {item.title}
                        </span>
                      ))}
                      {items.length > 2 && (
                        <span className="day-more">+{items.length - 2} more</span>
                      )}
                      {items.length > 0 && <span className="day-count">{items.length} due</span>}
                    </button>
                  </td>
                )
              })}
            </tr>
          ))}
        </tbody>
      </table>
      <div className="section-heading">
        <h2>{dateLabel(selected)}</h2>
        <button disabled={!canAdd} onClick={() => onAdd('assignment', undefined, selected)}>
          Add for this date
        </button>
      </div>
      <AssignmentList assignments={byDate.get(selected) || []} today={today} {...actions} />
    </div>
  )
}

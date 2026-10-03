export function dateKey(date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

export function shiftMonth(month, offset) {
  const [year, number] = month.split('-').map(Number)
  return dateKey(new Date(year, number - 1 + offset, 1, 12)).slice(0, 7)
}

export function monthDays(month) {
  const [year, number] = month.split('-').map(Number)
  const first = new Date(year, number - 1, 1, 12)
  const offset = (first.getDay() + 6) % 7
  const count = new Date(year, number, 0, 12).getDate()
  const cells = Math.ceil((offset + count) / 7) * 7
  return Array.from({ length: cells }, (_, index) =>
    dateKey(new Date(year, number - 1, 1 - offset + index, 12)),
  )
}

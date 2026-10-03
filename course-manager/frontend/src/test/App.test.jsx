import { beforeEach, describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor, within } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from '../App'

let data

beforeEach(() => {
  localStorage.clear()
  data = {
    courses: [{ id: 1, courseCode: 'CMPT 307', name: 'Algorithms', description: '' }],
    students: [{ id: 1, name: 'Alex', email: 'alex@example.com' }],
    enrollments: [],
    assignments: [
      {
        id: 1,
        courseId: 1,
        courseCode: 'CMPT 307',
        courseName: 'Algorithms',
        title: 'Problem set',
        description: '',
        dueDate: '2026-10-03',
        completed: false,
      },
    ],
    settings: { today: '2026-10-02', timeZone: 'America/Vancouver' },
  }
  vi.stubGlobal(
    'fetch',
    vi.fn(async (url, options = {}) => {
      const resource = url.split('/')[2].split('?')[0]
      if (options.method === 'PATCH') {
        data.assignments[0].completed = JSON.parse(options.body).completed
        return { ok: true, status: 200, json: async () => structuredClone(data.assignments[0]) }
      }
      if (options.method === 'POST' && resource === 'courses') {
        return {
          ok: false,
          status: 409,
          json: async () => ({ message: 'A course with that code already exists' }),
        }
      }
      return { ok: true, status: 200, json: async () => structuredClone(data[resource]) }
    }),
  )
})

async function openApp() {
  render(<App />)
  await screen.findByRole('heading', { name: 'Algorithms' })
  return userEvent.setup()
}

describe('course workspace', () => {
  it('restores the checkbox and shows an error if saving completion fails', async () => {
    const user = await openApp()
    fetch.mockResolvedValueOnce({
      ok: false,
      status: 500,
      json: async () => ({ message: 'Unable to save completion' }),
    })
    const checkbox = screen.getByRole('checkbox', { name: 'Completed: Problem set' })
    await user.click(checkbox)
    expect(await screen.findByRole('alert')).toHaveTextContent('Unable to save completion')
    expect(checkbox).not.toBeChecked()
    expect(checkbox).toBeEnabled()
  })
  it('keeps the completion checkbox visible and supports both directions', async () => {
    const user = await openApp()
    const checkbox = screen.getByRole('checkbox', { name: 'Completed: Problem set' })
    expect(checkbox).not.toBeChecked()
    await user.click(checkbox)
    await waitFor(() => expect(checkbox).toBeChecked())
    expect(screen.getByRole('article', { name: 'Problem set' })).toBeVisible()
    await user.click(checkbox)
    await waitFor(() => expect(checkbox).not.toBeChecked())
    expect(fetch).toHaveBeenCalledWith(
      '/api/assignments/1/completion',
      expect.objectContaining({ method: 'PATCH', body: '{"completed":false}' }),
    )
  })

  it('retains form values and shows the server error on a duplicate course', async () => {
    const user = await openApp()
    await user.click(screen.getByRole('button', { name: 'Add course', exact: true }))
    const dialog = screen.getByRole('dialog', { name: 'Add course' })
    await user.type(within(dialog).getByLabelText('Course code'), 'CMPT 307')
    await user.type(within(dialog).getByLabelText('Course name'), 'Algorithms')
    await user.click(within(dialog).getByRole('button', { name: 'Add course' }))
    expect(await within(dialog).findByRole('alert')).toHaveTextContent('already exists')
    expect(within(dialog).getByLabelText('Course code')).toHaveValue('CMPT 307')
    expect(within(dialog).getByRole('button', { name: 'Add course' })).toBeEnabled()
  })

  it('offers an empty state when there are no courses', async () => {
    data.courses = []
    data.assignments = []
    render(<App />)
    expect(await screen.findByRole('heading', { name: 'Start with a course' })).toBeVisible()
    expect(screen.getByRole('button', { name: 'Add your first course' })).toBeEnabled()
  })

  it('shows a retry action when the backend is unavailable', async () => {
    fetch.mockRejectedValue(new TypeError('Failed to fetch'))
    render(<App />)
    expect(await screen.findByRole('alert')).toHaveTextContent('Cannot reach the server')
    expect(screen.getByRole('button', { name: 'Retry' })).toBeEnabled()
  })

  it('explains related deletions and allows cancellation', async () => {
    const user = await openApp()
    await user.click(screen.getByRole('button', { name: 'Delete course' }))
    const dialog = screen.getByRole('dialog', { name: 'Delete course' })
    expect(dialog).toHaveTextContent('assignments and enrollments will also be deleted')
    await user.click(within(dialog).getByRole('button', { name: 'Cancel' }))
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
    expect(fetch.mock.calls.some(([, options]) => options.method === 'DELETE')).toBe(false)
  })

  it('sends custom ranges to the API and remembers the chosen filters', async () => {
    const user = await openApp()
    await user.click(screen.getByRole('button', { name: 'Deadlines', exact: true }))
    await user.selectOptions(screen.getByLabelText('Show'), 'all')
    await user.type(screen.getByLabelText('From'), '2026-10-01')
    await user.type(screen.getByLabelText('To'), '2026-10-31')
    await user.click(screen.getByRole('button', { name: 'Apply filters' }))
    await waitFor(() =>
      expect(fetch).toHaveBeenCalledWith(
        '/api/assignments?status=all&from=2026-10-01&to=2026-10-31',
        expect.any(Object),
      ),
    )
    expect(JSON.parse(localStorage.getItem('course-manager.filters'))).toMatchObject({
      status: 'all',
      from: '2026-10-01',
    })
    await user.click(screen.getByRole('button', { name: 'Courses', exact: true }))
    await user.click(screen.getByRole('button', { name: 'Deadlines', exact: true }))
    expect(screen.getByLabelText('Show')).toHaveValue('all')
    expect(screen.getByLabelText('From')).toHaveValue('2026-10-01')
  })

  it('rejects a reversed custom range before sending it', async () => {
    const user = await openApp()
    await user.click(screen.getByRole('button', { name: 'Deadlines', exact: true }))
    await user.selectOptions(screen.getByLabelText('Window'), 'custom')
    await user.type(screen.getByLabelText('From'), '2026-10-10')
    await user.type(screen.getByLabelText('To'), '2026-10-01')
    await user.click(screen.getByRole('button', { name: 'Apply filters' }))
    expect(await screen.findByRole('alert')).toHaveTextContent(
      'Start date must be on or before end date',
    )
    expect(fetch.mock.calls.some(([url]) => url.includes('from=2026-10-10'))).toBe(false)
  })
})

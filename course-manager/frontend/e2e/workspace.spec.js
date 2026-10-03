import { test, expect } from '@playwright/test'
import { dateLabel } from '../src/dates.js'

const backend = 'http://127.0.0.1:18080'

async function post(request, path, data) {
  const response = await request.post(`${backend}${path}`, { data })
  expect(response.ok()).toBeTruthy()
  return response.json()
}

async function addCourse(page, code = 'CMPT 307', name = 'Algorithms') {
  await page.getByRole('button', { name: 'Add course', exact: true }).click()
  const dialog = page.getByRole('dialog', { name: 'Add course' })
  await dialog.getByLabel('Course code').fill(code)
  await dialog.getByLabel('Course name').fill(name)
  await dialog.getByRole('button', { name: 'Add course' }).click()
  await expect(dialog).not.toBeVisible()
  await expect(page.getByRole('heading', { name, exact: true })).toBeVisible()
}

function offsetDate(today, days) {
  const date = new Date(`${today}T12:00:00Z`)
  date.setUTCDate(date.getUTCDate() + days)
  return date.toISOString().slice(0, 10)
}

test.beforeEach(async ({ request }) => {
  for (const path of ['/courses', '/students']) {
    const response = await request.get(`${backend}${path}`)
    for (const record of await response.json()) {
      expect((await request.delete(`${backend}${path}/${record.id}`)).ok()).toBeTruthy()
    }
  }
})

test('manage courses, students, enrollments and assignment completion', async ({
  page,
  request,
}) => {
  const errors = []
  page.on('pageerror', (error) => errors.push(error.message))
  await page.goto('/')
  await addCourse(page)
  await page.getByRole('button', { name: 'Edit course', exact: true }).click()
  let dialog = page.getByRole('dialog', { name: 'Edit course' })
  await dialog.getByLabel('Course name').fill('Design of Algorithms')
  await dialog.getByRole('button', { name: 'Save changes' }).click()
  await expect(page.getByRole('heading', { name: 'Design of Algorithms' })).toBeVisible()

  await page.getByRole('button', { name: 'Students', exact: true }).click()
  await page.getByRole('button', { name: 'Add student', exact: true }).click()
  dialog = page.getByRole('dialog', { name: 'Add student' })
  await dialog.getByLabel('Name', { exact: true }).fill('Alex Kim')
  await dialog.getByLabel('Email').fill('alex@example.com')
  await dialog.getByRole('button', { name: 'Add student' }).click()
  await expect(dialog).not.toBeVisible()
  await page.getByRole('button', { name: 'Edit student' }).click()
  dialog = page.getByRole('dialog', { name: 'Edit student' })
  await dialog.getByLabel('Name', { exact: true }).fill('Alex Lee')
  await dialog.getByRole('button', { name: 'Save changes' }).click()
  await expect(page.getByRole('heading', { name: 'Alex Lee' })).toBeVisible()

  await page.getByRole('button', { name: 'Courses', exact: true }).click()
  await page.getByRole('button', { name: 'Add enrollment' }).click()
  dialog = page.getByRole('dialog', { name: 'Add enrollment' })
  await dialog
    .getByRole('combobox', { name: 'Student', exact: true })
    .selectOption({ label: 'Alex Lee · alex@example.com' })
  await dialog.getByRole('button', { name: 'Add enrollment' }).click()
  await expect(dialog).not.toBeVisible()
  await expect(page.getByText('Alex Lee', { exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Edit enrollment' }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Save changes' }).click()
  await expect(page.getByRole('dialog')).not.toBeVisible()

  await page.getByRole('button', { name: 'Add assignment', exact: true }).click()
  dialog = page.getByRole('dialog', { name: 'Add assignment' })
  await dialog.getByLabel('Title', { exact: true }).fill('Problem set 1')
  await dialog.getByLabel('Notes').fill('Graphs and shortest paths')
  await dialog.getByRole('button', { name: 'Add assignment' }).click()
  await expect(dialog).not.toBeVisible()
  let row = page.getByRole('article', { name: 'Problem set 1', exact: true })
  await expect(row.getByRole('checkbox')).not.toBeChecked()
  await row.getByRole('button', { name: 'Edit', exact: true }).click()
  dialog = page.getByRole('dialog', { name: 'Edit assignment' })
  await dialog.getByLabel('Title', { exact: true }).fill('Problem set 2')
  await dialog.getByRole('button', { name: 'Save changes' }).click()
  row = page.getByRole('article', { name: 'Problem set 2', exact: true })
  await row.getByRole('checkbox').check()
  await expect(row.getByRole('checkbox')).toBeChecked()
  await page.reload()
  row = page.getByRole('article', { name: 'Problem set 2', exact: true })
  await expect(row.getByRole('checkbox')).toBeChecked()
  await row.getByRole('checkbox').uncheck()
  await expect(row.getByRole('checkbox')).not.toBeChecked()

  await row.getByRole('button', { name: 'Delete', exact: true }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Delete assignment' }).click()
  await expect(row).not.toBeVisible()
  await page.getByRole('button', { name: 'Remove', exact: true }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Delete enrollment' }).click()
  await expect(page.getByText('No students enrolled yet.')).toBeVisible()
  await page.getByRole('button', { name: 'Delete course', exact: true }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Delete course' }).click()
  await expect(page.getByRole('heading', { name: 'Start with a course' })).toBeVisible()
  await page.getByRole('button', { name: 'Students', exact: true }).click()
  await page.getByRole('button', { name: 'Delete student' }).click()
  await page.getByRole('dialog').getByRole('button', { name: 'Delete student' }).click()
  await expect(page.getByRole('heading', { name: 'No students yet' })).toBeVisible()
  expect(await (await request.get(`${backend}/assignments`)).json()).toEqual([])
  expect(errors).toEqual([])
})

test('filter deadlines, retain preferences and work from the calendar', async ({
  page,
  request,
}) => {
  const { today } = await (await request.get(`${backend}/settings`)).json()
  const tomorrow = offsetDate(today, 1)
  const course = await post(request, '/courses', { courseCode: 'CMPT 307', name: 'Algorithms' })
  for (const [title, day, completed] of [
    ['Overdue reading', -1, false],
    ['Tomorrow task', 1, false],
    ['Later task', 15, false],
    ['Finished task', 1, true],
  ]) {
    await post(request, `/courses/${course.id}/assignments`, {
      title,
      dueDate: offsetDate(today, day),
      completed,
    })
  }
  await page.goto('/')
  await page.getByRole('button', { name: 'Deadlines', exact: true }).click()
  await expect(page.getByRole('article', { name: 'Tomorrow task', exact: true })).toBeVisible()
  await expect(page.getByRole('article', { name: 'Later task', exact: true })).not.toBeVisible()
  await page.getByRole('combobox', { name: 'Window', exact: true }).selectOption('30')
  await page.getByRole('button', { name: 'Apply filters' }).click()
  await expect(page.getByRole('article', { name: 'Later task', exact: true })).toBeVisible()
  await page.getByRole('combobox', { name: 'Show', exact: true }).selectOption('overdue')
  await page.getByRole('button', { name: 'Apply filters' }).click()
  await expect(page.getByRole('article', { name: 'Overdue reading', exact: true })).toBeVisible()
  await expect(page.getByRole('article', { name: 'Tomorrow task', exact: true })).not.toBeVisible()
  await page.getByRole('combobox', { name: 'Show', exact: true }).selectOption('all')
  await page.getByLabel('From', { exact: true }).fill(tomorrow)
  await page.getByLabel('To', { exact: true }).fill(tomorrow)
  await page.getByRole('button', { name: 'Apply filters' }).click()
  await expect(page.getByRole('article')).toHaveCount(2)
  await page.reload()
  await page.getByRole('button', { name: 'Deadlines', exact: true }).click()
  await expect(page.getByLabel('From', { exact: true })).toHaveValue(tomorrow)
  await expect(page.getByRole('article')).toHaveCount(2)
  await page.getByRole('button', { name: 'Calendar', exact: true }).click()
  await page
    .getByRole('button', { name: `${dateLabel(tomorrow)}, 2 assignments`, exact: true })
    .click()
  await expect(page.getByRole('article', { name: 'Tomorrow task', exact: true })).toBeVisible()
  await page.getByRole('button', { name: 'Add for this date' }).click()
  const dialog = page.getByRole('dialog', { name: 'Add assignment' })
  await expect(dialog.getByLabel('Due date')).toHaveValue(tomorrow)
  await dialog
    .getByRole('combobox', { name: 'Course', exact: true })
    .selectOption(String(course.id))
  await dialog.getByLabel('Title', { exact: true }).fill('Calendar task')
  await dialog.getByRole('button', { name: 'Add assignment' }).click()
  await expect(dialog).not.toBeVisible()
  await expect(page.getByRole('article', { name: 'Calendar task', exact: true })).toBeVisible()
  await expect(
    page.getByRole('button', { name: `${dateLabel(tomorrow)}, 3 assignments`, exact: true }),
  ).toBeVisible()
  await page.getByRole('button', { name: 'Next month' }).click()
  await page.getByRole('button', { name: 'Previous month' }).click()
  await expect(
    page.getByRole('button', { name: `${dateLabel(tomorrow)}, 3 assignments`, exact: true }),
  ).toBeVisible()
  expect(
    await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth),
  ).toBeTruthy()
  await page.screenshot({ path: test.info().outputPath('calendar.png'), fullPage: true })
})

test('duplicate records keep the form open and keyboard dismissal works', async ({ page }) => {
  await page.goto('/')
  await addCourse(page)
  await page.getByRole('button', { name: 'Add course', exact: true }).click()
  const dialog = page.getByRole('dialog', { name: 'Add course' })
  await dialog.getByLabel('Course code').fill('CMPT 307')
  await dialog.getByLabel('Course name').fill('Duplicate')
  await dialog.getByRole('button', { name: 'Add course' }).click()
  await expect(dialog.getByRole('alert')).toContainText('already exists')
  await expect(dialog.getByLabel('Course name')).toHaveValue('Duplicate')
  await page.keyboard.press('Escape')
  await expect(dialog).not.toBeVisible()
  await expect(page.getByRole('heading', { name: 'Algorithms', exact: true })).toBeVisible()
  expect(
    await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth),
  ).toBeTruthy()
  await page.screenshot({ path: test.info().outputPath('courses.png'), fullPage: true })
})

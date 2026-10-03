import { useCallback, useEffect, useState } from 'react'
import { api } from './api'

async function load(signal) {
  const [courses, assignments, students, enrollments, settings] = await Promise.all(
    ['/courses', '/assignments', '/students', '/enrollments', '/settings'].map((path) =>
      api(path, { signal }),
    ),
  )
  return { courses, assignments, students, enrollments, settings }
}

export function useWorkspace() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  const [version, setVersion] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    load(controller.signal)
      .then(setData)
      .catch((error) => {
        if (error.name !== 'AbortError') setError(error.message)
      })
    return () => controller.abort()
  }, [])

  const reload = useCallback(async () => {
    try {
      setData(await load())
      setError('')
      setVersion((value) => value + 1)
    } catch (error) {
      setError(error.message)
    }
  }, [])

  return { data, error, reload, version }
}

export function useAssignments(query, version) {
  const key = `${query}:${version}`
  const [result, setResult] = useState({ key: '', data: [], error: '' })
  useEffect(() => {
    const controller = new AbortController()
    api(`/assignments?${query}`, { signal: controller.signal })
      .then((data) => setResult({ key: `${query}:${version}`, data, error: '' }))
      .catch((error) => {
        if (error.name !== 'AbortError')
          setResult({ key: `${query}:${version}`, data: [], error: error.message })
      })
    return () => controller.abort()
  }, [query, version])
  return { ...result, loading: result.key !== key }
}

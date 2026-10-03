export async function api(path, options = {}) {
  const { body, ...rest } = options
  let response
  try {
    response = await fetch(`/api${path}`, {
      ...rest,
      headers: body === undefined ? undefined : { 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch (error) {
    if (error.name === 'AbortError') throw error
    throw new Error('Cannot reach the server. Check that the backend is running.', { cause: error })
  }
  if (!response.ok) {
    const error = await response.json().catch(() => ({}))
    throw new Error(error.message || `Request failed (${response.status}). Please try again.`)
  }
  return response.status === 204 ? null : response.json()
}

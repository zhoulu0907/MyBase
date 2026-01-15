import type { RequestAPI } from '../types'

export function createRequest(): RequestAPI {
  return {
    get: (url: string, init?: RequestInit) => fetch(url, { method: 'GET', ...(init || {}) }),
    post: (url: string, body?: any, init?: RequestInit) => {
      const headers = { 'Content-Type': 'application/json', ...(init?.headers || {}) }
      const payload = body === undefined ? undefined : JSON.stringify(body)
      return fetch(url, { method: 'POST', body: payload, headers, ...(init || {}) })
    }
  }
}

import type { UIAPI } from '../types'

export function createUI(override?: UIAPI): UIAPI {
  const fallback: UIAPI = {
    reportError: (error: unknown) => {
      // eslint-disable-next-line no-console
      console.error('[plugin-error]', error)
    }
  }
  return override ?? fallback
}

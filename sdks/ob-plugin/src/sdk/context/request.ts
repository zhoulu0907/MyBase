import type { RequestAPI } from '../types'

export function enrichRequest(requestFn: (config: any) => Promise<any>): RequestAPI {
  return {
    get: (url: string, config?: any) => requestFn({ ...config, url, method: 'GET' }),
    post: (url: string, data?: any, config?: any) => requestFn({ ...config, url, method: 'POST', data }),
    put: (url: string, data?: any, config?: any) => requestFn({ ...config, url, method: 'PUT', data }),
    delete: (url: string, config?: any) => requestFn({ ...config, url, method: 'DELETE' }),
    request: requestFn
  }
}

export function createRequest(): RequestAPI {
  const request = async (config: any) => {
    const { url, method = 'GET', data, headers, ...rest } = config
    const isFormData = data instanceof FormData
    const isBlob = data instanceof Blob || (typeof File !== 'undefined' && data instanceof File)
    const isBuffer = data instanceof ArrayBuffer
    
    // 只有普通对象/数组才需要 JSON 序列化和 application/json 头
    const shouldJsonify = !isFormData && !isBlob && !isBuffer && typeof data === 'object' && data !== null
    
    const reqHeaders = {
      ...(shouldJsonify && { 'Content-Type': 'application/json' }),
      ...(headers || {})
    }
    
    const payload = shouldJsonify ? JSON.stringify(data) : data
    
    const response = await fetch(url, {
      method,
      body: payload,
      headers: reqHeaders,
      ...rest
    })
    
    const resData = await response.json().catch(() => ({}))
    return { ...resData, status: response.status, statusText: response.statusText }
  }

  return enrichRequest(request)
}

import React from 'react'
import ReactDOM from 'react-dom/client'
import * as ReactRouterDOM from 'react-router-dom'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Layout, Menu, Button } from '@arco-design/web-react'
import * as Arco from '@arco-design/web-react'
import '@arco-design/web-react/dist/css/arco.css'
import plugin from '../index'
import { HomePage } from '../pages/HomePage'
import type { HostSDK } from '@ob/plugin/sdk'
import { PluginManager } from '@ob/plugin/host'

;(window as any).React = React
;(window as any).ReactDOM = ReactDOM
;(window as any).Arco = Arco
;(window as any).ReactRouterDOM = ReactRouterDOM

const mockSDK: HostSDK & {
  context: HostSDK['context'] & {
    router: { push: (path: string) => void; getCurrentPath: () => string }
    storage: { set: (k: string, v: any) => void; get: (k: string) => any; remove: (k: string) => void }
    events: { on: (ev: string, h: (...args: any[]) => void) => void; emit: (ev: string, ...args: any[]) => void }
  }
  ui: HostSDK['ui'] & { notify: (type: 'success'|'error'|'info', message: string) => void }
} = {
  context: {
    terminal: 'PC',
    router: {
      push: (path) => window.history.pushState({}, '', path),
      getCurrentPath: () => window.location.pathname
    },
    storage: {
      set: (key, value) => localStorage.setItem(key, JSON.stringify(value)),
      get: (key) => {
        const val = localStorage.getItem(key)
        return val ? JSON.parse(val) : null
      },
      remove: (key) => localStorage.removeItem(key)
    },
    events: {
      on: (eventName, handler) => {
        window.addEventListener(`lowcode:${eventName}`, (e: any) => handler(e.detail))
      },
      emit: (eventName, ...args) => {
        window.dispatchEvent(new CustomEvent(`lowcode:${eventName}`, { detail: args }))
      }
    }
  },
  ui: {
    reportError: (error: unknown) => {
      console.error('[plugin-error]', error)
    },
    notify: (type, message) => {
      const el = document.createElement('div')
      el.style.position = 'fixed'
      el.style.top = '20px'
      el.style.right = '20px'
      el.style.padding = '10px 20px'
      el.style.borderRadius = '4px'
      el.style.color = 'white'
      el.style.zIndex = '9999'
      el.style.backgroundColor = type === 'success' ? '#52c41a' : type === 'error' ? '#ff4d4f' : '#1890ff'
      el.innerText = message
      document.body.appendChild(el)
      setTimeout(() => el.remove(), 3000)
    }
  }
}

const DevApp: React.FC = () => {
  const [loaded, setLoaded] = React.useState<any | null>(null)
  const managerRef = React.useRef<PluginManager | null>(null)

  React.useEffect(() => {
    const isDev = (import.meta as any)?.env?.DEV
    if (isDev) {
      setLoaded(plugin as any)
      return
    }
    ;(window as any)['ob-plugin-template'] = plugin
    const pm = new PluginManager({ terminal: 'PC' })
    managerRef.current = pm
    pm.registerPlugin({
      name: 'ob-plugin-template',
      version: '0.0.0',
      displayName: '示例插件',
      routePrefix: '/ob-plugin-template',
      resources: { js: '/ob-plugin-template.umd.js', css: '/ob-plugin-template.css' }
    })
    pm.loadPlugin('ob-plugin-template')
      .then((p) => { setLoaded(p) })
      .catch(() => { setLoaded(plugin as any) })
    return () => { pm.unloadPlugin('ob-plugin-template') }
  }, [])

  const handleNavigate = (path: string) => window.history.pushState({}, '', path)

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Layout.Sider width={200}>
        <div style={{ padding: '16px', background: '#f5f5f5', textAlign: 'center' }}>插件独立预览</div>
        <Menu mode="vertical" selectedKeys={[window.location.pathname]}>
          <Menu.Item key="/" onClick={() => handleNavigate('/')}>插件首页</Menu.Item>
          <Menu.Item key="/about" onClick={() => handleNavigate('/about')}>关于页面</Menu.Item>
        </Menu>
      </Layout.Sider>
      <Layout.Content style={{ padding: '20px', overflow: 'auto' }}>
        <div style={{ marginBottom: '20px' }}>
          <Button onClick={() => handleNavigate('/')} style={{ marginRight: 8 }}>首页</Button>
          <Button onClick={() => handleNavigate('/about')}>关于</Button>
        </div>
        {loaded ? (
          <Routes>
            <Route path="/" element={<HomePage sdk={mockSDK as any} />} />
            <Route path="/about" element={loaded.pages.about.component({ sdk: mockSDK }) as any} />
          </Routes>
        ) : null}
      </Layout.Content>
    </Layout>
  )
}

ReactDOM.createRoot(document.getElementById('app')!).render(
  <BrowserRouter>
    <DevApp />
  </BrowserRouter>
)

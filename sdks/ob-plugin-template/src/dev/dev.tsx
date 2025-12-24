import React, { useEffect, useState, useMemo } from 'react'
import ReactDOM from 'react-dom/client'
import * as ReactRouterDOM from 'react-router-dom'
import { BrowserRouter, Routes, Route, useNavigate, useLocation, useParams } from 'react-router-dom'
import { Layout, Menu, Button, Typography, Empty, Breadcrumb } from '@arco-design/web-react'
import * as Arco from '@arco-design/web-react'
import '@arco-design/web-react/dist/css/arco.css'
import plugin from '../index'
import type { HostSDK, LoadedPlugin } from '@ob/plugin/sdk'
import { PluginManager } from '@ob/plugin/host'
import { createMockHostSDK } from '@ob/plugin/mock'
import { ComponentDebugger } from './ComponentDebugger'
import { MOCK_ENTITIES, MOCK_FIELDS } from './mock/config'

;(window as any).React = React
;(window as any).ReactDOM = ReactDOM
;(window as any).Arco = Arco
;(window as any).ReactRouterDOM = ReactRouterDOM

// Initial mock SDK
const mockContext = {
  terminal: 'PC' as const,
  router: {
    push: (path: string) => window.history.pushState({}, '', path),
    getCurrentPath: () => window.location.pathname
  },
  storage: {
    set: (key: string, value: any) => localStorage.setItem(key, JSON.stringify(value)),
    get: (key: string) => {
      const val = localStorage.getItem(key)
      return val ? JSON.parse(val) : null
    },
    remove: (key: string) => localStorage.removeItem(key)
  },
  events: {
    on: (eventName: string, handler: any) => {
      window.addEventListener(`lowcode:${eventName}`, (e: any) => handler(e.detail))
    },
    emit: (eventName: string, ...args: any[]) => {
      window.dispatchEvent(new CustomEvent(`lowcode:${eventName}`, { detail: args }))
    }
  },
  form: {
    // Simple mock for form state (stored in window for persistence during component re-renders in dev)
    getValue: (field: string) => {
      const val = (window as any).__MOCK_FORM_STATE?.[field]
      console.log(`[MockSDK] get form value: ${field} = ${val}`)
      return val
    },
    setValue: (field: string, value: any) => {
      console.log(`[MockSDK] set form value: ${field} = ${value}`)
      ;(window as any).__MOCK_FORM_STATE = (window as any).__MOCK_FORM_STATE || {}
      ;(window as any).__MOCK_FORM_STATE[field] = value
      // Dispatch event for components to listen (if implemented)
      window.dispatchEvent(new CustomEvent('mock:form:change', { detail: { field, value } }))
    },
    listFields: () => {
      const fields = (window as any).__MOCK_FORM_FIELDS
      return Array.isArray(fields) ? fields : ['docType', 'userName', 'userId']
    }
  }
}

const mockSDK = createMockHostSDK(mockContext as any, {
  entities: MOCK_ENTITIES,
  fields: MOCK_FIELDS,
  ui: {
    reportError: (error: unknown) => {
      console.error('[plugin-error]', error)
    },
    notify: (type: any, message: string) => {
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
  } as any
}) as any

const PageRenderer = ({ plugin }: { plugin: LoadedPlugin }) => {
  const { pageKey } = useParams()
  const page = plugin.pages[pageKey || '']
  
  if (!page) return <Empty description={`Page ${pageKey} not found`} />
  
  const Component = page.component
  return (
    <div style={{ padding: 20 }}>
      <Typography.Title heading={5}>{page.title}</Typography.Title>
      <div style={{ border: '1px solid #eee', padding: 20, background: '#fff' }}>
        <Component sdk={mockSDK} />
      </div>
    </div>
  )
}

const ComponentRenderer = ({ plugin }: { plugin: LoadedPlugin }) => {
  const { componentKey } = useParams()
  const component = plugin.components[componentKey || '']
  
  if (!component) return <Empty description={`Component ${componentKey} not found`} />
  
  return <ComponentDebugger componentKey={componentKey!} component={component} plugin={plugin} sdk={mockSDK} />
}

const AppContent: React.FC = () => {
  const [loaded, setLoaded] = useState<LoadedPlugin | null>(null)
  const navigate = useNavigate()
  const location = useLocation()
  
  // Sync router
  useEffect(() => {
    mockSDK.context.router.push = (path: string) => navigate(path)
  }, [navigate])

  useEffect(() => {
    const isDev = (import.meta as any)?.env?.DEV
    if (isDev) {
      setLoaded(plugin as LoadedPlugin)
      return
    }
    
    // Simulate loading in production/preview
    ;(window as any)['ob-plugin-template'] = plugin
    const pm = new PluginManager(mockSDK.context)
    pm.registerPlugin({
      name: 'ob-plugin-template',
      version: '0.0.0',
      displayName: '示例插件',
      routePrefix: '/ob-plugin-template',
      resources: { js: '/ob-plugin-template.umd.js', css: '/ob-plugin-template.css' }
    })
    pm.loadPlugin('ob-plugin-template')
      .then((p) => { setLoaded(p) })
      .catch((e) => { 
        console.error(e)
        setLoaded(plugin as LoadedPlugin) 
      })
  }, [])

  const menuItems = useMemo(() => {
    if (!loaded) return []
    
    return [
      {
        key: 'pages',
        title: '页面 (Pages)',
        children: Object.entries(loaded.pages || {}).map(([key, page]: [string, any]) => ({
          key: `/pages/${key}`,
          title: page.title || key
        }))
      },
      {
        key: 'components',
        title: '组件 (Components)',
        children: Object.entries(loaded.components || {}).map(([key, comp]: [string, any]) => ({
          key: `/components/${key}`,
          title: comp.template?.displayName || key
        }))
      }
    ]
  }, [loaded])

  if (!loaded) return <div>Loading plugin...</div>

  return (
    <Layout style={{ height: '100vh', overflow: 'hidden' }}>
      <Layout.Sider width={240} style={{ background: '#fff', borderRight: '1px solid #eee' }}>
        <div style={{ padding: '16px', background: '#f7f8fa', textAlign: 'center', fontWeight: 'bold' }}>
          {loaded.meta.displayName || loaded.meta.name}
          <div style={{ fontSize: 12, color: '#999', fontWeight: 'normal' }}>Dev Environment</div>
        </div>
        <Menu 
          mode="vertical" 
          selectedKeys={[location.pathname]}
          defaultOpenKeys={['pages', 'components']}
          onClickMenuItem={(key) => navigate(key)}
          style={{ height: 'calc(100% - 54px)', overflow: 'auto' }}
        >
          <Menu.Item key="/">Dashboard</Menu.Item>
          
          <Menu.SubMenu key="pages" title="Pages">
            {menuItems.find(i => i.key === 'pages')?.children?.map(item => (
              <Menu.Item key={item.key}>{item.title}</Menu.Item>
            ))}
          </Menu.SubMenu>
          
          <Menu.SubMenu key="components" title="Components">
            {menuItems.find(i => i.key === 'components')?.children?.map(item => (
              <Menu.Item key={item.key}>{item.title}</Menu.Item>
            ))}
          </Menu.SubMenu>
        </Menu>
      </Layout.Sider>
      
      <Layout.Content style={{ height: '100%', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '12px 20px', background: '#fff', borderBottom: '1px solid #eee' }}>
           <Breadcrumb>
             <Breadcrumb.Item>Dev</Breadcrumb.Item>
             <Breadcrumb.Item>{location.pathname === '/' ? 'Dashboard' : location.pathname.split('/')[1]}</Breadcrumb.Item>
             <Breadcrumb.Item>{location.pathname.split('/')[2]}</Breadcrumb.Item>
           </Breadcrumb>
        </div>
        
        <div style={{ flex: 1, overflow: 'hidden' }}>
          <Routes>
            <Route path="/" element={
              <div style={{ padding: 40, textAlign: 'center' }}>
                <Typography.Title heading={3}>Welcome to Plugin Dev</Typography.Title>
                <Typography.Text>Select a page or component from the sidebar to start debugging.</Typography.Text>
              </div>
            } />
            <Route path="/pages/:pageKey" element={<PageRenderer plugin={loaded} />} />
            <Route path="/components/:componentKey" element={<ComponentRenderer plugin={loaded} />} />
          </Routes>
        </div>
      </Layout.Content>
    </Layout>
  )
}

ReactDOM.createRoot(document.getElementById('app')!).render(
  <BrowserRouter>
    <AppContent />
  </BrowserRouter>
)

import { HomePage } from './pages/HomePage'
import PluginDemoComponent from './components/DemoComponent'
import PluginInputText from './components/InputText'
import { InputTextSchema } from './components/InputText/schema'
import { pluginMethods } from './methods'
import { type LoadedPlugin } from '@ob/plugin/sdk'

const plugin: LoadedPlugin = {
  meta: {
    name: 'ob-plugin-template',
    version: '0.0.0',
    displayName: '示例插件',
    routePrefix: '/ob-plugin-template'
  },
  pages: {
    home: {
      path: '/',
      title: '插件首页',
      component: ({ sdk }: any) => HomePage({ sdk })
    },
    about: {
      path: '/about',
      title: '关于',
      component: ({ sdk }: any) => (
        <div style={{ padding: 20 }}>
          <h2>关于插件</h2>
          <p>这是一个示例插件，版本0.0.0</p>
          <button onClick={() => sdk.context?.router.push('/')}>返回平台首页</button>
        </div>
      )
    }
  },
  components: {
    PluginDemoComponent: {
      name: 'PluginDemoComponent',
      component: (props: any, sdk: any) => <PluginDemoComponent {...props} sdk={sdk} />
    },
    PluginInputText: {
      type: 'PluginInputText',
      ...InputTextSchema,
      component: PluginInputText
    }
  },
  methods: pluginMethods,
  initialize: async (sdk: any) => {
    const notify = (sdk?.ui as any)?.notify ?? (sdk as any)?.notify
    if (typeof notify === 'function') {
      notify('info', '示例插件已初始化')
    } else {
      console.log('[ob-plugin-template] initialized')
    }
  },
  destroy: async () => {}
}

export default plugin

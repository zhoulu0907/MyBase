import { HomePage } from './pages/HomePage'
import DemoComponent from './components/DemoComponent'
import InputText from './components/InputText'
import { pluginMethods } from './methods'
import type { LoadedPlugin } from '@ob/plugin/sdk'

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
    DemoComponent: {
      name: 'DemoComponent',
      component: (props: any, sdk: any) => <DemoComponent {...props} sdk={sdk} />
    },
    XInputText: {
      type: 'XInputText',
      schema: {
        // 对齐 UI-Kit 的可配置项列表（以字符串标识，宿主可映射到实际配置组件）
        editData: [
          'label',
          'placeholder',
          'tooltip',
          'dataField',
          'defaultValue',
          'verify',
          'status',
          'align',
          'layout',
          'security',
          'width',
          'common'
        ],
        // 完整的默认配置，字段名与 UI-Kit 保持一致
        config: {
          label: { text: '单行文本', display: true },
          placeholder: '请输入',
          tooltip: '',
          dataField: [],
          defaultValueConfig: { type: 'CUSTOM', customValue: '', formulaValue: '' },
          verify: {
            required: false,
            noRepeat: false,
            lengthLimit: false,
            minLength: 0,
            maxLength: 0
          },
          status: 'default',
          align: 'left',
          layout: 'vertical',
          security: { display: false, type: 'none' },
          width: 'full'
        }
      },
      template: { h: 36, w: 118, displayName: '单行文本', icon: 'text_input_cp.svg', category: 'form' },
      fieldMap: ['text', 'longText', 'id'],
      entityMap: ['text', 'url', 'address', 'geography', 'password', 'encrypted', 'aggregate', 'id'],
      component: (props: any, sdk: any) => <InputText {...props} sdk={sdk} />
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

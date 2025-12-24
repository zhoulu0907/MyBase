import { HomePage } from './pages/HomePage'
import PluginInputText from './components/InputText'
import PluginOCR from './components/PluginOCR'
import OCRBindingSetter from './components/Setters/OCRBindingSetter'
import ColorPickerSetter from './components/Setters/ColorPickerSetter'
import { InputTextSchema } from './components/InputText/schema'
import { PluginOCRSchema } from './components/PluginOCR/schema'
import { pluginMethods } from './methods'
import { createPlugin } from '@ob/plugin/sdk'
import { PLUGIN_NAME } from './constants'

const plugin = createPlugin({
  name: PLUGIN_NAME,
  version: '0.0.1',
  displayName: '示例插件',
  routePrefix: '/' + PLUGIN_NAME
})

// 注册页面
plugin.registerPages({
  home: {
    path: '/',
    title: '插件首页',
    component: ({ sdk }: any) => HomePage({ sdk })
  }
})

// 注册组件
plugin.registerComponents({
  PluginInputText: {
    type: 'PluginInputText',
    ...InputTextSchema,
    component: PluginInputText
  },
  PluginOCR: {
    type: 'PluginOCR',
    ...PluginOCRSchema,
    component: PluginOCR
  }
})

// 注册配置渲染器
plugin.registerConfigRenderers({
  ColorPickerSetter: {
    type: 'ColorPickerSetter',
    component: ColorPickerSetter
  },
  OCRBindingSetter: {
    type: 'OCRBindingSetter',
    component: OCRBindingSetter
  }
})

// 注册方法
plugin.registerMethods(pluginMethods)

// 生命周期
plugin.onInitialize(async (sdk: any) => {
  const notify = (sdk?.ui as any)?.notify ?? (sdk as any)?.notify
  if (typeof notify === 'function') {
    notify('info', '示例插件已初始化')
  } else {
    console.log('[ob-plugin-template] initialized')
  }
})

plugin.onDestroy(async () => {})

export default plugin.build()

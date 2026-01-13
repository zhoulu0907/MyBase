import { HomePage } from '../pages/HomePage'
import PluginInputText from '../components/InputText'
import { InputTextSchema } from '../components/InputText/schema'
import PluginOCR from '../components/PluginOCR'
import { PluginOCRSchema } from '../components/PluginOCR/schema'
import OCRSettingsSetter from '../components/Setters/OCRSettingsSetter'
import ColorPickerSetter from '../components/Setters/ColorPickerSetter'
import { pluginMethods } from '../methods'
import { createPlugin } from '@ob/plugin/sdk'
import { PLUGIN_MANIFEST } from '../constants'

const plugin = createPlugin(PLUGIN_MANIFEST)

plugin.registerPages({
  home: { path: '/', title: '插件首页', component: ({ sdk }: any) => HomePage({ sdk }) }
})

const componentsToRegister: any = {
  PluginOCR: { type: 'PluginOCR', ...PluginOCRSchema, component: PluginOCR }
}

if (import.meta.env.DEV) {
  componentsToRegister.PluginInputText = { type: 'PluginInputText', ...InputTextSchema, component: PluginInputText }
}

plugin.registerComponents(componentsToRegister)

plugin.registerConfigRenderers({
  ColorPickerSetter: { type: 'ColorPickerSetter', component: ColorPickerSetter },
  OCRSettingsSetter: { type: 'OCRSettingsSetter', component: OCRSettingsSetter }
})

plugin.registerMethods(pluginMethods)

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

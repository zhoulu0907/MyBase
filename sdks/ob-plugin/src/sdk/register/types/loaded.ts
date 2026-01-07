import type { PluginMeta } from './plugin'
import type { PluginPage } from './pages'
import type { PluginComponent } from './components'
import type { PluginMethod, PluginMethodMeta } from './methods'

export interface LoadedPlugin {
  meta: PluginMeta
  pages: Record<string, PluginPage>
  components: Record<string, PluginComponent>
  configRenderers?: Record<string, PluginComponent>
  methods: Record<string, PluginMethod>
  methodsMeta?: Record<string, PluginMethodMeta>
  initialize?: (sdk: any) => Promise<void>
  destroy?: () => Promise<void>
}

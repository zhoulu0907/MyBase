/** 宿主上下文与 SDK（供插件调用）；保持最小依赖与向后兼容 */
export type Terminal = 'PC' | 'MOBILE';
export interface Context { terminal: Terminal }
export interface ErrorReportOptions { scope?: string }
export interface UIAPI { reportError(error: unknown, options?: ErrorReportOptions): void }
export interface HostSDK { context: Context; ui: UIAPI }

/** 删除了旧版注册描述与帮助函数，统一采用下方插件规范 */

/** 插件元数据规范 */
export interface PluginMeta {
  name: string;           // 唯一标识
  version: string;        // 插件版本
  displayName: string;    // 展示名
  description?: string;   // 描述
  routePrefix: string;    // 路由前缀（避免冲突）
  resources?: {           // 远程资源（UMD/CSS）
    js: string;           // 插件 JS 资源地址
    css?: string;         // 插件 CSS 资源地址
  };
}

/** 插件提供的页面 */
export interface PluginPage {
  path: string;                              // 相对路径（基于 routePrefix）
  component: (props: { sdk: any }) => any;   // 页面组件（返回任意渲染结果）
  title: string;                             // 导航标题
}

/** 插件提供的组件 */
/** 与物料面板一致的组件模板元信息 */
export type ComponentCategory = 'custom'
export interface ComponentTemplate {
  h: number
  w: number
  displayName: string
  icon: string
  category: ComponentCategory
}

/** 插件提供的组件（对齐物料注册所需字段） */
export interface PluginComponent {
  /** 旧字段：组件名称（兼容保留，可选） */
  name?: string
  /** 注册所需：组件类型字符串（如 'XInputText'） */
  type?: string
  /** 注册所需：组件 schema（结构定义） */
  schema?: any
  /** 注册所需：物料面板模板信息 */
  template?: ComponentTemplate
  /** 可选：字段能力映射 */
  fieldMap?: string[]
  /** 可选：默认实体字段映射 */
  entityMap?: string[]
  /** 运行期实现：React 组件（可复用组件） */
  component?: (props: any, sdk: any) => any
}

/** 插件提供的方法 */
export type PluginMethod = (...args: any[]) => any | Promise<any>;

/** 插件完整结构（加载后解析） */
export interface LoadedPlugin {
  meta: PluginMeta;
  pages: Record<string, PluginPage>;
  components: Record<string, PluginComponent>;
  methods: Record<string, PluginMethod>;
  // 生命周期钩子
  initialize?: (sdk: any) => Promise<void>;
  destroy?: () => Promise<void>;
}

/** 插件注册信息（主工程维护） */
export interface PluginRegistry {
  meta: PluginMeta;
  status: 'registered' | 'loading' | 'loaded' | 'error';
}

export interface HostEvents {
  'plugin:registered': (payload: { meta: PluginMeta }) => void;
  'plugin:loaded': (payload: { id: string }) => void;
  'plugin:unloaded': (payload: { id: string }) => void;
  'plugin:invalidated': (payload: { id: string }) => void;
  'plugin:error': (payload: { id: string; error: unknown }) => void;
}

export type PluginStatus = 'registered' | 'loading' | 'loaded' | 'error' | 'unloaded' | 'invalidated';

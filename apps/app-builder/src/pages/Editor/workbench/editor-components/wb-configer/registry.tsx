export interface RenderContext {
  id: string;
  item: any;
  index: number;
  configs: any;
  isInSubTable: boolean;
  handlePropsChange: (key: string, value: any) => void;
  handleConfigsChange: (config: any) => void;
  handleMultiPropsChange: (updates: { key: string; value: any }[]) => void;
  handleLayoutChange: (key: string, value: string) => void;
}

type Renderer = (ctx: RenderContext) => JSX.Element | null;

function getRegistry(): Record<string | number, Renderer> {
  const g = globalThis as any;
  if (!g.__OB_ATTR_REG__) {
    g.__OB_ATTR_REG__ = {};
  }
  return g.__OB_ATTR_REG__ as Record<string | number, Renderer>;
}

export function registerConfigRenderer(type: string | number, renderer: Renderer) {
  const reg = getRegistry();
  reg[type] = renderer;
}

export function renderConfigItem(ctx: RenderContext) {
  const reg = getRegistry();
  const renderer = reg[ctx.item?.type as any];
  return renderer ? renderer(ctx) : null;
}

// 自动加载所有配置组件文件，执行文件末尾的 registerConfigRenderer 调用
const modules = import.meta.glob('./components/**/index.tsx', { eager: true });

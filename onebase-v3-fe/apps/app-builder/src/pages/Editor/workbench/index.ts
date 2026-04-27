/**
 * 工作台模块统一导出
 */

// 组件导出
export { default as WorkbenchPanel } from './editor-components/wb-panel';
export { default as WorkbenchConfiger } from './editor-components/wb-configer';
export { default as WorkbenchWorkspace } from './editor-components/wb-workspace';

// 类型导出
export type * from './types/workbench';
export type * from './types/workbench-component';

// Hook 导出
export { useWorkbenchItems } from './hooks/use-workbench-items';
export { useWorkbenchContainer } from './hooks/use-workbench-container';
export { useWorkbenchHandlers } from './hooks/use-workbench-handlers';
export { useWorkbenchResize } from './hooks/use-workbench-resize';

// 工具函数导出
export * from './utils/template';

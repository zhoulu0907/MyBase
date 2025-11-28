/**
 * ER 图边(Edge)样式常量
 */

// 边的正常状态样式
export const EDGE_NORMAL_STYLE = {
  stroke: 'rgb(var(--gray-5))',
  strokeWidth: 1
} as const;

// 边的选中状态样式
export const EDGE_SELECTED_STYLE = {
  stroke: 'rgba(var(--primary-7), 0.8)',
  strokeWidth: 3
} as const;

// 边的箭头配置
export const EDGE_TARGET_MARKER = {
  name: 'block',
  width: 12,
  height: 8
} as const;

// 边的动画过渡配置
export const EDGE_TRANSITION = {
  duration: 300,
  timing: 'ease-in-out'
} as const;

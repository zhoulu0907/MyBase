import type { ReactNode } from 'react';

/**
 * ModalPopover 组件的属性接口
 */
export interface ModalPopoverProps {
  /** 弹出层宽度 */
  width?: string | number;
  /** 触发元素 */
  children: React.ReactElement;
  /** 弹出层内容 */
  content: ReactNode;
  /** 是否可见 */
  visible?: boolean;
  /** 可见性变化回调 */
  onVisibleChange?: (visible: boolean) => void;
  /** 弹出层位置 */
  placement?: 'top' | 'bottom' | 'left' | 'right' | 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight' | 'auto';
  /** 是否阻止事件冒泡 */
  stopPropagation?: boolean;
}

/**
 * 位置信息接口
 */
export interface Position {
  top: number;
  left: number;
}

/**
 * 箭头位置信息接口
 */
export interface ArrowPosition {
  top: number;
  left: number;
}

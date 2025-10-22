import type { ReactNode } from 'react';

/**
 * 自定义悬浮层组件的属性接口
 */
export interface CustomPopoverProps {
  /** 触发元素 */
  children: React.ReactElement;
  /** 弹出层内容 */
  content: ReactNode;
  /** 是否可见 */
  visible?: boolean;
  /** 可见性变化回调 */
  onVisibleChange?: (visible: boolean) => void;
  /** 触发方式 */
  trigger?: 'click' | 'hover' | 'focus';
  /** 弹出层位置 */
  placement?: 'top' | 'bottom' | 'left' | 'right' | 'topLeft' | 'topRight' | 'bottomLeft' | 'bottomRight';
  /** 弹出层容器 */
  getPopupContainer?: () => HTMLElement;
  /** 弹出层类名 */
  popupClassName?: string;
  /** 弹出层样式 */
  popupStyle?: React.CSSProperties;
  /** 是否显示箭头 */
  showArrow?: boolean;
  /** 偏移量 */
  offset?: number;
  /** 是否禁用 */
  disabled?: boolean;
  /** 自动调整位置 */
  autoAdjust?: boolean;
  /** 延迟显示时间（毫秒） */
  delay?: number;
  /** 延迟隐藏时间（毫秒） */
  hideDelay?: number;
  /** 是否在Modal中 */
  inModal?: boolean;
  /** 是否阻止事件冒泡 */
  stopPropagation?: boolean;
  /** 强制置顶，使用最高z-index */
  forceTop?: boolean;
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

/**
 * 弹出层配置接口
 */
export interface PopoverConfig {
  /** 默认位置 */
  defaultPlacement: CustomPopoverProps['placement'];
  /** 默认偏移量 */
  defaultOffset: number;
  /** 是否默认显示箭头 */
  defaultShowArrow: boolean;
  /** 是否默认自动调整位置 */
  defaultAutoAdjust: boolean;
}

/**
 * 弹出层状态接口
 */
export interface PopoverState {
  /** 是否可见 */
  visible: boolean;
  /** 位置信息 */
  position: Position;
  /** 箭头位置信息 */
  arrowPosition: ArrowPosition;
  /** 容器元素 */
  container: HTMLElement | null;
}

/**
 * 触发事件类型
 */
export type TriggerEvent = React.MouseEvent | React.FocusEvent;

/**
 * 位置计算选项
 */
export interface PositionCalculationOptions {
  /** 触发元素矩形 */
  triggerRect: DOMRect;
  /** 弹出层矩形 */
  popoverRect: DOMRect;
  /** 容器矩形 */
  containerRect: DOMRect;
  /** 位置 */
  placement: CustomPopoverProps['placement'];
  /** 偏移量 */
  offset: number;
  /** 是否自动调整 */
  autoAdjust: boolean;
}

/**
 * 位置计算结果
 */
export interface PositionCalculationResult {
  /** 位置信息 */
  position: Position;
  /** 箭头位置信息 */
  arrowPosition: ArrowPosition;
}

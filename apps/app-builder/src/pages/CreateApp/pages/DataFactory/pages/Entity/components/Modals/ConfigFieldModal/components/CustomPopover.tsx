import React, { useEffect, useRef, useState, useCallback, useMemo } from 'react';
import type { ReactElement } from 'react';
import { createPortal } from 'react-dom';
import styles from './CustomPopover.module.less';

export interface CustomPopoverProps {
  /** 触发元素 */
  children: React.ReactElement;
  /** 弹出层内容 */
  content: React.ReactNode;
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

const CustomPopover: React.FC<CustomPopoverProps> = ({
  children,
  content,
  visible = false,
  onVisibleChange,
  trigger = 'click',
  placement = 'bottom',
  getPopupContainer,
  popupClassName = '',
  popupStyle = {},
  showArrow = true,
  offset = 8,
  disabled = false,
  autoAdjust = true,
  delay = 0,
  hideDelay = 0,
  inModal = false,
  stopPropagation = false,
  forceTop = false
}) => {
  const triggerRef = useRef<HTMLElement | null>(null);
  const popoverRef = useRef<HTMLDivElement>(null);
  const delayTimerRef = useRef<NodeJS.Timeout | null>(null);
  const hideTimerRef = useRef<NodeJS.Timeout | null>(null);
  const [internalVisible, setInternalVisible] = useState(visible);
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const [arrowPosition, setArrowPosition] = useState({ top: 0, left: 0 });
  const [container, setContainer] = useState<HTMLElement | null>(null);

  const isVisible = visible !== undefined ? visible : internalVisible;

  // 清理定时器
  const clearTimers = useCallback(() => {
    if (delayTimerRef.current) {
      clearTimeout(delayTimerRef.current);
      delayTimerRef.current = null;
    }
    if (hideTimerRef.current) {
      clearTimeout(hideTimerRef.current);
      hideTimerRef.current = null;
    }
  }, []);

  // 设置可见状态
  const setVisibleState = useCallback(
    (newVisible: boolean) => {
      clearTimers();
      if (newVisible) {
        if (delay > 0) {
          delayTimerRef.current = setTimeout(() => {
            setInternalVisible(true);
            onVisibleChange?.(true);
          }, delay);
        } else {
          setInternalVisible(true);
          onVisibleChange?.(true);
        }
      } else {
        if (hideDelay > 0) {
          hideTimerRef.current = setTimeout(() => {
            setInternalVisible(false);
            onVisibleChange?.(false);
          }, hideDelay);
        } else {
          setInternalVisible(false);
          onVisibleChange?.(false);
        }
      }
    },
    [delay, hideDelay, onVisibleChange, clearTimers]
  );

  // 获取容器元素
  useEffect(() => {
    const containerElement = getPopupContainer ? getPopupContainer() : document.body;
    setContainer(containerElement);
  }, [getPopupContainer]);

  // 获取触发元素的DOM节点（处理Tooltip等包装组件）
  const getTriggerElement = useCallback(() => {
    if (!triggerRef.current) return null;

    // 如果是Tooltip组件，尝试找到内部的Button元素
    const tooltipElement = triggerRef.current;
    if (tooltipElement.className && tooltipElement.className.includes('arco-tooltip')) {
      const buttonElement = tooltipElement.querySelector('button');
      if (buttonElement) {
        return buttonElement as HTMLElement;
      }
    }

    return triggerRef.current;
  }, []);

  // 计算弹出层位置
  const calculatePosition = useCallback(() => {
    if (!popoverRef.current || !container) return;

    // 使用getTriggerElement获取正确的DOM元素
    const triggerElement = getTriggerElement();
    if (!triggerElement || typeof triggerElement.getBoundingClientRect !== 'function') {
      console.warn('triggerElement is not a valid DOM element:', triggerElement);
      return;
    }

    const triggerRect = triggerElement.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();
    const popoverRect = popoverRef.current.getBoundingClientRect();

    const scrollTop = container === document.body ? window.pageYOffset : container.scrollTop;
    const scrollLeft = container === document.body ? window.pageXOffset : container.scrollLeft;

    let top = 0;
    let left = 0;
    let arrowTop = 0;
    let arrowLeft = 0;

    const triggerCenterX = triggerRect.left + triggerRect.width / 2;
    const triggerCenterY = triggerRect.top + triggerRect.height / 2;

    switch (placement) {
      case 'top':
        top = triggerRect.top - popoverRect.height - offset;
        left = triggerCenterX - popoverRect.width / 2;
        arrowTop = popoverRect.height;
        arrowLeft = popoverRect.width / 2;
        break;
      case 'topLeft':
        top = triggerRect.top - popoverRect.height - offset;
        left = triggerRect.left;
        arrowTop = popoverRect.height;
        arrowLeft = 20;
        break;
      case 'topRight':
        top = triggerRect.top - popoverRect.height - offset;
        left = triggerRect.right - popoverRect.width;
        arrowTop = popoverRect.height;
        arrowLeft = popoverRect.width - 20;
        break;
      case 'bottom':
        top = triggerRect.bottom + offset;
        left = triggerCenterX - popoverRect.width / 2;
        arrowTop = -8;
        arrowLeft = popoverRect.width / 2;
        break;
      case 'bottomLeft':
        top = triggerRect.bottom + offset;
        left = triggerRect.left;
        arrowTop = -8;
        arrowLeft = 20;
        break;
      case 'bottomRight':
        top = triggerRect.bottom + offset;
        left = triggerRect.right - popoverRect.width;
        arrowTop = -8;
        arrowLeft = popoverRect.width - 20;
        break;
      case 'left':
        top = triggerCenterY - popoverRect.height / 2;
        left = triggerRect.left - popoverRect.width - offset;
        arrowTop = popoverRect.height / 2;
        arrowLeft = popoverRect.width;
        break;
      case 'right':
        top = triggerCenterY - popoverRect.height / 2;
        left = triggerRect.right + offset;
        arrowTop = popoverRect.height / 2;
        arrowLeft = -8;
        break;
    }

    // 自动调整位置，防止超出视口
    if (autoAdjust) {
      const viewportWidth = window.innerWidth;
      const viewportHeight = window.innerHeight;

      // 水平调整
      if (left < 0) {
        left = 8;
      } else if (left + popoverRect.width > viewportWidth) {
        left = viewportWidth - popoverRect.width - 8;
      }

      // 垂直调整
      if (top < 0) {
        top = 8;
      } else if (top + popoverRect.height > viewportHeight) {
        top = viewportHeight - popoverRect.height - 8;
      }
    }

    // 转换为相对于容器的位置
    const finalTop = top - containerRect.top + scrollTop;
    const finalLeft = left - containerRect.left + scrollLeft;

    setPosition({ top: finalTop, left: finalLeft });
    setArrowPosition({ top: arrowTop, left: arrowLeft });
  }, [placement, offset, autoAdjust, container, getTriggerElement]);

  // 更新位置
  useEffect(() => {
    if (isVisible) {
      // 延迟计算位置，确保DOM已更新
      const timer = setTimeout(calculatePosition, 0);
      return () => clearTimeout(timer);
    }
  }, [isVisible, calculatePosition]);

  // 监听窗口大小变化和滚动事件
  useEffect(() => {
    if (!isVisible) return;

    const handleResize = () => {
      calculatePosition();
    };

    const handleScroll = () => {
      calculatePosition();
    };

    window.addEventListener('resize', handleResize);
    window.addEventListener('scroll', handleScroll, true);

    // 如果在Modal中，监听Modal容器的滚动
    if (inModal && container && container !== document.body) {
      container.addEventListener('scroll', handleScroll);
    }

    return () => {
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('scroll', handleScroll, true);
      if (inModal && container && container !== document.body) {
        container.removeEventListener('scroll', handleScroll);
      }
    };
  }, [isVisible, calculatePosition, inModal, container]);

  // 处理触发事件
  const handleTriggerEvent = useCallback(
    (event: React.MouseEvent | React.FocusEvent) => {
      if (disabled) return;

      if (stopPropagation) {
        event.preventDefault();
        event.stopPropagation();
      }

      const newVisible = !isVisible;
      setVisibleState(newVisible);
    },
    [disabled, isVisible, setVisibleState, stopPropagation]
  );

  // 处理点击外部关闭
  const handleClickOutside = useCallback(
    (event: MouseEvent) => {
      if (
        isVisible &&
        popoverRef.current &&
        !getTriggerElement()?.contains(event.target as Node) &&
        !popoverRef.current.contains(event.target as Node)
      ) {
        setVisibleState(false);
      }
    },
    [isVisible, setVisibleState, getTriggerElement]
  );

  // 监听点击外部事件
  useEffect(() => {
    if (isVisible && trigger === 'click') {
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }
  }, [isVisible, trigger, handleClickOutside]);

  // 处理悬停事件
  const handleMouseEnter = useCallback(() => {
    if (disabled || trigger !== 'hover') return;
    setVisibleState(true);
  }, [disabled, trigger, setVisibleState]);

  const handleMouseLeave = useCallback(() => {
    if (disabled || trigger !== 'hover') return;
    setVisibleState(false);
  }, [disabled, trigger, setVisibleState]);

  // 克隆触发元素并添加事件
  const triggerElement = useMemo(() => {
    return React.cloneElement(children, {
      ref: (node: HTMLElement | null) => {
        // 确保node是一个有效的DOM元素
        if (node && node.nodeType === Node.ELEMENT_NODE) {
          triggerRef.current = node as HTMLElement;
        } else {
          console.warn('Invalid DOM element received in ref:', node);
          triggerRef.current = null;
        }

        // 处理原始ref
        const originalRef = (children as ReactElement & { ref?: React.Ref<HTMLElement> }).ref;
        if (typeof originalRef === 'function') {
          originalRef(node);
        }
      },
      onClick: trigger === 'click' ? handleTriggerEvent : children.props.onClick,
      onMouseEnter: trigger === 'hover' ? handleMouseEnter : children.props.onMouseEnter,
      onMouseLeave: trigger === 'hover' ? handleMouseLeave : children.props.onMouseLeave,
      onFocus: trigger === 'focus' ? handleTriggerEvent : children.props.onFocus
    });
  }, [children, trigger, handleTriggerEvent, handleMouseEnter, handleMouseLeave]);

  // 渲染弹出层
  const renderPopover = useMemo(() => {
    if (!isVisible || !container) return null;

    // 在Modal环境中，强制渲染到document.body
    const targetContainer = inModal ? document.body : container;

    // 计算在Modal中的位置
    const modalPosition = inModal
      ? {
          top: position.top + (container === document.body ? 0 : container.getBoundingClientRect().top),
          left: position.left + (container === document.body ? 0 : container.getBoundingClientRect().left)
        }
      : position;

    return createPortal(
      <div
        ref={popoverRef}
        className={`${styles.customPopover} ${inModal ? styles.inModal : ''} ${forceTop ? styles.forceTop : ''} ${popupClassName}`}
        style={{
          ...popupStyle,
          top: modalPosition.top,
          left: modalPosition.left,
          // 在Modal中强制使用fixed定位
          position: inModal ? 'fixed' : 'absolute'
        }}
        onMouseEnter={trigger === 'hover' ? handleMouseEnter : undefined}
        onMouseLeave={trigger === 'hover' ? handleMouseLeave : undefined}
      >
        {showArrow && (
          <div
            className={`${styles.arrow} ${styles[`arrow-${placement}`]}`}
            style={{
              top: arrowPosition.top,
              left: arrowPosition.left
            }}
          />
        )}
        <div className={styles.content}>{content}</div>
      </div>,
      targetContainer
    );
  }, [
    isVisible,
    container,
    inModal,
    popupClassName,
    popupStyle,
    position,
    showArrow,
    placement,
    arrowPosition,
    content,
    trigger,
    handleMouseEnter,
    handleMouseLeave,
    forceTop
  ]);

  // 清理定时器
  useEffect(() => {
    return () => {
      clearTimers();
    };
  }, [clearTimers]);

  return (
    <>
      {triggerElement}
      {renderPopover}
    </>
  );
};

export default CustomPopover;

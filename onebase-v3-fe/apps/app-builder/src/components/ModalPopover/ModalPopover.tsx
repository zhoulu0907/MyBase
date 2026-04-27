import React, { useEffect, useRef, useState, useCallback, useMemo } from 'react';
import type { ReactElement } from 'react';
import { Modal } from '@arco-design/web-react';
import styles from './ModalPopover.module.less';

import type { ModalPopoverProps } from './types';

const ModalPopover: React.FC<ModalPopoverProps> = ({
  children,
  content,
  width = '300px',
  visible = false,
  onVisibleChange,
  placement = 'auto',
  stopPropagation = false
}) => {
  const triggerRef = useRef<HTMLElement | null>(null);
  const modalRef = useRef<HTMLDivElement>(null);
  const [internalVisible, setInternalVisible] = useState(visible);
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const [arrowPosition, setArrowPosition] = useState({ top: 0, left: 0 });
  const [finalPlacement, setFinalPlacement] = useState(placement);

  const isVisible = visible !== undefined ? visible : internalVisible;

  // 设置可见状态
  const setVisibleState = useCallback(
    (newVisible: boolean) => {
      setInternalVisible(newVisible);
      onVisibleChange?.(newVisible);
    },
    [onVisibleChange]
  );

  // 获取触发元素的DOM节点
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

  // 自动计算最佳位置
  const calculateAutoPlacement = useCallback(
    (triggerRect: DOMRect, modalWidth: number, modalHeight: number, offset: number) => {
      const viewportWidth = window.innerWidth;
      const viewportHeight = window.innerHeight;

      const triggerCenterX = triggerRect.left + triggerRect.width / 2;
      const triggerCenterY = triggerRect.top + triggerRect.height / 2;

      // 计算各个方向的空间
      const spaceTop = triggerRect.top;
      const spaceBottom = viewportHeight - triggerRect.bottom;
      const spaceLeft = triggerRect.left;
      const spaceRight = viewportWidth - triggerRect.right;

      // 优先级顺序：bottom -> top -> right -> left
      const placements = [
        {
          placement: 'bottom' as const,
          space: spaceBottom,
          top: triggerRect.bottom + offset,
          left: triggerCenterX - modalWidth / 2,
          arrowTop: -8,
          arrowLeft: modalWidth / 2
        },
        {
          placement: 'top' as const,
          space: spaceTop,
          top: triggerRect.top - modalHeight - offset,
          left: triggerCenterX - modalWidth / 2,
          arrowTop: modalHeight,
          arrowLeft: modalWidth / 2
        },
        {
          placement: 'right' as const,
          space: spaceRight,
          top: triggerCenterY - modalHeight / 2,
          left: triggerRect.right + offset,
          arrowTop: modalHeight / 2,
          arrowLeft: -8
        },
        {
          placement: 'left' as const,
          space: spaceLeft,
          top: triggerCenterY - modalHeight / 2,
          left: triggerRect.left - modalWidth - offset,
          arrowTop: modalHeight / 2,
          arrowLeft: modalWidth
        }
      ];

      // 找到有足够空间的位置
      const availablePlacement = placements.find((p) => {
        if (p.placement === 'top' || p.placement === 'bottom') {
          // 垂直方向需要检查垂直空间
          return p.space >= modalHeight;
        } else {
          // 水平方向需要检查水平空间
          return p.space >= modalWidth;
        }
      });
      const selectedPlacement = availablePlacement || placements[0]; // 默认使用bottom

      return {
        placement: selectedPlacement.placement,
        top: selectedPlacement.top,
        left: selectedPlacement.left,
        arrowTop: selectedPlacement.arrowTop,
        arrowLeft: selectedPlacement.arrowLeft
      };
    },
    []
  );

  // 计算弹出层位置
  const calculatePosition = useCallback(() => {
    if (!modalRef.current) return;

    const triggerElement = getTriggerElement();
    if (!triggerElement || typeof triggerElement.getBoundingClientRect !== 'function') {
      return;
    }

    const triggerRect = triggerElement.getBoundingClientRect();
    // 获取实际的弹窗尺寸
    const modalWidth = typeof width === 'number' ? width : parseInt(width as string) || 300;
    const modalHeight = modalRef.current?.offsetHeight || 200; // 使用实际高度，如果没有则使用默认值
    const offset = 8; // 固定偏移量

    let top = 0;
    let left = 0;
    let arrowTop = 0;
    let arrowLeft = 0;
    let finalPlacement = placement;

    const triggerCenterX = triggerRect.left + triggerRect.width / 2;
    const triggerCenterY = triggerRect.top + triggerRect.height / 2;

    // 如果placement是auto，则使用自动计算
    if (placement === 'auto') {
      const autoResult = calculateAutoPlacement(triggerRect, modalWidth, modalHeight, offset);
      top = autoResult.top;
      left = autoResult.left;
      arrowTop = autoResult.arrowTop;
      arrowLeft = autoResult.arrowLeft;
      finalPlacement = autoResult.placement;
    } else {
      // 使用指定的placement
      switch (placement) {
        case 'top':
          top = triggerRect.top - modalHeight - offset;
          left = triggerCenterX - modalWidth / 2;
          arrowTop = modalHeight;
          arrowLeft = modalWidth / 2;
          break;
        case 'topLeft':
          top = triggerRect.top - modalHeight - offset;
          left = triggerRect.left;
          arrowTop = modalHeight;
          arrowLeft = 20;
          break;
        case 'topRight':
          top = triggerRect.top - modalHeight - offset;
          left = triggerRect.right - modalWidth;
          arrowTop = modalHeight;
          arrowLeft = modalWidth - 20;
          break;
        case 'bottom':
          top = triggerRect.bottom + offset;
          left = triggerCenterX - modalWidth / 2;
          arrowTop = -8;
          arrowLeft = modalWidth / 2;
          break;
        case 'bottomLeft':
          top = triggerRect.bottom + offset;
          left = triggerRect.left;
          arrowTop = -8;
          arrowLeft = 20;
          break;
        case 'bottomRight':
          top = triggerRect.bottom + offset;
          left = triggerRect.right - modalWidth;
          arrowTop = -8;
          arrowLeft = modalWidth - 20;
          break;
        case 'left':
          top = triggerCenterY - modalHeight / 2;
          left = triggerRect.left - modalWidth - offset;
          arrowTop = modalHeight / 2;
          arrowLeft = modalWidth;
          break;
        case 'right':
          top = triggerCenterY - modalHeight / 2;
          left = triggerRect.right + offset;
          arrowTop = modalHeight / 2;
          arrowLeft = -8;
          break;
      }
    }

    // 自动调整位置，防止超出视口
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    // 水平调整
    if (left < 0) {
      left = 8;
    } else if (left + modalWidth > viewportWidth) {
      left = viewportWidth - modalWidth - 8;
    }

    // 垂直调整
    if (top < 0) {
      top = 8;
    } else if (top + modalHeight > viewportHeight) {
      top = viewportHeight - modalHeight - 8;
    }

    setPosition({ top, left });
    setArrowPosition({ top: arrowTop, left: arrowLeft });
    setFinalPlacement(finalPlacement);
  }, [placement, getTriggerElement, calculateAutoPlacement, width]);

  // 查找所有可滚动的父元素
  const findScrollableParents = useCallback((element: HTMLElement | null): HTMLElement[] => {
    const scrollableParents: HTMLElement[] = [];
    let current: HTMLElement | null = element;

    while (current && current !== document.body) {
      const style = window.getComputedStyle(current);
      const overflowY = style.overflowY || style.overflow;
      const overflowX = style.overflowX || style.overflow;

      // 检查是否可滚动
      if (
        (overflowY === 'auto' || overflowY === 'scroll' || overflowX === 'auto' || overflowX === 'scroll') &&
        (current.scrollHeight > current.clientHeight || current.scrollWidth > current.clientWidth)
      ) {
        scrollableParents.push(current);
      }

      current = current.parentElement;
    }

    // 添加 window 作为滚动容器
    return scrollableParents;
  }, []);

  // 更新位置
  useEffect(() => {
    if (isVisible) {
      // 延迟计算位置，确保DOM已更新
      const timer = setTimeout(calculatePosition, 0);

      // 获取触发元素和所有可滚动的父元素
      const triggerElement = getTriggerElement();
      const scrollableParents = triggerElement ? findScrollableParents(triggerElement) : [];

      // 使用 requestAnimationFrame 优化滚动性能
      let rafId: number | null = null;
      const handleScroll = () => {
        if (rafId === null) {
          rafId = requestAnimationFrame(() => {
            calculatePosition();
            rafId = null;
          });
        }
      };

      // 为所有滚动容器添加滚动监听
      scrollableParents.forEach((parent) => {
        parent.addEventListener('scroll', handleScroll, { passive: true });
      });

      // 监听窗口滚动和调整大小
      window.addEventListener('scroll', handleScroll, { passive: true });
      window.addEventListener('resize', handleScroll, { passive: true });

      return () => {
        clearTimeout(timer);
        if (rafId !== null) {
          cancelAnimationFrame(rafId);
        }
        scrollableParents.forEach((parent) => {
          parent.removeEventListener('scroll', handleScroll);
        });
        window.removeEventListener('scroll', handleScroll);
        window.removeEventListener('resize', handleScroll);
      };
    }
  }, [isVisible, calculatePosition, getTriggerElement, findScrollableParents]);

  // 处理触发事件
  const handleTriggerEvent = useCallback(
    (event: React.MouseEvent) => {
      if (stopPropagation) {
        event.preventDefault();
        event.stopPropagation();
      }

      const newVisible = !isVisible;
      setVisibleState(newVisible);
    },
    [isVisible, setVisibleState, stopPropagation]
  );

  // 克隆触发元素并添加事件
  const triggerElement = useMemo(() => {
    return React.cloneElement(children, {
      ref: (node: HTMLElement | null) => {
        // 确保node是一个有效的DOM元素
        if (node && node.nodeType === Node.ELEMENT_NODE) {
          triggerRef.current = node as HTMLElement;
        } else {
          triggerRef.current = null;
        }

        // 处理原始ref
        const originalRef = (children as ReactElement & { ref?: React.Ref<HTMLElement> }).ref;
        if (typeof originalRef === 'function') {
          originalRef(node);
        }
      },
      onClick: handleTriggerEvent
    });
  }, [children, handleTriggerEvent]);

  return (
    <>
      {triggerElement}
      <Modal
        visible={isVisible}
        onCancel={() => setVisibleState(false)}
        footer={null}
        title={null}
        closable={false}
        mask={false}
        maskClosable={false}
        getPopupContainer={() => document.body}
        className={styles.modalPopover}
        style={{
          position: 'fixed',
          top: position.top,
          left: position.left,
          width: width
        }}
      >
        <div ref={modalRef} className={styles.modalContent}>
          <div
            className={`${styles.arrow} ${styles[`arrow-${finalPlacement}`]}`}
            style={{
              top: arrowPosition.top,
              left: arrowPosition.left
            }}
          />
          <div className={styles.content}>{content}</div>
        </div>
      </Modal>
    </>
  );
};

export default ModalPopover;

import { Resizable, type ResizeCallbackData } from 'react-resizable';
import 'react-resizable/css/styles.css';
import { useRef, useEffect, useState } from 'react';
import { useWorkbenchResize } from '../../../hooks/use-workbench-resize';
import { parseWidthToPixel } from '../../../utils/width-utils';
import { MIN_WIDTH_PERCENTAGE } from '../../../utils/constants';
import styles from '../index.module.less';

interface ResizableWorkbenchItemProps {
  componentId: string;
  componentType: string;
  currentWidth: string;
  containerWidth: number;
  onWidthChange: (componentId: string, newWidth: string) => void;
  children: React.ReactNode;
  isSelected: boolean;
  onSelect: () => void;
}

/**
 * 可调整大小的工作台组件项
 */
export function ResizableWorkbenchItem({
  componentId,
  componentType,
  currentWidth,
  containerWidth,
  onWidthChange,
  children,
  isSelected,
  onSelect
}: ResizableWorkbenchItemProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  const [actualContainerWidth, setActualContainerWidth] = useState(containerWidth);
  const [localWidth, setLocalWidth] = useState<string>(currentWidth);

  // 当外部宽度变化时，同步本地宽度
  useEffect(() => {
    setLocalWidth(currentWidth);
  }, [currentWidth]);

  // 监听容器宽度变化（响应式支持）
  useEffect(() => {
    if (!containerRef.current) return;

    const resizeObserver = new ResizeObserver((entries) => {
      const newWidth = entries[0].contentRect.width;
      if (newWidth !== actualContainerWidth) {
        setActualContainerWidth(newWidth);
      }
    });

    resizeObserver.observe(containerRef.current);

    return () => {
      resizeObserver.disconnect();
    };
  }, [actualContainerWidth]);

  // 使用实际的容器宽度
  const effectiveContainerWidth = actualContainerWidth || containerWidth;
  const currentWidthPixel = parseWidthToPixel(localWidth, effectiveContainerWidth);

  const handleWidthChangeCallback = (newWidth: string) => {
    onWidthChange(componentId, newWidth);
  };

  const { handleResize } = useWorkbenchResize(
    componentType,
    effectiveContainerWidth,
    localWidth,
    handleWidthChangeCallback
  );

  // 计算最小和最大宽度约束
  const minWidth = (MIN_WIDTH_PERCENTAGE / 100) * effectiveContainerWidth;
  const maxWidth = effectiveContainerWidth;

  // 处理拖拽过程中的宽度变化（实时更新本地状态）
  const handleResizeDuring = (_e: React.SyntheticEvent, data: ResizeCallbackData) => {
    const percentage = (data.size.width / effectiveContainerWidth) * 100;
    const newWidthStr = `${percentage}%`;
    setLocalWidth(newWidthStr);
  };

  // 处理拖拽结束
  const handleResizeStop = (e: React.SyntheticEvent, data: ResizeCallbackData) => {
    const snappedWidth = handleResize(e, data);
    if (snappedWidth) {
      setLocalWidth(snappedWidth);
    }
  };

  // 处理点击事件（排除调整手柄）
  const handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
    const target = e.target as HTMLElement;
    if (target.closest(`.${styles.resizeHandle}`)) {
      return;
    }
    e.stopPropagation();
    onSelect();
  };

  return (
    <div ref={containerRef} className={styles.resizableWorkbenchItemWrapper} style={{ width: `${localWidth}` }}>
      <Resizable
        width={currentWidthPixel}
        height={0}
        onResize={handleResizeDuring}
        onResizeStop={handleResizeStop}
        minConstraints={[minWidth, 0]}
        maxConstraints={[maxWidth, 0]}
        handle={
          <span
            className={`${styles.resizeHandle} ${styles.resizeHandleRight}`}
            data-resize-handle="true"
            onMouseDown={(e) => {
              e.stopPropagation();
              e.preventDefault();
            }}
            onDragStart={(e) => {
              e.stopPropagation();
              e.preventDefault();
            }}
            onClick={(e) => e.stopPropagation()}
            style={{ pointerEvents: 'auto' }}
          />
        }
        resizeHandles={['e']}
      >
        <div
          className={styles.resizableWorkbenchItem}
          style={{
            width: '100%',
            borderColor: isSelected ? 'rgb(var(--primary-6))' : '',
            borderStyle: isSelected ? 'solid' : 'dashed',
            background: isSelected ? 'rgb(var(--primary-1))' : ''
          }}
          onMouseDown={handleClick}
          onClick={handleClick}
        >
          {children}
        </div>
      </Resizable>
    </div>
  );
}

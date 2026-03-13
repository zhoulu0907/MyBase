import { Resizable, type ResizeCallbackData } from 'react-resizable';
import 'react-resizable/css/styles.css';
import { useRef, useEffect, useState, useCallback } from 'react';
import { WB_GRID_CONFIG } from '../../../utils/constants';
import {
  calcRowSpan,
  colSpanToPixels,
  percentageToColSpan,
  snapToCol,
  colSpanToPercentage
} from '../../../utils/grid-layout';
import styles from '../index.module.less';

interface WorkbenchItemLayout {
  row: number;
  column: number;
  rowSpan?: number;
  colSpan?: number;
}

interface ResizableWorkbenchItemProps {
  componentId: string;
  componentType: string;
  currentWidth: string;
  containerWidth: number;
  rowHeight?: number;
  onWidthChange: (componentId: string, newWidth: string) => void;
  onHeightChange: (componentId: string, rowSpan: number) => void;
  children: React.ReactNode;
  isSelected: boolean;
  onSelect: () => void;
  layout?: WorkbenchItemLayout;
}

/**
 * 可调整宽度的工作台组件项。
 *
 * 宽度调整策略：
 * - wrapper 只做 grid 占位（gridColumn span），不设 width
 * - 拖动中：内容区使用像素 width 覆盖视觉宽度，避免触发 grid reflow
 * - 松手时：吸附到最近列，更新 schema width，draggingWidth 在 grid reflow 完成后（rAF）清除
 */
export function ResizableWorkbenchItem({
  componentId,
  componentType,
  currentWidth,
  containerWidth,
  rowHeight,
  onWidthChange,
  onHeightChange,
  children,
  isSelected,
  onSelect,
  layout
}: ResizableWorkbenchItemProps) {
  const contentRef = useRef<HTMLDivElement>(null);
  const [draggingWidth, setDraggingWidth] = useState<number | null>(null);
  const pendingCommitRef = useRef(false);
  const lastRowSpanRef = useRef<number>(0);

  // 监听内容高度，计算 rowSpan 并上报
  useEffect(() => {
    if (!contentRef.current) return;
    const observer = new ResizeObserver((entries) => {
      const rowSpan = calcRowSpan(entries[0].contentRect.height);
      if (rowSpan !== lastRowSpanRef.current) {
        lastRowSpanRef.current = rowSpan;
        onHeightChange(componentId, rowSpan);
      }
    });
    observer.observe(contentRef.current);
    return () => observer.disconnect();
  }, [componentId, onHeightChange]);

  const committedColSpan = percentageToColSpan(currentWidth);
  const minWidthPixel = colSpanToPixels(WB_GRID_CONFIG.minCols, containerWidth);
  const maxWidthPixel = colSpanToPixels(WB_GRID_CONFIG.columns, containerWidth);

  const handleResizeStart = useCallback(() => {
    pendingCommitRef.current = false;
    if (contentRef.current) {
      setDraggingWidth(contentRef.current.getBoundingClientRect().width);
    }
  }, []);

  const handleResizeDuring = useCallback((_e: React.SyntheticEvent, data: ResizeCallbackData) => {
    setDraggingWidth(data.size.width);
  }, []);

  const handleResizeStop = useCallback(
    (_e: React.SyntheticEvent, data: ResizeCallbackData) => {
      const snappedCols = snapToCol(data.size.width, containerWidth);
      const snappedPixels = colSpanToPixels(snappedCols, containerWidth);
      const snappedWidth = colSpanToPercentage(snappedCols, containerWidth);
      setDraggingWidth(snappedPixels);
      pendingCommitRef.current = true;
      onWidthChange(componentId, snappedWidth);
    },
    [containerWidth, componentId, onWidthChange]
  );

  // currentWidth 更新（schema 落地）后，等 grid reflow 完成再清除 draggingWidth
  useEffect(() => {
    if (!pendingCommitRef.current) return;
    const raf = requestAnimationFrame(() => {
      pendingCommitRef.current = false;
      setDraggingWidth(null);
    });
    return () => cancelAnimationFrame(raf);
  }, [currentWidth]);

  const handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if ((e.target as HTMLElement).closest(`.${styles.resizeHandle}`)) return;
    e.stopPropagation();
    onSelect();
  };

  const gridStyle: React.CSSProperties = layout?.row
    ? {
        gridRow: `${layout.row} / span ${layout.rowSpan ?? 1}`,
        gridColumn: `${layout.column} / span ${committedColSpan}`
      }
    : { gridColumn: `span ${committedColSpan}` };

  const contentWidthStyle: React.CSSProperties =
    draggingWidth !== null ? { width: `${draggingWidth}px`, transition: 'none' } : { width: '100%' };

  const resizableWidth = draggingWidth ?? colSpanToPixels(committedColSpan, containerWidth);

  return (
    <div className={styles.resizableWorkbenchItemWrapper} style={gridStyle}>
      <Resizable
        width={resizableWidth}
        height={0}
        onResizeStart={handleResizeStart}
        onResize={handleResizeDuring}
        onResizeStop={handleResizeStop}
        minConstraints={[minWidthPixel, 0]}
        maxConstraints={[maxWidthPixel, 0]}
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
          ref={contentRef}
          className={styles.resizableWorkbenchItem}
          style={{
            ...contentWidthStyle,
            borderColor: isSelected ? 'rgb(var(--primary-6))' : '',
            borderStyle: isSelected ? 'solid' : 'dashed'
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

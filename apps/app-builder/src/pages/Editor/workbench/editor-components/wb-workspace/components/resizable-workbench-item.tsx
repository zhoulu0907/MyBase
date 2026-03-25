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
 * - 拖动中：wrapper 的 gridColumn span 实时跟随吸附列数，防止内容区视觉溢出与相邻组件重叠
 * - 内容区使用像素 width 跟随鼠标，松手时吸附到最近列
 * - schema width 更新后，draggingColSpan/draggingWidth 在 grid reflow 完成后清除
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
  // 拖动中实时吸附的列数，用于同步扩展 wrapper gridColumn，防止视觉溢出
  const [draggingColSpan, setDraggingColSpan] = useState<number | null>(null);
  const pendingCommitRef = useRef(false);
  const lastRowSpanRef = useRef<number>(0);

  // 直接观测 contentRef（可见层）的高度变化，上报 rowSpan
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
      setDraggingColSpan(committedColSpan);
    }
  }, [committedColSpan]);

  const handleResizeDuring = useCallback(
    (_e: React.SyntheticEvent, data: ResizeCallbackData) => {
      const snapped = snapToCol(data.size.width, containerWidth);
      setDraggingWidth(data.size.width);
      setDraggingColSpan(snapped);
    },
    [containerWidth]
  );

  const handleResizeStop = useCallback(
    (_e: React.SyntheticEvent, data: ResizeCallbackData) => {
      const snappedCols = snapToCol(data.size.width, containerWidth);
      const snappedPixels = colSpanToPixels(snappedCols, containerWidth);
      const snappedWidth = colSpanToPercentage(snappedCols, containerWidth);
      setDraggingWidth(snappedPixels);
      setDraggingColSpan(snappedCols);
      pendingCommitRef.current = true;
      onWidthChange(componentId, snappedWidth);
    },
    [containerWidth, componentId, onWidthChange]
  );

  // currentWidth 更新（schema 落地）后，等 grid reflow 完成再清除 draggingWidth / draggingColSpan
  useEffect(() => {
    if (!pendingCommitRef.current) return;
    const raf = requestAnimationFrame(() => {
      pendingCommitRef.current = false;
      setDraggingWidth(null);
      setDraggingColSpan(null);
    });
    return () => cancelAnimationFrame(raf);
  }, [currentWidth]);

  const handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if ((e.target as HTMLElement).closest(`.${styles.resizeHandle}`)) return;
    e.stopPropagation();
    onSelect();
  };

  // 拖动中使用实时吸附列数占位，防止内容区超出 grid cell 与相邻组件重叠
  const activeColSpan = draggingColSpan ?? committedColSpan;

  const gridStyle: React.CSSProperties = layout?.row
    ? {
        gridRow: `${layout.row} / span ${layout.rowSpan ?? 1}`,
        gridColumn: `${layout.column} / span ${activeColSpan}`,
        alignSelf: 'start'
      }
    : { gridColumn: `span ${activeColSpan}`, alignSelf: 'start' };

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
            borderStyle: isSelected ? 'solid' : 'dashed',
            // 切断 fontSize 继承，防止子组件 em 尺寸变化影响此元素高度
            fontSize: 0
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

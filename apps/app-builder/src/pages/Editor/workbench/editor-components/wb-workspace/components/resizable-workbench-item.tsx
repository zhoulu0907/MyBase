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
  const measureRef = useRef<HTMLDivElement>(null);
  const [draggingWidth, setDraggingWidth] = useState<number | null>(null);
  // 拖动中实时吸附的列数，用于同步扩展 wrapper gridColumn，防止视觉溢出
  const [draggingColSpan, setDraggingColSpan] = useState<number | null>(null);
  const pendingCommitRef = useRef(false);
  const lastRowSpanRef = useRef<number>(0);

  // 通过独立测量层观察内容自然高度，避免 height:100% 子组件将 grid cell 高度反馈回来形成循环
  useEffect(() => {
    if (!measureRef.current) return;
    const observer = new ResizeObserver((entries) => {
      const rowSpan = calcRowSpan(entries[0].contentRect.height);
      if (rowSpan !== lastRowSpanRef.current) {
        lastRowSpanRef.current = rowSpan;
        onHeightChange(componentId, rowSpan);
      }
    });
    observer.observe(measureRef.current);
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
            borderStyle: isSelected ? 'solid' : 'dashed'
          }}
          onMouseDown={handleClick}
          onClick={handleClick}
        >
          {/* 独立测量层：position:absolute + height:fit-content，测量内容自然高度，不受 grid 撑高影响 */}
          <div
            ref={measureRef}
            style={{
              position: 'absolute',
              top: 0,
              left: 0,
              width: '100%',
              height: 'fit-content',
              visibility: 'hidden',
              pointerEvents: 'none',
              zIndex: -1,
              overflow: 'hidden'
            }}
            aria-hidden="true"
          >
            {children}
          </div>
          {children}
        </div>
      </Resizable>
    </div>
  );
}

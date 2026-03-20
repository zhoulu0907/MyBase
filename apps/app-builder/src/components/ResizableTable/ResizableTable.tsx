import { useState, useMemo, useCallback, forwardRef } from 'react';
import { Table } from '@arco-design/web-react';
import { Resizable } from 'react-resizable';
import ResizeHandle from './ResizeHandle';
import EmptyState from '@/components/EmptyState';
import type { ResizableTableProps, ResizableTitleProps } from './types';
import styles from './index.module.less';

const DEFAULT_MIN_WIDTH = 50;
const DEFAULT_MAX_WIDTH = 500;

// 可调整宽度的表头单元格组件
const ResizableTitle = forwardRef<HTMLTableCellElement, ResizableTitleProps>((props, ref) => {
  const { width, onResize, ...restProps } = props;

  if (!width) {
    return <th {...restProps} ref={ref} />;
  }

  return (
    <Resizable
      width={width}
      height={0}
      handle={<ResizeHandle handleAxis="e" />}
      onResize={onResize}
      draggableOpts={{
        enableUserSelectHack: false,
      }}
    >
      <th {...restProps} ref={ref} />
    </Resizable>
  );
});

ResizableTitle.displayName = 'ResizableTitle';

const ResizableTable = forwardRef<HTMLDivElement, ResizableTableProps>((props, ref) => {
  const {
    resizable = true,
    minWidth = DEFAULT_MIN_WIDTH,
    maxWidth = DEFAULT_MAX_WIDTH,
    columns = [],
    className,
    emptyContent,
    ...restProps
  } = props;

  // 维护列宽状态
  const [columnWidths, setColumnWidths] = useState<Record<number, number>>(() => {
    const widths: Record<number, number> = {};
    columns.forEach((col, index) => {
      if (col.width && typeof col.width === 'number') {
        widths[index] = col.width;
      }
    });
    return widths;
  });

  // 处理列宽调整
  const handleResize = useCallback((index: number) => {
    return (_e: React.SyntheticEvent, { size }: { size: { width: number } }) => {
      const newWidth = Math.min(Math.max(size.width, minWidth), maxWidth);
      setColumnWidths((prev) => ({
        ...prev,
        [index]: newWidth,
      }));
    };
  }, [minWidth, maxWidth]);

  // 构建带有拖拽功能的列配置
  const resizableColumns = useMemo(() => {
    if (!resizable) {
      return columns;
    }

    return columns.map((column, index) => {
      const originalWidth = column.width;
      const currentWidth = columnWidths[index] ?? (typeof originalWidth === 'number' ? originalWidth : undefined);

      return {
        ...column,
        width: currentWidth,
        onHeaderCell: (col: any) => {
          const originalOnHeaderCell = column.onHeaderCell;
          const originalResult = originalOnHeaderCell ? originalOnHeaderCell(col) : {};

          return {
            ...originalResult,
            width: col.width,
            onResize: handleResize(index),
          };
        },
      };
    });
  }, [columns, resizable, columnWidths, handleResize]);

  // 自定义表格组件
  const components = useMemo(() => {
    if (!resizable) {
      return {};
    }

    return {
      header: {
        th: ResizableTitle,
      },
    };
  }, [resizable]);

  // 合并 className
  const tableClassName = useMemo(() => {
    if (resizable) {
      return `${styles.resizableTable} ${className || ''}`.trim();
    }
    return className;
  }, [resizable, className]);

  return (
    <Table
      ref={ref}
      className={tableClassName || undefined}
      columns={resizableColumns}
      components={components}
      emptyContent={emptyContent ?? <EmptyState type="table" />}
      {...restProps}
    />
  );
});

ResizableTable.displayName = 'ResizableTable';

export default ResizableTable;
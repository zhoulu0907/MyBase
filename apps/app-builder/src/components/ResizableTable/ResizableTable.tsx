import { useState, useMemo, useCallback, forwardRef, useEffect } from 'react';
import { Table } from '@arco-design/web-react';
import { Resizable } from 'react-resizable';
import ResizeHandle from './ResizeHandle';
import EmptyState from '@/components/EmptyState';
import type { ResizableTableProps, ResizableTitleProps } from './types';
import styles from './index.module.less';

const DEFAULT_MIN_WIDTH = 40;
const DEFAULT_MAX_WIDTH = 600;

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
    components: externalComponents,
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

  // 当 columns 变化时同步更新列宽
  useEffect(() => {
    setColumnWidths((prev) => {
      const newWidths: Record<number, number> = {};
      columns.forEach((col, index) => {
        if (col.width && typeof col.width === 'number') {
          // 保留已调整过的宽度，新列使用初始宽度
          newWidths[index] = prev[index] ?? col.width;
        }
      });
      return newWidths;
    });
  }, [columns]);

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
          const originalResult = originalOnHeaderCell ? originalOnHeaderCell(col, {}) : {};

          return {
            ...originalResult,
            width: col.width,
            onResize: handleResize(index),
          };
        },
      };
    });
  }, [columns, resizable, columnWidths, handleResize]);

  // 自定义表格组件 - 合并外部传入的 components 和内部的列宽拖拽配置
  const components = useMemo(() => {
    const internalComponents = resizable
      ? {
          header: {
            th: ResizableTitle,
          },
        }
      : {};

    // 深度合并 components
    return {
      ...externalComponents,
      ...internalComponents,
      header: {
        ...externalComponents?.header,
        ...internalComponents?.header,
      },
      body: {
        ...externalComponents?.body,
      },
    };
  }, [resizable, externalComponents]);

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
      noDataElement={emptyContent ?? <EmptyState type="table" />}
      {...restProps}
    />
  );
});

ResizableTable.displayName = 'ResizableTable';

export default ResizableTable;
import React from 'react';
import { Table } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import type { DictData } from '@onebase/platform-center';
import styles from './index.module.less';

// 拖拽手柄组件 - 使用 forwardRef 包装
const DragHandle = SortableHandle(
  React.forwardRef<HTMLDivElement>((props, ref) => (
    <div ref={ref} {...props}>
      <IconDragDotVertical className={styles.dragHandle} />
    </div>
  ))
);

// 可排序的表格行组件 - 简化实现
const SortableTableRow = SortableElement(
  ({ children, ...props }: { children: React.ReactNode; [key: string]: unknown }) => {
    return <tr {...props}>{children}</tr>;
  }
);

// 可排序的表格体组件 - 简化实现
const SortableTableBody = SortableContainer((props: { children: React.ReactNode; [key: string]: unknown }) => {
  return <tbody {...props} />;
});

interface SortableTableProps {
  data: DictData[];
  columns: Array<{
    title: React.ReactNode;
    dataIndex: string;
    width?: number;
    render?: (value: unknown, record: DictData, index: number) => React.ReactNode;
  }>;
  onSort: ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => void;
}

const SortableTable: React.FC<SortableTableProps> = ({ data, columns, onSort }) => {
  const tableRef = React.useRef<HTMLDivElement>(null);

  // 表格组件配置
  const components = {
    header: {
      operations: () => [
        {
          node: <th />,
          width: 40
        }
      ]
    },
    body: {
      operations: () => [
        {
          node: (
            <td>
              <div>
                <DragHandle />
              </div>
            </td>
          ),
          width: 40
        }
      ],
      tbody: (props: { children: React.ReactNode; [key: string]: unknown }) => (
        <SortableTableBody
          useDragHandle
          onSortEnd={onSort}
          helperContainer={() => {
            // 使用 ref 而不是 querySelector 来避免 findDOMNode 警告
            return tableRef.current?.querySelector('dict-config-container') || document.body;
          }}
          helperClass="sortableHelper"
          {...props}
        />
      ),
      row: (props: { index: number; children: React.ReactNode; [key: string]: unknown }) => {
        const { index, children, ...rest } = props;
        return (
          // @ts-expect-error - react-sortable-hoc 类型定义问题
          <SortableTableRow index={index} {...rest}>
            {children}
          </SortableTableRow>
        );
      }
    }
  };

  return (
    <div ref={tableRef}>
      <Table
        data={data}
        columns={columns}
        pagination={false}
        className={styles.dictTable}
        rowKey="id"
        components={components}
        scroll={{ y: 510 }}
      />
    </div>
  );
};

export default SortableTable;

import React from 'react';
import { Table } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import type { SortableTableProps } from '../types';
import styles from '../index.module.less';

// 拖拽手柄组件
const DragHandle = SortableHandle(() => <IconDragDotVertical className={styles.dragHandle} />);

// 可排序的表格行组件
const SortableTableRow = SortableElement(
  ({ children, ...props }: { children: React.ReactNode; [key: string]: unknown }) => {
    return <tr {...props}>{children}</tr>;
  }
);

// 可排序的表格体组件
const SortableTableBody = SortableContainer((props: { children: React.ReactNode; [key: string]: unknown }) => {
  return <tbody {...props} />;
});

const SortableTable: React.FC<SortableTableProps> = ({ data, columns, onSort }) => {
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
          width: 30
        }
      ],
      tbody: (props: { children: React.ReactNode; [key: string]: unknown }) => (
        <SortableTableBody
          useDragHandle
          onSortEnd={onSort}
          helperContainer={() => {
            const container = document.querySelector(`#field-config-container table tbody`);
            return (container as HTMLElement) || document.body;
          }}
          helperClass="sortableHelper"
          {...props}
        />
      ),
      row: (props: { index: number; children: React.ReactNode; [key: string]: unknown }) => {
        const { index, children, ...rest } = props;
        return (
          <SortableTableRow index={index} {...rest}>
            {children}
          </SortableTableRow>
        );
      }
    }
  };

  return (
    <Table
      data={data}
      columns={columns}
      pagination={false}
      className={styles.fieldConfigTable}
      rowKey="id"
      components={components}
      scroll={{ y: 'calc(100vh - 400px)' }}
    />
  );
};

export default SortableTable;

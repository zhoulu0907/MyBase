import React, { forwardRef } from 'react';
import { Table } from '@arco-design/web-react';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import { IconDragDotVertical } from '@arco-design/web-react/icon';

// 拖拽手柄组件
const DragHandle = SortableHandle(() => <IconDragDotVertical className="drag-handle" />);

// 可排序的表格行组件
const SortableTableRow = SortableElement(({ children, ...props }: any) => {
  return <tr {...props}>{children}</tr>;
});

// 可排序的表格体组件
const SortableTableBody = SortableContainer(
  forwardRef<HTMLTableSectionElement, any>((props, ref) => {
    return <tbody ref={ref} {...props} />;
  })
);

interface SortableTableProps {
  data: any[];
  columns: any[];
  onSort: (newData: any[]) => void;
  className?: string;
  pagination?: boolean | object;
  rowKey?: string;
  rowClassName?: (record: any) => string;
}

const SortableTable: React.FC<SortableTableProps> = ({
  data,
  columns,
  onSort,
  className,
  pagination = false,
  rowKey = 'id',
  rowClassName
}) => {
  // 处理拖拽排序
  const handleSort = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    if (oldIndex !== newIndex) {
      const newData = [...data];
      const [removed] = newData.splice(oldIndex, 1);
      newData.splice(newIndex, 0, removed);
      onSort(newData);
    }
  };

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
              <div className="arco-table-cell">
                <DragHandle />
              </div>
            </td>
          ),
          width: 40
        }
      ],
      tbody: (props: any) => (
        <SortableTableBody
          useDragHandle
          onSortEnd={handleSort}
          helperContainer={() => document.querySelector('.field-table table tbody')}
          {...props}
        />
      ),
      row: (props: any) => {
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
      pagination={pagination}
      className={className}
      rowKey={rowKey}
      rowClassName={rowClassName}
      components={components}
    />
  );
};

export default SortableTable;

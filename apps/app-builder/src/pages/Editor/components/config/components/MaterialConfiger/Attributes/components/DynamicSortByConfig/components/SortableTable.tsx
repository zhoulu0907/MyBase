import { Table } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';

// 1. 拖拽手柄
const DragHandle = SortableHandle(() => (
  <IconDragDotVertical style={{ cursor: 'grab', color: '#86909c', fontSize: 18 }} />
));

const SortableWrapper = SortableContainer((props) => <tbody {...props} />);
const SortableItem = SortableElement((props) => <tr {...props} />);

function SortableTable({ data, columns, onSortEnd }) {
  const components = {
    body: {
      operations: () => [
        {
          node: (
            <td className="arco-table-td">
              <div className="arco-table-cell">
                <DragHandle />
              </div>
            </td>
          ),
          width: 10
        }
      ],
      tbody: (props) => (
        <SortableWrapper
          useDragHandle
          onSortEnd={onSortEnd}
          helperContainer={() => document.querySelector('.sort-table table tbody')}
          {...props}
        />
      ),
      row: (props) => <SortableItem index={props.index} {...props} />
    }
  };

  return (
    <div style={{ width: '100%', padding: 5 }}>
      {data.length > 0 && (
        <Table
          className="sort-table"
          pagination={false}
          border={false}
          showHeader={false}
          components={components}
          columns={columns}
          data={data}
          rowKey="key"
        />
      )}

      <style>{`
        .sort-table .arco-table-td {
          border-bottom: none;
          padding: 8px 0;
        }
        .sort-table .arco-table-tr:hover .arco-table-td {
          background-color: transparent;
        }
        .arco-radio-button {
          background-color: #f2f3f5;
          border: none !important;
        }
        .arco-radio-button-checked {
          background-color: #fff !important;
          box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .sort-table .arco-radio-button {
          min-width: 90px;
          text-align: center;
        }
      `}</style>
    </div>
  );
}

export default SortableTable;

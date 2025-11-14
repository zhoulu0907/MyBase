import { useState } from 'react';
import Header from '../../../header';
import BottomBtn from '../../../bottomBtn';
import { Table } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import type { BranchData, ApproveDrawerProps, TableComponents, SortEndHandler } from './indexType';
import styles from './index.module.less';

const arrayMoveMutate = (array: any, from: any, to: any) => {
  const startIndex = to < 0 ? array.length + to : to;

  if (startIndex >= 0 && startIndex < array.length) {
    const item = array.splice(from, 1)[0];
    array.splice(startIndex, 0, item);
  }
};

const arrayMove = (array: any, from: any, to: any) => {
  array = [...array];
  arrayMoveMutate(array, from, to);
  return array;
};

const columns = [
  {
    title: '分支名称',
    dataIndex: 'name'
  },
  {
    title: '下游节点',
    dataIndex: 'salary'
  },
  {
    title: '默认分支',
    dataIndex: 'address'
  },
  {
    title: '优先级',
    dataIndex: 'email'
  }
];

const initialData = [
  {
    key: '1',
    name: '年休假',
    salary: '执行人1',
    address: '否',
    email: '1'
  },
  {
    key: '3',
    name: '年休假',
    salary: '执行人2',
    address: '否',
    email: '2'
  },
  {
    key: '2',
    name: '/',
    salary: '执行人3',
    address: '是',
    email: '3'
  }
];

const DragHandle = SortableHandle(() => (
  <IconDragDotVertical
    style={{
      cursor: 'move',
      color: '#555'
    }}
  />
));

const SortableWrapper = SortableContainer((props: any) => {
  return <tbody {...props} />;
});
const SortableItem = SortableElement((props: any) => {
  return <tr {...props} />;
});

// { handleConfigSubmit, configData }: ApproveDrawerProps
export default function ApproveDreawer() {
  const [data, setData] = useState(initialData);
  const [editValue, setEditValue] = useState('');
  function onSortEnd({ oldIndex, newIndex }: SortEndHandler) {
    if (oldIndex !== newIndex) {
      const newData = arrayMove([...data], oldIndex, newIndex).filter((el: BranchData) => !!el);
      const updatedData = newData.map((item: BranchData, index: number) => ({
        ...item,
        email: String(index + 1)
      }));
      setData(updatedData);
    }
  }

  const DraggableContainer = (props: any) => (
    <SortableWrapper
      useDragHandle
      onSortEnd={onSortEnd}
      helperContainer={() => document.querySelector('.arco-drag-table-container-2 table tbody')}
      updateBeforeSortStart={({ node }) => {
        const tds = node.querySelectorAll('td');
        tds.forEach((td) => {
          td.style.width = td.clientWidth + 'px';
        });
      }}
      {...props}
    />
  );
  const DraggableRow = (props: { record: BranchData; index: number; [key: string]: any }) => {
    const { record, index, ...rest } = props;
    // 如果是默认分支则返回普通行
    if (record.address === '是') {
      return <tr {...rest} />;
    }
    return <SortableItem index={index} {...rest} />;
  };

  const components: TableComponents = {
    header: {
      operations: ({ expandNode }) => [
        {
          node: <th />,
          width: 40
        },
        {
          name: 'expandNode',
          node: expandNode
        }
      ]
    },
    body: {
      operations: ({ expandNode }) => [
        {
          node: (record: BranchData) => (
            <td>
              <div className="arco-table-cell">{record.address !== '是' && <DragHandle />}</div>
            </td>
          ),
          width: 40
        },
        {
          name: 'expandNode',
          node: expandNode
        }
      ],
      tbody: DraggableContainer,
      row: DraggableRow
    }
  };

  function handleSubmit() {
    // handleConfigSubmit(data);
  }
  return (
    <>
      <Header changeName={(name) => setEditValue(name)} />
      <div className={styles.conditional}>
        <div className={styles.configTitle}>
          分支优先级<span className={styles.titleTips}>可通过拖拽调整默认分支以外的分支优先级</span>
        </div>
        <div className={styles.configContent}>
          <Table
            className="arco-drag-table-container-2"
            components={components}
            columns={columns}
            data={data}
            pagination={false}
            rowSelection={{
              type: 'checkbox'
            }}
          />
        </div>
      </div>
      <BottomBtn handleSubmit={handleSubmit} />
    </>
  );
}

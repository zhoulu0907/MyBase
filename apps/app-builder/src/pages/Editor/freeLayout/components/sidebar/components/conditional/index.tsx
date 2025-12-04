import { useEffect, useState } from 'react';
import { Table } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { FlowNodeEntity, useClientContext } from '@flowgram.ai/free-layout-editor';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import type { BranchData, TableComponents, SortEndHandler } from './indexType';
import BottomBtn from '../../../bottomBtn';
import Header from '../../../header';
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
    dataIndex: 'toNodeName'
  },
  {
    title: '默认分支',
    dataIndex: 'isDefault'
  },
  {
    title: '优先级',
    dataIndex: 'priority'
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
export default function Conditional({ node }: { node: FlowNodeEntity }) {
  const [data, setData] = useState<BranchData[]>([]);
  const ctx = useClientContext();
  function onSortEnd({ oldIndex, newIndex }: SortEndHandler) {
    if (oldIndex !== newIndex) {
      const newData = arrayMove([...data], oldIndex, newIndex).filter((el: BranchData) => !!el);
      const updatedData = newData.map((item: BranchData, index: number) => ({
        ...item,
        priority: String(index + 1)
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
    if (record.isDefault === '是') {
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
              <div className="arco-table-cell">{record.isDefault !== '是' && <DragHandle />}</div>
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
    const allLines = ctx.document.linesManager
      .getAllLines()
      .filter((line) => line.info.from === node.id && line.lineData);

    data.forEach((tableItem) => {
      const matchingLine = allLines.find((line) => line.lineData.name === tableItem.name);
      if (matchingLine) {
        matchingLine.lineData = {
          ...matchingLine.lineData,
          priority: tableItem.priority // 直接使用表格中的优先级
        };
      }
    });
  }

  const sortLineData = () => {
    const allLines = ctx.document.linesManager.getAllLines();
    const nodeOutputNodes = allLines.filter((line) => line.info.from === node.id && line.lineData);
    const tableData = nodeOutputNodes
      .map((line) => {
        return {
          name: line.lineData.name,
          toNodeName: line?.to?.toJSON()?.data?.name,
          isDefault: line.lineData.isDefault ? '是' : '否',
          priority: line.lineData.priority
        };
      })
      .sort((a, b) => {
        const priorityA = Number(a.priority) || 0;
        const priorityB = Number(b.priority) || 0;
        return priorityA - priorityB;
      });
    setData(tableData);
  };
  useEffect(() => {
    sortLineData();
  }, []);
  return (
    <>
      <Header />
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
            rowKey="name"
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

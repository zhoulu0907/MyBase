import { useState } from 'react';
import { Button, Form, Input, Select, Table, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlusCircle } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import { nanoid } from 'nanoid';
import styles from './index.module.less';

interface field {
  id: string;
  fieldName?: string;
  fieldType?: string;
}

// 拖拽图标
const DragHandle = SortableHandle(() => <IconDragDotVertical className={styles.dragHandle} />);
// 排序的表格体
const SortableWrapper = SortableContainer((props: any) => {
  return <tbody {...props} />;
});
// 排序的表格行
const SortableItem = SortableElement((props: any) => {
  return <tr {...props} />;
});

export interface CollectFieldsProps {
  form: FormInstance;
}

const CollectFields: React.FC<CollectFieldsProps> = ({ form }) => {
  const [tableData, setTableData] = useState<field[]>([]);

  const columns = [
    {
      title: '字段名称',
      dataIndex: 'fieldName',
      render: (_: any, record: field, index: number) => {
        return (
          <Form.Item field={`fields[${index}].fieldName`} noStyle>
            <Input placeholder="请输入"></Input>
          </Form.Item>
        );
      }
    },
    {
      title: '字段类型',
      dataIndex: 'fieldType',
      render: (_: any, record: field, index: number) => {
        // todo  select
        return (
          <Form.Item field={`fields[${index}].fieldType`} noStyle>
            <Input placeholder="请输入"></Input>
          </Form.Item>
        );
      }
    },
    {
      title: '操作',
      dataIndex: 'operation',
      render: (_: any, record: field) => {
        return <Button onClick={() => removeRow(record.id)} type="text" icon={<IconDelete />}></Button>;
      }
    }
  ];
  // 删除
  const removeRow = (id: string) => {
    const newtableData = tableData.filter((item: field) => item.id !== id)
    setTableData(newtableData);
    form.setFieldValue('fields', newtableData);
  };
  // 添加
  const addRow = () => {
    const temp = {
      id: nanoid(),
      fieldName: '',
      fieldType: ''
    };
    const newtableData = [...tableData, temp];
    setTableData(newtableData);
    form.setFieldValue('fields', newtableData);
  };

  // 排序处理
  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    if (oldIndex !== newIndex) {
      const newtableData = form.getFieldValue('fields')
      const newData = arrayMove([...newtableData], oldIndex, newIndex).filter((el) => !!el);
      console.log('New Data: ', newData);
      setTableData(newData);
      form.setFieldValue('fields', newData);
    }
  };
  // 排序方法
  const arrayMoveMutate = (array: any[], from: number, to: number) => {
    const startIndex = to < 0 ? array.length + to : to;

    if (startIndex >= 0 && startIndex < array.length) {
      const item = array.splice(from, 1)[0];
      array.splice(startIndex, 0, item);
    }
  };
  const arrayMove = (array: any[], from: number, to: number) => {
    array = [...array];
    arrayMoveMutate(array, from, to);
    return array;
  };

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
        <SortableWrapper
          useDragHandle
          onSortEnd={onSortEnd}
          helperContainer={() => {
            let container: Element | null = null;
            container = document.querySelector(`#collect-field-config table tbody`);
            return (container as HTMLElement) || document.body;
          }}
          updateBeforeSortStart={({ node }) => {
            const tds = node.querySelectorAll('td');
            tds.forEach((td) => {
              td.style.width = td.clientWidth + 'px';
            });
          }}
          {...props}
        />
      ),
      row: (props: any) => {
        const { record, index, ...rest } = props;
        return <SortableItem index={index} {...rest} />;
      }
    }
  };
  return (
    <Form.Item field="fields" className={styles.collectFields} id='collect-field-config'>
      <Table
        className={styles.collectTable}
        rowKey="id"
        components={components}
        columns={columns}
        data={tableData}
        pagination={false}
      />
      <Button type="text" icon={<IconPlusCircle />} onClick={addRow} className={styles.addBtn}>
        添加收集字段
      </Button>
    </Form.Item>
  );
};

export default CollectFields;

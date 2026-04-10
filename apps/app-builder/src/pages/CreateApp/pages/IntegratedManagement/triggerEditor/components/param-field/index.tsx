import ResizableTable from '@/components/ResizableTable';
import { useEffect, useState } from 'react';
import { Button, Form, Input, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlusCircle } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import { nanoid } from 'nanoid';
import styles from './index.module.less';

interface Field {
  id: string;
  fieldName?: string;
  fieldValue?: string;
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

export interface ParamFieldsProps {
  data: Field[];
  form: FormInstance;
}

const ParamField: React.FC<ParamFieldsProps> = ({ data, form }) => {
  const [tableData, setTableData] = useState<Field[]>([]);

  const columns = [
    {
      title: '参数名',
      dataIndex: 'fieldName',
      width: '40%',
      render: (_: any, record: Field, index: number) => {
        return (
          <Form.Item
            field={`paramFields[${index}].fieldName`}
            noStyle
            rules={[
              {
                validator: (value, cb) => {
                  const paramFields = form.getFieldValue('paramFields');
                  const repeatFields = paramFields.filter((ele: Field) => ele.fieldName === value);
                  if (repeatFields.length > 1) {
                    return cb('字段名称不能重复');
                  }
                  return cb();
                }
              }
            ]}
          >
            <Input placeholder="请输入"></Input>
          </Form.Item>
        );
      }
    },
    {
      title: '参数值',
      dataIndex: 'fieldValue',
      width: '40%',
      render: (_: any, record: Field, index: number) => {
        return (
          <Form.Item field={`paramFields[${index}].fieldValue`} noStyle>
            <Input placeholder="请输入"></Input>
          </Form.Item>
        );
      }
    },
    {
      title: '操作',
      dataIndex: 'operation',
      render: (_: any, record: Field) => {
        return (
          <Button
            onClick={() => removeRow(record.id)}
            type="text"
            icon={<IconDelete style={{ fontSize: '15px', color: '#4E5969' }} />}
          ></Button>
        );
      }
    }
  ];

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    setTableData(data || []);
    form.setFieldValue('paramFields', data || []);
  };

  // 删除
  const removeRow = (id: string) => {
    const newData = form.getFieldValue('paramFields');
    const newtableData = newData.filter((item: Field) => item.id !== id);
    setTableData(newtableData);
    form.setFieldValue('paramFields', newtableData);
  };
  // 添加
  const addRow = () => {
    const newData = form.getFieldValue('paramFields');
    const temp = {
      id: nanoid().replace(/-/g, ''),
      fieldName: undefined,
      fieldValue: undefined
    };
    const newtableData = [...newData, temp];
    setTableData(newtableData);
    form.setFieldValue('paramFields', newtableData);
  };

  // 排序处理
  const onSortEnd = ({ oldIndex, newIndex }: { oldIndex: number; newIndex: number }) => {
    if (oldIndex !== newIndex) {
      const newtableData = form.getFieldValue('paramFields');
      const newData = arrayMove([...newtableData], oldIndex, newIndex).filter((el) => !!el);
      setTableData(newData);
      form.setFieldValue('paramFields', newData);
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
            container = document.querySelector(`#param-field-config table tbody`);
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
    <Form.Item field="paramFields" className={styles.paramFields} id="param-field-config">
      <ResizableTable rowKey="id" components={components} columns={columns} data={tableData} pagination={false} />
      <Button type="text" icon={<IconPlusCircle />} onClick={addRow} className={styles.addBtn}>
        添加收集字段
      </Button>
    </Form.Item>
  );
};

export default ParamField;

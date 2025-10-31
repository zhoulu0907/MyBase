import { useState } from 'react';
import { Radio, Button, Table, Link } from '@arco-design/web-react';
import { IconPlusCircle, IconDragDotVertical, IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { SortableContainer, SortableElement, SortableHandle } from 'react-sortable-hoc';
import './inner.style.less'
import ConditionModal from './ConditionModal';

const RadioGroup = Radio.Group;

const arrayMoveMutate = (array:Array<any>, from:number, to:number) => {
  const startIndex = to < 0 ? array.length + to : to;

  if (startIndex >= 0 && startIndex < array.length) {
    const item = array.splice(from, 1)[0];
    array.splice(startIndex, 0, item);
  }
};

const arrayMove = (array:Array<any>, from:number, to:number) => {
  array = [...array];
  arrayMoveMutate(array, from, to);
  return array;
};

export default function ConditionMode() {
    const columns = [
        {
            title: '审批人',
            dataIndex: 'name',
        },
        {
            title: '审批人生效条件',
            dataIndex: 'salary',
        },
        {
            title: '条件优先级',
            dataIndex: 'address',
        },
        {
            title: '操作',
            render: (_, record) => {
                return <>
                    <Link><IconEdit /></Link>
                    <Link><IconDelete /></Link>
                </>
            }
        },
    ];
    const initialData:any[] = [
        {
            key: '1',
            name: 'Jane Doe',
            salary: 23000,
            address: '32 Park Road, London',
            email: 'jane.doe@example.com',
        },
        {
            key: '2',
            name: 'Alisa Ross',
            salary: 25000,
            address: '35 Park Road, London',
            email: 'alisa.ross@example.com',
        },
        {
            key: '3',
            name: 'Kevin Sandra',
            salary: 22000,
            address: '31 Park Road, London',
            email: 'kevin.sandra@example.com',
        },
        {
            key: '4',
            name: 'Ed Hellen',
            salary: 17000,
            address: '42 Park Road, London',
            email: 'ed.hellen@example.com',
        },
        {
            key: '5',
            name: 'William Smith',
            salary: 27000,
            address: '62 Park Road, London',
            email: 'william.smith@example.com',
        },
    ];

    const [data, setData] = useState<any[]>(initialData);
    const [modalShow, setModalShow] = useState(false)

    const DragHandle = SortableHandle(() => (
        <IconDragDotVertical
            style={{
            cursor: 'move',
            color: '#555',
            }}
        />
    ));
    const SortableWrapper = SortableContainer((props:any) => {
        return <tbody {...props} />;
    });
    const SortableItem = SortableElement((props:any) => {
        return <tr {...props} />;
    });

    function onSortEnd({ oldIndex, newIndex }:any) {
        if (oldIndex !== newIndex) {
            let _arr:any = []
            const newData = arrayMove(_arr.concat(data), oldIndex, newIndex).filter((el) => !!el);
            setData(newData);
        }
    }
    const DraggableContainer = (props:any) => (
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

    const DraggableRow = (props:any) => {
        const { record, index, ...rest } = props;
        return <SortableItem index={index} {...rest} />;
    };
    const components = {
        header: {
            operations: ({ selectionNode, expandNode }:any) => [
                {
                    node: <th />,
                    width: 40,
                },
                {
                    name: 'expandNode',
                    node: expandNode,
                },
                {
                    name: 'selectionNode',
                    node: selectionNode,
                },
            ],
        },
        body: {
            operations: ({ selectionNode, expandNode }:any) => [
                {
                    node: (
                        <td>
                        <div className='arco-table-cell'>
                            <DragHandle />
                        </div>
                        </td>
                    ),
                    width: 40,
                },
                {
                    name: 'expandNode',
                    node: expandNode,
                },
                {
                    name: 'selectionNode',
                    node: selectionNode,
                },
            ],
            tbody: DraggableContainer,
            row: DraggableRow,
        },
    };

    return <>
        <div className="arco-card-header condition-mode" style={{border: 'none', padding: '0 0 16px 0'}}>
            <Button type='outline' className='gray-outline-btn' onClick={() => setModalShow(true)}><IconPlusCircle />添加一项</Button>
            <RadioGroup defaultValue='a'>
                <Radio value='a'>按优先级生效</Radio>
                <Radio value='b'>并行生效</Radio>
            </RadioGroup>
        </div>
        <Table
            className='arco-drag-table-container-2'
            pagination={false}
            components={components}
            columns={columns}
            data={data}
        />
        {modalShow && <ConditionModal modalShow={modalShow} setModalShow={setModalShow}/>}
    </>
}
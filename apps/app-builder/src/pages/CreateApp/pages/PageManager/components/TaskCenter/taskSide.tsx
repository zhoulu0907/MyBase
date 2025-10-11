import { useCallback, useEffect, useState, type FC } from 'react';
import {SortableContainer, SortableElement} from 'react-sortable-hoc';
import {arrayMoveImmutable} from 'array-move'
import { IconSettings, IconDragDotVertical } from '@arco-design/web-react/icon';
import willdoIcon from '@/assets/images/task_center/willdo.svg'
import './taskSide.less'

interface SortableItemProps {
  value: Object;
  index: number;
}
interface SortableListProps {
  items: Object[];
  onSortEnd: (params: { oldIndex: number; newIndex: number }) => void;
}
const SortableItem = SortableElement<SortableItemProps>(({value}:any) =>
  <li className='arco-tree-node sortable-line' onClick={() => handleSortList(value)}>
    <span className='arco-tree-node-switcher'></span>
    <div className='task-sortable-menu' onClick={() => handleSortList(value)}>
        <div className='left-part'>
            <img className='sort-menu-img' src={willdoIcon} alt=''/>
            <span>{value?.name}</span>
        </div>
        <div className='right-part'>
            <IconSettings />
            <IconDragDotVertical />
        </div>
    </div>
  </li>
);

function handleSortList(value:any) {
    console.log(' 99 00 ----', value)
}

const SortableList = SortableContainer<SortableListProps>(({items}:any) => {
  return (
    <ul className='move-box-ul'>
      {items.map((value:any, index:number) => (
        <SortableItem key={`item-${index}`} index={index} value={value} />
      ))}
    </ul>
  );
});

interface ComProps {
  setCurMenu: any;
}
const TaskCenterSide:FC<ComProps> = ({setCurMenu}:any) => {
    let [state, setState] = useState({
        items: [
            {name: '待我处理', sign: 'willdo'}, 
            {name: '我已处理', sign: 'done'}, 
            {name: '我创建的', sing: 'mycreate'}, 
            {name: '抄送我的', sign: 'copyme'}, 
            {name: '流程代理', sign: 'taskproxy'}
        ],
    });
    const onSortEnd = ({oldIndex, newIndex}:any) => {
        setState({
            items: arrayMoveImmutable(state.items, oldIndex, newIndex),
        });
    };
    function handleSortList2() {
        // undefined、{id: 'xxxx'}
        if (setCurMenu) {
            setCurMenu(undefined)
        }
    }
    return <section className='task-center-box'>
        <SortableList items={state.items} onSortEnd={onSortEnd} />
    </section>
}

export default TaskCenterSide;
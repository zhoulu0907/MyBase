import { useCallback, useEffect, useState, type FC } from 'react';
import {SortableContainer, SortableElement, arrayMove} from 'react-sortable-hoc';
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
  <li>{value?.name}</li>
);

const SortableList = SortableContainer<SortableListProps>(({items}:any) => {
  return (
    <ul className='move-box-ul'>
      {items.map((value:any, index:number) => (
        <SortableItem key={`item-${index}`} index={index} value={value} />
      ))}
    </ul>
  );
});

const TaskCenterSide:FC = () => {
  let [state, setState] = useState({
    items: [{name: '待我处理', sign: 'willdo'}, {name: '我已处理', sign: 'done'}, {name: '我创建的', sing: 'mycreate'}, {name: '抄送我的', sign: 'copyme'}, {name: '流程代理', sign: 'taskproxy'}],
  });
  const onSortEnd = ({oldIndex, newIndex}:any) => {
    setState({
      items: arrayMove(state.items, oldIndex, newIndex),
    });
  };
    return <section className='task-center-box'>
        <SortableList items={state.items} onSortEnd={onSortEnd} />
    </section>
}

export default TaskCenterSide;
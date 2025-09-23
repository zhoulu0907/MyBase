// src/components/SortableTable.tsx

import React from 'react';
import { Table } from '@arco-design/web-react';
import { DndContext, closestCenter, KeyboardSensor, PointerSensor, useSensor, useSensors } from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  sortableKeyboardCoordinates,
  verticalListSortingStrategy,
  useSortable
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { IconDragDotVertical } from '@arco-design/web-react/icon';

interface SortableTableProps<T> {
  data: T[];
  columns: any[];
  rowKey?: string | ((record: T) => string);
  onSort?: (newData: T[]) => void;
  className?: string;
  pagination?: boolean | object;
  disabledRowKeys?: string[];
}

// 自定义可排序行
const SortableTableRow = React.memo(({ record, className, children, disabledRowKeys = [], rowKey, ...restProps }) => {
  const id = typeof rowKey === 'function' ? rowKey(record) : record[rowKey];
  const isDisabled = disabledRowKeys.includes(String(id));

  const { setNodeRef, transform, transition, isDragging } = useSortable({
    id: String(id),
    disabled: isDisabled
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
    zIndex: isDragging ? 1 : undefined,
    position: 'relative' as const
  };

  return (
    <tr ref={setNodeRef} style={style} className={className} {...restProps}>
      {children}
    </tr>
  );
});

// 拖拽手柄组件
const DragHandle: React.FC<{ id: string; disabled?: boolean }> = ({ id, disabled }) => {
  const { attributes, listeners, isDragging, setActivatorNodeRef, active } = useSortable({ id, disabled });

  return (
    <span
      {...attributes}
      {...listeners}
      ref={setActivatorNodeRef}
      style={{
        cursor: disabled ? 'not-allowed' : 'grab',
        opacity: isDragging ? 0.4 : 1,
        display: 'inline-flex',
        alignItems: 'center'
      }}
    >
      <IconDragDotVertical />
    </span>
  );
};

const SortableTable = <T extends Record<string, any>>({
  data,
  columns,
  rowKey = 'id',
  onSort,
  className,
  pagination = false,
  disabledRowKeys = []
}: SortableTableProps<T>) => {
  const ids = data.map((record) => {
    const key = typeof rowKey === 'function' ? rowKey(record) : record[rowKey];
    return String(key);
  });

  function customCoordinatesGetter(event, args) {
    const {currentCoordinates} = args;
    const delta = 50;
    console.log('customCoordinatesGetter', event, args);
    switch (event.code) {
      case 'Right':
        return {
          ...currentCoordinates,
          x: currentCoordinates.x + delta,
        };
      case 'Left':
        return {
          ...currentCoordinates,
          x: currentCoordinates.x - delta,
        };
      case 'Down':
        return {
          ...currentCoordinates,
          y: currentCoordinates.y + delta,
        };
      case 'Up':
        return {
          ...currentCoordinates,
          y: currentCoordinates.y - delta,
        };
    }
  
    return undefined;
  };

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
    useSensor(KeyboardSensor, { coordinateGetter: customCoordinatesGetter }),
  );

  const handleDragEnd = (event: any) => {
    const { active, over } = event;
    if (!onSort || !over || active.id === over.id) return;

    const oldIndex = ids.indexOf(active.id);
    const newIndex = ids.indexOf(over.id);
    if (oldIndex === -1 || newIndex === -1) return;

    const newData = arrayMove(data, oldIndex, newIndex);
    onSort(newData);
  };

  // 自动加一列拖拽手柄
  const enhancedColumns = [
    {
      title: '',
      dataIndex: '__dragHandle__',
      width: 50,
      render: (_: any, record: T) => {
        const id = String(typeof rowKey === 'function' ? rowKey(record) : record[rowKey]);
        return <DragHandle id={id} disabled={disabledRowKeys.includes(id)} />;
      }
    },
    ...columns
  ];

  return (
    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <SortableContext items={ids} strategy={verticalListSortingStrategy}>
        <Table
          data={data}
          columns={enhancedColumns}
          rowKey={rowKey}
          pagination={pagination}
          className={className}
          components={{
            body: {
              row: (props) => <SortableTableRow {...props} rowKey={rowKey} disabledRowKeys={disabledRowKeys} />
            }
          }}
        />
      </SortableContext>
    </DndContext>
  );
};

export default SortableTable;

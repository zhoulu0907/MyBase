/**
 * 工作台组件列表组件
 */
import MaterialCard from '@/components/MaterialCard';
import { COMPONENT_GROUP_NAME } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';
import type { WorkbenchItem } from '../../types/workbench';
import styles from './index.module.less';

interface ComponentListProps {
  items: WorkbenchItem[];
  components: WorkbenchItem[];
  onItemsChange: (items: WorkbenchItem[]) => void;
}

export function ComponentList({ items, components, onItemsChange }: ComponentListProps) {
  if (components.length === 0) {
    return <div className={styles.emptyTip}>暂无组件</div>;
  }

  return (
    <ReactSortable
      list={items}
      setList={onItemsChange}
      group={{
        name: COMPONENT_GROUP_NAME,
        pull: 'clone',
        put: false
      }}
      sort={false}
      className={styles.componentCollapseContent}
      forceFallback={true}
      animation={150}
      onClone={(e) => {
        const cpType = e.item.getAttribute('data-cp-type');
        e.item.id = `${cpType}-${uuidv4()}`;

        onItemsChange(items.map((item) => (item.type === cpType ? { ...item, id: e.item.id } : item)));
      }}
    >
      {components.map((item) => (
        <MaterialCard
          key={item.type}
          id={item.id}
          displayName={item.displayName}
          type={item.type}
          icon={item.icon || ''}
          layout="column"
        />
      ))}
    </ReactSortable>
  );
}

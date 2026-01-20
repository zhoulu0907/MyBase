/**
 * 工作台组件列表组件
 */
import MaterialCard from '@/components/MaterialCard';
import { COMPONENT_GROUP_NAME, WORKBENCH_COMPONENT_TYPES, useWorkbenchSignal } from '@onebase/ui-kit';
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
  const { workbenchComponents } = useWorkbenchSignal();

  // 过滤掉按钮组件
  const sortableItems = items.filter((item) => item.type !== WORKBENCH_COMPONENT_TYPES.BUTTON_WORKBENCH);
  const displayComponents = components.filter((item) => item.type !== WORKBENCH_COMPONENT_TYPES.BUTTON_WORKBENCH);

  if (displayComponents.length === 0) {
    return <div className={styles.emptyTip}>暂无组件</div>;
  }

  // 检查workspace中是否已经存在快捷入口、欢迎卡片组件（仅能拖入一个）
  const hasQuickEntry = workbenchComponents?.some((cp) => cp.type === WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY);
  const hasWelcomeCard = workbenchComponents?.some((cp) => cp.type === WORKBENCH_COMPONENT_TYPES.WELCOME_CARD);

  return (
    <ReactSortable
      list={sortableItems}
      setList={onItemsChange}
      group={{
        name: COMPONENT_GROUP_NAME,
        pull: 'clone',
        put: false
      }}
      filter=".disabled-drag"
      sort={false}
      className={styles.componentCollapseContent}
      forceFallback={true}
      animation={150}
      onClone={(e) => {
        const cpType = e.item.getAttribute('data-cp-type');
        e.item.id = `${cpType}-${uuidv4()}`;

        onItemsChange(sortableItems.map((item) => (item.type === cpType ? { ...item, id: e.item.id } : item)));
      }}
    >
      {displayComponents.map((item) => {
        // 如果是快捷入口、欢迎卡片组件且workspace中已存在，则禁用拖拽
        const isQuickEntryDisabled = item.type === WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY && hasQuickEntry;
        const isWelcomeCardDisabled = item.type === WORKBENCH_COMPONENT_TYPES.WELCOME_CARD && hasWelcomeCard;
        return (
          <MaterialCard
            key={item.type}
            id={item.id}
            displayName={item.displayName}
            type={item.type}
            icon={item.icon || ''}
            layout="column"
            disabled={isQuickEntryDisabled || isWelcomeCardDisabled}
          />
        );
      })}
    </ReactSortable>
  );
}

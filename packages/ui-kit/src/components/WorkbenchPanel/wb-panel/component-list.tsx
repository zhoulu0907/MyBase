/**
 * 工作台组件列表组件
 */
import MaterialCard from '../../Panel/components/MaterialCard';
import { COMPONENT_GROUP_NAME } from '@/utils';
import { ReactSortable } from 'react-sortablejs';
import { v4 as uuidv4 } from 'uuid';
import type { WorkbenchItem } from '../types/workbench';
import styles from './index.module.css';
import { WORKBENCH_COMPONENT_TYPES } from '@/components/Materials/Workbench/core/componentTypes';

interface ComponentListProps {
  items: WorkbenchItem[];
  components: WorkbenchItem[];
  onItemsChange: (items: WorkbenchItem[]) => void;
  currentComponents: WorkbenchItem[];
}

export function ComponentList({ items, components, onItemsChange, currentComponents }: ComponentListProps) {
  // 移动端组件库过滤数据列表组件
  const displayComponents = components.filter((item) => item.type !== WORKBENCH_COMPONENT_TYPES.DATA_LIST);

  if (displayComponents.length === 0) {
    return <div className={styles.emptyTip}>暂无组件</div>;
  }

  // 检查workspace中是否已经存在快捷入口、欢迎卡片组件（仅能拖入一个）
  const hasQuickEntry = currentComponents?.some((cp) => cp.type === WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY);
  const hasWelcomeCard = currentComponents?.some((cp) => cp.type === WORKBENCH_COMPONENT_TYPES.WELCOME_CARD);
 
  return (
    <ReactSortable
      list={displayComponents}
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
        const clonedId = `${cpType}-${uuidv4()}`;

        e.item.id = clonedId;
        e.item.setAttribute('data-cp-id', clonedId);
      }}
    >
      {displayComponents.map((item) => {
        // 如果是快捷入口、欢迎卡片组件且workspace中已存在，则禁用拖拽
        const isQuickEntryDisabled = item.type === WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY && hasQuickEntry;
        const isWelcomeCardDisabled = item.type === WORKBENCH_COMPONENT_TYPES.WELCOME_CARD && hasWelcomeCard;
        return (<MaterialCard
          key={item.type}
          id={item.id}
          displayName={item.displayName}
          type={item.type}
          icon={item.icon || ''}
          layout="column"
          disabled={isQuickEntryDisabled || isWelcomeCardDisabled}
        />)
      })}
    </ReactSortable>
  );
}

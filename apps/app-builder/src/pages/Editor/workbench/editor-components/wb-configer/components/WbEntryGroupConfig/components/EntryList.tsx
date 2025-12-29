import { Button, Dropdown, Input, Menu, Popconfirm } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconMenu, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import type { EntryItem } from '../types';
import styles from '../index.module.less';

interface EntryListProps {
  entries: EntryItem[];
  showAddMenu: boolean;
  onSortChange: (newList: EntryItem[]) => void;
  onEdit: (entry: EntryItem) => void;
  onDelete: (entryId: string) => void;
  onAddEntry: (type: 'menu' | 'link') => void;
  onEditEntry: (entryId: string, field: string, value: string) => void;
  onShowAddMenuChange: (visible: boolean) => void;
}

export const EntryList = ({
  entries,
  showAddMenu,
  onSortChange,
  onEdit,
  onDelete,
  onAddEntry,
  onEditEntry,
  onShowAddMenuChange
}: EntryListProps) => {
  const addEntryMenu = (
    <Menu>
      <Menu.Item key="menu" onClick={() => onAddEntry('menu')}>
        <IconMenu style={{ marginRight: 8 }} />
        应用菜单
      </Menu.Item>
      <Menu.Item key="link" onClick={() => onAddEntry('link')}>
        <IconMenu style={{ marginRight: 8 }} />
        外部链接
      </Menu.Item>
    </Menu>
  );

  return (
    <>
      <ReactSortable
        list={entries.map((entry) => ({ ...entry, id: entry.entryId }))}
        setList={onSortChange}
        handle=".drag-handle"
        animation={200}
      >
        {entries.map((entry) => (
          <div key={entry.entryId} className={styles.entryItem}>
            <div className={`${styles.dragHandle} drag-handle`}>
              <IconDragDotVertical />
            </div>
            <div className={styles.entryContent}>
              <Input
                value={entry.entryName}
                onChange={(value) => onEditEntry(entry.entryId, 'entryName', value)}
                placeholder="请输入入口名称"
              />
            </div>
            <div className={styles.entryActions}>
              <IconEdit className={styles.actionIcon} onClick={() => onEdit(entry)} />
              <Popconfirm title="确定要删除这个入口吗？" onOk={() => onDelete(entry.entryId)}>
                <IconDelete className={styles.actionIcon} />
              </Popconfirm>
            </div>
          </div>
        ))}
      </ReactSortable>
      <Dropdown
        droplist={addEntryMenu}
        trigger="click"
        position="bl"
        popupVisible={showAddMenu}
        onVisibleChange={onShowAddMenuChange}
      >
        <Button type="outline" className={styles.addEntryBtn} icon={<IconPlus />}>
          添加入口
        </Button>
      </Dropdown>
    </>
  );
};


import { Button, Input, Popconfirm } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import type { EntryItem } from '../types';
import styles from '../index.module.less';

interface EntryListProps {
  entries: EntryItem[];
  onSortChange: (newList: EntryItem[]) => void;
  onEdit: (entry: EntryItem) => void;
  onDelete: (entryId: string) => void;
  onAddEntry: () => void;
  onEditEntry: (entryId: string, field: string, value: string) => void;
}

export const EntryList = ({ entries, onSortChange, onEdit, onDelete, onAddEntry, onEditEntry }: EntryListProps) => {
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

      <Button type="outline" className={styles.addEntryBtn} icon={<IconPlus />} onClick={onAddEntry}>
        添加入口
      </Button>
    </>
  );
};

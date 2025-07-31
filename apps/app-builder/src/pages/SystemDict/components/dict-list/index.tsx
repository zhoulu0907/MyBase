import ListItemWithDropdown from '@/components/ListItemWithDropdown';
import { Menu } from '@arco-design/web-react';
import { type DictItem } from '@onebase/platform-center';
import styles from '../../index.module.less';
import ListActions from './ListActions';
import ListSearch from './ListSearch';

interface DictionaryListProps {
  list: DictItem[];
  activeId: string;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onSelect: (id: string) => void;
  onAdd: () => void;
  onImport: () => void;
  onEdit: (vakue: DictItem) => void;
  onDelete: (value: string | undefined) => void
}

export default function DictionaryListProps({
  list,
  activeId,
  onSelect,
  searchValue,
  onSearchChange,
  onAdd,
  onImport,
  onEdit,
  onDelete,
}: DictionaryListProps) {
  return (
    <>
      <div className={styles.searchInput}>
        <ListSearch value={searchValue} onChange={onSearchChange} />
      </div>
      <div style={{ marginBottom: 16 }}>
        <ListActions onAdd={onAdd} onImport={onImport} />
      </div>
      <div className={styles.dictList}>
        {list?.map((item) => (
          <ListItemWithDropdown
            key={item.id}
            title={item.name}
            droplist={<Menu>
              <Menu.Item key='edit' onClick={() => onEdit(item)}>编辑</Menu.Item>
              <Menu.Item key='delete' onClick={() => onDelete(item.id)}>删除</Menu.Item>
            </Menu>}
            active={item.id?.toString() === activeId}
            onClick={() => onSelect(item.id?.toString() || '')}
          >
          </ListItemWithDropdown>
        ))}
      </div>
    </>
  );
}
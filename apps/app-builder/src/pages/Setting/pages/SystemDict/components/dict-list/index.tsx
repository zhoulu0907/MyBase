import ListItem from '@/components/ListItem';
import { Button, Input } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { type DictItem } from '@onebase/platform-center';
import styles from '../../index.module.less';

interface DictionaryListProps {
  list: DictItem[];
  activeId: number | undefined;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onSelect: (id: number | undefined) => void;
  onAdd: () => void;
}

export default function DictionaryListProps({
  list,
  activeId,
  onSelect,
  searchValue,
  onSearchChange,
  onAdd
}: DictionaryListProps) {
  const listTitle = `全部(${list?.length})`;
  return (
    <>
      <div className={styles.searchInput}>
        <Input.Search
          value={searchValue}
          onChange={onSearchChange}
          placeholder="输入字典名称"
          allowClear
          style={{ borderRadius: 24 }}
        />
      </div>
      <ListItem title={listTitle}>
        <Button type="text" onClick={onAdd} style={{ paddingLeft: '8px', paddingRight: '8px' }}>
          <IconPlus />
          新建
        </Button>
      </ListItem>
      <div className={styles.dictList}>
        {list?.map((item) => (
          <ListItem
            key={item.id}
            title={item.name}
            active={item.id === activeId}
            onClick={() => onSelect(item.id)}
          ></ListItem>
        ))}
      </div>
    </>
  );
}

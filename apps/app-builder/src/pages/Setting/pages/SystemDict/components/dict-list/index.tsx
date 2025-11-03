import ListItem from '@/components/ListItem';
import { Input } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { type DictItem } from '@onebase/platform-center';
import styles from '../../index.module.less';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import { hasPermission } from '@/utils/permission';
import { TENANT_DICT_PERMISSION as ACTIONS } from '@/constants/permission';
import { PermissionButton as Button } from '@/components/PermissionControl';
import StatusTag from '@/components/StatusTag';

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
      <Input.Search
        className={styles.searchInput}
        value={searchValue}
        onChange={onSearchChange}
        placeholder="输入字典名称"
        allowClear
      />
      <ListItem title={listTitle}>
        <Button
          permission={ACTIONS.CREATE}
          type="text"
          onClick={onAdd}
          style={{ paddingLeft: '8px', paddingRight: '8px' }}
        >
          <IconPlus />
          新建
        </Button>
      </ListItem>
      <PlaceholderPanel className={styles.dictList} hasPermission={hasPermission(ACTIONS.QUERY)}>
        {list?.map((item) => (
          <ListItem
            key={item.id}
            title={item.name}
            active={item.id === activeId}
            onClick={() => onSelect(item.id)}
            children={<StatusTag status={item.status} type="tag" />}
          ></ListItem>
        ))}
      </PlaceholderPanel>
    </>
  );
}

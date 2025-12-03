import ListItem from '@/components/ListItem';
import { PermissionButton as Button } from '@/components/PermissionControl';
import PlaceholderPanel from '@/components/PlaceholderPanel';
import StatusTag from '@/components/StatusTag';
import { Input } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { TENANT_DICT_PERMISSION as ACTIONS, hasPermission } from '@onebase/common';
import { type DictItem } from '@onebase/platform-center';
import styles from '../../index.module.less';

interface DictionaryListProps {
  list: DictItem[];
  activeId: string | undefined;
  searchValue: string;
  onSearchChange: (value: string) => void;
  onSelect: (id: string | undefined) => void;
  onAdd: () => void;
  isHideTenantAddDictButton?: boolean;
}

export default function DictionaryListProps({
  list,
  activeId,
  onSelect,
  searchValue,
  onSearchChange,
  onAdd,
  isHideTenantAddDictButton = false
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
        {!isHideTenantAddDictButton && (
          <Button
            permission={ACTIONS.CREATE}
            type="text"
            onClick={onAdd}
            style={{ paddingLeft: '8px', paddingRight: '8px' }}
          >
            <IconPlus />
            新建
          </Button>
        )}
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

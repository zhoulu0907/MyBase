import { Input, Button } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import s from '../../index.module.less';
interface TableHeaderProps {
  searchValue: string;
  onSearchChange: (value: string) => void;
  onAdd: () => void;
}
export function TableHeader({ searchValue, onSearchChange, onAdd }: TableHeaderProps) {
  return (
    <div className={s.tableHeader}>
      <Button type="primary" onClick={onAdd}>
        <IconPlus />
        添加
      </Button>
      <Input.Search
        value={searchValue}
        onChange={onSearchChange}
        placeholder="搜索字典项"
        style={{ width: 200 }}
        allowClear
      />
    </div>
  );
}

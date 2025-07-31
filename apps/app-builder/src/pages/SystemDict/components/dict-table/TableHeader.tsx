import { Input, Button, Space } from '@arco-design/web-react';
import s from '../../index.module.less';
interface TableHeaderProps {
  searchValue: string;
  onSearchChange: (value: string) => void;
  onAdd: () => void;
}
export function TableHeader({ searchValue, onSearchChange, onAdd }: TableHeaderProps) {
  return (
    <div className={s.tableHeader}>
      <div>数据字典列表</div>
      <div>
        <Space>
          <Input.Search
            value={searchValue}
            onChange={onSearchChange}
            placeholder="搜索字典项"
            style={{ width: 200 }}
            allowClear
          />
          <Button type="primary" onClick={onAdd}>
            添加
          </Button>
        </Space>
      </div>
    </div>
  );
} 
import { Input, Tree } from '@arco-design/web-react';
import ListItem from '@/components/ListItem';
import { useMemo, useState, useEffect } from 'react';
import { treeFilter } from '@/utils/tree';
import s from '../index.module.less';
import { type DeptTree } from '@onebase/platform-center';

type TreeDataType = {
  key?: string;
  _index?: number;
  children?: TreeDataType[];
  [key: string]: any;
};

interface DeptTreeProps {
  selectedDeptId?: number;
  onDeptSelect: (deptId?: number) => void;
  totalUserCount: number;
  treeData: any[];
  deptLoading?: boolean;
}

export default function DeptTree({ selectedDeptId, onDeptSelect, totalUserCount, treeData }: DeptTreeProps) {
  const [search, setSearch] = useState('');
  const [selectedKeys, setSelectedKeys] = useState<string[]>(selectedDeptId ? [String(selectedDeptId)] : []);

  const filteredTree = useMemo(() => treeFilter(treeData, search), [treeData, search]);

  // 同步外部选中状态
  useEffect(() => {
    const keys = selectedDeptId ? [String(selectedDeptId)] : [];
    setSelectedKeys(keys);
  }, [selectedDeptId]);

  const handleSelect = (keys: string[]) => {
    const deptId = keys.length > 0 ? Number(keys[0]) : undefined;
    onDeptSelect(deptId);
  };

  return (
    <div>
      <Input.Search
        placeholder="输入部门名称"
        allowClear
        value={search}
        onChange={setSearch}
        style={{ marginBottom: 12 }}
      />
      <ListItem
        onClick={() => {
          setSelectedKeys([]);
          onDeptSelect(undefined);
        }}
        title={`全部（${totalUserCount}）`}
        active={!selectedDeptId}
      />
      <Tree
        treeData={filteredTree}
        selectedKeys={selectedKeys}
        onSelect={handleSelect}
        blockNode
        className={s.deptTreeNode}
        renderTitle={(node: TreeDataType) => {
          return <span className="tableColumnUsername">{`${node.title}(${node.userCount || 0})`}</span>;
        }}
      />
    </div>
  );
}

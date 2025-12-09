import ListItem from '@/components/ListItem';
import { treeFilter } from '@/utils/tree';
import { Input, Tree } from '@arco-design/web-react';
import { type DeptTree } from '@onebase/platform-center';
import { useEffect, useMemo, useState } from 'react';
import s from '../index.module.less';

type TreeDataType = {
  key?: string;
  _index?: number;
  children?: TreeDataType[];
  [key: string]: any;
};

interface DeptTreeProps {
  selectedDeptId?: string;
  onDeptSelect: (deptId?: string) => void;
  totalUserCount?: number;
  treeData: DeptTree[];
  deptLoading?: boolean;
}

const DeptTreeCmp: React.FC<DeptTreeProps> = ({ selectedDeptId, onDeptSelect, totalUserCount = 0, treeData }) => {
  const [search, setSearch] = useState('');
  const [selectedKeys, setSelectedKeys] = useState<string[]>(selectedDeptId ? [String(selectedDeptId)] : []);

  const filteredTree = useMemo(() => treeFilter(treeData, search), [treeData, search]);

  // 同步外部选中状态
  useEffect(() => {
    const keys = selectedDeptId ? [String(selectedDeptId)] : [];
    setSelectedKeys(keys);
  }, [selectedDeptId]);

  const handleSelect = (keys: string[]) => {
    const deptId = keys.length > 0 ? keys[0] : undefined;
    onDeptSelect(deptId);
  };

  return (
    <div>
      <Input.Search
        className={s.inputSearch}
        placeholder="输入部门名称"
        allowClear
        value={search}
        onChange={setSearch}
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
};

export default DeptTreeCmp;

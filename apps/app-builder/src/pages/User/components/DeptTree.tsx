import { Input, Tree } from '@arco-design/web-react';
import React, { useMemo, useState } from 'react';
import s from '../index.module.less';

interface DeptTreeProps {
  selectedDeptId?: number;
  onDeptSelect: (deptId?: number) => void;
  totalUserCount: number;
  treeData: any[];
  deptLoading?: boolean; // 部门数据加载状态
}

export default function DeptTree({ selectedDeptId, onDeptSelect, totalUserCount, treeData }: DeptTreeProps) {
  const [search, setSearch] = useState('');
  const [selectedKey, setSelectedKey] = useState<string | undefined>(selectedDeptId ? String(selectedDeptId) : undefined);

  const filterTreeData = (list: any[], keyword: string): any[] => {
    if (!keyword) return list;
    return list
      .map(item => {
        const titleText = typeof item.title === 'string' ? item.title : item.title.props?.children[0];
        const match = titleText.includes(keyword);
        const children = item.children ? filterTreeData(item.children, keyword) : undefined;
        if (match || (children && children.length > 0)) {
          return { ...item, children };
        }
        return null;
      })
      .filter(Boolean) as any[];
  };

  const filteredTree = useMemo(() => filterTreeData(treeData, search), [treeData, search]);

  React.useEffect(() => {
    setSelectedKey(selectedDeptId ? String(selectedDeptId) : undefined);
  }, [selectedDeptId]);

  return (
    <div>
      <Input.Search
        placeholder="输入部门名称"
        allowClear
        value={search}
        onChange={setSearch}
        style={{ marginBottom: 12 }}
      />
      <div
        className={selectedDeptId === undefined ? s.selected : s.allItem}
        style={{ marginBottom: 8, cursor: 'pointer' }}
        onClick={() => onDeptSelect(undefined)}
      >
        全部（{totalUserCount}）
      </div>
      <Tree
        treeData={filteredTree}
        selectedKeys={selectedKey ? [selectedKey] : []}
        onSelect={keys => {
          const findDeptId = (nodes: any[], key: string): number | undefined => {
            for (const node of nodes) {
              if (node.key === key) {
                return Number(node.key);
              }
              if (node.children) {
                const found = findDeptId(node.children, key);
                if (found) return found;
              }
            }
            return undefined;
          };

          const deptId = findDeptId(treeData, keys[0]);
          if (deptId) onDeptSelect(deptId);
        }}
        blockNode
      />
    </div>
  );
}
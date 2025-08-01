import type { DeptVO, DeptTree } from '@onebase/platform-center/src/types/dept';

// 将部门列表转换为树形结构（用于部门树展示）
export const convertDeptListToTree = (list: DeptTree[]): any[] => {
  return list.map((item) => ({
    key: String(item.id),
    title: `${item.name}（${item.userCount}）`,
    children: item.children ? convertDeptListToTree(item.children) : undefined,
    isLeaf: !item.children,
  }));
};

// 将部门列表转换为树形结构（用于TreeSelect选择框）
export const convertDeptListToSelectTree = (deptList: DeptVO[]): any[] => {
  // 构建ID到部门的映射
  const deptMap = new Map<number, any>();
  deptList.forEach(dept => {
    deptMap.set(dept.id, {
      ...dept,
      key: dept.id,
      value: dept.id,
      title: dept.name,
      children: []
    });
  });

  // 构建树形结构
  const tree: any[] = [];
  deptList.forEach(dept => {
    const deptNode = deptMap.get(dept.id);
    if (dept.parentId === 0) {
      // 根节点
      tree.push(deptNode);
    } else {
      // 子节点
      const parent = deptMap.get(dept.parentId);
      if (parent) {
        parent.children.push(deptNode);
      }
    }
  });
  console.log(tree)

  return tree;
};

// 根据key查找部门ID
export const findDeptIdByKey = (list: DeptTree[], key: string): number | undefined => {
  for (const item of list) {
    if (String(item.id) === key) return item.id;
    if (item.children) {
      const found = findDeptIdByKey(item.children, key);
      if (found) return found;
    }
  }
  return undefined;
};
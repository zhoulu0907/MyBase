import { create } from 'zustand';
import type { DeptTree } from '@onebase/platform-center';

//用户管理下的部门树支持拖拽, 需要把拖拽之后形成新的部门树同步到组织管理table中
export interface deptTreeStore {
  // 当前的部门树
  deptTreeList: DeptTree[];
  // 设置当前部门树
  seDeptTreeList: (deptTreeList: DeptTree[]) => void;
  // 清除当前部门树
  clearDeptTreeList: () => void;
}

export const useDeptTreeListStore = create<deptTreeStore>((set) => ({
  deptTreeList: [],
  seDeptTreeList: (deptTreeList: DeptTree[]) => set(() => ({ deptTreeList })),
  clearDeptTreeList: () => set(() => ({ deptTreeList: [] }))
}));

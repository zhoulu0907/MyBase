import { type DeptForm, type DeptVO } from '@onebase/platform-center';

// 格式化数据
export const formatDeptAndUsers = (data: DeptForm) => {
  const deptNodes =
    data?.deptList?.map((d: DeptVO) => ({
      ...d,
      key: d.id,
      title: d.name
    })) || null;

  return {
    ...data?.deptInfo,
    children: deptNodes
  };
}

// 获取多选数据
export const getDeptData = (data: any[], deptIds: string[]) => {
  const map = new Map(data.map(item => [item.id, item]));
  const result = deptIds
    .map(id => map.get(id))
    ?.filter(Boolean)
    .map(item => ({
      deptId: item.id,
      deptName: item.name,
    }));
  return deptIds.length === 1 ? result[0] : result;
};

export const parseDeptName = (data: any[], deptIds: string[]) => {
  const deptNames = data
    ?.filter(item => deptIds.includes(item.id))
    .map(item => item.name) || [];

  return deptNames.join('，');
}
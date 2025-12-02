import { type DeptForm, type DeptVO } from '@onebase/platform-center';

// 格式化数据
export const formatDeptAndUsers = (data: DeptForm) => {
  const deptNodes =
    data?.deptList?.map((d: DeptVO) => ({
      ...d,
      key: d.id,
      title: d.name
    })) || [];

  return {
    ...data?.deptInfo,
    children: deptNodes
  };
}
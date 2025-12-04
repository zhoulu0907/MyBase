import { type PageParam } from '../types/common';
import { type DeptForm, type DeptVO, type UpdateAdminOrDirectorReq } from '../types/dept';
import { runtimeCorpService } from './clients';

// 查询部门（精简)列表
export const getCorpSimpleDeptList = (): Promise<Partial<DeptVO>[]> => {
  return runtimeCorpService.get('/dept/simple-list');
};

// 查询部门列表
export const getCorpDeptList = (params?: any) => {
  return runtimeCorpService.get('/dept/list', params);
};

// 查询部门分页
export const getCorpDeptPage = (params: PageParam) => {
  return runtimeCorpService.get('/dept/list', params);
};

// 查询部门详情
export const getCorpDept = (id: number) => {
  return runtimeCorpService.get('/dept/get?id=' + id);
};

// 新增部门
export const createCorpDept = (data: DeptForm) => {
  return runtimeCorpService.post('/dept/create', data);
};

// 修改部门
export const updateCorpDept = (data: DeptForm) => {
  return runtimeCorpService.post('/dept/update', data);
};

// 删除部门
export const deleteCorpDept = (id: number) => {
  return runtimeCorpService.post('/dept/delete?id=' + id);
};

// 批量删除部门
export const deleteCorpDeptList = (ids: number[]) => {
  return runtimeCorpService.post('/dept/delete-list', { ids });
};

// 修改用户管理员/主管
export const updateCorpAdminOrDirector = (data: UpdateAdminOrDirectorReq) => {
  return runtimeCorpService.post('/dept/update-dept-admin-or-director', data);
};

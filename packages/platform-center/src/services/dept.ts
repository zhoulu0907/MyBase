import { type PageParam } from '../types/common';
import { GetDeptsByIdReq, type DeptForm, type DeptVO, type UpdateAdminOrDirectorReq } from '../types/dept';
import { systemService } from './clients';

// 查询部门（精简)列表
export const getSimpleDeptList = (): Promise<Partial<DeptVO>[]> => {
  return systemService.get('/dept/simple-list');
};

// 查询部门列表
export const getDeptList = (params?: any) => {
  return systemService.get('/dept/list', params);
};

// 查询部门分页
export const getDeptPage = async (params: PageParam) => {
  return await systemService.get('/dept/list', params);
};

// 查询部门详情
export const getDept = (id: number) => {
  return systemService.get('/dept/get?id=' + id);
};

// 新增部门
export const createDept = (data: DeptForm) => {
  return systemService.post('/dept/create', data);
};

// 修改部门
export const updateDept = (data: DeptForm) => {
  return systemService.post('/dept/update', data);
};

// 删除部门
export const deleteDept = async (id: number) => {
  return await systemService.post('/dept/delete?id=' + id);
};

// 批量删除部门
export const deleteDeptList = async (ids: number[]) => {
  return await systemService.post('/dept/delete-list', { ids });
};

// 修改用户管理员/主管
export const updateAdminOrDirector = (data: UpdateAdminOrDirectorReq) => {
  return systemService.post('/dept/update-dept-admin-or-director', data);
};

// 根据用户ID获取其所属部门及其父部门列表
export const getDeptsById = async (params: GetDeptsByIdReq) => {
  return await systemService.get(`/dept/get-depts-by-id?id=${params.id}&idType=${params.idType}`);
};
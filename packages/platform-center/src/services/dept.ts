import { type PageParam } from "../types/common";
import { type DeptVO, type DeptForm } from "../types/dept";
import systemClient from "./clients/system";

// 查询部门（精简)列表
export const getSimpleDeptList = (): Promise<Partial<DeptVO>[]> => {
  return systemClient.get("/dept/simple-list");
};

// 查询部门列表
export const getDeptList = (params?: any) => {
  return systemClient.get("/dept/list", { params });
};

// 查询部门分页
export const getDeptPage = async (params: PageParam) => {
  return await systemClient.get("/dept/list", { params });
};

// 查询部门详情
export const getDept = (id: number) => {
  return systemClient.get("/dept/get?id=" + id);
};

// 新增部门
export const createDept = (data: DeptForm) => {
  return systemClient.post("/dept/create", data);
};

// 修改部门
export const updateDept = (data: DeptForm) => {
  return systemClient.put("/dept/update", data);
};

// 删除部门
export const deleteDept = async (id: number) => {
  return await systemClient.delete("/dept/delete?id=" + id);
};

// 批量删除部门
export const deleteDeptList = async (ids: number[]) => {
  return await systemClient.delete("/dept/delete-list", {
    params: { ids: ids.join(",") },
  });
};

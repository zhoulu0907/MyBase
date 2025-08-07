import systemClient from "./clients/system";

// 获取平台管理员列表信息
export const getPlatformAdminListApi = (params: any) =>
  systemClient.get("/system/administrator/page", { params });

// 新增平台管理员
export const addPlatformAdminApi = (data: any) =>
  systemClient.post("/system/administrator", data);

// 删除平台管理员
export const deletePlatformAdminApi = (id: number) =>
  systemClient.delete(`/system/administrator/${id}`);

// 修改平台管理员
export const updatePlatformAdminApi = (id: number, data: any) =>
  systemClient.put(`/system/administrator/${id}`, data);

// 模糊搜索
export const searchPlatformAdminApi = (params: any) =>
  systemClient.get("/system/administrator/search", { params });

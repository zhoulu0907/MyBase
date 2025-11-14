// 字典
export interface DictItem {
  id: string;
  name: string; // 字典名称
  type: string; // 字典编码
  status: number; // 字典状态
  remark?: string; // 备注
  readonly createTime: string; // 创建时间
  dictOwnerType?: string;
  dictOwnerId?: string;
}

// 字典数据
export interface DictData {
  id: string;
  sort: number;
  label: string;
  value: string;
  dictType?: string;
  status: number;
  remark?: string;
  createTime?: string;
  colorType?: string;
}

export type DictForm = Partial<DictItem>;

export type DictDataForm = Partial<DictData>;

// 批量操作字典数据参数
export interface BatchConfigDictDataParams {
  createList: DictData[];
  updateList: DictData[];
  deleteIds: string[];
}
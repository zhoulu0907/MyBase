// 字典
export interface Dict {
  name: string; // 字典名称
  type: string; // 字典编码
  status: number; // 字典状态
  remark?: string; // 备注
}

export interface DictDataForm {
  label: string;
  value: string;
  dictType: string;
  remark?: string;
  sort: number;
  status: number; // 修改类型为 number
}

// 字典数据
export interface DictData {
  id: string
  sort: number
  label: string
  value: string
  dictType: string
  status: number
  colorType: string
  cssClass: string
  remark: string
  createTime: string
}

// 字典项（类型）
export interface DictItem {
  id?: string; // 主键，新增时可不传
  name: string; // 字典名称
  type: string; // 字典编码
  status: number; // 字典状态
  remark?: string; // 备注
  createTime?: string; // 创建时间
}

import { BaseNode, Condition, FieldType } from "../base";

// 获取数据节点
export interface UpdateDataNode extends BaseNode {
    payload: UpdateDataNodePayload;
}

export interface UpdateDataField {
    fieldId: string;
    fieldType: FieldType;
    fieldValue: string;
}


export interface UpdateDataNodePayload {
    pageId: string;

    // 过滤条件
    condition?: Condition;

    fields: UpdateDataField[];

    // 当未获取到数据时,是否新增
    addWhenNoData?: boolean;
    // 是否支持批量更新
    batchUpdate?: boolean;
    // 批量更新最多条数
    batchUpdateMaxCount?: number;
}
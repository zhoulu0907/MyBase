import { BaseNode, FieldType } from "../base";

export interface AddDataNode extends BaseNode {
    payload: AddDataNodePayload;
}

export enum AddType {
    // 主表单新增
    MAIN_FORM = 'main_form',
    // 子表单新增
    SUB_FORM = 'sub_form',
}

export enum AddDataType {
    // 单个新增
    SINGLE = 'single',
    // 批量新增
    BATCH = 'batch',
}

// 新增数据字段
export interface AddDataField {
    // 字段id
    fieldId: string;
    // 字段类型
    fieldType: FieldType;
    // 字段值
    fieldValue: string;
}

export interface AddDataNodePayload {
    addType: AddType;
    // 表单页面id
    pageId: string;
    addDataType: AddDataType;
    // 新增数据源,只有addDataType为BATCH时有效
    addDataSource?: string;

    fields: AddDataField[];

    // 是否同步处理子表
    syncSubForm?: boolean;
}
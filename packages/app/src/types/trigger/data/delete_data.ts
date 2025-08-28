import { BaseNode, Condition } from "../base";

export interface DeleteDataNode extends BaseNode {
    payload: DeleteDataNodePayload;
}

export enum DeleteDataType {
    // 单个删除
    SOFT_DELETE = 'soft_delete',
    // 物理删除
    PHYSICAL_DELETE = 'physical_delete',
}

export interface DeleteDataNodePayload {
    // 目标表单
    pageId: string;
    // 删除方式
    deleteDataType: DeleteDataType;
    // 过滤条件
    condition?: Condition;
    // 批量删除限制
    batchDeleteLimit?: number;
    // 级联删除
    cascadeDelete?: boolean;
}
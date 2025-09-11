import { BaseNode, Condition } from "../base";

export interface DeleteDataNode extends BaseNode {
    payload: DeleteDataNodePayload;
}

export enum DeleteDataType {
    // 2软删除
    SOFT_DELETE = 2,
    // 物理删除/硬删除
    PHYSICAL_DELETE = 1,
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
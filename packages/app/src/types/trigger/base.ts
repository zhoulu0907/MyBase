export interface BaseNode {
    id: string;
    title: string;
    type: string;
}

export interface Condition {
    id: string;
    // AND 、OR
    condition: string;
    // 规则id
    ruleId: string;
    // 父级id
    parentId: string;
    // 字段id
    fieldId?: string;
    // 操作符
    op?: string;
    // 操作符code
    opCode?: string;
    // 操作值
    operators?: string[];
    // 递归嵌套
    rules?: Condition[];
}


export enum FieldType {
    // 值
    VALUE = 'value',
    // 字段
    FIELD = 'field',
    // 公式
    EXPRESSION = 'expression',
}

export enum SortType {
    // 不排序
    NONE = 'none',
    // 升序
    ASC = 'asc',
    DESC = 'desc',
}
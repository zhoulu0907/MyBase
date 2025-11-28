import type { EntityStatus } from './const';

// 业务对象节点数据
export interface EntityNode {
  id?: string;
  entityId: string;
  code?: string;
  entityName: string;
  tableName: string;
  displayName: string;
  description: string;
  fields: Array<EntityField>;
  positionX: number;
  positionY: number;
  status?: EntityStatus;
}

export interface EntityField {
  fieldId: string;
  code: string;
  fieldName: string;
  fieldType: string;
  isSystemField: number; // 0: 自定义 1: 系统
  displayName: string;
}

// 节点信息
export interface EntityNodeProps {
  onNodeEdit?: (data: EntityNode) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
  onNodeAddField?: (node: EntityNode) => void;
  onNodeAddRelation?: (id: string) => void;
  onNodeAddMasterDetail?: (id: string) => void;
  onNodeAddRule?: (id: string) => void;
  onlyUpdateNode?: boolean;
  // mode?: 'view' | 'edit';
  nodeData: EntityNode;
}

// 字段级别的边
export interface FieldEdge {
  source: { cell: string; port: string };
  target: { cell: string; port: string };
  label?: string;
}

// 边的数据结构示例
export interface EdgeData {
  // source: { cell: string; port: string }; // 源节点ID
  // target: { cell: string; port: string }; // 目标节点ID
  sourceEntityId: string;
  sourceFieldId: string;
  targetEntityId: string;
  targetFieldId: string;
  label?: string; // 关系标签
  relationshipId?: string;
}

// 全部节点数据
export interface EntityData {
  nodes: EntityNode[];
  edges: EdgeData[];
}

// 全部节点信息
export interface EntityERProps {
  data: EntityData;
  mode?: 'view' | 'edit';
  onNodeEdit?: (data: EntityNode) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
  onNodeAddField?: (node: EntityNode) => void;
  onNodeAddRelation?: (id: string) => void;
  onNodeAddMasterDetail?: (id: string) => void;
  onNodeAddRule?: (id: string) => void;
  onFieldClick?: (id: string, entityId?: string) => void;
  onEdgeEdit?: (data: EdgeData) => void;
  onlyUpdateNode?: boolean;
  updateEntityPosition?: (data: EntityNode, x: number, y: number) => void;
  onStatusChange?: (data: Partial<EntityNode>) => void;
}

// 资产列表
// 资产详情
export interface Entity {
  id: string;
  tableName?: string;
  displayName: string;
  entityName?: string;
  code?: string;
  description?: string;
  fields?: EntityField[];
  relations?: EntityRelation[];
  rules?: EntityRule[];
  methods?: EntityMethod[];
}

// 资产列表
export interface EntityListItem {
  id: string;
  displayName: string;
  code: string;
}

// 资产关系
export interface EntityRelation {
  id: string;
  displayName: string;
  code: string;
}

// 资产规则
export interface EntityRule {
  id: string;
  displayName: string;
  code: string;
}

// 资产方法
export interface EntityMethod {
  id: string;
  displayName: string;
  code: string;
}

// 资产字段
export interface EntityField {
  id: string;
  displayName: string;
  code: string;
}

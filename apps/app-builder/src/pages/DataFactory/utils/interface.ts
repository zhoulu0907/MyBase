// 业务对象节点数据
export interface EntityNode {
  entityId: string;
  code: string;
  entityName: string;
  description: string;
  fields: Array<EntityField>;
  positionX: number;
  positionY: number;
}

export interface EntityField {
  fieldId: string;
  code: string;
  fieldName: string;
  fieldType: string;
  isSystemField: boolean;
}

// 节点信息
export interface EntityNodeProps {
  onNodeEdit?: (data: EntityNode) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
  onNodeAddField?: (id: string) => void;
  onNodeAddRelation?: (id: string) => void;
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
  source: { cell: string; port: string }; // 源节点ID
  target: { cell: string; port: string }; // 目标节点ID
  label?: string; // 关系标签
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
  onNodeAddField?: (id: string) => void;
  onNodeAddRelation?: (id: string) => void;
  onlyUpdateNode?: boolean;
}

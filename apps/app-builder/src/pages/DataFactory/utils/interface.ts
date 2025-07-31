// 业务对象节点
export interface EntityNode {
  id: string;
  title: string;
  fields: Array<{
    id: string; // 添加字段 ID
    name: string;
    type: string;
    isSystem?: boolean;
  }>;
  x: number;
  y: number;
  onNodeEdit?: (id: string, data: EntityNode) => void;
  onNodeAdd?: () => void;
  mode?: 'view' | 'edit';
}

// 字段级别的边
export interface FieldEdge {
  source: { cell: string; port: string };
  target: { cell: string; port: string };
  label?: string;
}
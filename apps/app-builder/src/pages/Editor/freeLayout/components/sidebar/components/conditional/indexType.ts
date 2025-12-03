// 定义数据类型
export interface BranchData {
  name: any;
  toNodeName: any;
  isDefault: string;
  priority: any;
}

// 定义组件Props类型
export interface ApproveDrawerProps {
  handleConfigSubmit: (data: BranchData[]) => void;
  configData?: BranchData[];
}

// 定义表格组件类型
export interface TableComponents {
  header: {
    operations: (params: { selectionNode?: React.ReactNode; expandNode?: React.ReactNode }) => Array<{
      node: React.ReactNode;
      width?: number;
      name?: string;
    }>;
  };
  body: {
    operations: (params: { selectionNode?: React.ReactNode; expandNode?: React.ReactNode }) => Array<{
      node: React.ReactNode | ((record: BranchData) => React.ReactNode);
      width?: number;
      name?: string;
    }>;
    tbody: any;
    row: any;
  };
}
export interface SortEndHandler {
  oldIndex: number;
  newIndex: number;
}

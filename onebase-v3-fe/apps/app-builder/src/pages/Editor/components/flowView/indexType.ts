export enum IndexType {
  COMPLETED_COLOR = '#4FAE7B',
  PENDING_COLOR = '#d9d9d9',
  PASS = 'pass',
  COMPLETED = 'completed',
  PENDING = 'pending',
  PROCESSING = 'processing'
}

import type { WorkflowNodeJSON, WorkflowEdgeJSON } from '@flowgram.ai/free-layout-editor';

export interface ExtendedWorkflowEdgeJSON extends WorkflowEdgeJSON {
  type?: string;
}

export interface WorkflowJSON {
  nodes: WorkflowNodeJSON[];
  edges: ExtendedWorkflowEdgeJSON[];
}

export interface FlowData {
  id?: string;
  flowCode?: string;
  flowName?: string;
  version?: string;
  versionAlias?: string;
  versionStatus?: string;
  businessId?: string;
}

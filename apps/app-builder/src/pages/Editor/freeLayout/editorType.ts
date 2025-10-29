import type { WorkflowNodeJSON, WorkflowEdgeJSON } from '@flowgram.ai/free-layout-editor';

export interface ExtendedWorkflowEdgeJSON extends WorkflowEdgeJSON {
  type?: string;
}

export interface WorkflowJSON {
  nodes: WorkflowNodeJSON[];
  edges: ExtendedWorkflowEdgeJSON[];
}

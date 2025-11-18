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

export enum IdList {
  PROT_OUTPUT_START_0_ = 'port_output_start_0_',
  START_0_ = 'start_0_-_',
  START_0_START_1 = 'start_0_-start_1_'
}

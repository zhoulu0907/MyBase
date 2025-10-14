import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

export const updateDataDeleteOutputs = (nodeID: string) => {
  const outputs = {};

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

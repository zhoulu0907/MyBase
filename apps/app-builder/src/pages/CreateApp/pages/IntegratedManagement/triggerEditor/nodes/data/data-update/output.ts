import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

export const updateDataUpdateOutputs = (nodeID: string, values: any) => {
  const outputs = {
    updateType: values.updateType,
    mainEntityId: values.mainEntityId,
    subEntityId: values.subEntityId
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

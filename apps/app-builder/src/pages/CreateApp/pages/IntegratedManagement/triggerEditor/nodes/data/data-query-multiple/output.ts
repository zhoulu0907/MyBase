import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

export const updateDataQueryMultipleOutputs = (nodeID: string, values: any) => {
  const outputs = {
    dataType: values.dataType,
    mainEntityId: values.mainEntityId,
    subEntityId: values.subEntityId,
    dataNodeId: values.dataNodeId
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

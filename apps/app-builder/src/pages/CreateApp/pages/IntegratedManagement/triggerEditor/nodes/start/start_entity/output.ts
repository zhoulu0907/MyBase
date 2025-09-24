import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

export const updateStartEntityOutputs = (nodeID: string, values: any, entityList: any[]) => {
  const outputs = {
    entityId: values.entityId,
    entityName: entityList.find((item) => item.entityId === values.entityId)?.entityName
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

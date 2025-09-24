import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import type { ConfitionField } from '@onebase/app';

export const updateDataUpdateOutputs = (nodeID: string, values: any, conditionFields: ConfitionField[]) => {
  const outputs = {
    conditionFields: conditionFields
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

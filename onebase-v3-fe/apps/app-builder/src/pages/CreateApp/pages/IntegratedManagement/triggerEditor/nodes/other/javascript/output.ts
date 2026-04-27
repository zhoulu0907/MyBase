import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import type { ConditionField } from '@onebase/app';

export const updateJavascriptOutputs = (nodeID: string, conditionFields: ConditionField[]) => {
  const outputs = {
    conditionFields: conditionFields
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

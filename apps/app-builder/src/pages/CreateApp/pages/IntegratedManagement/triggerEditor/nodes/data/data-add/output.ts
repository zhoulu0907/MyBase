import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import type { ConfitionField } from '@onebase/app';

export const updateDataAddOutputs = (nodeID: string, values: any, conditionFields: ConfitionField[]) => {
  const outputs = {
    fields: values.fields,
    conditionFields: conditionFields
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

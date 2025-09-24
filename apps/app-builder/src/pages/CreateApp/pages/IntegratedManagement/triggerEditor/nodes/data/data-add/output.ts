import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import type { AppEntityField } from '@onebase/app';

export const updateDataAddOutputs = (nodeID: string, values: any, fieldList: AppEntityField[]) => {
  const outputs = {
    fields: values.fields,
    fieldList: fieldList
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import {type Field} from '../../../typings'

export const updateModalOutputs = (nodeID: string, fields: Field[]) => {
  const outputs = {
    fields: fields
  };

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};
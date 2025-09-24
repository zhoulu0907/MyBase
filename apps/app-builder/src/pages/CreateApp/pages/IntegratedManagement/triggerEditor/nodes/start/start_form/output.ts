import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';

export const updateStartFormOutputs = (nodeID: string, values: any, pageList: any[], conditionFields: any[]) => {
  const outputs = {
    pageId: values.pageId,
    pageName: pageList.find((item) => item.id === values.pageId)?.pageName,
    fieldId: values.fieldId,
    fieldName: conditionFields.find((item) => item.value === values.fieldId)?.label,
    triggerRange: values.triggerRange
  };

  console.log('outputs: ', outputs);

  triggerNodeOutputSignal.addTriggerNodeOutput(nodeID, outputs);
};

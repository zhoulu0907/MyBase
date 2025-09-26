import { type FormMeta, type FormRenderProps, Field } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useEffect } from 'react';
import { useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const { node } = useNodeRenderContext();

  useEffect(() => {
    if (form.initialValues && form.initialValues.initialData) {
      triggerEditorSignal.setNodeData(node.id, {
        ...triggerEditorSignal.nodeData.value[node.id],
        value: form.initialValues.initialData.value ? true : false
      });
    }
  }, [form]);

  return (
    <div
      style={{
        width: '100%',
        height: '100%',
        backgroundColor: node.index === 0 ? 'green' : 'red',
        color: 'white',
        display: 'flex',
        pointerEvents: 'none',
        alignItems: 'center',
        justifyContent: 'center'
      }}
    >
      <Field name="title">{({ field }) => <>{field.value}</>}</Field>
    </div>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};

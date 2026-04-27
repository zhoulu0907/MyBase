import { type FormMeta, type FormRenderProps, Field } from '@flowgram.ai/fixed-layout-editor';
import { IconCheck, IconClose } from '@arco-design/web-react/icon';
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
        width:'auto',
        height: '100%',
        border: '1px solid',
        borderColor: node.index === 0 ? 'rgb(var(--primary-6))' : '#F53F3F',
        backgroundColor: '#fff',
        padding: '4px 8px',
        borderRadius: 16,
        color: '#272E3B',
        fontSize: '12px',
        boxSizing: 'border-box',
        display: 'flex',
        pointerEvents: 'none',
        alignItems: 'center'
      }}
    >
      <div
        style={{
          width: 16,
          height: 16,
          backgroundColor: node.index === 0 ? 'rgb(var(--primary-1))' : '#FFECE8',
          color: node.index === 0 ? 'rgb(var(--primary-6))' : '#F53F3F',
          textAlign: 'center',
          lineHeight: '16px',
          borderRadius: 8,
          fontWeight: 500,
          marginRight: 8
        }}
      >
        {node.index === 0 ? <IconCheck /> : <IconClose />}
      </div>
      <div style={{
        width:'auto',
        lineHeight: '24px',
        whiteSpace: 'nowrap',
      }}>
        <Field name="title">{({ field }) => <>{field.value}</>}</Field>
      </div>
    </div>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { CAL_TYPE, type ConditionField } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect } from 'react';
import CaclRuleEditor from '../../../components/calc-rule-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [payloadForm] = Form.useForm();
  const calType = Form.useWatch('calType', payloadForm);

  const handleCalTypeChange = (curCalType: CAL_TYPE) => {
    payloadForm.clearFields(['calRules']);
  };

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            layout="vertical"
            initialValues={getInitData()}
            requiredSymbol={{ position: 'end' }}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>

            <Form.Item label="计算方式" field="calType" rules={[{ required: true, message: '请选择计算方式' }]}>
              <Radio.Group direction="horizontal" onChange={handleCalTypeChange}>
                <Radio value={CAL_TYPE.FORMULA}>公式计算</Radio>
                <Radio disabled value={CAL_TYPE.DATASUMMARY}>
                  数据汇总
                </Radio>
              </Radio.Group>
            </Form.Item>

            {calType === CAL_TYPE.FORMULA && (
              <Grid.Row>
                <Form.Item label="字段设置">
                  <CaclRuleEditor form={payloadForm} nodeId={node.id} />
                </Form.Item>
              </Grid.Row>
            )}
          </Form>
        </FormContent>
      ) : (
        <FormContent>
          <FormOutputs />
        </FormContent>
      )}
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
};

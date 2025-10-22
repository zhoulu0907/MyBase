import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select, Switch, Tooltip } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { FLOW_MODAL_CANCEL, FLOW_MODAL_TYPE } from '@onebase/common';
import { useEffect } from 'react';
import CollectFields from '../../../components/collect-fields';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();
  const modalType = Form.useWatch('modalType', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  // 弹窗类型改变
  const modalTypeChange = (value: string) => {
    payloadForm.clearFields(['fields', 'arrange']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      fields: [],
      arrange: undefined
    });
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>

            <Form.Item label="弹窗标题" field="modalTitle" rules={[{ required: true, message: '请输入弹窗标题' }]}>
              <Input placeholder="请输入" />
            </Form.Item>

            <Form.Item label="弹窗类型" field="modalType" rules={[{ required: true, message: '请选择弹窗类型' }]}>
              <Select placeholder="请选择" onChange={modalTypeChange}>
                <Select.Option value={FLOW_MODAL_TYPE.CONFIRM}>二次确认</Select.Option>
                <Select.Option value={FLOW_MODAL_TYPE.INFOR}>收集信息</Select.Option>
                {/* <Select.Option value={FLOW_MODAL_TYPE.CUSTOM}>自定义弹窗</Select.Option> */}
              </Select>
            </Form.Item>

            {modalType === FLOW_MODAL_TYPE.INFOR && (
              <>
                <CollectFields data={triggerEditorSignal.nodeData.value[node.id]?.fields || []} form={payloadForm} />
                <Form.Item label="收集字段排列方式" field="arrange">
                  <Radio.Group>
                    <Radio value={1}>一列</Radio>
                    <Radio value={2}>两列</Radio>
                  </Radio.Group>
                </Form.Item>
              </>
            )}

            <Form.Item label="提示文字" field="prompt">
              <Input.TextArea placeholder="请输入" rows={4} />
            </Form.Item>

            <Grid.Row gutter={24}>
              <Grid.Col span={12}>
                <Form.Item
                  label="确定按钮文字"
                  field="okText"
                  rules={[{ required: true, message: '请输入确定按钮文字' }]}
                >
                  <Input placeholder="请输入" />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={12}>
                <Form.Item
                  label="取消按钮文字"
                  field="cancelText"
                  rules={[{ required: true, message: '请输入取消按钮文字' }]}
                >
                  <Input placeholder="请输入" />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>

            <Form.Item label="弹窗取消后" field="afterCancel">
              <Radio.Group>
                <Radio value={FLOW_MODAL_CANCEL.STOP}>事件终止</Radio>
                <Radio value={FLOW_MODAL_CANCEL.CONTINUE}>事件继续执行</Radio>
              </Radio.Group>
            </Form.Item>

            <Grid.Row gutter={24}>
              <Grid.Col span={12}>
                <Form.Item
                  label={
                    <>
                      <span>关闭默认终止提醒</span>
                      <Tooltip disabled content="todo">
                        <IconQuestionCircle />
                      </Tooltip>
                    </>
                  }
                  field="closeWarn"
                  triggerPropName="checked"
                >
                  <Switch />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={12}>
                <Form.Item label="弹窗取消后提醒" field="cancelWarn" triggerPropName="checked">
                  <Switch />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>
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

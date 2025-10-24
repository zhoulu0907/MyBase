import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Radio, Select } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { REFRESH_TYPE, REFRESH_STRATEGY, type SelectOption } from '@onebase/app';
import { useEffect, useState } from 'react';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [payloadForm] = Form.useForm();
  const refreshRange = Form.useWatch('refreshRange', payloadForm);
  const pageId = Form.useWatch('pageId', payloadForm);

  const [pageList, setPageList] = useState<SelectOption[]>([]);
  const [componentList, setComponentList] = useState<SelectOption[]>([]);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    // todo 接口获取页面列表  类型判断：组件列表
    setPageList([]);
    setComponentList([]);
  };

  // 刷新范围
  const handleRefreshRangeChange = () => {
    payloadForm.clearFields(['pageId', 'componentId']);
  };
  // 所属页面
  const handlePageIdChange = () => {
    payloadForm.clearFields(['componentId']);
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
            <Form.Item label="节点ID" field="id" initialValue={node.id} required>
              <Input disabled />
            </Form.Item>

            <Form.Item label="刷新范围" field="refreshRange" rules={[{ required: true, message: '请选择刷新范围' }]}>
              <Radio.Group direction="vertical" onChange={handleRefreshRangeChange}>
                <Radio value={REFRESH_TYPE.CURRENT_PAGE}>当前页面</Radio>
                <Radio value={REFRESH_TYPE.SPECIFY_PAGE}>指定页面</Radio>
                <Radio value={REFRESH_TYPE.SPECIFY_COMPONENT}>页面内指定组件</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 指定页面 */}
            {refreshRange === REFRESH_TYPE.SPECIFY_PAGE && (
              <Form.Item label="选择页面" field="pageId" rules={[{ required: true, message: '请选择页面' }]}>
                <Select options={pageList} placeholder="请选择页面"></Select>
              </Form.Item>
            )}
            {/* 页面内指定组件 */}
            {refreshRange === REFRESH_TYPE.SPECIFY_COMPONENT && (
              <>
                <Form.Item
                  label="所属页面"
                  field="pageId"
                  rules={[{ required: true, message: '请选择所属页面' }]}
                  onChange={handlePageIdChange}
                >
                  <Select options={pageList} placeholder="请选择所属页面"></Select>
                </Form.Item>
                <Form.Item
                  label="目标组件"
                  field="componentId"
                  disabled={!pageId}
                  rules={[{ required: true, message: '请选择目标组件' }]}
                >
                  <Select options={componentList} placeholder="请选择目标组件"></Select>
                </Form.Item>
              </>
            )}

            <Form.Item label="刷新策略" field="refreshStrategy" rules={[{ required: true, message: '请选择刷新策略' }]}>
              <Radio.Group direction="vertical">
                <Radio value={REFRESH_STRATEGY.RESERVE}>保留状态刷新</Radio>
                <Radio value={REFRESH_STRATEGY.RESET}>重置状态刷新</Radio>
              </Radio.Group>
            </Form.Item>
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

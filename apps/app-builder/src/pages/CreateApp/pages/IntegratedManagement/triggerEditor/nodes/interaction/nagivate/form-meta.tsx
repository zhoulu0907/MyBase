import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select, Radio, Button, Grid, InputNumber, Switch } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { TARGET_PAGE_TYPE, OPEN_PAGE_TYPE, MODAL_SIZE_TYPE, UNAUTHORIZED_EVENT, type SelectOption } from '@onebase/app';
import { useEffect, useState } from 'react';
import ParamField from '../../../components/param-field';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [payloadForm] = Form.useForm();
  const targetPageType = Form.useWatch('targetPageType', payloadForm);
  const openPageType = Form.useWatch('openPageType', payloadForm);
  const modalSizeType = Form.useWatch('modalSizeType', payloadForm);
  const authorize = Form.useWatch('authorize', payloadForm);

  const [pageList, setPageList] = useState<SelectOption[]>([]);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    // todo 接口获取跳转页面列表
    setPageList([]);
  };

  // 目标页面类型
  const handleTargetPageTypeChange = () => {
    payloadForm.clearFields(['pageId', 'outsideUrl']);
  };
  // 打开方式
  const handleOpenPageTypeChange = () => {
    payloadForm.clearFields(['modalSizeType', 'modalWidth', 'modalHeight', 'modalTitle', 'modalPlacement']);
  };
  // 弹窗尺寸
  const handleModalSizeTypeChange = () => {
    payloadForm.clearFields(['modalWidth', 'modalHeight']);
  };
  // 权限校验
  const handleAuthorizeChange = () => {
    payloadForm.clearFields(['unAuthorizedEvent']);
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
            <Form.Item
              label="目标页面类型"
              field="targetPageType"
              rules={[{ required: true, message: '请选择目标页面类型' }]}
            >
              <Select
                onChange={handleTargetPageTypeChange}
                placeholder="请选择"
                options={[
                  { label: '系统内页面', value: TARGET_PAGE_TYPE.INSIDE },
                  { label: '外部链接', value: TARGET_PAGE_TYPE.OUTSIDE }
                ]}
              ></Select>
            </Form.Item>

            {/* 系统内页面 */}
            {targetPageType === TARGET_PAGE_TYPE.INSIDE && (
              <Form.Item label="选择页面" field="pageId" rules={[{ required: true, message: '请选择目标页面' }]}>
                <Select options={pageList} placeholder="请选择"></Select>
              </Form.Item>
            )}
            {/* 外部链接 */}
            {targetPageType === TARGET_PAGE_TYPE.OUTSIDE && (
              <Form.Item
                label="链接地址"
                field="outsideUrl"
                rules={[
                  { required: true, message: '请输入链接地址' },
                  { match: /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/, message: '请输入正确的链接地址' }
                ]}
              >
                <Input placeholder="https://example.com"></Input>
              </Form.Item>
            )}

            <ParamField data={triggerEditorSignal.nodeData.value[node.id]?.paramFields || []} form={payloadForm} />

            <Form.Item label="打开方式" field="openPageType" rules={[{ required: true, message: '请选择打开方式' }]}>
              <Radio.Group direction="vertical" onChange={handleOpenPageTypeChange}>
                <Radio value={OPEN_PAGE_TYPE.CURRENT_WINDOW}>当前窗口覆盖</Radio>
                <Radio value={OPEN_PAGE_TYPE.NEW_WINDOW}>新窗口打开</Radio>
                <Radio value={OPEN_PAGE_TYPE.MODAL}>弹窗打开</Radio>
                <Radio value={OPEN_PAGE_TYPE.DRAWER}>侧边栏打开</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 弹窗打开 */}
            {openPageType === OPEN_PAGE_TYPE.MODAL && (
              <>
                {/* 弹窗尺寸 */}
                <Form.Item label="弹窗尺寸" field="modalSizeType">
                  <Radio.Group onChange={handleModalSizeTypeChange}>
                    <Radio value={MODAL_SIZE_TYPE.SMALL}>
                      {({ checked }) => <Button type={checked ? 'primary' : 'default'}>小 (400×300)</Button>}
                    </Radio>
                    <Radio value={MODAL_SIZE_TYPE.MEDIUN}>
                      {({ checked }) => <Button type={checked ? 'primary' : 'default'}>中 (600×450)</Button>}
                    </Radio>
                    <Radio value={MODAL_SIZE_TYPE.LARGE}>
                      {({ checked }) => <Button type={checked ? 'primary' : 'default'}>大 (800×600)</Button>}
                    </Radio>
                    <Radio value={MODAL_SIZE_TYPE.CUSTOM}>
                      {({ checked }) => <Button type={checked ? 'primary' : 'default'}>自定义</Button>}
                    </Radio>
                  </Radio.Group>
                </Form.Item>

                {/* 自定义尺寸 */}
                {modalSizeType === MODAL_SIZE_TYPE.CUSTOM && (
                  <Grid.Row>
                    <Grid.Col span={4}>
                      <Form.Item field="modalWidth">
                        <InputNumber placeholder="请输入宽度" />
                      </Form.Item>
                    </Grid.Col>
                    <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                      <IconClose />
                    </Grid.Col>
                    <Grid.Col span={4}>
                      <Form.Item field="modalHeight">
                        <InputNumber placeholder="请输入高度" />
                      </Form.Item>
                    </Grid.Col>
                  </Grid.Row>
                )}
                {/* 弹窗标题 */}
                <Form.Item label="弹窗标题" field="modalTitle">
                  <Input placeholder="请输入弹窗标题" />
                </Form.Item>
              </>
            )}
            {/* 侧边栏打开 */}
            {openPageType === OPEN_PAGE_TYPE.DRAWER && (
              <>
                <Form.Item label="侧边栏位置" field="modalPlacement" rules={[{ required: true, message: '打开位置' }]}>
                  <Radio.Group direction="vertical">
                    <Radio value="left">左侧打开</Radio>
                    <Radio value="right">右侧打开</Radio>
                  </Radio.Group>
                </Form.Item>
                <Form.Item label="侧边栏宽度" field="modalWidth">
                  <InputNumber placeholder="请输入侧边栏宽度" />
                </Form.Item>
                <Form.Item label="侧边栏标题" field="modalTitle">
                  <Input placeholder="请输入侧边栏标题" />
                </Form.Item>
              </>
            )}

            <Form.Item label="权限校验" field="authorize">
              <Switch onChange={handleAuthorizeChange} />
            </Form.Item>
            {authorize && (
              <Form.Item label="无权限时" field="unAuthorizedEvent">
                <Select
                  options={[
                    { label: '提示"无权限"', value: UNAUTHORIZED_EVENT.PROMPT },
                    { label: '跳转首页', value: UNAUTHORIZED_EVENT.JUMP }
                  ]}
                ></Select>
              </Form.Item>
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

import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Steps } from '@arco-design/web-react';
import { listConnectorNodeConfig } from '@onebase/app';
import { useEffect, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

const Step = Steps.Step;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
  };

  const [payloadForm] = Form.useForm();
  const [currentStep, setCurrentStep] = useState(1);

  const [nodeConfigList, setNodeConfigList] = useState<any[]>([]);

  useEffect(() => {
    if (currentStep === 1) {
      handleListConnectorNodeConfig();
    }
  }, [currentStep]);

  const handleStepChange = (current: number) => {
    setCurrentStep(current);
  };

  const handleListConnectorNodeConfig = async () => {
    const res = await listConnectorNodeConfig();
    console.log(res);
    if (res) {
      setNodeConfigList(res);
    }
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            requiredSymbol={{ position: 'end' }}
          >
            <Steps type="navigation"
            current={currentStep} style={{ padding: '16px' }} size="small" onChange={handleStepChange}>
              <Step title="选择连接器" />
              <Step title="选择连接" />
              <Step title="选择动作" />
              <Step title="参数配置" />
            </Steps>

            {/* 步骤1: 选择连接器 */}
            {currentStep === 1 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
              {/* 渲染 nodeConfig 列表，允许用户选择连接器 */}
              <div>
                {nodeConfigList && nodeConfigList.length > 0 ? (
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16 }}>
                    {nodeConfigList.map((config) => (
                      <div
                        key={config.nodeName}
                        style={{
                          border: '1px solid #e5e6eb',
                          borderRadius: 8,
                          padding: 16,
                          minWidth: 160,
                          cursor: 'pointer',
                          display: 'flex',
                          alignItems: 'center',
                          background: payloadForm.getFieldValue('nodeConfigId') === config.id ? 'rgba(22, 119, 255, 0.08)' : '#fff',
                          boxShadow: payloadForm.getFieldValue('nodeConfigId') === config.id ? '0 0 0 2px #1677ff' : 'none'
                        }}
                        onClick={() => {
                          payloadForm.setFieldValue('nodeConfigId', config.id);
                          // 可根据流程设置 node 数据（示例）
                          triggerEditorSignal.setNodeData(node.id, {
                            ...triggerEditorSignal.nodeData.value[node.id],
                            nodeConfigId: config.id,
                            nodeConfigName: config.connectorName
                          });
                        }}
                      >
                        {config.iconUrl ? (
                          <img src={config.iconUrl} alt={config.connectorName} style={{ width: 36, height: 36, marginRight: 12 }} />
                        ) : (
                          <div style={{
                            width: 36,
                            height: 36,
                            borderRadius: '50%',
                            background: '#f5f6fa',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: '#999',
                            fontSize: 18,
                            marginRight: 12
                          }}>
                            {config.connectorName?.[0] || 'C'}
                          </div>
                        )}
                        <div>
                          <div style={{ fontWeight: 500 }}>{config.connectorName}</div>
                          <div style={{ color: '#999', fontSize: 12 }}>{config.description}</div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div style={{ color: '#999', padding: '32px 0', textAlign: 'center' }}>
                    暂无可用连接器，请先在集成管理-连接器中心添加
                  </div>
                )}
              </div>

              </div>
            )}

            {/* 步骤2: 选择连接 */}
            {currentStep === 2 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                {/* 步骤2内容容器 */}
              </div>
            )}

            {/* 步骤3: 选择动作 */}
            {currentStep === 3 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                {/* 步骤3内容容器 */}
              </div>
            )}

            {/* 步骤4: 参数配置 */}
            {currentStep === 4 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                {/* 步骤4内容容器 */}
              </div>
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

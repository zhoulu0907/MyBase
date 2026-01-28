import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Steps } from '@arco-design/web-react';
import {
  listConnectorByType,
  listConnectorNodeConfig,
  type ConnectorNodeConfig,
  type FlowConnector
} from '@onebase/app';
import { useEffect, useMemo, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { ActionFormConfig } from './components/ActionFormConfig';
import { ActionList, type ActionItem } from './components/ActionList';
import { ConnectorList } from './components/ConnectorList';
import { ConnectorNodeConfigList } from './components/ConnectorNodeConfigList';

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

  const [nodeConfigList, setNodeConfigList] = useState<ConnectorNodeConfig[]>([]);
  const [connectorList, setConnectorList] = useState<FlowConnector[]>([]);
  const [selectedConnector, setSelectedConnector] = useState<FlowConnector | null>(null);
  const [selectedActionKey, setSelectedActionKey] = useState<string | null>(null);

  const actionItems = useMemo(() => {
    const config = selectedConnector?.config;
    if (!config) return [];
    const properties = config.properties;
    if (!properties || typeof properties !== 'object') return [];

    const actions = Object.entries(properties).map(([key, property]: [string, any]) => ({
      key: key,
      title: property.title,
      description: property.description
    }));

    console.log('actions :', actions);
    return actions;
  }, [selectedConnector]);

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

  const handleListConnectorByType = async (typeCode: string) => {
    const res = await listConnectorByType({ typeCode });
    console.log(res);
    if (res) {
      setConnectorList(res);
    }
  };

  const handleConnectorTypeSelect = (typeCode: string) => {
    setCurrentStep(2);
    handleListConnectorByType(typeCode);
  };

  const handleConnectorSelect = (connector: FlowConnector) => {
    console.log('connector:', connector);
    setSelectedConnector(connector);
    setCurrentStep(3);
  };

  const handleActionSelect = (item: ActionItem) => {
    setSelectedActionKey(item.key);
    setCurrentStep(4);
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
            <Steps
              type="navigation"
              current={currentStep}
              style={{ padding: '16px' }}
              size="small"
              onChange={handleStepChange}
            >
              <Step title="选择连接器" />
              <Step title="选择连接" />
              <Step title="选择动作" />
              <Step title="参数配置" />
            </Steps>

            {/* 步骤1: 选择连接器 */}
            {currentStep === 1 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                <ConnectorNodeConfigList
                  nodeConfigList={nodeConfigList}
                  form={payloadForm}
                  onSelect={handleConnectorTypeSelect}
                />
              </div>
            )}

            {/* 步骤2: 选择连接 */}
            {currentStep === 2 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                <ConnectorList connectorList={connectorList} form={payloadForm} onSelect={handleConnectorSelect} />
              </div>
            )}

            {/* 步骤3: 选择动作 */}
            {currentStep === 3 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                <ActionList items={actionItems} form={payloadForm} onSelect={handleActionSelect} />
              </div>
            )}

            {/* 步骤4: 参数配置 */}
            {currentStep === 4 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                <ActionFormConfig connector={selectedConnector} actionKey={selectedActionKey} />
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

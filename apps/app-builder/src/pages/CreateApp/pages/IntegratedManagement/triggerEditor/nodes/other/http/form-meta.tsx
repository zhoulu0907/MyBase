import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Spin, Steps } from '@arco-design/web-react';
import { type Form as FormilyForm } from '@formily/core';
import {
  listConnectorActionInfos,
  listConnectorByType,
  listConnectorNodeConfig,
  type ConnectorNodeConfig,
  type FlowConnector
} from '@onebase/app';
import { useCallback, useEffect, useRef, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { ActionFormConfig } from './components/ActionFormConfig';
import { ActionList, type ActionItem } from './components/ActionList';
import { ConnectorList } from './components/ConnectorList';
import { ConnectorNodeConfigList } from './components/ConnectorNodeConfigList';

const Step = Steps.Step;

export const renderForm = ({}: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  // 保存数据到 signal（作为缓存）
  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id] || {};
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
  };

  const [payloadForm] = Form.useForm();

  // 从 nodeData 中读取已保存的配置
  const savedNodeData = triggerEditorSignal.nodeData.value[node.id] || {};
  const savedNodeConfigCode = savedNodeData.nodeConfigCode;
  const savedConnectorId = savedNodeData.connectorId;
  const savedConnectorUuid = savedNodeData.connectorUuid;
  const savedActionKey = savedNodeData.actionKey;
  // actionParams 按 actionKey 存储：{ [actionKey]: params }
  const savedActionParams = savedNodeData.actionParams || {};

  // 根据保存的数据确定初始步骤
  const getInitialStep = () => {
    if (savedActionKey) return 4;
    if (savedConnectorId) return 3;
    if (savedNodeConfigCode) return 2;
    return 1;
  };

  const [currentStep, setCurrentStep] = useState(getInitialStep());

  const [nodeConfigList, setNodeConfigList] = useState<ConnectorNodeConfig[]>([]);
  const [connectorList, setConnectorList] = useState<FlowConnector[]>([]);
  const [selectedConnector, setSelectedConnector] = useState<FlowConnector | null>(null);
  const [selectedActionKey, setSelectedActionKey] = useState<string | null>(savedActionKey || null);
  const [actionItems, setActionItems] = useState<ActionItem[]>([]);
  const [actionsLoading, setActionsLoading] = useState(false);
  // 使用 ref 存储当前动作的参数值，用于在切换动作时保存
  const actionParamsRef = useRef<Record<string, any> | undefined>(undefined);
  // 使用 ref 存储 ActionFormConfig 的 form 实例，用于在保存时读取最新值
  const actionFormRef = useRef<FormilyForm | null>(null);

  // 使用 listConnectorActions 接口获取动作列表
  useEffect(() => {
    if (!selectedConnector?.id) {
      setActionItems([]);
      return;
    }
    setActionsLoading(true);
    listConnectorActionInfos({
      id: selectedConnector.id,
      pageNo: 1,
      pageSize: 500
    })
      .then((res: any) => {
        const items: ActionItem[] = [];
        const raw = res?.list ?? res?.records ?? res;

        if (raw?.properties && typeof raw.properties === 'object') {
          Object.entries(raw.properties).forEach(([key, property]: [string, any]) => {
            items.push({
              key,
              title: property?.title,
              description: property?.description
            });
          });
        } else if (Array.isArray(raw)) {
          (raw as any[]).forEach((item) => {
            items.push({
              key: item.actionName ?? item.key ?? item.id,
              title: item.title ?? item.actionName ?? item.name,
              description: item.description
            });
          });
        }
        setActionItems(items);
      })
      .catch(() => {
        setActionItems([]);
      })
      .finally(() => {
        setActionsLoading(false);
      });
  }, [selectedConnector?.id]);

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

      // 如果有保存的连接ID，找到对应的连接器
      const currentSavedConnectorId = triggerEditorSignal.nodeData.value[node.id]?.connectorId;
      if (currentSavedConnectorId) {
        const connector = res.find((c: FlowConnector) => c.id === currentSavedConnectorId);
        if (connector) {
          setSelectedConnector(connector);
        }
      }
    }
  };

  // 初始化：加载连接器配置列表并恢复状态
  useEffect(() => {
    const initData = async () => {
      // 加载连接器配置列表
      await handleListConnectorNodeConfig();

      // 如果有保存的连接器类型，加载对应的连接列表
      if (savedNodeConfigCode) {
        await handleListConnectorByType(savedNodeConfigCode);
      }
    };

    initData();
  }, []);

  // 初始化表单值
  useEffect(() => {
    const formValues: Record<string, any> = {};
    if (savedNodeConfigCode) {
      formValues.nodeConfigCode = savedNodeConfigCode;
    }
    if (savedConnectorId) {
      formValues.connectorId = savedConnectorId;
    }
    if (savedConnectorUuid) {
      formValues.connectorUuid = savedConnectorUuid;
    }
    if (savedActionKey) {
      formValues.actionKey = savedActionKey;
      // 如果有保存的动作参数，初始化 ref
      if (savedActionParams && typeof savedActionParams === 'object') {
        // 兼容旧格式：如果 savedActionParams 是对象但不是按 actionKey 存储的，则可能是旧格式
        if (savedActionParams[savedActionKey]) {
          actionParamsRef.current = savedActionParams[savedActionKey];
        } else if (!savedActionParams.actionKey && Object.keys(savedActionParams).length > 0) {
          // 旧格式：直接是参数对象
          actionParamsRef.current = savedActionParams;
        }
      }
    }
    if (savedActionParams) {
      // 将 actionParams 作为整体字段保存
      formValues.actionParams = savedActionParams;
    }

    if (Object.keys(formValues).length > 0) {
      payloadForm.setFieldsValue(formValues);
    }
  }, []);

  const handleStepChange = (current: number) => {
    setCurrentStep(current);

    // 当切换到步骤2时，如果还没有加载连接列表，则加载
    if (current === 2 && savedNodeConfigCode && connectorList.length === 0) {
      handleListConnectorByType(savedNodeConfigCode);
    }
  };

  const handleConnectorTypeSelect = (typeCode: string) => {
    // 更新表单值
    payloadForm.setFieldValue('nodeConfigCode', typeCode);
    // 保存到 signal（作为缓存）
    handlePropsOnChange('nodeConfigCode', typeCode);
    setCurrentStep(2);
    handleListConnectorByType(typeCode);
  };

  const handleConnectorSelect = (connector: FlowConnector) => {
    console.log('connector:', connector);
    // 更新表单值
    payloadForm.setFieldValue('connectorId', connector.id);
    payloadForm.setFieldValue('connectorUuid', connector.connectorUuid);
    // 保存到 signal（作为缓存）
    handlePropsOnChange('connectorId', connector.id);
    handlePropsOnChange('connectorUuid', connector.connectorUuid);
    setSelectedConnector(connector);
    setCurrentStep(3);
  };

  const handleActionSelect = (item: ActionItem) => {
    // 如果之前有选择动作，先将当前动作的参数保存到临时存储（不保存到 signal）
    // 这样切换回来时还能看到之前的修改
    if (selectedActionKey && actionParamsRef.current !== undefined) {
      // 将当前动作的参数暂存到 ref 中，但不保存到 signal
      // 参数会在点击确定时统一保存
    }

    // 更新表单值
    payloadForm.setFieldValue('actionKey', item.key);
    // 保存到 signal（作为缓存）- actionKey 可以立即保存，因为这是步骤选择
    handlePropsOnChange('actionKey', item.key);
    setSelectedActionKey(item.key);

    // 加载新动作的参数（从 signal 中读取已保存的参数）
    const savedActionParams = triggerEditorSignal.nodeData.value[node.id]?.actionParams || {};
    if (savedActionParams && typeof savedActionParams === 'object' && !Array.isArray(savedActionParams)) {
      actionParamsRef.current = savedActionParams[item.key];
    } else {
      actionParamsRef.current = undefined;
    }

    setCurrentStep(4);
  };

  // 处理动作参数变化 - 只更新 ref，不立即保存到 signal（等待点击确定时才保存）
  const handleActionParamsChange = (params: Record<string, any>) => {
    console.log('handleActionParamsChange called with params:', params);
    // 只更新 ref，不立即保存到 signal
    actionParamsRef.current = params;
  };

  // 保存当前动作参数到 signal（在点击确定时调用）
  const saveActionParamsToSignal = useCallback(() => {
    console.log('saveActionParamsToSignal called, selectedActionKey:', selectedActionKey);
    console.log('actionParamsRef.current:', actionParamsRef.current);
    console.log('actionFormRef.current:', actionFormRef.current);

    if (selectedActionKey) {
      // 优先从表单实例中读取最新值，如果没有则使用 ref 中的值
      let currentParams: Record<string, any> | undefined;
      if (actionFormRef.current) {
        currentParams = actionFormRef.current.values || {};
        console.log('Reading from form instance:', currentParams);
      } else if (actionParamsRef.current !== undefined) {
        currentParams = actionParamsRef.current;
        console.log('Reading from ref:', currentParams);
      } else {
        // 如果都没有，从 signal 中读取已保存的值（保持已有值）
        const nodeData = triggerEditorSignal.nodeData.value[node.id] || {};
        const savedActionParams = nodeData.actionParams || {};
        if (typeof savedActionParams === 'object' && !Array.isArray(savedActionParams)) {
          currentParams = savedActionParams[selectedActionKey];
        }
        console.log('Reading from signal:', currentParams);
      }

      // 如果 currentParams 是 undefined，使用空对象（表示没有参数）
      const paramsToSave = currentParams !== undefined ? currentParams : {};

      const nodeData = triggerEditorSignal.nodeData.value[node.id] || {};
      const currentActionParams = nodeData.actionParams || {};
      // 确保 currentActionParams 是对象格式
      const actionParamsObj =
        typeof currentActionParams === 'object' && !Array.isArray(currentActionParams) ? currentActionParams : {};
      actionParamsObj[selectedActionKey] = paramsToSave;

      // 直接使用 triggerEditorSignal.setNodeData 保存
      triggerEditorSignal.setNodeData(node.id, {
        ...nodeData,
        actionParams: actionParamsObj
      });

      // 同时更新表单值，保持同步
      payloadForm.setFieldValue('actionParams', actionParamsObj);
      console.log('Saved actionParams for', selectedActionKey, ':', actionParamsObj);
      console.log('After save, nodeData:', triggerEditorSignal.nodeData.value[node.id]);
    } else {
      console.log('saveActionParamsToSignal: selectedActionKey is null');
    }
  }, [selectedActionKey, node.id, payloadForm]);

  // 将 saveActionParamsToSignal 方法暴露到表单实例上，供 form-footer 调用
  useEffect(() => {
    if (payloadForm) {
      // @ts-ignore - 将保存方法挂载到表单实例上
      payloadForm.saveActionParamsToSignal = saveActionParamsToSignal;
    }
  }, [payloadForm, saveActionParamsToSignal]);

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
                <Spin loading={actionsLoading} style={{ width: '100%' }}>
                  <ActionList items={actionItems} form={payloadForm} onSelect={handleActionSelect} />
                </Spin>
              </div>
            )}

            {/* 步骤4: 参数配置 */}
            {currentStep === 4 && (
              <div style={{ padding: '16px', minHeight: '200px' }}>
                <ActionFormConfig
                  connector={selectedConnector}
                  actionKey={selectedActionKey}
                  initialValues={
                    selectedActionKey && savedActionParams && typeof savedActionParams === 'object'
                      ? savedActionParams[selectedActionKey] || {}
                      : typeof savedActionParams === 'object' && !Array.isArray(savedActionParams) && savedActionParams
                        ? savedActionParams // 兼容旧格式
                        : {}
                  }
                  onChange={handleActionParamsChange}
                  formRef={actionFormRef}
                />
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

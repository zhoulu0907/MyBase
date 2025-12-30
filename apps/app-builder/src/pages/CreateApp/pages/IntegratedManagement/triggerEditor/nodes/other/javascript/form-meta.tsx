import jsNodeIcon from '@/assets/flow/connect/js_node.svg';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Pagination, Steps } from '@arco-design/web-react';
import { IconSync } from '@douyinfe/semi-icons';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  getScriptAction,
  listConnectInstance,
  listScriptAction,
  type ConnectInstance,
  type ListConnectInstanceReq,
  type ListScriptActionReq,
  type ScriptActionItem
} from '@onebase/app';
import { getCommonPaginationList, getHashQueryParam } from '@onebase/common';
import { useCallback, useEffect, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import styles from './index.module.less';
import { InputParameterForm } from './InputParameterForm';

const Step = Steps.Step;

export const renderForm = ({}: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [instanceList, setInstanceList] = useState<any[]>([]);
  const [total, setTotal] = useState(0);
  const [actionList, setActionList] = useState<ScriptActionItem[]>([]);

  const [selectedInstanceId, setSelectedInstanceId] = useState<string>(
    triggerEditorSignal.nodeData.value[node.id]?.instanceId || ''
  );
  const [selectedActionId, setSelectedActionId] = useState<string>(
    triggerEditorSignal.nodeData.value[node.id]?.actionId || ''
  );
  const [inputParameter, setInputParameter] = useState<any[]>([]);
  const [onSwap, setOnSwap] = useState(false);

  const [payloadForm] = Form.useForm();

  const [currentStep, setCurrentStep] = useState(1);

  const handleStepChange = (current: number) => {
    setCurrentStep(current);
  };

  //   useEffect(() => {
  //     console.log(node.id);
  //     console.log(triggerEditorSignal.nodeData.value[node.id]);
  //   }, []);

  useEffect(() => {
    getConnectInstanceList();
  }, []);

  useEffect(() => {
    if (selectedInstanceId) {
      handleGetScriptActionList(selectedInstanceId);
    }
  }, [selectedInstanceId]);

  // 初始化时，如果已有 selectedActionId，立即获取 inputParameter
  useEffect(() => {
    if (selectedActionId) {
      handleGetScriptAction(selectedActionId);
    }
  }, [selectedActionId]);

  const getConnectInstanceList = async () => {
    const curAppId = getHashQueryParam('appId');
    const req: ListConnectInstanceReq = {
      applicationId: curAppId || '',
      pageNo: pageNo,
      pageSize: pageSize || 8
    };
    const res = await getCommonPaginationList(
      (param) => listConnectInstance(param as ListConnectInstanceReq),
      req,
      setPageNo
    );

    console.log('res :', res);
    if (res) {
      setInstanceList(res.list || []);
    }
  };

  const handleGetScriptActionList = async (id: string) => {
    const req: ListScriptActionReq = {
      pageNo: pageNo,
      pageSize: pageSize,
      connectorId: id
    };

    const res = await getCommonPaginationList(
      (param) => listScriptAction(param as ListScriptActionReq),
      req,
      setPageNo
    );

    if (res) {
      setActionList(res.list || []);
      setTotal(res.total || 0);
    }
  };

  const handleGetScriptAction = async (scriptId: string) => {
    const res = await getScriptAction(scriptId);
    console.log(res);
    if (res) {
      if (res.inputSchema) {
        setInputParameter(res.inputSchema || []);
      } else {
        setInputParameter([]);
      }
      const outputSchema = JSON.stringify(res.outputSchema || []);
      payloadForm.setFieldValue('outputParameter', outputSchema);
    }
  };

  const renderInputParameter = useCallback(() => {
    console.log(payloadForm.getFieldsValue());
    return <InputParameterForm inputParameter={inputParameter} form={payloadForm} nodeId={node.id} />;
  }, [inputParameter, node.id]);

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
            <Form.Item field="instanceId" initialValue={selectedInstanceId} hidden>
              <Input disabled />
            </Form.Item>
            <Form.Item field="actionId" initialValue={selectedActionId} hidden>
              <Input disabled />
            </Form.Item>
            <Form.Item field="outputParameter" hidden>
              <Input disabled />
            </Form.Item>

            <div className={styles.ipaasContainer}>
              <div className={styles.stepsContainer}>
                <Steps current={currentStep} style={{ margin: '0 auto' }} size="small" onChange={handleStepChange}>
                  <Step title="选择动作" />
                  <Step title="参数配置" />
                </Steps>
              </div>

              {currentStep === 1 && selectedInstanceId != '' && (
                <div className={styles.selectedInstance}>
                  <div className={styles.selectedInstanceLeft}>
                    <img src={jsNodeIcon} alt="" className={styles.selectIcon} />
                    <div className={styles.instanceItemText}>
                      {instanceList.find((item) => item.id === selectedInstanceId)?.connectorName}
                    </div>
                  </div>

                  <div
                    className={styles.selectedInstanceRight}
                    onClick={() => {
                      setOnSwap(true);
                      setPageNo(1);
                    }}
                  >
                    <IconSync />
                    切换
                  </div>
                </div>
              )}

              {currentStep === 1 && (onSwap || selectedInstanceId === '') && (
                <div className={styles.instanceList}>
                  {instanceList?.map((item: ConnectInstance) => (
                    <div
                      key={item.id}
                      className={styles.instanceItem}
                      onClick={() => {
                        setSelectedInstanceId(item.id);
                        payloadForm.setFieldValue('instanceId', item.id);
                        setOnSwap(false);
                        setPageNo(1);
                      }}
                      style={{
                        border: selectedInstanceId === item.id ? '2px solid rgb(var(--primary-6))' : '1px solid #9c9c9c'
                      }}
                    >
                      <img src={jsNodeIcon} alt="" className={styles.instanceItemIcon} />
                      <div className={styles.instanceItemText}>{item.connectorName}</div>
                    </div>
                  ))}
                </div>
              )}

              {currentStep === 1 && !onSwap && (
                <div className={styles.actionList}>
                  {actionList?.map((item: ScriptActionItem) => (
                    <div
                      key={item.id}
                      className={styles.actionItem}
                      onClick={() => {
                        setSelectedActionId(item.id);
                        payloadForm.setFieldValue('actionId', item.id);
                        // 如果选中的 actionId 与当前不同，清空 inputParameterFields 参数配置
                        if (item.id !== selectedActionId) {
                          payloadForm.setFieldValue('inputParameterFields', []);
                        }
                        setCurrentStep(2);
                      }}
                      style={{
                        border: selectedActionId === item.id ? '2px solid rgb(var(--primary-6))' : '1px solid #9c9c9c'
                      }}
                    >
                      <div className={styles.actionItemName}>{item.scriptName}</div>
                      <div className={styles.actionItemDescription}>{item.description}</div>
                    </div>
                  ))}
                </div>
              )}

              {currentStep === 1 && (
                <div className={styles.paginationContainer}>
                  <Pagination
                    total={total}
                    current={pageNo}
                    pageSize={pageSize}
                    onChange={(pNo, pSize) => {
                      setPageNo(pNo);
                      setPageSize(pSize);
                    }}
                  />
                </div>
              )}

              <div className={currentStep === 2 ? styles.inputParameterVisible : styles.inputParameterHidden}>
                {renderInputParameter()}
              </div>
            </div>
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

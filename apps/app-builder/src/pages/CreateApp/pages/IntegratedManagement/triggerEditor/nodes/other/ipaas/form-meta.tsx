import jsNodeIcon from '@/assets/flow/connect/js_node.svg';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Pagination, Steps } from '@arco-design/web-react';
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
import { useEffect, useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import styles from './index.module.less';

const Step = Steps.Step;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [instanceList, setInstanceList] = useState<any[]>([]);
  const [total, setTotal] = useState(0);
  const [actionList, setActionList] = useState<ScriptActionItem[]>([]);

  const [selectedInstance, setSelectedInstance] = useState<ConnectInstance>();
  const [selectedAction, setSelectedAction] = useState<ScriptActionItem>();
  const [inputParameter, setInputParameter] = useState<string>('{}');
  const [onSwap, setOnSwap] = useState(false);

  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
  };

  const [payloadForm] = Form.useForm();

  const [currentStep, setCurrentStep] = useState(1);

  const handleStepChange = (current: number) => {
    setCurrentStep(current);
  };

  useEffect(() => {
    getConnectInstanceList();
  }, []);

  useEffect(() => {
    if (selectedInstance && selectedInstance.connectorId) {
      handleGetScriptActionList(selectedInstance.connectorId);
    }
  }, [selectedInstance]);

  useEffect(() => {
    if (selectedAction && selectedAction.scriptId) {
      handleGetScriptAction(selectedAction.scriptId);
    }
  }, [selectedAction]);

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
    if (res && res.inputParameter) {
      console.log('res.inputParameter :', res.inputParameter);
      setInputParameter(res.inputParameter);
    }
  };

  const renderInputParameter = () => {
    const inputParameterObj = JSON.parse(inputParameter);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <div className={styles.ipaasContainer}>
            <div className={styles.stepsContainer}>
              <Steps current={currentStep} style={{ margin: '0 auto' }} size="small" onChange={handleStepChange}>
                <Step title="选择动作" />
                <Step title="参数配置" />
              </Steps>
            </div>
            {currentStep === 1 && selectedInstance && (
              <div className={styles.selectedInstance}>
                <div className={styles.selectedInstanceLeft}>
                  <img src={jsNodeIcon} alt="" className={styles.selectIcon} />
                  <div className={styles.instanceItemText}>{selectedInstance.connectorName}</div>
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

            {currentStep === 1 && (onSwap || !selectedInstance) && (
              <div className={styles.instanceList}>
                {instanceList?.map((item: ConnectInstance) => (
                  <div
                    key={item.connectorId}
                    className={styles.instanceItem}
                    onClick={() => {
                      setSelectedInstance(item);
                      setOnSwap(false);
                      setPageNo(1);
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
                    key={item.scriptId}
                    className={styles.actionItem}
                    onClick={() => {
                      setSelectedAction(item);
                      setCurrentStep(2);
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

            {/* {currentStep === 2 && renderInputParameter()} */}
          </div>

          {/* <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            requiredSymbol={{ position: 'end' }}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="title">
              <Input onChange={(e) => handlePropsOnChange('title', e)} />
            </Form.Item>
          </Form> */}
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

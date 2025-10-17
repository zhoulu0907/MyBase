import { Form, Input, Modal, Grid } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { triggerFlowExecForm } from '@onebase/app';
import { FLOW_MODAL_CANCEL, FLOW_MODAL_TYPE, NodeType } from '@onebase/common';

interface Flow {
  [key: string]: any;
}

interface FlowsProps {
  flows: Flow[];
  inputParams: any;
}

interface Field {
  id: string;
  fieldName: string;
  fieldType: string;
  value: string;
}

interface TriggerFlowRes {
  success: boolean;
  triggered: boolean;
  executionUuid: string;
  executionEnd: boolean;
  nodeType: string;
  outputParams: {
    [key: string]: any;
  };
}

const ExecuteFlows: React.FC<FlowsProps> = ({ flows, inputParams }) => {
  let curFlowIndex = 0;
  // 信息收集弹窗
  const [infoForm] = Form.useForm();
  const [infoModalVisibel, setInfoModalVisibel] = useState(false);
  const [flowRespon, setFlowRespon] = useState<any>({});
  const [outputParams, setOutputParams] = useState({
    modalTitle: '',
    modalType: '',
    fields: [],
    arrange: 1,
    okText: '',
    cancelText: ''
  });

  const executeSingleFlow = async (executionUuid?: string, inputFields?: Field[]) => {
    if (curFlowIndex >= flows.length) {
      // 全部流程结束
      curFlowIndex = 0;
      return;
    }
    const param = {
      processId: flows[curFlowIndex].processId,
      executionUuid: executionUuid || '',
      inputParams,
      inputFields
    };
    const res: TriggerFlowRes = await triggerFlowExecForm(param);

    if (res.success) {
      if (res.executionEnd) {
        curFlowIndex++;
        await executeSingleFlow('');
        return;
      }
      // 弹窗
      if (res.nodeType === NodeType.MODAL) {
        // 二次确认
        if (res.outputParams.modalType === FLOW_MODAL_TYPE.CONFIRM) {
          confirmModalFlow(res);
        }
        // 信息收集
        if (res.outputParams.modalType === FLOW_MODAL_TYPE.INFOR) {
          collectInfoModalFlow(res);
        }
      }
    }
  };

  // 弹窗 二次确认
  const confirmModalFlow = (res: TriggerFlowRes) => {
    Modal.confirm({
      title: res.outputParams.modalTitle || '确认',
      content: res.outputParams.prompt || '',
      okText: res.outputParams.okText || '确认',
      cancelText: res.outputParams.cancelText || '取消',
      maskClosable: false,
      escToExit: false,
      unmountOnExit: true,
      onOk: async () => {
        const executionUuid = res.executionUuid || '';
        console.log('二次确认executionUuid', executionUuid);
        await executeSingleFlow(executionUuid);
      },
      onCancel: async () => {
        // 弹窗取消后事件终止
        if (res.outputParams.afterCancel === FLOW_MODAL_CANCEL.STOP) {
          curFlowIndex++;
          await executeSingleFlow();
          return;
        }
        const executionUuid = res.executionUuid || '';
        console.log('二次确认取消executionUuid', executionUuid);
        await executeSingleFlow(executionUuid);
      }
    });
  };

  // 打开信息收集弹窗
  const collectInfoModalFlow = async (res: TriggerFlowRes) => {
    setFlowRespon(res);
    console.log('flowRespon', flowRespon);
    setInfoModalVisibel(true);
    setOutputParams({ ...outputParams, ...res.outputParams });
  };

  // 收集信息弹窗 确定按钮
  const cofirmInfoModal = async () => {
    setInfoModalVisibel(false);
    const infoFormData = infoForm.getFieldsValue();
    const inputFields = outputParams.fields.map((item: any) => {
      return { ...item, value: infoFormData[item.fieldName] };
    });
    const executionUuid = flowRespon.executionUuid || '';
    console.log('收集信息executionUuid', flowRespon);
    await executeSingleFlow(executionUuid, inputFields);
  };
  // 收集信息弹窗 取消按钮
  const cancaelInfoModal = async () => {
    setInfoModalVisibel(false);
    if (flowRespon.outputParams?.afterCancel === FLOW_MODAL_CANCEL.STOP) {
      curFlowIndex++;
      await executeSingleFlow('');
      return;
    }
    const executionUuid = flowRespon.executionUuid || '';
    console.log('收集信息取消', flowRespon);

    await executeSingleFlow(executionUuid);
  };

  useEffect(() => {
    if (flows?.length) {
      executeSingleFlow();
    }
  }, [flows]);

  return (
    <>
      <Modal
        visible={infoModalVisibel}
        title={outputParams.modalTitle}
        okText={outputParams.okText}
        cancelText={outputParams.cancelText}
        onOk={cofirmInfoModal}
        onCancel={cancaelInfoModal}
        unmountOnExit
        escToExit={false}
        maskClosable={false}
        style={{ width: 600 }}
      >
        <Form layout="vertical" form={infoForm}>
          <Grid.Row gutter={12}>
            {outputParams.fields.map((cp: any) => (
              <Grid.Col span={outputParams.arrange === 2 ? 12 : 24} key={cp.id}>
                <Form.Item label={cp.fieldName} field={cp.fieldName}>
                  <Input placeholder="请输入" />
                </Form.Item>
              </Grid.Col>
            ))}
          </Grid.Row>
        </Form>
      </Modal>
    </>
  );
};

export default ExecuteFlows;

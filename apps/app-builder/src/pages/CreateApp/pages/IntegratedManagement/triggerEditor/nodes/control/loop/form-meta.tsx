import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import type { ConditionField } from '@onebase/app';
import { NodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import { BreakMode } from '../../../components/const';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getPrecedingNodes, validateNodeForm } from '../../utils';

const ALLOW_DATANODE_TYPES = [NodeType.DATA_QUERY_MULTIPLE];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();

  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();

  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    const nodes = triggerEditorSignal.nodes.value;
    const newDataNodeList = getPrecedingNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
    setDataNodeList(newDataNodeList);
  };

  //   const handleDateNodeSourceChange = async (dataNodeId: string) => {
  //     const nodes = triggerEditorSignal.nodes.value;

  //     const newDataNodeList = getPrecedingNodes(node.id, nodes, [NodeType.DATA_QUERY_MULTIPLE]);
  //     setDataNodeList(newDataNodeList);

  //     const originDataSource = getDataNodeSource(dataNodeId);
  //     console.log('originDataSource: ', originDataSource);

  //     getEntityFieldList(originDataSource, handleSetConditionFields, () => {});

  //   };

  const handleSetConditionFields = (conditionFields: ConditionField[]) => {
    console.log(conditionFields);
  };

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} initialValues={getInitData()} layout="vertical" requiredSymbol={{ position: 'end' }}>
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                从
              </Grid.Col>
              <Grid.Col span={19}>
                <Form.Item field="dataNodeId">
                  <Select allowClear>
                    {dataNodeList.map((item) => (
                      <Select.Option key={item.id} value={item.id}>
                        {item.data.title}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                <span>中查询数据</span>
              </Grid.Col>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="执行出现异常时，阻断模式" field="breakMode">
                <Radio.Group>
                  <Radio value={BreakMode.Break}>直接阻断，跳出循环</Radio>
                  <Radio value={BreakMode.Continue}>跳过并执行下一条数据</Radio>
                </Radio.Group>
              </Form.Item>
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

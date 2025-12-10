import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { DATA_SOURCE_TYPE } from '@onebase/app';
import { NodeType } from '@onebase/common';
import { useEffect, useState } from 'react';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useEntityFields, useIsSidebar, useMainEntityList, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getPrecedingNodes, validateNodeForm } from '../../utils';

const RadioGroup = Radio.Group;

const ALLOW_DATANODE_TYPES = [NodeType.DATA_QUERY_MULTIPLE, NodeType.DATA_QUERY, NodeType.DATA_CALC];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntityList } = useMainEntityList();
  const {
    subEntityList,
    fieldDataList,
    handleMainTableChange,
    handleSubTableChange,
    setFieldDataListDirectly,
    resetFields
  } = useEntityFields();

  const [dataNodeList, setDataNodeList] = useState<any[]>([]);
  const [payloadForm] = Form.useForm();

  const addType = Form.useWatch('addType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const batchType = Form.useWatch('batchType', payloadForm);
  const dataNodeId = Form.useWatch('dataNodeId', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    const nodes = triggerEditorSignal.nodes.value;
    const newDataNodeList = getPrecedingNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
    console.log('newDataNodeList: ', newDataNodeList);
    setDataNodeList(newDataNodeList);
  }, []);

  useEffect(() => {
    // 初始化字段数据
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData?.addType === DATA_SOURCE_TYPE.MAIN_TABLE && nodeData?.mainTableName && mainEntityList.length > 0) {
      setFieldDataListDirectly(nodeData.mainTableName, mainEntityList);
    } else if (
      nodeData?.addType === DATA_SOURCE_TYPE.SUB_TABLE &&
      nodeData?.mainTableName &&
      mainEntityList.length > 0
    ) {
      handleMainTableChange(nodeData.mainTableName, mainEntityList, { onlySubEntityList: true }).then(() => {
        if (nodeData?.subTableName && subEntityList.length > 0) {
          handleSubTableChange(nodeData.subTableName, subEntityList);
        }
      });
    }
  }, [mainEntityList.length, subEntityList.length]);

  // 新增方式变更
  const handleDataTypeChange = (curAddType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'dataNodeId', 'fields']);
    resetFields();
  };

  // 主表数据变更
  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'dataNodeId', 'fields']);

    if (addType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      await setFieldDataListDirectly(curMainTableName, mainEntityList);
    } else if (addType === DATA_SOURCE_TYPE.SUB_TABLE) {
      await handleMainTableChange(curMainTableName, mainEntityList, { onlySubEntityList: true });
    }
  };

  // 子表数据变更
  const handleSubTableNameChange = async (curSubTableName: string) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
    await handleSubTableChange(curSubTableName, subEntityList);
  };

  const handleBatchTypeChange = (value: boolean) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
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
            layout="vertical"
          >
            <Grid.Row>
              <Form.Item
                label="节点ID"
                field="id"
                initialValue={node.id}
                rules={[
                  {
                    required: true
                  }
                ]}
              >
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="新增方式" field="addType" rules={[{ required: true, message: '请选择新增方式' }]}>
                <RadioGroup onChange={handleDataTypeChange}>
                  <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>在主表中新增</Radio>
                  <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>在子表中新增</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中插入 */}
            {addType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainTableName" disabled={!addType} rules={[{ required: true, message: '请选择' }]}>
                    <Select onChange={handleMainTableNameChange} allowClear>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  <span>中插入数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中查询 */}
            {addType === DATA_SOURCE_TYPE.SUB_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainTableName" disabled={!addType} rules={[{ required: true, message: '请选择' }]}>
                    <Select allowClear onChange={handleMainTableNameChange}>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  的
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item
                    field="subTableName"
                    disabled={!mainTableName}
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select allowClear onChange={handleSubTableNameChange}>
                      {subEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  <span>中插入数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            <Grid.Row>
              <Form.Item label="新增数据" field="batchType" rules={[{ required: true, message: '请选择新增数据' }]}>
                <RadioGroup onChange={handleBatchTypeChange}>
                  <Radio value={false}>新增单条数据</Radio>
                  <Radio value={true}>新增多条数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {batchType ? (
              <>
                <Grid.Row>
                  <Form.Item label="数据源" field="dataNodeId">
                    <Select allowClear>
                      {dataNodeList.map((item) => (
                        <Select.Option key={item.id} value={item.id}>
                          {item.data.title}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Row>
                <Grid.Row>
                  <Form.Item label="字段设置">
                    <FieldEditor
                      nodeId={node.id}
                      fieldList={fieldDataList}
                      form={payloadForm}
                      dataNodeId={dataNodeId}
                    />
                  </Form.Item>
                </Grid.Row>
              </>
            ) : (
              <Grid.Row>
                <Form.Item label="字段设置">
                  <FieldEditor nodeId={node.id} fieldList={fieldDataList} form={payloadForm} />
                </Form.Item>
              </Grid.Row>
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

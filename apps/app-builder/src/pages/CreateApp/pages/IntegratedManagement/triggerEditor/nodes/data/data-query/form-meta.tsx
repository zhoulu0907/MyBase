import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { DATA_SOURCE_TYPE, FILTER_TYPE, type ConditionField } from '@onebase/app';
import { NodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useEntityFields, useIsSidebar, useMainEntityList, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getDataNodeSource, getPrecedingNodes, validateNodeForm } from '../../utils';

const ALLOW_DATANODE_TYPES = [
  NodeType.DATA_QUERY,
  NodeType.DATA_QUERY_MULTIPLE,
  NodeType.LOOP,
  NodeType.IF,
  NodeType.IF_BLOCK,
  NodeType.CASE,
  NodeType.CASE_DEFAULT
];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntityList } = useMainEntityList();
  const {
    subEntityList,
    mainEntityFields,
    subEntityFields,
    dataNodeEntityFields,
    validationTypes,
    handleMainTableChange,
    handleSubTableChange,
    handleDataNodeChange,
    resetFields
  } = useEntityFields();

  const [dataNodeList, setDataNodeList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<ConditionField[]>([]);
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const subTableName = Form.useWatch('subTableName', payloadForm);
  const filterType = Form.useWatch('filterType', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    const nodes = triggerEditorSignal.nodes.value;
    const newDataNodeList = getPrecedingNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
    setDataNodeList(newDataNodeList);

    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData?.dataType && mainEntityList.length > 0) {
      if (
        (nodeData.dataType === DATA_SOURCE_TYPE.MAIN_TABLE || nodeData.dataType === DATA_SOURCE_TYPE.SUB_TABLE) &&
        nodeData?.mainTableName
      ) {
        handleMainTableChange(nodeData.mainTableName, mainEntityList).then(() => {
          if (nodeData.dataType === DATA_SOURCE_TYPE.SUB_TABLE && nodeData?.subTableName) {
            setTimeout(() => {
              handleSubTableChange(nodeData.subTableName, subEntityList);
            }, 100);
          }
        });
      } else if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE && nodeData?.dataNodeId) {
        handleDataNodeChange(nodeData.dataNodeId, getDataNodeSource);
      }
    }
  }, [mainEntityList.length]);

  // 查询方式变更
  const handleDataTypeChange = (curDataType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition', 'sortBy']);
    resetFields();
  };

  // 主表数据变更
  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'dataNodeId', 'filterCondition', 'sortBy']);
    await handleMainTableChange(curMainTableName, mainEntityList);
  };

  // 子表数据变更
  const handleSubTableNameChange = (_curSubTableName: string) => {
    payloadForm.clearFields(['dataNodeId', 'filterCondition', 'sortBy']);
  };

  // 数据节点变更
  const handleDateNodeSourceChange = async (dataNodeId: string) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition', 'sortBy']);
    resetFields();
    await handleDataNodeChange(dataNodeId, getDataNodeSource);
  };

  const handleFilterTypeChange = (_value: FILTER_TYPE) => {
    payloadForm.clearFields(['filterCondition']);
  };

  const conditionFieldsData = useMemo((): TreeSelectDataType[] => {
    if (dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      return [mainEntityFields];
    }
    if (dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subTableName);
      if (curSubEntityFields) {
        return [mainEntityFields, curSubEntityFields];
      }
      return [mainEntityFields];
    }
    if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      return [dataNodeEntityFields];
    }
    return [];
  }, [dataType, mainEntityFields, subEntityFields, mainTableName, subTableName, dataNodeEntityFields]);

  const conditionFieldsForEditor = useMemo((): ConditionField[] => {
    if (dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      return (
        (mainEntityFields.children || [])?.map((item) => ({
          label: item.title as string,
          value: item.key as string,
          fieldType: item.fieldType
        })) || []
      );
    }
    if (dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subTableName);
      if (curSubEntityFields) {
        return (
          (curSubEntityFields.children || [])?.map((item) => ({
            label: item.title as string,
            value: item.key as string,
            fieldType: item.fieldType
          })) || []
        );
      }
      return [];
    }
    if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      return (
        (dataNodeEntityFields.children || [])?.map((item) => ({
          label: item.title as string,
          value: item.key as string,
          fieldType: item.fieldType
        })) || []
      );
    }
    return [];
  }, [dataType, mainEntityFields, subEntityFields, mainTableName, subTableName, dataNodeEntityFields]);

  useEffect(() => {
    setConditionFields(conditionFieldsForEditor);
  }, [conditionFieldsForEditor]);

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout="vertical" initialValues={getInitData()} requiredSymbol={{ position: 'end' }}>
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>

            <Form.Item label="查询方式" field="dataType" rules={[{ required: true, message: '请选择查询方式' }]}>
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>从主表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>从子表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从数据节点中查询</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 从主表中查询 */}
            {dataType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainTableName" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
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
                  <span>中查询数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中查询 */}
            {dataType === DATA_SOURCE_TYPE.SUB_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainTableName" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
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
                  <span>中查询数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从数据节点中查询 */}
            {dataType === DATA_SOURCE_TYPE.DATA_NODE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="dataNodeId" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
                    <Select onChange={handleDateNodeSourceChange} allowClear>
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
            )}

            <Grid.Row>
              <Form.Item label="查询规则" field="filterType" rules={[{ required: true, message: '请选择查询规则' }]}>
                <Radio.Group onChange={handleFilterTypeChange}>
                  <Radio value={FILTER_TYPE.ALL}>全部数据</Radio>
                  <Radio value={FILTER_TYPE.CONDITION}>按条件过滤</Radio>
                </Radio.Group>
              </Form.Item>
            </Grid.Row>

            {filterType === FILTER_TYPE.CONDITION && (
              <Grid.Row>
                <ConditionEditor
                  nodeId={node.id}
                  label="条件"
                  required
                  fields={conditionFieldsData}
                  entityFieldValidationTypes={validationTypes}
                  form={payloadForm}
                />
              </Grid.Row>
            )}

            <Grid.Row>
              <Form.Item label="排序规则">
                <SortByEditor
                  data={triggerEditorSignal.nodeData.value[node.id]?.sortBy || []}
                  fields={conditionFields}
                  form={payloadForm}
                />
              </Form.Item>
            </Grid.Row>
            <div style={{ color: '#4e5969' }}>仅查询排序的第一条数据</div>
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

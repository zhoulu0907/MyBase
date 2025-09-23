import { useAppStore } from '@/store';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  type ConfitionField,
  DATA_SOURCE_TYPE,
  type EntityFieldValidationTypes,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  type MetadataEntityPair
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { NodeType } from '../../const';
import {
  clearDataOriginNodeId,
  getBeforeCurQueryNodes,
  getDataNodeSource,
  getEntityFieldList,
  validateNodeForm
} from '../../utils';

const ALLOW_DATANODE_TYPES = [NodeType.DATA_ADD, NodeType.DATA_UPDATE, NodeType.DATA_QUERY, NodeType.START_ENTITY];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();

  const { curAppId } = useAppStore();
  const { node } = useNodeRenderContext();
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);
  const mainEntityId = Form.useWatch('mainEntityId', payloadForm);

  // 数据源选择
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    getEntityAndDataNodeList();
  }, []);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  const handleDataTypeChange = (curDataType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainEntityId', 'subEntityId', 'dataNodeId', 'filterCondition']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainEntityId: undefined,
      subEntityId: undefined,
      dataNodeId: undefined,
      filterCondition: []
    });

    setMainEntityList([]);
    setSubEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    getEntityAndDataNodeList(curDataType);

    clearDataOriginNodeId(node.id);
  };

  const handleMainEntityIdChange = async (curMainEntityId: string) => {
    payloadForm.clearFields(['subEntityId', 'dataNodeId', 'filterCondition']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subEntityId: undefined,
      dataNodeId: undefined,
      filterCondition: []
    });

    setSubEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    const res = await getEntityFieldsWithChildren(curMainEntityId);
    const newEntityList = (res.childEntities || []).map((item: any) => {
      return {
        entityId: item.childEntityId,
        entityName: item.childEntityName
      };
    });
    setSubEntityList(newEntityList);
    if (dataType !== DATA_SOURCE_TYPE.SUBFORM && curMainEntityId) {
      getFieldList(curMainEntityId);
    }

    clearDataOriginNodeId(node.id);
  };

  const handleSubEntityIdChange = (curSubEntityId: string) => {
    payloadForm.clearFields(['dataNodeId', 'filterCondition']);
    setConditionFields([]);
    setValidationTypes([]);

    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataNodeId: undefined,
      filterCondition: []
    });
    // 根据数据源重新获取字段列表
    if (curSubEntityId) {
      getFieldList(curSubEntityId);
    }

    clearDataOriginNodeId(node.id);
  };

  const handleDateNodeSourceChange = async (dataNodeId: string) => {
    payloadForm.clearFields(['mainEntityId', 'subEntityId', 'filterCondition']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainEntityId: undefined,
      subEntityId: undefined,
      filterCondition: []
    });

    setMainEntityList([]);
    setSubEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    const nodes = triggerEditorSignal.nodes.value;

    const newDataNodeList = getBeforeCurQueryNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
    setDataNodeList(newDataNodeList);

    clearDataOriginNodeId(node.id);
    getFieldList(dataNodeId);
  };

  // 获取各类数据源列表，不传值获取全部(用于初始化)
  const getEntityAndDataNodeList = async (curDateType?: DATA_SOURCE_TYPE) => {
    if (curDateType === DATA_SOURCE_TYPE.FORM || curDateType === undefined) {
      // 从主表中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
    if (curDateType === DATA_SOURCE_TYPE.ASSOCIA_FORM || curDateType === undefined) {
      // 从关联表单中查询  ASSOCIA_FORM
    }

    if (curDateType === DATA_SOURCE_TYPE.SUBFORM || curDateType === undefined) {
      // 从子表中查询  SUBFORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }

    if (curDateType === DATA_SOURCE_TYPE.DATA_NODE || curDateType === undefined) {
      // 从上游数据节点查询
      const nodes = triggerEditorSignal.nodes.value;
      // 过滤掉当前节点,过滤blocks,并且只能选当前节点之前的节点
      const newDataNodeList = getBeforeCurQueryNodes(node.id, nodes, ALLOW_DATANODE_TYPES);

      setDataNodeList(newDataNodeList);
    }

    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (!nodeData) {
      return;
    }
    if (nodeData.dataType === DATA_SOURCE_TYPE.FORM) {
      getEntityFieldList(nodeData.mainEntityId, setConditionFields, setValidationTypes);
    } else if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      const originDataSource = getDataNodeSource(nodeData.dataNodeId);
      getEntityFieldList(originDataSource, setConditionFields, setValidationTypes);
    } else if (nodeData.dataType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
      getEntityFieldList(nodeData.subEntityId, setConditionFields, setValidationTypes);
    }
  };

  // 获取排序字段下拉列表
  const getFieldList = async (dataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // 根据不同获取方式走不同接口
    if (dataType === DATA_SOURCE_TYPE.FORM || dataType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从主表中查询/从子表中查询
      getEntityFieldList(dataSource, setConditionFields, setValidationTypes);
    } else if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE
      const originDataSource = getDataNodeSource(dataSource);
      getEntityFieldList(originDataSource, setConditionFields, setValidationTypes);
    } else if (dataType === DATA_SOURCE_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    }
  };

  // 表单内容改变
  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    // 校验表单
    validateNodeForm(form, payloadForm, false);

    handlePropsOnChange(values);
  };

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} initialValues={getInitData()} onValuesChange={onValuesChange} layout="vertical">
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="数据获取方式" field="dataType">
                <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                  <Radio value={DATA_SOURCE_TYPE.FORM}>从主表中查询</Radio>
                  <Radio value={DATA_SOURCE_TYPE.SUBFORM}>从子表中查询</Radio>
                  <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从上游节点中查询</Radio>
                  {/* <Radio value={DATA_SOURCE_TYPE.ASSOCIA_FORM}>从关联表单中查询</Radio> */}
                </Radio.Group>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中查询 */}
            {dataType === DATA_SOURCE_TYPE.FORM && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainEntityId" disabled={!dataType}>
                    <Select onChange={handleMainEntityIdChange} allowClear>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  <span>中获取数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中查询 */}
            {dataType === DATA_SOURCE_TYPE.SUBFORM && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainEntityId" disabled={!dataType}>
                    <Select allowClear onChange={handleMainEntityIdChange}>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
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
                  <Form.Item field="subEntityId" disabled={!mainEntityId}>
                    <Select allowClear onChange={handleSubEntityIdChange}>
                      {subEntityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
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
                  <Form.Item field="dataNodeId" disabled={!dataType}>
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
              <ConditionEditor
                label="条件"
                required
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
                form={payloadForm}
              />
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

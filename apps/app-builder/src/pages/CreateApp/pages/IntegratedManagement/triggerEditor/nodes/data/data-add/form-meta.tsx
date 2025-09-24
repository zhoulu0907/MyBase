import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  FLOW_ENTITY_TYPE,
  getEntityFields,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  type AppEntityField,
  type ConfitionField,
  type MetadataEntityPair
} from '@onebase/app';
import { useEffect, useState } from 'react';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { NodeType } from '../../const';
import { getBeforeCurQueryNodes, validateNodeForm } from '../../utils';
import { updateDataAddOutputs } from './output';

const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  const [payloadForm] = Form.useForm();

  const addType = Form.useWatch('addType', payloadForm);
  const mainEntityId = Form.useWatch('mainEntityId', payloadForm);
  const batchType = Form.useWatch('batchType', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      if (nodeData.addType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
        // 在主表中
        const res = await getEntityListByApp(curAppId);
        setMainEntityList(res);
        getFieldList(nodeData?.mainEntityId);
      }
      if (nodeData.addType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
        // 在子表中
        const res = await getEntityListByApp(curAppId);
        setMainEntityList(res);
        if (nodeData?.mainEntityId) {
          const res = await getEntityFieldsWithChildren(nodeData.mainEntityId);
          const newEntityList = (res.childEntities || []).map((item: any) => {
            return {
              entityId: item.childEntityId,
              entityName: item.childEntityName
            };
          });
          setSubEntityList(newEntityList);
          getFieldList(nodeData.subEntityId);
        }
      }
    }

    const nodes = triggerEditorSignal.nodes.value;
    const newDataNodeList = getBeforeCurQueryNodes(node.id, nodes, [NodeType.DATA_QUERY_MULTIPLE, NodeType.DATA_QUERY]);
    setDataNodeList(newDataNodeList);
  };

  // 新增方式变更
  const handleDataTypeChange = (curAddType: FLOW_ENTITY_TYPE) => {
    payloadForm.clearFields(['mainEntityId', 'subEntityId', 'dataNodeId', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainEntityId: undefined,
      subEntityId: undefined,
      fields: []
    });
    setMainEntityList([]);
    setSubEntityList([]);
    setConditionFields([]);
    setFieldDataList([]);
    setMainEntityList([]);
    getEntityList(curAddType);
  };

  const getEntityList = async (curAddType?: FLOW_ENTITY_TYPE) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (curAddType === FLOW_ENTITY_TYPE.MAIN_ENTITY || curAddType === undefined) {
      // 从主表中
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
      getFieldList(nodeData?.mainEntityId);
    }
    if (curAddType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
      // 从子表中
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };
  // 主表数据变更
  const handleMainEntityIdChange = async (curMainEntityId: string) => {
    payloadForm.clearFields(['subEntityId', 'dataNodeId', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subEntityId: undefined,
      fields: []
    });

    setConditionFields([]);
    setFieldDataList([]);

    if (addType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
      getFieldList(curMainEntityId);
    }
    if (addType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
      setSubEntityList([]);
      const res = await getEntityFieldsWithChildren(curMainEntityId);
      const newEntityList = (res.childEntities || []).map((item: any) => {
        return {
          entityId: item.childEntityId,
          entityName: item.childEntityName
        };
      });
      setSubEntityList(newEntityList);
    }
  };
  // 子表数据变更
  const handleSubEntityIdChange = (curSubEntityId: string) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      fields: []
    });
    setConditionFields([]);
    setFieldDataList([]);
    getFieldList(curSubEntityId);
  };

  // 获取字段下拉列表
  const getFieldList = async (dataSource: string) => {
    if (!dataSource) {
      return;
    }
    const res = await getEntityFields({ entityId: dataSource });
    const newConditionFields: ConfitionField[] = [];
    res.forEach((item: any) => {
      item.fieldId = item.id;

      newConditionFields.push({
        label: item.displayName,
        value: item.id,
        fieldType: item.fieldType
      });
    });

    setConditionFields(newConditionFields);
    setFieldDataList(res);
  };

  const handleBatchTypeChange = (value: boolean) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
  };

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (values: any) => {
    // 校验表单
    validateNodeForm(form, payloadForm, false);

    updateDataAddOutputs(node.id, values, conditionFields);

    handlePropsOnChange(values);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            onChange={onValuesChange}
            layout="vertical"
          >
            <Grid.Row>
              <Form.Item
                label="节点ID"
                field="id"
                initialValue={node.id}
                rules={[
                  {
                    required: true,
                    message: '请选择节点ID'
                  }
                ]}
              >
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="新增方式" field="addType" rules={[{ required: true, message: '请选择新增方式' }]}>
                <RadioGroup onChange={handleDataTypeChange}>
                  <Radio value={FLOW_ENTITY_TYPE.MAIN_ENTITY}>在主表中新增</Radio>
                  <Radio value={FLOW_ENTITY_TYPE.SUB_ENTITY}>在子表中新增</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中插入 */}
            {addType === FLOW_ENTITY_TYPE.MAIN_ENTITY && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainEntityId" disabled={!addType}>
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
                  <span>中插入数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中查询 */}
            {addType === FLOW_ENTITY_TYPE.SUB_ENTITY && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainEntityId" disabled={!addType}>
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
            ) : (
              <Grid.Row>
                <Form.Item label="字段设置">
                  <FieldEditor fieldList={fieldDataList} form={payloadForm} />
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

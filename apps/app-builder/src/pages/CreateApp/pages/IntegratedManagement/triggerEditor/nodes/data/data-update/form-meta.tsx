import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import {
  FLOW_ENTITY_TYPE,
  getEntityListByApp,
  getEntityFields,
  getEntityFieldsWithChildren,
  getFieldCheckTypeApi,
  type ConfitionField,
  type AppEntityField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';
import { useAppStore } from '@/store/store_app';
import FieldEditor from '../../../components/field-editor';

const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    // 校验表单
    validateNodeForm(form, payloadForm, false);

    handlePropsOnChange(values);
  };

  const [payloadForm] = Form.useForm();

  const updateType = Form.useWatch('updateType', payloadForm);
  const mainDataSource = Form.useWatch('mainDataSource', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      if (nodeData.updateType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
        // 在主表中
        const res = await getEntityListByApp(curAppId);
        setEntityList(res);
        getFieldList(nodeData?.mainDataSource);
      }
      if (nodeData.updateType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
        // 在子表中
        const res = await getEntityListByApp(curAppId);
        setMainEntityList(res);
        if (nodeData?.mainDataSource) {
          const res = await getEntityFieldsWithChildren(nodeData.mainDataSource);
          const newEntityList = (res.childEntities || []).map((item: any) => {
            return {
              entityId: item.childEntityId,
              entityName: item.childEntityName
            };
          });
          setEntityList(newEntityList);
          getFieldList(nodeData.subDataSource);
        }
      }
    }
  };

  // 方式变更
  const handleDataTypeChange = (curUpdateType: FLOW_ENTITY_TYPE) => {
    payloadForm.clearFields(['mainDataSource', 'subDataSource', 'filterCondition','fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainDataSource: undefined,
      subDataSource: undefined,
      filterCondition: [],
      fields: []
    });
    setEntityList([]);
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    setMainEntityList([]);
    getEntityList(curUpdateType);
  };
  const getEntityList = async (curUpdateType?: FLOW_ENTITY_TYPE) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (curUpdateType === FLOW_ENTITY_TYPE.MAIN_ENTITY || curUpdateType === undefined) {
      // 从主表中
      const res = await getEntityListByApp(curAppId);
      setEntityList(res);
      getFieldList(nodeData?.mainDataSource);
    }
    if (curUpdateType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
      // 从子表中
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };
  // 主表数据变更
  const handleMainDataSourceChange = async (curMainDataSource: string) => {
    payloadForm.clearFields(['subDataSource', 'filterCondition','fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subDataSource: undefined,
      filterCondition: [],
      fields: []
    });
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    if (updateType === FLOW_ENTITY_TYPE.MAIN_ENTITY) {
      getFieldList(curMainDataSource);
    }
    if (updateType === FLOW_ENTITY_TYPE.SUB_ENTITY) {
      setEntityList([]);
      const res = await getEntityFieldsWithChildren(curMainDataSource);
      const newEntityList = (res.childEntities || []).map((item: any) => {
        return {
          entityId: item.childEntityId,
          entityName: item.childEntityName
        };
      });
      setEntityList(newEntityList);
    }
  };
  // 子表数据变更
  const handleSubDataSourceChange = (curSubDataSource: string) => {
    payloadForm.clearFields(['filterCondition','fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      filterCondition: [],
      fields:[]
    });
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    getFieldList(curSubDataSource);
  };
  // 获取字段下拉列表
  const getFieldList = async (dataSource: string) => {
    if (!dataSource) {
      return;
    }
    const res = await getEntityFields({ entityId: dataSource });
    const filedIds: string[] = [];
    const newConditionFields: ConfitionField[] = [];
    res.forEach((item: any) => {
      item.fieldId = item.id;
      filedIds.push(item.id);
      newConditionFields.push({
        label: item.displayName,
        value: item.id,
        fieldType: item.fieldType
      });
    });
    setConditionFields(newConditionFields);
    setValidationTypes([]);
    setFieldDataList(res);
    if (filedIds?.length) {
      const newValidationTypes = await getFieldCheckTypeApi(filedIds);
      console.log('validationTypes: ', newValidationTypes);
      setValidationTypes(newValidationTypes);
    }
  };

  const filterConditionChange = (e: any[]) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subDataSource: undefined,
      filterCondition: e || []
    });
    payloadForm.setFieldValue('filterCondition', e);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            onValuesChange={onValuesChange}
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
              <Form.Item label="更新方式" field="updateType" rules={[{ required: true, message: '请选择更新方式' }]}>
                <RadioGroup onChange={handleDataTypeChange}>
                  <Radio value={FLOW_ENTITY_TYPE.MAIN_ENTITY}>更新主表数据</Radio>
                  <Radio value={FLOW_ENTITY_TYPE.SUB_ENTITY}>更新子表数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中 */}
            {updateType === FLOW_ENTITY_TYPE.MAIN_ENTITY && (
              <Grid.Row>
                <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainDataSource" disabled={!updateType}>
                    <Select onChange={handleMainDataSourceChange} allowClear>
                      {entityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={3} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  的数据
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中 */}
            {updateType === FLOW_ENTITY_TYPE.SUB_ENTITY && (
              <Grid.Row>
                <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainDataSource" disabled={!updateType}>
                    <Select allowClear onChange={handleMainDataSourceChange}>
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
                  <Form.Item field="subDataSource" disabled={!mainDataSource}>
                    <Select allowClear onChange={handleSubDataSourceChange}>
                      {entityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={3} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  的数据
                </Grid.Col>
              </Grid.Row>
            )}

            <Grid.Row>
              <Form.Item label="匹配规则" field="filterCondition" required>
                <ConditionEditor
                  data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                  onChange={filterConditionChange}
                />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="更新规则" field="fields">
                <FieldEditor fieldList={fieldDataList} form={payloadForm} />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="未匹配到数据时" field="noData">
                <RadioGroup defaultValue="skip">
                  <Radio value="skip">跳过当前节点</Radio>
                  <Radio value="add">新增一条数据</Radio>
                </RadioGroup>
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

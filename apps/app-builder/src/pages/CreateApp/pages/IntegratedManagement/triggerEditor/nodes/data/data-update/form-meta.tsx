import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  getEntityFields,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type AppEntityField,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';
import { updateDataUpdateOutputs } from './output';

const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);

  const [payloadForm] = Form.useForm();

  const updateType = Form.useWatch('updateType', payloadForm);
  const mainEntityId = Form.useWatch('mainEntityId', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      if (nodeData.updateType === DATA_SOURCE_TYPE.FORM) {
        // 在主表中
        const res = await getEntityListByApp(curAppId);
        setMainEntityList(res);
        getFieldList(nodeData?.mainEntityId);
      }
      if (nodeData.updateType === DATA_SOURCE_TYPE.SUBFORM) {
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
  };

  // 方式变更
  const handleDataTypeChange = (curUpdateType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainEntityId', 'subEntityId', 'filterCondition', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainEntityId: undefined,
      subEntityId: undefined,
      filterCondition: [],
      fields: []
    });
    setMainEntityList([]);
    setSubEntityList([]);
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    setMainEntityList([]);
    getEntityList(curUpdateType);
  };
  const getEntityList = async (curUpdateType?: DATA_SOURCE_TYPE) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (curUpdateType === DATA_SOURCE_TYPE.FORM || curUpdateType === undefined) {
      // 从主表中
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
      getFieldList(nodeData?.mainEntityId);
    }
    if (curUpdateType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从子表中
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };
  // 主表数据变更
  const handleMainEntityIdChange = async (curMainEntityId: string) => {
    payloadForm.clearFields(['subEntityId', 'filterCondition', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subEntityId: undefined,
      filterCondition: [],
      fields: []
    });
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    if (updateType === DATA_SOURCE_TYPE.FORM) {
      getFieldList(curMainEntityId);
    }
    if (updateType === DATA_SOURCE_TYPE.SUBFORM) {
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
    payloadForm.clearFields(['filterCondition', 'fields']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      filterCondition: [],
      fields: []
    });
    setConditionFields([]);
    setFieldDataList([]);
    setValidationTypes([]);
    getFieldList(curSubEntityId);
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
      setValidationTypes(newValidationTypes);
    }
  };

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    // 校验表单
    validateNodeForm(form, payloadForm, false);

    updateDataUpdateOutputs(node.id, values, conditionFields);

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
                  <Radio value={DATA_SOURCE_TYPE.FORM}>更新主表数据</Radio>
                  <Radio value={DATA_SOURCE_TYPE.SUBFORM}>更新子表数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中 */}
            {updateType === DATA_SOURCE_TYPE.FORM && (
              <Grid.Row>
                <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainEntityId" disabled={!updateType}>
                    <Select onChange={handleMainEntityIdChange} allowClear>
                      {mainEntityList.map((item) => (
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
            {updateType === DATA_SOURCE_TYPE.SUBFORM && (
              <Grid.Row>
                <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainEntityId" disabled={!updateType}>
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
                <Grid.Col span={3} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  的数据
                </Grid.Col>
              </Grid.Row>
            )}

            <Grid.Row>
              <ConditionEditor
                nodeId={node.id}
                label="匹配规则"
                required
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
                form={payloadForm}
              />
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="更新规则" field="fields">
                <FieldEditor nodeId={node.id} fieldList={fieldDataList} form={payloadForm} />
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

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  getComponentListByPageId,
  getEntityFieldsWithChildren,
  getFieldCheckTypeApi,
  getPageListByAppId,
  getPageMetadata,
  TRIGGER_EVENTS,
  type AppEntityField,
  type ChildEntity,
  type ComponentConfig,
  type ConditionField,
  type EntityFieldValidationTypes,
  type MetadataEntityField
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { TriggerRange } from '../../../components/const';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { updateStartFormOutputs } from './output';

const CheckboxGroup = Checkbox.Group;
const Option = Select.Option;
const RadioGroup = Radio.Group;

const sortEntityFields = (a: MetadataEntityField, b: MetadataEntityField): number => {
  if (a.isSystemField !== b.isSystemField) {
    return a.isSystemField ? 1 : -1;
  }
  return 0;
};

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [pageList, setPageList] = useState<any[]>([]);
  const [componentList, setComponentList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<TreeSelectDataType[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [payloadForm] = Form.useForm();
  const pageUuid = Form.useWatch('pageUuid', payloadForm);
  const triggerRange = Form.useWatch('triggerRange', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetPageList(appId);
    }
  }, []);

  useEffect(() => {
    if (pageUuid) {
      handleGetComponentList(pageUuid);
      handleGetFieldList(pageUuid);
    }
  }, [pageUuid]);

  useEffect(() => {
    if (triggerRange === TriggerRange.Record || triggerRange === TriggerRange.Field) {
      payloadForm.setFieldsValue({
        pageUuid: triggerEditorSignal.nodeData.value[node.id].pageUuid
      });
    }
  }, [triggerRange]);

  const handleGetPageList = async (appId: string) => {
    const res = await getPageListByAppId({ appId });
    setPageList(res.pages);
  };

  const handleTriggerRangeChange = (value: string) => {
    payloadForm.clearFields(['fieldId', 'recordTriggerEvents', 'fieldTriggerEvents']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      recordTriggerEvents: undefined,
      fieldTriggerEvents: undefined,
      filterCondition: []
    });
  };

  const handleGetFieldList = async (pageUuid: string) => {
    const res = await getPageMetadata({ pageUuid: pageUuid });
    if (res && res.metadata) {
      const entityWithChildren = await getEntityFieldsWithChildren(res.metadata);

      console.log('entityWithChildren: ', entityWithChildren);

      const conditions: ConditionField[] = [];
      const fieldIds: string[] = [];

      // 主表字段
      entityWithChildren.parentFields.sort(sortEntityFields).forEach((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        conditions.push({
          label: item.displayName,
          value: `${entityWithChildren.tableName}.${item.fieldName}`,
          fieldType: item.fieldType
        });
      });

      //   子表字段
      if (entityWithChildren.childEntities?.length > 0) {
        entityWithChildren.childEntities.forEach((item: ChildEntity) => {
          const childConditions: ConditionField[] = [];

          item.childFields.sort(sortEntityFields).forEach((field: AppEntityField) => {
            fieldIds.push(field.fieldId);
            childConditions.push({
              label: field.displayName,
              value: `${entityWithChildren.tableName}.${item.childTableName}.${field.fieldName}`,
              fieldType: field.fieldType
            });
          });
          conditions.push({
            label: item.childEntityName,
            value: item.childTableName,
            fieldType: '',
            children: childConditions
          });
        });
      }

      if (fieldIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
        newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
          const fieldName =
            [...entityWithChildren.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)
              ?.fieldName || '';
          item.fieldKey = `${entityWithChildren.tableName}.${fieldName}`;

          if (!fieldName) {
            for (const subEntity of entityWithChildren.childEntities) {
              const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
              if (foundField) {
                item.fieldKey = `${entityWithChildren.tableName}.${subEntity.childTableName}.${foundField.fieldName}`;
              }
            }
          }
        });

        setValidationTypes(newValidationTypes);
      }

      const newConditionFields: TreeSelectDataType[] = [
        {
          key: entityWithChildren.tableName,
          title: entityWithChildren.entityName,
          children: conditions.map((item) => ({
            key: item.value,
            title: item.label,
            fieldType: item.fieldType,
            children: item.children?.map((child: ConditionField) => ({
              key: child.value,
              title: child.label,
              fieldType: child.fieldType
            }))
          }))
        }
      ];

      setConditionFields(newConditionFields);

      updateStartFormOutputs(node.id, conditions);
    }
  };

  const handleGetComponentList = async (pageUuid: string) => {
    const res = await getComponentListByPageId({ pageUuid: pageUuid });
    if (res && res.list) {
      const newComponentList: any[] = [];
      res.list.forEach((item: ComponentConfig) => {
        const cpConfig = JSON.parse(item.config);
        if (cpConfig.dataField && cpConfig.dataField.length > 1) {
          newComponentList.push({
            label: cpConfig.label.text ? cpConfig.label.text : cpConfig.label,
            value: item.componentCode,
            fieldType: item.componentType
          });
        }
      });
      setComponentList(newComponentList);
    }
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
          >
            <Grid.Row>
              <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="触发范围" field="triggerRange" rules={[{ required: true, message: '请选择触发范围' }]}>
                <RadioGroup onChange={handleTriggerRangeChange}>
                  <Radio value={TriggerRange.Record}>整表</Radio>
                  <Radio value={TriggerRange.Field}>特定字段</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            <Grid.Row gutter={8} align="start">
              {triggerRange === TriggerRange.Record && (
                <>
                  <Grid.Col span={1}>
                    <div style={{ textAlign: 'center', lineHeight: '32px' }}>在</div>
                  </Grid.Col>
                  <Grid.Col span={21}>
                    <Form.Item field="pageUuid" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                      <Select disabled style={{ width: '100%' }}>
                        {pageList?.map((item) => (
                          <Option key={item.pageUuid} value={item.pageUuid}>
                            {item.pageName}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Grid.Col>
                  <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                    触发
                  </Grid.Col>
                </>
              )}
              {triggerRange === TriggerRange.Field && (
                <>
                  <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                    在
                  </Grid.Col>
                  <Grid.Col span={10}>
                    <Form.Item field="pageUuid" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                      <Select disabled style={{ width: '100%' }}>
                        {pageList?.map((item) => (
                          <Option key={item.pageUuid} value={item.pageUuid}>
                            {item.pageName}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Grid.Col>
                  <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                    的
                  </Grid.Col>
                  <Grid.Col span={10}>
                    <Form.Item field="fieldId" rules={[{ required: true, message: '请选择字段' }]} layout="vertical">
                      <Select style={{ width: '100%' }}>
                        {componentList?.map((item) => (
                          <Option key={item.value} value={item.value}>
                            {item.label}
                          </Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Grid.Col>
                  <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                    触发
                  </Grid.Col>
                </>
              )}
            </Grid.Row>

            <Grid.Row>
              {triggerRange === TriggerRange.Record && (
                <Form.Item
                  label="触发事件"
                  field="recordTriggerEvents"
                  layout="vertical"
                  rules={[{ required: true, message: '请选择触发事件' }]}
                >
                  <CheckboxGroup
                    direction="horizontal"
                    options={[
                      { label: '记录创建', value: TRIGGER_EVENTS.CREATE },
                      { label: '记录修改', value: TRIGGER_EVENTS.UPDATE },
                      { label: '记录删除', value: TRIGGER_EVENTS.DELETE }
                    ]}
                  />
                </Form.Item>
              )}

              {triggerRange === TriggerRange.Field && (
                <Form.Item
                  label="触发事件"
                  field="fieldTriggerEvents"
                  layout="vertical"
                  rules={[{ required: true, message: '请选择触发事件' }]}
                >
                  <RadioGroup>
                    <Radio value="valueChange">值改变</Radio>
                    <Radio value="focus">焦点失去</Radio>
                  </RadioGroup>
                </Form.Item>
              )}
            </Grid.Row>

            <Grid.Row>
              {validationTypes && (
                <ConditionEditor
                  nodeId={node.id}
                  label="过滤条件"
                  required
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                  form={payloadForm}
                />
              )}
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

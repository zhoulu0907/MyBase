import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import {
  getComponentListByPageId,
  getEntityFieldsWithChildren,
  getFieldCheckTypeApi,
  getPageListByAppId,
  getPageMetadata,
  type AppEntityField,
  type ComponentConfig,
  type ConditionField,
  type EntityFieldValidationTypes
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

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [pageList, setPageList] = useState<any[]>([]);

  const [componentList, setComponentList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<TreeSelectDataType[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const [payloadForm] = Form.useForm();

  const pageId = Form.useWatch('pageId', payloadForm);

  const triggerRange = Form.useWatch('triggerRange', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetPageList(appId);
    }
  }, []);

  useEffect(() => {
    if (pageId) {
      handleGetComponentList(pageId);
      handleGetFieldList(pageId);
    }
  }, [pageId]);

  useEffect(() => {
    if (triggerRange === TriggerRange.Record || triggerRange === TriggerRange.Field) {
      payloadForm.setFieldsValue({
        pageId: triggerEditorSignal.nodeData.value[node.id].pageId
      });
    }
  }, [triggerRange]);

  const handleGetPageList = async (appId: string) => {
    const res = await getPageListByAppId({ appId });
    setPageList(res.pages);
  };

  const handleTriggerRangeChange = (value: string) => {
    payloadForm.clearFields(['fieldId', 'recordtTriggerEvents', 'fieldTriggerEvents']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      recordtTriggerEvents: undefined,
      fieldTriggerEvents: undefined,
      filterCondition: []
    });
  };

  const handleGetFieldList = async (id: string) => {
    const res = await getPageMetadata({ pageId: id });
    if (res && res.metadata) {
      console.log(res);

      const entityWithChildren = await getEntityFieldsWithChildren(res.metadata);
      console.log(entityWithChildren);

      const conditions: ConditionField[] = [];
      const fieldIds: string[] = [];
      const fieldList: any[] = [];

      entityWithChildren.parentFields.forEach((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        fieldList.push({
          label: item.displayName,
          value: item.fieldId
        });

        conditions.push({
          label: item.displayName,
          value: item.fieldId,
          fieldType: item.fieldType
        });
      });

      if (fieldIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
        setValidationTypes(newValidationTypes);
      }

      setConditionFields([
        {
          key: entityWithChildren.entityId,
          title: entityWithChildren.entityName,
          children: conditions.map((item) => {
            return {
              key: item.value,
              title: item.label,
              fieldType: item.fieldType
            };
          })
        }
      ]);

      // 更新节点输出配置
      updateStartFormOutputs(node.id, conditions);
    }
  };

  const handleGetComponentList = async (id: string) => {
    const res = await getComponentListByPageId({ pageId: id });
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

      console.log(newComponentList);
      setComponentList(newComponentList);
    }
  };

  const onValuesChange = (changeValue: any, values: any) => {
    // handlePropsOnChange(values);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{
              ...triggerEditorSignal.nodeData.value[node.id]
            }}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
            onValuesChange={onValuesChange}
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
                    <Form.Item field="pageId" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                      <Select disabled style={{ width: '100%' }}>
                        {pageList?.map((item) => (
                          <Option key={item.id} value={item.id}>
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
                    <Form.Item field="pageId" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                      <Select disabled style={{ width: '100%' }}>
                        {pageList?.map((item) => (
                          <Option key={item.id} value={item.id}>
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
                  field="recordtTriggerEvents"
                  layout="vertical"
                  rules={[{ required: true, message: '请选择触发事件' }]}
                >
                  <CheckboxGroup
                    direction="horizontal"
                    options={[
                      {
                        label: '记录创建',
                        value: 'create'
                      },
                      {
                        label: '记录修改',
                        value: 'update'
                      },
                      {
                        label: '记录删除',
                        value: 'delete'
                      }
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
  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};

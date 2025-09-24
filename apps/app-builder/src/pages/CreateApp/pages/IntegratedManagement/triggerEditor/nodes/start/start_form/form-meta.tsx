import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { ComponentConfig } from '@onebase/app';
import {
  getComponentListByPageId,
  getFieldCheckTypeApi,
  getPageListByAppId,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { TriggerRange } from '../../../components/const';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

const CheckboxGroup = Checkbox.Group;
const Option = Select.Option;
const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [pageList, setPageList] = useState<any[]>();

  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
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
    console.log('res: ', res);
    setPageList(res.pages);
  };

  const handleTriggerRangeChange = (value: string) => {
    payloadForm.clearFields(['fieldId', 'triggerEvents']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      triggerEvents: undefined,
      filterCondition: []
    });
  };

  const handleGetComponentList = async (id: string) => {
    const res = await getComponentListByPageId({ pageId: id });
    if (res && res.list) {
      const newConditionFields: ConfitionField[] = [];
      const filedIds: string[] = [];
      res.list.forEach((item: ComponentConfig) => {
        const cpConfig = JSON.parse(item.config);
        if (cpConfig.dataField && cpConfig.dataField.length > 1) {
          filedIds.push(cpConfig.dataField[1]);

          newConditionFields.push({
            label: cpConfig.label.text ? cpConfig.label.text : cpConfig.label,
            value: cpConfig.dataField[1],
            fieldType: item.componentType
          });
        }
      });

      if (filedIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(filedIds);
        setValidationTypes(newValidationTypes);
      }

      setConditionFields(newConditionFields);
    }
  };

  const onValuesChange = (changeValue: any, values: any) => {
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
            layout="vertical"
            onValuesChange={onValuesChange}
          >
            <Grid.Row>
              <Form.Item
                label="节点ID"
                field="id"
                initialValue={node.id}
                rules={[{ required: true, message: '请选择' }]}
              >
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
                        {conditionFields?.map((item) => (
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
                  field="triggerEvents"
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
                  field="triggerEvents"
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

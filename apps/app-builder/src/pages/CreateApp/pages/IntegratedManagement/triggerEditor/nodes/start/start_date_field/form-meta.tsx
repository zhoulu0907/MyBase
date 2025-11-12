import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, InputNumber, Select, TimePicker } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import {
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type AppEntityField,
  type ConditionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useEffect, useMemo, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { updateStartDateFieldOutputs } from './output';

const Option = Select.Option;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [entityList, setEntityList] = useState<any[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [conditionFieldsData, setConditionFieldsData] = useState<TreeSelectDataType>([]);

  const [payloadForm] = Form.useForm();

  const entityId = Form.useWatch('entityId', payloadForm);
  const batchMode = Form.useWatch('batchMode', payloadForm);

  useEffect(() => {
    init();
  }, []);

  useEffect(() => {
    if (entityId) {
      handleGetEntityFieldsById(entityId);
    }
  }, [entityId]);

  const init = async () => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      const res = await getEntityListByApp(appId);
      setEntityList(res);
    }
  };

  const handleGetEntityFieldsById = async (entityId: string) => {
    const res = await getEntityFieldsWithChildren(entityId);

    const fieldIds: string[] = [];

    const fields = res.parentFields.map((item: AppEntityField) => {
      fieldIds.push(item.fieldId);
      return {
        key: item.fieldId,
        title: item.displayName,
        fieldType: item.fieldType
      };
    });

    setConditionFieldsData({
      key: res.entityId,
      title: res.entityName,
      children: fields
    });

    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    setValidationTypes(newValidationTypes);
  };

  const conditionFieldsForEditor = useMemo((): ConditionField[] => {
    return (
      (conditionFieldsData.children || [])?.map((item) => ({
        label: item.title as string,
        value: item.key as string,
        fieldType: item.fieldType
      })) || []
    );
  }, [conditionFieldsData]);

  // 使用 useEffect 更新条件字段状态和输出，避免在渲染过程中直接更新状态
  useEffect(() => {
    // 只在有实际数据时才更新 triggerNodeOutputSignal，避免初始化时载入空数据
    if (conditionFieldsForEditor.length > 0) {
      updateStartDateFieldOutputs(node.id, conditionFieldsForEditor);
    }
  }, [conditionFieldsForEditor, node.id]);

  const offsetFiledId = Form.useWatch('offsetFiledId', payloadForm);

  const offsetFiledIdChange = () => {
    payloadForm.clearFields(['offsetUnit']);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            layout="vertical"
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            requiredSymbol={{ position: 'end' }}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>

            <Grid.Row gutter={8}>
              <Grid.Col span={12}>
                <Form.Item label="实体" field="entityId" rules={[{ required: true, message: '请选择实体' }]}>
                  <Select disabled={true}>
                    {entityList?.map((item) => (
                      <Option key={item.entityId} value={item.entityId}>
                        {item.entityName}
                      </Option>
                    ))}
                  </Select>
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={12}>
                <Form.Item
                  label="基准日期字段"
                  field="offsetFiledId"
                  rules={[{ required: true, message: '请选择基准日期字段' }]}
                >
                  <Select
                    onChange={offsetFiledIdChange}
                    options={conditionFieldsData.children
                      ?.filter(
                        (item) =>
                          item.fieldType == ENTITY_FIELD_TYPE.DATETIME.VALUE ||
                          item.fieldType == ENTITY_FIELD_TYPE.DATE.VALUE
                      )
                      .map((item) => ({
                        label: item.title as string,
                        value: item.key as string
                      }))}
                  />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>
            <Grid.Row gutter={8} align="end">
              <Grid.Col span={4}>
                <Form.Item label="偏移模式" field="offsetMode" rules={[{ required: true, message: '请选择偏移模式' }]}>
                  <Select
                    options={[
                      { label: '无', value: 'none' },
                      { label: '提前', value: 'before' },
                      { label: '延后', value: 'after' }
                    ]}
                  />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={16}>
                <Form.Item field="offsetValue">
                  <InputNumber mode="button" />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={4}>
                <Form.Item field="offsetUnit">
                  {/* 根据基准日期类型 选择不同的下拉选项 */}
                  <Select
                    options={
                      conditionFieldsData.children?.find((item) => item.key === offsetFiledId)?.fieldType ==
                      ENTITY_FIELD_TYPE.DATETIME.VALUE
                        ? [
                            { label: '小时', value: 'hour' },
                            { label: '分钟', value: 'minute' }
                          ]
                        : [{ label: '天', value: 'day' }]
                    }
                  />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>

            <Grid.Row gutter={8} align="end">
              <Grid.Col span={4}>
                <Form.Item label="批处理" field="batchMode" triggerPropName="checked" layout="vertical" hidden={true}>
                  <Checkbox>开启</Checkbox>
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={8}>
                <Form.Item field="batchSize" layout="horizontal" hidden={batchMode == false}>
                  <InputNumber mode="button" />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>
            <Grid.Row>
              <Grid.Col span={8}>
                <Form.Item
                  label="每日触发时间"
                  layout="vertical"
                  field="dailyExecTime"
                  rules={[{ required: true, message: '请选择每日触发时间' }]}
                >
                  <TimePicker format="HH:mm" />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>
            <Grid.Row>
              <ConditionEditor
                nodeId={node.id}
                label="匹配规则"
                required
                fields={[conditionFieldsData]}
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
  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};

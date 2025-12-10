import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, InputNumber, Select, TimePicker } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import { useEffect } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext, useStartEntityFields } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { updateStartDateFieldOutputs } from './output';

const Option = Select.Option;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { entityList, conditionFields, validationTypes, loadEntityFields } = useStartEntityFields();

  const [payloadForm] = Form.useForm();
  const tableName = Form.useWatch('tableName', payloadForm);
  const batchMode = Form.useWatch('batchMode', payloadForm);
  const offsetFiledId = Form.useWatch('offsetFiledId', payloadForm);

  useEffect(() => {
    if (tableName && entityList.length > 0) {
      const entityId = entityList.find((item) => item.tableName === tableName)?.entityId;
      if (entityId) {
        loadEntityFields(entityId, tableName).then(({ conditions }) => {
          if (conditions.length > 0) {
            updateStartDateFieldOutputs(node.id, conditions);
          }
        });
      }
    }
  }, [tableName, entityList]);

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
                <Form.Item label="实体" field="tableName" rules={[{ required: true, message: '请选择实体' }]}>
                  <Select disabled={true}>
                    {entityList?.map((item) => (
                      <Option key={item.tableName} value={item.tableName}>
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
                    options={conditionFields[0]?.children
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
                  <Select
                    options={
                      conditionFields[0]?.children?.find((item) => item.key === offsetFiledId)?.fieldType ==
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

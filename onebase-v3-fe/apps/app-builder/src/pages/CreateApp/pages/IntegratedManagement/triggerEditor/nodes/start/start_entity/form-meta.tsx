import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { useEffect } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext, useStartEntityFields } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { updateStartEntityOutputs } from './output';

const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

const beforeTriggerEvents = [
  { label: '创建前', value: 'beforeCreate' },
  { label: '修改前', value: 'beforeUpdate' },
  { label: '删除前', value: 'beforeDelete' }
];

const afterTriggerEvents = [
  { label: '创建后', value: 'afterCreate' },
  { label: '修改后', value: 'afterUpdate' },
  { label: '删除后', value: 'afterDelete' }
];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { entityList, conditionFields, validationTypes, loadEntityFields } = useStartEntityFields();

  const [payloadForm] = Form.useForm();
  const tableName = Form.useWatch('tableName', payloadForm);
  const triggerType = Form.useWatch('triggerType', payloadForm);

  useEffect(() => {
    if (entityList.length > 0 && tableName) {
      const entityId = entityList.find((item) => item.tableName === tableName)?.entityId;
      if (entityId) {
        loadEntityFields(entityId, tableName).then(({ conditions }) => {
          if (conditions.length > 0) {
            updateStartEntityOutputs(node.id, conditions);
          }
        });
      }
    }
  }, [entityList, tableName]);

  const handleTriggerTypeChange = () => {
    payloadForm.clearFields('triggerEvents');
    triggerEditorSignal.setNodeData(node.id, {
      ...triggerEditorSignal.nodeData.value[node.id],
      triggerEvents: []
    });
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
              <Form.Item label="实体" field="tableName">
                <Select disabled={true}>
                  {entityList?.map((item) => (
                    <Option key={item.entityUuid} value={item.tableName}>
                      {item.entityName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item
                label="触发类型"
                field="triggerType"
                layout="vertical"
                rules={[{ required: true, message: '请选择触发类型' }]}
              >
                <RadioGroup
                  onChange={handleTriggerTypeChange}
                  direction="horizontal"
                  options={[
                    { label: '前置', value: 'before' },
                    { label: '后置', value: 'after' }
                  ]}
                />
              </Form.Item>
            </Grid.Row>

            {triggerType && (
              <Grid.Row align="end">
                <Form.Item
                  label="事件类型"
                  field="triggerEvents"
                  layout="vertical"
                  rules={[{ required: true, message: '请选择触发类型' }]}
                >
                  <CheckboxGroup
                    direction="horizontal"
                    options={triggerType === 'before' ? beforeTriggerEvents : afterTriggerEvents}
                  />
                </Form.Item>
              </Grid.Row>
            )}

            <Grid.Row>
              <ConditionEditor
                nodeId={node.id}
                label="过滤条件"
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

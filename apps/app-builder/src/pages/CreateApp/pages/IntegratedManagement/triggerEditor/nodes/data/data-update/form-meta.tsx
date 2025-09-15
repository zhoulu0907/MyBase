import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { AppEntityField, ConfitionField, EntityFieldValidationTypes } from '@onebase/app';
import { useEffect, useState } from 'react';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import ConditionEditor from '../../../components/condition-editor';

const RadioGroup = Radio.Group;
const Option = Select.Option;

const UPDATE_TYPE = {
  MAIN_ENTITY: 'mainEntity',
  SUB_ENTITY: 'subEntity'
};

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntities, subEntities } = triggerEditorSignal;

  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);
  const [entityList, setEntityList] = useState<any[]>();

  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  const [payloadForm] = Form.useForm();

  const updateType = Form.useWatch('updateType', payloadForm);

  useEffect(() => {
    if (updateType) {
      console.log('updateType: ', updateType);
      if (updateType == UPDATE_TYPE.MAIN_ENTITY) {
        console.log('mainEntities.value: ', mainEntities.value);
        setEntityList(mainEntities.value);
        payloadForm.clearFields('entityId');
      } else {
        console.log('subEntities.value: ', subEntities.value);
        setEntityList(subEntities.value);
        payloadForm.clearFields('entityId');
      }
    }
  }, [updateType]);

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
              <Form.Item label="节点ID" field="id" initialValue={node.id}>
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="更新方式" field="updateType" rules={[{ required: true, message: '请选择更新方式' }]}>
                <RadioGroup>
                  <Radio value={UPDATE_TYPE.MAIN_ENTITY}>更新主表数据</Radio>
                  <Radio value={UPDATE_TYPE.SUB_ENTITY}>更新子表数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item field="entityId" rules={[{ required: true, message: '请选择表单' }]} layout="vertical">
                <Select
                  style={{ width: '100%' }}
                  onChange={(value) => {
                    console.log('value: ', value);
                    [...mainEntities.value, ...subEntities.value].forEach((item) => {
                      if (item.entityId === value) {
                        setFieldDataList(item.fields);
                      }
                    });
                  }}
                >
                  {entityList?.map((item) => (
                    <Option key={item.entityId} value={item.entityId}>
                      {item.entityName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Grid.Row>
            <Grid.Row>
              <Form.Item field="filterCondition" label='匹配规则' required>
                <ConditionEditor
                  data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="更新规则" field="fields">
                <FieldEditor fieldList={fieldDataList} />
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

import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Grid, Input, Select } from '@arco-design/web-react';
import {
  getEntityFields,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type Condition,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type MetadataEntityField
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

const Option = Select.Option;
const CheckboxGroup = Checkbox.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const [payloadForm] = Form.useForm();

  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [entityList, setEntityList] = useState<any[]>();
  const [triggerFieldList, setTriggerFieldList] = useState<any[]>();

  const entityId = Form.useWatch('entityId', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetEntityListByApp(appId);
    }
  }, []);

  useEffect(() => {
    if (entityId) {
      handleGetEntityFieldList(entityId);
    }
  }, [entityId]);

  const handleGetEntityListByApp = async (appId: string) => {
    const res = await getEntityListByApp(appId);
    setEntityList(res);
  };

  const handleGetEntityFieldList = async (eId: string) => {
    const res = await getEntityFields({ entityId: eId });
    if (res) {
      console.log(res);
      const newConditionFields: ConfitionField[] = [];
      const filedIds: string[] = [];
      const fieldList: any[] = [];
      res.forEach((item: MetadataEntityField) => {
        filedIds.push(item.id);
        fieldList.push({
          label: item.displayName,
          value: item.id
        });

        newConditionFields.push({
          label: item.fieldName,
          value: item.id,
          fieldType: item.fieldType
        });
      });

      if (filedIds?.length) {
        const newValidationTypes = await getFieldCheckTypeApi(filedIds);
        console.log('validationTypes: ', newValidationTypes);
        setValidationTypes(newValidationTypes);
      }

      //   console.log('newConditionFields: ', newConditionFields);

      setConditionFields(newConditionFields);
      setTriggerFieldList(fieldList);
    }
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  const onConditionChange = (conditions: Condition[]) => {
    console.log(conditions);
    handlePropsOnChange({
      ...triggerEditorSignal.nodeData.value[node.id],
      filterConditions: conditions
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
              <Form.Item label="实体" field="entityId">
                <Select disabled={true}>
                  {entityList?.map((item) => (
                    <Option key={item.entityId} value={item.entityId}>
                      {item.entityName}
                    </Option>
                  ))}
                </Select>
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item
                label="事件类型"
                field="triggerEvents"
                layout="vertical"
                rules={[{ required: true, message: '请选择触发类型' }]}
              >
                <CheckboxGroup
                  direction="horizontal"
                  options={[
                    {
                      label: '创建前',
                      value: 'beforeCreate'
                    },
                    {
                      label: '创建后',
                      value: 'afterCreate'
                    },
                    {
                      label: '修改前',
                      value: 'beforeUpdate'
                    },
                    {
                      label: '修改后',
                      value: 'afterUpdate'
                    },
                    {
                      label: '删除前',
                      value: 'beforeDelete'
                    },
                    {
                      label: '删除后',
                      value: 'afterDelete'
                    }
                  ]}
                />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="触发字段" field="triggerFieldIds" layout="vertical">
                <Select options={triggerFieldList} />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="过滤条件" field="filterCondition" layout="vertical">
                {validationTypes && (
                  <ConditionEditor
                    onChange={onConditionChange}
                    data={triggerEditorSignal.nodeData.value[node.id].filterCondition}
                    fields={conditionFields}
                    entityFieldValidationTypes={validationTypes}
                  />
                )}
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
  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};

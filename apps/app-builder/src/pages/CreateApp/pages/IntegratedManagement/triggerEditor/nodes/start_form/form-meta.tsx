import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Switch } from '@arco-design/web-react';
import {
  getComponentListByPageId,
  getFieldCheckTypeApi,
  type ComponentConfig,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const { pageId } = triggerEditorSignal;
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const [payloadForm] = Form.useForm();

  useEffect(() => {
    if (pageId.value) {
      handleGetComponentList(pageId.value);
    }
  }, [pageId]);

  const handleGetComponentList = async (id: string) => {
    const res = await getComponentListByPageId({ pageId: id });
    console.log('res: ', res);
    if (res && res.list) {
      const newConditionFields: ConfitionField[] = [];
      const filedIds: string[] = [];
      res.list.forEach((item: ComponentConfig) => {
        const cpConfig = JSON.parse(item.config);
        if (cpConfig.dataField && cpConfig.dataField.length > 1) {
          filedIds.push(cpConfig.dataField[1]);

          newConditionFields.push({
            label: cpConfig.label.text,
            value: cpConfig.dataField[1],
            fieldType: item.componentType
          });
        }
      });

      const newValidationTypes = await getFieldCheckTypeApi(filedIds);
      console.log('validationTypes: ', newValidationTypes);
      setValidationTypes(newValidationTypes);

      console.log('newConditionFields: ', newConditionFields);

      setConditionFields(newConditionFields);
    }
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

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
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="过滤条件" field="filterConditions" layout="vertical">
              {validationTypes && (
                <ConditionEditor
                  // TODO(mickey): 补充onChange
                  onChange={() => {}}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                />
              )}
            </Form.Item>

            <Form.Item label="忽略空值变更" field="ignoreEmptyChange" layout="vertical" triggerPropName="checked">
              <Switch />
            </Form.Item>
            <Form.Item label="关联子表触发" field="relatedSubtableTrigger" layout="vertical" triggerPropName="checked">
              <Switch />
            </Form.Item>
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

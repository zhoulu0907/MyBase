import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, InputNumber, Switch } from '@arco-design/web-react';
import { getComponentListByPageId, type ComponentConfig, type ConfitionField } from '@onebase/app';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const { pageId } = triggerEditorSignal;
  const [conditionField, setConditionField] = useState<ConfitionField[]>([]);

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
      const newConditionField: ConfitionField[] = [];
      const filedIds: string[] = [];
      res.list.forEach((item: ComponentConfig) => {
        const cpConfig = JSON.parse(item.config);
        if (cpConfig.dataField && cpConfig.dataField.length > 1) {
          filedIds.push(cpConfig.dataField[1]);
        }
      });

      // TODO(mickey): 等天宇提供接口后
      //   res.list.forEach((item: ComponentConfig) => {

      //     const cpConfig = JSON.parse(item.config);
      //     if (cpConfig.dataField && cpConfig.dataField.length > 1) {
      //       newConditionField.push({ label: cpConfig.label, value: cpConfig.dataField[1], fieldType: cpConfig.dataField[0] });
      //     }
      //   });

      //   setConditionField(newConditionField);
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
              <ConditionEditor onChange={() => {}} fields={[]} fieldOperatorMapping={{}} />
            </Form.Item>

            <Form.Item label="忽略空值变更" field="ignoreEmptyChange" layout="vertical" triggerPropName="checked">
              <Switch />
            </Form.Item>
            <Form.Item label="关联子表触发" field="relatedSubtableTrigger" layout="vertical" triggerPropName="checked">
              <Switch />
            </Form.Item>

            <Form.Item label="防抖时间" field="debounceTime" layout="vertical">
              <InputNumber min={100} max={1000} />
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

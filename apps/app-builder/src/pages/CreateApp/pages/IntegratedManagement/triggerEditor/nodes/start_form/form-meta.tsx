import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Checkbox, Form, Input, Select, Switch } from '@arco-design/web-react';
import type { ComponentConfig, Condition } from '@onebase/app';
import {
  getComponentListByPageId,
  getFieldCheckTypeApi,
  getFlowMgmt,
  getPageListByAppId,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

const CheckboxGroup = Checkbox.Group;
const Option = Select.Option;

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

  const triggerUserType = Form.useWatch('triggerUserType', payloadForm);

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetPageList(appId);
    }

    const flowId = getHashQueryParam('flowId');
    if (flowId) {
      handleGetFlowInfo(flowId);
    }
  }, []);

  // 根据流程id获取流程详细信息
  const handleGetFlowInfo = async (flowId: string) => {
    const res = await getFlowMgmt(flowId);
    console.log('res: ', res);
    if (res && res.triggerConfig && res.triggerConfig.pageId) {
      payloadForm.setFieldValue('pageId', res.triggerConfig.pageId);
      handleGetComponentList(res.triggerConfig.pageId);
    }
  };

  const handleGetPageList = async (appId: string) => {
    const res = await getPageListByAppId({ appId });
    console.log('res: ', res);
    setPageList(res.pages);
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
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="表单" field="pageId">
              <Select disabled>
                {pageList?.map((item) => (
                  <Option key={item.id} value={item.id}>
                    {item.pageName}
                  </Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item label="过滤条件" field="filterCondition" layout="vertical">
              {validationTypes && (
                <ConditionEditor
                  onChange={onConditionChange}
                  data={triggerEditorSignal.nodeData.value[node.id].filterConditions}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                />
              )}
            </Form.Item>
            <Form.Item label="关联子表触发" field="isChildTriggerAllowed" layout="vertical" triggerPropName="checked">
              <Switch />
            </Form.Item>
            <Form.Item label="触发人" field="triggerUserType" layout="vertical">
              <Select
                options={[
                  { label: '创建人', value: 'creator' },
                  { label: '修改人', value: 'modifier' },
                  { label: '具体用户', value: 'specific' }
                ]}
              />
            </Form.Item>
            {triggerUserType === 'specific' && (
              <Form.Item label="指定触发人" field="triggerUserValue" layout="vertical">
                <Select />
              </Form.Item>
            )}
            <Form.Item label="触发事件" field="triggerEvents" layout="vertical">
              <CheckboxGroup direction="vertical" options={['记录创建', '记录修改', '记录删除']} />
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

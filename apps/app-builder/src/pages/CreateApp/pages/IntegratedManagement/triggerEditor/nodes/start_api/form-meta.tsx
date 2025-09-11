import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
  };

  // 请求方法  GET/POST/PUT/DELETE
  const httpMethodOptions = [
    { label: "GET", value: "GET" },
    { label: "POST", value: "POST" },
    { label: "PUT", value: "PUT" },
    { label: "DELETE", value: "DELETE" },
  ];

  // 认证方式  匿名/API Key/Token/OAuth2
  const authTypeOptions = [
    { label: "匿名", value: "1" },
    { label: "API Key", value: "2" },
    { label: "Token", value: "3" },
    { label: "OAuth2", value: "4" },
  ]


  const [payloadForm] = Form.useForm();
  const triggerType = Form.useWatch('triggerType', payloadForm);

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout='vertical' initialValues={{ ...triggerEditorSignal.nodeData.value[node.id], data: {} }}>
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="nodeName">
              <Input placeholder='请输入节点名称' onChange={(e) => handlePropsOnChange('nodeName', e)} />
            </Form.Item>
            <Form.Item label="API路径" field="data.urlPath">
              <Input placeholder='请输入API路径' onChange={(e) => handlePropsOnChange('data.urlPath', e)} />
            </Form.Item>
            <Form.Item label="请求方法" field="data.httpMethod">
              <Select
                placeholder='请选择请求方法'
                options={httpMethodOptions}
                allowClear
                onChange={(e) => handlePropsOnChange("data.httpMethod", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="认证方式" field="data.authType">
              <Select
                placeholder='请选择认证方式'
                options={authTypeOptions}
                allowClear
                onChange={(e) => handlePropsOnChange("data.authType", e)}
              ></Select>
            </Form.Item>
            <Form.Item label="认证Key" field="data.authKey">
              <Input placeholder='请输入认证Key' onChange={(e) => handlePropsOnChange('data.authKey', e)} />
            </Form.Item>
            <Form.Item label="请求参数" field="data.reuqestParamsSchema">
              <Input.TextArea placeholder='请输入请求参数' onChange={(e) => handlePropsOnChange('data.reuqestParamsSchema', e)} />
            </Form.Item>
            <Form.Item label="成功响应格式" field="data.successResponseSchema">
              <Input.TextArea placeholder='请输入成功响应格式' onChange={(e) => handlePropsOnChange('data.successResponseSchema', e)} />
            </Form.Item>
            <Form.Item label="失败响应格式" field="data.failResponseSchema">
              <Input.TextArea placeholder='请输入失败响应格式' onChange={(e) => handlePropsOnChange('data.failResponseSchema', e)} />
            </Form.Item>
            <Form.Item label="数据响应格式" field="data.responseMapping">
              <Input.TextArea placeholder='请输入数据响应格式' onChange={(e) => handlePropsOnChange('data.responseMapping', e)} />
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

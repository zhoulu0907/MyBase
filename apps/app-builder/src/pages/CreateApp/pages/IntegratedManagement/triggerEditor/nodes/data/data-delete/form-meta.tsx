import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select, Switch, InputNumber } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import {
  DeleteDataType,
  getEntityFields,
  getPageListByAppId,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useAppStore } from '@/store/store_app';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const [pageList, setPageList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const { curAppId } = useAppStore();

  useEffect(() => {
    if (curAppId) {
      handleGetPageList(curAppId);
    }
  }, []);

  // 根据流程id获取流程详细信息
  const handleGetPageList = async (appId: string) => {
    const res = await getPageListByAppId({ appId });
    console.log('res: ', res);
    setPageList(res.pages);
  };

  // 表单项内容变更
  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
    if (key === 'pageId') {
      pageChange(value);
    }
  };

  // 目标表单变更  更新筛选条件列表，清除已填筛选条件
  const pageChange = (pageId: string) => {
    payloadForm.clearFields(['filter_condition']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      filter_condition: [] // null 和 '' 在 Select 中都被认为是值
    });
    // 重新获取字段列表
    if (pageId) {
      getFieldList(pageId)
    }
  };
  // 获取字段下拉列表
  const getFieldList = async (pageId: string,) => {
    // 数据库表 查询指定实体的字段列表
    const res = await getEntityFields({ entityId: pageId });
    const filedIds: string[] = [];
    const newConditionFields: ConfitionField[] = [];
    res.forEach((item: any) => {
      filedIds.push(item.id);
      newConditionFields.push({
        label: item.displayName,
        value: item.id,
        fieldType: item.fieldType
      });
    });
    setConditionFields(newConditionFields);
    if (filedIds?.length) {
      const newValidationTypes = await getFieldCheckTypeApi(filedIds);
      console.log('validationTypes: ', newValidationTypes);
      setValidationTypes(newValidationTypes);
    }
  }

  const deleteTypeOptions = [
    { label: '软删除', value: DeleteDataType.SOFT_DELETE },
    { label: '硬删除', value: DeleteDataType.PHYSICAL_DELETE }
  ];

  const [payloadForm] = Form.useForm();

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout="vertical" initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}>
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="nodeName">
              <Input placeholder="请输入节点名称" onChange={(e) => handlePropsOnChange('nodeName', e)} />
            </Form.Item>
            <Form.Item label="目标表单" field="pageId">
              <Select placeholder="请选择目标表单" allowClear onChange={(e) => handlePropsOnChange('pageId', e)}>
                {pageList?.map((item) => (
                  <Select.Option key={item.id} value={item.id}>
                    {item.pageName}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item label="筛选条件" field="filter_condition">
              <ConditionEditor
                onChange={(e) => handlePropsOnChange('filter_condition', e)}
                data={triggerEditorSignal.nodeData.value[node.id]?.filter_condition || []}
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
              />
            </Form.Item>
            <Form.Item label="删除方式" field="deleteType">
              <Select
                placeholder="请选择删除方式"
                options={deleteTypeOptions}
                allowClear
                onChange={(e) => handlePropsOnChange('deleteType', e)}
              ></Select>
            </Form.Item>
            <Form.Item label="是否批量删除" field="batchDeleteType">
              <Switch onChange={(e) => handlePropsOnChange('batchDeleteType', e)} />
            </Form.Item>
            <Form.Item label="批量删除限制" field="batchDeleteSize">
              <InputNumber precision={0} onChange={(e) => handlePropsOnChange('batchDeleteSize', e)} />
            </Form.Item>
            <Form.Item label="级联删除" field="cascadeDelete">
              <Switch onChange={(e) => handlePropsOnChange('cascadeDelete', e)} />
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
};

import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select, Switch, InputNumber, Radio, Grid } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import {
  DeleteDataType,
  DeleteMethod,
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

  const [deleteMethod, serDeleteType] = useState<DeleteMethod>(DeleteMethod.MAIN_DATA);

  useEffect(() => {
    if (curAppId) {
      handleGetPageList(curAppId);
    }
    const formData = payloadForm.getFieldsValue();
    if (formData.deleteMethod) {
      serDeleteType(formData.deleteMethod);
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
    } else if (key === 'deleteMethod') {
      serDeleteType(value);
    }
  };

  // 目标表单变更  更新筛选条件列表，清除已填筛选条件
  const pageChange = (pageId: string) => {
    payloadForm.clearFields(['filterCondition']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      filterCondition: [] // null 和 '' 在 Select 中都被认为是值
    });
    // 重新获取字段列表
    if (pageId) {
      getFieldList(pageId);
    }
  };
  // 获取字段下拉列表
  const getFieldList = async (pageId: string) => {
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
  };

  const deleteMethodOptions = [
    { label: '删除主表数据', value: DeleteMethod.MAIN_DATA },
    { label: '删除子表数据', value: DeleteMethod.SUB_DATA }
  ];

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
            <Form.Item label="节点名称" field="nodeName" required>
              <Input placeholder="请输入节点名称" onChange={(e) => handlePropsOnChange('nodeName', e)} />
            </Form.Item>
            <Form.Item label="删除方式" field="deleteMethod" required>
              <Radio.Group
                options={deleteMethodOptions}
                onChange={(e) => handlePropsOnChange('deleteMethod', e)}
              ></Radio.Group>
            </Form.Item>
            {deleteMethod === DeleteMethod.MAIN_DATA ? (
              <Form.Item field="pageId">
                <Grid.Row align="center">
                  <Grid.Col span={2} style={{ textAlign: 'center' }}>
                    删除
                  </Grid.Col>
                  <Grid.Col span={20}>
                    <Select placeholder="请选择目标表单" allowClear onChange={(e) => handlePropsOnChange('pageId', e)}>
                      {pageList?.map((item) => (
                        <Select.Option key={item.id} value={item.id}>
                          {item.pageName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Grid.Col>
                  <Grid.Col span={2} style={{ textAlign: 'center' }}>
                    的数据
                  </Grid.Col>
                </Grid.Row>
              </Form.Item>
            ) : (
              <Form.Item>
                <Grid.Row align="center">
                  <Grid.Col span={2} style={{ textAlign: 'center', marginBottom: '20px' }}>
                    删除
                  </Grid.Col>
                  <Grid.Col span={9}>
                    <Form.Item field="pageId">
                      <Select placeholder="请选择主表" allowClear onChange={(e) => handlePropsOnChange('pageId', e)}>
                        {pageList?.map((item) => (
                          <Select.Option key={item.id} value={item.id}>
                            {item.pageName}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Grid.Col>
                  <Grid.Col span={2} style={{ textAlign: 'center', marginBottom: '20px' }}>
                    中
                  </Grid.Col>
                  <Grid.Col span={9}>
                    <Form.Item field="subPageId">
                      <Select placeholder="请选择子表" allowClear onChange={(e) => handlePropsOnChange('subPageId', e)}>
                        {pageList?.map((item) => (
                          <Select.Option key={item.id} value={item.id}>
                            {item.pageName}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                  </Grid.Col>
                  <Grid.Col span={2} style={{ textAlign: 'center', marginBottom: '20px' }}>
                    的数据
                  </Grid.Col>
                </Grid.Row>
              </Form.Item>
            )}

            <Form.Item label="匹配规则" field="filterCondition" required>
              <ConditionEditor
                onChange={(e) => handlePropsOnChange('filterCondition', e)}
                data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                fields={conditionFields}
                entityFieldValidationTypes={validationTypes}
              />
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

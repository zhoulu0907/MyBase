import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select, Radio, Grid } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import {
  DeleteMethod,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const [entityList, setEntityList] = useState<any[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const { mainEntities, subEntities } = triggerEditorSignal;

  const [payloadForm] = Form.useForm();

  const pageId = Form.useWatch('pageId', payloadForm);
  const deleteMethod = Form.useWatch('deleteMethod', payloadForm);

  useEffect(() => {
    if (deleteMethod) {
      payloadForm.clearFields(['pageId']);
      if (deleteMethod == DeleteMethod.MAIN_DATA) {
        console.log('mainEntities.value: ', mainEntities.value);
        setEntityList(mainEntities.value);
        payloadForm.clearFields('entityId');
      } else {
        console.log('subEntities.value: ', subEntities.value);
        setEntityList(subEntities.value);
        payloadForm.clearFields('entityId');
      }
    }
  }, [deleteMethod]);
  useEffect(() => {
    if (pageId) {
      pageChange(pageId);
    }
  }, [pageId]);

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      ...values
    });
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

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            layout="vertical"
            onValuesChange={onValuesChange}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
          >
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="nodeName" required>
              <Input placeholder="请输入节点名称" />
            </Form.Item>
            <Form.Item label="删除方式" field="deleteMethod" required>
              <Radio.Group options={deleteMethodOptions}></Radio.Group>
            </Form.Item>
            <Grid.Row>
              <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                删除
              </Grid.Col>
              <Grid.Col span={20}>
                <Form.Item field="pageId">
                  <Select placeholder="请选择目标表单" allowClear>
                    {entityList?.map((item) => (
                      <Select.Option key={item.entityId} value={item.entityId}>
                        {item.entityName}
                      </Select.Option>
                    ))}
                  </Select>
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={2} style={{ textAlign: 'center', lineHeight: '32px' }}>
                的数据
              </Grid.Col>
            </Grid.Row>

            <Form.Item label="匹配规则" field="filterCondition" required>
              <ConditionEditor
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

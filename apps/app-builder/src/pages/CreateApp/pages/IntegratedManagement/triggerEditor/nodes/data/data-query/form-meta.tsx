import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { type FlowNodeJSON } from '../../../typings';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { Form, Input, Select, Radio, Grid } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import {
  getEntityListByApp,
  getEntityFields,
  getFieldCheckTypeApi,
  DATA_SOURCE_TYPE,
  FILTER_TYPE,
  getEntityFieldsWithChildren,
  type SelectOption,
  type MetadataEntityPair,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';
import { useAppStore } from '@/store/store_app';
import { getBeforeCurNodes } from '../../../components/utils';

export const renderForm = () => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();
  const dataType = Form.useWatch('dataType', payloadForm);
  const dataSource = Form.useWatch('dataSource', payloadForm);
  const filterType = Form.useWatch('filterType', payloadForm);
  const mainDataSource = Form.useWatch('mainDataSource', payloadForm);

  // 数据源选择
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    if (dataType) {
      dataTypeChange(dataType);
    }
  }, [dataType]);
  useEffect(() => {
    if (dataSource) {
      dataSourceChange(dataSource);
    }
  }, [dataSource]);
  useEffect(() => {
    if (mainDataSource) {
      mainDataSourceChange(mainDataSource);
    }
  }, [mainDataSource]);

  /**
   * 获取方式变更
   * 更新数据源下拉列表，清除已选择数据源
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const dataTypeChange = async (value: number) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: undefined, // null 和 '' 在 Select 中都被认为是值
      dataSourceOriginNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setMainEntityList([]);
    setConditionFields([]);
    setValidationTypes([]);
    if (value) {
      getEntityList();
    }
  };

  // 数据源变更  更新排序字段下拉列表，清除已选择排序字段 判断DATA_SOURCE_TYPE.DATA_NODE 绑定源节点nodeId
  const dataSourceChange = async (value: string) => {
    // 判断源节点
    const originNodeData = triggerEditorSignal.nodeData.value[value];
    const originNodeId = originNodeData?.dataSourceOriginNodeId || value
    payloadForm.clearFields(['sortBy']);
    setConditionFields([]);
    setValidationTypes([]);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSourceOriginNodeId: dataType === DATA_SOURCE_TYPE.DATA_NODE ? originNodeId : undefined,
      sortBy: [] // 清除已选择排序字段
    });
    // 根据数据源重新获取字段列表
    if (value) {
      getFieldList();
    }
  };
  const mainDataSourceChange = async (value: string) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: undefined, // null 和 '' 在 Select 中都被认为是值
      dataSourceOriginNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setConditionFields([]);
    setValidationTypes([]);
    const res = await getEntityFieldsWithChildren(value);
    const newEntityList = (res.childEntities || []).map((item: any) => {
      return {
        entityId: item.childEntityId,
        entityName: item.childEntityName
      };
    });
    setEntityList(newEntityList);
  };
  
  // 获取数据源列表
  const getEntityList = async () => {
    // todo  判断
    if (dataType === DATA_SOURCE_TYPE.FORM) {
      // 从表单中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setEntityList(res);
    } else if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE  dataSourceOriginNodeId
      const nodes = triggerEditorSignal.nodes.value;
      const newEntityList = getBeforeCurNodes(node.id, nodes);
      setEntityList(
        newEntityList.map((item) => {
          return { entityName: item.data?.title, entityId: item.id };
        })
      );
    } else if (dataType === DATA_SOURCE_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    } else if (dataType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
    }
  };
  // 获取排序字段下拉列表
  const getFieldList = async () => {
    // 根据数据源 查询指定实体的字段列表
    // todo 根据不同获取方式走不同接口
    if (dataType === DATA_SOURCE_TYPE.FORM) {
      // 从表单中查询  FORM
      const res = await getEntityFields({ entityId: dataSource });
      const filedIds: string[] = [];
      const newConditionFields: ConfitionField[] = [];
      const fieldOptions: SelectOption[] = [];
      res.forEach((item: any) => {
        fieldOptions.push({
          label: item.displayName,
          value: item.id
        });
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
    } else if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE
      const nodeData = triggerEditorSignal.nodeData.value[dataSource];
      if(!nodeData.dataSource){
        return;
      }
      const res = await getEntityFields({ entityId: nodeData.dataSource });
      const filedIds: string[] = [];
      const newConditionFields: ConfitionField[] = [];
      const fieldOptions: SelectOption[] = [];
      res.forEach((item: any) => {
        fieldOptions.push({
          label: item.displayName,
          value: item.id
        });
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
    } else if (dataType === DATA_SOURCE_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    } else if (dataType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };

  // 表单内容改变
  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);
    triggerEditorSignal.setNodeData(node.id, values);
  };

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
            <Form.Item label="节点ID" field="id " initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="查询方式" field="dataType" required>
              <Radio.Group direction="vertical">
                <Radio value={DATA_SOURCE_TYPE.FORM}>从表单中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从数据节点中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.ASSOCIA_FORM}>从关联表单中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUBFORM}>从子表中查询</Radio>
              </Radio.Group>
            </Form.Item>
            {dataType === DATA_SOURCE_TYPE.SUBFORM ? (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainDataSource">
                    <Select allowClear>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  的
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="dataSource">
                    <Select allowClear>
                      {entityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  <span>中查询数据</span>
                </Grid.Col>
              </Grid.Row>
            ) : (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="dataSource">
                    <Select allowClear>
                      {entityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
                          {item.entityName}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Col>
                <Grid.Col span={4} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  <span>中查询数据</span>
                </Grid.Col>
              </Grid.Row>
            )}
            <Form.Item label="查询规则" field="filterType" required>
              <Radio.Group>
                <Radio value={FILTER_TYPE.ALL}>全部数据</Radio>
                <Radio value={FILTER_TYPE.CONDITION}>按条件过滤</Radio>
              </Radio.Group>
            </Form.Item>
            {filterType === FILTER_TYPE.CONDITION && (
              <Form.Item field="filterCondition">
                <ConditionEditor
                  data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                />
              </Form.Item>
            )}
            <Form.Item label="排序规则" required field="sortBy">
              <SortByEditor
                data={triggerEditorSignal.nodeData.value[node.id]?.sortBy || []}
                fields={conditionFields}
              ></SortByEditor>
            </Form.Item>
            <div style={{ color: '#4e5969' }}>仅查询排序的第一条数据</div>
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

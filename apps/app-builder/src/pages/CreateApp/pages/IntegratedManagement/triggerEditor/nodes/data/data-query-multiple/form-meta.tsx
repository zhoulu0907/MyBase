import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  FILTER_TYPE,
  getEntityFields,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair,
  type SelectOption
} from '@onebase/app';
import { useEffect, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';
import { getBeforeCurQueryNodes } from '../../../components/utils';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);

  const filterType = Form.useWatch('filterType', payloadForm);
  const mainDataSource = Form.useWatch('mainDataSource', payloadForm);
  //   const subDataSource = Form.useWatch('subDataSource', payloadForm);

  const [clearSortBy, setClearSortBy] = useState<number>(0);
  // 数据源选择
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  /**
   * 获取方式变更
   * 更新数据源下拉列表，清除已选择数据源
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const handleDataTypeChange = (curDataType: number) => {
    payloadForm.clearFields(['mainDataSource', 'subDataSource', 'sortBy']);
    setClearSortBy(clearSortBy + 1);
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

    getEntityList(curDataType);

    clearDataSourceOriginNodeId();
  };

  const handleSubDataSourceChange = (curSubDataSource: string) => {
    payloadForm.clearFields(['sortBy']);
    setClearSortBy(clearSortBy + 1);
    const originNodeData = triggerEditorSignal.nodeData.value[curSubDataSource];
    const originNodeId = originNodeData?.dataSourceOriginNodeId || curSubDataSource;
    setConditionFields([]);
    setValidationTypes([]);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSourceOriginNodeId: dataType === DATA_SOURCE_TYPE.DATA_NODE ? originNodeId : undefined,
      sortBy: [] // 清除已选择排序字段
    });
    // 根据数据源重新获取字段列表
    if (curSubDataSource) {
      getFieldList(curSubDataSource);
    }

    clearDataSourceOriginNodeId();
  };

  const handleMainDataSourceChange = async (curMainDataSource: string) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    setClearSortBy(clearSortBy + 1);
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
    const res = await getEntityFieldsWithChildren(curMainDataSource);
    const newEntityList = (res.childEntities || []).map((item: any) => {
      return {
        entityId: item.childEntityId,
        entityName: item.childEntityName
      };
    });
    setEntityList(newEntityList);

    clearDataSourceOriginNodeId();
  };

  // 获取数据源列表
  const getEntityList = async (curDateType: number) => {
    if (curDateType === DATA_SOURCE_TYPE.FORM) {
      // 从主表中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setEntityList(res);
    } else if (curDateType === DATA_SOURCE_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE  dataSourceOriginNodeId
      const nodes = triggerEditorSignal.nodes.value;
      const newEntityList = getBeforeCurQueryNodes(node.id, nodes);
      setEntityList(
        newEntityList.map((item) => {
          return { entityName: item?.data?.title, entityId: item.id };
        })
      );
    } else if (curDateType === DATA_SOURCE_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    } else if (curDateType === DATA_SOURCE_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };

  // 获取排序字段下拉列表
  const getFieldList = async (curSubDataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // todo 根据不同获取方式走不同接口
    if (dataType === DATA_SOURCE_TYPE.FORM) {
      // 从主表中查询  FORM
      const res = await getEntityFields({ entityId: curSubDataSource });
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
      const nodeData = triggerEditorSignal.nodeData.value[curSubDataSource];
      if (!nodeData.dataSource) {
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
      const res = await getEntityFields({ entityId: curSubDataSource });
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
    }
  };

  // 清除数据节点依赖关系
  const clearDataSourceOriginNodeId = () => {
    const nodeData = triggerEditorSignal.nodeData.value;
    const keys = Object.keys(triggerEditorSignal.nodeData.value);
    for (let key of keys) {
      if (nodeData[key].dataSourceOriginNodeId === node.id) {
        triggerEditorSignal.setNodeData(nodeData[key].id, {
          ...nodeData[key],
          dataSourceOriginNodeId: undefined,
          dataSource: undefined,
          sortBy: [] // 清除已选择排序字段
        });
      }
    }
  };

  // 表单内容改变
  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    // 校验表单
    validateNodeForm(form, payloadForm, false);

    handlePropsOnChange(values);
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
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.FORM}>从主表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUBFORM}>从子表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从数据节点中查询</Radio>
                {/* <Radio value={DATA_SOURCE_TYPE.ASSOCIA_FORM}>从关联表单中查询</Radio> */}
              </Radio.Group>
            </Form.Item>
            {dataType === DATA_SOURCE_TYPE.SUBFORM ? (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainDataSource">
                    <Select allowClear onChange={handleMainDataSourceChange}>
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
                  <Form.Item field="subDataSource">
                    <Select allowClear onChange={handleSubDataSourceChange}>
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
                  <Form.Item field="mainDataSource">
                    <Select onChange={handleMainDataSourceChange} allowClear>
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
            <Form.Item label="查询规则" field="filterType" rules={[{ required: true, message: '请选择查询规则' }]}>
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
            <Form.Item label="排序规则" rules={[{ required: true, message: '请选择排序规则' }]} field="sortBy">
              <SortByEditor
                data={triggerEditorSignal.nodeData.value[node.id]?.sortBy || []}
                fields={conditionFields}
                form={payloadForm}
                clearSortByNum={clearSortBy}
              ></SortByEditor>
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

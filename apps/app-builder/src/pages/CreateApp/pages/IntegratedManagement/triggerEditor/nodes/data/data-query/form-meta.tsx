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
import { NodeType } from '../../const';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);
  const filterType = Form.useWatch('filterType', payloadForm);

  const [clearSortBy, setClearSortBy] = useState<number>(0);

  // 数据源选择
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    getEntityAndDataNodeList();
  }, []);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  /**
   * 获取方式变更
   * 更新数据源下拉列表，清除已选择数据源
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const handleDataTypeChange = async (curDataType: DATA_SOURCE_TYPE) => {
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
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);
    getEntityAndDataNodeList(curDataType);
  };
  const handleSubDataSourceChange = (curSubDataSource: string) => {
    payloadForm.clearFields(['sortBy']);
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
  };

  // 数据源变更  更新排序字段下拉列表，清除已选择排序字段 判断DATA_SOURCE_TYPE.DATA_NODE 绑定源节点nodeId
  const handleMainDataSourceChange = async (curMainDataSource: string) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: undefined, // null 和 '' 在 Select 中都被认为是值
      dataSourceOriginNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);
    console.log(curMainDataSource);
    const res = await getEntityFieldsWithChildren(curMainDataSource);
    const newEntityList = (res.childEntities || []).map((item: any) => {
      return {
        entityId: item.childEntityId,
        entityName: item.childEntityName
      };
    });
    setEntityList(newEntityList);
  };

  const handleDateNodeSourceChange = async (curDateNodeSource: string) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: undefined, // null 和 '' 在 Select 中都被认为是值
      dataSourceOriginNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    const nodes = triggerEditorSignal.nodes.value;

    const newDataNodeList = Object.values(nodes).filter(
      (item: any) => item.type === NodeType.DATA_QUERY_MULTIPLE && item.id !== curDateNodeSource
    );
    setDataNodeList(newDataNodeList);
  };

  // 获取各类数据源列表，不传值获取全部(用于初始化)
  const getEntityAndDataNodeList = async (curDateType?: DATA_SOURCE_TYPE) => {
    if (curDateType === DATA_SOURCE_TYPE.FORM || curDateType === undefined) {
      // 从主表中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setEntityList(res);
    }
    if (curDateType === DATA_SOURCE_TYPE.ASSOCIA_FORM || curDateType === undefined) {
      // 从关联表单中查询  ASSOCIA_FORM
    }

    if (curDateType === DATA_SOURCE_TYPE.SUBFORM || curDateType === undefined) {
      // 从子表中查询  SUBFORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }

    if (curDateType === DATA_SOURCE_TYPE.DATA_NODE || curDateType === undefined) {
      // 从上游数据节点查询
      const nodes = triggerEditorSignal.nodes.value;
      // TODO(mickey) 过滤掉当前节点,过滤blocks,并且只能选当前节点之前的节点
      const newDataNodeList = Object.values(nodes).filter(
        (item: any) => item.type === NodeType.DATA_QUERY_MULTIPLE && item.id !== node.id
      );
      setDataNodeList(newDataNodeList);
    }
  };

  // 获取排序字段下拉列表
  const getFieldList = async (curSubDataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // todo 根据不同获取方式走不同接口
    if (dataType === DATA_SOURCE_TYPE.FORM) {
      // 从表单中查询  FORM
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
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="查询方式" field="dataType" rules={[{ required: true, message: '请选择查询方式' }]}>
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.FORM}>从主表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUBFORM}>从子表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从数据节点中查询</Radio>
                {/* <Radio value={DATA_SOURCE_TYPE.ASSOCIA_FORM}>从关联表单中查询</Radio> */}
              </Radio.Group>
            </Form.Item>
             {/* 从子表中查询 */}
            {dataType === DATA_SOURCE_TYPE.SUBFORM && (
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
            )}

            {/* 从主表中查询 */}
            {dataType === DATA_SOURCE_TYPE.FORM && (
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

            {/* 从主数据节点中查询 */}
            {dataType === DATA_SOURCE_TYPE.DATA_NODE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainDataSource">
                    <Select onChange={handleDateNodeSourceChange} allowClear>
                      {dataNodeList.map((item) => (
                        <Select.Option key={item.id} value={item.id}>
                          {item.data.title}
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

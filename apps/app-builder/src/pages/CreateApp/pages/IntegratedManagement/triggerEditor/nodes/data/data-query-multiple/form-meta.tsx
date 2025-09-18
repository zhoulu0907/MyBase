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
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import { clearDataOriginNodeId } from '../../utils';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getBeforeCurQueryNodes } from '../../utils';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);

  const filterType = Form.useWatch('filterType', payloadForm);

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
  const handleDataTypeChange = (curDataType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainDataSource', 'subDataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainDataSource: undefined,
      subDataSource: undefined,
      dataNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });

    setEntityList([]);
    setMainEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    getEntityAndDataNodeList(curDataType);

    clearDataOriginNodeId(node.id);
  };

  const handleMainDataSourceChange = async (curMainDataSource: string) => {
    payloadForm.clearFields(['subDataSource', 'dataNodeId', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      subDataSource: undefined,
      dataNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setDataNodeList([]);
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

    clearDataOriginNodeId(node.id);
  };

  const handleSubDataSourceChange = (curSubDataSource: string) => {
    payloadForm.clearFields(['dataNodeId', 'sortBy']);
    setConditionFields([]);
    setValidationTypes([]);

    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataNodeId: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    // 根据数据源重新获取字段列表
    if (curSubDataSource) {
      getFieldList(curSubDataSource);
    }

    clearDataOriginNodeId(node.id);
  };

  const handleDateNodeSourceChange = async (dataNodeId: string) => {
    payloadForm.clearFields(['mainDataSource', 'subDataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      mainDataSource: undefined,
      subDataSource: undefined,
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    setDataNodeList([]);
    setConditionFields([]);
    setValidationTypes([]);

    const nodes = triggerEditorSignal.nodes.value;

    const newDataNodeList = getBeforeCurQueryNodes(node.id, nodes);
    setDataNodeList(newDataNodeList);

    clearDataOriginNodeId(node.id);
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
      // 过滤掉当前节点,过滤blocks,并且只能选当前节点之前的节点
      const newDataNodeList = getBeforeCurQueryNodes(node.id, nodes);

      setDataNodeList(newDataNodeList);
    }
  };

  // 获取排序字段下拉列表
  const getFieldList = async (dataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // 根据不同获取方式走不同接口
    if (dataType === DATA_SOURCE_TYPE.FORM) {
      // 从主表中查询  FORM
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
      // TODO(mickey) 根据数据节点查询数据
      const nodeData = triggerEditorSignal.nodeData.value[dataSource];
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
    }
  };

  // 表单内容改变
  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = async (changeValue: any, values: any) => {
    // console.log('onValuesChange: ', changeValue, values);

    // 校验表单
    validateNodeForm(form, payloadForm, false);

    handlePropsOnChange(values);
  };

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout="vertical" onValuesChange={onValuesChange} initialValues={getInitData()}>
            <Form.Item label="节点ID" field="id" initialValue={node.id}>
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

            {/* 从主数据节点中查询 */}
            {dataType === DATA_SOURCE_TYPE.DATA_NODE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="dataNodeId">
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

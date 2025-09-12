import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Input, Select, Radio, Grid } from '@arco-design/web-react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import {
  SortType,
  getEntityListByApp,
  getEntityFields,
  getFieldCheckTypeApi,
  type ConfitionField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useEffect, useState } from 'react';
import { useAppStore } from '@/store/store_app';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';

interface SelectOption {
  label: string;
  value: string;
}

// 数据源类型
const enum DATA_TYPE {
  FORM = 1,
  DATA_NODE = 2,
  ASSOCIA_FORM = 3,
  SUBFORM = 4
}

// 查询规则
const enum FILTER_TYPE {
  ALL = 0,
  CONDITION = 1
}

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  // 数据源选择
  const [entityList, setEntityList] = useState<SelectOption[]>([]);
  const [filterType, setFilterType] = useState<FILTER_TYPE>(0);
  
  // 查询条件
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);

  useEffect(() => {
    const formData = payloadForm.getFieldsValue();
    if (formData.dataType) {
      getEntityList(formData.dataType);
      if (formData.dataSource) {
        getFieldList(formData.dataType, formData.dataSource);
      }
    }
    if(formData.filterType){
      setFilterType(formData.filterType);
    }
  }, []);

  // 表单项内容变更
  const handlePropsOnChange = (key: string, value: any) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      [key]: value
    });
    if (key === 'dataType') {
      dataTypeChange(value);
    } else if (key === 'dataSource') {
      dataSourceChange(value);
    } else if (key === 'filterType') {
      setFilterType(value);
    }
  };
  /**
   * 获取方式变更
   * 更新数据源下拉列表，清除已选择数据源
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const dataTypeChange = async (value: number | string) => {
    payloadForm.clearFields(['dataSource', 'sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      dataSource: undefined, // null 和 '' 在 Select 中都被认为是值
      sortBy: [] // 清除已选择排序字段
    });
    setEntityList([]);
    if (value) {
      getEntityList(value);
    }
  };

  // 数据源变更  更新排序字段下拉列表，清除已选择排序字段
  const dataSourceChange = async (value: string) => {
    payloadForm.clearFields(['sortBy']);
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    triggerEditorSignal.setNodeData(node.id, {
      ...nodeData,
      sortBy: [] // 清除已选择排序字段
    });
    // 根据数据源重新获取字段列表
    if (value) {
      const dataType = payloadForm.getFieldValue('dataType');
      getFieldList(dataType, value);
    }
  };
  // 获取数据源列表
  const getEntityList = async (dataType: number | string) => {
    if (dataType === DATA_TYPE.FORM) {
      // 从表单中查询  FORM
      const res = await getEntityListByApp(curAppId);
      console.log('数据库表res: ', res);
      const fieldOptions = res.map((field: any) => ({
        label: field.entityName,
        value: field.entityId
      }));
      setEntityList(fieldOptions);
    } else if (dataType === DATA_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE
    } else if (dataType === DATA_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    } else if (dataType === DATA_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
    }
  };
  // 获取排序字段下拉列表
  const getFieldList = async (dataType: number | string, dataSource: string) => {
    // 根据数据源 查询指定实体的字段列表
    // todo 根据不同获取方式走不同接口
    if (dataType === DATA_TYPE.FORM) {
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
    } else if (dataType === DATA_TYPE.DATA_NODE) {
      // 从数据节点中查询  DATA_NODE
    } else if (dataType === DATA_TYPE.ASSOCIA_FORM) {
      // 从关联表单中查询  ASSOCIA_FORM
    } else if (dataType === DATA_TYPE.SUBFORM) {
      // 从子表中查询  SUBFORM
    }
  };

  const [payloadForm] = Form.useForm();

  // 获取方式  数据查询的来源
  const dataTypeOptions = [
    { label: '从表单中查询', value: DATA_TYPE.FORM },
    { label: '从数据节点中查询', value: DATA_TYPE.DATA_NODE },
    { label: '从关联表单中查询', value: DATA_TYPE.ASSOCIA_FORM },
    { label: '从子表中查询', value: DATA_TYPE.SUBFORM }
  ];
  const quertTypeOptions = [
    { label: '全部数据', value: FILTER_TYPE.ALL },
    { label: '按条件过滤', value: FILTER_TYPE.CONDITION }
  ];
  // 排序类型
  const sortTypeOptions = [
    { label: '升序', value: SortType.ASC },
    { label: '降序', value: SortType.DESC }
  ];

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout="vertical" initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}>
            <Form.Item label="节点ID" field="id " initialValue={node.id}>
              <Input disabled />
            </Form.Item>
            <Form.Item label="节点名称" field="nodeName" required>
              <Input onChange={(e) => handlePropsOnChange('nodeName', e)} />
            </Form.Item>
            <Form.Item label="查询方式" field="dataType">
              <Radio.Group direction="vertical" onChange={(e) => handlePropsOnChange('dataType', e)}>
                {dataTypeOptions.map((item) => (
                  <Radio value={item.value}>{item.label}</Radio>
                ))}
              </Radio.Group>
            </Form.Item>
            <Form.Item field="dataSource">
              <Grid.Row align='center'>
                <Grid.Col span={1}>从</Grid.Col>
                <Grid.Col span={19}>
                  <Select
                    options={entityList}
                    allowClear
                    onChange={(e) => handlePropsOnChange('dataSource', e)}
                  ></Select>
                </Grid.Col>
                <Grid.Col span={4} style={{textAlign:'center'}}>
                  <span>中查询数据</span>
                </Grid.Col>
              </Grid.Row>
            </Form.Item>
            <Form.Item label="查询规则" field="filterType">
              <Radio.Group onChange={(e) => handlePropsOnChange('filterType', e)}>
                {quertTypeOptions.map((item) => (
                  <Radio value={item.value}>{item.label}</Radio>
                ))}
              </Radio.Group>
            </Form.Item>
            {filterType === FILTER_TYPE.CONDITION && (
              <Form.Item field="filterCondition">
                <ConditionEditor
                  onChange={(e) => handlePropsOnChange('filterCondition', e)}
                  data={triggerEditorSignal.nodeData.value[node.id]?.filterCondition || []}
                  fields={conditionFields}
                  entityFieldValidationTypes={validationTypes}
                />
              </Form.Item>
            )}
            <Form.Item label="排序规则" required field="sortBy">
              <SortByEditor
                onChange={(e) => handlePropsOnChange('sortBy', e)}
                data={triggerEditorSignal.nodeData.value[node.id]?.sortBy || []}
                fields={conditionFields}
                sortByTypes={sortTypeOptions}
              ></SortByEditor>
            </Form.Item>
            <div style={{color:'#4e5969'}}>仅查询排序的第一条数据</div>
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

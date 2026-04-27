import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { DATA_SOURCE_TYPE, FILTER_TYPE } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useEntityFields, useIsSidebar, useMainEntityList, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntityList } = useMainEntityList();
  const { subEntityList, mainEntityFields, subEntityFields, validationTypes, handleMainTableChange, resetFields } =
    useEntityFields();

  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const subTableName = Form.useWatch('subTableName', payloadForm);
  const filterType = Form.useWatch('filterType', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData?.dataType && nodeData?.mainTableName && mainEntityList.length > 0) {
      handleMainTableChange(nodeData.mainTableName, mainEntityList);
    }
  }, [mainEntityList.length]);

  // 删除方式变更
  const handleDataTypeChange = (curDataType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition']);
    resetFields();
  };

  // 主表数据变更
  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'filterCondition']);
    await handleMainTableChange(curMainTableName, mainEntityList);
  };

  // 子表数据变更
  const handleSubTableNameChange = (_curSubTableName: string) => {
    payloadForm.clearFields(['filterCondition']);
  };

  const handleFilterTypeChange = (_value: FILTER_TYPE) => {
    payloadForm.clearFields(['filterCondition']);
  };

  const conditionFieldsData = useMemo((): TreeSelectDataType[] => {
    if (dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      return [mainEntityFields];
    }

    if (dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subTableName);

      if (curSubEntityFields) {
        return [mainEntityFields, curSubEntityFields];
      }

      return [mainEntityFields];
    }

    return [];
  }, [dataType, mainEntityFields, subEntityFields, mainTableName, subTableName]);

  const getInitData = () => {
    return { ...triggerEditorSignal.nodeData.value[node.id] };
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form form={payloadForm} layout="vertical" initialValues={getInitData()} requiredSymbol={{ position: 'end' }}>
            <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
              <Input disabled />
            </Form.Item>

            <Form.Item label="删除方式" field="dataType" required>
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>删除主表数据</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>删除子表数据</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 从主表中查询 */}
            {dataType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainTableName" disabled={!dataType}>
                    <Select onChange={handleMainTableNameChange} allowClear>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
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
            {dataType === DATA_SOURCE_TYPE.SUB_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainTableName" disabled={!dataType}>
                    <Select allowClear onChange={handleMainTableNameChange}>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
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
                  <Form.Item field="subTableName" disabled={!mainTableName}>
                    <Select allowClear onChange={handleSubTableNameChange}>
                      {subEntityList.map((item) => (
                        <Select.Option key={item.tableName} value={item.tableName}>
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

            <Grid.Row>
              <Form.Item label="查询规则" field="filterType" rules={[{ required: true, message: '请选择查询规则' }]}>
                <Radio.Group onChange={handleFilterTypeChange}>
                  <Radio value={FILTER_TYPE.ALL}>全部数据</Radio>
                  <Radio value={FILTER_TYPE.CONDITION}>按条件过滤</Radio>
                </Radio.Group>
              </Form.Item>
            </Grid.Row>

            {filterType === FILTER_TYPE.CONDITION && (
              <Grid.Row>
                <ConditionEditor
                  nodeId={node.id}
                  label="条件"
                  required
                  fields={conditionFieldsData}
                  entityFieldValidationTypes={validationTypes}
                  form={payloadForm}
                />
              </Grid.Row>
            )}
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

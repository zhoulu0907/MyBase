import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { DATA_SOURCE_TYPE } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useEntityFields, useIsSidebar, useMainEntityList, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  const { mainEntityList } = useMainEntityList();
  const {
    subEntityList,
    mainEntityFields,
    subEntityFields,
    validationTypes,
    fieldDataList,
    handleMainTableChange,
    handleSubTableChange,
    resetFields
  } = useEntityFields();

  const [payloadForm] = Form.useForm();

  const updateType = Form.useWatch('updateType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const subTableName = Form.useWatch('subTableName', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData?.updateType && nodeData?.mainTableName && mainEntityList.length > 0) {
      handleMainTableChange(nodeData.mainTableName, mainEntityList).then(() => {
        if (nodeData.updateType === DATA_SOURCE_TYPE.SUB_TABLE && nodeData?.subTableName) {
          setTimeout(() => {
            handleSubTableChange(nodeData.subTableName, subEntityList);
          }, 100);
        }
      });
    }
  }, [mainEntityList.length]);

  // 更新方式变更
  const handleDataTypeChange = (curUpdateType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition', 'fields']);
    resetFields();
  };

  // 主表数据变更
  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'filterCondition', 'fields']);
    await handleMainTableChange(curMainTableName, mainEntityList);
  };

  // 子表数据变更
  const handleSubTableNameChange = async (curSubTableName: string) => {
    payloadForm.clearFields(['filterCondition', 'fields']);
    await handleSubTableChange(curSubTableName, subEntityList);
  };

  const conditionFieldsData = useMemo((): TreeSelectDataType[] => {
    if (updateType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      return [mainEntityFields];
    }
    if (updateType === DATA_SOURCE_TYPE.SUB_TABLE) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subTableName);
      if (curSubEntityFields) {
        return [mainEntityFields, curSubEntityFields];
      }
      return [mainEntityFields];
    }
    return [];
  }, [updateType, mainEntityFields, subEntityFields, subTableName]);

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

            <Form.Item label="更新方式" field="updateType" rules={[{ required: true, message: '请选择更新方式' }]}>
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>更新主表数据</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>更新子表数据</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 更新主表 */}
            {updateType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item
                    field="mainTableName"
                    disabled={!updateType}
                    rules={[{ required: true, message: '请选择' }]}
                  >
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
                  <span>的数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 更新子表 */}
            {updateType === DATA_SOURCE_TYPE.SUB_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item
                    field="mainTableName"
                    disabled={!updateType}
                    rules={[{ required: true, message: '请选择' }]}
                  >
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
                  <Form.Item
                    field="subTableName"
                    disabled={!mainTableName}
                    rules={[{ required: true, message: '请选择' }]}
                  >
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
              <ConditionEditor
                nodeId={node.id}
                label="条件"
                required
                fields={conditionFieldsData}
                entityFieldValidationTypes={validationTypes}
                form={payloadForm}
              />
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="更新规则" field="fields" rules={[{ required: true, message: '请填写更新规则' }]}>
                <FieldEditor nodeId={node.id} fieldList={fieldDataList} form={payloadForm} />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="未匹配到数据时" field="noData">
                <RadioGroup defaultValue="skip">
                  <Radio value="skip">跳过当前节点</Radio>
                  <Radio value="add">新增一条数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>
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

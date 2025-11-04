import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  type AppEntityField,
  type ChildEntity,
  type ConditionField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { validateNodeForm } from '../../utils';

const RadioGroup = Radio.Group;

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();

  const updateType = Form.useWatch('updateType', payloadForm);

  const mainEntityId = Form.useWatch('mainEntityId', payloadForm);
  const subEntityId = Form.useWatch('subEntityId', payloadForm);

  // 数据源选择
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [mainEntityFields, setMainEntityFields] = useState<TreeSelectDataType>([]);
  const [subEntityFields, setSubEntityFields] = useState<TreeSelectDataType[]>([]);

  const [conditionFields, setConditionFields] = useState<ConditionField[]>([]);

  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    // 初始化 获取实体和数据节点列表数据，用于下拉菜单
    getEntityList();
    // 从缓存中载入节点数据
    init();
  }, []);

  /**
   * 数据查询方式变更（主表、子表、数据节点中查询）
   * 更新数据源下拉列表，清除已选择数据源
   * 更新条件过滤
   * 清除排序字段下拉列表，清除已选择排序字段
   */
  const handleDataTypeChange = (curUpdateType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainEntityId', 'subEntityId', 'filterCondition', 'fields']);

    setMainEntityList([]);
    setSubEntityList([]);
    setFieldDataList([]);
    setConditionFields([]);
    setValidationTypes([]);

    getEntityList(curUpdateType);
  };

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      if (nodeData.updateType === DATA_SOURCE_TYPE.FORM || nodeData.updateType === DATA_SOURCE_TYPE.SUBFORM) {
        if (!nodeData?.mainEntityId) {
          return;
        }

        const fieldIds: string[] = [];
        const res = await getEntityFieldsWithChildren(nodeData?.mainEntityId);

        const newSubEntityList = (res.childEntities || []).map((item: any) => {
          return {
            entityId: item.childEntityId,
            entityName: item.childEntityName
          };
        });

        setSubEntityList(newSubEntityList);

        if (res.parentFields) {
          const fields = res.parentFields.map((item: AppEntityField) => {
            fieldIds.push(item.fieldId);
            return {
              key: item.fieldId,
              title: item.displayName,
              fieldType: item.fieldType
            };
          });

          setMainEntityFields({
            key: res.entityId,
            title: res.entityName,
            children: fields
          });

          if (nodeData.updateType === DATA_SOURCE_TYPE.FORM) {
            setFieldDataList(res.parentFields);
          }
        }

        if (res.childEntities) {
          const subFields: TreeSelectDataType[] = [];
          res.childEntities.forEach((item: ChildEntity) => {
            const fields = item.childFields.map((item: AppEntityField) => {
              fieldIds.push(item.fieldId);
              return {
                key: item.fieldId,
                title: item.displayName,
                fieldType: item.fieldType
              };
            });
            subFields.push({
              key: item.childEntityId,
              title: item.childEntityName,
              children: fields
            });
          });

          setSubEntityFields(subFields);

          const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
          setValidationTypes(newValidationTypes);

          if (nodeData.updateType === DATA_SOURCE_TYPE.SUBFORM) {
            const newFieldDataList: any[] = [];

            subFields
              .find((item) => item.key == nodeData.subEntityId)
              ?.children?.forEach((item) => {
                newFieldDataList.push({
                  fieldId: item.key,
                  displayName: item.title,
                  fieldType: item.fieldType
                });
              }) || [];

            setFieldDataList(newFieldDataList);
          }
        }
      }
    }
  };

  const getEntityList = async (curDateType?: DATA_SOURCE_TYPE) => {
    if (curDateType === DATA_SOURCE_TYPE.FORM || curDateType === undefined) {
      // 从主表中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }

    if (curDateType === DATA_SOURCE_TYPE.SUBFORM || curDateType === undefined) {
      // 从子表中查询  SUBFORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
  };

  const handleMainEntityIdChange = async (curMainEntityId: string) => {
    if (!curMainEntityId) {
      return;
    }
    payloadForm.clearFields(['subEntityId', 'filterCondition', 'fields']);

    setSubEntityList([]);
    setValidationTypes([]);
    setFieldDataList([]);
    setConditionFields([]);

    const fieldIds: string[] = [];
    const res = await getEntityFieldsWithChildren(curMainEntityId);
    const newEntityList = (res.childEntities || []).map((item: any) => {
      return {
        entityId: item.childEntityId,
        entityName: item.childEntityName
      };
    });

    setSubEntityList(newEntityList);

    if (res.parentFields) {
      const fields = res.parentFields.map((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        return {
          key: item.fieldId,
          title: item.displayName,
          fieldType: item.fieldType
        };
      });
      setMainEntityFields({
        key: res.entityId,
        title: res.entityName,
        children: fields
      });

      setFieldDataList(res.parentFields);
    }

    if (res.childEntities) {
      const subFields: TreeSelectDataType[] = [];
      res.childEntities.forEach((item: ChildEntity) => {
        const fields = item.childFields.map((item: AppEntityField) => {
          fieldIds.push(item.fieldId);
          return {
            key: item.fieldId,
            title: item.displayName,
            fieldType: item.fieldType
          };
        });
        subFields.push({
          key: item.childEntityId,
          title: item.childEntityName,
          children: fields
        });
      });

      setSubEntityFields(subFields);

      const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
      setValidationTypes(newValidationTypes);
    }
  };

  const handleSubEntityIdChange = (_curSubEntityId: string) => {
    payloadForm.clearFields(['filterCondition', 'fields']);

    setConditionFields([]);

    const newFieldDataList: any[] = [];

    subEntityFields
      .find((item) => item.key == _curSubEntityId)
      ?.children?.forEach((item) => {
        newFieldDataList.push({
          fieldId: item.key,
          displayName: item.title,
          fieldType: item.fieldType
        });
      }) || [];

    setFieldDataList(newFieldDataList);
  };

  const conditionFieldsData = useMemo((): TreeSelectDataType[] => {
    if (updateType === DATA_SOURCE_TYPE.FORM) {
      return [mainEntityFields];
    }
    if (updateType === DATA_SOURCE_TYPE.SUBFORM) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subEntityId);
      if (curSubEntityFields) {
        return [mainEntityFields, curSubEntityFields];
      }
      return [mainEntityFields];
    }

    return [];
  }, [updateType, mainEntityFields, subEntityFields, subEntityId]);

  const conditionFieldsForEditor = useMemo((): ConditionField[] => {
    if (updateType === DATA_SOURCE_TYPE.FORM) {
      return (
        (mainEntityFields.children || [])?.map((item) => ({
          label: item.title as string,
          value: item.key as string,
          fieldType: item.fieldType
        })) || []
      );
    }
    if (updateType === DATA_SOURCE_TYPE.SUBFORM) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subEntityId);
      if (curSubEntityFields) {
        return (
          (curSubEntityFields.children || [])?.map((item) => ({
            label: item.title as string,
            value: item.key as string,
            fieldType: item.fieldType
          })) || []
        );
      }

      return [];
    }

    return [];
  }, [updateType, mainEntityFields, subEntityFields, subEntityId]);

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
                <Radio value={DATA_SOURCE_TYPE.FORM}>更新主表数据</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUBFORM}>更新子表数据</Radio>
              </Radio.Group>
            </Form.Item>

            {/* 从主表中查询 */}
            {updateType === DATA_SOURCE_TYPE.FORM && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  更新
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item
                    field="mainEntityId"
                    disabled={!updateType}
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select onChange={handleMainEntityIdChange} allowClear>
                      {mainEntityList.map((item) => (
                        <Select.Option key={item.entityId} value={item.entityId}>
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

            {/* 从子表中查询 */}
            {updateType === DATA_SOURCE_TYPE.SUBFORM && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item
                    field="mainEntityId"
                    disabled={!updateType}
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select allowClear onChange={handleMainEntityIdChange}>
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
                  <Form.Item
                    field="subEntityId"
                    disabled={!mainEntityId}
                    rules={[{ required: true, message: '请选择' }]}
                  >
                    <Select allowClear onChange={handleSubEntityIdChange}>
                      {subEntityList.map((item) => (
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

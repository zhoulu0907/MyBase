import { RELATIONSHIP_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/types';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, InputNumber, Radio, Select } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  FILTER_TYPE,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  getFieldCheckTypeApi,
  RELATION_TYPE,
  type AppEntityField,
  type ChildEntity,
  type ConditionField,
  type EntityFieldValidationTypes,
  type MetadataEntityPair
} from '@onebase/app';
import { NodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useMemo, useState } from 'react';
import ConditionEditor from '../../../components/condition-editor';
import SortByEditor from '../../../components/sortby-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getDataNodeSource, getPrecedingNodes, validateNodeForm } from '../../utils';

const ALLOW_DATANODE_TYPES = [
  NodeType.DATA_QUERY_MULTIPLE,
  NodeType.LOOP,
  NodeType.IF,
  NodeType.IF_BLOCK,
  NodeType.CASE,
  NodeType.CASE_DEFAULT
];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  useSignals();

  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [payloadForm] = Form.useForm();

  const dataType = Form.useWatch('dataType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const subTableName = Form.useWatch('subTableName', payloadForm);
  const filterType = Form.useWatch('filterType', payloadForm);

  // 数据源选择
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  // 查询规则
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  const [mainEntityFields, setMainEntityFields] = useState<TreeSelectDataType>([]);
  const [subEntityFields, setSubEntityFields] = useState<TreeSelectDataType[]>([]);
  const [dataNodeEntityFields, setDataNodeEntityFields] = useState<TreeSelectDataType>([]);

  const [conditionFields, setConditionFields] = useState<ConditionField[]>([]);

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
  const handleDataTypeChange = (curDataType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition', 'sortBy']);

    setMainEntityList([]);
    setSubEntityList([]);
    setDataNodeList([]);
    setValidationTypes([]);

    getEntityList(curDataType);
  };

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      const curEntityList = await getEntityListByApp(curAppId);

      const curMainEntities = curEntityList.filter(
        (item: MetadataEntityPair) =>
          item.relationType !== RELATION_TYPE.SLAVE ||
          (item.relationType === RELATION_TYPE.SLAVE &&
            !item.relationshipTypes.includes(RELATIONSHIP_TYPE.SUBTABLE_ONE_TO_MANY))
      );

      if (nodeData.dataType === DATA_SOURCE_TYPE.MAIN_TABLE || nodeData.dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
        if (!nodeData?.mainTableName) {
          return;
        }

        const mainEntityId = curMainEntities.find(
          (item: MetadataEntityPair) => item.tableName === nodeData.mainTableName
        )?.entityId;

        if (!mainEntityId) {
          return;
        }

        const fieldIds: string[] = [];
        const res = await getEntityFieldsWithChildren(mainEntityId);

        const newSubEntityList = (res.childEntities || []).map((item: any) => {
          return {
            entityId: item.childEntityId,
            tableName: item.childTableName,
            entityName: item.childEntityName
          };
        });

        setSubEntityList(newSubEntityList);

        if (res.parentFields) {
          const fields = res.parentFields.map((item: AppEntityField) => {
            fieldIds.push(item.fieldId);
            return {
              key: `${res.tableName}.${item.fieldName}`,
              title: item.displayName,
              fieldType: item.fieldType
            };
          });

          setMainEntityFields({
            key: res.entityId,
            title: res.entityName,
            children: fields
          });
        }

        if (res.childEntities) {
          const subFields: TreeSelectDataType[] = [];
          res.childEntities.forEach((subEntity: ChildEntity) => {
            const fields = subEntity.childFields.map((item: AppEntityField) => {
              fieldIds.push(item.fieldId);
              return {
                key: `${subEntity.childTableName}.${item.fieldName}`,
                title: item.displayName,
                fieldType: item.fieldType
              };
            });
            subFields.push({
              key: subEntity.childEntityId,
              title: subEntity.childEntityName,
              children: fields
            });
          });

          setSubEntityFields(subFields);
        }

        const newValidationTypes = await getFieldCheckTypeApi(fieldIds);

        newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
          const fieldName =
            [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
          item.fieldKey = `${res.tableName}.${fieldName}`;

          if (!fieldName) {
            for (const subEntity of res.childEntities) {
              const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
              if (foundField) {
                // 同时返回字段名和子表tableName
                item.fieldKey = `${subEntity.childTableName}.${foundField.fieldName}`;
              }
            }
          }
        });

        setValidationTypes(newValidationTypes);
      }

      if (nodeData.dataType === DATA_SOURCE_TYPE.DATA_NODE) {
        if (!nodeData?.dataNodeId) {
          return;
        }

        const fieldIds: string[] = [];

        const originDataSource = getDataNodeSource(nodeData?.dataNodeId);
        const entityId = curEntityList.find(
          (item: MetadataEntityPair) => item.tableName === originDataSource
        )?.entityId;

        const res = await getEntityFieldsWithChildren(entityId);

        if (res.parentFields) {
          const fields = res.parentFields.map((item: AppEntityField) => {
            fieldIds.push(item.fieldId);
            return {
              key: `${originDataSource}.${item.fieldName}`,
              title: item.displayName,
              fieldType: item.fieldType
            };
          });

          setDataNodeEntityFields({
            key: res.entityId,
            title: res.entityName,
            children: fields
          });
        }

        const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
        newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
          const fieldName =
            [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
          item.fieldKey = `${originDataSource}.${fieldName}`;
        });

        setValidationTypes(newValidationTypes);
      }
    }
  };

  const getEntityList = async (curDateType?: DATA_SOURCE_TYPE) => {
    if (curDateType === DATA_SOURCE_TYPE.MAIN_TABLE || DATA_SOURCE_TYPE.SUB_TABLE || curDateType === undefined) {
      // 从主表中查询  FORM
      const res = await getEntityListByApp(curAppId);
      setMainEntityList(res);
    }
    if (curDateType === DATA_SOURCE_TYPE.ASSOCIA_FORM || curDateType === undefined) {
      // 从关联表单中查询  ASSOCIA_FORM
    }

    if (curDateType === DATA_SOURCE_TYPE.DATA_NODE || curDateType === undefined) {
      // 从上游数据节点查询
      const nodes = triggerEditorSignal.nodes.value;
      // 过滤掉当前节点,过滤blocks,并且只能选当前节点之前的节点
      const newDataNodeList = getPrecedingNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
      setDataNodeList(newDataNodeList);
    }
  };

  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'dataNodeId', 'filterCondition', 'sortBy']);

    setSubEntityList([]);
    setDataNodeList([]);
    setValidationTypes([]);

    const fieldIds: string[] = [];

    const mainEntityId = mainEntityList.find((item) => item.tableName === curMainTableName)?.entityId;
    if (!mainEntityId) {
      return;
    }

    const res = await getEntityFieldsWithChildren(mainEntityId);
    console.log(res);

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
          key: `${res.tableName}.${item.fieldName}`,
          title: item.displayName,
          fieldType: item.fieldType
        };
      });
      setMainEntityFields({
        key: res.entityId,
        title: res.entityName,
        children: fields
      });
    }

    if (res.childEntities) {
      const subFields: TreeSelectDataType[] = [];
      res.childEntities.forEach((subEntity: ChildEntity) => {
        const fields = subEntity.childFields.map((item: AppEntityField) => {
          fieldIds.push(item.fieldId);
          return {
            key: `${subEntity.childTableName}.${item.fieldName}`,
            title: item.displayName,
            fieldType: item.fieldType
          };
        });
        subFields.push({
          key: subEntity.childEntityId,
          title: subEntity.childEntityName,
          children: fields
        });
      });

      setSubEntityFields(subFields);
    }

    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
      const fieldName =
        [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
      item.fieldKey = `${res.tableName}.${fieldName}`;

      if (!fieldName) {
        for (const subEntity of res.childEntities) {
          const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
          if (foundField) {
            // 同时返回字段名和子表tableName
            item.fieldKey = `${subEntity.childTableName}.${foundField.fieldName}`;
          }
        }
      }
    });
    setValidationTypes(newValidationTypes);
  };

  const handleSubTableNameChange = (_curSubEntityId: string) => {
    payloadForm.clearFields(['dataNodeId', 'filterCondition', 'sortBy']);
  };

  const handleDateNodeSourceChange = async (dataNodeId: string) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'filterCondition', 'sortBy']);

    setMainEntityList([]);
    setSubEntityList([]);
    setValidationTypes([]);

    const fieldIds: string[] = [];

    const originDataSource = getDataNodeSource(dataNodeId);

    const curEntityList = await getEntityListByApp(curAppId);
    const entityId = curEntityList.find((item: MetadataEntityPair) => item.tableName === originDataSource)?.entityId;
    if (!entityId) {
      return;
    }

    const res = await getEntityFieldsWithChildren(originDataSource);

    if (res.parentFields) {
      const fields = res.parentFields.map((item: AppEntityField) => {
        fieldIds.push(item.fieldId);
        return {
          key: `${res.tableName}.${item.fieldName}`,
          title: item.displayName,
          fieldType: item.fieldType
        };
      });

      setDataNodeEntityFields({
        key: res.entityId,
        title: res.entityName,
        children: fields
      });
    }

    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
      const fieldName =
        [...res.parentFields].find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
      item.fieldKey = `${res.tableName}.${fieldName}`;

      if (!fieldName) {
        for (const subEntity of res.childEntities) {
          const foundField = subEntity.childFields.find((field: AppEntityField) => field.fieldId == item.fieldId);
          if (foundField) {
            // 同时返回字段名和子表tableName
            item.fieldKey = `${subEntity.childTableName}.${foundField.fieldName}`;
          }
        }
      }
    });
    setValidationTypes(newValidationTypes);
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
    if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      return [dataNodeEntityFields];
    }

    return [];
  }, [dataType, mainEntityFields, subEntityFields, mainTableName, subTableName, dataNodeEntityFields]);

  const conditionFieldsForEditor = useMemo((): ConditionField[] => {
    if (dataType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      return (
        (mainEntityFields.children || [])?.map((item) => ({
          label: item.title as string,
          value: item.key as string,
          fieldType: item.fieldType
        })) || []
      );
    }
    if (dataType === DATA_SOURCE_TYPE.SUB_TABLE) {
      const curSubEntityFields = subEntityFields.find((item) => item.key === subTableName);
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
    if (dataType === DATA_SOURCE_TYPE.DATA_NODE) {
      return (
        (dataNodeEntityFields.children || [])?.map((item) => ({
          label: item.title as string,
          value: item.key as string,
          fieldType: item.fieldType
        })) || []
      );
    }

    return [];
  }, [dataType, mainEntityFields, subEntityFields, mainTableName, subTableName, dataNodeEntityFields]);

  const getInitData = () => {
    return {
      // 初始值写前面,覆写放后面
      maxCount: 500,
      ...triggerEditorSignal.nodeData.value[node.id]
    };
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

            <Form.Item label="查询方式" field="dataType" rules={[{ required: true, message: '请选择查询方式' }]}>
              <Radio.Group direction="vertical" onChange={handleDataTypeChange}>
                <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>从主表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>从子表中查询</Radio>
                <Radio value={DATA_SOURCE_TYPE.DATA_NODE}>从数据节点中查询</Radio>
                {/* <Radio value={DATA_SOURCE_TYPE.ASSOCIA_FORM}>从关联表单中查询</Radio> */}
              </Radio.Group>
            </Form.Item>

            {/* 从主表中查询 */}
            {dataType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainTableName" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
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
                  <Form.Item field="mainTableName" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
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
                    field="subEntityId"
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

            {/* 从数据节点中查询 */}
            {dataType === DATA_SOURCE_TYPE.DATA_NODE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  从
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="dataNodeId" disabled={!dataType} rules={[{ required: true, message: '请选择' }]}>
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

            <Grid.Row>
              <Form.Item label="排序规则">
                <SortByEditor
                  data={triggerEditorSignal.nodeData.value[node.id]?.sortBy || []}
                  fields={conditionFields}
                  form={payloadForm}
                ></SortByEditor>
              </Form.Item>
            </Grid.Row>

            <Grid.Row gutter={8} align="center">
              <Grid.Col span={6} style={{ color: 'grey' }}>
                按排序规则，获取过滤后的前
              </Grid.Col>
              <Grid.Col span={3}>
                <Form.Item field="maxCount" style={{ marginTop: '20px' }}>
                  <InputNumber min={1} max={500} />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={4} style={{ color: 'grey' }}>
                条数据
              </Grid.Col>
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

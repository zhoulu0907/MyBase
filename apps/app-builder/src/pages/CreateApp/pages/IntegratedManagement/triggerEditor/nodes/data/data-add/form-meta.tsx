import { RELATIONSHIP_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/types';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { useAppStore } from '@/store/store_app';
import { Form, Grid, Input, Radio, Select } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import {
  DATA_SOURCE_TYPE,
  getEntityFields,
  getEntityFieldsWithChildren,
  getEntityListByApp,
  RELATION_TYPE,
  type AppEntityField,
  type MetadataEntityPair
} from '@onebase/app';
import { NodeType } from '@onebase/common';
import { useEffect, useState } from 'react';
import FieldEditor from '../../../components/field-editor';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';
import { getPrecedingNodes, validateNodeForm } from '../../utils';

const RadioGroup = Radio.Group;

const ALLOW_DATANODE_TYPES = [NodeType.DATA_QUERY_MULTIPLE, NodeType.DATA_QUERY, NodeType.DATA_CALC];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();
  // 当前页应用id
  const { curAppId } = useAppStore();
  const [fieldDataList, setFieldDataList] = useState<AppEntityField[]>([]);
  const [mainEntityList, setMainEntityList] = useState<MetadataEntityPair[]>([]);
  const [subEntityList, setSubEntityList] = useState<MetadataEntityPair[]>([]);

  const [dataNodeList, setDataNodeList] = useState<any[]>([]);

  const [payloadForm] = Form.useForm();

  const addType = Form.useWatch('addType', payloadForm);
  const mainTableName = Form.useWatch('mainTableName', payloadForm);
  const batchType = Form.useWatch('batchType', payloadForm);
  const dataNodeId = Form.useWatch('dataNodeId', payloadForm);

  useEffect(() => {
    payloadForm && validateNodeForm(form, payloadForm, true);
  }, [payloadForm]);

  useEffect(() => {
    init();
  }, []);

  const init = async () => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];
    if (nodeData) {
      const res = await getEntityListByApp(curAppId);

      const curMainEntities = res.filter(
        (item: MetadataEntityPair) =>
          item.relationType !== RELATION_TYPE.SLAVE ||
          (item.relationType === RELATION_TYPE.SLAVE &&
            !item.relationshipTypes.includes(RELATIONSHIP_TYPE.SUBTABLE_ONE_TO_MANY))
      );

      setMainEntityList(curMainEntities);
      if (nodeData.addType === DATA_SOURCE_TYPE.MAIN_TABLE) {
        // 在主表中
        getFieldList(nodeData?.mainTableName, curMainEntities);
      }
      if (nodeData.addType === DATA_SOURCE_TYPE.SUB_TABLE) {
        if (!nodeData?.mainTableName) {
          return;
        }

        const mainEntityId = curMainEntities.find(
          (item: MetadataEntityPair) => item.tableName === nodeData.mainTableName
        )?.entityId;

        if (!mainEntityId) {
          return;
        }
        const res = await getEntityFieldsWithChildren(mainEntityId);
        const curSubEntityList = (res.childEntities || []).map((item: any) => {
          return {
            entityId: item.childEntityId,
            tableName: item.childTableName,
            entityName: item.childEntityName
          };
        });

        setSubEntityList(curSubEntityList);
        getFieldList(nodeData.subTableName, curSubEntityList);
      }
    }

    const nodes = triggerEditorSignal.nodes.value;
    const newDataNodeList = getPrecedingNodes(node.id, nodes, ALLOW_DATANODE_TYPES);
    setDataNodeList(newDataNodeList);
  };

  // 新增方式变更
  const handleDataTypeChange = (curAddType: DATA_SOURCE_TYPE) => {
    payloadForm.clearFields(['mainTableName', 'subTableName', 'dataNodeId', 'fields']);
    setMainEntityList([]);
    setSubEntityList([]);
    setFieldDataList([]);
    setMainEntityList([]);
    getEntityList(curAddType);
  };

  const getEntityList = async (curAddType?: DATA_SOURCE_TYPE) => {
    const nodeData = triggerEditorSignal.nodeData.value[node.id];

    const res = await getEntityListByApp(curAppId);
    const curMainEntities = res.filter(
      (item: MetadataEntityPair) =>
        item.relationType !== RELATION_TYPE.SLAVE ||
        (item.relationType === RELATION_TYPE.SLAVE &&
          !item.relationshipTypes.includes(RELATIONSHIP_TYPE.SUBTABLE_ONE_TO_MANY))
    );
    setMainEntityList(curMainEntities);

    if (curAddType === DATA_SOURCE_TYPE.MAIN_TABLE || curAddType === undefined) {
      // 从主表中
      getFieldList(nodeData?.mainTableName, res);
    }
    if (curAddType === DATA_SOURCE_TYPE.SUB_TABLE) {
      // 从子表中
    }
  };
  // 主表数据变更
  const handleMainTableNameChange = async (curMainTableName: string) => {
    payloadForm.clearFields(['subTableName', 'dataNodeId', 'fields']);
    setFieldDataList([]);

    if (addType === DATA_SOURCE_TYPE.MAIN_TABLE) {
      getFieldList(curMainTableName, mainEntityList);
    }
    if (addType === DATA_SOURCE_TYPE.SUB_TABLE) {
      setSubEntityList([]);
      const mainEntityId = mainEntityList.find((item) => item.tableName === curMainTableName)?.entityId;
      if (!mainEntityId) {
        return;
      }

      const res = await getEntityFieldsWithChildren(mainEntityId);

      const newEntityList = (res.childEntities || []).map((item: any) => {
        return {
          entityId: item.childEntityId,
          tableName: item.childTableName,
          entityName: item.childEntityName
        };
      });

      setSubEntityList(newEntityList);
    }
  };
  // 子表数据变更
  const handleSubTableNameChange = (curSubTableName: string) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
    setFieldDataList([]);
    getFieldList(curSubTableName, subEntityList);
  };

  // 获取字段下拉列表
  const getFieldList = async (tableName: string, entityList: MetadataEntityPair[]) => {
    if (!tableName) {
      return;
    }
    const entityId = [...entityList].find((item) => item.tableName === tableName)?.entityId;

    if (!entityId) {
      return;
    }

    const res = await getEntityFields({ entityId: entityId });
    res.forEach((item: any) => {
      item.fieldKey = `${tableName}.${item.fieldName}`;
    });

    setFieldDataList(res);
  };

  const handleBatchTypeChange = (value: boolean) => {
    payloadForm.clearFields(['dataNodeId', 'fields']);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            requiredSymbol={{ position: 'end' }}
            layout="vertical"
          >
            <Grid.Row>
              <Form.Item
                label="节点ID"
                field="id"
                initialValue={node.id}
                rules={[
                  {
                    required: true
                  }
                ]}
              >
                <Input disabled />
              </Form.Item>
            </Grid.Row>

            <Grid.Row>
              <Form.Item label="新增方式" field="addType" rules={[{ required: true, message: '请选择新增方式' }]}>
                <RadioGroup onChange={handleDataTypeChange}>
                  <Radio value={DATA_SOURCE_TYPE.MAIN_TABLE}>在主表中新增</Radio>
                  <Radio value={DATA_SOURCE_TYPE.SUB_TABLE}>在子表中新增</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {/* 从主表中插入 */}
            {addType === DATA_SOURCE_TYPE.MAIN_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={19}>
                  <Form.Item field="mainTableName" disabled={!addType} rules={[{ required: true, message: '请选择' }]}>
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
                  <span>中插入数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            {/* 从子表中查询 */}
            {addType === DATA_SOURCE_TYPE.SUB_TABLE && (
              <Grid.Row>
                <Grid.Col span={1} style={{ textAlign: 'center', lineHeight: '32px' }}>
                  向
                </Grid.Col>
                <Grid.Col span={9}>
                  <Form.Item field="mainTableName" disabled={!addType} rules={[{ required: true, message: '请选择' }]}>
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
                  <span>中插入数据</span>
                </Grid.Col>
              </Grid.Row>
            )}

            <Grid.Row>
              <Form.Item label="新增数据" field="batchType" rules={[{ required: true, message: '请选择新增数据' }]}>
                <RadioGroup onChange={handleBatchTypeChange}>
                  <Radio value={false}>新增单条数据</Radio>
                  <Radio value={true}>新增多条数据</Radio>
                </RadioGroup>
              </Form.Item>
            </Grid.Row>

            {batchType ? (
              <>
                <Grid.Row>
                  <Form.Item label="数据源" field="dataNodeId">
                    <Select allowClear>
                      {dataNodeList.map((item) => (
                        <Select.Option key={item.id} value={item.id}>
                          {item.data.title}
                        </Select.Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Grid.Row>
                <Grid.Row>
                  <Form.Item label="字段设置">
                    <FieldEditor
                      nodeId={node.id}
                      fieldList={fieldDataList}
                      form={payloadForm}
                      dataNodeId={dataNodeId}
                    />
                  </Form.Item>
                </Grid.Row>
              </>
            ) : (
              <Grid.Row>
                <Form.Item label="字段设置">
                  <FieldEditor nodeId={node.id} fieldList={fieldDataList} form={payloadForm} />
                </Form.Item>
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

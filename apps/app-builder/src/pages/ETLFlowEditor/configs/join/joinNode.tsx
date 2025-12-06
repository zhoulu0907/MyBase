import { Checkbox, Form, Input, Layout, Table } from '@arco-design/web-react';
import useForm from '@arco-design/web-react/es/Form/useForm';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { cloneDeep } from 'lodash-es';
import React, { useEffect, useState } from 'react';
import DataPreview from '../../components/dataPreview';
import DataRemark from '../../components/dataRemark';
import { handlePreviewData, setNodeDataAndResetDownstream, type PreviewData } from '../utils';
import JoinRow from './components/joinRow';
import styles from './index.module.less';

type Row = {
  isSelected?: boolean;
  fieldType: string;
  // displayFieldName: string;            TODO
  // updatedDisplayFieldName: string;     TODO
  fieldFqn: string;
  fieldName: string;
  updatedFieldName: string;
  nodeId: string;
  nodeName: string;
};

type JoinNodeConfigProps = { onRegisterSave?: (fn: () => void) => void };

export const JoinNodeConfig: React.FC<JoinNodeConfigProps> = ({ onRegisterSave }) => {
  useSignals();

  const { curNode, curDrawerTab, nodeData, graphData } = etlEditorSignal;
  const [newPayload, setNewPayload] = useState<any>(cloneDeep(nodeData.value[curNode.value.id]));
  const [form] = useForm();
  const [finalNodeList, setFinalNodeList] = useState<any[]>([]);

  const [fieldListform] = useForm();
  const [rows, setRows] = useState<Row[]>([]);

  useEffect(() => {
    const finalNodeList = generateNodeList(curNode.value.id);
    setFinalNodeList(finalNodeList);
  }, []);

  useEffect(() => {
    onRegisterSave?.(handleSaveInner);
  }, [onRegisterSave]);

  const handleSaveInner = () => {
    setNodeDataAndResetDownstream(newPayload, curNode.value.id, graphData.value, nodeData.value);
  };

  useEffect(() => {
    if (curDrawerTab.value === ETLDrawerTab.FIELD_CONFIG) {
      const nodeListDetail = nodeData.value;
      const curNodeDetailConfig = newPayload?.config;
      const leftNodeId = curNodeDetailConfig?.leftNodeId;
      const rightNodeId = curNodeDetailConfig?.rightNodeId;
      let allFieldList = [];
      if (curNodeDetailConfig?.mappings?.length > 0) {
        allFieldList = curNodeDetailConfig?.mappings;
      } else if (leftNodeId && rightNodeId) {
        const leftFiledList = nodeListDetail[leftNodeId]?.output?.fields;
        const rightFiledList = nodeListDetail[rightNodeId]?.output?.fields;
        const finalLeftFiledList = leftFiledList?.map((field: any) => ({
          fieldType: field.fieldType,
          fieldFqn: field.fieldFqn,
          fieldName: field.fieldName,
          updatedFieldName: field.fieldName,
          nodeId: leftNodeId,
          nodeName: nodeListDetail[leftNodeId].title
        }));
        const finalRightFiledList = rightFiledList?.map((field: any) => ({
          fieldType: field.fieldType,
          fieldFqn: field.fieldFqn,
          fieldName: field.fieldName,
          updatedFieldName: field.fieldName,
          nodeId: rightNodeId,
          nodeName: nodeListDetail[rightNodeId].title
        }));
        allFieldList = finalLeftFiledList.concat(finalRightFiledList);
      }

      fieldListform.setFieldsValue({ mappings: allFieldList });
      setRows(allFieldList);

      setCurNodeData(allFieldList);
    }

    if (curDrawerTab.value == ETLDrawerTab.DATA_PREVIEW) {
      handlePreviewData(graphData.value, nodeData.value, curNode.value, setPreviewData);
    }
  }, [curDrawerTab.value]);

  const [previewData, setPreviewData] = useState<PreviewData>({
    columns: [],
    data: []
  });

  // 同步表单变化到本地 rows（用于 Table 渲染）
  const onValuesChange = (_: any, allValues: any) => {
    const rows = (allValues?.mappings as Row[]) ?? [];
    setRows(rows);
    const selectedRow = rows.filter((r) => r.isSelected);
    setCurNodeData(rows, selectedRow);
  };

  const setCurNodeData = (rows: Row[], selectedRow?: Row[]) => {
    const payload = newPayload;
    payload.config = {
      ...payload.config,
      mappings: rows
    };
    if (selectedRow && selectedRow.length > 0) {
      const fields = selectedRow.map((field) => ({
        fieldFqn: curNode.value.id + `.${field.updatedFieldName}`,
        fieldName: field.updatedFieldName,
        fieldType: field.fieldType
      }));
      payload.output = {
        verified: true,
        fields
      };
    } else {
      payload.output = {
        verified: false
      };
    }

    setNewPayload(payload);
  };

  const selectedRowKeys = rows.filter((r) => r.isSelected).map((r) => r.fieldFqn) as (string | number)[];
  const onSelectChange = (keys: (string | number)[], selectedRows: Row[]) => {
    const keySet = new Set(keys.map(String));
    const next = rows.map((r) => ({ ...r, isSelected: keySet.has(String(r.fieldFqn)) }));
    fieldListform.setFieldsValue({ mappings: next });
    setRows(next);
  };

  const rowSelection = {
    type: 'checkbox' as const,
    selectedRowKeys,
    onChange: onSelectChange
  };

  const generateNodeList = (targetNodeId: string) => {
    if (!graphData) return [];

    const { edges = [] } = graphData.value;
    const nodeDataList = nodeData.value;

    // 找出指向 targetNodeID 的所有 sourceNodeID，去重
    const sourceIds = Array.from(
      new Set(edges.filter((e: any) => e.targetNodeID === targetNodeId).map((e: any) => e.sourceNodeID))
    );

    // 从 nodeDataList 映射取 title
    return sourceIds.map((id) => {
      const node = nodeDataList[id];
      return { sourceNodeID: id, title: node.title };
    });
  };

  return (
    <Layout className={styles.joinDataConfigContainer}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <div>
          <div className={styles.joinHeader}>
            <span>添加连接字段</span>
            <Checkbox>合并连接字段</Checkbox>
          </div>

          <Form form={form} className={styles.content} initialValues={newPayload?.config}>
            <JoinRow finalNodeList={finalNodeList} form={form} payload={newPayload} setPayload={setNewPayload} />
            {/* <Form.List field="joinList">
              {(joinList, { add: addRow }) => {
                return (
                  <>
                    {joinList.map((item, index) => {
                      return <JoinRow key={index} row={item} finalNodeList={finalNodeList} form={form} />;
                    })}
                    <Grid.Row>
                  <Grid.Col span={18} className={styles.addConnectionBox}>
                    <Button
                      type="text"
                      onClick={() => {
                        addRow();
                      }}
                    >
                      + 添加连接
                    </Button>
                  </Grid.Col>
                </Grid.Row>
                  </>
                );
              }}
            </Form.List> */}
          </Form>
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.FIELD_CONFIG && (
        <div>
          <Form form={fieldListform} onValuesChange={onValuesChange}>
            <Form.List field="mappings">
              {(fields, {}) => {
                const columns = [
                  {
                    title: '类型',
                    dataIndex: 'fieldType',
                    width: 120,
                    render: (_: any, record: Row) => <div>{record.fieldType}</div>
                  },
                  // TODO
                  // {
                  //   title: '字段名称',
                  //   dataIndex: 'fieldName',
                  //   render: (_: any, record: Row) => <div>{record.fieldName}</div>
                  // },
                  {
                    title: '原字段名称',
                    dataIndex: 'updatedFieldName',
                    render: (_: any, record: Row, idx: number) => {
                      return (
                        <Form.Item noStyle field={`mappings.${idx}.updatedFieldName`}>
                          <Input size="mini" allowClear />
                        </Form.Item>
                      );
                    }
                  },
                  {
                    title: '来源节点',
                    dataIndex: 'nodeName',
                    render: (_: any, record: Row) => <div>{record.nodeName}</div>
                  }
                ];

                return (
                  <>
                    <Table
                      columns={columns}
                      data={rows}
                      rowKey={(r: Row) => r.fieldFqn}
                      pagination={false}
                      rowSelection={rowSelection}
                    />
                  </>
                );
              }}
            </Form.List>
          </Form>
        </div>
      )}
      {curDrawerTab.value === ETLDrawerTab.DATA_PREVIEW && (
        <DataPreview data={previewData.data} columns={previewData.columns} />
      )}
      {curDrawerTab.value === ETLDrawerTab.NODE_REMARK && <DataRemark />}
    </Layout>
  );
};

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Button, Input, Modal, Select, Switch, Table, Tabs, Message } from '@arco-design/web-react';
import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import React from 'react';
import TriggerEditor from '../../triggerEditor';
import styles from './index.module.less';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  useSignals();
  const { nodeData, nodes, flowId, invalidNodes, isInvalidNode } = triggerEditorSignal;
  const { getTriggerNodeOutput } = triggerNodeOutputSignal;
  const { flowInputs, flowOutputs, setFlowInputs, setFlowOutputs } = triggerEditorSignal;

  const [ioModalOpen, setIoModalOpen] = React.useState(false);
  const [draftInputs, setDraftInputs] = React.useState<any[]>([]);
  const [draftOutputs, setDraftOutputs] = React.useState<any[]>([]);

  const openIOModal = () => {
    setDraftInputs(Array.isArray(flowInputs.value) ? [...flowInputs.value] : []);
    setDraftOutputs(Array.isArray(flowOutputs.value) ? [...flowOutputs.value] : []);
    setIoModalOpen(true);
  };

  const applyIOModal = () => {
    setFlowInputs(draftInputs);
    setFlowOutputs(draftOutputs);
    setIoModalOpen(false);
  };

  const closeIOModal = () => {
    setIoModalOpen(false);
  };

  const FIELD_TYPE_OPTIONS = [
    { label: '字符串', value: 'string' },
    { label: '数字', value: 'number' },
    { label: '布尔', value: 'boolean' },
    { label: '对象', value: 'object' },
    { label: '数组', value: 'array' }
  ];

  const addDraftRow = (kind: 'input' | 'output') => {
    const row = {
      id: `row-${Date.now()}-${Math.random().toString(36).slice(2)}`,
      key: '',
      fieldName: '',
      fieldType: 'string',
      required: false,
      defaultValue: '',
      description: ''
    };
    if (kind === 'input') setDraftInputs((prev) => [...prev, row]);
    else setDraftOutputs((prev) => [...prev, row]);
  };

  const removeDraftRow = (kind: 'input' | 'output', index: number) => {
    if (kind === 'input') setDraftInputs((prev) => prev.filter((_, i) => i !== index));
    else setDraftOutputs((prev) => prev.filter((_, i) => i !== index));
  };

  const updateDraftRow = (kind: 'input' | 'output', index: number, patch: Record<string, unknown>) => {
    const updater = (prev: any[]) => {
      const next = [...prev];
      next[index] = { ...(next[index] || {}), ...patch };
      return next;
    };
    if (kind === 'input') setDraftInputs(updater);
    else setDraftOutputs(updater);
  };

  const dealProcessDefinition = (newNodes: any[]): any[] => {
    const processDefinitionJson = newNodes.map((item) => {
      const { outputs: nodeOutputs, initialData: nodeInitialData, ...restNodeData } = nodeData.value[item.id] || {};

      const output = getTriggerNodeOutput(item.id);
      if (item.blocks?.length) {
        const blocks = dealProcessDefinition(item.blocks);

        const data = {
          id: item.id,
          type: item.type,
          blocks,
          data: {
            ...restNodeData,
            // 覆写的属性写在后面
            title: item.data?.title
          },
          output: output
        };
        return data;
      } else {
        const data = {
          id: item.id,
          type: item.type,
          data: {
            ...restNodeData,
            // 覆写的属性写在后面
            title: item.data?.title
          },
          output: output
        };
        return data;
      }
    });
    return processDefinitionJson;
  };

  const handleSaveAndRelease = async () => {
    // 表单校验结果验证
    console.log('invalidNodes.value', invalidNodes.value);
    console.log('invalidNodes.value', nodes.value);
    const nodesValidate = Object.entries(invalidNodes.value).every(([key, value]) => {
      if (!key || key === 'undefined') {
        return true;
      }
      if (nodes.value.find((ele) => ele.id === key)) {
        return !value;
      }
      return true;
    });
    if (!nodesValidate) {
      Message.warning('存在未配置完成的节点');
      return;
    }
    const processDefinitionJson = dealProcessDefinition(nodes.value);
    console.log('processDefinition', processDefinitionJson);

    const params = {
      id: flowId.value || '',
      processDefinition: JSON.stringify({
        nodes: processDefinitionJson,
        ioConfig: {
          inputs: flowInputs.value || [],
          outputs: flowOutputs.value || []
        }
      }),
      enableStatus: ProcessStatus.ORIGINAL
    };

    console.log('params', params);

    const res = await updateFlowMgmtDefinition(params);
    if (res) {
      Message.success(`保存成功`);
    }
  };

  return (
    <div className={styles.flowEditorPage}>
      <div className={styles.header}>
        <Button type="primary" onClick={handleSaveAndRelease}>
          保存
        </Button>
        <Button style={{ marginLeft: 12 }} onClick={openIOModal}>
          输入输出设置
        </Button>
      </div>
      <div className={styles.body}>
        <TriggerEditor />
      </div>
      <Modal
        title="逻辑流输入输出设置"
        visible={ioModalOpen}
        onCancel={closeIOModal}
        onOk={applyIOModal}
        style={{ width: 920 }}
      >
        <Tabs defaultActiveTab="inputs" type="card-gutter">
          <Tabs.TabPane key="inputs" title="输入">
            <div style={{ marginBottom: 8 }}>
              <Button type="dashed" onClick={() => addDraftRow('input')}>
                添加一行
              </Button>
            </div>
            <Table
              data={draftInputs}
              pagination={false}
              size="small"
              rowKey={(record: any) => record.id ?? `row-${record.key}-${record.fieldName}`}
              scroll={{ x: 900 }}
              columns={[
                {
                  title: '字段 Key',
                  dataIndex: 'key',
                  width: 140,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.key}
                      onChange={(v) => updateDraftRow('input', index, { key: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '字段名称',
                  dataIndex: 'fieldName',
                  width: 140,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.fieldName}
                      onChange={(v) => updateDraftRow('input', index, { fieldName: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '字段类型',
                  dataIndex: 'fieldType',
                  width: 120,
                  render: (_: unknown, row: any, index: number) => (
                    <Select
                      value={row.fieldType}
                      options={FIELD_TYPE_OPTIONS}
                      onChange={(v) => updateDraftRow('input', index, { fieldType: v })}
                      style={{ width: '100%' }}
                    />
                  )
                },
                {
                  title: '必填',
                  dataIndex: 'required',
                  width: 80,
                  render: (_: unknown, row: any, index: number) => (
                    <Switch
                      checked={Boolean(row.required)}
                      onChange={(v) => updateDraftRow('input', index, { required: v })}
                    />
                  )
                },
                {
                  title: '默认值',
                  dataIndex: 'defaultValue',
                  width: 140,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.defaultValue}
                      onChange={(v) => updateDraftRow('input', index, { defaultValue: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '字段描述',
                  dataIndex: 'description',
                  ellipsis: true,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.description}
                      onChange={(v) => updateDraftRow('input', index, { description: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '操作',
                  dataIndex: '_op',
                  width: 80,
                  fixed: 'right' as const,
                  render: (_: unknown, __: any, index: number) => (
                    <Button type="text" status="danger" onClick={() => removeDraftRow('input', index)}>
                      删除
                    </Button>
                  )
                }
              ]}
            />
          </Tabs.TabPane>
          <Tabs.TabPane key="outputs" title="输出">
            <div style={{ marginBottom: 8 }}>
              <Button type="dashed" onClick={() => addDraftRow('output')}>
                添加一行
              </Button>
            </div>
            <Table
              data={draftOutputs}
              pagination={false}
              size="small"
              rowKey={(record: any) => record.id ?? `row-${record.key}-${record.fieldName}`}
              scroll={{ x: 900 }}
              columns={[
                {
                  title: '字段 Key',
                  dataIndex: 'key',
                  width: 140,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.key}
                      onChange={(v) => updateDraftRow('output', index, { key: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '字段名称',
                  dataIndex: 'fieldName',
                  width: 140,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.fieldName}
                      onChange={(v) => updateDraftRow('output', index, { fieldName: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '字段类型',
                  dataIndex: 'fieldType',
                  width: 120,
                  render: (_: unknown, row: any, index: number) => (
                    <Select
                      value={row.fieldType}
                      options={FIELD_TYPE_OPTIONS}
                      onChange={(v) => updateDraftRow('output', index, { fieldType: v })}
                      style={{ width: '100%' }}
                    />
                  )
                },
                {
                  title: '字段描述',
                  dataIndex: 'description',
                  ellipsis: true,
                  render: (_: unknown, row: any, index: number) => (
                    <Input
                      value={row.description}
                      onChange={(v) => updateDraftRow('output', index, { description: v })}
                      allowClear
                    />
                  )
                },
                {
                  title: '操作',
                  dataIndex: '_op',
                  width: 80,
                  fixed: 'right' as const,
                  render: (_: unknown, __: any, index: number) => (
                    <Button type="text" status="danger" onClick={() => removeDraftRow('output', index)}>
                      删除
                    </Button>
                  )
                }
              ]}
            />
          </Tabs.TabPane>
        </Tabs>
      </Modal>
    </div>
  );
};

export default FlowEditorPage;

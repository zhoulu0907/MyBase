import TablePagination from '@/components/TablePagination';
import ResizableTable from '@/components/ResizableTable';
import {
  Button,
  Input,
  Message,
  Modal,
  Popconfirm,
  Select,
  Space,
  Spin,
  Table,
  Upload,
  type TableColumnProps
} from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import {
  ConnectorActionStatusText,
  ConnectorActionStatus,
  deleteConnectorAction,
  listConnectorActionsByUuid,
  createConnectorAction,
  deleteScriptAction,
  listScriptAction,
  type ConnectorActionDO,
  type ListScriptActionReq,
  type ScriptActionItem
} from '@onebase/app';
import { getCommonPaginationList, getHashQueryParam } from '@onebase/common';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';

import CreateHTTPActionPage from '../createHTTP';
import CreateScriptActionPage from '../createJS';
import {
  buildActionNameFromOpenApi,
  buildHttpActionConfigFromOpenApi,
  getOperationKey,
  isRecord,
  parseOpenApiOperations,
  type OpenApiOperation
} from '../openapi';
import styles from './index.module.less';

/**
 * 生成动作编码：ACTION_ + 8位，英文大写开头，包含英文大写和数字
 */
const generateActionCode = (): string => {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  // 第一位必须是英文大写
  let code = letters.charAt(Math.floor(Math.random() * letters.length));
  // 后面7位是英文大写或数字
  for (let i = 0; i < 7; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return `ACTION_${code}`;
};

/**
 * 连接器动作列表页面
 */
interface ScriptActionListPageProps {
  isScript?: boolean;
}

const ScriptActionListPage: React.FC<ScriptActionListPageProps> = ({ isScript = false }) => {
  const [searchActionName, setSearchActionName] = useState('');
  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);

  const [loading, setLoading] = useState(false);
  const [actionList, setActionList] = useState<(ScriptActionItem | ConnectorActionDO)[]>();
  const [isCreate, setIsCreate] = useState(false);
  const [editingScriptId, setEditingScriptId] = useState<string | null>(null);
  const [openApiCreateModalOpen, setOpenApiCreateModalOpen] = useState(false);
  const [openApiBatchCreateModalOpen, setOpenApiBatchCreateModalOpen] = useState(false);
  const [openApiRaw, setOpenApiRaw] = useState('');
  const [openApiOps, setOpenApiOps] = useState<OpenApiOperation[]>([]);
  const [openApiOpKey, setOpenApiOpKey] = useState<string>('');
  const [openApiSelectedKeys, setOpenApiSelectedKeys] = useState<string[]>([]);
  const [openApiSingleKey, setOpenApiSingleKey] = useState<string>('');
  const [openApiParseError, setOpenApiParseError] = useState<string>('');
  const [openApiCreating, setOpenApiCreating] = useState(false);
  const [openApiBatchCreating, setOpenApiBatchCreating] = useState(false);
  const [openApiBatchStatus, setOpenApiBatchStatus] = useState('');
  const [openApiImport, setOpenApiImport] = useState<{ token: number; raw: string; opKey?: string }>();

  useEffect(() => {
    handleGetScriptActionList();
  }, [isCreate, editingScriptId]);

  useEffect(() => {
    if (pageSize) {
      handleGetScriptActionList(searchActionName);
    }
  }, [pageNo, pageSize, searchActionName]);

  const debouncedSearch = useCallback(
    debounce((actionName: string) => {
      handleGetScriptActionList(actionName);
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  const handleGetScriptActionList = async (actionName?: string) => {
    setLoading(true);

    const id = getHashQueryParam('id');

    if (id) {
      if (isScript) {
        const req: ListScriptActionReq = {
          connectorId: id,
          pageNo: pageNo,
          pageSize: pageSize,
          scriptName: actionName || undefined
        };
        const res = await getCommonPaginationList(
          (param: unknown) => listScriptAction(param as ListScriptActionReq),
          req,
          setPageNo
        );
        if (res) {
          const list = (res.list || []).map((item: ScriptActionItem, index: number) => ({
            ...item,
            _rowKey: item.id ?? item.scriptName ?? `row-${index}`
          }));
          setActionList(list);
          setTotal(res.total || list.length);
          setLoading(false);
        }
      } else {
        // 使用新的统一动作表 API 查询 HTTP 动作列表
        const res = await listConnectorActionsByUuid(id);
        if (res && Array.isArray(res)) {
          const list = res.map((item: ConnectorActionDO, index: number) => ({
            ...item,
            _rowKey: item.id ?? item.actionCode ?? `row-${index}`,
            actionCode: item.actionCode,
            actionName: item.actionName,
            description: item.description,
            updateTime: item.updateTime,
            status: item.activeStatus === 1 ? ConnectorActionStatus.Published : ConnectorActionStatus.Unpublished
          }));
          setActionList(list);
          setTotal(list.length);
          setLoading(false);
        }
      }
    }
  };

  const handleDeleteScript = async (scriptId: string) => {
    try {
      const res = await deleteScriptAction(scriptId);
      if (res) {
        Message.success('删除成功');
        handleGetScriptActionList(searchActionName);
      } else {
        Message.error('删除失败');
      }
    } catch (error) {
      Message.error('删除失败，请稍后重试');
      console.error('删除动作失败:', error);
    }
  };

  const handleDeleteHttp = async (actionId: number) => {
    try {
      const res = await deleteConnectorAction(actionId);
      if (res) {
        Message.success('删除成功');
        handleGetScriptActionList(searchActionName);
      } else {
        Message.error('删除失败');
      }
    } catch (error) {
      Message.error('删除失败，请稍后重试');
      console.error('删除动作失败:', error);
    }
  };

  const handleEdit = (record: ConnectorActionDO) => {
    // 使用 actionCode 作为编辑标识（getConnectorActionByCode 需要 actionCode）
    const actionCode = record.actionCode;
    if (actionCode) {
      setEditingScriptId(actionCode);
    } else {
      Message.warning('无法获取动作标识，请稍后重试');
    }
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      width: 80,
      render: (_: unknown, __: unknown, index: number) => (pageNo - 1) * pageSize + index + 1
    },
    {
      title: '动作名称',
      dataIndex: isScript ? 'scriptName' : 'actionName',
      width: 200
    },
    ...(!isScript ? [{
      title: '动作编码',
      dataIndex: 'actionCode',
      width: 180,
      render: (actionCode: string) => <span style={{ fontFamily: 'monospace', color: '#86909c' }}>{actionCode || '-'}</span>
    } as TableColumnProps] : []),
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true
    },
    {
      title: '创建时间',
      dataIndex: 'updateTime',
      width: 180,
      render: (createTime: string | number) => {
        return <span>{dayjs(createTime).format('YYYY-MM-DD HH:mm:ss')}</span>;
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status: ConnectorActionStatus) => <span>{ConnectorActionStatusText[status] ?? '-'}</span>
    },
    {
      title: <div style={{ textAlign: 'center', width: '90%' }}>操作</div>,
      dataIndex: 'operation',
      width: 150,
      fixed: 'right',
      render: (_: unknown, record: ScriptActionItem | ConnectorActionDO) => (
        <Space>
          <Button
            type="text"
            size="mini"
            onClick={(e) => {
              e.stopPropagation();
              handleEdit(record as ConnectorActionDO);
            }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除吗？"
            content="删除后不可恢复"
            onOk={() => {
              if (isScript) {
                handleDeleteScript((record as ScriptActionItem).id);
                return;
              }
              // 使用新的 API 删除（需要动作 ID）
              const actionId = (record as ConnectorActionDO).id;
              if (!actionId) {
                Message.warning('无法获取动作标识，请稍后重试');
                return;
              }
              handleDeleteHttp(actionId);
            }}
          >
            <Button type="text" size="mini" status="danger">
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const parseOpenApiText = (raw: string) => {
    const parsed = parseOpenApiOperations(raw);
    setOpenApiOps(parsed.operations);
    const firstKey = parsed.operations.length > 0 ? getOperationKey(parsed.operations[0]) : '';
    setOpenApiOpKey(firstKey);
    setOpenApiSingleKey(firstKey);
    setOpenApiSelectedKeys(parsed.operations.map((o) => getOperationKey(o)));
    setOpenApiParseError(parsed.error ?? '');
  };

  const handleOpenApiFileChange = (files: unknown) => {
    const list = Array.isArray(files) ? files : [];
    const first = list[0];
    if (!isRecord(first)) return;
    const originFile = first.originFile;
    if (!(originFile instanceof File)) return;
    originFile
      .text()
      .then((text) => {
        setOpenApiRaw(text);
        parseOpenApiText(text);
      })
      .catch(() => {
        setOpenApiParseError('读取文件失败');
      });
  };

  const handleOpenApiCreate = async () => {
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    let doc: unknown;
    try {
      doc = JSON.parse(openApiRaw || '{}') as unknown;
    } catch {
      setOpenApiParseError('OpenAPI 内容不是合法 JSON');
      return;
    }
    const selected = openApiOps.find((o) => getOperationKey(o) === openApiOpKey);
    if (!selected) {
      Message.error('请选择要创建的接口');
      return;
    }

    setOpenApiCreating(true);
    try {
      const built = buildHttpActionConfigFromOpenApi(doc, selected);
      const actionName = built.actionName;
      const actionConfig = built.actionConfig;
      await createConnectorAction({
        connectorUuid: connectorId,
        connectorType: 'HTTP',
        actionCode: generateActionCode(),
        actionName,
        actionConfig: JSON.stringify(actionConfig),
        activeStatus: 1
      });
      Message.success('创建成功');
      setOpenApiCreateModalOpen(false);
      handleGetScriptActionList(searchActionName);
    } catch (e) {
      Message.error((e as Error)?.message ?? '创建失败');
    } finally {
      setOpenApiCreating(false);
    }
  };

  const handleOpenApiBatchCreate = async () => {
    const connectorId = getHashQueryParam('id');
    if (!connectorId) {
      Message.error('缺少连接器 ID');
      return;
    }
    if (!openApiSelectedKeys.length) {
      Message.error('请先选择要创建的接口');
      return;
    }
    let doc: unknown;
    try {
      doc = JSON.parse(openApiRaw || '{}') as unknown;
    } catch {
      setOpenApiParseError('OpenAPI 内容不是合法 JSON');
      return;
    }

    const opByKey = new Map<string, OpenApiOperation>();
    openApiOps.forEach((o) => {
      opByKey.set(getOperationKey(o), o);
    });
    const selectedOps = openApiSelectedKeys.map((k) => opByKey.get(k)).filter(Boolean) as OpenApiOperation[];
    if (selectedOps.length === 0) {
      Message.error('请先选择要创建的接口');
      return;
    }

    const usedNames = new Set<string>();
    const genUniqueName = (base: string) => {
      let name = base;
      let i = 1;
      while (usedNames.has(name)) {
        const suffix = `_${i}`;
        name = `${base.slice(0, Math.max(0, 128 - suffix.length))}${suffix}`;
        i += 1;
      }
      usedNames.add(name);
      return name;
    };

    setOpenApiBatchCreating(true);
    setOpenApiBatchStatus('');
    try {
      let ok = 0;
      const failed: Array<{ key: string; reason: string }> = [];
      for (let i = 0; i < selectedOps.length; i += 1) {
        const op = selectedOps[i];
        const key = `${op.method.toUpperCase()} ${op.path}`;
        setOpenApiBatchStatus(`正在创建 ${i + 1}/${selectedOps.length}: ${key}`);
        try {
          const actionName = genUniqueName(buildActionNameFromOpenApi(op));
          const actionConfig = buildHttpActionConfigFromOpenApi(doc, op, actionName).actionConfig;
          await createConnectorAction({
            connectorUuid: connectorId,
            connectorType: 'HTTP',
            actionCode: generateActionCode(),
            actionName,
            actionConfig: JSON.stringify(actionConfig),
            activeStatus: 1
          });
          ok += 1;
        } catch (e) {
          failed.push({ key, reason: (e as Error)?.message ?? '创建失败' });
        }
      }

      if (failed.length === 0) {
        Message.success(`已创建 ${ok} 个动作`);
        setOpenApiBatchCreateModalOpen(false);
        handleGetScriptActionList(searchActionName);
      } else {
        Message.warning(`已创建 ${ok} 个动作，失败 ${failed.length} 个`);
        setOpenApiParseError(
          failed
            .slice(0, 3)
            .map((f) => `${f.key}: ${f.reason}`)
            .join('\n')
        );
      }
    } finally {
      setOpenApiBatchCreating(false);
      setOpenApiBatchStatus('');
    }
  };

  return (
    <div className={styles.scriptActionListPage}>
      <div className={styles.title}>动作配置</div>
      {isCreate || editingScriptId ? (
        isScript ? (
          <CreateScriptActionPage
            editData={editingScriptId ? ({ id: editingScriptId } as unknown as ScriptActionItem) : undefined}
            onSuccess={() => {
              setIsCreate(false);
              setEditingScriptId(null);
            }}
          />
        ) : (
          <CreateHTTPActionPage
            editActionName={editingScriptId ?? undefined}
            onSuccess={() => {
              setIsCreate(false);
              setEditingScriptId(null);
              setOpenApiImport(undefined);
            }}
            openApiImport={openApiImport}
          />
        )
      ) : (
        <>
          <div className={styles.header}>
            <div className={styles.headerLeft}>
              <Button type="primary" icon={<IconPlus />} onClick={() => setIsCreate(true)}>
                创建动作
              </Button>
              {!isScript ? (
                <Button
                  type="outline"
                  onClick={() => {
                    setOpenApiBatchCreateModalOpen(true);
                  }}
                >
                  OpenAPI 批量创建
                </Button>
              ) : null}
            </div>
            <div className={styles.headerRight}>
              <Input.Search
                allowClear
                placeholder="请输入动作名称"
                style={{ width: 240 }}
                onChange={(value) => {
                  setSearchActionName(value);
                }}
              />
            </div>
          </div>

          <div className={styles.content}>
            <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
              <div className={styles.tableContainer}>
                <ResizableTable
                  rowKey="_rowKey"
                  columns={columns}
                  data={actionList || []}
                  pagination={false}
                  loading={loading}
                />
              </div>
            </Spin>
          </div>
          <div className={styles.footer}>
            <TablePagination
              className={styles.myAppPagination}
              total={total}
              current={pageNo}
              pageSize={pageSize}
              onChange={(pNo) => {
                setPageNo(pNo);
              }}
              onPageSizeChange={(pSize) => {
                setPageSize(pSize);
              }}
            />
          </div>
        </>
      )}

      <Modal
        title="OpenAPI 批量创建"
        visible={openApiBatchCreateModalOpen}
        onCancel={() => setOpenApiBatchCreateModalOpen(false)}
        footer={null}
        style={{ width: 860 }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size={12}>
          <Space>
            <Upload
              accept=".json,application/json"
              autoUpload={false}
              showUploadList={false}
              onChange={handleOpenApiFileChange}
            >
              <Button>上传 JSON 文件</Button>
            </Upload>
            <Button onClick={() => parseOpenApiText(openApiRaw)}>解析</Button>
          </Space>
          <Input.TextArea
            value={openApiRaw}
            onChange={(v) => setOpenApiRaw(v)}
            placeholder="粘贴 OpenAPI JSON（v3）"
            autoSize={{ minRows: 10, maxRows: 16 }}
          />
          <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <Select
              mode="multiple"
              style={{ flex: 1 }}
              placeholder="选择多个接口"
              value={openApiSelectedKeys}
              onChange={(v) => setOpenApiSelectedKeys(v)}
              options={openApiOps.map((o) => {
                const k = `${o.method.toUpperCase()} ${o.path}`;
                return { label: `${k}${o.summary ? ` - ${o.summary}` : ''}`, value: k };
              })}
            />
            <Button
              type="primary"
              loading={openApiBatchCreating}
              disabled={openApiSelectedKeys.length === 0}
              onClick={handleOpenApiBatchCreate}
            >
              一键创建
            </Button>
          </div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <Select
              style={{ flex: 1 }}
              placeholder="请选择一个接口"
              value={openApiSingleKey || undefined}
              onChange={(v) => setOpenApiSingleKey(v)}
              options={openApiOps.map((o) => {
                const k = `${o.method.toUpperCase()} ${o.path}`;
                return { label: `${k}${o.summary ? ` - ${o.summary}` : ''}`, value: k };
              })}
            />
            <Button
              type="primary"
              onClick={async () => {
                const connectorId = getHashQueryParam('id');
                if (!connectorId) {
                  Message.error('缺少连接器 ID');
                  return;
                }
                if (!openApiSingleKey) {
                  Message.warning('请先选择一个接口');
                  return;
                }
                let doc: unknown;
                try {
                  doc = JSON.parse(openApiRaw || '{}') as unknown;
                } catch {
                  setOpenApiParseError('OpenAPI 内容不是合法 JSON');
                  return;
                }
                const op = openApiOps.find((o) => `${o.method.toUpperCase()} ${o.path}` === openApiSingleKey);
                if (!op) {
                  Message.warning('未找到选中的接口');
                  return;
                }
                try {
                  const actionName = buildActionNameFromOpenApi(op);
                  const actionConfig = buildHttpActionConfigFromOpenApi(doc, op, actionName).actionConfig;
                  const actionCode = generateActionCode();
                  await createConnectorAction({
                    connectorUuid: connectorId,
                    connectorType: 'HTTP',
                    actionCode,
                    actionName,
                    actionConfig: JSON.stringify(actionConfig),
                    activeStatus: 1
                  });
                  Message.success('创建成功');
                  setOpenApiBatchCreateModalOpen(false);
                  setEditingScriptId(actionCode);
                  setIsCreate(true);
                } catch (e) {
                  Message.error((e as Error)?.message ?? '创建失败');
                }
              }}
            >
              创建并进入详情
            </Button>
          </div>
          {openApiBatchStatus ? <div style={{ color: 'var(--color-text-2)' }}>{openApiBatchStatus}</div> : null}
          {openApiParseError ? <div style={{ color: 'var(--color-danger-6)' }}>{openApiParseError}</div> : null}
        </Space>
      </Modal>

      <Modal
        title="OpenAPI 导入创建"
        visible={openApiCreateModalOpen}
        onCancel={() => setOpenApiCreateModalOpen(false)}
        footer={null}
        style={{ width: 860 }}
      >
        <Space direction="vertical" style={{ width: '100%' }} size={12}>
          <Space>
            <Upload
              accept=".json,application/json"
              autoUpload={false}
              showUploadList={false}
              onChange={handleOpenApiFileChange}
            >
              <Button>上传 JSON 文件</Button>
            </Upload>
            <Button onClick={() => parseOpenApiText(openApiRaw)}>解析</Button>
          </Space>
          <Input.TextArea
            value={openApiRaw}
            onChange={(v) => setOpenApiRaw(v)}
            placeholder="粘贴 OpenAPI JSON（v3）"
            autoSize={{ minRows: 10, maxRows: 16 }}
          />
          <Space>
            <Select
              style={{ width: 520 }}
              placeholder="请选择要创建的接口"
              value={openApiOpKey || undefined}
              onChange={(v) => {
                setOpenApiOpKey(v);
              }}
              options={openApiOps.map((o) => {
                const k = `${o.method.toUpperCase()} ${o.path}`;
                return {
                  label: `${k}${o.summary ? ` - ${o.summary}` : ''}`,
                  value: k
                };
              })}
            />
            <Button type="primary" loading={openApiCreating} disabled={!openApiOpKey} onClick={handleOpenApiCreate}>
              立即创建
            </Button>
            <Button
              type="outline"
              onClick={() => {
                setOpenApiCreateModalOpen(false);
                setOpenApiImport({ token: Date.now(), raw: openApiRaw, opKey: openApiOpKey });
                setEditingScriptId(null);
                setIsCreate(true);
              }}
            >
              导入到表单
            </Button>
          </Space>
          {openApiParseError ? <div style={{ color: 'var(--color-danger-6)' }}>{openApiParseError}</div> : null}
        </Space>
      </Modal>
    </div>
  );
};

export default ScriptActionListPage;

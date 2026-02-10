import {
  Button,
  Input,
  Message,
  Pagination,
  Popconfirm,
  Space,
  Spin,
  Table,
  type TableColumnProps
} from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import {
  ConnectorActionStatusText,
  deleteHTTPAction,
  listConnectorActionInfos,
  type ConnectorActionStatus,
  type ListConnectorActionReq,
  type ScriptActionItem
} from '@onebase/app';
import { getCommonPaginationList, getHashQueryParam } from '@onebase/common';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';

import CreateHTTPActionPage from '../createHTTP';
import styles from './index.module.less';

/**
 * 连接器动作列表页面
 */
const ScriptActionListPage: React.FC = () => {
  const [searchActionName, setSearchActionName] = useState('');
  const [pageSize, setPageSize] = useState<number>(8);
  const [pageNo, setPageNo] = useState(1);
  const [total, setTotal] = useState(0);

  const [loading, setLoading] = useState(false);
  const [actionList, setActionList] = useState<ScriptActionItem[]>();
  const [isCreate, setIsCreate] = useState(false);
  const [editingScriptId, setEditingScriptId] = useState<string | null>(null);

  useEffect(() => {
    handleGetScriptActionList();
  }, [isCreate, editingScriptId]);

  useEffect(() => {
    pageSize && handleGetScriptActionList(searchActionName);
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
      const req: ListConnectorActionReq = {
        id: id,
        pageNo: pageNo,
        pageSize: pageSize
      };

      const res = await getCommonPaginationList(
        (param: any) => listConnectorActionInfos(param as ListConnectorActionReq),
        req,
        setPageNo
      );

      if (res) {
        type RowItem = ScriptActionItem & { actionName?: string };
        const list = (res || []).map((item: RowItem, index: number) => ({
          ...item,
          _rowKey: item.id ?? item.actionName ?? item.scriptName ?? `row-${index}`
        }));
        setActionList(list);
        setTotal(list.length);
        setLoading(false);
      }
    }
  };

  //   const handleDelete = async (scriptId: string) => {
  //     try {
  //       const res = await deleteScriptAction(scriptId);
  //       if (res) {
  //         Message.success('删除成功');
  //         handleGetScriptActionList(searchActionName);
  //       } else {
  //         Message.error('删除失败');
  //       }
  //     } catch (error) {
  //       Message.error('删除失败，请稍后重试');
  //       console.error('删除动作失败:', error);
  //     }
  //   };

  const handleDelete = async (connectorId: string, actionName: string) => {
    try {
      const res = await deleteHTTPAction(connectorId, actionName);
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

  const handleEdit = (record: ScriptActionItem & { actionName?: string }) => {
    // 列表接口可能返回 id 或 actionCode，getConnectorActionInfo 需要 actionCode
    console.log(record);
    const actionName = record.id ?? record.actionName;
    if (actionName) {
      setEditingScriptId(actionName);
    } else {
      Message.warning('无法获取动作标识，请稍后重试');
    }
  };

  const columns: TableColumnProps[] = [
    {
      title: '序号',
      dataIndex: 'index',
      width: 80,
      render: (_: any, __: any, index: number) => (pageNo - 1) * pageSize + index + 1
    },
    {
      title: '动作名称',
      dataIndex: 'actionName',
      width: 200
    },
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true
    },
    {
      title: '创建时间',
      dataIndex: 'updateTime',
      width: 180,
      render: (createTime: number) => {
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
      render: (_: any, record: any) => (
        <Space>
          <Button
            type="text"
            size="mini"
            onClick={(e) => {
              e.stopPropagation();
              handleEdit(record);
            }}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除吗？"
            content="删除后不可恢复"
            onOk={() => handleDelete(getHashQueryParam('id') || '', record.actionName)}
          >
            <Button type="text" size="mini" status="danger">
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div className={styles.scriptActionListPage}>
      <div className={styles.title}>动作配置</div>
      {isCreate || editingScriptId ? (
        <CreateHTTPActionPage
          editActionName={editingScriptId ?? undefined}
          onSuccess={() => {
            setIsCreate(false);
            setEditingScriptId(null);
          }}
        />
      ) : (
        <>
          <div className={styles.header}>
            <Button type="primary" icon={<IconPlus />} onClick={() => setIsCreate(true)}>
              创建动作
            </Button>
            <Input.Search
              allowClear
              placeholder="请输入动作名称"
              style={{ width: 240 }}
              onChange={(value) => {
                setSearchActionName(value);
              }}
            />
          </div>

          <div className={styles.content}>
            <Spin loading={loading} size={40} style={{ width: '100%', height: '100%' }} tip="加载中...">
              <div className={styles.tableContainer}>
                <Table
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
            <Pagination
              className={styles.myAppPagination}
              total={total}
              current={pageNo}
              pageSize={pageSize}
              onChange={(pNo, pSize) => {
                setPageNo(pNo);
                setPageSize(pSize);
              }}
            />
          </div>
        </>
      )}
    </div>
  );
};

export default ScriptActionListPage;

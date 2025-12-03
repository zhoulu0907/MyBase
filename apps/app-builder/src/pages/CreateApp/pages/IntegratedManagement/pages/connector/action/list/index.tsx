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
import { deleteScriptAction, listScriptAction, type ListScriptActionReq, type ScriptActionItem } from '@onebase/app';
import { getCommonPaginationList, getHashQueryParam } from '@onebase/common';
import dayjs from 'dayjs';
import { debounce } from 'lodash-es';
import React, { useCallback, useEffect, useState } from 'react';
import CreateScriptActionPage from '../create';
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
      const req: ListScriptActionReq = {
        pageNo: pageNo,
        pageSize: pageSize,
        connectorId: id,
        scriptName: actionName
      };

      const res = await getCommonPaginationList(
        (param) => listScriptAction(param as ListScriptActionReq),
        req,
        setPageNo
      );

      console.log('res :', res);
      if (res) {
        setActionList(res.list || []);
        setTotal(res.total || 0);
        setLoading(false);
      }
    }
  };

  const handleDelete = async (scriptId: string) => {
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

  const handleEdit = (record: ScriptActionItem) => {
    setEditingScriptId(record.id);
  };

  const getEditingData = () => {
    if (!editingScriptId || !actionList) return undefined;
    return actionList.find((item) => item.id === editingScriptId);
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
      dataIndex: 'scriptName',
      width: 200
    },
    {
      title: '描述',
      dataIndex: 'description',
      ellipsis: true
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      render: (createTime: number) => {
        return <span>{dayjs(createTime).format('YYYY-MM-DD HH:mm:ss')}</span>;
      }
    },
    {
      title: <div style={{ textAlign: 'center', width: '90%' }}>操作</div>,
      dataIndex: 'operation',
      width: 150,
      fixed: 'right',
      render: (_: any, record: ScriptActionItem) => (
        <Space>
          <Button type="text" size="mini" onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm title="确定删除吗？" content="删除后不可恢复" onOk={() => handleDelete(record.id)}>
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
        <CreateScriptActionPage
          editData={getEditingData()}
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
                <Table rowKey="id" columns={columns} data={actionList || []} pagination={false} loading={loading} />
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

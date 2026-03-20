import TablePagination from '@/components/TablePagination';
import { Button, Input, Message, Select, Spin } from '@arco-design/web-react';
import { IconPlus } from '@arco-design/web-react/icon';
import { deleteConnectInstance, listAllConnectInstance, type ConnectInstance } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import ConnectInstanceCard from '../../../components/ConnectInstanceCard';
import styles from './index.module.less';

/**
 * 流程管理页面
 * 目前集成触发器编辑器作为主内容
 */
const ConnectorInstancesPage: React.FC = () => {
  const navigate = useNavigate();
  const { tenantId } = useParams();

  const [loading, setLoading] = useState(false);

  // 连接器实例列表
  const [instanceList, setInstanceList] = useState<ConnectInstance[]>([]);
  const [total, setTotal] = useState(0);

  // 分页状态
  const [pageNo, setPageNo] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');

  useEffect(() => {
    getConnectInstanceList();
  }, [pageNo, pageSize, keyword]);

  const getConnectInstanceList = async () => {
    setLoading(true);

    try {
      // 调用 /flow/connector/list-all 接口，传递分页参数
      const res = await listAllConnectInstance({ pageNo, pageSize, connectorName: keyword });

      if (res) {
        if (Array.isArray(res)) {
          // 后端直接返回数组，需要在前端分页
          const start = (pageNo - 1) * pageSize;
          const end = start + pageSize;
          const paginatedList = res.slice(start, end);
          setInstanceList(paginatedList);
          setTotal(res.length);
        } else if (res.list && Array.isArray(res.list)) {
          // 后端返回分页数据
          setInstanceList(res.list);
          setTotal(res.total || 0);
        } else {
          setInstanceList([]);
          setTotal(0);
        }
      }
    } catch (error) {
      console.error('获取连接器实例列表失败:', error);
      // 显示具体的错误信息
      if (error instanceof Error) {
        Message.error(error.message || '获取连接器实例列表失败');
      }
    } finally {
      setLoading(false);
    }
  };

  const getAppId = () => {
    const curAppId = getHashQueryParam('appId');
    if (!curAppId) {
      Message.error('应用ID获取失败');
      return null;
    }
    return curAppId;
  };

  const handleCreateInstance = () => {
    const curAppId = getAppId();
    if (!curAppId) return;

    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector?appId=${curAppId}&mode=select`);
  };

  const handleEdit = (id: string) => {
    const curAppId = getAppId();
    if (!curAppId) return;

    navigate(`/onebase/${tenantId}/home/create-app/integrated-management/connector-detail?appId=${curAppId}&id=${id}`);
  };

  const handleDelete = async (id: string) => {
    const res = await deleteConnectInstance(id);
    if (res) {
      Message.success('删除成功');
      getConnectInstanceList();
    }
  };

  return (
    <div className={styles.connectorInstancesPage}>
      <div className={styles.header}>
        <div className={styles.title}>连接器实例</div>

        <div
          className={styles.searchContainer}
          style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}
        >
          <div style={{ display: 'flex', gap: 8 }}>
            <Select placeholder="全部类型" style={{ width: 120 }} allowClear>
              {/* Options will be populated possibly from API */}
            </Select>
            <Select placeholder="全部状态" style={{ width: 120 }} allowClear>
              <Select.Option value="enabled">已启用</Select.Option>
              <Select.Option value="disabled">已禁用</Select.Option>
            </Select>
            <Input.Search
              allowClear
              placeholder="请输入实例名称搜索"
              style={{ width: 300 }}
              onSearch={(val) => {
                setKeyword(val);
              }}
            />
          </div>
          <Button type="primary" icon={<IconPlus />} onClick={handleCreateInstance}>
            创建实例
          </Button>
        </div>
      </div>

      <div className={styles.body}>
        <div className={styles.content}>
          <Spin loading={loading} size={40} style={{ width: '100%', minHeight: '100%' }} tip="加载中...">
            <div className={styles.tableContainer}>
              {instanceList?.map((item, index) => (
                <ConnectInstanceCard key={`flow-${index}`} data={item} onEdit={handleEdit} onDelete={handleDelete} />
              ))}
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
      </div>
    </div>
  );
};

export default ConnectorInstancesPage;

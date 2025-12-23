import { formatTimestamp, simplifyUrl } from '@/utils/date';
import { getPlatformFeDomain } from '@/utils/domain';
import { Button, Dropdown, Input, Menu, Message, Modal, Pagination, Select, Space, Tag } from '@arco-design/web-react';
import {
  IconCaretDown,
  IconCheckCircleFill,
  IconCopy,
  IconDelete,
  IconEdit,
  IconPlus,
  IconRecord,
  IconSearch
} from '@arco-design/web-react/icon';
import { copyToClipboard } from '@onebase/common';
import {
  deletePlatformTenantApi,
  getPlatformTenantListApi,
  PlatformTenantSort,
  PlatformTenantStatus,
  updatePlatformTenantApi,
  type PlatformTenantInfo,
  type UpdateTenantParams
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

interface Options {
  label: string;
  value: string | PlatformTenantStatus;
}

const timeOptions: Options[] = [
  {
    label: '按创建时间正序排序',
    value: PlatformTenantSort.asc
  },
  {
    label: '按创建时间倒序排序',
    value: PlatformTenantSort.desc
  }
];

const statusOptions: Options[] = [
  {
    label: '全部状态',
    value: ''
  },
  {
    label: '已启用',
    value: PlatformTenantStatus.enabled
  },
  {
    label: '已禁用',
    value: PlatformTenantStatus.disabled
  }
];

const TenantManagement: React.FC = () => {
  const nav = useNavigate();
  const [tenantList, setTenantList] = useState<PlatformTenantInfo[]>([]); // 空间数据
  const [statusFilter, setStatusFilter] = useState<number | null>(null); // 状态筛选
  const [timeFilter, setTimeFilter] = useState<PlatformTenantSort>(PlatformTenantSort.desc); // 时间筛选
  const [keywordSearch, setKeywordSearch] = useState(''); // 关键字搜索
  const [searchInputValue, setSearchInputValue] = useState(''); // 输入框显示的值
  const [searchDebounceTimer, setSearchDebounceTimer] = useState<NodeJS.Timeout | null>(null);

  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);

  const [loading, setLoading] = useState(false);

  // 获取租户列表
  const getPlatformTenantList = async () => {
    setLoading(true);
    try {
      const resp = await getPlatformTenantListApi({
        pageNo: currentPage,
        pageSize: 10,
        status: statusFilter, // 添加状态筛选参数
        keywords: keywordSearch, // 添加关键词搜索参数
        sortType: timeFilter // 添加时间筛选参数 todo
      });
      setTenantList(resp.list);
      setTotal(resp.total);
    } catch (error: any) {
      console.error(error);
      Message.error(error.message || '获取租户列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setSearchInputValue(keywordSearch);
  }, [keywordSearch]);

  useEffect(() => {
    getPlatformTenantList();
  }, [statusFilter, keywordSearch, currentPage, timeFilter]);

  // 处理状态筛选
  const handleStatusChange = (status: number) => {
    setStatusFilter(status);
    setCurrentPage(1); // 重置到第一页
  };

  // 处理时间筛选
  const handleTimeChange = (value: any) => {
    setTimeFilter(value);
    setCurrentPage(1); // 重置到第一页
  };

  // 处理搜索输入变化（即时更新输入框）
  const handleSearchInputChange = (value: string) => {
    setSearchInputValue(value);

    // 清除之前的定时器
    if (searchDebounceTimer) {
      clearTimeout(searchDebounceTimer);
    }

    // 设置新的定时器
    const timer = setTimeout(() => {
      // 这里不需要再设置状态，因为 setKeywordSearch 已经在上面执行了
      setKeywordSearch(value);
      setCurrentPage(1); // 重置到第一页
    }, 500); // 500ms 防抖延迟

    setSearchDebounceTimer(timer);
  };

  // 启用 / 禁用
  const confirmEnable = (data: PlatformTenantInfo) => {
    const statusText = data.status === 0 ? '启用' : '禁用';
    const updateParams: UpdateTenantParams = {
      id: data.id!,
      name: data.name!,
      tenantCode: data.tenantCode!,
      accountCount: data.accountCount!,
      website: data.website!,
      publishModel: data.publishModel,
      logoUrl: data.logoUrl,
      status: data.status === 1 ? 0 : 1,
      tenantAdminUserUpdateReqVOSList: data.tenantAdminUserList
    };
    Modal.confirm({
      title: `确认要${statusText}空间（${data?.name}）吗？`,
      content: (
        <>
          <div style={{ marginBottom: 20 }}>
            <p>
              {data.status === 0
                ? '启用空间，该空间下的用户可正常进入空间并使用'
                : '禁用空间，该空间下的用户将无法登录，再次启用时可恢复正常使用'}
            </p>
          </div>
        </>
      ),
      okButtonProps: { status: data.status === 1 ? 'danger' : 'default' },
      onOk: async () => {
        await updatePlatformTenantApi(updateParams);
        await getPlatformTenantList();
        Message.success(`${statusText}空间成功`);
      }
    });
  };

  // 空间删除
  const confirmDelete = async (data: PlatformTenantInfo) => {
    let workspaceName = '';
    Modal.confirm({
      title: `确认要删除空间（${data?.name}）吗？`,
      content: (
        <>
          <div style={{ marginBottom: 20 }}>
            <p>删除空间，该空间下的企业、用户等数据将被永久删除，操作不可逆，请谨慎操作。</p>
          </div>
          <p style={{ marginBottom: 8 }}>如确定删除，请输入空间名称：{data?.name}</p>
          <Input
            placeholder="请输入空间名称"
            onChange={(v) => {
              workspaceName = v;
            }}
          />
        </>
      ),
      okButtonProps: { status: 'danger' },
      onOk: async () => {
        if (data.name !== workspaceName) {
          Message.warning('空间名称不正确');
          return Promise.reject();
        }
        await deletePlatformTenantApi(data.id!);
        await getPlatformTenantList();
        Message.success('删除空间成功');
      }
    });
  };

  // 处理点击地址跳转
  const handleClick = (text: string) => {
    window.open(text);
  };

  // 处理分页变化
  const handlePageChange = async (pageNo: number) => {
    try {
      setCurrentPage(pageNo);
    } catch (error) {
      console.error(error);
    }
  };

  const iconStyle = {
    marginRight: 4,
    fontSize: 16,
    transform: 'translateY(1px)'
  };

  const DropList = ({ data }: { data: PlatformTenantInfo }) => {
    return (
      <Menu>
        <Menu.Item key="1" onClick={() => nav(`edit?id=${data.id}`)}>
          <IconEdit style={iconStyle} />
          编辑
        </Menu.Item>
        <Menu.Item key="2" onClick={() => confirmEnable(data)}>
          <IconRecord style={iconStyle} />
          {data.status === 0 ? '启用' : '禁用'}
        </Menu.Item>
        <Menu.Item key="4" onClick={() => confirmDelete(data)}>
          <IconDelete style={iconStyle} />
          删除
        </Menu.Item>
      </Menu>
    );
  };

  return (
    <div className={styles.tenant}>
      {/* 新建搜索条件栏 */}
      <div className={styles.toolbar}>
        <Button type="primary" icon={<IconPlus />} onClick={() => nav('create')}>
          新建空间
        </Button>
        <Space size="large">
          <Select options={timeOptions} defaultValue={timeFilter} bordered={false} onChange={handleTimeChange} />
          <Select
            options={statusOptions}
            defaultValue={statusOptions[0].value}
            bordered={false}
            onChange={handleStatusChange}
          />

          <Input.Search
            className={styles.inputSearch}
            placeholder="搜索空间"
            allowClear
            value={searchInputValue}
            onChange={(value) => handleSearchInputChange(value)}
            // searchButton
            suffix={<IconSearch />}
          />
        </Space>
      </div>

      {/* 租户表格 */}
      <div className={styles.tenantList}>
        {tenantList.map((tenant) => {
          const { id, website = '' } = tenant;
          // 获取当前环境的域名前缀
          const platformFe = getPlatformFeDomain();
          const fullUrl = `${platformFe}#/tenant/${id}/${website}/`;
          const displayUrl = simplifyUrl(fullUrl);

          return (
            <div className={styles.tenantItem} key={tenant.id}>
              <div className={styles.left}>
                {tenant.logoUrl ? (
                  <img className={styles.tenantLogo} src={tenant.logoUrl} alt="" />
                ) : (
                  <div className={styles.tenantLogo}>{tenant?.name?.slice(0, 6)}</div>
                )}
                <div className={styles.tenantBaseInfo}>
                  <div className={styles.tenantName}>
                    <div className={styles.tenantNameText}>{tenant.name}</div>
                    {tenant.status === PlatformTenantStatus.enabled ? (
                      <Tag color="arcoblue" icon={<IconCheckCircleFill />}>
                        已启用
                      </Tag>
                    ) : (
                      <Tag color="gray" icon={<IconCheckCircleFill />}>
                        未启用
                      </Tag>
                    )}
                  </div>
                  <div className={styles.tenantID}>
                    <div>ID：</div>
                    <div className={styles.idText}>{tenant.tenantCode}</div>
                  </div>
                  <div className={styles.tenantAddress}>
                    <div>访问地址：</div>
                    <div className={styles.addressText} onClick={() => handleClick(fullUrl)}>
                      {displayUrl}
                    </div>
                    <IconCopy onClick={() => copyToClipboard(fullUrl)} style={{ fontSize: 16 }} />
                  </div>
                </div>
              </div>

              <div className={styles.center}>
                <div className={styles.enterpriseNumber}>
                  <div className={styles.rowName}>企业数</div>
                  <div className={styles.rowValue16}>{tenant.corpCount || 0}</div>
                </div>

                <div className={styles.UserNumber}>
                  <div className={styles.rowName}>用户数</div>
                  <div className={styles.flexRow}>
                    <div className={styles.rowValue16}>{tenant.existUserCount || 0}</div>
                    <div className={styles.rowValue14}>/{tenant.accountCount || 0}</div>
                  </div>
                </div>

                <div className={styles.appNumber}>
                  <div className={styles.rowName}>应用数</div>
                  <div className={styles.rowValue16}>{tenant.appCount || 0}</div>
                </div>

                <div className={styles.createTime}>
                  <div className={styles.rowName}>创建时间</div>
                  <div className={styles.rowValue12}>{formatTimestamp(tenant.createTime)}</div>
                </div>
              </div>

              <div className={styles.right}>
                <Button type="primary" onClick={() => handleClick(fullUrl)}>
                  进入空间
                </Button>
                <Dropdown.Button type="secondary" icon={<IconCaretDown />} droplist={<DropList data={tenant} />}>
                  <div onClick={() => nav(`edit?id=${tenant.id}`)}>
                    <IconEdit fontSize={16} style={{ marginRight: 4 }} />
                    编辑
                  </div>
                </Dropdown.Button>
              </div>
            </div>
          );
        })}
      </div>

      <Pagination
        current={currentPage}
        pageSize={10}
        total={total}
        onChange={handlePageChange}
        style={{
          alignSelf: 'flex-end'
        }}
      />
    </div>
  );
};

export default TenantManagement;

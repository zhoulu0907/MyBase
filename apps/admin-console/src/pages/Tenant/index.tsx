import { copyToClipboard, formatTimestamp, generateTimestampString, getDomainPrefix, simplifyUrl } from '@/utils/date';
import {
  Button,
  Dropdown,
  Form,
  Input,
  InputNumber,
  Menu,
  Message,
  Modal,
  Pagination,
  Select,
  Space,
  Switch,
  Tag,
  Tooltip,
  Typography
} from '@arco-design/web-react';
import { IconCaretDown, IconCheckCircleFill, IconCopy, IconDelete, IconEdit, IconPlus, IconRecord, IconSearch, IconStop } from '@arco-design/web-react/icon';
import {
  addPlatformTenantApi,
  getCreateTenantCountApi,
  getOtherTenantCountApi,
  getPlatformInfoApi,
  getPlatformTenantAdminListApi,
  getPlatformTenantListApi,
  getTenantUserCountApi,
  PlatformTenantStatus,
  updatePlatformTenantApi,
  type CreateTenantParams,
  type PlatformTenantInfo,
  type UpdateTenantParams
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './index.module.less';

const { Text } = Typography;
const { Option } = Select;
const { useForm } = Form;

interface Options {
  label: string;
  value: string | PlatformTenantStatus;
};

const timeOptions: Options[] = [
  {
    label: "按创建时间正序排序",
    value: 1 // todo
  },
  {
    label: "按创建时间倒序排序",
    value: 0
  },
];

const statusOptions: Options[] = [
  {
    label: "全部状态",
    value: ""
  },
  {
    label: "已启用",
    value: PlatformTenantStatus.enabled
  },
  {
    label: "已禁用",
    value: PlatformTenantStatus.disabled
  }
];

const TenantManagement: React.FC = () => {
  const [tenantList, setTenantList] = useState<PlatformTenantInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState<number | null>(null); // 状态筛选
  const [timeFilter, setTimeFilter] = useState<number>(1); // 时间筛选
  const [keywordSearch, setKeywordSearch] = useState(''); // 关键字搜索
  const [searchInputValue, setSearchInputValue] = useState(''); // 输入框显示的值
  const [searchDebounceTimer, setSearchDebounceTimer] = useState<NodeJS.Timeout | null>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [isNewTenant, setIsNewTenant] = useState(false);
  const [currentTenant, setCurrentTenant] = useState<PlatformTenantInfo | null>(null);
  // const [originalAdmin, setOriginalAdmin] = useState<string>('');
  // const [allocatableLicense, setAllocatableLicense] = useState<number>(10000); // 可分配许可证数量
  const [tenantLimit, setTenantLimit] = useState<number>(10000); // 租户数量限制
  const [otherTenantCount, setOtherTenantCount] = useState<number>(0); // 其他租户分配数
  const [tenantUserCount, setTenantUserCount] = useState<number>(0); // 租户下用户数
  const [adminList, setAdminList] = useState<{ id: string; nickname: string; username: string; mobile: string }[]>([]);
  const [confirmText, setConfirmText] = useState('');
  const [total, setTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);

  // 弹窗
  const [modalVisible, setModalVisible] = useState(false);
  const [confirmDisableVisible, setConfirmDisableVisible] = useState(false);
  const [confirmDeleteVisible, setConfirmDeleteVisible] = useState(false);


  const [form] = useForm();
  const nav = useNavigate();

  // 获取租户列表
  const getPlatformTenantList = async () => {
    setLoading(true);
    try {
      const resp = await getPlatformTenantListApi({
        pageNo: currentPage,
        pageSize: 10,
        status: statusFilter, // 添加状态筛选参数
        keywords: keywordSearch, // 添加关键词搜索参数
        // todo 时间排序
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
    getPlatformTenantList();
    // getTenantData()
  }, []);

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

  // 获取Tenant参数
  const getTenantData = () => {
    getPlatformAdminList();
    getAllocatable();
    getLicenseLimit();
  };

  // 获取license总数
  const getLicenseLimit = async () => {
    try {
      const licenseResp = await getPlatformInfoApi();
      if (licenseResp) {
        setTenantLimit(licenseResp.userLimit);
      }
    } catch (error) {
      console.error('Error fetching getLicenseLimit:', error);
    }
  };

  // 获取可分配数量
  const getAllocatable = async () => {
    try {
      const resp = await getCreateTenantCountApi();
      if (resp) {
        // setAllocatableLicense(resp);
      }
    } catch (error) {
      console.error('Error fetching allocatable:', error);
    }
  };

  // 获取其他租户数量
  const getOtherTenantCount = async (id?: string) => {
    try {
      const resp = await getOtherTenantCountApi(id);
      if (resp) {
        setOtherTenantCount(resp);
      }
    } catch (error) {
      console.error('Error fetching otherTenantCount:', error);
    }
  };
  // 获取用户数量
  const getTenantUserCount = async (id: string) => {
    try {
      const resp = await getTenantUserCountApi(id);
      if (resp) {
        setTenantUserCount(resp);
      }
    } catch (error) {
      console.error('Error fetching tenantUserCount:', error);
    }
  };

  // 获取管理员列表
  const getPlatformAdminList = async () => {
    try {
      const adminListResp = await getPlatformTenantAdminListApi();
      setAdminList(adminListResp);
    } catch (error) {
      console.error('Error fetching adminList:', error);
    }
  };
  // 打开新建弹窗
  const handleCreate = () => {
    setCurrentTenant(null);
    form.resetFields();
    form.setFieldsValue({
      status: PlatformTenantStatus.enabled,
      admin: undefined
    });
    getTenantData();
    getOtherTenantCount();
    setModalVisible(true);
    setIsNewTenant(true);
  };

  // 生成租户编码
  const generateTenantCode = () => {
    const timestamp = generateTimestampString();
    return `tenant_${timestamp}`;
  };

  // 打开编辑弹窗
  const handleEdit = (record: PlatformTenantInfo) => {
    const tenant: PlatformTenantInfo = {
      id: record.id.toString(),
      name: record.name,
      adminMobile: record.adminMobile,
      accountCount: record.accountCount,
      adminNickName: record.adminNickName,
      adminUserName: record.adminUserName,
      createTime: record.createTime,
      status: record.status,
      tenantCode: record.tenantCode
    };
    setCurrentTenant(tenant);
    // setOriginalAdmin(record.adminNickName || record.adminUserName);

    form.setFieldsValue({
      tenantName: record.name,
      tenantCode: record.tenantCode,
      admin: record.adminNickName,
      allocatedCount: record.accountCount,
      nickName: record.adminNickName,
      status:
        record.status === PlatformTenantStatus.enabled ? PlatformTenantStatus.enabled : PlatformTenantStatus.disabled,
      website: record.website
    });
    getTenantData();
    getTenantUserCount(record.id.toString());
    getOtherTenantCount(record.id.toString());

    setModalVisible(true);
    setIsNewTenant(false);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      setModalLoading(true);
      const values = await form.validate();

      // 检查分配人数（仅在创建租户或更新租户且状态为启用时检查）
      const allocatedCount = values.allocatedCount;
      const isStatusChangeToDisabled =
        currentTenant &&
        form.getFieldValue('status') === PlatformTenantStatus.disabled &&
        currentTenant.status === PlatformTenantStatus.enabled;

      // 只有在不是从启用改为禁用的情况下才检查人数限制
      if (!isStatusChangeToDisabled) {
        // 允许分配的人数
        let allowCount = tenantLimit - otherTenantCount;
        if (allowCount <= 0) {
          allowCount = 0;
        }

        if (allocatedCount > allowCount) {
          Message.error(`可分配人数不足，License总人数是${tenantLimit}，剩余${allowCount}`);
          setModalLoading(false); // 重置加载状态
          return;
        }
      }
      if (allocatedCount && allocatedCount < tenantUserCount) {
        Message.error(`租户内已使用租户数量为${tenantUserCount}，分配的租户数量不能低于此数量`);
        setModalLoading(false); // 重置加载状态
        return;
      }

      if (currentTenant) {
        // 更新租户
        await updateTenant(values);
      } else {
        // 创建租户
        await createTenant(values);
      }
    } catch (error) {
      console.error('表单验证失败:', error);
      setModalLoading(false); // 重置加载状态
    }
  };

  /**
   * 更新租户信息
   */
  const updateTenant = async (values: any) => {
    try {
      // 检查管理员是否发生变化
      const newAdminId = values.admin; // 这里是 id
      const originalAdminId = currentTenant?.adminUserName || ''; // 原始 contactName 是 username

      // 根据 id 查找管理员
      const selectedAdmin = adminList.find((admin) => admin.id === newAdminId);
      const adminUsername = selectedAdmin ? selectedAdmin.username : '';
      const adminNickname = selectedAdmin ? selectedAdmin.nickname : '';
      const adminMobile = selectedAdmin ? selectedAdmin.mobile : '';

      // 构建更新参数
      if (currentTenant?.id) {
        const updateParams: UpdateTenantParams = {
          id: currentTenant.id,
          name: values.tenantName,
          tenantCode: values.tenantCode,
          // 只有管理员发生变化时才传递管理员信息，否则传递空字符串
          adminNickName: newAdminId !== originalAdminId ? adminNickname : '',
          adminUserName: newAdminId !== originalAdminId ? adminUsername : '',
          adminMobile: newAdminId !== originalAdminId ? adminMobile : '',
          status: values.status,
          accountCount: values.allocatedCount,
          website: values.website
        };
        // 调用 updatePlatformTenantApi
        await updatePlatformTenantApi(updateParams);
        getPlatformTenantList();
        Message.success('更新成功');
        setModalVisible(false);
      } else {
        // 添加错误处理，以防万一id不存在
        Message.error('租户信息不完整，无法更新');
        console.error('Tenant ID is missing', currentTenant);
      }
    } catch (error: any) {
      console.error('更新租户信息失败:', error);
      Message.error('更新租户信息失败');
    } finally {
      setModalLoading(false); // 重置加载状态
    }
  };

  /**
   * 创建新租户
   */
  const createTenant = async (values: any) => {
    try {
      // 根据 id 查找管理员
      const selectedAdmin = adminList.find((admin) => admin.id === values.admin);
      const adminUsername = selectedAdmin ? selectedAdmin.username : '';
      const adminNickname = selectedAdmin ? selectedAdmin.nickname : '';
      const adminMobile = selectedAdmin ? selectedAdmin.mobile : '';

      const newTenantData: CreateTenantParams = {
        name: values.tenantName,
        tenantCode: generateTenantCode(),
        adminUserName: adminUsername,
        adminNickName: adminNickname,
        adminMobile: adminMobile,
        status: values.status,
        accountCount: values.allocatedCount,
        website: values.website
      };
      await addPlatformTenantApi(newTenantData);
      setCurrentPage(1);
      getPlatformTenantList();
      Message.success('创建租户成功');
      setModalVisible(false);
    } catch (error: any) {
      console.error('创建租户失败:', error);
      Message.error('创建租户失败');
    } finally {
      setModalLoading(false); // 重置加载状态
    }
  };

  // 确认禁用
  // 添加名称验证逻辑
  const confirmDisable = () => {
    // 添加名称验证逻辑
    if (confirmText !== currentTenant?.name) {
      Message.error(`租户名不正确`);
      return;
    }
    setConfirmText('');
    // 根据是否是新租户决定关闭哪些弹窗
    if (isNewTenant) {
      // 新建时只关闭确认弹窗
      setConfirmDisableVisible(false);
    } else {
      // 编辑时关闭所有弹窗
      setConfirmDisableVisible(false);
    }
  };

  // 取消禁用
  const cancelDisable = () => {
    setConfirmDisableVisible(false);
    // 同时Switch状态变为已启用
    form.setFieldsValue({ status: PlatformTenantStatus.enabled });
  };

  // 删除
  const confirmDelete = () => {
    Message.success('确认删除');
  };

  // 表格列定义
  const columns = [
    {
      title: '租户名称',
      dataIndex: 'name',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => a.name.localeCompare(b.name)
    },
    {
      title: '租户编码',
      dataIndex: 'tenantCode',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => {
        const timestampA = parseInt(a.tenantCode.replace('tenant_', ''), 10);
        const timestampB = parseInt(b.tenantCode.replace('tenant_', ''), 10);
        return timestampA - timestampB;
      }
    },
    {
      title: '分配的人员数量',
      dataIndex: 'accountCount',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => a.accountCount - b.accountCount
    },
    {
      title: '管理员',
      dataIndex: 'adminNickName'
    },
    {
      title: '访问地址',
      dataIndex: 'website',
      render: (text: string, record: PlatformTenantInfo) => {
        // 获取当前环境的域名前缀
        const domainPrefix = getDomainPrefix();
        const fullUrl = `${domainPrefix}/v0/obappbuilder/#/tenant/${record.id}/${text}/`;
        const displayUrl = simplifyUrl(fullUrl);

        return (
          <Space className={styles.urlWrapper}>
            <Tooltip position="tl" content={fullUrl}>
              <Text className={styles.fullUrl} onClick={() => handleClick(fullUrl)}>
                {displayUrl}
              </Text>
            </Tooltip>
            <IconCopy
              className={styles.copyIcon}
              onClick={(e) => {
                e.stopPropagation();
                copyToClipboard(fullUrl);
              }}
            />
          </Space>
        );
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) =>
        new Date(a.createTime).getTime() - new Date(b.createTime).getTime(),
      render: (text: string) => <div>{formatTimestamp(text)}</div>
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status: number) => (
        <Tag color={status === PlatformTenantStatus.enabled ? 'green' : 'gray'}>
          {status === PlatformTenantStatus.enabled ? '启用' : '禁用'}
        </Tag>
      )
    },
    {
      title: '操作',
      render: (_: PlatformTenantInfo, record: PlatformTenantInfo) => (
        <Text className={styles.tableBtn} onClick={() => handleEdit(record)}>
          修改
        </Text>
      )
    }
  ];

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
    transform: 'translateY(1px)',
  };

  const DropList = ({ data }: { data: PlatformTenantInfo }) => {
    console.log('tenant', data)
    return (
      <Menu onClickMenuItem={() => setCurrentTenant(data)}>
        <Menu.Item key='1' onClick={() => nav(`edit?id=${data.id}`)}>
          <IconEdit style={iconStyle} />
          编辑
        </Menu.Item>
        <Menu.Item key='2' onClick={confirmDelete}>
          <IconRecord style={iconStyle} />
          启用
        </Menu.Item>
        <Menu.Item key='3' onClick={confirmDisable}>
          <IconStop style={iconStyle} />
          禁用
        </Menu.Item>
        <Menu.Item key='4' onClick={() => setConfirmDeleteVisible(true)}>
          <IconDelete style={iconStyle} />
          删除
        </Menu.Item>
      </Menu>
    )
  }

  const hasTenantLogo = true;

  return (
    <div className={styles.tenant}>
      {/* 新建搜索条件栏 */}
      <div className={styles.toolbar}>
        <Button type="primary" icon={<IconPlus />} onClick={() => nav('create')}>
          新建空间
        </Button>
        <Space size="large">
          <Select options={timeOptions} defaultValue={timeOptions[0].value} bordered={false} onChange={handleTimeChange} />
          <Select options={statusOptions} defaultValue={statusOptions[0].value} bordered={false} onChange={handleStatusChange} />

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
        {
          tenantList.map(tenant => {
            const { id, website = '' } = tenant;
            // 获取当前环境的域名前缀
            const domainPrefix = getDomainPrefix();
            const fullUrl = `${domainPrefix}/v0/obappbuilder/#/tenant/${id}/${website}/`;
            const displayUrl = simplifyUrl(fullUrl);

            return (
              <div className={styles.tenantItem} key={tenant.id}>
                <div className={styles.left}>
                  {hasTenantLogo ? <img className={styles.tenantLogo} src="" alt="" /> :
                    <div className={styles.tenantLogo}>{tenant.name.slice(0, 6)}</div>}
                  <div className={styles.tenantBaseInfo}>
                    <div className={styles.tenantName}>
                      <div className={styles.tenantNameText}>{tenant.name}</div>
                      {tenant.status === PlatformTenantStatus.enabled ? <Tag color='arcoblue' icon={<IconCheckCircleFill />}>
                        已启用
                      </Tag> :
                        <Tag color='gray' icon={<IconCheckCircleFill />}>
                          未启用
                        </Tag>
                      }
                    </div>
                    <div className={styles.tenantID}>
                      <div>ID：</div>
                      <div className={styles.idText}>{tenant.tenantCode}</div>
                    </div>
                    <div className={styles.tenantAddress}>
                      <div>访问地址：</div>
                      <div className={styles.addressText}>{displayUrl}</div>
                      <IconCopy onClick={() => copyToClipboard(fullUrl)} style={{ fontSize: 16 }} />
                    </div>
                  </div>
                </div>

                <div className={styles.center}>
                  <div className={styles.enterpriseNumber}>
                    <div className={styles.rowName}>企业数</div>
                    <div className={styles.rowValue16}>137</div>
                  </div>

                  <div className={styles.UserNumber}>
                    <div className={styles.rowName}>用户数</div>
                    <div className={styles.flexRow}>
                      <div className={styles.rowValue16}>2200</div>
                      <div className={styles.rowValue14}>/4000</div>
                    </div>
                  </div>

                  <div className={styles.appNumber}>
                    <div className={styles.rowName}>应用数</div>
                    <div className={styles.rowValue16}>220</div>
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
                  <Dropdown.Button
                    type='secondary'
                    icon={<IconCaretDown />}
                    droplist={<DropList data={tenant} />}
                  >
                    <div onClick={() => nav(`edit?id=${tenant.id}`)} >
                      <IconEdit fontSize={16} style={{ marginRight: 4 }} />编辑
                    </div>
                  </Dropdown.Button>

                </div>

              </div>
            )
          })
        }

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

      {/* 新建/修改弹窗 */}
      <Modal
        title={currentTenant ? '修改租户' : '新建租户'}
        visible={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        autoFocus={false}
        focusLock={true}
        style={{ width: 600 }}
        confirmLoading={modalLoading}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="租户编号" field="tenantCode">
            <Input
              placeholder={generateTenantCode()}
              disabled
              value={currentTenant ? currentTenant.tenantCode : generateTenantCode()}
            />
          </Form.Item>
          <Form.Item label="租户名称" field="tenantName" rules={[{ required: true, message: '请输入租户名称' }]}>
            <Input placeholder="请输入租户名称" />
          </Form.Item>

          <Form.Item label="管理员" field="admin" rules={[{ required: true, message: '请选择管理员' }]}>
            <Select
              placeholder="请选择管理员"
              showSearch
              filterOption={(input, option) => {
                return option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
              }}
            >
              {adminList.map((admin) => (
                <Option key={admin.id} value={admin.id}>
                  {admin.nickname}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label={`分配人员数量 (可分配人员数量：${Math.max(0, tenantLimit - otherTenantCount)})`}
            field="allocatedCount"
            rules={[
              { required: true, message: '请输入分配人员数量' },
              { type: 'number', min: 1, message: '必须大于0' }
            ]}
          >
            <InputNumber placeholder="请输入分配人员数量" min={1} />
          </Form.Item>

          {/* 访问地址 */}
          <Form.Item
            label="访问地址"
            field="website"
            rules={[{ required: true, message: '请输入访问地址' }]}
            validateTrigger={['onBlur']}
          >
            <Input addBefore={getDomainPrefix()} placeholder="请输入访问地址" />
          </Form.Item>

          <Form.Item label="状态" field="status">
            <Switch
              checkedText="启用"
              uncheckedText="禁用"
              defaultChecked // 默认启用状态
              checked={form.getFieldValue('status') === PlatformTenantStatus.enabled}
              disabled={!currentTenant}
              onChange={(checked) => {
                form.setFieldsValue({
                  status: checked ? PlatformTenantStatus.enabled : PlatformTenantStatus.disabled
                });
                if (!checked) {
                  setConfirmDisableVisible(true);
                } else if (currentTenant && currentTenant.status === PlatformTenantStatus.disabled) {
                  setTenantList(
                    tenantList.map((item) =>
                      item.id === currentTenant.id ? { ...item, status: PlatformTenantStatus.enabled } : item
                    )
                  );
                }
              }}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 确认禁用弹窗 */}
      <Modal title="确认禁用租户" visible={confirmDisableVisible} onOk={confirmDisable} onCancel={cancelDisable}>
        <div style={{ marginBottom: 20 }}>
          <p>
            禁用租户将导致该租户下的所有数据（包括租户信息、配置记录、业务应用等）被直接禁用，正在运行的应用停止访问。
          </p>
          <p>请确认您已完成以下事项：</p>
          <p>1. 租户内的应用现在无法在访问的浏览；</p>
          <p>2. 确保此操作是您的最终决定。</p>
        </div>
        <p style={{ marginBottom: 8 }}>为防止误操作，请输入“{currentTenant?.name}”进行确认：</p>
        <Input value={confirmText} onChange={(value) => setConfirmText(value)} placeholder="请输入要禁用的租户名称" />
      </Modal>

      {/* 删除弹窗 */}
      <Modal title={`确认要删除空间（${currentTenant?.name}）吗？`} okButtonProps={{ status: 'danger' }} visible={confirmDeleteVisible} onOk={confirmDelete} onCancel={() => setConfirmDeleteVisible(false)}>
        <div style={{ marginBottom: 20 }}>
          <p>
            删除空间，该空间下的企业、用户等数据将被永久删除，操作不可逆，请谨慎操作。
          </p>
        </div>
        <p style={{ marginBottom: 8 }}>如确定删除，请输入空间名称：{currentTenant?.name}</p>
        <Input value={confirmText} onChange={(value) => setConfirmText(value)} placeholder="请输入空间名称" />
      </Modal>
    </div>
  );
};

export default TenantManagement;

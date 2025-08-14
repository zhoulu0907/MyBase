import React, { useState, useEffect } from 'react';
import {
  Table,
  Button,
  Input,
  Select,
  Modal,
  Message,
  Space,
  Typography,
  Radio,
  Form,
  InputNumber,
  Switch,
  Tag,
  Tooltip
} from '@arco-design/web-react';
import { IconCopy, IconSearch } from '@arco-design/web-react/icon';
import styles from './index.module.less'
import {
  getPlatformTenantListApi,
  getPlatformInfoApi,
  addPlatformTenantApi,
  updatePlatformTenantApi,
  getCreateTenantCountApi,
  getPlatformTenantAdminListApi,
  PlatformTenantStatus,
  ADMIN_ROOT_ID,
  getOtherTenantCountApi,
  getTenantUserCountApi,
  type PlatformTenantInfo,
  type CreateTenantParams,
  type UpdateTenantParams
} from "@onebase/platform-center";
import { formatTimestamp, generateTimestampString } from '@/utils/date';

const { Text } = Typography;
const { Option } = Select;
const { useForm } = Form;

const TenantManagement: React.FC = () => {
  const [tenantList, setTenantList] = useState<PlatformTenantInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    status: PlatformTenantStatus.all,
    keywords: ''
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [confirmDisableVisible, setConfirmDisableVisible] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [isNewTenant, setIsNewTenant] = useState(false);
  const [currentTenant, setCurrentTenant] = useState<PlatformTenantInfo | null>(null);
  const [originalAdmin, setOriginalAdmin] = useState<string>('');
  const [allocatableLicense, setAllocatableLicense] = useState<number>(10000); // 可分配许可证数量
  const [tenantLimit, setTenantLimit] = useState<number>(10000); // 租户数量限制
  const [otherTenantCount, setOtherTenantCount] = useState<number>(0); // 其他租户分配数
  const [tenantUserCount, setTenantUserCount] = useState<number>(0); // 租户下用户数
  const [adminList, setAdminList] = useState<{id: string, nickname: string, username: string}[]>([])
  const [confirmText, setConfirmText] = useState('');
  const [total, setTotal] = useState(0) 
  const [currentPage, setCurrentPage] = useState(1);
  
  const [form] = useForm();

  // 获取租户列表
  const getPlatformTenantList = async () => {
    setLoading(true);
    try {
      console.log("getPlatformTenantList searchParams.status: ", searchParams.status);
      const resp = await getPlatformTenantListApi({
        pageNo: currentPage,
        pageSize: 10,
        status: searchParams.status, // 添加状态筛选参数
        keywords: searchParams.keywords // 添加关键词搜索参数
      });
      setTenantList(resp.list);
      setTotal(resp.total)
      console.log(' 租户列表 resp.list: ', resp.list);
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
    getPlatformTenantList();
  }, [searchParams, currentPage]);

  // 处理状态筛选
  const handleStatusChange = (status: number) => {
    console.log('启用状态 before: ', searchParams.status);
    console.log('启用状态 new: ', status);
    setSearchParams({ ...searchParams, status });
    setCurrentPage(1); // 重置到第一页
  };

  // 处理搜索
  const handleSearch = async (keywords: string) => {
    setSearchParams({ ...searchParams, keywords });
    setCurrentPage(1); // 重置到第一页
  };

  // 获取Tenant参数
  const getTenantData = () => {
    getPlatformAdminList()
    getAllocatable()
    getLicenseLimit()
  }

  // 获取license总数
  const getLicenseLimit = async () => {
    try {
      const licenseResp = await getPlatformInfoApi();
      if(licenseResp) {
        console.log('license总数 licenseResp:', licenseResp);
        console.log('license下用户总数 licenseResp:', licenseResp.userLimit);
        setTenantLimit(licenseResp.userLimit)
      }
    } catch (error) {
      console.error('Error fetching getLicenseLimit:', error);
    }
  }

  // 获取可分配数量
  const getAllocatable = async () => {
    try {
      const resp = await getCreateTenantCountApi();
      if(resp) {
        console.log('可分配数量 resp:', resp);
        setAllocatableLicense(resp);
      }
    } catch (error) {
      console.error('Error fetching allocatable:', error);
    }
  }

  // 获取其他租户数量
  const getOtherTenantCount = async (id: string) => { 
    try {
      const resp = await getOtherTenantCountApi(id);
      if(resp) {
        console.log('其他租户数量 resp:', resp);
        setOtherTenantCount(resp);
      }
    } catch (error) {
      console.error('Error fetching otherTenantCount:', error);
    }
  }
  // 获取用户数量
  const getTenantUserCount = async (id: string) => {
    try {
      const resp = await getTenantUserCountApi(id);
      if(resp) {
        console.log('用户数量 resp:', resp);
        setTenantUserCount(resp);
      }
    } catch (error) {
      console.error('Error fetching tenantUserCount:', error);
    }
  }

  // 获取管理员列表
  const getPlatformAdminList = async () => { 
    try {
      const adminListResp = await getPlatformTenantAdminListApi()
      console.log('管理员列表 adminListResp:', adminListResp);
      setAdminList(adminListResp)
    } catch (error) {
      console.error('Error fetching adminList:', error);
    }
  }
  // 打开新建弹窗
  const handleCreate = () => {
    setCurrentTenant(null);
    form.resetFields();
    form.setFieldsValue({
      status: PlatformTenantStatus.enabled,
      admin: adminList.length > 0 ? adminList[0].nickname : undefined
    });
    getTenantData()
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
      contactMobile: record.contactMobile,
      accountCount: record.accountCount,
      contactName: record.contactName,
      createTime: record.createTime,
      status: record.status,
      tenantCode: record.tenantCode,
    };
    setCurrentTenant(tenant);
    setOriginalAdmin(record.contactName);
    
    form.setFieldsValue({
      tenantName: record.name,
      tenantCode: record.tenantCode,
      admin: record.contactName,
      allocatedCount: record.accountCount,
      status: record.status === PlatformTenantStatus.enabled ? PlatformTenantStatus.enabled : PlatformTenantStatus.disabled,
      website: record.website,
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

      // 检查分配人数
      const allocatedCount = values.allocatedCount;
      // 允许分配的人数
      const allowCount = tenantLimit - otherTenantCount;

      if (allocatedCount > allowCount) {
        Message.error(`可分配人数不足，License总人数是${tenantLimit}，剩余${allowCount}`);
        return;
      }

      if (allocatedCount && allocatedCount < tenantUserCount) {
        Message.error(`租户内已使用租户数量为${tenantUserCount}，分配的租户数量不能低于此数量`);
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
      setModalLoading(false);
    }
  };

  /**
   * 更新租户信息
   */
  const updateTenant = async (values: any) => {
    try {
      // 检查管理员是否发生变化
      const newAdmin = values.admin || '';
      
      // 构建更新参数
      const updateParams: UpdateTenantParams = {
        id: currentTenant?.id,
        name: values.tenantName,
        tenantCode: values.tenantCode,
        // 只有管理员发生变化时才传递管理员信息，否则传递空字符串
        contactName: originalAdmin !== newAdmin ? newAdmin : '',
        status: values.status,
        accountCount: values.allocatedCount,
        website: values.website,
      };
      
      // 调用 updatePlatformTenantApi
      await updatePlatformTenantApi(updateParams);
      getPlatformTenantList();
      Message.success('更新成功');
      setModalVisible(false);
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
      const newTenantData: CreateTenantParams = {
        name: values.tenantName,
        tenantCode: generateTenantCode(),
        contactName: values.admin,
        status: values.status,
        accountCount: values.allocatedCount,
        website: values.website,
      };
      await addPlatformTenantApi(newTenantData);
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
      // setModalVisible(false);
    }
  };

  // 取消禁用
  const cancelDisable = () => {
    setConfirmDisableVisible(false);
    // 同时Switch状态变为已启用
    form.setFieldsValue({ status: PlatformTenantStatus.enabled });
  };

  // 表格列定义
  const columns = [
    // { 
    //   title: '序号',
    //   dataIndex: 'order',
    //   key: 'order',
    //   render: (text: any, record: any, index: number) => index + 1,
    //   width: '5%',
    // },
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
      dataIndex: 'contactName'
    },
    {
      title: '访问地址',
      dataIndex: 'website',
      render: (text: string) => {
        // 获取当前环境的域名前缀
        const domainPrefix = getDomainPrefix();
        const fullUrl = `${domainPrefix}/v0/obappbuilder/#/${text}`;
        const displayUrl = simplifyUrl(fullUrl);
        
        return (
          <Space className={styles.urlWrapper}>
            {/* http://s25029301301.dev.internal.virtueit.net:81/v0/obappbuilder/#/XXX */}
            <Tooltip position='tl' content={fullUrl}>
              <Text className={styles.fullUrl} onClick={() => handleClick(displayUrl)}>
                {/* http://s25029301301.dev.internal.virtueit.net:81/v0/obappbuilder/#/XXX */}
                {fullUrl}
              </Text>
            </Tooltip>
            <IconCopy className={styles.copyIcon} onClick={(e) => {
              e.stopPropagation();
              navigator.clipboard.writeText(fullUrl).then(() => {
                Message.success('复制成功!');
              }).catch((error) => {
                console.error('复制失败:', error);
                // 提供备选方案
                Message.error('复制失败');
              });
            }}/>
          </Space>
        );
      }
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime(),
      render: (text: string) => (
        <div>{formatTimestamp(text)}</div>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status: number) => (
        <Tag color={ status === PlatformTenantStatus.enabled ? 'green' : 'gray' }>
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
    console.log('处理跳转地址:', text);
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
  
  // 获取当前环境的域名前缀
  const getDomainPrefix = () => {
    // 检查全局配置
    if (typeof window !== 'undefined' && window.global_config?.BASE_URL) {
      try {
        const url = new URL(window.global_config.BASE_URL);
        return `${url.protocol}//${url.host}`;
      } catch (e) {
        console.error('解析BASE_URL失败:', e);
      }
    }
    
    // 检查环境变量
    if (import.meta.env.VITE_API_BASE_URL) {
      try {
        const url = new URL(import.meta.env.VITE_API_BASE_URL);
        return `${url.protocol}//${url.host}`;
      } catch (e) {
        console.error('解析VITE_API_BASE_URL失败:', e);
      }
    }
    
    // 返回默认值
    return 'http://localhost:9524';
  };

  // 简化URL显示
  const simplifyUrl = (url: string) => {
    try {
      const urlObj = new URL(url);
      const host = urlObj.host;
      const protocol = urlObj.protocol;
      const pathname = urlObj.pathname;
      const hash = urlObj.hash;

      // 如果主机名很短，直接返回
      if (host.length <= 20) {
        return url;
      }

      // 省略主机名中间部分
      const simplifiedHost = `${host.substring(0, 16)}...`;
      return `${protocol}//${simplifiedHost}/${pathname}/${hash}`;
    } catch (e) {
      // 如果URL解析失败，返回原始URL
      return url;
    }
  };

  return (
    <div className={styles.tenant}>
      {/* 新建搜索条件栏 */}
      <div className={styles.toolbar}>
        <Button type="primary" onClick={handleCreate}>
          + 新建
        </Button>
        <Space size="large">
          <Radio.Group type="button" value={searchParams.status} onChange={handleStatusChange}>
            <Radio value={PlatformTenantStatus.all}>全部</Radio>
            <Radio value={PlatformTenantStatus.enabled}>启用</Radio>
            <Radio value={PlatformTenantStatus.disabled}>禁用</Radio>
          </Radio.Group>
          <Input.Search
            placeholder="搜索租户名称/编码"
            style={{ width: 300 }}
            allowClear
            value={searchParams.keywords}
            onChange={value => handleSearch(value)}
            // searchButton
            suffix={<IconSearch />}
          />
        </Space>
      </div>
      {/* 租户表格 */}
      <Table
        rowKey="id"
        border={false}
        columns={columns}
        data={tenantList}
        loading={loading}
        rowClassName={(record) => record.status === PlatformTenantStatus.enabled ? 'enabled-row' : ''}
        pagination={{
          current: currentPage,
          pageSize: 10,
          showTotal: true,
          total: total,
          onChange: handlePageChange
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
              filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
            >
              {adminList.map((admin) => (
                <Option key={admin.id} value={admin.nickname}>
                  {admin.nickname}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label={`分配人员数量 (可分配人员数量：${tenantLimit - otherTenantCount})`}
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
                  // console.log('checked', checked);
                  setConfirmDisableVisible(true);
                } else if (currentTenant && currentTenant.status === PlatformTenantStatus.disabled) {
                  setTenantList(tenantList.map(item => 
                    item.id === Number(currentTenant.id) ? { ...item, status: PlatformTenantStatus.enabled } : item
                  ));
                  // Message.success('已启用租户');
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
    </div>
  );
};

export default TenantManagement;
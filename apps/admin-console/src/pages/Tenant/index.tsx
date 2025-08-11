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
  Switch
} from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import styles from './index.module.less'
import { getPlatformTenantListApi, addPlatformTenantApi, updatePlatformTenantApi, getPlatformTenantAdminListApi, type PlatformTenantInfo, PlatformTenantStatus } from "@onebase/platform-center";
import { formatTimestamp } from '@/utils/date';

const { Text } = Typography;
const { Option } = Select;
const { useForm } = Form;
// 模拟数据
const mockData: PlatformTenantInfo[]  = [
  {
    id: 1,
    name: '默认用户',
    contactMobile: 'ZH2025070001',
    accountCount: 50,
    contactName: '石头',
    createTime: '2025-08-14 10:30',
    status: PlatformTenantStatus['已启用'],
    expireTime: '2025-08-14 10:30',
    packageId:1,
  },
  {
    id: 2,
    name: '测试环境验证用户',
    contactMobile: 'ZH2025070002',
    accountCount: 50,
    contactName: '石头',
    createTime: '2025-08-14 10:30',
    status: PlatformTenantStatus['已禁用'],
    expireTime: '2025-08-14 10:30',
    packageId:1,
  }
];

// 模拟管理员数据
const mockAdmins = Array.from({ length: 3 }, (_, i) => ({
  id: i + 1,
  name: `管理员${i + 1}`
}));

const TenantManagement: React.FC = () => {
  const [data, setData] = useState<PlatformTenantInfo[]>([]);
  const [tenantList, setTenantList] = useState<PlatformTenantInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    status: 'all',
    keyword: ''
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [confirmDisableVisible, setConfirmDisableVisible] = useState(false);
  const [isNewTenant, setIsNewTenant] = useState(false);
  const [currentTenant, setCurrentTenant] = useState<PlatformTenantInfo | null>(null);
  const [form] = useForm();
  const [totalLicense, setTotalLicense] = useState(100); // 总License数
  const [usedLicense, setUsedLicense] = useState(50); // 已用License数
  const [confirmText, setConfirmText] = useState('');

  // 获取租户列表
  const getPlatformTenantList = async () => {
    try {
      const resp = await getPlatformTenantListApi({ pageNum: 1, pageSize: 10 });
      const adminListResp = await getPlatformTenantAdminListApi()
      console.log('getPlatformTenantList resp.list:', resp.list);
      setTenantList(resp.list)
      console.log('adminListResp:', adminListResp);
    } catch (error: any) {
      console.error(error);
      Message.error(error.message || '获取租户列表失败');
    }
  };

  useEffect(() => {
    getPlatformTenantList();
  }, []);
  // 计算剩余License
  const remainingLicense = totalLicense - usedLicense;

  // 加载数据
  const loadData = async () => {
    setLoading(true);
    // 这里应该是API调用
    setTimeout(() => {
      setLoading(false);
    }, 500);
  };

  // 初始化加载
  useEffect(() => {
    loadData();
  }, []);

  // 筛选数据
  useEffect(() => {
    let result = [...tenantList];

    // 状态筛选
    if (searchParams.status !== 'all') {
      if (searchParams.status === 'enabled') {
        result = result.filter(item => item.status === PlatformTenantStatus['已启用']);
      } else if (searchParams.status === 'disabled') {
        result = result.filter(item => item.status === PlatformTenantStatus['已禁用']);
      }
    }

    // 关键词搜索
    if (searchParams.keyword) {
      result = result.filter(item => 
        item.name.includes(searchParams.keyword) || 
        item.name.includes(searchParams.keyword)
      );
    }
    
    setTenantList(result);
  }, [searchParams, tenantList]);

  // 处理状态筛选
  const handleStatusChange = (status: string) => {
    setSearchParams({ ...searchParams, status });
  };

  // 处理搜索
  const handleSearch = (keyword: string) => {
    setSearchParams({ ...searchParams, keyword });
  };

  // 重置搜索
  // const handleReset = () => {
  //   setSearchParams({ status: 'all', keyword: '' });
  // };

  // 打开新建弹窗
  const handleCreate = () => {
    setCurrentTenant(null);
    form.resetFields();
    form.setFieldsValue({ status: 'enabled' });
    setModalVisible(true);
    setIsNewTenant(true);
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
      status: record.status
    };
    setCurrentTenant(tenant);
    form.setFieldsValue({
      ...record,
      status: record.status === PlatformTenantStatus['已启用'] ? 'enabled' : 'disabled'
    });
    setModalVisible(true);
    setIsNewTenant(false);
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validate();

      // 检查分配人数
      const allocatedCount = values.allocatedCount;
      const currentUsed = currentTenant ? currentTenant.allocatedCount : 0;
      const delta = allocatedCount - currentUsed;

      if (delta > remainingLicense) {
        Message.error(`可分配人数不足，License总人数是${totalLicense}，剩余${remainingLicense}`);
        return;
      }

      if (currentTenant && allocatedCount < currentUsed) {
        Message.error(`用户内已使用用户数量为${currentUsed}，分配的用户数量不能低于此数量`);
        return;
      }
      
      // 如果是从禁用状态切换到启用状态
      if (currentTenant && currentTenant.status === 'disabled' && values.status === 'enabled') {
        // 更新数据状态
        setTenantList(tenantList.map((item) => (item.id === Number(currentTenant.id) ? { ...item, status: 'enabled' } : item)));
        Message.success('已启用用户');
        setModalVisible(false);
        return;
      }
      // 这里应该是API调用
      if (currentTenant) {
        // 更新
        setTenantList(tenantList.map((item) => (item.id === Number(currentTenant.id) ? { ...item, ...values } : item)));
        // Message.success('更新成功');
      } else {
        // 新增
        const newTenant = {
          id: tenantList.length + 1,
          tenantCode: `ZH${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${Math.floor(
            Math.random() * 10000
          )
            .toString()
            .padStart(4, '0')}`,
          ...values,
          createTime: new Date().toISOString()
        };
        setTenantList([...tenantList, newTenant]);
        Message.success('创建成功');
      }

      setModalVisible(false);
    } catch (error) {
      console.error('表单验证失败:', error);
    }
  };

  // 确认禁用
  // 添加名称验证逻辑
  const confirmDisable = () => {
    // 添加名称验证逻辑
    if (confirmText !== currentTenant?.name) {
      Message.error(`用户名不正确`);
      return;
    }
    const values = form.getFieldsValue();
    // 这里应该是API调用
    setTenantList(tenantList.map((item) => (item.id === Number(currentTenant?.id) ? { ...item, ...values } : item)));
    Message.success('已禁用用户');
    setConfirmText(''); // 确认禁用后清空输入框
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
    form.setFieldsValue({ status: 'enabled' });
  };
  // 表格列定义
  const columns = [
    { 
      title: '序号',
      dataIndex: 'order',
      key: 'order',
      render: (text: any, record: any, index: number) => index + 1,
      width: '5%',
      fixed: 'left',
    },
    // {
    //   title: '序号',
    //   dataIndex: 'id',
    //   width: 80
    // },
    {
      title: '用户名称',
      dataIndex: 'name',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => a.name.localeCompare(b.name)
    },
    {
      title: '用户编码',
      dataIndex: 'contactMobile',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => a.contactMobile.localeCompare(b.contactMobile)
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
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: (a: PlatformTenantInfo, b: PlatformTenantInfo) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime(),
      render: (text) => (
        <div>{formatTimestamp(text)}</div>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status: string) => (
        <Text style={{ color: status === 'enabled' ? '#00b42a' : '' }}>
          {status === 'enabled' ? '已启用' : '已禁用'}
        </Text>
      )
    },
    {
      title: '操作',
      render: (_: PlatformTenantInfo, record: PlatformTenantInfo) => (
        <Button type="text" onClick={() => handleEdit(record)}>
          修改
        </Button>
      )
    }
  ];

  return (
    <div className={styles.tenant}>
      {/* <Card bordered={false}> */}
        <div className={styles.toolbar}>
          <Button type="primary" status='success' onClick={handleCreate}>
            + 新建
          </Button>

          <Space size="large">
            {/* 添加激活状态 颜色为 #00b42a */}

            <Radio.Group type="button" value={searchParams.status} onChange={handleStatusChange}>
              <Radio value="all">全部</Radio>
              <Radio value="enabled">启用</Radio>
              <Radio value="disabled">禁用</Radio>
            </Radio.Group>

            <Input.Search
              placeholder="搜索租户名称/编码"
              style={{ width: 300 }}
              allowClear
              value={searchParams.keyword}
              onChange={value => handleSearch(value)}
              // searchButton
              suffix={<IconSearch />}
            />
          </Space>
        </div>

        <Table
          rowKey="id"
          border={false}
          columns={columns}
          data={tenantList}
          loading={loading}
          rowClassName={(record) => record.status === PlatformTenantStatus['已启用'] ? 'enabled-row' : ''}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条`
          }}
        />
      {/* </Card> */}
      
      {/* 新建/修改弹窗 */}
      <Modal
        title={currentTenant ? '修改用户' : '新建用户'}
        visible={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        autoFocus={false}
        focusLock={true}
        style={{ width: 600 }}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="租户编号" field="tenantCode">
            <Input placeholder="自动生成" disabled />
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
              {mockAdmins.map((admin) => (
                <Option key={admin.id} value={admin.name}>
                  {admin.name}
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            label={`分配人员数量 (可分配人员数量：${remainingLicense})`}
            field="allocatedCount"
            rules={[
              { required: true, message: '请输入分配人员数量' },
              { type: 'number', min: 1, message: '必须大于0' }
            ]}
          >
            <InputNumber placeholder="请输入分配人员数量" min={1} />
          </Form.Item>

          <Form.Item label="状态" field="status">
            <Switch
              checkedText="已启用"
              uncheckedText="已禁用"
              defaultChecked // 默认启用状态
              checked={form.getFieldValue('status') === 'enabled'}
              disabled={!currentTenant}
              onChange={(checked) => {
                form.setFieldsValue({
                  status: checked ? 'enabled' : 'disabled'
                });
                if (!checked) {
                  console.log('checked', checked);
                  setConfirmDisableVisible(true);
                } else if (currentTenant && currentTenant.status === 'disabled') {
                  setTenantList(tenantList.map(item => 
                    item.id === Number(currentTenant.id) ? { ...item, status: PlatformTenantStatus['已启用'] } : item
                  ));
                  Message.success('已启用用户');
                }
              }}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 确认禁用弹窗 */}
      <Modal title="确认禁用用户" visible={confirmDisableVisible} onOk={confirmDisable} onCancel={cancelDisable}>
        <div style={{ marginBottom: 20 }}>
          <p>
            禁用租户将导致该租户下的所有数据（包括用户信息、配置记录、业务应用等）被直接禁用，正在运行的应用停止访问。
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

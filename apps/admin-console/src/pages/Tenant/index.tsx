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
  Card,
  InputNumber,
  Switch
} from '@arco-design/web-react';
import { IconSearch } from '@arco-design/web-react/icon';
import type { Tenant, TenantRecord } from '@/types/tenant';
// import ConfirmDisableModal from './ConfirmDisableModal';

const { Text } = Typography;
const { Option } = Select;
const { useForm } = Form;

// 模拟数据
const mockData = [
  {
    id: 1,
    tenantName: '默认用户',
    tenantCode: 'ZH2025070001',
    allocatedCount: 50,
    admin: '石头',
    createTime: '2025-08-14 10:30',
    status: 'enabled'
  },
  {
    id: 2,
    tenantName: '测试环境验证用户',
    tenantCode: 'ZH2025070002',
    allocatedCount: 50,
    admin: '石头',
    createTime: '2025-08-14 10:30',
    status: 'disabled'
  }
];

// 模拟管理员数据
const mockAdmins = Array.from({ length: 3 }, (_, i) => ({
  id: i + 1,
  name: `管理员${i + 1}`
}));

const TenantManagement: React.FC = () => {
  const [data, setData] = useState(mockData);
  const [filteredData, setFilteredData] = useState(mockData);
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    status: 'all',
    keyword: ''
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [confirmDisableVisible, setConfirmDisableVisible] = useState(false);
  const [isNewTenant, setIsNewTenant] = useState(false);
  const [currentTenant, setCurrentTenant] = useState<Tenant | null>(null);
  const [form] = useForm();
  const [totalLicense, setTotalLicense] = useState(100); // 总License数
  const [usedLicense, setUsedLicense] = useState(50); // 已用License数
  const [confirmText, setConfirmText] = useState('');

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
    let result = [...data];
    
    // 状态筛选
    if (searchParams.status !== 'all') {
      result = result.filter(item => item.status === searchParams.status);
    }
    
    // 关键词搜索
    if (searchParams.keyword) {
      result = result.filter(item => 
        item.tenantName.includes(searchParams.keyword) || 
        item.tenantCode.includes(searchParams.keyword)
      );
    }
    
    setFilteredData(result);
  }, [searchParams, data]);

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
  const handleEdit = (record: TenantRecord) => {
    const tenant: Tenant = {
      id: record.id.toString(),
      name: record.tenantName,
      code: record.tenantCode,
      allocatedCount: record.allocatedCount,
      admin: record.admin,
      createTime: record.createTime,
      status: record.status
    };
    setCurrentTenant(tenant);
    form.setFieldsValue({
      ...record,
      status: record.status === 'enabled' ? 'enabled' : 'disabled'
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
      
      // // 检查状态变更 (启用 -> 禁用)
      // if (currentTenant && 
      //     currentTenant.status === 'enabled' && 
      //     values.status === 'disabled') {
      //   setConfirmDisableVisible(true);
      //   return;
      // }
      // 如果是从禁用状态切换到启用状态
      if (currentTenant && 
          currentTenant.status === 'disabled' && 
          values.status === 'enabled') {
        // 更新数据状态
        setData(data.map(item => 
          item.id === Number(currentTenant.id) ? { ...item, status: 'enabled' } : item
        ));
        Message.success('已启用用户');
        setModalVisible(false);
        return;
      }
      // 这里应该是API调用
      if (currentTenant) {
        // 更新
        setData(data.map(item => 
          item.id === Number(currentTenant.id) ? { ...item, ...values } : item
        ));
        // Message.success('更新成功');
      } else {
        // 新增
        const newTenant = {
          id: data.length + 1,
          tenantCode: `ZH${new Date().getFullYear()}${(new Date().getMonth() + 1).toString().padStart(2, '0')}${Math.floor(Math.random() * 10000).toString().padStart(4, '0')}`,
          ...values,
          createTime: new Date().toISOString()
        };
        setData([...data, newTenant]);
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
    setData(data.map(item => 
      item.id === Number(currentTenant?.id) ? { ...item, ...values } : item
    ));
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
      dataIndex: 'id',
      width: 80
    },
    {
      title: '用户名称',
      dataIndex: 'tenantName',
      sorter: (a: TenantRecord, b: TenantRecord) => a.tenantName.localeCompare(b.tenantName)
    },
    {
      title: '用户编码',
      dataIndex: 'tenantCode',
      sorter: (a: TenantRecord, b: TenantRecord) => a.tenantCode.localeCompare(b.tenantCode)
    },
    {
      title: '分配的人员数量',
      dataIndex: 'allocatedCount',
      sorter: (a: TenantRecord, b: TenantRecord) => a.allocatedCount - b.allocatedCount
    },
    {
      title: '管理员',
      dataIndex: 'admin'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      sorter: (a: TenantRecord, b: TenantRecord) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime()
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
      render: (_: TenantRecord, record: TenantRecord) => (
        <Button type="text" onClick={() => handleEdit(record)}>
          修改
        </Button>
      )
    }
  ];

  return (
    <div className="tenant-management">
      <Card bordered={false}>
        <div className="toolbar" style={{ marginBottom: 20, display: 'flex', justifyContent: 'space-between'}}>
          <Button type="primary" status='success' onClick={handleCreate}>
            新建
          </Button>

          <Space size="large">
            {/* 添加激活状态 颜色为 #00b42a */}
            
            <Radio.Group
              type="button"
              value={searchParams.status}
              onChange={handleStatusChange}
            >
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
              searchButton
              suffix={<IconSearch />}
            />
          </Space>
        </div>
        
        <Table
          rowKey="id"
          columns={columns}
          data={filteredData}
          loading={loading}
          rowClassName={(record) => record.status === 'enabled' ? 'enabled-row' : ''}
          pagination={{
            pageSize: 10,
            showTotal: (total) => `共 ${total} 条`
          }}
        />
      </Card>
      
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
          
          <Form.Item 
            label="租户名称" 
            field="tenantName"
            rules={[{ required: true, message: '请输入租户名称' }]}
          >
            <Input placeholder="请输入租户名称" />
          </Form.Item>
          
          <Form.Item 
            label="管理员" 
            field="admin"
            rules={[{ required: true, message: '请选择管理员' }]}
          >
            <Select
              placeholder="请选择管理员"
              showSearch
              filterOption={(input, option) => 
                option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
              }
            >
              {mockAdmins.map(admin => (
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
              defaultChecked  // 默认启用状态
              checked={form.getFieldValue('status') === 'enabled'}
              disabled={!currentTenant}
              onChange={(checked) => {
                form.setFieldsValue({ status: checked ? 'enabled' : 'disabled' });
                if(!checked) {
                  console.log('checked', checked);
                  setConfirmDisableVisible(true)
                } else if (currentTenant && currentTenant.status === 'disabled') {
                  setData(data.map(item => 
                    item.id === Number(currentTenant.id) ? { ...item, status: 'enabled' } : item
                  ));
                  Message.success('已启用用户');
                }
              }}
            />
          </Form.Item>
        </Form>
      </Modal>
      
      {/* 确认禁用弹窗 */}
      <Modal
        title="确认禁用用户"
        visible={confirmDisableVisible}
        onOk={confirmDisable}
        onCancel={cancelDisable}
        
      >
        <div style={{ marginBottom: 20 }}>
          <p>
            禁用租户将导致该租户下的所有数据（包括用户信息、配置记录、业务应用等）被直接禁用，正在运行的应用停止访问。
          </p>
          <p>请确认您已完成以下事项：</p>
          <p>1. 租户内的应用现在无法在访问的浏览；</p>
          <p>2. 确保此操作是您的最终决定。</p>
        </div>
        <p style={{ marginBottom: 8 }}>
          为防止误操作，请输入“{currentTenant?.name}”进行确认：
        </p>
        <Input
          value={confirmText}
          onChange={(value) => setConfirmText(value)}
          placeholder="请输入要禁用的租户名称"
        />
      </Modal>
    </div>
  );
};

export default TenantManagement;
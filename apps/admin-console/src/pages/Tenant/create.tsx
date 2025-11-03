import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Form,
  Input,
  Button,
  Upload,
  Select,
  Message,
  Space,
  Checkbox,
} from '@arco-design/web-react';
import { IconUpload } from '@arco-design/web-react/icon';
import { addPlatformTenantApi, getCreateTenantCountApi, getOtherTenantCountApi, getPlatformInfoApi, getPlatformTenantAdminListApi, getTenantUserCountApi, PlatformTenantStatus, type CreateTenantParams } from '@onebase/platform-center';
import { generateTimestampString } from '@/utils/date';
import styles from './index.module.less';

const Option = Select.Option;

const CreateSpace = () => {
  const [form] = Form.useForm();
  const nav = useNavigate();

  const [adminList, setAdminList] = useState<{ id: string; nickname: string; username: string; mobile: string }[]>([]);
  const [tenantLimit, setTenantLimit] = useState<number>(10000); // 租户数量限制
  const [otherTenantCount, setOtherTenantCount] = useState<number>(0); // 其他租户分配数
  const [tenantUserCount, setTenantUserCount] = useState<number>(0); // 租户下用户数

  // 获取Tenant参数
  useEffect(() => {
    form.resetFields();
    form.setFieldsValue({
      status: PlatformTenantStatus.enabled,
      admin: undefined
    });
    getPlatformAdminList();
    getAllocatable();
    getLicenseLimit();
    getOtherTenantCount();
    // getTenantUserCount();
  }, []);

  // 生成租户编码
  const generateTenantCode = () => {
    const timestamp = generateTimestampString();
    return `tenant_${timestamp}`;
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
  // const getTenantUserCount = async (id: string) => {
  //   try {
  //     const resp = await getTenantUserCountApi(id);
  //     if (resp) {
  //       setTenantUserCount(resp);
  //     }
  //   } catch (error) {
  //     console.error('Error fetching tenantUserCount:', error);
  //   }
  // };

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
      nav('tenant');
      Message.success('创建租户成功');
    } catch (error: any) {
      console.error('创建租户失败:', error);
      Message.error('创建租户失败');
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      console.log('提交数据:', values);
      // 检查分配人数（仅在创建租户或更新租户且状态为启用时检查）
      const allocatedCount = values.allocatedCount;
      const isStatusChangeToDisabled = false;

      // 只有在不是从启用改为禁用的情况下才检查人数限制
      if (!isStatusChangeToDisabled) {
        // 允许分配的人数
        let allowCount = tenantLimit - otherTenantCount;
        if (allowCount <= 0) {
          allowCount = 0;
        }

        if (allocatedCount > allowCount) {
          Message.error(`可分配人数不足，License总人数是${tenantLimit}，剩余${allowCount}`);
          return;
        }
      }
      if (allocatedCount && allocatedCount < tenantUserCount) {
        Message.error(`租户内已使用租户数量为${tenantUserCount}，分配的租户数量不能低于此数量`);
        return;
      }

      // 创建租户
      await createTenant(values);
    } catch (error) {
      console.log('表单验证失败', error);
    }
  };

  const handleBack = () => {
    window.history.back();
  };

  return (
    <div className={styles.createPage}>
      <Form
        form={form}
        layout="horizontal"
        autoComplete="off"
        initialValues={{
          userLimit: 5000,
          status: true,
          saas: false,
        }}
      >
        <Form.Item
          label="空间名称"
          field="name"
          rules={[{ required: true, message: '请输入空间名称' }]}
        >
          <Input placeholder="输入空间名称" />
        </Form.Item>

        <Form.Item label="空间 Logo" field="logo">
          <Upload
            listType="picture-card"
            action="/"
            showUploadList={false}
          >
            <Button type='outline' icon={<IconUpload />}>上传图片</Button>
          </Upload>
          <div style={{ color: '#999', marginTop: 4 }}>建议比例 2:1</div>
        </Form.Item>

        <Form.Item
          label="访问地址"
          field="url"
          rules={[{ required: true, message: '请输入访问地址' }]}
        >
          <Input prefix="http://" placeholder="www.onebase.com/" />
        </Form.Item>

        <Form.Item
          label="用户上限"
          field="userLimit"
          rules={[{ required: true, message: '请输入用户上限' }]}
        >
          <Input type="number" />
        </Form.Item>

        <Form.Item
          label="管理员"
          field="admin"
          rules={[{ required: true, message: '请选择管理员' }]}
          extra="当前用户将作为空间所有者"
        >
          <Select
            placeholder="选择管理员"
            mode="multiple"
            allowClear
            style={{ width: '100%' }}
          >
            <Option value="王少青">王少青</Option>
            <Option value="张三">张三</Option>
            <Option value="李四">李四</Option>
          </Select>
        </Form.Item>

        <Form.Item
          label="状态"
          field="status"
          triggerPropName="checked"
          rules={[{ required: true }]}
        >
          <Checkbox>启用</Checkbox>
        </Form.Item>

        <Form.Item
          label="SaaS 功能"
          field="saas"
          triggerPropName="checked"
          rules={[{ required: true }]}
        >
          <Checkbox>启用</Checkbox>
        </Form.Item>

        <Space style={{ margin: 'auto' }}>
          <Button onClick={handleBack}>返回</Button>
          <Button type="primary" onClick={handleSubmit}>
            提交
          </Button>
        </Space>
      </Form>
    </div>
  );
};

export default CreateSpace;

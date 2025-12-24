import { filterSpace } from '@/utils';
import { emailValidator, phoneValidator } from '@/utils/validator';
import {
  Avatar,
  Button,
  Form,
  Grid,
  Input,
  Message,
  Modal,
  Select,
  Space,
  Switch,
  Typography
} from '@arco-design/web-react';
import { formatTimeYMDHMS } from '@onebase/common';
import type { SimpleRoleVO, UserVO } from '@onebase/platform-center';
import { createExternalUserApi, getAuthAppListApi, getExternalUser, StatusEnum, updateExternalUserApi } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import styles from '../index.module.less';
import type { authorizedAppList } from '../../Business/types/appItem';

const Row = Grid.Row;
const Col = Grid.Col;

interface UserFormModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
  initialValues?: Partial<UserVO>;
  mode?: 'create' | 'edit';
  isDetail?: boolean;
  tableData?: any[];
  deptLoading?: boolean;
  onRefreshDept: () => void;
}

export default function UserFormModal({
  visible,
  onCancel,
  onOk,
  onRefreshDept,
  initialValues,
  tableData,
  mode = 'create',
  isDetail = false // 详情模式
}: UserFormModalProps) {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [statusCheckedValue, setStatusCheckedValue] = useState(false);
  const [avatarUrl, setAvatarUrl] = useState<string>();
  const [encryptedMobile, setEncryptedMobile] = useState<string>('');
  const [dropdownList, setDropdownList] = useState<authorizedAppList[]>([]);

  const getApplicationIdResult = async () => {
    try {
      const res: authorizedAppList[] = await getAuthAppListApi();
      setDropdownList(res ? res : []);
    } catch (error) {
      Message.error('获取列表失败');
    }
  };

  useEffect(() => {
    if (visible) {
      getApplicationIdResult();
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue(initialValues);
        setAvatarUrl(initialValues.avatar);
        setStatusCheckedValue(initialValues.status === StatusEnum.ENABLE ? true : false);
      } else {
        // 创建时用户状态默认为开启
        form.setFieldValue('status', StatusEnum.ENABLE);
        setStatusCheckedValue(true);
      }
    }
  }, [visible, initialValues, form]);

  useEffect(() => {
    if (!visible) {
      return;
    }

    // 在编辑模式下获取用户信息并设置角色ID为初始值
    if (mode === 'edit' && initialValues?.id) {
      if (initialValues.mobile?.includes('*')) {
        setEncryptedMobile(initialValues.mobile);
      }
      getExternalUser(initialValues.id).then((user: UserVO) => {
        form.setFieldsValue({ roleIds: user.roles?.map((item: SimpleRoleVO) => item.id) });
      });
    }
  }, [visible, mode, initialValues, form]);

  const handleSubmit = async () => {
    if (isDetail) {
      // 详情模式下直接关闭
      onOk();
      return;
    }

    try {
      const values = await form.validate();
      const params = {
        ...values,
        avatar: avatarUrl,
        mobile: filterSpace(values.mobile),
        email: filterSpace(values.email),
        nickName: filterSpace(values.nickName),
        status: statusCheckedValue ? StatusEnum.ENABLE : StatusEnum.DISABLE
      };
      setLoading(true);
      if (mode === 'create') {
        await createExternalUserApi(params);
        Message.success('新建成功');
        onRefreshDept();
      } else {
        await updateExternalUserApi({
          ...params,
          id: initialValues?.id,
          mobile: encryptedMobile ? encryptedMobile : filterSpace(values.mobile)
        });
        Message.success('编辑成功');
        onRefreshDept();
      }
      onOk();
    } finally {
      setLoading(false);
    }
  };

  const filterOptions = dropdownList.filter((data) => !tableData?.some((item) => item.id === data.id));

  return (
    <Modal
      title={
        <div style={{ textAlign: 'left' }}>{isDetail ? '用户详情' : mode === 'create' ? '新建用户' : '编辑用户'}</div>
      }
      visible={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      unmountOnExit
      style={{ width: 700 }}
      // 详情模式下自定义footer 只有关闭按钮
      footer={
        isDetail
          ? [
              <Button key="close" onClick={onCancel}>
                关闭
              </Button>
            ]
          : undefined
      }
    >
      <Form form={form} layout="vertical" autoComplete="off" disabled={isDetail}>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="姓名" field="nickName" rules={[{ required: true, message: '请输入姓名' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="邮箱" field="email" rules={[{ validator: emailValidator }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item
              label="手机号"
              field="mobile"
              disabled={mode === 'edit'}
              rules={
                (mode === 'create' && [{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]) ||
                []
              }
            >
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={24}>
            <Form.Item label="选择应用" field="applicationIdList" rules={[{ required: true, message: '请选择应用' }]}>
              <Select
                mode="multiple"
                placeholder="选择应用"
                allowClear
                showSearch
                renderFormat={(option: any, value: any) => {
                  const selectedOption = dropdownList.find((item) => item.id === value);
                  return selectedOption ? selectedOption.appName : value;
                }}
              >
                {filterOptions.map((option) => (
                  <Select.Option key={option.id} value={option.id}>
                    <Space align="center" size={12}>
                      <Avatar style={{ backgroundColor: option.iconColor }}>{option.iconName}</Avatar>
                      <div className={styles.authorizedOption}>
                        <Typography.Text>{option.appName}</Typography.Text>
                        <Typography.Text type="secondary">
                          {option.versionNumber} · {formatTimeYMDHMS(option.createTime)}
                        </Typography.Text>
                      </div>
                    </Space>
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="启用状态" triggerPropName="checked" labelCol={{ span: 6 }} wrapperCol={{ span: 12 }}>
              <Switch checked={statusCheckedValue} onChange={setStatusCheckedValue} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
}

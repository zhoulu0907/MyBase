import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Grid, Switch, Select, Message, Button, TreeSelect } from '@arco-design/web-react';
import { createUser, updateUser, getSimpleRoleList, getUser } from '@onebase/platform-center';
import type { UserVO, SimpleRoleVO } from '@onebase/platform-center';

const Row = Grid.Row;
const Col = Grid.Col;

interface UserFormModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
  initialValues?: Partial<UserVO>;
  mode?: 'create' | 'edit';
  isDetail?: boolean; // 详情模式标志
  deptTree?: any[]; // 部门树数据
  deptLoading?: boolean; // 部门数据加载状态
}

export default function UserFormModal({
  visible,
  onCancel,
  onOk,
  initialValues,
  mode = 'create',
  isDetail = false, // 详情模式
  deptTree = [],
  deptLoading = false
}: UserFormModalProps) {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [roleList, setRoleList] = useState<SimpleRoleVO[]>([]);

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue(initialValues);
      }
    }
  }, [visible, initialValues, form]);

  useEffect(() => {
    if (!visible) {
      return;
    }

    getSimpleRoleList().then((res) => {
      setRoleList(res);
    });

    // 在编辑模式下获取用户信息并设置角色ID为初始值
    if (mode === 'edit' && initialValues?.id) {
      getUser(initialValues.id).then((user) => {
        form.setFieldsValue({ roleIds: user.roleId });
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
      setLoading(true);
      if (mode === 'create') {
        await createUser({ ...values, status: values.status ? 1 : 0 });
        Message.success('新建成功');
      } else {
        // TODO 待接口修改后重新验证
        await updateUser({ ...values, status: values.status ? 1 : 0 });
        Message.success('编辑成功');
      }
      onOk();
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isDetail ? '用户详情' : mode === 'create' ? '新建用户' : '编辑用户'}
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
            <Form.Item label="姓名" field="nickname" rules={[{ required: true, message: '请输入姓名' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="手机号" field="mobile" rules={[{ required: true, message: '请输入手机号' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="账号" field="username" rules={[{ required: true, message: '请输入账号' }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
          <Col span={12}>
            {mode === 'create' && (
              <Form.Item
                label="密码"
                field="password"
                rules={mode === 'create' && !isDetail ? [{ required: true, message: '请输入密码' }] : []}
              >
                <Input.Password placeholder="请输入" />
              </Form.Item>
            )}
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="邮箱（选填）" field="email">
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="部门（选填）" field="deptId">
              <TreeSelect
                placeholder="请选择"
                allowClear
                treeData={deptTree}
                loading={deptLoading}
                disabled={deptLoading || isDetail}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="角色（选填）" field="roleIds">
              <Select placeholder="请选择" mode="multiple" allowClear>
                {roleList.map((role) => (
                  <Select.Option key={role.id} value={role.id}>
                    {role.name}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label=" " field="status" style={{ marginBottom: 0 }}>
              <span style={{ marginRight: 8 }}>启用状态</span>
              <Switch defaultChecked={initialValues?.status === 1} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
}

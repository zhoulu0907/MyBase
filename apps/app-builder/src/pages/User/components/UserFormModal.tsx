import React, { useEffect } from 'react';
import { Modal, Form, Input, Grid, Switch, Select, Message, Button, TreeSelect } from '@arco-design/web-react';
import { createUser, updateUser } from '@onebase/platform-center/src/services/user';
import type { UserVO } from '@onebase/platform-center/src/types/user';

const Row = Grid.Row;
const Col = Grid.Col;

interface UserFormModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: () => void;
  initialValues?: Partial<UserVO>;
  mode?: 'create' | 'edit';
  isDetail?: boolean; // 添加详情模式标志
  deptTree?: any[]; // 部门树数据
  deptLoading?: boolean; // 部门数据加载状态
}

export default function UserFormModal({
  visible,
  onCancel,
  onOk,
  initialValues,
  mode = 'create',
  isDetail = false, // 添加详情模式参数
  deptTree = [],
  deptLoading = false
}: UserFormModalProps) {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue(initialValues);
      }
    }
  }, [visible, initialValues, form]);

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
        await updateUser({ ...initialValues, ...values, status: values.status ? 1 : 0 });
        Message.success('编辑成功');
      }
      onOk();
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={isDetail ? '用户详情' : (mode === 'create' ? '新建用户' : '编辑用户')}
      visible={visible}
      onCancel={onCancel}
      onOk={handleSubmit}
      confirmLoading={loading}
      unmountOnExit
      style={{ width: 700 }}
      // 详情模式下自定义footer
      footer={isDetail ? 
        [
          <Button key="close" onClick={onCancel}>
            关闭
          </Button>
        ] : 
        undefined
      }
    >
      <Form 
        form={form} 
        layout="vertical" 
        autoComplete="off"
        disabled={isDetail}
      >
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
            <Form.Item label="密码" field="password" rules={mode === 'create' && !isDetail ? [{ required: true, message: '请输入密码' }] : []}>
              <Input.Password placeholder="请输入" />
              </Form.Item>
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
                disabled={deptLoading}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="角色（选填）" field="roleIds">
              <Select placeholder="请选择" mode="multiple" allowClear>
                <Select.Option value={1}>管理员</Select.Option>
                <Select.Option value={2}>开发工程师</Select.Option>
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
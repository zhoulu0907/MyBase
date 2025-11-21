import { emailValidator, phoneValidator } from '@/utils/validator';
import { Button, Form, Grid, Input, Message, Modal, Switch, TreeSelect } from '@arco-design/web-react';
import type { SimpleRoleVO, UserVO } from '@onebase/platform-center';
import { createUser, getUser, StatusEnum, updateUser } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { hasPermission } from '@/utils/permission';
import { CORP_DEPT_QUERY } from '@/constants/permission';

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
  onRefreshDept: () => void;
}

export default function UserFormModal({
  visible,
  onCancel,
  onOk,
  onRefreshDept,
  initialValues,
  mode = 'create',
  isDetail = false, // 详情模式
  deptTree = [],
  deptLoading = false
}: UserFormModalProps) {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);
  const [statusCheckedValue, setStatusCheckedValue] = useState(false);
  const [hasDeptQueryPermission, setHasDeptQueryPermission] = useState(true);

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue(initialValues);
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

    // 检查是否有角色/部门查询权限
    const deptPermission = hasPermission(CORP_DEPT_QUERY);
    setHasDeptQueryPermission(deptPermission);

    // 在编辑模式下获取用户信息并设置角色ID为初始值
    if (mode === 'edit' && initialValues?.id) {
      getUser(initialValues.id).then((user: UserVO) => {
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
      const params = { ...values, status: statusCheckedValue ? StatusEnum.ENABLE : StatusEnum.DISABLE };
      setLoading(true);
      if (mode === 'create') {
        await createUser(params);
        Message.success('新建成功');
        onRefreshDept();
      } else {
        await updateUser({ ...params, id: initialValues?.id });
        Message.success('编辑成功');
        onRefreshDept();
      }
      onOk();
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{isDetail ? '用户详情' : mode === 'create' ? '新建用户' : '编辑用户'}</div>}
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
            <Form.Item
              label="手机号"
              field="mobile"
              rules={[{ required: true, message: '请输入手机号' }, { validator: phoneValidator }]}
            >
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="账号" field="username" rules={[{ required: true, message: '请输入账号' }]}>
              <Input placeholder="请输入" autoComplete="new-password" />
            </Form.Item>
          </Col>
          {mode === 'create' && <Col span={12}>
            <Form.Item
              label="密码"
              field="password"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password placeholder="请输入" autoComplete="new-password" />
            </Form.Item>
          </Col>}
        </Row>
        <Row gutter={24}>
          <Col span={12}>
            <Form.Item label="邮箱" field="email" rules={[{ validator: emailValidator }]}>
              <Input placeholder="请输入" />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label="部门" field="deptId">
              <TreeSelect
                showSearch
                filterTreeNode={(inputValue, treeNode) => {
                  return treeNode.props.name.includes(inputValue);
                }}
                placeholder={hasDeptQueryPermission ? '请选择' : '无权限'}
                allowClear
                treeData={deptTree}
                loading={deptLoading}
                disabled={deptLoading || isDetail || !hasDeptQueryPermission}
              />
            </Form.Item>
          </Col>
        </Row>
        <Row gutter={24} justify="start">
          <Col span={12}>
            <Form.Item label="启用状态" triggerPropName="checked" layout="horizontal" labelCol={{ span: 6 }} wrapperCol={{ span: 12 }} >
              <Switch checked={statusCheckedValue} onChange={setStatusCheckedValue} />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  );
}

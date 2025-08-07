import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, Select, TreeSelect } from '@arco-design/web-react';
import { getSimpleUserList, getSimpleDeptList } from '@onebase/platform-center';
import type { UserVO } from '@onebase/platform-center';
import type { DeptForm } from '@onebase/platform-center';
import { listToTree } from '@/utils/tree';
const FormItem = Form.Item;

interface DepartmentModalProps {
  visible: boolean;
  onCancel: () => void;
  onConfirm: (values: DeptForm) => void;
  loading?: boolean;
  initialValues?: DeptForm;
}

export type SimpleUserVO = Pick<UserVO, 'id' | 'username' | 'nickname'> & Partial<UserVO>;
const DepartmentModal: React.FC<DepartmentModalProps> = (props) => {
  const { visible, onCancel, onConfirm, loading, initialValues } = props;
  const [form] = Form.useForm();
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [userList, setUserList] = useState<SimpleUserVO[]>([]);
  const [deptTree, setDeptTree] = useState<any[]>([]);

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue({ ...initialValues });
      }
      // 获取用户列表和部门树
      fetchUserList();
      fetchDeptTree();
    }
  }, [visible, initialValues, form]);

  const fetchUserList = async () => {
    const users = await getSimpleUserList();
    setUserList(users);
  };

  const fetchDeptTree = async () => {
    const deptList = await getSimpleDeptList();
    const tree = listToTree(deptList);
    setDeptTree(tree);
  };

  const handleConfirm = async () => {
    try {
      await form.validate();
      const values: DeptForm = form.getFieldsValue();
      setConfirmLoading(true);
      await onConfirm(values);
      setConfirmLoading(false);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{initialValues ? '编辑部门' : '新增部门'}</div>}
      visible={visible}
      onConfirm={handleConfirm}
      onCancel={onCancel}
      confirmLoading={loading || confirmLoading}
      maskClosable={false}
    >
      <Form form={form} layout="vertical">
        <FormItem label="部门名称" field="name" rules={[{ required: true, message: '请输入部门名称' }]}>
          <Input placeholder="请输入部门名称" />
        </FormItem>
        <FormItem label="部门描述" field="remark">
          <Input.TextArea placeholder="请输入部门描述" autoSize={{ minRows: 3, maxRows: 5 }} />
        </FormItem>
        <FormItem label="上级部门" field="parentId">
          <TreeSelect
            placeholder="请选择上级部门"
            treeData={deptTree}
            allowClear
            showSearch
            treeProps={{
              virtualListProps: {
                height: 200
              }
            }}
          />
        </FormItem>
        <FormItem label="管理员" field="leaderUserId" rules={[{ required: true, message: '请选择管理员' }]}>
          <Select
            placeholder="请选择管理员"
            allowClear
            showSearch
            filterOption={(input: string, option: any) =>
              option?.children?.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0
            }
          >
            {userList.map((user) => (
              <Select.Option key={user.id} value={user.id}>
                {user.nickname}
              </Select.Option>
            ))}
          </Select>
        </FormItem>
      </Form>
    </Modal>
  );
};

export default DepartmentModal;
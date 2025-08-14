import { listToTree } from '@/utils/tree';
import { Form, Input, Modal, Select, TreeSelect } from '@arco-design/web-react';
import type { DeptForm, UserVO } from '@onebase/platform-center';
import { getSimpleDeptList, getSimpleUserList } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { hasPermission } from '@/utils/permission';
import { TENANT_USER_QUERY } from '@/constants/permission';

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
  const [hasUserQueryPermission, setHasUserQueryPermission] = useState(true);

  useEffect(() => {
    if (visible) {
      form.resetFields();
      if (initialValues) {
        form.setFieldsValue({ ...initialValues });
      }
      
      // 检查是否有用户查询权限
      const userPermission = hasPermission(TENANT_USER_QUERY);
      setHasUserQueryPermission(userPermission);
      
      // 获取用户列表和部门树
      if (userPermission) {
        fetchUserList();
      }
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

  const filterTreeNode = (inputText: string, node: any) => {
    return node.props.title.toLowerCase().indexOf(inputText.toLowerCase()) > -1;
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
            filterTreeNode={filterTreeNode}
          />
        </FormItem>
        <FormItem label="管理员" field="leaderUserId">
          <Select
            placeholder={hasUserQueryPermission ? "请选择管理员" : "无权限"}
            allowClear
            showSearch
            disabled={!hasUserQueryPermission}
            filterOption={(input: string, option: any) =>
              option.props?.children?.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0
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
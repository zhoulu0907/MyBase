import { Form, Input, Modal, Select, TreeSelect, type FormInstance } from '@arco-design/web-react';
import React from 'react';

interface CreateModalProps {
  title: string;
  handleCreate: () => void;
  onCancel: () => void;
  form: FormInstance;
  pageTypeOptions: { label: string; value: any }[];
  visibleCreateForm: string;
  initValue: { pageType: number; menuName: string; parentCode: string };
  treeData: any[];
}

const CreateModal: React.FC<CreateModalProps> = ({
  title,
  handleCreate,
  onCancel,
  form,
  pageTypeOptions,
  visibleCreateForm,
  initValue,
  treeData
}) => {
  return (
    <Modal
      title={title}
      visible={visibleCreateForm !== ''}
      onOk={handleCreate}
      onCancel={onCancel}
      autoFocus={false}
      focusLock={true}
      unmountOnExit={true}
    >
      <Form
        layout="vertical"
        form={form}
        initialValues={{
          pageType: initValue.pageType,
          menuName: initValue.menuName,
          parentCode: initValue.parentCode
        }}
      >
        <Form.Item
          label="页面类型"
          field="pageType"
          hidden={visibleCreateForm === 'group'}
          rules={[{ required: true, message: '请选择页面类型' }]}
        >
          <Select options={pageTypeOptions} placeholder="请选择页面类型" allowClear />
        </Form.Item>

        <Form.Item
          label={visibleCreateForm === 'page' ? '页面名称' : '分组名称'}
          field="menuName"
          rules={[
            { required: true, message: '请输入页面名称' },
            { maxLength: 20, message: '页面名称不能超过20个字符' }
          ]}
        >
          <Input
            maxLength={20}
            placeholder="请输入页面名称，不超过20个字符"
            allowClear
            onChange={(value) => {
              form.setFieldValue('menuName', value);
            }}
          />
        </Form.Item>

        {/* TODO: 添加菜单图标 */}

        <Form.Item label="父级页面" field="parentCode">
          <TreeSelect treeData={treeData} placeholder="请选择父级页面" allowClear />
        </Form.Item>

        {/* TODO: 添加业务实体 */}
      </Form>
    </Modal>
  );
};

export default CreateModal;

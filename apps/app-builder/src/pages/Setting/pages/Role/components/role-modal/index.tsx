import { Form, Input, Message, Modal } from '@arco-design/web-react';
import type { RoleVO } from '@onebase/platform-center';
import { useCallback, useEffect } from 'react';

const FormItem = Form.Item;

interface RoleModalProps {
  visible: boolean;
  onCancel: () => void;
  onOk: (values: Partial<RoleVO>) => void;
  confirmLoading: boolean;
  initialValues?: RoleVO | null;
}

export default function RoleModal({ visible, onCancel, onOk, confirmLoading, initialValues }: RoleModalProps) {
  const [form] = Form.useForm();

  // 重置表单
  const resetForm = useCallback(() => {
    form.resetFields();
  }, [form]);

  // 设置表单初始值
  const setFormValues = useCallback(() => {
    if (initialValues) {
      form.setFieldsValue({
        name: initialValues.name,
        remark: initialValues.remark
      });
    } else {
      resetForm();
    }
  }, [initialValues, form, resetForm]);

  useEffect(() => {
    if (visible) {
      setFormValues();
    }
  }, [visible, setFormValues]);

  const handleConfirm = useCallback(async () => {
    try {
      const values = await form.validate();

      // 编辑时只传递必要的字段
      if (initialValues && initialValues.id) {
        onOk({
          id: initialValues.id,
          name: values.name,
          remark: values.remark
        });
      } else {
        onOk(values);
      }
    } catch (error) {
      console.error('表单验证失败:', error);
      Message.error('请检查表单输入');
    }
  }, [form, initialValues, onOk]);

  const handleCancel = useCallback(() => {
    resetForm();
    onCancel();
  }, [resetForm, onCancel]);

  const modalTitle = initialValues ? '编辑角色' : '新增角色';

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{modalTitle}</div>}
      visible={visible}
      onOk={handleConfirm}
      onCancel={handleCancel}
      confirmLoading={confirmLoading}
      okText="确定"
      cancelText="取消"
      maskClosable={false}
    >
      <Form form={form} layout="vertical" autoComplete="off">
        <FormItem
          label="角色名称"
          field="name"
          rules={[
            { required: true, message: '请输入角色名称' },
            { maxLength: 30, message: '角色名称不能超过50个字符' },
            {
              validator: (value, cb) => {
                if (value && value.trim().length === 0) {
                  cb('角色名称不能为空');
                }
                cb();
              }
            }
          ]}
        >
          <Input placeholder="请输入角色名称" maxLength={30} showWordLimit />
        </FormItem>
        <FormItem label="角色描述" field="remark" rules={[{ maxLength: 200, message: '角色描述不能超过200个字符' }]}>
          <Input.TextArea placeholder="请输入角色描述" rows={4} maxLength={200} showWordLimit />
        </FormItem>
      </Form>
    </Modal>
  );
}

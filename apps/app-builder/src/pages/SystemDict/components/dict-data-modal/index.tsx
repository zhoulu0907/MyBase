import { Form, Input, InputNumber, Modal, Switch } from '@arco-design/web-react';
import { type DictDataForm } from '@onebase/platform-center';
import { StatusEnum } from '@onebase/platform-center/src/types/common';
import { useEffect } from 'react';


interface DictionaryItemModalProps {
  visible: boolean;
  loading?: boolean;
  initialValues?: DictDataForm;
  onOk: (values: DictDataForm) => void;
  onCancel: () => void;
  title?: string;
}

export default function DictionaryItemModal({
  visible,
  loading,
  initialValues,
  onOk,
  onCancel,
  title = '新增字典项',
}: DictionaryItemModalProps) {
  const [form] = Form.useForm<DictDataForm>();

  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        label: initialValues?.label || '',
        dictType: initialValues?.dictType || '',
        remark: initialValues?.remark || '',
        sort: initialValues?.sort ?? 0,
        status: initialValues?.status ?? StatusEnum.ENABLE, // 使用 StatusEnum.ENABLE 作为默认值
      });
    } else {
      form.resetFields();
    }
  }, [visible, initialValues, form]);

  return (
    <Modal
      title={title}
      visible={visible}
      onOk={() => {
        form.validate().then(values => {
          onOk(values);
        });
      }}
      onCancel={onCancel}
      confirmLoading={loading}
      unmountOnExit
      autoFocus={false}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          label="选项名称"
          field="name"
          rules={[{ required: true, message: '请输入选项名称' }]}
        >
          <Input placeholder="请输入选项名称" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item
          label="选项编码"
          field="type"
          rules={[{ required: true, message: '请输入选项编码' }]}
        >
          <Input placeholder="请输入选项编码" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item label="描述" field="remark">
          <Input.TextArea placeholder="请输入描述" maxLength={100} allowClear />
        </Form.Item>
        <Form.Item
          label="显示顺序"
          field="sort"
          rules={[{ required: true, message: '请输入显示顺序' }]}
        >
          <InputNumber min={0} max={9999} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="是否启用" field="status" triggerPropName="checked">
          <Switch
            checkedText="启用"
            uncheckedText="停用"
            checked={form.getFieldValue('status') === StatusEnum.ENABLE}
            onChange={(checked) => form.setFieldValue('status', checked ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
}
import { Form, Input, InputNumber, Modal, Switch } from '@arco-design/web-react';
import { StatusEnum, type DictData } from '@onebase/platform-center';
import { useEffect, useState } from 'react';

interface DictionaryItemModalProps {
  visible: boolean;
  loading?: boolean;
  initialValues?: Partial<DictData>;
  onOk: (values: DictData) => void;
  onCancel: () => void;
  title?: string;
}

export default function DictionaryItemModal({
  visible,
  loading,
  initialValues,
  onOk,
  onCancel,
  title = '新增字典项'
}: DictionaryItemModalProps) {
  const [form] = Form.useForm<DictData>();
  const [statusCheckedValue, setStatusCheckedValue] = useState(false);

  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        label: initialValues?.label || '',
        value: initialValues?.value || '',
        dictType: initialValues?.dictType || '',
        remark: initialValues?.remark || '',
        sort: initialValues?.sort ?? 0
      });
      const isChecked = !initialValues || initialValues.status === StatusEnum.ENABLE ? true : false;
      setStatusCheckedValue(isChecked);
    }
  }, [visible, initialValues, form]);

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{title}</div>}
      visible={visible}
      onOk={() => {
        form.validate().then((values) => {
          const params = { ...values, status: statusCheckedValue ? StatusEnum.ENABLE : StatusEnum.DISABLE };
          onOk(params);
        });
      }}
      onCancel={onCancel}
      confirmLoading={loading}
      unmountOnExit
      autoFocus={false}
    >
      <Form form={form} layout="vertical">
        <Form.Item label="字典值" field="label" rules={[{ required: true, message: '请输入字典值' }]}>
          <Input placeholder="请输入字典值" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item label="字典值编码" field="value" rules={[{ required: true, message: '请输入字典值编码' }]}>
          <Input placeholder="请输入字典值编码" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item label="描述" field="remark">
          <Input.TextArea placeholder="请输入描述" maxLength={200} allowClear />
        </Form.Item>
        <Form.Item label="显示顺序" field="sort" rules={[{ required: true, message: '请输入显示顺序' }]}>
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>
        <Form.Item label="是否启用">
          <Switch checked={statusCheckedValue} onChange={setStatusCheckedValue} />
        </Form.Item>
      </Form>
    </Modal>
  );
}

import { Form, Input, Modal, Switch } from '@arco-design/web-react';
import { type DictItem } from '@onebase/platform-center';
import { StatusEnum } from '@onebase/platform-center';
import { useEffect } from 'react';

export interface DictForm {
  type: string;
  name: string;
  remark?: string;
  status: number;
}

interface DictionaryTypeModalProps {
  visible: boolean;
  loading?: boolean;
  initialValues?: Partial<DictItem>;
  onOk: (values: DictItem) => void;
  onCancel: () => void;
  title?: string;
}

export default function DictionaryTypeModal({
  visible,
  loading,
  initialValues,
  onOk,
  onCancel,
  title = '新增数据字典'
}: DictionaryTypeModalProps) {
  const [form] = Form.useForm<DictItem>();

  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        type: initialValues?.type || '',
        name: initialValues?.name || '',
        remark: initialValues?.remark || '',
        status: initialValues?.status ?? StatusEnum.DISABLE
      });
    } else {
      form.resetFields();
    }
  }, [visible, initialValues, form]);

  return (
    <Modal
      title={<div style={{ textAlign: 'left' }}>{title}</div>}
      visible={visible}
      onOk={() => {
        form.validate().then((values) => {
          onOk(values);
        });
      }}
      onCancel={onCancel}
      confirmLoading={loading}
      unmountOnExit
      autoFocus={false}
    >
      <Form form={form} layout="vertical">
        <Form.Item label="字典编码" field="type" rules={[{ required: true, message: '请输入字典编码' }]}>
          <Input placeholder="请输入字典编码" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item label="字典名称" field="name" rules={[{ required: true, message: '请输入字典名称' }]}>
          <Input placeholder="请输入字典名称" maxLength={32} allowClear />
        </Form.Item>
        <Form.Item label="描述" field="remark">
          <Input.TextArea placeholder="请输入描述" maxLength={100} allowClear />
        </Form.Item>
        <Form.Item label="是否启用" field="status" triggerPropName="checked">
          <Switch
            checked={form.getFieldValue('status') === StatusEnum.ENABLE}
            onChange={(checked) => form.setFieldValue('status', checked ? StatusEnum.ENABLE : StatusEnum.DISABLE)}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
}

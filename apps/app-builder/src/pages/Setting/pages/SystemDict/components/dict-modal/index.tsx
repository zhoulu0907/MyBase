import { Form, Input, Modal, Switch } from '@arco-design/web-react';
import { StatusEnum, type DictItem } from '@onebase/platform-center';
import { useEffect, useState } from 'react';

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
  const [statusCheckedValue, setStatusCheckedValue] = useState(false);

  useEffect(() => {
    if (visible) {
      form.setFieldsValue({
        type: initialValues?.type || '',
        name: initialValues?.name || '',
        remark: initialValues?.remark || ''
      });
      const isChecked = !initialValues || initialValues.status === StatusEnum.ENABLE ? true : false;
      setStatusCheckedValue(isChecked);
    }
  }, [visible, initialValues, form]);

  const rules = {
    type: [
      { required: true, message: '请输入字典编码' },
      {
        validator: (value: string | undefined, cb: (error?: React.ReactNode) => void) => {
          if (value && !/^[a-zA-Z0-9_]+$/.test(value)) {
            cb('请输入字母、数字或下划线');
          } else {
            cb();
          }
        },
        message: '请输入字母、数字或下划线'
      }
    ],
    name: [{ required: true, message: '请输入字典名称' }]
  };

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
        <Form.Item label="字典编码" field="type" rules={rules.type}>
          <Input placeholder="请输入字母、数字或下划线" maxLength={50} allowClear />
        </Form.Item>
        <Form.Item label="字典名称" field="name" rules={rules.name}>
          <Input placeholder="请输入字典名称" maxLength={30} allowClear />
        </Form.Item>
        <Form.Item label="字典描述" field="remark">
          <Input.TextArea placeholder="请输入字典描述" maxLength={100} allowClear />
        </Form.Item>
        <Form.Item label="是否启用" layout="horizontal" labelCol={{ span: 3.5 }}>
          <Switch checked={statusCheckedValue} onChange={setStatusCheckedValue} />
        </Form.Item>
      </Form>
    </Modal>
  );
}

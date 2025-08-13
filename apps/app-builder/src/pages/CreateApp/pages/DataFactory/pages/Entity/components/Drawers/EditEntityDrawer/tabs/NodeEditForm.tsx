import { type EntityField, type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Button, Form, Input, Switch } from '@arco-design/web-react';
import React, { useEffect } from 'react';
import styles from './NodeEditForm.module.less';

// 节点编辑表单组件
interface NodeEditFormProps {
  node: EntityNode;
  onSave: (data: Partial<FormValues>) => void;
  onCancel: () => void;
  successCallback?: () => void;
}

interface FormItem {
  field: string;
  label: string;
}

interface FormValues {
  code: string;
  name: string;
  description: string;
  systemFields: {
    creator: boolean;
    updater: boolean;
    created_time: boolean;
    updated_time: boolean;
    owner_id: boolean;
    owner_dept: boolean;
  };
}

const NodeEditForm: React.FC<NodeEditFormProps> = ({ node, onCancel, onSave, successCallback }) => {
  const [form] = Form.useForm<FormValues>();

  // 初始化表单数据
  const initialValues: FormValues = {
    code: node.code || '',
    name: node.entityName || '',
    description: node.description || '',
    systemFields: {
      creator: node?.fields.find((field: EntityField) => field.isSystemField === 0 && field.fieldName === 'creator')
        ? true
        : false,
      updater: node?.fields.find((field: EntityField) => field.isSystemField === 0 && field.fieldName === 'updater')
        ? true
        : false,
      created_time: node?.fields.find(
        (field: EntityField) => field.isSystemField === 0 && field.fieldName === 'created_time'
      )
        ? true
        : false,
      updated_time: node?.fields.find(
        (field: EntityField) => field.isSystemField === 0 && field.fieldName === 'updated_time'
      )
        ? true
        : false,
      owner_id: node?.fields.find((field: EntityField) => field.isSystemField === 0 && field.fieldName === 'owner_id')
        ? true
        : false,
      owner_dept: node?.fields.find(
        (field: EntityField) => field.isSystemField === 0 && field.fieldName === 'owner_dept'
      )
        ? true
        : false
    }
  };

  const formItems = [
    { field: 'systemFields.creator', label: '记录创建人' },
    { field: 'systemFields.updater', label: '记录更新人' },
    { field: 'systemFields.created_time', label: '记录创建时间' },
    { field: 'systemFields.updated_time', label: '记录更新时间' },
    { field: 'systemFields.owner_id', label: '记录数据拥有者' },
    { field: 'systemFields.owner_dept', label: '记录数据拥有部门' }
  ];

  const handleSwitchChange = (value: boolean, item: FormItem) => {
    console.log('handleSwitchChange item', value, item);
    // form.setFieldValue(item.field as keyof FormValues, value);
    form.setFieldValue('systemFields', {
      [item.field]: value
    } as FormValues['systemFields']);
  };

  const handleDelete = () => {
    console.log('handleDelete data', node.entityId);
  };

  useEffect(() => {
    console.log('useEffect initialValues', initialValues);
    form.setFieldsValue(initialValues);
  }, [node]);

  return (
    <div className={styles['node-edit-form']}>
      <h3>业务实体</h3>

      <Form form={form} initialValues={initialValues} layout="vertical" style={{ width: '100%' }}>
        {/* 基本设置 */}
        <div className={styles['form-section']}>
          <h4>基本设置</h4>

          <Form.Item
            label="业务实体编码"
            field="code"
            rules={[
              { required: true, message: '请输入业务实体编码' },
              { max: 40, message: '业务实体编码不能超过40个字符' }
            ]}
          >
            <Input placeholder="请输入业务实体编码" maxLength={40} readOnly />
          </Form.Item>

          <Form.Item
            label="业务实体名称"
            field="name"
            rules={[
              { required: true, message: '请输入业务实体名称' },
              { max: 50, message: '业务实体名称不能超过50个字符' }
            ]}
          >
            <Input placeholder="请输入业务实体名称" maxLength={50} />
          </Form.Item>

          <Form.Item label="业务实体描述" field="description">
            <Input.TextArea placeholder="请输入描述 (选填)" rows={4} maxLength={500} showWordLimit />
          </Form.Item>
        </div>

        {/* 系统字段 */}
        <div className={styles['form-section']}>
          <h4>系统字段</h4>

          {formItems.length &&
            formItems.map((item) => (
              <Form.Item field={item.field} key={item.field}>
                <div className={styles['switch-item']}>
                  <span>{item.label}</span>
                  <Switch
                    onChange={(value: boolean) => handleSwitchChange(value, item)}
                    checked={form.getFieldValue(item.field as keyof FormValues) as unknown as boolean}
                  />
                </div>
              </Form.Item>
            ))}
        </div>

        <Form.Item className={styles['form-actions']}>
          {/* 更换为删除按钮 */}
          <Button type="text" status="danger" onClick={() => handleDelete()}>
            删除
          </Button>
          {/* <Button onClick={onCancel} style={{ marginRight: 16 }}>
            取消
          </Button>
          <Button
            type="primary"
            onClick={() => {
              onSave(form.getFieldsValue());
              successCallback?.();
            }}
          >
            保存
          </Button> */}
        </Form.Item>
      </Form>
    </div>
  );
};

export default NodeEditForm;

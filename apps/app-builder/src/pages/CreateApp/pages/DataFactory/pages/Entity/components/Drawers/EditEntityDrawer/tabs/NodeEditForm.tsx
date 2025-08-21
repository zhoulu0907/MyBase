import { type EntityField, type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { Button, Form, Input, Grid } from '@arco-design/web-react';
import { IconCheck } from '@arco-design/web-react/icon';
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
      creator: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'creator'
      )
        ? true
        : false,
      updater: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'updater'
      )
        ? true
        : false,
      created_time: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'created_time'
      )
        ? true
        : false,
      updated_time: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'updated_time'
      )
        ? true
        : false,
      owner_id: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'owner_id'
      )
        ? true
        : false,
      owner_dept: node?.fields.find(
        (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'owner_dept'
      )
        ? true
        : false
    }
  };

  const formItems: FormItem[] = [
    { field: 'systemFields.creator', label: '记录创建人' },
    { field: 'systemFields.updater', label: '记录更新人' },
    { field: 'systemFields.created_time', label: '记录创建时间' },
    { field: 'systemFields.updated_time', label: '记录更新时间' },
    { field: 'systemFields.owner_id', label: '记录数据拥有者' },
    { field: 'systemFields.owner_dept', label: '记录数据拥有部门' }
  ];

  const handleDelete = () => {
    console.log('handleDelete data', node.entityId);
  };

  useEffect(() => {
    console.log('useEffect initialValues', initialValues);
    form.setFieldsValue(initialValues);
  }, [node]);

  return (
    <div className={styles.nodeEditForm}>
      <div className={styles.header}>
        <h3>业务实体</h3>
      </div>

      <Form form={form} initialValues={initialValues} layout="vertical" style={{ width: '100%' }}>
        {/* 基本设置 */}
        <div className={styles.formSection}>
          <h4 className={styles.formSectionTitle}>基本设置</h4>

          <Form.Item
            label="业务实体编码"
            field="code"
            rules={[
              { required: true, message: '请输入业务实体编码' },
              { max: 40, message: '业务实体编码不能超过40个字符' }
            ]}
          >
            <Input placeholder="请输入业务实体编码" maxLength={40} disabled />
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
        <div className={styles.formSection}>
          <h4 className={styles.formSectionTitle}>系统字段</h4>

          <Grid.Row gutter={16}>
            {formItems.length &&
              formItems.map((item) => (
                <Grid.Col span={12} key={item.field}>
                  <Form.Item field={item.field} key={item.field}>
                    <div className={styles.checkboxItem}>
                      {/* 后续改为可编辑单选框 */}
                      {/* <Checkbox
                        checked={form.getFieldValue(item.field as keyof FormValues) as unknown as boolean}
                        disabled
                        className={styles.systemFieldCheckbox}
                      >
                        {item.label}
                      </Checkbox> */}
                      <IconCheck className={styles.iconCheck} />
                      {item.label}
                    </div>
                  </Form.Item>
                </Grid.Col>
              ))}
          </Grid.Row>
        </div>

        <Form.Item className={styles.formActions}>
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

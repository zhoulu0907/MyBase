import { type EntityField, type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { FIELD_TYPE } from '@onebase/ui-kit';
import { Button, Form, Input, Grid, Space, Message } from '@arco-design/web-react';
import { IconCheck } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { createEntityRules } from '@/pages/CreateApp/pages/DataFactory/utils/rules';
import { deleteEntity } from '@onebase/app';
import { DeleteConfirmModal } from '../../../Modals';
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
  id: string;
  code: string;
  tableName: string;
  displayName: string;
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
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // 初始化表单数据
  const getInitialValues = (node: EntityNode) => {
    console.log('getInitialValues', node);
    return {
      id: node.entityId || node.id || '',
      code: node.code || '',
      tableName: node.tableName || '',
      displayName: node.entityName || node.displayName || '',
      description: node.description || '',
      systemFields: {
        creator: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'creator'
        )
          ? true
          : false,
        updater: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'updater'
        )
          ? true
          : false,
        created_time: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'created_time'
        )
          ? true
          : false,
        updated_time: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'updated_time'
        )
          ? true
          : false,
        owner_id: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'owner_id'
        )
          ? true
          : false,
        owner_dept: node?.fields?.find(
          (field: EntityField) => field.isSystemField === FIELD_TYPE.SYSTEM && field.fieldName === 'owner_dept'
        )
          ? true
          : false
      }
    };
  };

  const formItems: FormItem[] = [
    { field: 'systemFields.creator', label: '记录创建人' },
    { field: 'systemFields.updater', label: '记录更新人' },
    { field: 'systemFields.created_time', label: '记录创建时间' },
    { field: 'systemFields.updated_time', label: '记录更新时间' },
    { field: 'systemFields.owner_id', label: '记录数据拥有者' },
    { field: 'systemFields.owner_dept', label: '记录数据拥有部门' }
  ];

  const openDeleteModal = () => {
    setDeleteModalVisible(true);
  };

  const handleDelete = async () => {
    setDeleteLoading(true);

    try {
      const res = await deleteEntity(node.id || node.entityId);
      if (res) {
        Message.success('删除成功');
        onCancel();
        successCallback?.();
      }
    } catch (error) {
      console.error('删除失败:', error);
    } finally {
      setDeleteLoading(false);
      setDeleteModalVisible(false);
    }
  };

  useEffect(() => {
    const initialValues = getInitialValues(node);
    form.setFieldsValue(initialValues);
  }, [node]);

  return (
    <div className={styles.nodeEditForm}>
      <div className={styles.header}>
        <h3>业务实体</h3>
      </div>

      <Form form={form} layout="vertical" className={styles.editForm}>
        {/* 基本设置 */}
        <div className={styles.formSection}>
          <h4 className={styles.formSectionTitle}>基本设置</h4>

          <Form.Item label="业务实体名称" field="tableName" rules={[...createEntityRules.tableName]}>
            <Input placeholder="请输入业务实体名称" maxLength={40} disabled />
          </Form.Item>

          <Form.Item label="业务展示名称" field="displayName" rules={[...createEntityRules.displayName]}>
            <Input placeholder="请输入业务展示名称" maxLength={50} />
          </Form.Item>

          <Form.Item label="业务实体描述" field="description" rules={[...createEntityRules.description]}>
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
      </Form>

      <div className={styles.formActions}>
        <Button type="text" status="danger" onClick={() => openDeleteModal()}>
          删除
        </Button>
        <Space>
          <Button onClick={onCancel}>取消</Button>
          <Button
            type="primary"
            onClick={() => {
              onSave({ ...form.getFieldsValue(), id: node.id || node.entityId });
              successCallback?.();
            }}
          >
            保存
          </Button>
        </Space>
      </div>

      <DeleteConfirmModal
        content="确定要删除这个业务实体吗？删除后无法恢复。"
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={handleDelete}
        confirmLoading={deleteLoading}
      />
    </div>
  );
};

export default NodeEditForm;

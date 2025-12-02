import React, { useEffect, useState } from 'react';
import { useAppStore } from '@/store/store_app';
import { Button, Drawer, Form, Input, Message, Select, Space, Spin } from '@arco-design/web-react';
import { deleteField, getFieldById, updateField } from '@onebase/app';
import { ENTITY_FIELD_TYPE, FIELD_TYPE } from '@onebase/ui-kit';
import { DeleteConfirmModal } from '../../Modals';
import styles from './EditFieldDrawer.module.less';

interface FieldDetail {
  id: string;
  fieldCode: string;
  fieldName: string;
  description: string;
  fieldType: string;
  defaultValue: string;
  isUnique: boolean;
  isRequired: boolean;
  constraints: string;
  isSystemField: number;
  entityId: string;
  entityName: string;
  applicationId: string;
  displayName: string;
}

interface EditFieldDrawerProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  fieldId: string;
  onSuccess?: () => void;
}

const EditFieldDrawer: React.FC<EditFieldDrawerProps> = ({ visible, setVisible, fieldId, onSuccess }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm();
  const [fieldDetail, setFieldDetail] = useState<FieldDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // 字段类型选项
  const fieldTypeOptions = Object.entries(ENTITY_FIELD_TYPE).map(([key, value]) => ({
    label: value.LABEL as string,
    value: key
  }));

  useEffect(() => {
    if (visible && fieldId) {
      fetchFieldDetail();
    }
  }, [visible, fieldId]);

  // 获取字段详情
  const fetchFieldDetail = async () => {
    try {
      setLoading(true);
      const response = await getFieldById(fieldId);
      console.log('getFieldById', response);

      if (response) {
        setFieldDetail(response);
        form.setFieldsValue(response);
      }
    } catch (error) {
      console.error('获取字段详情失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 表单验证规则
  const validateFieldCode = async (value: string | undefined, callback: (error?: React.ReactNode) => void) => {
    if (!value) {
      callback('请输入字段编码');
      return;
    }
    if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(value)) {
      callback('字段编码只能包含字母、数字和下划线，且必须以字母开头');
      return;
    }
    callback();
  };

  const validateFieldName = (value: string | undefined, callback: (error?: React.ReactNode) => void) => {
    if (!value) {
      callback('请输入字段名称');
      return;
    }
    if (value.length > 50) {
      callback('字段名称不能超过50个字符');
      return;
    }
    callback();
  };

  const validateDisplayName = (value: string | undefined, callback: (error?: React.ReactNode) => void) => {
    if (!value) {
      callback('请输入展示名称');
      return;
    }
    callback();
    if (value.length > 50) {
      callback('展示名称不能超过50个字符');
      return;
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      setSubmitting(true);

      if (!fieldDetail) {
        Message.error('字段信息不存在');
        return;
      }

      const updateData = {
        applicationId: curAppId,
        id: fieldDetail.id,
        entityId: fieldDetail.entityId,
        fieldCode: values.fieldCode,
        fieldName: values.fieldName,
        description: values.description,
        fieldType: values.fieldType,
        isSystemField: fieldDetail.isSystemField,
        displayName: values.displayName
      };

      await updateField(updateData);
      Message.success('更新字段成功');
      setVisible(false);
      onSuccess?.();
    } catch (error) {
      console.error('更新字段失败:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const openDeleteModal = () => {
    setDeleteModalVisible(true);
  };

  const handleDelete = async () => {
    setDeleteLoading(true);

    try {
      const res = await deleteField(fieldDetail?.id || '');
      if (res) {
        Message.success('删除成功');
        onSuccess?.();
      }
    } catch (error) {
      console.error('删除字段失败:', error);
    } finally {
      setDeleteModalVisible(false);
      setVisible(false);
      setDeleteLoading(false);
    }
  };

  return (
    <>
      <Drawer
        title="数据字段配置"
        visible={visible}
        onCancel={() => setVisible(false)}
        width={500}
        mask={false}
        className={styles['edit-field-drawer']}
        footer={
          <>
            {/* 本期不支持删除 */}
            <Button type="text" status="danger" onClick={() => openDeleteModal()} disabled>
              删除
            </Button>
            <Space>
              <Button onClick={() => setVisible(false)}>取消</Button>
              <Button type="primary" loading={submitting} onClick={handleSubmit}>
                确定
              </Button>
            </Space>
          </>
        }
      >
        {loading ? (
          <div className={styles['loading-container']}>
            <Spin size={40} />
            <p>加载中...</p>
          </div>
        ) : fieldDetail ? (
          <Form form={form} layout="vertical" className={styles['edit-form']}>
            {/* 基本信息 */}
            <div className={styles['section']}>
              <h3 className={styles['form-section-title']}>基本设置</h3>

              <Form.Item
                label="字段名称"
                field="fieldName"
                rules={[{ required: true, message: '请输入字段名称' }, { validator: validateFieldName }]}
              >
                <Input placeholder="请输入字段名称" disabled />
              </Form.Item>

              <Form.Item
                label="展示名称"
                field="displayName"
                rules={[{ required: true, message: '请输入展示名称' }, { validator: validateDisplayName }]}
              >
                <Input placeholder="请输入展示名称" />
              </Form.Item>

              <Form.Item label="数据类型" field="fieldType" rules={[{ required: true, message: '请选择数据类型' }]}>
                <Select placeholder="请选择数据类型" options={fieldTypeOptions} disabled />
              </Form.Item>

              <Form.Item label="字段描述" field="description">
                <Input.TextArea placeholder="请输入字段描述" rows={3} />
              </Form.Item>

              <Form.Item label="默认值" field="defaultValue">
                <Input placeholder="请输入默认值" />
              </Form.Item>
            </div>

            {/* 字段属性 -- 暂时隐藏 */}
            {/* <div className={styles['section']}>
              <h3 className={styles['form-section-title']}>字段属性</h3>

              <Form.Item label="默认值" field="defaultValue">
                <Input placeholder="请输入默认值" />
              </Form.Item>

              <Form.Item label="唯一性" field="isUnique" triggerPropName="checked">
                <Checkbox>设置为唯一字段</Checkbox>
              </Form.Item>

              <Form.Item label="空值约束" field="isRequired" triggerPropName="checked">
                <Checkbox>必填</Checkbox>
              </Form.Item>

              <Form.Item label="字段约束" field="constraints">
                <Input.TextArea placeholder="请输入字段约束" rows={2} />
              </Form.Item>
            </div> */}

            {/* 字段信息 -- 暂时隐藏 */}
            {/* <div className={styles['section']}>
              <h3 className={styles['form-section-title']}>字段信息</h3>

              <div className={styles['info-item']}>
                <span className={styles['info-label']}>字段类型：</span>
                <span className={styles['info-value']}>
                  {FIELD_TYPE_LABEL[fieldDetail.isSystemField as keyof typeof FIELD_TYPE_LABEL]}
                </span>
              </div>

              <div className={styles['info-item']}>
                <span className={styles['info-label']}>所属资产：</span>
                <span className={styles['info-value']}>{fieldDetail.entityName}</span>
              </div>

              <div className={styles['info-item']}>
                <span className={styles['info-label']}>资产ID：</span>
                <span className={styles['info-value']}>{fieldDetail.entityId}</span>
              </div>
            </div> */}
          </Form>
        ) : (
          <div className={styles['empty-container']}>
            <p>未找到字段信息</p>
          </div>
        )}
      </Drawer>

      <DeleteConfirmModal
        content="确定要删除这个字段吗？删除后无法恢复。"
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={handleDelete}
        confirmLoading={deleteLoading}
      />
    </>
  );
};

export default EditFieldDrawer;

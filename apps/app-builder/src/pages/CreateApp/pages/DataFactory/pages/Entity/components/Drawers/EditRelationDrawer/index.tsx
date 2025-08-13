import { resouceId } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { Button, Drawer, Form, Message, Select, Space, Spin } from '@arco-design/web-react';
import { getEntityFields, getEntityList, updateRelation } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import type { EdgeData } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { useAppStore } from '@/store';

interface EntityOption {
  label: string;
  value: string;
}

interface FieldOption {
  label: string;
  value: string;
}

interface RelationFormValues {
  sourceEntityId: string;
  sourceFieldId: string;
  relationshipType: string;
  targetEntityId: string;
  targetFieldId: string;
}

interface EditRelationDrawerProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  relationData?: EdgeData; // 关联关系数据
  onSuccess?: () => void;
}

// 关联关系类型选项
const relationTypes = [
  { label: '一对一', value: 'ONE_TO_ONE' },
  { label: '一对多', value: 'ONE_TO_MANY' },
  { label: '多对一', value: 'MANY_TO_ONE' },
  { label: '多对多', value: 'MANY_TO_MANY' }
];

const EditRelationDrawer: React.FC<EditRelationDrawerProps> = ({ visible, setVisible, relationData, onSuccess }) => {
  const { curAppId } = useAppStore();
  const [form] = Form.useForm<RelationFormValues>();
  const [leftEntityOptions, setLeftEntityOptions] = useState<EntityOption[]>([]);
  const [rightEntityOptions, setRightEntityOptions] = useState<EntityOption[]>([]);
  const [leftFieldOptions, setLeftFieldOptions] = useState<FieldOption[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<FieldOption[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // 初始化实体选项
  useEffect(() => {
    if (visible) {
      loadEntities();
    }
  }, [visible]);

  // 当关联关系数据变化时，设置表单值
  useEffect(() => {
    if (visible && relationData) {
      form.setFieldsValue({
        sourceEntityId: relationData.source.cell,
        sourceFieldId: relationData.source.port,
        relationshipType: relationData.label,
        targetEntityId: relationData.target.cell,
        targetFieldId: relationData.target.port
      });

      // 加载对应的字段选项
      if (relationData.source.cell) {
        loadFields(relationData.source.cell, 'left');
      }
      if (relationData.target.cell) {
        loadFields(relationData.target.cell, 'right');
      }
    }
  }, [visible, relationData]);

  const loadEntities = async () => {
    try {
      const res = await getEntityList(resouceId);
      if (res.length > 0) {
        const entityOptions = res.map((entity: any) => ({
          label: entity.displayName,
          value: entity.id
        }));
        setLeftEntityOptions(entityOptions);
        setRightEntityOptions(entityOptions);
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
      Message.error('加载实体列表失败');
    }
  };

  const loadFields = async (entityId: string, side: 'left' | 'right') => {
    try {
      setLoading(true);
      const res = await getEntityFields({ entityId });
      if (res.length > 0) {
        const fieldOptions = res.map((field: any) => ({
          label: field.fieldName,
          value: field.id
        }));

        if (side === 'left') {
          setLeftFieldOptions(fieldOptions);
        } else {
          setRightFieldOptions(fieldOptions);
        }
      }
    } catch (error) {
      console.error('加载字段列表失败:', error);
      Message.error('加载字段列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 处理实体选择变化
  const handleEntityChange = (entityId: string, side: 'left' | 'right') => {
    if (entityId) {
      loadFields(entityId, side);
    } else {
      if (side === 'left') {
        setLeftFieldOptions([]);
        form.setFieldValue('sourceFieldId', undefined);
      } else {
        setRightFieldOptions([]);
        form.setFieldValue('targetFieldId', undefined);
      }
    }
  };

  // 提交表单
  const handleSubmit = async () => {
    try {
      const values = await form.validate();
      setSubmitting(true);

      const updateData = {
        id: relationData?.relationshipId,
        ...values,
        appId: curAppId
      };

      await updateRelation(updateData);
      Message.success('关联关系更新成功');
      setVisible(false);
      onSuccess?.();
    } catch (error) {
      console.error('更新关联关系失败:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const handleClose = () => {
    form.resetFields();
    setVisible(false);
  };

  return (
    <Drawer
      title="关联关系配置"
      visible={visible}
      onCancel={handleClose}
      width={500}
      footer={
        <Space>
          <Button onClick={handleClose}>取消</Button>
          <Button type="primary" loading={submitting} onClick={handleSubmit}>
            保存
          </Button>
        </Space>
      }
      className={styles['edit-relation-drawer']}
    >
      <div className={styles.content}>
        <div className={styles.section}>
          <h4>基本设置</h4>

          <Form form={form} layout="vertical" className={styles.form}>
            <Form.Item
              label="左关联表"
              field="sourceEntityId"
              required
              rules={[{ required: true, message: '请选择左关联表' }]}
            >
              <Select
                placeholder="请选择左关联表"
                options={leftEntityOptions}
                onChange={(value) => handleEntityChange(value, 'left')}
                allowClear
              />
            </Form.Item>

            <Form.Item
              label="左关联字段"
              field="sourceFieldId"
              required
              rules={[{ required: true, message: '请选择左关联字段' }]}
            >
              <Select
                placeholder="请选择字段"
                options={leftFieldOptions}
                allowClear
                disabled={!form.getFieldValue('sourceEntityId')}
              />
            </Form.Item>

            <Form.Item
              label="关联关系"
              field="relationshipType"
              required
              rules={[{ required: true, message: '请选择关联关系' }]}
            >
              <Select placeholder="请选择关联关系" options={relationTypes} allowClear />
            </Form.Item>

            {/* 右关联表 */}
            <Form.Item
              label="右关联表"
              field="targetEntityId"
              required
              rules={[{ required: true, message: '请选择右关联表' }]}
            >
              <Select
                placeholder="请选择右关联表"
                options={rightEntityOptions}
                onChange={(value) => handleEntityChange(value, 'right')}
                allowClear
              />
            </Form.Item>

            <Form.Item
              label="右关联字段"
              field="targetFieldId"
              required
              rules={[{ required: true, message: '请选择右关联字段' }]}
            >
              <Select
                placeholder="请选择字段"
                options={rightFieldOptions}
                allowClear
                disabled={!form.getFieldValue('targetEntityId')}
              />
            </Form.Item>
          </Form>
        </div>
      </div>

      {loading && (
        <div className={styles.loading}>
          <Spin size={24} />
          <span>加载中...</span>
        </div>
      )}
    </Drawer>
  );
};

export default EditRelationDrawer;

import { useAppStore } from '@/store/store_app';
import { useResourceStore } from '@/store/store_resource';
import { Button, Drawer, Form, Message, Select, Space, Spin } from '@arco-design/web-react';
import { deleteRelation, getEntityFields, getEntityList, updateRelation } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import { DeleteConfirmModal } from '../../Modals';
interface EntityOption {
  label: string;
  value: string;
}

interface FieldOption {
  label: string;
  value: string;
}

interface RelationFormValues {
  sourceEntityId?: string;
  sourceFieldId: string;
  relationshipType: string;
  targetEntityId?: string;
  targetFieldId?: string;
  relationName?: string;
  id?: string;
  target?: { cell: string; port: string };
  source?: { cell: string; port: string };
}

interface EditRelationDrawerProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  relationData?: RelationFormValues; // 关联关系数据
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
  const { curDataSourceId } = useResourceStore();
  const [form] = Form.useForm<RelationFormValues>();
  const [leftEntityOptions, setLeftEntityOptions] = useState<EntityOption[]>([]);
  const [rightEntityOptions, setRightEntityOptions] = useState<EntityOption[]>([]);
  const [leftFieldOptions, setLeftFieldOptions] = useState<FieldOption[]>([]);
  const [rightFieldOptions, setRightFieldOptions] = useState<FieldOption[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  // 初始化资产选项
  useEffect(() => {
    if (visible && curDataSourceId) {
      loadEntities();
    }
  }, [visible, curDataSourceId]);

  // 当关联关系数据变化时，设置表单值
  useEffect(() => {
    console.log('relationData=====', relationData);
    if (visible && relationData) {
      form.setFieldsValue({
        sourceEntityId: relationData.source?.cell || relationData.sourceEntityId,
        sourceFieldId: relationData.source?.port || relationData.sourceFieldId,
        relationshipType: relationData.relationshipType,
        targetEntityId: relationData.target?.cell || relationData.targetEntityId,
        targetFieldId: relationData.target?.port || relationData.targetFieldId
      });
    }
  }, [visible, relationData]);

  const loadEntities = async () => {
    try {
      const res = await getEntityList(curDataSourceId);
      if (res.length > 0) {
        const entityOptions = res.map((entity: any) => ({
          label: entity.displayName,
          value: entity.id
        }));
        setLeftEntityOptions(entityOptions);
        setRightEntityOptions(entityOptions.filter((item: EntityOption) => item.value !== relationData?.source?.cell));
        handleEntityChange(relationData?.source?.cell || relationData?.sourceEntityId || '', 'left');
        handleEntityChange(relationData?.target?.cell || relationData?.targetEntityId || '', 'right');
      }
    } catch (error) {
      console.error('加载资产列表失败:', error);
    }
  };

  const loadFields = async (entityId: string, side: 'left' | 'right') => {
    try {
      setLoading(true);
      const res = await getEntityFields({ entityId });
      if (res.length > 0) {
        const fieldOptions = res.map((field: any) => ({
          label: field.displayName,
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
    } finally {
      setLoading(false);
    }
  };

  // 处理资产选择变化
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
        id: relationData?.id,
        ...values,
        appId: curAppId
      };

      await updateRelation(updateData);
      Message.success('关联关系更新成功');
      setVisible(false);
      handleClose();
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

  const openDeleteModal = () => {
    setDeleteModalVisible(true);
  };

  const handleDelete = async () => {
    setDeleteLoading(true);

    try {
      const res = await deleteRelation(relationData?.id || '');

      setDeleteModalVisible(false);
      if (res) {
        Message.success('删除成功');
        handleClose();
        onSuccess?.();
      }
    } catch (error) {
      console.error('删除关联关系失败:', error);
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <Drawer
      title="关联关系配置"
      visible={visible}
      onCancel={handleClose}
      width={500}
      mask={false}
      footer={
        <div className={styles.footer}>
          {/* 本期不支持删除 */}
          <Button type="text" status="danger" onClick={() => openDeleteModal()} style={{ float: 'left' }} disabled>
            删除
          </Button>
          <Space>
            <Button onClick={handleClose}>取消</Button>
            <Button type="primary" loading={submitting} onClick={handleSubmit}>
              确定
            </Button>
          </Space>
        </div>
      }
      className={styles['edit-relation-drawer']}
    >
      <div className={styles.content}>
        <h4 className={styles.formSectionTitle}>基本设置</h4>

        <Form form={form} layout="vertical" className={styles.form}>
          <Form.Item label="主表" field="sourceEntityId" required rules={[{ required: true, message: '请选择主表' }]}>
            <Select
              placeholder="请选择主表"
              options={leftEntityOptions}
              onChange={(value) => handleEntityChange(value, 'left')}
              disabled
            />
          </Form.Item>

          <Form.Item
            label="主表字段"
            field="sourceFieldId"
            required
            rules={[{ required: true, message: '请选择主表字段' }]}
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

          {/* 关联表 */}
          <Form.Item
            label="关联表"
            field="targetEntityId"
            required
            rules={[{ required: true, message: '请选择关联表' }]}
          >
            <Select
              placeholder="请选择关联表"
              options={rightEntityOptions}
              onChange={(value) => handleEntityChange(value, 'right')}
              allowClear
            />
          </Form.Item>

          <Form.Item
            label="关联字段"
            field="targetFieldId"
            required
            rules={[{ required: true, message: '请选择关联字段' }]}
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

      {loading && (
        <div className={styles.loading}>
          <Spin size={24} />
          <span>加载中...</span>
        </div>
      )}

      <DeleteConfirmModal
        content="确定要删除这个关联关系吗？删除后无法恢复。"
        visible={deleteModalVisible}
        onVisibleChange={setDeleteModalVisible}
        onConfirm={handleDelete}
        confirmLoading={deleteLoading}
      />
    </Drawer>
  );
};

export default EditRelationDrawer;

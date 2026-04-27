import { Descriptions, Drawer, Empty, Spin, Tag } from '@arco-design/web-react';
import { getFieldById } from '@onebase/app';
import { ENTITY_FIELD_TYPE, FIELD_TYPE, FIELD_TYPE_LABEL } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from './FieldDetailDrawer.module.less';

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
  createTime: string;
  updateTime: string;
  createBy: string;
  updateBy: string;
}

interface FieldDetailDrawerProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  fieldId: string;
}

const FieldDetailDrawer: React.FC<FieldDetailDrawerProps> = ({ visible, setVisible, fieldId }) => {
  const [fieldDetail, setFieldDetail] = useState<FieldDetail | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (visible && fieldId) {
      fetchFieldDetail();
    }
  }, [visible, fieldId]);

  const fetchFieldDetail = async () => {
    try {
      setLoading(true);
      const response = await getFieldById(fieldId);
      console.log('getFieldById', response);
      setFieldDetail(response);
    } catch (error) {
      console.error('获取字段详情失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const getFieldTypeLabel = (fieldType: string) => {
    return ENTITY_FIELD_TYPE[fieldType as keyof typeof ENTITY_FIELD_TYPE].LABEL || fieldType;
  };
  const renderFieldTypeTag = (fieldType: string) => {
    const label = getFieldTypeLabel(fieldType);
    let color = 'blue';

    // 根据字段类型设置不同的颜色
    if (fieldType === 'TEXT' || fieldType === 'LONG_TEXT') {
    } else if (fieldType === 'NUMBER' || fieldType === 'CURRENCY') {
      color = 'green';
    } else if (fieldType === 'DATE' || fieldType === 'DATETIME') {
      color = 'orange';
    } else if (fieldType === 'PICKLIST' || fieldType === 'MULTI_PICKLIST') {
      color = 'purple';
    } else if (fieldType === 'AUTO_CODE') {
      color = 'red';
    } else if (fieldType === 'BOOLEAN') {
      color = 'cyan';
    }

    return <Tag color={color}>{label}</Tag>;
  };

  const renderSystemFieldTag = (isSystemField: number) => {
    return (
      <Tag color={isSystemField === FIELD_TYPE.SYSTEM ? 'red' : 'green'}>
        {FIELD_TYPE_LABEL[isSystemField as keyof typeof FIELD_TYPE_LABEL]}
      </Tag>
    );
  };

  const renderUniqueTag = (isUnique: boolean) => {
    if (isUnique) {
      return <Tag color="red">唯一</Tag>;
    }
    return <Tag color="gray">非唯一</Tag>;
  };

  const renderisRequiredTag = (isRequired: boolean) => {
    if (isRequired) {
      return <Tag color="orange">允许空值</Tag>;
    }
    return <Tag color="red">不允许空值</Tag>;
  };

  return (
    <Drawer
      title="字段详情"
      visible={visible}
      onCancel={() => setVisible(false)}
      width={400}
      className={styles['field-detail-drawer']}
      footer={null}
    >
      {loading ? (
        <div className={styles['loading-container']}>
          <Spin size={40} />
          <p>加载中...</p>
        </div>
      ) : fieldDetail ? (
        <div className={styles['field-detail-content']}>
          {/* 基本信息 */}
          <div className={styles['section']}>
            <h3 className={styles['section-title']}>基本信息</h3>
            <Descriptions
              column={1}
              border
              data={[
                { label: '字段编码', value: fieldDetail.fieldCode },
                { label: '字段名称', value: fieldDetail.fieldName },
                { label: '字段描述', value: fieldDetail.description || '-' },
                { label: '数据类型', value: renderFieldTypeTag(fieldDetail.fieldType) },
                { label: '字段类型', value: renderSystemFieldTag(fieldDetail.isSystemField) }
              ]}
            />
          </div>

          {/* 字段属性 */}
          <div className={styles['section']}>
            <h3 className={styles['section-title']}>字段属性</h3>
            <Descriptions
              column={1}
              border
              data={[
                { label: '默认值', value: fieldDetail.defaultValue || '-' },
                { label: '唯一性', value: renderUniqueTag(fieldDetail.isUnique) },
                { label: '空值约束', value: renderisRequiredTag(fieldDetail.isRequired) },
                { label: '字段约束', value: fieldDetail.constraints || '-' }
              ]}
            />
          </div>

          {/* 所属实体 */}
          <div className={styles['section']}>
            <h3 className={styles['section-title']}>所属实体</h3>
            <Descriptions
              column={1}
              border
              data={[
                { label: '实体名称', value: fieldDetail.entityName },
                { label: '实体ID', value: fieldDetail.entityId }
              ]}
            />
          </div>

          {/* 时间信息 */}
          <div className={styles['section']}>
            <h3 className={styles['section-title']}>时间信息</h3>
            <Descriptions
              column={1}
              border
              data={[
                { label: '创建时间', value: fieldDetail.createTime },
                { label: '更新时间', value: fieldDetail.updateTime },
                { label: '创建人', value: fieldDetail.createBy },
                { label: '更新人', value: fieldDetail.updateBy }
              ]}
            />
          </div>
        </div>
      ) : (
        <div className={styles['empty-container']}>
          <Empty description="未找到字段信息" />
        </div>
      )}
    </Drawer>
  );
};

export default FieldDetailDrawer;

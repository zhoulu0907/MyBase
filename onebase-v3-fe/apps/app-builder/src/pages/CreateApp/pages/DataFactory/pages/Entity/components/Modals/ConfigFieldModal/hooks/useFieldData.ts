import { useState, useEffect, useCallback } from 'react';
import { getEntityFields, getEntityFieldsWithChildren } from '@onebase/app';
import { FIELD_TYPE } from '@onebase/ui-kit';
import type { FieldFormValues, FieldDataManager } from '../types';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';

// 数据管理hook（加载、状态管理）
export const useFieldData = (
  visible: boolean,
  entity: Partial<EntityNode>,
  onSuccess?: () => void
): FieldDataManager => {
  const [fields, setFields] = useState<FieldFormValues[]>([]);
  const [originFields, setOriginFields] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // 计算活跃字段（未删除且非系统字段）
  const activeFields = fields.filter((field) => !field.isDeleted && field.isSystemField === FIELD_TYPE.CUSTOM);

  // 加载资产字段
  const loadEntityFields = useCallback(async () => {
    if (!entity?.entityId) return;

    try {
      const res = await getEntityFields({ entityId: entity.entityId });
      console.log('getEntityFields', res);
      const fieldsData = res.map((field: any, index: number) => ({
        ...field,
        sortOrder: index
      }));
      setFields(fieldsData);
    } catch (error) {
      console.error('加载资产字段失败:', error);
    }
  }, [entity?.entityId]);

  // 加载资产及子表字段
  const loadEntityFieldsWithChildren = useCallback(async () => {
    if (!entity?.entityId) return;

    try {
      const res = await getEntityFieldsWithChildren(entity.entityId);
      const transformEntity = (entity: any, isChild = false) => ({
        label: isChild ? entity.childEntityName : entity.entityName,
        value: isChild ? entity.childEntityUuid : entity.entityUuid,
        children: (isChild ? entity?.childFields || [] : entity?.parentFields || []).map((field: any) => ({
          label: field.displayName,
          value: field.fieldUuid,
          fieldType: field.fieldType,
          isSystemField: field.isSystemField
        }))
      });

      const uniqueChildEntities = (res.childEntities || []).reduce((acc: any[], child: any) => {
        const existingIndex = acc.findIndex((item) => item.childEntityUuid === child.childEntityUuid);
        if (existingIndex === -1) {
          acc.push(child);
        }
        return acc;
      }, []);

      const entities = [transformEntity(res), ...uniqueChildEntities.map((child: any) => transformEntity(child, true))];

      setOriginFields(entities);
    } catch (error) {
      console.error('加载资产及子表字段失败:', error);
    }
  }, [entity?.entityId]);

  // 刷新字段数据
  const refreshFields = useCallback(async () => {
    setLoading(true);
    try {
      await Promise.all([loadEntityFields(), loadEntityFieldsWithChildren()]);
    } finally {
      setLoading(false);
    }
  }, [loadEntityFields, loadEntityFieldsWithChildren]);

  // 监听弹窗显示状态
  useEffect(() => {
    if (visible) {
      refreshFields();
    } else {
      // 关闭时重置状态
      setFields([]);
      setOriginFields([]);
      setErrors({});
    }
  }, [visible, refreshFields]);

  return {
    fields,
    activeFields,
    originFields,
    loading,
    errors,
    setFields,
    refreshFields
  };
};

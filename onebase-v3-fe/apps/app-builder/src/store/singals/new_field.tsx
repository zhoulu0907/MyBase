import { signal } from '@preact/signals-react';

export const createNewFieldSignal = () => {
  // 存储新增字段的 Map，key 为 entityId，value 为字段 ID 数组
  const newFields = signal<Map<string, string[]>>(new Map());

  // 添加新增字段
  const addNewField = (entityId: string, fieldId: string) => {
    const currentMap = new Map(newFields.value);
    const entityFields = currentMap.get(entityId) || [];
    if (!entityFields.includes(fieldId)) {
      entityFields.push(fieldId);
      currentMap.set(entityId, entityFields);
      newFields.value = currentMap;
    }
  };

  // 移除新增字段
  const removeNewField = (entityId: string, fieldId: string) => {
    const currentMap = new Map(newFields.value);
    const entityFields = currentMap.get(entityId) || [];
    const filteredFields = entityFields.filter(id => id !== fieldId);
    if (filteredFields.length === 0) {
      currentMap.delete(entityId);
    } else {
      currentMap.set(entityId, filteredFields);
    }
    newFields.value = currentMap;
  };

  // 清空指定实体的新增字段
  const clearEntityNewFields = (entityId: string) => {
    const currentMap = new Map(newFields.value);
    currentMap.delete(entityId);
    newFields.value = currentMap;
  };

  // 清空所有新增字段
  const clearAllNewFields = () => {
    newFields.value = new Map();
  };

  // 检查字段是否为新增字段
  const isNewField = (entityId: string, fieldId: string) => {
    const entityFields = newFields.value.get(entityId) || [];
    return entityFields.includes(fieldId);
  };

  // 获取实体的所有新增字段
  const getEntityNewFields = (entityId: string) => {
    return newFields.value.get(entityId) || [];
  };

  return {
    newFields,
    addNewField,
    removeNewField,
    clearEntityNewFields,
    clearAllNewFields,
    isNewField,
    getEntityNewFields
  };
};

// 创建默认的 store 实例
export const newFieldSignal = createNewFieldSignal();

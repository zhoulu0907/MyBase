import type { AppEntityField, AppEntity, AppEntities, EntityFieldOption } from '@onebase/app';
import { getDictDataListByTypeId, type DictData } from '@onebase/platform-center';

// 获取单个字段的配置
export const getFieldConfig = (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
    if (!dataField || dataField.length === 0) {
        return null;
    }
    
    const [tableName, fieldName] = dataField;

    if (!tableName || !fieldName || !mainEntity.tableName) {
        return null;
    }
    let realTableName = tableName;
    let realFieldName = fieldName;
    const index = fieldName?.indexOf('.');
    const lastIndex = fieldName?.lastIndexOf('.');
    if (index !== -1) {
        // 表格中实际的名称
        realTableName = fieldName.slice(0, index);
        realFieldName = lastIndex === -1 ? fieldName : fieldName.slice(lastIndex + 1);
    }
    if (mainEntity.tableName === realTableName) {
        // 主表
        // 当前字段
        const currentField = mainEntity.fields.find((ele: AppEntityField) => ele.fieldName === realFieldName);
        return currentField;
    } else {
        // 子表
        const currentSubEntity = subEntities.entities?.find((ele: AppEntity) => ele.tableName === realTableName);
        // 字段
        const currentField = currentSubEntity?.fields.find((ele: AppEntityField) => ele.fieldName === realFieldName);
        return currentField;
    }
}

// 通过配置获取下拉选项
export const getFieldOptionsConfig = async (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
    const currentField = getFieldConfig(dataField, mainEntity, subEntities)
    if (!currentField) {
        return [];
    }
    if (currentField.dictTypeId) {
        const dictDataList = await getDictDataListByTypeId(currentField.dictTypeId);
        const dictOptions = dictDataList?.filter((e: DictData) => e.status === 1); // 只显示启用状态的字典数据
        return dictOptions || [];
    } else if (currentField.options?.length) {
        const newOptions = currentField.options.map((ele: EntityFieldOption) => ({
            id: ele.optionUuid || ele.id,
            sort: 0,
            label: ele.optionLabel,
            value: ele.optionValue,
            status: 1
        }));
        return newOptions || [];
    }
    return [];
}

export const getFieldAutoCodeConfig = async (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
    const currentField = getFieldConfig(dataField, mainEntity, subEntities)
    if (!currentField) {
        return [];
    }
    if (currentField.autoNumberConfig?.rules?.length) {
        return [...currentField.autoNumberConfig?.rules];
    }
    return [];
}
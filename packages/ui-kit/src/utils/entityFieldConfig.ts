import type { AppEntityField, AppEntity, AppEntities, EntityFieldOption } from '@onebase/app';
import { getDictDetail, getDictDataListByType, type DictData } from '@onebase/platform-center';

// 获取单个字段的配置
export const getFieldConfig = (dataField: string[], mainEntity: AppEntity, subEntities: AppEntities) => {
    const [tableName, fieldName] = dataField;
    if (!tableName || !fieldName || !mainEntity.tableName) {
        return null;
    }
    if (mainEntity.tableName === tableName) {
        // 主表
        // 当前字段
        const currentField = mainEntity.fields.find((ele: AppEntityField) => ele.fieldName === fieldName);
        return currentField;
    } else {
        // 子表
        const currentSubEntity = subEntities.entities?.find((ele: AppEntity) => ele.tableName === tableName);
        // 字段
        const currentField = currentSubEntity?.fields.find((ele: AppEntityField) => ele.fieldName === fieldName);
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
        const res = await getDictDetail(currentField.dictTypeId);
        const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
        const dictOptions = dictDataList?.filter((e: DictData) => e.status === 1); // 只显示启用状态的字典数据
        return dictOptions || [];
    } else if (currentField.options?.length) {
        const newOptions = currentField.options.map((ele: EntityFieldOption) => ({
            id: ele.id,
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
import { FORM_COMPONENT_TYPES } from '../components/Materials/componentTypes';

type NormalizeFormValuesParams = {
  dataItem: Record<string, any> | null | undefined;
  componentSchemas?: Record<string, any>;
  subEntities?: Array<{ childTableName?: string; childEntityUuid?: string }>;
  subTableComponents?: Record<string, Array<{ id: string }>>;
  setSubTableDataLength?: (key: string, length: number) => void;
};

/**
 * 将接口/草稿数据按组件配置转换为表单可用的值结构
 * - 处理主表字段的特殊类型（上传、单选/多选）
 * - 处理子表字段并补齐行 id
 */
export const normalizeFormValues = ({
  dataItem,
  componentSchemas = {},
  subEntities = [],
  subTableComponents = {},
  setSubTableDataLength
}: NormalizeFormValuesParams): Record<string, any> => {
  const formValues: Record<string, any> = {};

  // 主表渲染逻辑
  if (dataItem && typeof dataItem === 'object') {
    Object.entries(dataItem).forEach(([fieldName, value]: [string, any]) => {
      const componentSchemaList = Object.keys(componentSchemas);
      const currentKey = componentSchemaList.find((key) =>
        componentSchemas?.[key]?.config?.dataField?.includes(fieldName)
      );
      const currentSchema = currentKey ? componentSchemas?.[currentKey] : undefined;

      if (
        (currentSchema?.type === FORM_COMPONENT_TYPES.IMG_UPLOAD ||
          currentSchema?.type === FORM_COMPONENT_TYPES.FILE_UPLOAD) &&
        Array.isArray(value)
      ) {
        // 处理文件、图片
        formValues[fieldName] = (value || []).map((ele: any) => ({ ...ele, uid: ele.id }));
      } else if (
        (currentSchema?.type === FORM_COMPONENT_TYPES.RADIO ||
          currentSchema?.type === FORM_COMPONENT_TYPES.SELECT_ONE) &&
        typeof value === 'object' &&
        value !== null
      ) {
        // 处理单选框、下拉列表
        formValues[fieldName] = value?.id;
      } else if (
        (currentSchema?.type === FORM_COMPONENT_TYPES.CHECKBOX ||
          currentSchema?.type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) &&
        Array.isArray(value)
      ) {
        // 处理复选框、下拉多选列表
        formValues[fieldName] = (value || []).map((ele: any) => ele.id);
      } else {
        formValues[fieldName] = value;
      }
    });
  }

  // 子表渲染逻辑
  for (const subEntity of subEntities) {
    if (
      dataItem &&
      subEntity?.childTableName &&
      Object.prototype.hasOwnProperty.call(dataItem, subEntity.childTableName)
    ) {
      const subData = (dataItem as any)[subEntity.childTableName];

      Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
        if (schema?.config?.subTable == subEntity.childEntityUuid) {
          setSubTableDataLength?.(key, (subData || []).length);

          const componentIds = subTableComponents?.[key]?.map((ele: any) => ele.id) || [];
          for (let idx = 0; idx < (subData || []).length; idx++) {
            for (const componentId of componentIds) {
              const fieldName = componentSchemas[componentId]?.config?.dataField?.[1];
              const fieldValue = subData?.[idx]?.[fieldName];

              if (
                (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.IMG_UPLOAD ||
                  componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.FILE_UPLOAD) &&
                Array.isArray(fieldValue)
              ) {
                formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = (fieldValue || []).map((e: any) => ({
                  ...e,
                  uid: e.id
                }));
              } else if (
                (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.RADIO ||
                  componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.SELECT_ONE) &&
                typeof fieldValue === 'object' &&
                fieldValue !== null
              ) {
                formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = fieldValue?.id;
              } else if (
                (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.CHECKBOX ||
                  componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) &&
                Array.isArray(fieldValue)
              ) {
                formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = (fieldValue || []).map(
                  (ele: any) => ele.id
                );
              } else {
                formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = fieldValue;
              }
            }
            formValues[`${subEntity.childTableName}.${idx}.id`] = subData?.[idx]?.id;
          }
        }
      });
    }
  }

  return formValues;
};

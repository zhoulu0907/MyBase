import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';

/**
 * 将输入的 JSON 字符串转换为 JSON Schema，
 * 支持 string、array、number、boolean 类型，并自动推断 required 字段。
 *
 * @param jsonStr 输入的 JSON 字符串
 * @returns 对应的 JSON Schema
 */

export function jsonToJsonSchema(jsonStr: string): any {
  let jsonObj: any;
  try {
    jsonObj = JSON.parse(jsonStr);
  } catch (e) {
    throw new Error('无效的JSON字符串');
  }

  /**
   * 获取字段类型 默认返回文本
   * @param val
   * @returns
   */
  function inferType(val: any): string {
    // 数组 默认多选
    if (Array.isArray(val)) return ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE;
    if (val === null) return ENTITY_FIELD_TYPE.TEXT.VALUE;
    if (typeof val === 'number' || typeof val === 'bigint') return ENTITY_FIELD_TYPE.NUMBER.VALUE;
    if (typeof val === 'boolean') return ENTITY_FIELD_TYPE.BOOLEAN.VALUE;
    return ENTITY_FIELD_TYPE.TEXT.VALUE;
  }

  // name type value
  const keys = Object.keys(jsonObj);

  const schema = keys.map((key) => {
    const fieldType = inferType(jsonObj[key]);
    return {
      name: key,
      type: fieldType
    };
  });

  return schema;
}

/**
 * 将 JSON Schema 转换为表单初始数据
 * 递归处理嵌套的对象和数组结构，为嵌套字段生成 children 数据
 * 同时保留原始 JSON 对象的值
 * @param schema JSON Schema 对象
 * @param jsonValue 原始的 JSON 对象值（可选）
 * @returns 表单初始数据
 */
export function schemaToFormData(schema: any, jsonValue?: any): any[] {
  if (!schema || !schema.type) {
    return [];
  }

  if (schema.type === 'object' && schema.properties) {
    const properties = schema.properties;
    const objectValue = jsonValue || {};

    return Object.keys(properties).map((key) => {
      const propertySchema = properties[key];
      const oldPropertyType = propertySchema.type || 'string';

      // 如果 schema 中已经定义了自定义类型（不是标准的 string/number/boolean），直接使用
      // 这样可以保留 DATE、EMAIL、PHONE、ID 等自定义类型
      const propertyType = oldPropertyType;
      const propertyValue = objectValue[key];

      const formItem: any = {
        name: key,
        type: propertyType,
        schema: propertySchema
      };

      // 递归处理嵌套的对象类型
      if (propertyType === 'object' && propertySchema.properties) {
        const children = schemaToFormData(propertySchema, propertyValue);
        if (children.length > 0) {
          formItem.children = children;
        }
      }
      // 递归处理嵌套的数组类型
      else if (propertyType === 'array' && propertySchema.items) {
        const itemsSchema = propertySchema.items;
        const oldItemsType = itemsSchema.type || 'string';
        // 直接使用类型
        const itemsType = oldItemsType;
        const arrayValue = Array.isArray(propertyValue) ? propertyValue : [];

        // 数组类型的 children 包含数组中的所有项
        if (itemsType === 'object' && itemsSchema.properties) {
          // 如果数组项是对象类型，递归生成 children
          formItem.children = arrayValue.map((itemValue: any) => ({
            type: itemsType,
            schema: itemsSchema,
            children: schemaToFormData(itemsSchema, itemValue)
          }));

          // 如果数组为空，至少创建一个空项
          if (formItem.children.length === 0) {
            formItem.children = [
              {
                type: itemsType,
                schema: itemsSchema,
                children: schemaToFormData(itemsSchema)
              }
            ];
          }
        } else {
          // 如果数组项是基本类型，生成所有项的值
          formItem.children = arrayValue.map((itemValue: any) => ({
            type: itemsType,
            schema: itemsSchema,
            value: itemValue
          }));

          // 如果数组为空，至少创建一个空项
          if (formItem.children.length === 0) {
            formItem.children = [
              {
                type: itemsType,
                schema: itemsSchema
              }
            ];
          }
        }
      }
      // 基本类型：保留原始值（包括 null、undefined 等）
      else {
        // 只有当值不为 undefined 时才设置 value，避免丢失 null 值
        // propertyValue 可能为 null、undefined 或其他值
        // 如果是 undefined（字段不存在），不设置 value；如果是 null，设置 value 为 null
        if (propertyValue !== undefined) {
          formItem.value = propertyValue;
        }
      }

      return formItem;
    });
  }

  if (schema.type === 'array' && schema.items) {
    // 数组类型默认生成一个空项
    return [];
  }

  return [];
}

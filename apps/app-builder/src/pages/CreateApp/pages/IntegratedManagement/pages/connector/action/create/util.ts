/**
 * 将输入的 JSON 字符串转换为 JSON Schema，
 * 支持 object、string、array、number、boolean 类型，并自动推断 required 字段。
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

  function inferType(val: any): string {
    if (Array.isArray(val)) return 'array';
    if (val === null) return 'null';
    return typeof val;
  }

  function generateSchema(value: any): any {
    const valueType = inferType(value);

    switch (valueType) {
      case 'object':
        if (value === null) {
          // treat null as empty
          return { type: 'null' };
        }
        const properties: Record<string, any> = {};
        const required: string[] = [];
        for (const key in value) {
          properties[key] = generateSchema(value[key]);
          // 只要 key 存在，就标记为 required
          required.push(key);
        }
        return {
          type: 'object',
          properties,
          required: required.length > 0 ? required : undefined
        };
      case 'array':
        // 简单推断 items；如数组为空，items 为{}
        return {
          type: 'array',
          items: value.length > 0 ? generateSchema(value[0]) : {}
        };
      case 'string':
        return { type: 'string' };
      case 'number':
        return { type: 'number' };
      case 'boolean':
        return { type: 'boolean' };
      default:
        return {};
    }
  }

  const schema = generateSchema(jsonObj);

  return {
    $schema: 'http://json-schema.org/draft-07/schema#',
    ...schema
  };
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
      const propertyType = propertySchema.type || 'string';
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
        const itemsType = itemsSchema.type || 'string';
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

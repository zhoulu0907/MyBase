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
 * @param schema JSON Schema 对象
 * @returns 表单初始数据
 */
export function schemaToFormData(schema: any): any[] {
  if (!schema || !schema.type) {
    return [];
  }

  if (schema.type === 'object' && schema.properties) {
    const properties = schema.properties;
    return Object.keys(properties).map((key) => ({
      name: key,
      type: properties[key].type || 'string',
      schema: properties[key]
    }));
  }

  if (schema.type === 'array' && schema.items) {
    // 数组类型默认生成一个空项
    return [];
  }

  return [];
}

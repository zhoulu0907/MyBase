import { getComponentSchema } from '@/components/Materials/schema';

export function getComponentWidth(schema: any, itemType: string): string {
  if (!schema || !schema.config || !schema.config.width) {
    schema = getComponentSchema(itemType as any);
    // console.log("初始化 schema.config.width", schema.config.width);
  }
  return schema.config.width;
}

export function getComponentConfig(schema: any, itemType: string): any {
  if (!schema || !schema.config) {
    schema = getComponentSchema(itemType as any);
    // console.log("初始化 schema.config 默认配置", schema.config);
  }
  return schema.config;
}

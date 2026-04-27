import { v4 as uuidv4 } from 'uuid';
import type { WorkbenchItem, WorkbenchTemplateItem } from '../types/workbench';

/**
 * 过滤并生成组件ID
 */
export function filterAndGenerateIds(items: WorkbenchTemplateItem[], keyword: string): WorkbenchItem[] {
  const lowerKeyword = keyword.toLowerCase();
  return items
    .filter((item) => !keyword || item.displayName?.toLowerCase().includes(lowerKeyword))
    .map((item) => ({
      type: item.type,
      displayName: item.displayName,
      id: `${item.type}-${uuidv4()}`,
      icon: item.icon
    }));
}

/**
 * 从模板中提取组件列表
 */
export function extractItemsFromTemplate(
  template: { basic?: Array<{ items: WorkbenchTemplateItem[] }>; advanced?: Array<{ items: WorkbenchTemplateItem[] }> },
  category: 'basic' | 'advanced'
): WorkbenchTemplateItem[] {
  const categoryData = template[category] || [];
  return categoryData.flatMap((cat) => cat.items || []);
}

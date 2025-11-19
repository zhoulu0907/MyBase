/**
 * 工作台相关类型定义
 */

export interface WorkbenchItem {
  type: string;
  displayName: string;
  id: string;
  icon?: string;
}

export interface WorkbenchTemplateItem {
  type: string;
  displayName: string;
  icon?: string;
  h?: number;
  w?: number;
  category?: string;
}

export interface WorkbenchCategory {
  category: string;
  items: WorkbenchTemplateItem[];
}

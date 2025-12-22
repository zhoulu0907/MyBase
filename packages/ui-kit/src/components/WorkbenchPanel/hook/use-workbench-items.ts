/**
 * 工作台组件项管理 Hook
 */
import { useEffect, useMemo, useState } from 'react';
import { workbenchTemplate } from '@/components/Materials';
import type { WorkbenchItem, WorkbenchTemplateItem } from '../types/workbench';
import { extractItemsFromTemplate, filterAndGenerateIds } from '../utils/template';

interface UseWorkbenchItemsParams {
  keyword: string;
  category: 'basic' | 'advanced';
}

/**
 * 管理工作台组件项的 Hook
 */
export function useWorkbenchItems({ keyword, category }: UseWorkbenchItemsParams) {
  const [items, setItems] = useState<WorkbenchItem[]>([]);
  const [components, setComponents] = useState<WorkbenchItem[]>([]);

  // 从模板中提取组件数据
  const templateItems = useMemo<WorkbenchTemplateItem[]>(() => {
    const wt = workbenchTemplate || { basic: [], advanced: [] };
    return extractItemsFromTemplate(wt, category);
  }, [category]);

  // 过滤并生成ID
  useEffect(() => {
    const filteredItems = filterAndGenerateIds(templateItems, keyword);
    setItems(filteredItems);
    setComponents(filteredItems);
  }, [keyword, templateItems]);

  return {
    items,
    components,
    setItems
  };
}

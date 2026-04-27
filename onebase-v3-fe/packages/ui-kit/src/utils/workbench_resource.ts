import { Message } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  loadPageSet,
  savePageSet,
  SavePageSetReq,
  type ComponentConfig,
  type LoadPageSetReq,
  type PageSet
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP } from 'src/components';
import type { EditConfig } from 'src/components/Materials/types';
import { useWorkbenchEditorSignal } from 'src/signals/workbench_editor';
import { isBlank } from './common';
import { findPageConfig, isPageConfig, PAGE_CONFIG_TYPE } from '../components/Materials/Workbench/utils/page-config-util';

export interface SaveWorkbenchPageSetParams {
  pageSetId: string;
  workbenchComponents: any[];
  wbComponentSchemas: { [key: string]: EditConfig };
}

/**
 * 保存工作台页面集
 * @param params
 * @param onSuccess
 */
export async function startSaveWorkbenchPageSet(
  params: SaveWorkbenchPageSetParams,
  onSuccess?: Function
) {
  const { pageSetId, workbenchComponents, wbComponentSchemas } = params;

  // 更新signal中的数据
  const { setWorkbenchComponents, loadWbComponentSchemas } = useWorkbenchEditorSignal;
  setWorkbenchComponents(workbenchComponents);
  loadWbComponentSchemas(wbComponentSchemas);

  // 加载现有页面集数据
  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId
  };
  const loadPagesetResp = await loadPageSet(loadPageSetReq);

  // 查找工作台页面
  const workbenchPageIndex = loadPagesetResp.pages.findIndex(
    (_page: PageSet) => _page.pageType === CATEGORY_TYPE.WORKBENCH
  );

  if (workbenchPageIndex === -1) {
    Message.error('未找到工作台页面');
    return;
  }

  // 转换组件数据为保存格式
  const normalizedComponents = workbenchComponents.map((component) => {
    const schema = wbComponentSchemas[component.id] || { config: {}, editData: {} };
    return {
      componentCode: component.id,
      componentType: component.type,
      config: JSON.stringify(schema.config || {}),
      editData: JSON.stringify(schema.editData || {}),
      parentCode: '',
      blockIndex: 0,
      containerIndex: 0
    } as ComponentConfig;
  });

  // 保存页面配置（使用工具函数查找）
  const pageConfigEntry = findPageConfig(wbComponentSchemas);
  
  if (pageConfigEntry) {
    const [pageConfigId, pageConfigSchema] = pageConfigEntry;
    normalizedComponents.push({
      componentCode: pageConfigId,
      componentType: PAGE_CONFIG_TYPE,
      config: JSON.stringify(pageConfigSchema.config || {}),
      editData: JSON.stringify(pageConfigSchema.editData || {}),
      parentCode: '',
      blockIndex: 0,
      containerIndex: 0
    } as ComponentConfig);
  }

  // 更新工作台页面数据
  loadPagesetResp.pages[workbenchPageIndex] = {
    ...loadPagesetResp.pages[workbenchPageIndex],
    components: normalizedComponents,
    isLatestUpdated: 1
  };

  // 发起保存请求
  const savePageSetReq: SavePageSetReq = {
    id: pageSetId,
    pageSetName: '',
    pages: loadPagesetResp.pages
  };

  const res = await savePageSet(savePageSetReq);

  if (res) {
    Message.success('保存成功');
    onSuccess?.();
  }

  return res;
}

// 加载参数接口
export interface LoadWorkbenchPageSetParams {
  pageSetId: string;
}

/**
 * 加载工作台页面集
 * @param params 加载参数
 */
export async function startLoadWorkbenchPageSet(params: LoadWorkbenchPageSetParams) {
  const { pageSetId } = params;

  const { setWorkbenchComponents, setWbComponentSchemas } = useWorkbenchEditorSignal;

  const { setCurPage } = pagesRuntimeSignal;

  // 加载页面集数据
  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId
  };
  const pageSet = await loadPageSet(loadPageSetReq);
  setCurPage(pageSet)
  console.log('载入工作台页面集数据: ', pageSet);

  // 查找工作台页面
  const workbenchPage = pageSet.pages.find(
    (page: PageSet) => page.pageType === CATEGORY_TYPE.WORKBENCH
  );

  if (!workbenchPage) {
    console.warn('未找到工作台页面');
    return;
  }

  // 解析组件数据
  const newComponents: any[] = [];
  const newPageComponentSchemas: { [key: string]: EditConfig } = {};

  workbenchPage.components.forEach((component: ComponentConfig) => {
    if (isBlank(component.parentCode)) {
      // 判断是否是页面配置组件（使用工具函数判断）
      if (isPageConfig({ type: component.componentType })) {
        newPageComponentSchemas[component.componentCode] = {
          id: component.componentCode,
          type: PAGE_CONFIG_TYPE, // 统一转换为新类型
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        };
      } else {
        // 普通组件，添加到组件列表和 schema
        newComponents.push({
          id: component.componentCode,
          chosen: false,
          selected: false,
          type: component.componentType,
          displayName: WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType as keyof typeof WORKBENCH_COMPONENT_TYPE_DISPLAY_NAME_MAP] || ''
        });

        const parsedConfig = JSON.parse(component.config);
        // 加载时清除旧的 gridLayout.colSpan/row/column（这些由 applyGridLayout 动态计算），
        // 只保留 rowSpan（由 ResizeObserver 测量后上报，作为初始估算值）
        const savedRowSpan = parsedConfig?.gridLayout?.rowSpan ?? parsedConfig?.rowSpan;
        const initialRowSpan = (typeof savedRowSpan === 'number' && savedRowSpan > 0) ? savedRowSpan : 1;
        const cleanConfig = {
          ...parsedConfig,
          gridLayout: { rowSpan: initialRowSpan }
        };
        newPageComponentSchemas[component.componentCode] = {
          id: component.componentCode,
          type: component.componentType,
          config: cleanConfig,
          editData: JSON.parse(component.editData)
        };
      }
    }
  });

  // 如果没有页面配置，不需要在这里创建，会在 WorkbenchWorkspace 初始化时创建

  // 更新signal
  setWorkbenchComponents(newComponents);
  Object.entries(newPageComponentSchemas).forEach(([componentId, config]) => {
    setWbComponentSchemas(componentId, config);
  });

  console.log('工作台组件加载完成', {
    components: newComponents,
    schemas: newPageComponentSchemas
  });
}

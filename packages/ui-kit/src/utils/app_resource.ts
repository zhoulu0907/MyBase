import { Message } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  listPageView,
  loadPageSet,
  PageView,
  savePageSet,
  SavePageSetReq,
  type ComponentConfig,
  type LoadPageSetReq,
  type PageSet
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import { cloneDeep } from 'lodash-es';
import {
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  EditConfig,
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES
} from 'src/components';
import {
  createPageEditorSignal,
  useEditorSignalMap,
  useFormEditorSignal,
  useListEditorSignal,
  usePageSettingSignal,
  usePageViewEditorSignal
} from 'src/signals';
import { isBlank } from './common';

export interface SavePageSetParams {
  pageSetId: string;
  formComponents: any[];
  formPageComponentSchemas: { [key: string]: EditConfig };
  fromColComponentsMap: { [key: string]: any[][] };
  fromSubTableComponentsMap: { [key: string]: any[] };
  listComponents: any[];
  listPageComponentSchemas: Map<string, EditConfig>;
  listColComponentsMap: {
    colComponents: Map<string, any[][]>;
  };
  dataTitleType?: number;
  redirectType?: number;
  dataTitle?: string;
}

const processColComponent = (
  component: any,
  colIndex: number,
  blockIndex: number,
  parentCode: string,
  pageComponentSchemas: Map<string, EditConfig>
): ComponentConfig => {
  return {
    componentCode: component.id,
    componentType: component.type,
    config: JSON.stringify(pageComponentSchemas.get(component.id)?.config),
    editData: JSON.stringify(pageComponentSchemas.get(component.id)?.editData),
    parentCode: parentCode,
    blockIndex: blockIndex,
    containerIndex: colIndex
  } as ComponentConfig;
};

const processColComponents = (
  cols: any[][],
  parentCode: string,
  pageComponentSchemas: Map<string, EditConfig>
): ComponentConfig[] => {
  const colComponents: ComponentConfig[] = [];
  cols?.forEach((col: any[], index: number) => {
    col.forEach((component: any, colIndex: number) => {
      colComponents.push(processColComponent(component, colIndex, index, parentCode, pageComponentSchemas));
    });
  });
  return colComponents;
};

const processLayoutSubComponents = (
  layoutSubComponentsMap: Map<string, any[][]>,
  pageComponentSchemas: Map<string, EditConfig>
): ComponentConfig[] => {
  const colComponents: ComponentConfig[] = [];
  layoutSubComponentsMap.forEach((cols: any[][], parentCode: string) => {
    console.log(parentCode, ': cols: ', cols);
    const components = processColComponents(cols, parentCode, pageComponentSchemas);
    colComponents.push(...components);
  });
  return colComponents;
};

const processListColComponents = (
  listColComponentsMap: { colComponents: Map<string, any[][]> },
  listPageComponentSchemas: Map<string, EditConfig>
): ComponentConfig[] => {
  const colComponents: ComponentConfig[] = [];
  listColComponentsMap.colComponents.forEach((cols: any[][], parentCode: string) => {
    console.log(parentCode, ': cols: ', cols);
    const components = processColComponents(cols, parentCode, listPageComponentSchemas);
    colComponents.push(...components);
  });
  return colComponents;
};

export async function startSavePageSet(params: SavePageSetParams, onSuccess?: Function,message?:string) {
  const {
    formComponents,
    formPageComponentSchemas,
    listComponents,
    listPageComponentSchemas,
    fromColComponentsMap,
    fromSubTableComponentsMap,
    listColComponentsMap,
    pageSetId
  } = params;

  const { curViewId, pageViews } = usePageViewEditorSignal;

  //   最新的表单配置保存到useEditorSignalMap中
  useEditorSignalMap.get(curViewId.value)!.setComponents(formComponents);
  useEditorSignalMap.get(curViewId.value)!.loadPageComponentSchemas(formPageComponentSchemas);
  useEditorSignalMap.get(curViewId.value)!.loadLayoutSubComponents(fromColComponentsMap);
  useEditorSignalMap.get(curViewId.value)!.loadSubTableComponents(fromSubTableComponentsMap);

  // 过滤出 pageViews 中 created 为 true 的元素
  const createdPageViews = Object.entries(pageViews.value)
    .filter(([_, view]) => view.created === true)
    .map(([_, view]) => ({ ...view, created: true, components: [] }));

  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId
  };
  const loadPagesetResp = await loadPageSet(loadPageSetReq);

  // 补充到已有的结果中
  loadPagesetResp.pages.push(...createdPageViews);

  // 给每个页面赋值组件
  loadPagesetResp.pages.forEach((_page: PageSet, index: number) => {
    if (_page.pageType === CATEGORY_TYPE.FORM) {
      const components = useEditorSignalMap.get(_page.pageUuid)!.components.value;

      const pageComponentSchemas = new Map(
        Object.entries(cloneDeep(useEditorSignalMap.get(_page.pageUuid)!.pageComponentSchemas.value))
      );

      const layoutSubComponentsMap = new Map(
        Object.entries(cloneDeep(useEditorSignalMap.get(_page.pageUuid)!.layoutSubComponents.value))
      );
      const subTableComponentsMap = new Map(
        Object.entries(cloneDeep(useEditorSignalMap.get(_page.pageUuid)!.subTableComponents.value))
      );

      console.log(loadPagesetResp.pages[index]);
      loadPagesetResp.pages[index] = {
        ...loadPagesetResp.pages[index],
        detailViewMode: pageViews.value[_page.pageUuid]?.detailViewMode,
        editViewMode: pageViews.value[_page.pageUuid]?.editViewMode,
        isDefaultDetailViewMode: pageViews.value[_page.pageUuid]?.isDefaultDetailViewMode,
        isDefaultEditViewMode: pageViews.value[_page.pageUuid]?.isDefaultEditViewMode,
        interactionRules: JSON.stringify(pageViews.value[_page.pageUuid]?.interactionRules)
      };

      loadPagesetResp.pages[index].components = components.map((component) => {
        return {
          componentCode: component.id,
          componentType: component.type,
          config: JSON.stringify(pageComponentSchemas.get(component.id)?.config),
          editData: JSON.stringify(pageComponentSchemas.get(component.id)?.editData),
          parentCode: '',
          blockIndex: 0,
          containerIndex: 0
        } as ComponentConfig;
      });

      const colComponents = processLayoutSubComponents(layoutSubComponentsMap, pageComponentSchemas);
      subTableComponentsMap.forEach((col: any[], parentCode: string) => {
        console.log(parentCode, 'parentCode : col: ', col);
        col?.forEach((component: any, colIndex: number) => {
          colComponents.push({
            componentCode: component.id,
            componentType: component.type,
            config: JSON.stringify(pageComponentSchemas.get(component.id)?.config),
            editData: JSON.stringify(pageComponentSchemas.get(component.id)?.editData),
            parentCode: parentCode,
            blockIndex: index,
            containerIndex: colIndex
          } as ComponentConfig);
        });
      });

      loadPagesetResp.pages[index].components.push(...colComponents);

      //   更新视图名称
      loadPagesetResp.pages[index].pageName = pageViews.value[_page.pageUuid]?.pageName;

      if (_page.pageUuid === curViewId.value) {
        loadPagesetResp.pages[index].isLatestUpdated = 1;
      } else {
        loadPagesetResp.pages[index].isLatestUpdated = 0;
      }
    } else if (_page.pageType === CATEGORY_TYPE.LIST) {
      console.log('listComponents: ', listComponents);
      loadPagesetResp.pages[index].components = listComponents.map((component) => {
        return {
          componentCode: component.id,
          componentType: component.type,
          config: JSON.stringify(listPageComponentSchemas.get(component.id)?.config),
          editData: JSON.stringify(listPageComponentSchemas.get(component.id)?.editData),
          parentCode: '',
          blockIndex: 0,
          containerIndex: 0
        } as ComponentConfig;
      });

      const colComponents = processListColComponents(listColComponentsMap, listPageComponentSchemas);

      loadPagesetResp.pages[index].components.push(...colComponents);
    }
  });

  console.log(loadPagesetResp);

  const { dataTitleType, redirectType, dataTitle } = usePageSettingSignal;

  const savePageSetReq: SavePageSetReq = {
    id: pageSetId,
    pageSetName: '',
    pages: loadPagesetResp.pages,
    dataTitleType: dataTitleType.value,
    redirectType: redirectType.value,
    dataTitle: dataTitle.value
  };
  const res = await savePageSet(savePageSetReq);

  if (res) {
    Message.success(message || '保存成功');
    onSuccess?.();
  }

  return;
}

export interface LoadPageSetParams {
  pageSetId: string;
  runtime?: boolean;
  allowViewUuids?: string[];
  isDev?: boolean;
}

export async function startLoadPageSet(params: LoadPageSetParams) {
  const { pageSetId, allowViewUuids, isDev } = params;

  const { setPageViews, curViewId, setCurViewId } = usePageViewEditorSignal;

  const { setCurPage, setEditPageViewId, editPageViewId } = pagesRuntimeSignal;

  const {
    setComponents: setFormComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    loadPageComponentSchemas: loadFormPageComponentSchemas,
    setLayoutSubComponents: setFromLayoutSubComponents,
    loadLayoutSubComponents: loadFormLayoutSubComponents,
    setSubTableComponents: setFromSubTableComponents,
    loadSubTableComponents: loadFormSubTableComponents
  } = useFormEditorSignal;

  const {
    setComponents: setListComponents,
    setPageComponentSchemas: setListPageComponentSchemas,
    setLayoutSubComponents: setListLayoutSubComponents,
    setSubTableComponents: setListSubTableComponents
  } = useListEditorSignal;

  const { setDataTitleType, setRedirectType, setDataTitle } = usePageSettingSignal;

  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId,
    isDev: isDev
  };
  const pageSet = await loadPageSet(loadPageSetReq);
  setCurPage(pageSet);
  console.log('载入页面集数据: ', pageSet);

  if (pageSet.dataTitleType) {
    setDataTitleType(pageSet.dataTitleType);
  }
  if (pageSet.redirectType) {
    setRedirectType(pageSet.redirectType);
  }
  if (pageSet.dataTitle) {
    setDataTitle(pageSet.dataTitle);
  } else if (pageSet.dataTitleType === 1) {
    setDataTitle('规则：发起人发起的页面名称 示例：小贝发起的工时填报');
  }

  //   //   如果allowViewUuids不为空，则过滤掉不在allowViewUuids中的视图
  //   if (allowViewUuids && allowViewUuids.length > 0) {
  //     pageSet.pages = pageSet.pages.filter((page: PageSet) => allowViewUuids.includes(page.pageUuid));
  //     console.log('过滤后的页面集数据: ', pageSet.pages);
  //   }

  pageSet.pages.forEach((page: PageSet) => {
    useEditorSignalMap.set(page.pageUuid, createPageEditorSignal());
  });

  pageSet.pages.forEach((page: PageSet) => {
    let newComponents: any[] = [];
    let newPageComponentSchemas = new Map<string, any>();
    let newColComponentsMap = new Map<string, any[][]>();
    let newSubTableComponentsMap = new Map<string, any[]>();

    page.components.forEach((component: ComponentConfig) => {
      if (isBlank(component.parentCode)) {
        newComponents.push({
          id: component.componentCode,
          chosen: false,
          selected: false,
          type: component.componentType,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType] || ''
        });

        newPageComponentSchemas.set(component.componentCode, {
          id: component.componentCode,
          type: component.componentType,
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      }

      const layoutList: string[] = [
        LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT,
        FORM_COMPONENT_TYPES.SUB_TABLE,
        LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT,
        LAYOUT_COMPONENT_TYPES.TABS_LAYOUT,
        LAYOUT_COMPONENT_TYPES.STEPS_LAYOUT
      ];

      const subList: string[] = [FORM_COMPONENT_TYPES.SUB_TABLE];

      // 载入布局组件的列数初始化
      if (layoutList.includes(component.componentType)) {
        const config = JSON.parse(component.config);
        const colCount = config.colCount;
        const columns: any[][] = [];
        for (let i = 0; i < colCount; i++) {
          columns.push([]);
        }
        newColComponentsMap.set(component.componentCode, columns);
      }
      if (subList.includes(component.componentType)) {
        const columns: any[] = [];
        newSubTableComponentsMap.set(component.componentCode, columns);
      }
    });

    //   载入布局组件内的组件配置
    page.components.forEach((component: ComponentConfig) => {
      if (!isBlank(component.parentCode)) {
        if (component.parentCode.indexOf(FORM_COMPONENT_TYPES.SUB_TABLE) !== -1) {
          const colComponents = newSubTableComponentsMap.get(component.parentCode);
          if (colComponents) {
            colComponents[component.containerIndex] = {
              id: component.componentCode,
              chosen: false,
              selected: false,
              type: component.componentType,
              displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType] || ''
            };
          }
          if (page.pageType === CATEGORY_TYPE.FORM) {
            useEditorSignalMap.get(page.pageUuid)!.setSubTableComponents(component.parentCode, colComponents as any[]);
          } else if (page.pageType === CATEGORY_TYPE.LIST) {
            setListSubTableComponents(component.parentCode, colComponents as any[]);
          }
        } else {
          const colComponents = newColComponentsMap.get(component.parentCode);
          if (colComponents) {
            // 如果列数不够，则初始化列数
            if (colComponents[component.blockIndex].length - 1 < component.containerIndex) {
              for (let i = colComponents[component.blockIndex].length; i <= component.containerIndex; i++) {
                colComponents[component.blockIndex].push([]);
              }
            }
            colComponents[component.blockIndex][component.containerIndex] = {
              id: component.componentCode,
              chosen: false,
              selected: false,
              type: component.componentType,
              displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType] || ''
            };
          }
          if (page.pageType === CATEGORY_TYPE.FORM) {
            useEditorSignalMap
              .get(page.pageUuid)!
              .setLayoutSubComponents(component.parentCode, colComponents as any[][]);
          } else if (page.pageType === CATEGORY_TYPE.LIST) {
            setListLayoutSubComponents(component.parentCode, colComponents as any[][]);
          }
        }

        newPageComponentSchemas.set(component.componentCode, {
          id: component.componentCode,
          type: component.componentType,
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      }
    });

    if (page.pageType === CATEGORY_TYPE.FORM) {
      useEditorSignalMap.get(page.pageUuid)!.setComponents(newComponents);

      newPageComponentSchemas.forEach((config, componentId) => {
        useEditorSignalMap.get(page.pageUuid)!.setPageComponentSchemas(componentId, config);
      });
    } else if (page.pageType === CATEGORY_TYPE.LIST) {
      setListComponents(newComponents);
      newPageComponentSchemas.forEach((config, componentId) => {
        setListPageComponentSchemas(componentId, config);
      });
    }
  });

  // 载入视图
  const res = await listPageView({
    pageSetId: pageSetId
  });
  if (res && res.pages) {
    // 如果没有视图选中，就选中默认视图

    let newCurViewId = res.pages.find((item: PageView) => item.isLatestUpdated == 1)?.pageUuid;
    if (!newCurViewId) {
      newCurViewId = res.pages.find((item: PageView) => item.isDefaultEditViewMode == 1)?.pageUuid;
    }

    console.log('newCurViewId: ', newCurViewId);
    if (newCurViewId) {
      setCurViewId(newCurViewId);
      setFormComponents(useEditorSignalMap.get(newCurViewId)!.components.value);
      loadFormPageComponentSchemas(useEditorSignalMap.get(newCurViewId)!.pageComponentSchemas.value);
      loadFormLayoutSubComponents(useEditorSignalMap.get(newCurViewId)!.layoutSubComponents.value);
      loadFormSubTableComponents(useEditorSignalMap.get(newCurViewId)!.subTableComponents.value);
    }

    // 规则string转对象
    res.pages.forEach((item: any, index: number) => {
      if (item.interactionRules) {
        res.pages[index].interactionRules = JSON.parse(item.interactionRules);
      } else {
        res.pages[index].interactionRules = [];
      }
    });
    console.log('载入视图: ', res.pages);
    setPageViews(res.pages);
    // 设置默认编辑视图
    setEditPageViewId(res.pages.find((item: PageView) => item.isDefaultEditViewMode == 1)?.pageUuid);
    console.log('设置默认编辑视图: ', editPageViewId.value);
  }
}

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
  usePageViewEditorSignal
} from 'src/signals';

export interface SavePageSetParams {
  pageSetId: string;
  formComponents: any[];
  formPageComponentSchemas: { [key: string]: EditConfig };
  fromColComponentsMap: { [key: string]: any[][] };

  listComponents: any[];
  listPageComponentSchemas: Map<string, EditConfig>;
  listColComponentsMap: {
    colComponents: Map<string, any[][]>;
  };
}

export async function startSavePageSet(params: SavePageSetParams, onSuccess?: Function) {
  const {
    formComponents,
    formPageComponentSchemas,
    listComponents,
    listPageComponentSchemas,
    fromColComponentsMap,
    listColComponentsMap,
    pageSetId
  } = params;

  const { curViewId, pageViews } = usePageViewEditorSignal;

  //   最新的表单配置保存到useEditorSignalMap中
  useEditorSignalMap.get(curViewId.value)!.setComponents(formComponents);
  useEditorSignalMap.get(curViewId.value)!.loadPageComponentSchemas(formPageComponentSchemas);
  useEditorSignalMap.get(curViewId.value)!.loadLayoutSubComponents(fromColComponentsMap);

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
      const components = useEditorSignalMap.get(_page.id)!.components.value;

      const pageComponentSchemas = new Map(
        Object.entries(cloneDeep(useEditorSignalMap.get(_page.id)!.pageComponentSchemas.value))
      );

      const layoutSubComponentsMap = new Map(
        Object.entries(cloneDeep(useEditorSignalMap.get(_page.id)!.layoutSubComponents.value))
      );

      loadPagesetResp.pages[index].components = components.map((component) => {
        // console.log('component: ', component);
        // console.log('formPageComponentSchemas: ', formPageComponentSchemas);
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
      // console.log('loadPagesetResp.pages[index].components: ', loadPagesetResp.pages[index].components);

      const colComponents: any[] = [];
      layoutSubComponentsMap.forEach((cols: any[][], parentCode: string) => {
        console.log(parentCode, ': cols: ', cols);

        cols &&
          cols.forEach((col: any[], index: number) => {
            col.forEach((component: any, colIndex: number) => {
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
      });

      loadPagesetResp.pages[index].components.push(...colComponents);
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

      const colComponents: any[] = [];
      listColComponentsMap.colComponents.forEach((cols: any[][], parentCode: string) => {
        console.log(parentCode, ': cols: ', cols);
        cols &&
          cols.forEach((col: any[], index: number) => {
            col.forEach((component: any, colIndex: number) => {
              colComponents.push({
                componentCode: component.id,
                componentType: component.type,
                config: JSON.stringify(listPageComponentSchemas.get(component.id)?.config),
                editData: JSON.stringify(listPageComponentSchemas.get(component.id)?.editData),
                parentCode: parentCode,
                blockIndex: index,
                containerIndex: colIndex
              } as ComponentConfig);
            });
          });
      });

      loadPagesetResp.pages[index].components.push(...colComponents);
    }
  });

  console.log(loadPagesetResp);

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

  return;
}

export interface LoadPageSetParams {
  pageSetId: string;
}

export async function startLoadPageSet(params: LoadPageSetParams) {
  const { pageSetId } = params;

  const { setPageViews, curViewId, setCurViewId } = usePageViewEditorSignal;

  const {
    setComponents: setFormComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    loadPageComponentSchemas: loadFormPageComponentSchemas,
    setLayoutSubComponents: setFromLayoutSubComponents,
    loadLayoutSubComponents: loadFormLayoutSubComponents
  } = useFormEditorSignal;

  const {
    setComponents: setListComponents,
    setPageComponentSchemas: setListPageComponentSchemas,
    setLayoutSubComponents: setListLayoutSubComponents
  } = useListEditorSignal;

  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId
  };
  const pageSet = await loadPageSet(loadPageSetReq);
  console.log('载入页面集数据: ', pageSet);

  pageSet.pages.forEach((page: PageSet) => {
    useEditorSignalMap.set(page.id, createPageEditorSignal());
  });

  pageSet.pages.forEach((page: PageSet) => {
    let newComponents: any[] = [];
    let newPageComponentSchemas = new Map<string, any>();
    let newColComponentsMap = new Map<string, any[][]>();

    page.components.forEach((component: ComponentConfig) => {
      if (component.parentCode == '' || component.parentCode == null) {
        newComponents.push({
          id: component.componentCode,
          chosen: false,
          selected: false,
          type: component.componentType,
          displayName: COMPONENT_TYPE_DISPLAY_NAME_MAP[component.componentType] || ''
        });

        newPageComponentSchemas.set(component.componentCode, {
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      }

      const layoutList: string[] = [
        LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT,
        FORM_COMPONENT_TYPES.SUB_TABLE,
        LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT,
        LAYOUT_COMPONENT_TYPES.TABS_LAYOUT
      ];

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
    });

    //   载入布局组件内的组件配置
    page.components.forEach((component: ComponentConfig) => {
      if (component.parentCode !== '' && component.parentCode !== null) {
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
          useEditorSignalMap.get(page.id)!.setLayoutSubComponents(component.parentCode, colComponents as any[][]);
        } else if (page.pageType === CATEGORY_TYPE.LIST) {
          setListLayoutSubComponents(component.parentCode, colComponents as any[][]);
        }

        newPageComponentSchemas.set(component.componentCode, {
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      }
    });

    if (page.pageType === CATEGORY_TYPE.FORM) {
      useEditorSignalMap.get(page.id)!.setComponents(newComponents);

      newPageComponentSchemas.forEach((config, componentId) => {
        useEditorSignalMap.get(page.id)!.setPageComponentSchemas(componentId, config);
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
    if (!curViewId.value) {
      const newCurViewId = res.pages.find(
        (item: PageView) => item.isDefaultEditViewMode || item.isDefaultDetailViewMode
      )?.id;

      if (newCurViewId) {
        setCurViewId(newCurViewId);
        setFormComponents(useEditorSignalMap.get(newCurViewId)!.components.value);
        loadFormPageComponentSchemas(useEditorSignalMap.get(newCurViewId)!.pageComponentSchemas.value);
        loadFormLayoutSubComponents(useEditorSignalMap.get(newCurViewId)!.layoutSubComponents.value);
      }
    }

    setPageViews(res.pages);
    console.log('载入视图: ', res.pages);
  }
}

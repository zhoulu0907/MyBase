import { Message } from '@arco-design/web-react';
import {
  loadPageSet,
  savePageSet,
  type ComponentConfig,
  type LoadPageSetReq,
  type PageSet,
  type SavePageSetReq
} from '@onebase/app';
import { COMPONENT_TYPE_DISPLAY_NAME_MAP, EditConfig, LAYOUT_COMPONENT_TYPES, FORM_COMPONENT_TYPES } from 'src/components';
import { useFormEditorSignal, useListEditorSignal } from 'src/signals';

export interface SavePageSetParams {
  pageSetId: string;
  formComponents: any[];
  listComponents: any[];
  formPageComponentSchemas: Map<string, EditConfig>;
  listPageComponentSchemas: Map<string, EditConfig>;
  fromColComponentsMap: {
    colComponents: Map<string, any[][]>;
  };
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

  console.log('formComponents: ', formComponents);
  console.log('formPageComponentSchemas: ', formPageComponentSchemas);
  console.log('listComponents: ', listComponents);
  console.log('listPageComponentSchemas: ', listPageComponentSchemas);

  const loadPageSetReq: LoadPageSetReq = {
    id: pageSetId
  };
  const loadPagesetResp = await loadPageSet(loadPageSetReq);
  console.log('res: ', loadPagesetResp);

  loadPagesetResp.pages.forEach((_page: PageSet, index: number) => {
    if (_page.pageType === 'form') {
      // console.log('formComponentsSchemas: ', formPageComponentSchemas);

      loadPagesetResp.pages[index].components = formComponents.map((component) => {
        console.log('component: ', component);
        console.log('formPageComponentSchemas: ', formPageComponentSchemas);
        return {
          componentCode: component.id,
          componentType: component.type,
          config: JSON.stringify(formPageComponentSchemas.get(component.id)?.config),
          editData: JSON.stringify(formPageComponentSchemas.get(component.id)?.editData),
          parentCode: '',
          blockIndex: 0,
          containerIndex: 0
        } as ComponentConfig;
      });
      // console.log('loadPagesetResp.pages[index].components: ', loadPagesetResp.pages[index].components);

      const colComponents: any[] = [];
      console.log(fromColComponentsMap.colComponents);
      fromColComponentsMap.colComponents.forEach((cols: any[][], parentCode: string) => {
        console.log(parentCode, ': cols: ', cols);

        cols &&
          cols.forEach((col: any[], index: number) => {
            col.forEach((component: any, colIndex: number) => {
              colComponents.push({
                componentCode: component.id,
                componentType: component.type,
                config: JSON.stringify(formPageComponentSchemas.get(component.id)?.config),
                editData: JSON.stringify(formPageComponentSchemas.get(component.id)?.editData),
                parentCode: parentCode,
                blockIndex: index,
                containerIndex: colIndex
              } as ComponentConfig);
            });
          });
      });

      loadPagesetResp.pages[index].components.push(...colComponents);
    } else if (_page.pageType === 'list') {
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

  const {
    setComponents: setFormComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    setLayoutSubComponents: setFromLayoutSubComponents
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
  console.log('res: ', pageSet);

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

      // 载入布局组件的列数初始化
      if (component.componentType === LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT || component.componentType === FORM_COMPONENT_TYPES.CHILDREN_TABLE) {
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
        if (page.pageType === 'form') {
          setFromLayoutSubComponents(component.parentCode, colComponents as any[][]);
        } else if (page.pageType === 'list') {
          setListLayoutSubComponents(component.parentCode, colComponents as any[][]);
        }

        newPageComponentSchemas.set(component.componentCode, {
          config: JSON.parse(component.config),
          editData: JSON.parse(component.editData)
        });
      }
    });

    //   console.log(page.pageType,": newComponents: ", newComponents);
    //   console.log(page.pageType,": newPageComponentSchemas: ", newPageComponentSchemas);
    //   console.log(page.pageType,": newColComponentsMap: ", newColComponentsMap);

    if (page.pageType === 'form') {
      setFormComponents(newComponents);
      newPageComponentSchemas.forEach((config, componentId) => {
        setFromPageComponentSchemas(componentId, config);
      });
    } else if (page.pageType === 'list') {
      setListComponents(newComponents);
      newPageComponentSchemas.forEach((config, componentId) => {
        setListPageComponentSchemas(componentId, config);
      });
    }
  });
}

import { getComponentSchema } from '@/components/Materials/schema';
import { COMPONENT_TYPE_DISPLAY_NAME_MAP } from '@/components/Materials/template';
import type { EditConfig } from '@/components/Materials/types';
import { LAYOUT_COMPONENT_TYPES } from '@/constants/componentTypes';
import { Message } from '@arco-design/web-react';
import { loadPageSet, savePageSet, type ComponentConfig, type LoadPageSetReq, type PageSet, type SavePageSetReq } from '@onebase/app';

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

export interface SavePageSetParams {
    pageSetCode: string;
    fromComponents: any[];
    listComponents: any[];
    fromPageComponentSchemas: Map<string, EditConfig>;
    listPageComponentSchemas: Map<string, EditConfig>;
    fromColComponentsMap: {
        colComponents: Map<string, any[][]>;
    };
    listColComponentsMap: {
        colComponents: Map<string, any[][]>;
    };
}

export async function startSavePageSet(params: SavePageSetParams) {
    const { fromComponents, fromPageComponentSchemas, listComponents, listPageComponentSchemas,
        fromColComponentsMap, listColComponentsMap, pageSetCode } = params;
    console.log(fromComponents);
    console.log(fromPageComponentSchemas);

    console.log(fromColComponentsMap);

    // console.log(listComponents);
    // console.log(listPageComponentSchemas);

    // console.log(listColComponentsMap);

    const loadPageSetReq: LoadPageSetReq = {
      pageSetCode: pageSetCode
    };
    const loadPagesetResp = await loadPageSet(loadPageSetReq);
    console.log('res: ', loadPagesetResp);

    loadPagesetResp.pages.forEach((_page: PageSet, index: number) => {
      if (_page.pageType === 'form') {
        loadPagesetResp.pages[index].components = fromComponents.map((component) => {
          return {
            componentCode: component.id,
            componentType: component.type,
            config: JSON.stringify(fromPageComponentSchemas.get(component.id)?.config),
            editData: JSON.stringify(fromPageComponentSchemas.get(component.id)?.editData),
            parentCode: "",
            blockIndex: 0,
            containerIndex: 0,
          };
        });

        const colComponents: any[] = [];
        console.log(fromColComponentsMap.colComponents);
        fromColComponentsMap.colComponents.forEach((cols: any[][], parentCode: string) => {
            console.log(parentCode, ": cols: ", cols);

            cols && cols &&cols.forEach((col: any[], index: number) => {
                col.forEach((component: any, colIndex: number) => {
                    colComponents.push({
                        componentCode: component.id,
                        componentType: component.type,
                        config: JSON.stringify(fromPageComponentSchemas.get(component.id)?.config),
                        editData: JSON.stringify(fromPageComponentSchemas.get(component.id)?.editData),
                        parentCode: parentCode,
                        blockIndex: index,
                        containerIndex: colIndex,
                    });
                });
            });
        });

        loadPagesetResp.pages[index].components.push(...colComponents);


      } else if (_page.pageType === 'list') {
        loadPagesetResp.pages[index].components = listComponents.map((component) => {
          return {
            componentCode: component.id,
            componentType: component.type,
            config: JSON.stringify(listPageComponentSchemas.get(component.id)?.config),
            editData: JSON.stringify(listPageComponentSchemas.get(component.id)?.editData),
            parentCode: "",
            blockIndex: 0,
            containerIndex: 0,
          };
        });

        const colComponents: any[] = [];
        listColComponentsMap.colComponents.forEach((cols: any[][], parentCode: string) => {
            console.log(parentCode, ": cols: ", cols);
            cols && cols.forEach((col: any[], index: number) => {
                col.forEach((component: any, colIndex: number) => {
                    colComponents.push({
                        componentCode: component.id,
                        componentType: component.type,
                        config: JSON.stringify(listPageComponentSchemas.get(component.id)?.config),
                        editData: JSON.stringify(listPageComponentSchemas.get(component.id)?.editData),
                        parentCode: parentCode,
                        blockIndex: index,
                        containerIndex: colIndex,
                    });
                });
            });
        });

        loadPagesetResp.pages[index].components.push(...colComponents);
      }
    });

    console.log(loadPagesetResp);

    const savePageSetReq: SavePageSetReq = {
      pageSetCode: pageSetCode,
      pageSetName: '',
      pages: loadPagesetResp.pages
    };
    const res = await savePageSet(savePageSetReq);

    console.log('res: ', res);
    if (res) {
      Message.success('保存成功');
    }

    return;
}

export interface LoadPageSetParams {
    pageSetCode: string;
    setFromComponents: Function;
    setFromPageComponentSchemas: Function;
    setListComponents: Function;
    setListPageComponentSchemas: Function;
    setFromColComponentsMap: Function;
    setListColComponentsMap: Function;
}

export async function startLoadPageSet(params: LoadPageSetParams) {
    const { pageSetCode, setFromComponents, setFromPageComponentSchemas, setListComponents, setListPageComponentSchemas ,setFromColComponentsMap, setListColComponentsMap} = params;

    const loadPageSetReq: LoadPageSetReq = {
      pageSetCode: pageSetCode
    };
    const pageSet = await loadPageSet(loadPageSetReq);
    console.log('res: ', pageSet);

    pageSet.pages.forEach((page: PageSet) => {
      let newComponents: any[] = [];
      let newPageComponentSchemas = new Map<string, any>();
      let newColComponentsMap = new Map<string, any[][]>();

      page.components.forEach((component: ComponentConfig) => {
        if (component.parentCode == "" || component.parentCode == null) {
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
        if (component.componentType === LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT) {
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
        if (component.parentCode !== "" && component.parentCode !== null) {
            const colComponents = newColComponentsMap.get(component.parentCode);
            if (colComponents) {
                // 如果列数不够，则初始化列数
                if (colComponents[component.blockIndex].length-1 < component.containerIndex) {

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
                setFromColComponentsMap(component.parentCode, colComponents as any[][]);
            } else if (page.pageType === 'list') {
                setListColComponentsMap(component.parentCode, colComponents as any[][]);
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
        setFromComponents(newComponents);
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
import { useIsRuntimeDev } from '@/hooks/useIsRuntimeDev';
import { Form } from '@arco-design/web-react';
import { getEntityFieldsWithChildren, getPageSetMetaData, type AppEntityField } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  FORM_COMPONENT_TYPES,
  getComponentWidth,
  normalizeFormValues,
  PreviewRender,
  startLoadPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  useFormEditorSignal,
  ENTITY_FIELD_TYPE,
  useAppEntityStore,
  menuDictSignal,
  setMainMetaData as setMainMetaDataToStore,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { forwardRef, Fragment, useEffect, useImperativeHandle, useRef, useState } from 'react';

interface PreviewProps {
  pageSetId: string;
  detailData: any;
}

type ViewMode = 'edit' | 'detail';
type FormStatus = 'default' | 'readonly' | 'hidden';

const ViewModeMap: Record<ViewMode, FormStatus> = {
  edit: 'default',
  detail: 'readonly'
};

const DetailFormMap: Record<FormStatus, FormStatus> = {
  default: 'readonly',
  readonly: 'readonly',
  hidden: 'hidden'
};

/**
 * 根据节点配置、表单配置和视图模式确定最终的表单状态
 *
 * @param nodeConfig - 节点配置（优先级最高）
 * @param formConfig - 表单配置
 * @param viewMode - 视图模式（edit/detail）
 * @returns 最终状态
 *
 * 优先级规则：
 * 1. 如果提供了nodeConfig，则直接使用nodeConfig（节点配置优先）
 * 2. 如果没有nodeConfig且viewMode是edit，则使用formConfig（表单配置）
 *    - 如果formConfig未定义，则使用viewMode
 * 3. 如果没有nodeConfig且viewMode是detail，则使用formConfig转换后的值：
 *    - 将default转换为readonly（detail视图下默认只读）
 *    - 其他状态保持不变
 */
const parseStatus = (nodeConfig: FormStatus, formConfig: FormStatus, viewMode: FormStatus) => {
  if (nodeConfig) {
    return nodeConfig;
  } else if (viewMode === ViewModeMap.edit) {
    return formConfig || viewMode;
  } else if (viewMode === ViewModeMap.detail) {
    return DetailFormMap[formConfig as keyof typeof DetailFormMap];
  }
};

const PreviewContainer = forwardRef<any, PreviewProps>((props: PreviewProps, ref) => {
  // 启用 signal 的响应式更新
  useSignals();
  // 直接访问 signal 的值，useSignals() 会确保组件在 signal 变化时重新渲染
  const { pageSetId, detailData } = props;
  const [form] = Form.useForm();
  const isDev = useIsRuntimeDev();
  const { editPageViewId, mainMetaDataFields, setMainMetaDataFields, subEntities, setSubEntities } = pagesRuntimeSignal;
  const { loadPageComponentSchemas: loadFormPageComponentSchemas } = useFormEditorSignal;
  const { setMainEntity, setSubEntities: setSubEntitiesToStore } = useAppEntityStore();
  const { batchSetAppDict } = menuDictSignal;
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [newCompents, setNewCompents] = useState<any>();
  const [loading, setLoading] = useState(false);
  const executionCount = useRef(0);
  const pageComponentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
  const fieldPerm = detailData?.formData?.fieldPermMap;
  const prevFieldPermRef = useRef();
  useImperativeHandle(ref, () => ({
    getFormData: () => {
      const res = getFormValues();
      return res;
    }
  }));

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaDataId = await getPageSetMetaData({ pageSetId: pageSetId });
    setMainMetaData(mainMetaDataId);
    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaDataId);

    // 设置 pagesRuntimeSignal（原有逻辑）
    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);

    // 同步设置 useAppEntityStore，确保 SelectOne 等组件能获取枚举选项
    setMainMetaDataToStore(entityWithChildren, setMainEntity, setSubEntitiesToStore, batchSetAppDict);
  };

  const updateComponentStatus = async () => {
    if (pageComponentSchemas) {
      const updatedData: {
        [key: string]: any;
      } = {};
      Object.keys(pageComponentSchemas).forEach((key) => {
        const originalItem = pageComponentSchemas[key];
        const secondDataTableName = originalItem?.config?.dataField?.[0];
        const secondDataField = originalItem?.config?.dataField?.[1];
        let newStatus = originalItem?.config?.status;
        if (secondDataField) {
          const nodeConfig = fieldPerm?.[secondDataTableName]?.[secondDataField];
          const formConfig = originalItem?.config?.status;
          const viewMode = ViewModeMap[detailData?.pageView?.viewMode as keyof typeof ViewModeMap];
          newStatus = parseStatus(nodeConfig, formConfig, viewMode);
        }

        updatedData[key] = {
          ...originalItem,
          config: {
            ...(originalItem?.config || {}),
            status: newStatus
          }
        };
      });
      setNewCompents(updatedData);
    }
  };

  const updatePageComponentSchemas = async () => {
    if (pageComponentSchemas && fieldPerm) {
      Object.entries(pageComponentSchemas).map(([key, value]) => {
        const [bpmKey, tKey] = value?.config?.dataField || [];
        const newStatus = fieldPerm?.[bpmKey]?.[tKey];
        const tableName = value?.config?.tableName;
        let newTableStatus = '';
        if (tableName) {
          //子表XsubTable没有dataField,需要用tableName
          newTableStatus = fieldPerm?.[tableName]?.[tableName];
        }
        if (value && value.config) {
          const newConfig = {
            ...value,
            config: {
              ...value.config,
              status: newStatus || newTableStatus,
              subTableConfig: {
                showIndex: true,
                showOperate: true,
                editRow: true,
                operateFixed: true,
                pageSize: 5,
                columnFixed: 0,
                deleteRow: newTableStatus === STATUS_VALUES[STATUS_OPTIONS.DEFAULT]
              }
            }
          };
          useEditorSignalMap.get(editPageViewId.value)!.setPageComponentSchemas(key, newConfig);
          loadFormPageComponentSchemas(useEditorSignalMap.get(editPageViewId.value)!.pageComponentSchemas.value);
          prevFieldPermRef.current = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
        }
      });
    }
  };

  const handleGetData = async () => {
    const res = detailData?.formData;
    let formValues: Record<string, any> = {};
    if (res && res?.data) {
      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
      const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;

      formValues = normalizeFormValues({
        dataItem: res?.data,
        componentSchemas,
        subEntities: subEntities.value,
        subTableComponents,
        setSubTableDataLength: pagesRuntimeSignal.setSubTableDataLength
      });
    }
    console.log('formValues: ', formValues);
    form.setFieldsValue(formValues);
  };
  // 提交表单
  const getFormValues = async () => {
    const fields = form.getFieldsValue();
    console.log(fields, FORM_COMPONENT_TYPES);

    const formData = {} as any;
    const subFormData: Record<string, any[]> = {};
    Object.entries(fields).forEach(([key, value]) => {
      // 处理主表逻辑
      const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldName == key);
      if (field) {
        if (field.fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE || field.fieldType === ENTITY_FIELD_TYPE.FILE.VALUE) {
          // 图片、文件上传 数据处理 转换成后端需要的数据
          formData[field.fieldName] = (value || []).map((ele: any) => {
            return { name: ele.name, id: ele.response?.fileId || ele.id };
          });
        } else {
          formData[field.fieldName] = value;
        }
      }

      // 判断是子表
      const subEntity = subEntities.value.find((ele: any) => ele.childTableName == key);
      // 处理子表逻辑
      if (subEntity) {
        const subTableName = subEntity.childTableName;

        //   过滤空行
        const subTableRows = [] as any;
        subFormData[subTableName] = subTableRows;

        for (const item of value) {
          if (Object.values(item).every((v: any) => v === undefined)) {
            return;
          }
          const keys = Object.keys(item);
          let temp: any = {};
          for (let key of keys) {
            const newKey = key.slice(key.lastIndexOf('.') + 1);
            const subField = (subEntity?.childFields || []).find((f: AppEntityField) => f.fieldName == key);
            if (
              subField?.fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE ||
              subField?.fieldType === ENTITY_FIELD_TYPE.FILE.VALUE
            ) {
              // 图片、文件上传 数据处理 转换成后端需要的数据
              temp[newKey] = (item[key] || []).map((ele: any) => {
                return { name: ele.name, id: ele.response?.fileId };
              });
            } else {
              temp[newKey] = item[key];
            }
          }
          subTableRows.push(temp);
        }
        subFormData[subTableName] = subTableRows;
      }
    });
    console.log('formData:   ', formData);
    console.log('subFormData:   ', subFormData);
    const dataObj = {
      ...formData,
      ...subFormData
    };
    return dataObj;
  };

  const parseData = async () => {
    setLoading(true);
    try {
      await startLoadPageSet({ pageSetId: pageSetId, runtime: true, isDev });
    } finally {
      // 数据加载完成后，延迟一小段时间确保组件已更新
      setTimeout(() => {
        setLoading(false);
      }, 100);
    }
  };

  useEffect(() => {
    updateComponentStatus();
    handleGetData();
  }, [pageComponentSchemas, fieldPerm, detailData]);

  useEffect(() => {
    parseData();
    getMainMetaData(pageSetId);
  }, [pageSetId]);

  useEffect(() => {
    if (fieldPerm && pageComponentSchemas && pageComponentSchemas !== prevFieldPermRef.current) {
      updatePageComponentSchemas();
    }
  }, [pageComponentSchemas, fieldPerm]);

  useEffect(() => {
    setNewCompents(null);
  }, []);

  return (
    <div>
      <Form layout="inline" form={form}>
        {newCompents &&
          useEditorSignalMap.get(editPageViewId.value)?.components.value.map(
            (cp: GridItem) =>
              newCompents[cp.id] &&
              !loading && (
                <Fragment key={cp.id}>
                  {newCompents?.[cp.id]?.config?.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                    <div
                      key={cp.id}
                      style={{
                        width: `calc(${getComponentWidth(
                          useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id],
                          cp.type
                        )} - 8px)`,
                        margin: '4px'
                      }}
                    >
                        <PreviewRender
                          cpId={cp.id}
                          cpType={cp.type}
                          pageType={pageType}
                          pageComponentSchema={newCompents && newCompents[cp.id]}
                          runtime={true}
                          showFromPageData={() => {
                            setPageType(EDITOR_TYPES.FORM_EDITOR);
                          }}
                        />
                    </div>
                  )}
                </Fragment>
              )
          )}
      </Form>
    </div>
  );
});
export default PreviewContainer;

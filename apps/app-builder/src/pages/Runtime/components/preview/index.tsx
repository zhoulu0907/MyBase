import EditorEmpty from '@/assets/images/edit_empty.svg';
import { Button, Form, Spin } from '@arco-design/web-react';
import {
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  type AppEntityField,
  type GetPageSetIdReq
} from '@onebase/app';
import { getHashQueryParam, pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  getComponentWidth,
  getWorkbenchComponentWidth,
  PreviewRender,
  startLoadPageSet,
  startLoadWorkbenchPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  useListEditorSignal,
  useWorkbenchEditorSignal,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useCallback, useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
  pagesetType?: number;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime, pagesetType }) => {
  useSignals();

  const [form] = Form.useForm();

  const {
    components: listComponents,
    pageComponentSchemas: listPageComponentSchemas,
    clearComponents,
    clearPageComponentSchemas
  } = useListEditorSignal;

  const { workbenchComponents, wbComponentSchemas, clearWorkbenchComponents, clearWbComponentSchemas } =
    useWorkbenchEditorSignal;

  const { editPageViewId } = pagesRuntimeSignal;

  const [appId, setAppId] = useState('');
  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [tableName, setTableName] = useState<string>('');
  const [mainMetaDataFields, setMainMetaDataFields] = useState<AppEntityField[]>([]);
  const [editTargetId, setEditTargetId] = useState('');
  const [loading, setLoading] = useState(false);
  const preview = true;

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      setAppId(appId);
    }
  }, [window.location.hash]);

  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setTableName(entityWithChildren.tableName);
    setMainMetaDataFields(entityWithChildren.parentFields);
  };

  const handleGetPageSetId = useCallback(async (menuId: string) => {
    const req: GetPageSetIdReq = { menuId: menuId };
    const res = await getPageSetId(req);
    setPageSetId(res);
  }, []);

  useEffect(() => {
    if (menuId) {
      // 重置所有状态，避免显示上一次的数据
      setPageSetId('');
      setPageType('');
      setMainMetaData('');
      setMainMetaDataFields([]);
      setEditTargetId('');
      form.resetFields();

      // 清空全局 signals 中的组件和 schemas
      clearComponents();
      clearPageComponentSchemas();
      clearWorkbenchComponents();
      clearWbComponentSchemas();

      // 设置加载状态
      setLoading(true);

      // 然后加载新的数据
      handleGetPageSetId(menuId);
    }
  }, [menuId, handleGetPageSetId, clearComponents, clearPageComponentSchemas, form]);

  // 仅在 mainMetaData 或 mainMetaDataFields 变化且存在 editTargetId 时重新获取数据
  useEffect(() => {
    if (editTargetId && tableName && mainMetaDataFields) {
      handleGetData(editTargetId);
    }
  }, [tableName, mainMetaData]);

  useEffect(() => {
    if (pageSetId) {
      const loadData = async () => {
        try {
          await Promise.all([loadPageSetInfo(pageSetId), getMainMetaData(pageSetId)]);
        } finally {
          // 数据加载完成后，延迟一小段时间确保组件已更新
          setTimeout(() => {
            setLoading(false);
          }, 100);
        }
      };

      // 工作台页面不获取主表数据
      if (pagesetType === PageType.WORKBENCH) {
        loadPageSetInfo(pageSetId).finally(() => {
          setLoading(false);
        });
      } else {
        loadData();
      }
    }
    // 优先切换到列表页
    setPageType(pagesetType === PageType.WORKBENCH ? EDITOR_TYPES.WORKBENCH_EDITOR : EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const loadPageSetInfo = async (pageSetId: string) => {
    // 工作台使用独立加载逻辑
    if (pagesetType === PageType.WORKBENCH) {
      await startLoadWorkbenchPageSet({ pageSetId: pageSetId });
      return;
    }

    // 表单和列表使用原有加载逻辑
    await startLoadPageSet({ pageSetId: pageSetId });
  };

  const submitForm = async () => {
    const fields = form.getFieldsValue();
    if (editTargetId) {
      setEditTargetId('');
    }

    setPageType(EDITOR_TYPES.LIST_EDITOR);
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');
    form.resetFields();

    setPageType(EDITOR_TYPES.LIST_EDITOR);
  };

  const showFromPageData = (id: string) => {
    setPageType(EDITOR_TYPES.FORM_EDITOR);
    form.resetFields();

    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
      // 直接获取数据，避免依赖状态变化触发
      if (tableName) {
        handleGetData(id);
      }
    }
  };

  const handleGetData = async (id: string) => {
    // const req: DetailMethodV2Params = {
    //   id: id
    // };
    // const res = await dataMethodDetailV2(tableName, menuId, req);
    // console.log(res);
    // // 遍历 res, 将数据回填到表单
    // const formValues: Record<string, any> = {};
    // if (res) {
    //   const dataItem = res;
    //   if (dataItem && typeof dataItem === 'object') {
    //     Object.entries(dataItem).forEach(([fieldName, value]) => {
    //       formValues[fieldName] = value;
    //     });
    //   }
    // }
    // return res;
  };

  return (
    <div className={styles.previewPage}>
      <div className={styles.content}>
        {loading ? (
          <div className={styles.loading}>
            <Spin size={40} tip="加载中..." />
          </div>
        ) : pageType === EDITOR_TYPES.LIST_EDITOR && listComponents.value.length > 0 ? (
          listComponents.value.map((cp: GridItem) => (
            <Fragment key={cp.id}>
              {listPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: `calc(${getComponentWidth(listPageComponentSchemas.value[cp.id], cp.type)} - 8px)`,
                    margin: '4px'
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                    runtime={runtime}
                    preview={preview}
                    showFromPageData={showFromPageData}
                  />
                </div>
              )}
            </Fragment>
          ))
        ) : pageType === EDITOR_TYPES.LIST_EDITOR && listComponents.value.length === 0 ? (
          <div className={styles.noData}>
            <img src={EditorEmpty} alt="暂无数据" />
          </div>
        ) : null}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form}>
            {useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
              // {formComponents.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {/* {formPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && ( */}
                {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                  STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
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
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                      }
                      runtime={runtime}
                      preview={preview}
                      showFromPageData={() => {
                        setPageType(EDITOR_TYPES.FORM_EDITOR);
                      }}
                    />
                  </div>
                )}
              </Fragment>
            ))}

            <div className={styles.footer}>
              <Button type="primary" disabled={preview} onClick={submitForm}>
                提交
              </Button>
              <Button type="default" onClick={cancelSubmitForm}>
                取消
              </Button>
            </div>
          </Form>
        )}

        {pageType == EDITOR_TYPES.WORKBENCH_EDITOR && (
          <Form layout="inline" form={form}>
            {workbenchComponents.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {wbComponentSchemas.value[cp.id]?.config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: `calc(${getWorkbenchComponentWidth(
                        wbComponentSchemas.value[cp.id],
                        cp.type as WorkbenchComponentType
                      )} - 8px)`,
                      margin: '4px'
                    }}
                  >
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageComponentSchema={wbComponentSchemas.value[cp.id]}
                      runtime={runtime}
                      preview={preview}
                    />
                  </div>
                )}
              </Fragment>
            ))}
          </Form>
        )}
      </div>
    </div>
  );
};

export default PreviewContainer;

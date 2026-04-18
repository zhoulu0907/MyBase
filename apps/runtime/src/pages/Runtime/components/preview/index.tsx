import { useIsRuntimeDev } from '@/hooks/useIsRuntimeDev';
import { pluginBridge } from '@/plugin/bridge';
import ExecuteFlows from '@/utils/flow';
import { Form, Message, Modal } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  createDraft,
  dataMethodCreateV2,
  dataMethodDetailV2,
  dataMethodUpdateV2,
  getEntityFieldsWithChildren,
  getFormDetail,
  getPageSetId,
  getPageSetMetaData,
  listPageView,
  LISTTYPE,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  updateDraft,
  type AppEntityField,
  type DetailMethodV2Params,
  type GetPageSetIdReq,
  type InsertMethodV2Params,
  type UpdateMethodV2Params
} from '@onebase/app';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { getDashBoardURL, pagesRuntimeSignal, TokenManager } from '@onebase/common';
import {
  EDITOR_TYPES,
  ENTITY_FIELD_TYPE,
  normalizeFormValues,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import DetailPop from '../TaskCenter/page/DetailPop';
import DetailRuntime from './DetailRuntime';
import EditRuntime from './EditRuntime';
import FlowPredict from './flowPredict';
import styles from './index.module.less';
import ListRuntime from './ListRuntime';
import WorkbenchRuntime from './WorkbenchRuntime';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
  menuUuid: string;
  pageSetType: PageType;
}

enum PageTypeMap {
  list = 'list'
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime, menuUuid, pageSetType }) => {
  useSignals();

  const [form] = Form.useForm();
  const [detailForm] = Form.useForm();

  const {
    curPage,
    drawerVisible,
    setDrawerVisible,
    editPageViewId,
    detailPageViewId,
    mainMetaDataFields,
    setMainMetaDataFields,
    subEntities,
    setSubEntities,
    bpmInstanceId,
    flows,
    setFlows,
    resetFlows,
    rowDataType,
    rowData,
    setRowData
  } = pagesRuntimeSignal;

  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [tableName, setTableName] = useState<string>('');
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);

  const [editTargetId, setEditTargetId] = useState('');

  const [detailMode, setDetailMode] = useState(true);
  const [refresh, setRefresh] = useState(Date.now());
  const [isPredictVisible, setPredictVisible] = useState(false);
  const [isAdd, setAdd] = useState(false);

  const [dashboardId, setDashboardId] = useState<string>('');
  const [iframeUrl, setIframeUrl] = useState<string>('');
  const isRuntimeDev = useIsRuntimeDev();

  const dashboardType = 'dashboard';
  const resourceUrl = getDashBoardURL();
  const isDev = useIsRuntimeDev();
  const tenantId = TokenManager.getTokenInfo()?.tenantId || '';

  useEffect(() => {
    if (drawerVisible.value) {
      setDetailMode(true);
    } else {
      // 关闭 drawer 时重置 detailForm，避免显示旧数据
      detailForm.resetFields();
    }
  }, [drawerVisible.value, detailForm]);

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaDataId = await getPageSetMetaData({ pageSetId: pageSetId, isDev: isRuntimeDev });

    // 如果 mainMetaDataId 不存在，不继续获取实体数据
    if (!mainMetaDataId) {
      console.log('无主表元数据，跳过实体数据加载');
      return;
    }

    setMainMetaData(mainMetaDataId);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaDataId);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setTableName(entityWithChildren.tableName);

    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);
  };

  const getDashboardId = async (pageSetId: string) => {
    try {
      const res = await listPageView({
        pageSetId: pageSetId,
        isDev: isDev
      });

      if (res && res.pages && res.pages.length > 0) {
        setDashboardId(res.pages[0].id);
        // 如果是 iframe 类型，获取 iframeUrl
        if (res.pages[0].iframeUrl) {
          setIframeUrl(res.pages[0].iframeUrl);
        }
      }
    } catch (error) {
      console.error('获取页面视图失败:', error);
    }
  };

  const handleGetPageSetId = async (menuId: string, isDev: boolean) => {
    const req: GetPageSetIdReq = { menuId: menuId, isDev };
    const res = await getPageSetId(req);
    setPageSetId(res);
  };

  useEffect(() => {
    if (menuId) {
      handleGetPageSetId(menuId, isRuntimeDev);
      setEditTargetId('');
      resetFlows();
    }
  }, [menuId, isRuntimeDev]);

  useEffect(() => {
    // 获取详情数据
    if (editTargetId && tableName && mainMetaDataFields.value.length > 0) {
      handleGetData(editTargetId);
    }
  }, [tableName, mainMetaDataFields.value]);

  useEffect(() => {
    // 工作台、大屏、iframe 页面不获取主表数据
    if (pageSetId && pageSetType !== PageType.WORKBENCH && pageSetType !== PageType.DASHBOARD && pageSetType !== PageType.IFRAME) {
      getMainMetaData(pageSetId);
      setPageType(EDITOR_TYPES.LIST_EDITOR);
      return;
    }

    if (pageSetId && pageSetType === PageType.DASHBOARD) {
      getDashboardId(pageSetId);

      setPageType(EDITOR_TYPES.DASHBOARD_PREVIEW);
      return;
    }

    if (pageSetId && pageSetType === PageType.IFRAME) {
      getDashboardId(pageSetId);

      setPageType(EDITOR_TYPES.IFRAME_PREVIEW);
      return;
    }

    if (pageSetType === PageType.WORKBENCH) {
      setPageType(EDITOR_TYPES.WORKBENCH_EDITOR);
      return;
    }
  }, [pageSetId]);

  // 收集信息弹窗
  const [inputParams, setInputParams] = useState<any>({});

  const [entityParam, setEntityParam] = useState<any>();

  // 提交表单
  /**
   * 提交表单
   * @param isSave 是否保存
   * @param isDraft 是否是草稿
   */
  const submitForm = async (isSave = false, isDraft?: boolean) => {
    if (!isDraft) {
      await form.validate();
    }

    const draftId = form.getFieldValue('draftId');

    !isSave && setSubmitLoading(true);
    const fields = form.getFieldsValue();

    const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
    const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;

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
          const temp: any = {};
          for (const key of keys) {
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

    let subVerify = false;
    const subComponentKeys = Object.keys(subTableComponents);
    subComponentKeys.forEach((e) => {
      if (
        componentSchemas[e]?.config?.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] &&
        componentSchemas[e]?.config?.verify?.required
      ) {
        const subEntity = subEntities.value.find(
          (ele: any) => ele.childEntityUuid == componentSchemas[e]?.config?.subTable
        );
        if (subEntity?.childTableName && !subFormData[subEntity.childTableName]?.length) {
          subVerify = true;
        }
      }
    });
    if (subVerify) {
      Message.warning('子表单至少添加一行');
      setSubmitLoading(false);
      return;
    }

    console.log('formData:   ', formData);
    console.log('subFormData:   ', subFormData);

    // 接口判断 页面触发
    const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const pageId = curFormPage?.id;
    const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
    setInputParams({ ...formData, ...subFormData });

    console.log('editTargetId: ', editTargetId);

    if (editTargetId) {
      const req: UpdateMethodV2Params = {
        id: editTargetId,
        ...formData,
        ...subFormData
      };

      console.log('req: ', req);
      const res = await dataMethodUpdateV2(tableName, menuId, req);
      console.log(res);

      const updateFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.UPDATE)
      );
      setFlows(updateFlows);
      if (res) {
        Message.success('更新成功');
      }
      setEditTargetId('');
      setDrawerVisible(false);
      setTimeout(() => setRefresh(Date.now()), 150);

      setSubmitLoading(false);

      if (curPage?.value?.pageSetType === PageType.BPM) {
        setPageType(EDITOR_TYPES.FORM_EDITOR);
      } else {
        setPageType(EDITOR_TYPES.LIST_EDITOR);
      }
    } else {
      try {
        let res = null;
        const req: InsertMethodV2Params = { ...formData, ...subFormData };
        if (isDraft) {
          res = draftId
            ? await updateDraft(tableName, menuId, { ...req, id: draftId })
            : await createDraft(tableName, menuId, req);
        } else {
          if (curPage?.value?.pageSetType === PageType.BPM) {
            const reqFlow = {
              isDraft: isSave,
              formName:
                curPage?.value?.pages?.find((page: any) => page.pageType === CATEGORY_TYPE.FORM)?.pageName || '',
              businessUuid: menuUuid,
              entity: {
                tableName: tableName,
                data: { ...formData, ...subFormData }
              }
            };
            res = await fetchSubmitInstance(reqFlow as any);

            setPageType(EDITOR_TYPES.FORM_EDITOR);
          } else {
            res = await dataMethodCreateV2(tableName, menuId, req, draftId);
            setPageType(EDITOR_TYPES.LIST_EDITOR);
          }
        }

        const createFlows = (flowRes || []).filter(
          (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE)
        );
        setFlows(createFlows);
        setPredictVisible(false);
        if (res) {
          if (isDraft) {
            Message.success('保存草稿成功');
          } else {
            Message.success('创建成功');
          }
          cancelSubmitForm();
        }
        setTimeout(() => setRefresh(Date.now()), 150);
        setSubmitLoading(false);
      } catch (error) {
        debugger
        Message.error((error as any).message || '创建失败');
        console.error('创建失败', error);
        setPredictVisible(false);
        setSubmitLoading(false);
      }
    }
    setPredictVisible(false);
    // 关闭页面后子表清空
    pagesRuntimeSignal.resetSubTableDataLength();
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
    form.resetFields();
    // 关闭页面后子表清空
    pagesRuntimeSignal.resetSubTableDataLength();
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    setAdd(!id);
    form.resetFields();
    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
      if (tableName) {
        handleGetData(id);
      }
    } else {
      // id为空，属于新增，需要重置子表数据长度为0
      setEditTargetId('');
      pagesRuntimeSignal.resetSubTableDataLength();
    }
    if (toFormPage) {
      setPageType(EDITOR_TYPES.FORM_EDITOR);
      setDrawerVisible(false);
    }
  };

  const handleGetData = async (id: string) => {
    const req: DetailMethodV2Params = {
      id: id
    };
    let res;
    if (rowDataType.value === PageType.BPM) {
      const formDetail = await getFormDetail({
        instanceId: bpmInstanceId.value,
        from: PageTypeMap.list
      });
      res = formDetail?.formData?.data;
    } else {
      res = await dataMethodDetailV2(tableName, menuId, req);
    }
    let formValues: Record<string, any> = {};

    if (res) {
      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
      const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;

      formValues = normalizeFormValues({
        dataItem: res,
        componentSchemas,
        subEntities: subEntities.value,
        subTableComponents,
        setSubTableDataLength: pagesRuntimeSignal.setSubTableDataLength
      });
    }

    console.log('formValues: ', formValues);
    form.setFieldsValue(formValues);
    detailForm.setFieldsValue(formValues);
    setRowData(res);

    return res;
  };

  const onSubmit = () => {
    if (curPage?.value?.pageSetType === PageType.BPM) {
      const fields = form.getFieldsValue();
      const formData: any = {};
      Object.entries(fields).forEach(([key, value]) => {
        // 处理主表逻辑
        const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldId == key);
        if (field) {
          formData[field.fieldId] = value || '';
        }
      });

      setEntityParam({
        tableName,
        data: formData
      });
      setPredictVisible(true);
    } else {
      submitForm();
    }
  };

  const onSaveSubmit = () => {
    submitForm(true);
  };

  const onSaveDraft = () => {
    submitForm(true, true);
  };

  const onBack = () => {
    setDrawerVisible(false);
    setPredictVisible(false);
    setTimeout(() => setRefresh(Date.now()), 150);
  };

  const getBGcolor = { backgroundColor: pageType === EDITOR_TYPES.WORKBENCH_EDITOR ? 'transparent' : '#fff' };

  // iframe 类型需要去掉 padding，铺满屏幕
  const getContentStyle = () => {
    if (pageType === EDITOR_TYPES.IFRAME_PREVIEW) {
      return {
        backgroundColor: '#fff',
        padding: 0,
        height: '100%'
      };
    }
    return getBGcolor;
  };

  // 替换 iframe URL 中的动态参数
  const replaceIframeUrlParams = (url: string) => {
    const appId = TokenManager.getCurAppId() || '';
    const userId = TokenManager.getTokenInfo()?.userId || '';
    return url
      .replace(/\$\{tenantId\}/g, tenantId)
      .replace(/\$\{appId\}/g, appId)
      .replace(/\$\{userId\}/g, userId)
      .replace(/\$\{menuId\}/g, menuId);
  };

  React.useEffect(() => {
    pluginBridge.registerContext({ form });
    return () => {
      pluginBridge.registerContext({ form: undefined });
    };
  }, [form]);

  return (
    <div className={`${styles.previewPage} runtime-preview-formpage`} style={getBGcolor}>
      <div className={styles.content} style={getContentStyle()}>
        {pageType === EDITOR_TYPES.WORKBENCH_EDITOR && <WorkbenchRuntime pageSetId={pageSetId} runtime={runtime} />}

        {(pageType === EDITOR_TYPES.LIST_EDITOR || pageType === EDITOR_TYPES.FORM_EDITOR) && (
          <ListRuntime
            pageSetType={pageSetType}
            pageSetId={pageSetId}
            runtime={runtime}
            showFromPageData={showFromPageData}
            refresh={refresh}
          />
        )}

        {rowDataType.value === PageType.BPM ? (
          <DetailPop
            detailPopVisible={drawerVisible.value}
            setPopVisible={setDrawerVisible}
            onBack={onBack}
            rowData={{ instanceId: bpmInstanceId.value, pageSetId }}
            listType={LISTTYPE.LIST}
          />
        ) : (
          <DetailRuntime
            visible={drawerVisible.value}
            onCancel={() => setDrawerVisible(false)}
            form={detailForm}
            detailMode={detailMode}
            onUpdate={() => submitForm()}
            onCancelUpdate={cancelSubmitForm}
            showFromPageData={showFromPageData}
            editTargetId={editTargetId}
          />
        )}
        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <EditRuntime
            form={form}
            isAdd={isAdd}
            submitLoading={submitLoading}
            onSubmit={onSubmit}
            onSaveSubmit={onSaveSubmit}
            onSaveDraft={onSaveDraft}
            onCancel={cancelSubmitForm}
            menuId={menuId}
            tableName={tableName}
          />
        )}
        {pageType == EDITOR_TYPES.DASHBOARD_PREVIEW && dashboardId && (
          <div className={styles.dashboardPreview}>
            <iframe
              key={`dashboard-${dashboardId}`}
              src={`${resourceUrl}chart/preview/${dashboardId}/${dashboardType}?tenantId=${tenantId}`}
              style={{ width: '100%', height: '100%', border: 'none' }}
              title="Dashboard Preview"
            />
          </div>
        )}
        {pageType == EDITOR_TYPES.IFRAME_PREVIEW && iframeUrl && (
          <div className={styles.iframePreview}>
            <iframe
              key={`iframe-${menuId}`}
              src={replaceIframeUrlParams(iframeUrl)}
              title="iframe Preview"
            />
          </div>
        )}
      </div>

      {/* 收集信息弹窗 */}
      <ExecuteFlows flows={flows.value} inputParams={inputParams}></ExecuteFlows>
      {isPredictVisible && (
        <Modal
          title=""
          visible={isPredictVisible}
          onOk={() => submitForm()}
          onCancel={() => setPredictVisible(false)}
          autoFocus={false}
          focusLock={true}
        >
          <FlowPredict businessId={curPage?.value?.id} entityParam={entityParam} businessUuid={menuUuid} />
        </Modal>
      )}
    </div>
  );
};

export default PreviewContainer;

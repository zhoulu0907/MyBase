import ExecuteFlows from '@/utils/flow';
import { Button, Drawer, Form, Message } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { getHashQueryParam, pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  FORM_COMPONENT_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime }) => {
  useSignals();

  const [form] = Form.useForm();

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

  const { curPage, drawerVisible, setDrawerVisible, editPageViewId, detailPageViewId } = pagesRuntimeSignal;

  const [appId, setAppId] = useState('');
  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [mainMetaDataFields, setMainMetaDataFields] = useState<AppEntityField[]>([]);
  const [editTargetId, setEditTargetId] = useState('');

  // 当前时间戳
  const [detailMode, setDetailMode] = useState(true);
  const [refresh, setRefresh] = useState(Date.now());

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      setAppId(appId);
    }
  }, [window.location.hash]);

  useEffect(() => {
    if (drawerVisible.value) {
      setDetailMode(true);
    }
  }, [drawerVisible.value]);

  // 获取主表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setMainMetaDataFields(entityWithChildren.parentFields);
  };

  useEffect(() => {
    if (menuId) {
      handleGetPageSetId(menuId);
      setEditTargetId('');
    }
  }, [menuId]);

  useEffect(() => {
    if (editTargetId && mainMetaData && mainMetaDataFields.length > 0) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [mainMetaData, mainMetaDataFields]);

  useEffect(() => {
    if (pageSetId) {
      loadPageSetInfo(pageSetId);
      getMainMetaData(pageSetId);
    }
    // 优先切换到列表页
    setPageType(EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const handleGetPageSetId = async (menuId: string) => {
    const req: GetPageSetIdReq = { menuId: menuId };
    const res = await getPageSetId(req);
    setPageSetId(res);
  };

  const loadPageSetInfo = async (pageSetId: string) => {
    startLoadPageSet({ pageSetId: pageSetId });
  };

  // 信息收集弹窗
  const [flows, setFlows] = useState<any[]>([]);
  const [inputParams, setInputParams] = useState<any>({});

  // 提交表单
  const submitForm = async () => {
    const fields = form.getFieldsValue();
    console.log('fields: ', fields);
    console.log('mainMetaDataFields: ', mainMetaDataFields);

    const formData = {} as any;
    const subFormData = [] as any;
    Object.entries(fields).forEach(([key, value]) => {
      console.log('key: ', key, '   value: ', value);
      // 处理主表逻辑
      const field = (mainMetaDataFields || []).find((f: AppEntityField) => f.fieldId == key);
      if (field) {
        console.log('field: ', field);
        formData[field.fieldId] = value;
      }

      // 处理子表逻辑
      if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE)) {
        const subEntityId = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[key]?.config
          ?.subTable;
        subFormData.push({
          subEntityId: subEntityId,
          subData: value
        });
      }
    });

    console.log('formData:   ', formData);
    console.log('subFormData:   ', subFormData);

    // 接口判断 页面触发
    const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const pageId = curFormPage?.id;
    const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
    setInputParams(formData);

    console.log('editTargetId: ', editTargetId);

    if (editTargetId) {
      const req: UpdateMethodParams = {
        entityId: mainMetaData,
        id: editTargetId,
        data: formData,
        subEntities: subFormData
      };
      const res = await dataMethodUpdate(req);
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
      setRefresh(Date.now());
    } else {
      const req: InsertMethodParams = {
        entityId: mainMetaData,
        data: formData,
        subEntities: subFormData
      };

      const res = await dataMethodInsert(req);
      console.log(res);

      const createFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE)
      );
      setFlows(createFlows);

      if (res) {
        Message.success('创建成功');
      }
    }

    setPageType(EDITOR_TYPES.LIST_EDITOR);
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    form.resetFields();

    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
      if (mainMetaData) {
        handleGetData(mainMetaData, id);
      }
    }

    if (toFormPage) {
      setPageType(EDITOR_TYPES.FORM_EDITOR);
    }
  };

  const handleGetData = async (entityId: string, id: string) => {
    const req: DataMethodParam = {
      entityId: entityId,
      id: id
    };
    const res = await dataMethodData(req);
    console.log(res);

    // 遍历 res.data，将数据回填到表单
    if (res && res.data) {
      const fieldIdNameMap: Record<string, string> = {};
      (mainMetaDataFields || []).forEach((field: AppEntityField) => {
        fieldIdNameMap[field.fieldName] = field.fieldId;
      });

      // 只处理第一个数据对象（通常为单条数据）
      const dataItem = Array.isArray(res.data) ? res.data[0] : res.data;

      if (dataItem && typeof dataItem === 'object') {
        const formValues: Record<string, any> = {};
        Object.entries(dataItem).forEach(([fieldName, value]) => {
          const fieldID = fieldIdNameMap[fieldName];
          if (fieldID) {
            formValues[fieldID] = value;
          }
        });
        form.setFieldsValue(formValues);
      }
    }

    return res;
  };

  const toEditMode = () => {
    setDetailMode(false);
  };

  return (
    <div className={styles.previewPage}>
      <div className={styles.content}>
        {pageType === EDITOR_TYPES.LIST_EDITOR &&
          listComponents.value.map((cp: GridItem) => (
            <Fragment key={cp.id}>
              {listPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: getComponentWidth(listPageComponentSchemas.value[cp.id], cp.type)
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                    runtime={runtime}
                    showFromPageData={showFromPageData}
                    refresh={refresh}
                  />
                </div>
              )}
            </Fragment>
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form} requiredSymbol={{ position: 'end' }}>
            {useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                  STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: getComponentWidth(
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id],
                        cp.type
                      )
                    }}
                  >
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                      }
                      runtime={true}
                      showFromPageData={() => {
                        setPageType(EDITOR_TYPES.FORM_EDITOR);
                      }}
                    />
                  </div>
                )}
              </Fragment>
            ))}

            <div className={styles.footer}>
              <Button type="primary" onClick={submitForm}>
                提交
              </Button>
              <Button type="default" onClick={cancelSubmitForm}>
                取消
              </Button>
            </div>
          </Form>
        )}

        {/* 右侧详情抽屉 */}
        <Drawer
          width={600}
          title={
            <div className={styles.drawerTitle}>
              <div>详情</div>
              {detailMode && (
                <Button type="primary" onClick={() => toEditMode()}>
                  编辑
                </Button>
              )}
            </div>
          }
          visible={drawerVisible.value}
          placement="right"
          onCancel={() => setDrawerVisible(false)}
          footer={null}
        >
          <div className={styles.content}>
            <Form layout="inline" form={form} requiredSymbol={{ position: 'end' }}>
              {useEditorSignalMap.get(detailPageViewId.value)?.components.value.map((cp: GridItem) => (
                <Fragment key={cp.id}>
                  {useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                    STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                    <div
                      key={cp.id}
                      className={styles.componentItem}
                      style={{
                        width: getComponentWidth(
                          useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id],
                          cp.type
                        )
                      }}
                    >
                      <PreviewRender
                        cpId={cp.id}
                        cpType={cp.type}
                        pageComponentSchema={
                          useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id]
                        }
                        runtime={true}
                        detailMode={detailMode}
                        showFromPageData={() => {}}
                      />
                    </div>
                  )}
                </Fragment>
              ))}

              {!detailMode && (
                <div className={styles.footer}>
                  <Button type="primary" onClick={submitForm}>
                    更新
                  </Button>
                  <Button type="default" onClick={cancelSubmitForm}>
                    取消
                  </Button>
                </div>
              )}
            </Form>
          </div>
        </Drawer>
      </div>

      {/* 信息收集弹窗 */}
      <ExecuteFlows flows={flows} inputParams={inputParams}></ExecuteFlows>
    </div>
  );
};

export default PreviewContainer;

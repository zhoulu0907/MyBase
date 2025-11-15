import ExecuteFlows from '@/utils/flow';
import { Button, Drawer, Form, Message, Modal } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { pagesRuntimeSignal } from '@onebase/common';
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
  usePageViewEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useCallback, useEffect, useState } from 'react';
import FlowPredict from './flowPredict';
import styles from './index.module.less';
import { initInteractionRule } from './interaction_rule';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime }) => {
  useSignals();

  const [form] = Form.useForm();

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

  const {
    curPage,
    drawerVisible,
    setDrawerVisible,
    editPageViewId,
    detailPageViewId,
    mainMetaDataFields,
    setMainMetaDataFields,
    subEntities,
    setSubEntities
  } = pagesRuntimeSignal;

  const { pageViews, curViewId } = usePageViewEditorSignal;

  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [submitLoading, setSubmitLoading] = useState<boolean>(false);

  const [editTargetId, setEditTargetId] = useState('');

  // 当前时间戳
  const [detailMode, setDetailMode] = useState(true);
  const [refresh, setRefresh] = useState(Date.now());
  const [isPredictVisible, setPredictVisible] = useState(false);
  const [isAdd, setAdd] = useState(false);

  useEffect(() => {
    if (drawerVisible.value) {
      setDetailMode(true);
    }
  }, [drawerVisible.value]);

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);
  };

  useEffect(() => {
    if (menuId) {
      handleGetPageSetId(menuId);
      setEditTargetId('');
    }
  }, [menuId]);

  useEffect(() => {
    if (editTargetId && mainMetaData && mainMetaDataFields.value.length > 0) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [mainMetaData, mainMetaDataFields.value]);

  useEffect(() => {
    if (pageSetId) {
      startLoadPageSet({ pageSetId: pageSetId });
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

  // 信息收集弹窗
  const [flows, setFlows] = useState<any[]>([]);
  const [inputParams, setInputParams] = useState<any>({});

  // 提交表单
  const submitForm = async (isSave = false) => {
    !isSave && setSubmitLoading(true);
    const fields = form.getFieldsValue();
    console.log('fields: ', fields);
    console.log('mainMetaDataFields: ', mainMetaDataFields.value);
    console.log('menuId: ', menuId);

    const formData = {} as any;
    const subFormData = [] as any;
    Object.entries(fields).forEach(([key, value]) => {
      console.log('key: ', key, '   value: ', value);

      // 处理主表逻辑
      const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldId == key);
      if (field) {
        console.log('field: ', field);
        formData[field.fieldId] = value || '';
      }

      // 处理子表逻辑
      if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE)) {
        const subEntityId = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[key]?.config
          ?.subTable;

        //   过滤空行
        const subTableRows = [] as any;
        for (const item of value) {
          if (Object.values(item).every((v: any) => v === undefined)) {
            return;
          }
          const keys = Object.keys(item);
          let temp: any = {};
          for (let key of keys) {
            const newKey = key.slice(key.lastIndexOf('.') + 1);
            temp[newKey] = item[key];
          }
          subTableRows.push(temp);
        }
        subFormData.push({
          subEntityId: subEntityId,
          subData: subTableRows
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
        menuId: menuId,
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

      setSubmitLoading(false);

      if (curPage?.value?.pageSetType === PageType.BPM) {
        setPageType(EDITOR_TYPES.FORM_EDITOR);
      } else {
        setPageType(EDITOR_TYPES.LIST_EDITOR);
      }
    } else {
      try {
        let res = null;
        if (curPage?.value?.pageSetType === PageType.BPM) {
          const reqFlow = {
            isDraft: isSave,
            formName: curPage?.value?.pages?.find((page: any) => page.pageType === CATEGORY_TYPE.FORM)?.pageName || '',
            businessId: curPage?.value?.id,
            entity: {
              entityId: mainMetaData,
              data: formData
            }
          };
          res = await fetchSubmitInstance(reqFlow);
          setPageType(EDITOR_TYPES.FORM_EDITOR);
        } else {
          const req: InsertMethodParams = {
            menuId: menuId,
            entityId: mainMetaData,
            data: formData,
            subEntities: subFormData
          };
          res = await dataMethodInsert(req);

          setPageType(EDITOR_TYPES.LIST_EDITOR);
        }

        const createFlows = (flowRes || []).filter(
          (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE)
        );
        setFlows(createFlows);
        setPredictVisible(false);
        if (res) {
          Message.success('创建成功');
          cancelSubmitForm();
        }
        setSubmitLoading(false);
      } catch (error) {
        Message.error('创建失败');
        setPredictVisible(false);
        setSubmitLoading(false);
      }
    }
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
    form.resetFields();
    setCpStates({});
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    setAdd(!id);
    form.resetFields();

    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
      if (mainMetaData) {
        handleGetData(mainMetaData, id);
      }
    } else {
      // id为空，属于新增，需要重置子表数据长度为0
      setEditTargetId('');
      pagesRuntimeSignal.resetSubTableDataLength();
    }

    if (toFormPage) {
      setPageType(EDITOR_TYPES.FORM_EDITOR);
    }
  };

  const handleGetData = async (entityId: string, id: string) => {
    const req: DataMethodParam = {
      menuId: menuId,
      entityId: entityId,
      id: id
    };
    const res = await dataMethodData(req);
    console.log(res);

    // 遍历 res.data，将数据回填到表单
    const formValues: Record<string, any> = {};

    if (res && res.data) {
      console.log('res.data: ', res.data);
      const fieldIdNameMap: Record<string, string> = {};
      (mainMetaDataFields.value || []).forEach((field: AppEntityField) => {
        fieldIdNameMap[field.fieldName] = field.fieldId;
      });

      console.log('fieldIdNameMap: ', fieldIdNameMap);

      // 只处理第一个数据对象（通常为单条数据）
      const dataItem = Array.isArray(res.data) ? res.data[0] : res.data;

      if (dataItem && typeof dataItem === 'object') {
        Object.entries(dataItem).forEach(([fieldName, value]) => {
          const fieldID = fieldIdNameMap[fieldName];
          if (fieldID) {
            formValues[fieldID] = value;
          }
        });
      }
    }

    // TODO(mickey): remove debug log
    if (res && res.subEntities) {
      console.log('subEntities: ', res.subEntities);

      console.log(useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value);

      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;

      for (const subEntity of res.subEntities) {
        const targetSubEntity = subEntities.value.find((ele: any) => ele.childEntityId == subEntity.subEntityId);
        console.log('已找到目标子表: ', targetSubEntity);

        if (targetSubEntity) {
          Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
            if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE) && schema?.config?.subTable == subEntity.subEntityId) {
              console.log('subEntity.data: ', subEntity.subData);

              pagesRuntimeSignal.setSubTableDataLength(key, (subEntity.subData || []).length);

              for (let idx = 0; idx < (subEntity.subData || []).length; idx++) {
                const keys = Object.keys((subEntity.subData || [])[idx]);
                for (let ele in componentSchemas) {
                  const config = componentSchemas[ele]?.config;
                  const fieldId = config?.dataField?.[1];
                  if (keys.includes(fieldId)) {
                    formValues[`${key}.${idx}.${fieldId}`] = subEntity.subData[idx]?.[fieldId];
                  }
                }
              }
            }
          });
        }
      }
    }

    console.log('formValues: ', formValues);
    form.setFieldsValue(formValues);

    return res;
  };

  const onSubmit = () => {
    if (curPage?.value?.pageSetType === PageType.BPM) {
      setPredictVisible(true);
    } else {
      submitForm();
    }
  };

  const onSaveSubmit = () => {
    submitForm(true);
  };

  const toEditMode = () => {
    setDetailMode(false);
  };

  const [cpStates, setCpStates] = useState<Record<string, any>>({});

  const handleFormValuesChange = async (value: Partial<any>, values: Partial<any>) => {
    const states = await initInteractionRule(
      values,
      pageViews.value[curViewId.value]?.interactionRules,
      useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value
    );

    setCpStates(states);
  };

  const hiddenState = useCallback(
    (cpId: string) => {
      if (cpStates[cpId]?.status !== undefined) {
        return cpStates[cpId].status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN];
      } else {
        if (pageType === EDITOR_TYPES.LIST_EDITOR) {
          return listPageComponentSchemas.value[cpId].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN];
        } else if (pageType === EDITOR_TYPES.FORM_EDITOR) {
          return (
            useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cpId].config.status !==
            STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
          );
        }
      }
    },
    [cpStates, pageType, editPageViewId.value]
  );

  return (
    <div className={styles.previewPage}>
      <div className={styles.content}>
        {pageType === EDITOR_TYPES.LIST_EDITOR &&
          listComponents.value.map((cp: GridItem) => (
            <Fragment key={cp.id}>
              {hiddenState(cp.id) && (
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
                    pageType={pageType}
                    pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                    runtime={runtime}
                    showFromPageData={showFromPageData}
                    refresh={refresh}
                    cpState={cpStates[cp.id]}
                  />
                </div>
              )}
            </Fragment>
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form} onValuesChange={handleFormValuesChange}>
            {useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {hiddenState(cp.id) && (
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
                      pageType={pageType}
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                      }
                      runtime={true}
                      showFromPageData={() => {
                        setPageType(EDITOR_TYPES.FORM_EDITOR);
                      }}
                      cpState={cpStates[cp.id]}
                    />
                  </div>
                )}
              </Fragment>
            ))}

            <div className={styles.footer}>
              {curPage?.value?.pageSetType === PageType.BPM && isAdd && (
                <Button type="primary" onClick={onSaveSubmit} loading={submitLoading}>
                  保存
                </Button>
              )}
              <Button type="primary" onClick={onSubmit} loading={submitLoading}>
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
          width={'60vw'}
          title={
            <div className={styles.drawerTitle}>
              <div>详情</div>
              {/* {detailMode && (
                <Button type="primary" onClick={() => toEditMode()}>
                  编辑
                </Button>
              )} */}
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
                        width: `calc(${getComponentWidth(
                          useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id],
                          cp.type
                        )} - 8px)`,
                        margin: '4px'
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
                  <Button type="primary" onClick={() => submitForm()}>
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
      {isPredictVisible && (
        <Modal
          title=""
          visible={isPredictVisible}
          onOk={() => submitForm()}
          onCancel={() => setPredictVisible(false)}
          autoFocus={false}
          focusLock={true}
        >
          <FlowPredict businessId={curPage?.value?.id} />
        </Modal>
      )}
    </div>
  );
};

export default PreviewContainer;

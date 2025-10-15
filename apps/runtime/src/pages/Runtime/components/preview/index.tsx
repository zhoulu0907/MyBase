import { Button, Drawer, Form, Input, Message, Modal } from '@arco-design/web-react';
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
  triggerFlowExecForm,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { FLOW_MODAL_CANCEL, FLOW_MODAL_TYPE, getHashQueryParam, NodeType, pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
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

  //   const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

  const { curPage, drawerVisible, setDrawerVisible, editPageViewId, detailPageViewId } = pagesRuntimeSignal;

  const [appId, setAppId] = useState('');
  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [mainMetaDataFields, setMainMetaDataFields] = useState<AppEntityField[]>([]);
  const [editTargetId, setEditTargetId] = useState('');

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    if (appId) {
      setAppId(appId);
    }
  }, [window.location.hash]);

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
  }, [editTargetId, mainMetaData, mainMetaDataFields]);

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
  const [infoModalVisibel, setInfoModalVisibel] = useState(false);
  const [outputParams, setOutputParams] = useState({
    modalTitle: '',
    modalType: '',
    fields: [],
    arrange: 1,
    okText: '',
    cancelText: ''
  });
  const [infoForm] = Form.useForm();
  let flowParam: any = {};
  let flowRespon: any = {};

  // 流程多次触发
  const triggerFlows = async () => {
    const res = await triggerFlowExecForm(flowParam);
    flowRespon = res;
    if (res?.success) {
      // 弹窗
      if (res.nodeType === NodeType.MODAL) {
        // 二次确认
        if (res.outputParams?.modalType === FLOW_MODAL_TYPE.CONFIRM) {
          Modal.confirm({
            title: res.outputParams.modalTitle || '确认',
            content: res.outputParams.prompt || '',
            okText: res.outputParams.okText || '确认',
            cancelText: res.outputParams.cancelText || '取消',
            maskClosable: false,
            onOk: async () => {
              if (res.executionEnd) {
                return;
              }
              flowParam = {
                processId: flowParam.processId,
                executionUuid: res.executionUuid || '',
                inputParams: flowParam.inputParams
              };
              await triggerFlows();
            },
            onCancel: () => {
              infoCancel();
            }
          });
        }

        // 信息收集
        if (res.outputParams?.modalType === FLOW_MODAL_TYPE.INFOR) {
          // todo
          // setOutputParams({ ...outputParams, ...res.outputParams });
          // setInfoModalVisibel(true);
        }
      }

      if (res.executionEnd) {
        return;
      }
    }
  };

  // 收集信息弹窗 确定按钮
  const cofirmInfoModal = async () => {
    if (flowRespon.executionEnd) {
      return;
    }
    flowParam = {
      processId: flowParam.processId,
      executionUuid: flowRespon.executionUuid || '',
      inputParams: flowParam.inputParams,
      collectInfo: infoForm.getFieldsValue()
    };
    await triggerFlows();
  };
  const infoCancel = async () => {
    // todo
    console.log('关闭默认终止提醒', flowRespon.outputParams.closeWarn);
    console.log('弹窗取消后提醒', flowRespon.outputParams.cancelWarn);

    // 事件结束 或者 弹窗取消后事件终止
    if (flowRespon.executionEnd || flowRespon.outputParams.afterCancel === FLOW_MODAL_CANCEL.STOP) {
      return;
    }
    flowParam = {
      processId: flowParam.processId,
      executionUuid: flowRespon.executionUuid || '',
      inputParams: flowParam.inputParams
    };
    await triggerFlows();
  };
  // 收集信息弹窗 取消按钮
  const cancaelInfoModal = () => {
    setInfoModalVisibel(false);
    infoCancel();
  };

  const submitForm = async () => {
    const fields = form.getFieldsValue();
    console.log('fields: ', fields);
    console.log('mainMetaDataFields: ', mainMetaDataFields);

    const formData = {} as any;
    Object.entries(fields).forEach(([key, value]) => {
      console.log('key: ', key, '   value: ', value);
      const field = (mainMetaDataFields || []).find((f: AppEntityField) => f.fieldId == key);
      if (field) {
        console.log('field: ', field);
        formData[field.fieldId] = value;
      }
    });

    console.log('formData:   ', formData);

    // 接口判断 页面触发
    const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const pageId = curFormPage?.id;
    const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
    if (editTargetId) {
      const req: UpdateMethodParams = {
        entityId: mainMetaData,
        id: editTargetId,
        data: formData
      };
      const res = await dataMethodUpdate(req);
      console.log(res);

      const updateFlows = (flowRes || []).filter((ele: any) => ele.recordTriggerEvents.includes(TRIGGER_EVENTS.UPDATE));
      for (let ele of updateFlows) {
        flowParam = {
          processId: ele.processId,
          executionUuid: '',
          inputParams: formData
        };
        await triggerFlows();
      }

      if (res) {
        Message.success('更新成功');
      }
      setEditTargetId('');
    } else {
      const req: InsertMethodParams = {
        entityId: mainMetaData,
        data: formData
      };

      const res = await dataMethodInsert(req);
      console.log(res);

      const createFlows = (flowRes || []).filter((ele: any) => ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE));
      for (let ele of createFlows) {
        flowParam = {
          processId: ele.processId,
          executionUuid: '',
          inputParams: formData
        };
        await triggerFlows();
      }

      if (res) {
        Message.success('创建成功');
      }
    }

    setPageType(EDITOR_TYPES.LIST_EDITOR);
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');
    form.resetFields();

    setPageType(EDITOR_TYPES.LIST_EDITOR);
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    form.resetFields();
    if (id === editTargetId) {
      console.log(666666);
      handleGetData(mainMetaData, id);
    }

    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
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
                  />
                </div>
              )}
            </Fragment>
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form}>
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
          title={<span>详情</span>}
          visible={drawerVisible.value}
          placement="right"
          onCancel={() => setDrawerVisible(false)}
          footer={null}
        >
          <div className={styles.content}>
            <Form layout="inline" form={form}>
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
                        detailMode={true}
                        showFromPageData={() => {}}
                      />
                    </div>
                  )}
                </Fragment>
              ))}
            </Form>
          </div>
        </Drawer>
      </div>

      {/* 信息收集弹窗 */}
      <Modal
        visible={infoModalVisibel}
        title={outputParams.modalTitle}
        okText={outputParams.okText}
        cancelText={outputParams.cancelText}
        onOk={cofirmInfoModal}
        onCancel={cancaelInfoModal}
      >
        <Form layout="inline" form={infoForm}>
          {outputParams.fields.map((cp: any) => (
            <Form.Item key={cp.id} label={cp.fieldName} field={cp.fieldName}>
              <Input placeholder="请输入" />
            </Form.Item>
          ))}
        </Form>
      </Modal>
    </div>
  );
};

export default PreviewContainer;

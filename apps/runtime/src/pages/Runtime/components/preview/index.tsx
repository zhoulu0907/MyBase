import ExecuteFlows from '@/utils/flow';
import { Form, Message, Modal } from '@arco-design/web-react';
import DetailPop from '../TaskCenter/page/DetailPop';
import {
  CATEGORY_TYPE,
  dataMethodCreateV2,
  dataMethodDetailV2,
  dataMethodUpdateV2,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  LISTTYPE,
  type AppEntityField,
  type DetailMethodV2Params,
  type GetPageSetIdReq,
  type InsertMethodV2Params,
  type UpdateMethodV2Params
} from '@onebase/app';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  ENTITY_FIELD_TYPE,
  FORM_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
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

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime, menuUuid, pageSetType }) => {
  useSignals();

  const [form] = Form.useForm();

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
    resetFlows
  } = pagesRuntimeSignal;

  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [tableName, setTableName] = useState<string>('');
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
    const mainMetaDataId = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('pageSetId: ', pageSetId);
    console.log('mainMetaDataId: ', mainMetaDataId);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaDataId);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setTableName(entityWithChildren.tableName);

    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);
  };

  useEffect(() => {
    if (menuId) {
      handleGetPageSetId(menuId);
      setEditTargetId('');
      resetFlows();
    }
  }, [menuId]);

  useEffect(() => {
    // 获取详情数据
    if (editTargetId && tableName && mainMetaDataFields.value.length > 0) {
      handleGetData(editTargetId);
    }
  }, [tableName, mainMetaDataFields.value]);

  useEffect(() => {
    // 工作台页面不获取主表数据
    if (pageSetId && pageSetType !== PageType.WORKBENCH) {
      getMainMetaData(pageSetId);
    }
    setPageType(EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const handleGetPageSetId = async (menuId: string) => {
    const req: GetPageSetIdReq = { menuId: menuId };
    const res = await getPageSetId(req);
    setPageSetId(res);
  };

  // 收集信息弹窗
  const [inputParams, setInputParams] = useState<any>({});

  const [entityParam, setEntityParam] = useState<any>();

  // 提交表单
  const submitForm = async (isSave = false) => {
    await form.validate();
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
    setInputParams(formData);

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
        if (curPage?.value?.pageSetType === PageType.BPM) {
          const reqFlow = {
            isDraft: isSave,
            formName: curPage?.value?.pages?.find((page: any) => page.pageType === CATEGORY_TYPE.FORM)?.pageName || '',
            businessUuid: menuUuid,
            entity: {
              tableName: tableName,
              data: { ...formData, ...subFormData }
            }
          };
          res = await fetchSubmitInstance(reqFlow);
          setPageType(EDITOR_TYPES.FORM_EDITOR);
        } else {
          console.log(formData);
          const req: InsertMethodV2Params = { ...formData, ...subFormData };

          console.log(req);

          res = await dataMethodCreateV2(tableName, menuId, req);
          console.log(res);

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
        setTimeout(() => setRefresh(Date.now()), 150);
        setSubmitLoading(false);
      } catch (error) {
        Message.error('创建失败');
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
    }
  };

  const handleGetData = async (id: string) => {
    const req: DetailMethodV2Params = {
      id: id
    };
    const res = await dataMethodDetailV2(tableName, menuId, req);
    console.log(res);

    // 遍历 res, 将数据回填到表单
    const formValues: Record<string, any> = {};

    if (res) {
      const dataItem = res;

      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
      const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;

      //   主表渲染逻辑
      if (dataItem && typeof dataItem === 'object') {
        Object.entries(dataItem).forEach(([fieldName, value]: [string, any]) => {
          const componentSchemaList = Object.keys(componentSchemas);
          const currentKey = componentSchemaList.find((key) =>
            componentSchemas?.[key]?.config?.dataField?.includes(fieldName)
          );
          const currentSchema = componentSchemas?.[currentKey];
          if (
            (currentSchema?.type === FORM_COMPONENT_TYPES.IMG_UPLOAD ||
              currentSchema?.type === FORM_COMPONENT_TYPES.FILE_UPLOAD) &&
            Array.isArray(value)
          ) {
            // 处理文件、图片
            formValues[fieldName] = (value || []).map((ele: any) => ({ ...ele, uid: ele.id }));
          } else if (
            (currentSchema?.type === FORM_COMPONENT_TYPES.RADIO ||
              currentSchema?.type === FORM_COMPONENT_TYPES.SELECT_ONE) &&
            typeof value === 'object' &&
            value !== null
          ) {
            // 处理单选框、下拉列表
            formValues[fieldName] = value?.id;
          } else if (
            (currentSchema?.type === FORM_COMPONENT_TYPES.CHECKBOX ||
              currentSchema?.type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) &&
            Array.isArray(value)
          ) {
            // 处理复选框、下拉多选列表
            formValues[fieldName] = (value || []).map((ele: any) => ele.id);
          } else {
            formValues[fieldName] = value;
          }
        });
      }

      //   子表渲染逻辑
      for (const subEntity of subEntities.value) {
        // 判断 res 对象内的 key 是否等于 subEntity.childTableName
        if (
          dataItem &&
          subEntity.childTableName &&
          Object.prototype.hasOwnProperty.call(dataItem, subEntity.childTableName)
        ) {
          // 子表数据
          const subData = dataItem[subEntity.childTableName];

          Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
            // 找到子表
            if (schema?.config?.subTable == subEntity.childEntityUuid) {
              pagesRuntimeSignal.setSubTableDataLength(key, (subData || []).length);

              // 当前子表单的所有组件id
              const componentIds = subTableComponents[key]?.map((ele: any) => ele.id);
              for (let idx = 0; idx < (subData || []).length; idx++) {
                for (let componentId of componentIds) {
                  const fieldName = componentSchemas[componentId]?.config?.dataField?.[1];
                  if (
                    (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.IMG_UPLOAD ||
                      componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.FILE_UPLOAD) &&
                    Array.isArray(subData[idx]?.[fieldName])
                  ) {
                    formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = (
                      subData[idx]?.[fieldName] || []
                    ).map((e: any) => ({
                      ...e,
                      uid: e.id
                    }));
                  } else if (
                    (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.RADIO ||
                      componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.SELECT_ONE) &&
                    typeof subData[idx]?.[fieldName] === 'object' &&
                    subData[idx]?.[fieldName] !== null
                  ) {
                    // 处理单选框、下拉列表
                    formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = subData[idx]?.[fieldName]?.id;
                  } else if (
                    (componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.CHECKBOX ||
                      componentSchemas[componentId]?.type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE) &&
                    Array.isArray(subData[idx]?.[fieldName])
                  ) {
                    // 处理复选框、下拉多选列表
                    formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = (
                      subData[idx]?.[fieldName] || []
                    ).map((ele: any) => ele.id);
                  } else {
                    formValues[`${subEntity.childTableName}.${idx}.${fieldName}`] = subData[idx]?.[fieldName];
                  }
                }
                // 补充id
                formValues[`${subEntity.childTableName}.${idx}.id`] = subData[idx]?.id;
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

  const onBack = () => {
    setPredictVisible(false);
    setTimeout(() => setRefresh(Date.now()), 150);
  };

  return (
    <div className={`${styles.previewPage} runtime-preview-formpage`}>
      <div className={styles.content}>
        {pageSetType === PageType.WORKBENCH ? (
          <WorkbenchRuntime pageSetId={pageSetId} runtime={runtime} />
        ) : (
          <ListRuntime
            pageSetType={pageSetType}
            pageSetId={pageSetId}
            runtime={runtime}
            showFromPageData={showFromPageData}
            refresh={refresh}
          />
        )}

        {pageSetType === PageType.NORMAL && (
          <DetailRuntime
            visible={drawerVisible.value}
            onCancel={() => setDrawerVisible(false)}
            form={form}
            detailMode={detailMode}
            onUpdate={() => submitForm()}
            onCancelUpdate={cancelSubmitForm}
            showFromPageData={showFromPageData}
            editTargetId={editTargetId}
          />
        )}

        {pageSetType === PageType.BPM && drawerVisible.value && bpmInstanceId.value && (
          <DetailPop
            detailPopVisible={drawerVisible.value}
            setPopVisible={setDrawerVisible}
            onBack={onBack}
            rowData={{ instanceId: bpmInstanceId.value, pageSetId }}
            listType={LISTTYPE.LIST}
          />
        )}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <EditRuntime
            form={form}
            isAdd={isAdd}
            submitLoading={submitLoading}
            onSubmit={onSubmit}
            onSaveSubmit={onSaveSubmit}
            onCancel={cancelSubmitForm}
          />
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

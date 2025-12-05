import ExecuteFlows from '@/utils/flow';
import { Form, Message, Modal } from '@arco-design/web-react';
import {
  CATEGORY_TYPE,
  dataMethodCreateV2,
  dataMethodData,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodV2Params
} from '@onebase/app';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { pagesRuntimeSignal } from '@onebase/common';
import { EDITOR_TYPES, FORM_COMPONENT_TYPES, useEditorSignalMap } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import DetailRuntime from './DetailRuntime';
import EditRuntime from './EditRuntime';
import FlowPredict from './flowPredict';
import styles from './index.module.less';
import ListRuntime from './ListRuntime';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime }) => {
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
    setSubEntities
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
    }
  }, [menuId]);

  useEffect(() => {
    if (editTargetId && tableName && mainMetaDataFields.value.length > 0) {
      // TODO(mickey): mainMetaData 换成 entityName
      //   handleGetData(mainMetaData, editTargetId);
    }
  }, [tableName, mainMetaDataFields.value]);

  useEffect(() => {
    if (pageSetId) {
      getMainMetaData(pageSetId);
    }
    setPageType(EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const handleGetPageSetId = async (menuId: string) => {
    // TODO(mickey多租户): 待runtime接口提供后打开
    const req: GetPageSetIdReq = { menuId: menuId };
    const res = await getPageSetId(req);
    setPageSetId(res);
  };

  // 信息收集弹窗
  const [flows, setFlows] = useState<any[]>([]);
  const [inputParams, setInputParams] = useState<any>({});

  const [entityParam, setEntityParam] = useState<any>();

  // 提交表单
  const submitForm = async (isSave = false) => {
    await form.validate();
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
      const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldName == key);
      if (field) {
        console.log('field: ', field);
        formData[field.fieldName] = value || '';
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
      // TODO(mickey): mainMetaData 换成 entityName, 接口用V2的

      //   const req: UpdateMethodParams = {
      //     menuId: menuId,
      //     entityId: mainMetaData,
      //     id: editTargetId,
      //     data: formData,
      //     subEntities: subFormData
      //   };
      //   const res = await dataMethodUpdate(req);
      //   console.log(res);

      const updateFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.UPDATE)
      );
      setFlows(updateFlows);
      //   if (res) {
      //     Message.success('更新成功');
      //   }
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
            businessId: curPage?.value?.id,
            entity: {
              entityId: mainMetaData,
              data: formData
            }
          };
          res = await fetchSubmitInstance(reqFlow);
          setPageType(EDITOR_TYPES.FORM_EDITOR);
        } else {
          //   const req: InsertMethodParams = {
          //     menuId: menuId,
          //     entityId: mainMetaData,
          //     data: formData,
          //     subEntities: subFormData
          //   };
          console.log(formData);
          const req: InsertMethodV2Params = { ...formData };
          console.log(req);

          res = await dataMethodCreateV2(tableName, menuId, req);
          console.log(res);

          //   res = await dataMethodInsert(req);

          //   setPageType(EDITOR_TYPES.LIST_EDITOR);
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
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
    form.resetFields();
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
        entityId: mainMetaData,
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

  const toEditMode = () => {
    setDetailMode(false);
  };

  return (
    <div className={`${styles.previewPage} runtime-preview-formpage`}>
      <div className={styles.content}>
        <ListRuntime pageSetId={pageSetId} runtime={runtime} showFromPageData={showFromPageData} refresh={refresh} />

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
          <FlowPredict businessId={curPage?.value?.id} entityParam={entityParam} />
        </Modal>
      )}
    </div>
  );
};

export default PreviewContainer;

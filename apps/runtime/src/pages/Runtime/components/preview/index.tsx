import { Button, Form, Message } from '@arco-design/web-react';
import {
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  queryFlowExecForm,
  triggerFlowExecForm,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useFormEditorSignal,
  useListEditorSignal,
  pagesRuntimeDataSignal,
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
  const [form] = Form.useForm();

  useSignals();

  const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

  const { curPage } = pagesRuntimeDataSignal;

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
    }
  }, [menuId]);

  useEffect(() => {
    if (editTargetId && mainMetaData) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [editTargetId, mainMetaData]);

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
    console.log('res', res);
    setPageSetId(res);
  };

  const loadPageSetInfo = async (pageSetId: string) => {
    startLoadPageSet({ pageSetId: pageSetId });
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
    const curFormPage = curPage.value?.pages.find((ele: any) => ele.pageType === 'form');
    const pageId = curFormPage.id;

    const flowRes = await queryFlowExecForm(pageId);

    if (editTargetId) {
      const req: UpdateMethodParams = {
        entityId: mainMetaData,
        id: editTargetId,
        data: formData
      };
      const res = await dataMethodUpdate(req);
      console.log(res);

      const updateFlow = (flowRes || []).filter((ele: any) => ele.recordTriggerEvents.includes('update'));
      for (let ele of updateFlow) {
        const param = {
          processId: ele.processId,
          executionUuid: '',
          inputParams: formData
        };
        await triggerFlowExecForm(param);
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

      const createFlow = (flowRes || []).filter((ele: any) => ele.recordTriggerEvents.includes('create'));
      for (let ele of createFlow) {
        const param = {
          processId: ele.processId,
          executionUuid: '',
          inputParams: formData
        };
        await triggerFlowExecForm(param);
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

  const toCreatePage = (id: string) => {
    setPageType(EDITOR_TYPES.FORM_EDITOR);
    form.resetFields();

    if (id) {
      console.log('edit row id: ', id);
      setEditTargetId(id);
    }
  };

  const handleGetData = async (entityId: string, id: string) => {
    const req: DataMethodParam = {
      entityId: entityId,
      id: id
    };
    const res = await dataMethodData(req);
    console.log(res);
    console.log(res.data);

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
                    toCreatePage={toCreatePage}
                  />
                </div>
              )}
            </Fragment>
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form}>
            {formComponents.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {formPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: getComponentWidth(formPageComponentSchemas.value[cp.id], cp.type)
                    }}
                  >
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageComponentSchema={formPageComponentSchemas.value[cp.id]}
                      runtime={true}
                      toCreatePage={() => {
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
      </div>
    </div>
  );
};

export default PreviewContainer;

import { Button, Form, Message } from '@arco-design/web-react';
import {
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { getHashQueryParam, pagesRuntimeSignal } from '@onebase/common';
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
import React, { Fragment, useCallback, useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime }) => {
  const [form] = Form.useForm();

  useSignals();

  const {
    components: listComponents,
    pageComponentSchemas: listPageComponentSchemas,
    clearComponents,
    clearPageComponentSchemas
  } = useListEditorSignal;

  const { editPageViewId } = pagesRuntimeSignal;

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

  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

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

      // 然后加载新的数据
      handleGetPageSetId(menuId);
    }
  }, [menuId, handleGetPageSetId, clearComponents, clearPageComponentSchemas, form]);

  // 仅在 mainMetaData 或 mainMetaDataFields 变化且存在 editTargetId 时重新获取数据
  useEffect(() => {
    if (editTargetId && mainMetaData && mainMetaDataFields) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [mainMetaDataFields, mainMetaData]);

  useEffect(() => {
    if (pageSetId) {
      loadPageSetInfo(pageSetId);
      getMainMetaData(pageSetId);
    }
    // 优先切换到列表页
    setPageType(EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const loadPageSetInfo = async (pageSetId: string) => {
    startLoadPageSet({ pageSetId: pageSetId });
  };

  const submitForm = async () => {
    const fields = form.getFieldsValue();
    console.log('fields: ', fields);
    console.log('mainMetaDataFields: ', mainMetaDataFields);
    console.log('menuId: ', menuId);

    const formData = {} as any;
    Object.entries(fields).forEach(([key, value]) => {
      const field = (mainMetaDataFields || []).find((f: AppEntityField) => f.fieldId == key);
      if (field) {
        formData[field.fieldId] = value;
      }
    });

    console.log(formData);

    if (editTargetId) {
      const req: UpdateMethodParams = {
        menuId: menuId,
        entityId: mainMetaData,
        id: editTargetId,
        data: formData
      };
      const res = await dataMethodUpdate(req);
      console.log(res);
      if (res) {
        Message.success('更新成功');
      }
      setEditTargetId('');
    } else {
      const req: InsertMethodParams = {
        menuId: menuId,
        entityId: mainMetaData,
        data: formData
      };

      const res = await dataMethodInsert(req);
      console.log(res);
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

  const showFromPageData = (id: string) => {
    setPageType(EDITOR_TYPES.FORM_EDITOR);
    form.resetFields();

    if (id && id !== '') {
      console.log('edit row id: ', id);
      setEditTargetId(id);
      // 直接获取数据，避免依赖状态变化触发
      if (mainMetaData) {
        handleGetData(mainMetaData, id);
      }
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
                    width: `calc(${getComponentWidth(listPageComponentSchemas.value[cp.id], cp.type)} - 8px)`,
                    margin: '4px'
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
      </div>
    </div>
  );
};

export default PreviewContainer;

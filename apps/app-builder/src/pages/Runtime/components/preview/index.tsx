import PreviewRender from '@/pages/Editor/components/render/PreviewRender';
import { getComponentWidth, startLoadPageSet } from '@/pages/Editor/utils/app_resource';
import { EDITOR_TYPES, type GridItem } from '@/pages/Editor/utils/const';
import { useFromEditorStore, useListEditorStore } from '@/store';
import { Button, Form, Message } from '@arco-design/web-react';
import {
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetCode,
  getPageSetMetaData,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetCodeReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuCode: string;
  runtime: boolean;
}

const PreviewContainer: React.FC<PreviewProps> = ({ menuCode, runtime }) => {
  const [form] = Form.useForm();

  const {
    setComponents: setListComponents,
    setPageComponentSchemas: setListPageComponentSchemas,
    setColComponentsMap: setListColComponentsMap,
    pageComponentSchemas: listPageComponentSchemas,
    components: listComponents
  } = useListEditorStore();

  const {
    setComponents: setFromComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    setColComponentsMap: setFromColComponentsMap,
    pageComponentSchemas: formPageComponentSchemas,
    components: formComponents
  } = useFromEditorStore();

  const [appId, setAppId] = useState('');
  const [pageSetCode, setPageSetCode] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [mainMetaDataFields, setMainMetaDataFields] = useState<AppEntityField[]>([]);
  const [editTargetId, setEditTargetId] = useState('');

  useEffect(() => {
    // console.log('window.location.hash: ', window.location.hash);
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const appId = params.get('appId');

      if (appId) {
        setAppId(appId);
      }
    }
  }, [window.location.hash]);

  const getMainMetaData = async (pageSetCode: string) => {
    const mainMetaData = await getPageSetMetaData({ code: pageSetCode });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('entityWithChildren: ', entityWithChildren);

    setMainMetaDataFields(entityWithChildren.parentFields);
  };

  useEffect(() => {
    // console.log('menuCode', menuCode);
    if (menuCode) {
      handleGetPageSetCode(menuCode);
    }
  }, [menuCode]);

  useEffect(() => {
    if (editTargetId && mainMetaData) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [editTargetId, mainMetaData]);

  useEffect(() => {
    // console.log('pageSetCode', pageSetCode);
    if (pageSetCode) {
      loadPageSetInfo(pageSetCode);
      getMainMetaData(pageSetCode);
    }
    // 优先切换到列表页
    setPageType(EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetCode]);

  const handleGetPageSetCode = async (menuCode: string) => {
    const req: GetPageSetCodeReq = {
      menuCode: menuCode
    };
    const res = await getPageSetCode(req);
    console.log('res', res);
    setPageSetCode(res);
  };

  const loadPageSetInfo = async (pgsetCode: string) => {
    startLoadPageSet({
      pageSetCode: pgsetCode,
      setFromComponents: setFromComponents,
      setFromPageComponentSchemas: setFromPageComponentSchemas,
      setListComponents: setListComponents,
      setListPageComponentSchemas: setListPageComponentSchemas,
      setFromColComponentsMap: setFromColComponentsMap,
      setListColComponentsMap: setListColComponentsMap
    });
  };

  const submitForm = async () => {
    const fields = form.getFieldsValue();
    console.log('fields: ', fields);

    const formData = {} as any;
    Object.entries(fields).forEach(([key, value]) => {
      const field = (mainMetaDataFields || []).find((f: AppEntityField) => f.fieldID == key);
      if (field) {
        formData[field.fieldName] = value;
      }
    });
    console.log(formData);

    if (editTargetId) {
      const req: UpdateMethodParams = {
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
        fieldIdNameMap[field.fieldName] = field.fieldID;
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
          listComponents.map((cp: GridItem) => (
            <div
              key={cp.id}
              className={styles.componentItem}
              style={{
                width: getComponentWidth(listPageComponentSchemas.get(cp.id), cp.type)
              }}
            >
              <PreviewRender
                cpId={cp.id}
                cpType={cp.type}
                pageComponentSchema={listPageComponentSchemas.get(cp.id)}
                runtime={runtime}
                toCreatePage={toCreatePage}
              />
            </div>
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form}>
            {formComponents.map((cp: GridItem) => (
              <div
                key={cp.id}
                className={styles.componentItem}
                style={{
                  width: getComponentWidth(formPageComponentSchemas.get(cp.id), cp.type)
                }}
              >
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageComponentSchema={formPageComponentSchemas.get(cp.id)}
                  runtime={true}
                  toCreatePage={() => {
                    setPageType(EDITOR_TYPES.FORM_EDITOR);
                  }}
                />
              </div>
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

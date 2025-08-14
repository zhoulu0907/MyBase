import { useFromEditorStore, useListEditorStore } from '@/store/store_editor';
import { Button, Form, Message } from '@arco-design/web-react';
import {
  dataMethodData,
  dataMethodInsert,
  getAppEntities,
  getAppIdByPageSetCode,
  getPageSetMetaData,
  type AppEntities,
  type DataMethodParam,
  type InsertMethodParams
} from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import PreviewRender from '../../components/render/PreviewRender';
import { getComponentWidth, startLoadPageSet } from '../../utils/app_resource';
import { EDITOR_TYPES, type GridItem } from '../../utils/const';
import styles from './index.module.less';

interface PreviewProps {}

const Preview: React.FC<PreviewProps> = ({}) => {
  const navigate = useNavigate();
  const [appEntities, setAppEntities] = useState<AppEntities>();

  const [appId, setAppId] = useState('');
  const [editTargetId, setEditTargetId] = useState('');

  const {
    setComponents: setFromComponents,
    setPageComponentSchemas: setFromPageComponentSchemas,
    setColComponentsMap: setFromColComponentsMap,
    pageComponentSchemas: formPageComponentSchemas,
    components: formComponents
  } = useFromEditorStore();

  const {
    setComponents: setListComponents,
    setPageComponentSchemas: setListPageComponentSchemas,
    setColComponentsMap: setListColComponentsMap,
    pageComponentSchemas: listPageComponentSchemas,
    components: listComponents
  } = useListEditorStore();

  const [pageSetCode, setPageSetCode] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>();
  const [form] = Form.useForm();

  useEffect(() => {
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pSetCode = params.get('pageSetCode') || '';
      const pType = params.get('pageType') || '';
      const id = params.get('id') || '';

      setPageSetCode(pSetCode);
      setPageType(pType);
      setEditTargetId(id);
    }
  }, [window.location.hash]);

  useEffect(() => {
    if (pageSetCode) {
      console.log('pageSetCode: ', pageSetCode);
      loadPageSetInfo(pageSetCode);
      getAppID(pageSetCode);
      getMainMetaData(pageSetCode);
    }
  }, [pageSetCode]);

  const getMainMetaData = async (pageSetCode: string) => {
    const mainMetaData = await getPageSetMetaData({ code: pageSetCode });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);
  };

  //   获取行数据
  useEffect(() => {
    if (editTargetId && mainMetaData) {
      console.log('editTargetId: ', editTargetId, '   mainMetaData: ', mainMetaData);
      handleGetData(mainMetaData, editTargetId);
    }
  }, [editTargetId, mainMetaData]);

  useEffect(() => {
    if (appId) {
      console.log('appId: ', appId);

      handleGetAppEntities(appId);
    }
  }, [appId]);

  const handleGetAppEntities = async (appId: string) => {
    const res = await getAppEntities(appId);
    console.log('appEntities: ', res);
    if (res) {
      setAppEntities(res);
    }
    return res;
  };

  const handleGetData = async (entityId: string, id: string) => {
    const req: DataMethodParam = {
      entityId: entityId,
      id: id
    };
    const res = await dataMethodData(req);
    console.log(res);
    return res;
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

  const getAppID = async (pageSetCode: string) => {
    const res = await getAppIdByPageSetCode({ code: pageSetCode });
    if (res) {
      setAppId(res);
    }
    return res;
  };

  const submitForm = async () => {
    console.log('提交');
    const fields = form.getFieldsValue();

    const formData = {} as any;
    Object.entries(fields).forEach(([key, value]) => {
      let fieldInfo = null;
      for (const entity of appEntities?.entities || []) {
        const field = (entity.fields || []).find((f: any) => f.fieldID == key);
        if (field) {
          fieldInfo = field;
          break;
        }
      }
      if (fieldInfo) {
        formData[fieldInfo.fieldName] = value;
      }
    });
    console.log(formData);

    const req: InsertMethodParams = {
      entityId: appEntities?.entities[0].entityID || '',
      data: formData
    };

    const res = await dataMethodInsert(req);
    console.log(res);
    if (res) {
      Message.success('插入成功');
      navigate(`/onebase/preview-app/preview?pageSetCode=${pageSetCode}&pageType=${EDITOR_TYPES.LIST_EDITOR}`);
    }
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');
    form.resetFields();

    navigate(`/onebase/preview-app/preview?pageSetCode=${pageSetCode}&pageType=${EDITOR_TYPES.LIST_EDITOR}`);
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
                runtime={true}
              />
            </div>
          ))}

        {pageType === EDITOR_TYPES.FORM_EDITOR && (
          <>
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
          </>
        )}
      </div>
    </div>
  );
};

export default Preview;

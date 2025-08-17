import { useFormEditorSignal, useListEditorSignal } from '@/store/singals/page_editor';
import { getHashQueryParam } from '@/utils/router';
import { Button, Form, Message } from '@arco-design/web-react';
import {
  dataMethodData,
  dataMethodInsert,
  getAppEntities,
  getAppIdByPageSetId,
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

  const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>();
  const [form] = Form.useForm();

  useEffect(() => {
    const pageSetId = getHashQueryParam('pageSetId');
    if (pageSetId) {
      setPageSetId(pageSetId);
    }

    const pageType = getHashQueryParam('pageType');
    if (pageType) {
      setPageType(pageType);
    }

    const editTargetId = getHashQueryParam('id');
    if (editTargetId) {
      setEditTargetId(editTargetId);
    }
  }, [window.location.hash]);

  useEffect(() => {
    if (pageSetId) {
      loadPageSetInfo(pageSetId);
      getAppID(pageSetId);
      getMainMetaData(pageSetId);
    }
  }, [pageSetId]);

  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
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

  const loadPageSetInfo = async (pageSetId: string) => {
    startLoadPageSet({ pageSetId: pageSetId });
  };

  const getAppID = async (pageSetId: string) => {
    const res = await getAppIdByPageSetId({ pageSetId: pageSetId });
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
      navigate(`/onebase/preview-app/preview?pageSetId=${pageSetId}&pageType=${EDITOR_TYPES.LIST_EDITOR}`);
    }
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');
    form.resetFields();
    navigate(`/onebase/preview-app/preview?pageSetId=${pageSetId}&pageType=${EDITOR_TYPES.LIST_EDITOR}`);
  };

  return (
    <div className={styles.previewPage}>
      <div className={styles.content}>
        {pageType === EDITOR_TYPES.LIST_EDITOR &&
          listComponents.value.map((cp: GridItem) => (
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
                runtime={true}
              />
            </div>
          ))}

        {pageType === EDITOR_TYPES.FORM_EDITOR && (
          <>
            <Form layout="inline" form={form}>
              {formComponents.value.map((cp: GridItem) => (
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

import { useFromEditorStore, useListEditorStore } from '@/store';
import { Button, Form } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import PreviewRender from '../../components/render/PreviewRender';
import { getComponentWidth, startLoadPageSet } from '../../utils/app_resource';
import { EDITOR_TYPES, type GridItem } from '../../utils/const';
import styles from './index.module.less';

interface PreviewProps {}

const Preview: React.FC<PreviewProps> = ({}) => {
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
  const [form] = Form.useForm();

  useEffect(() => {
    const hash = window.location.hash;
    const queryIndex = hash.indexOf('?');
    if (queryIndex !== -1) {
      const queryString = hash.substring(queryIndex + 1);
      const params = new URLSearchParams(queryString);
      const pSetCode = params.get('pageSetCode') || '';
      const pType = params.get('pageType') || '';

      setPageSetCode(pSetCode);
      setPageType(pType);
    }
  }, []);

  useEffect(() => {
    console.log('pageSetCode', pageSetCode);
    if (pageSetCode) {
      loadPageSetInfo(pageSetCode);
    }
  }, [pageSetCode]);

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

  const submitForm = () => {
    console.log('提交');
    console.log(form.getFields());
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');
    form.resetFields();

    // navigator()
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
              onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                e.stopPropagation();
                console.log('点击组件: ', cp.id);
              }}
            >
              <PreviewRender cpId={cp.id} cpType={cp.type} pageComponentSchema={listPageComponentSchemas.get(cp.id)} />
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
                    width: getComponentWidth(listPageComponentSchemas.get(cp.id), cp.type)
                  }}
                  onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                    e.stopPropagation();
                    console.log('点击组件: ', cp.id);
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={formPageComponentSchemas.get(cp.id)}
                  />
                </div>
              ))}

              <div className={styles.footer}>
                <Button type="primary" onClick={submitForm}>
                  {' '}
                  提交{' '}
                </Button>
                <Button type="default" onClick={cancelSubmitForm}>
                  {' '}
                  取消{' '}
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

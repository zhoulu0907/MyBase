import { useFromEditorStore, useListEditorStore } from '@/store';
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

  return (
    <div className={styles.previewPage}>
      <div className={styles.workspaceContent}>
        {(pageType === EDITOR_TYPES.FORM_EDITOR ? formComponents : listComponents).map((cp: GridItem) => (
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
              pageComponentSchema={
                pageType === EDITOR_TYPES.FORM_EDITOR
                  ? formPageComponentSchemas.get(cp.id)
                  : listPageComponentSchemas.get(cp.id)
              }
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Preview;

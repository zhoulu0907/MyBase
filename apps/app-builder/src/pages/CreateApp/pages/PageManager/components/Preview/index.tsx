import PreviewRender from '@/pages/Editor/components/render/PreviewRender';
import { getComponentWidth, startLoadPageSet } from '@/pages/Editor/utils/app_resource';
import { type GridItem } from '@/pages/Editor/utils/const';
import { useFromEditorStore, useListEditorStore } from '@/store';
import { getPageSetCode, type GetPageSetCodeReq } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuCode: string;
}

const PageManagerPreview: React.FC<PreviewProps> = ({ menuCode }) => {
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
    setColComponentsMap: setFromColComponentsMap
    // pageComponentSchemas: formPageComponentSchemas,
    // components: formComponents
  } = useFromEditorStore();

  const [pageSetCode, setPageSetCode] = useState('');

  useEffect(() => {
    console.log('menuCode', menuCode);
    if (menuCode) {
      handleGetPageSetCode(menuCode);
    }
  }, [menuCode]);

  useEffect(() => {
    console.log('pageSetCode', pageSetCode);
    if (pageSetCode) {
      loadPageSetInfo(pageSetCode);
    }
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

  return (
    <div className={styles.previewPage}>
      <div className={styles.workspaceContent}>
        {listComponents.map((cp: GridItem) => (
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
      </div>
    </div>
  );
};

export default PageManagerPreview;

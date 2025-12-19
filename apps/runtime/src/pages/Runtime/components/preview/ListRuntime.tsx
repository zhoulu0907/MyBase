import DevelopEmpty from '@/assets/images/develop_empty.svg';

import { Spin } from '@arco-design/web-react';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import styles from './index.module.less';

interface ListRuntimeProps {
  pageSetId: string;
  runtime: boolean;
  showFromPageData: (id: string, toFormPage?: boolean) => void;
  refresh: number;
}

const ListRuntime: React.FC<ListRuntimeProps> = ({ pageSetId, runtime, showFromPageData, refresh }) => {
  useSignals();

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (pageSetId) {
      const loadData = async () => {
        setLoading(true);
        try {
          await startLoadPageSet({ pageSetId, runtime: true });
        } finally {
          // 数据加载完成后，延迟一小段时间确保组件已更新
          setTimeout(() => {
            setLoading(false);
          }, 100);
        }
      };
      loadData();
    }
  }, [pageSetId]);

  return (
    <>
      {loading ? (
        <div className={styles.loading}>
          <Spin size={40} tip="加载中..." />
        </div>
      ) : listComponents.value.length > 0 ? (
        listComponents.value.map((cp: GridItem) => {
          const schema = listPageComponentSchemas.value[cp.id];
          const sanitizedSchema = {
            ...schema
          };
          return (
            <Fragment key={cp.id}>
              <div
                key={cp.id}
                className={styles.componentItem}
                style={{
                  width: `calc(${getComponentWidth(sanitizedSchema, cp.type)} - 8px)`,
                  margin: '4px'
                }}
              >
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageType={EDITOR_TYPES.LIST_EDITOR}
                  pageComponentSchema={sanitizedSchema}
                  runtime={runtime}
                  showFromPageData={showFromPageData}
                  refresh={refresh}
                />
              </div>
            </Fragment>
          );
        })
      ) : (
        <div className={styles.noData}>
          <img src={DevelopEmpty} alt="暂无数据" />
        </div>
      )}
    </>
  );
};

export default ListRuntime;

import DevelopEmpty from '@/assets/images/develop_empty.svg';

import { useIsRuntimeDev } from '@/hooks/useIsRuntimeDev';
import { Spin } from '@arco-design/web-react';
import { PageType } from '@onebase/app';
import { menuPermissionSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';

const FLOATING_COMPONENT_TYPES = ['XChatbot'];

const isFloatingComponent = (type: string): boolean => {
  return FLOATING_COMPONENT_TYPES.includes(type);
};
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import styles from './index.module.less';

interface ListRuntimeProps {
  pageSetId: string;
  runtime: boolean;
  showFromPageData: (id: string, toFormPage?: boolean) => void;
  refresh: number;
  pageSetType?: PageType;
}

const ListRuntime: React.FC<ListRuntimeProps> = ({ pageSetId, runtime, showFromPageData, refresh, pageSetType }) => {
  useSignals();

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const { menuPermission } = menuPermissionSignal;
  const [loading, setLoading] = useState(false);
  const isDev = useIsRuntimeDev();

  useEffect(() => {
    if (pageSetId) {
      const loadData = async () => {
        setLoading(true);
        try {
          await startLoadPageSet({
            pageSetId,
            runtime: true,
            isDev,
            allowViewUuids: menuPermission.value?.viewUuids || []
          });
        } finally {
          // 数据加载完成后，延迟一小段时间确保组件已更新
          setTimeout(() => {
            setLoading(false);
          }, 100);
        }
      };
      loadData();
    }
  }, [pageSetId, isDev]);

  return (
    <>
      {loading ? (
        <div className={styles.loading}>
          <Spin size={40} tip="加载中..." />
        </div>
      ) : listComponents.value.length > 0 ? (
        <>
          {/* 浮动组件 */}
          {listComponents.value
            .filter((cp: GridItem) => isFloatingComponent(cp.type))
            .map((cp: GridItem) => {
              const floatingConfig = listPageComponentSchemas.value[cp.id]?.config?.floatingConfig;
              const right = floatingConfig?.right ?? 80;
              const bottom = floatingConfig?.bottom ?? 80;
              const width = floatingConfig?.width ?? 80;
              const height = floatingConfig?.height ?? 80;

              return (
                <div
                  key={cp.id}
                  style={{
                    position: 'fixed',
                    right,
                    bottom,
                    width,
                    height,
                    zIndex: 100
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageType={EDITOR_TYPES.LIST_EDITOR}
                    pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                    runtime={true}
                    showFromPageData={showFromPageData}
                    refresh={refresh}
                    pageSetType={pageSetType}
                  />
                </div>
              );
            })}

          {/* 普通组件 */}
          {listComponents.value
            .filter((cp: GridItem) => !isFloatingComponent(cp.type))
            .map((cp: GridItem) => {
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
                      pageSetType={pageSetType}
                    />
                  </div>
                </Fragment>
              );
            })}
        </>
      ) : (
        <div className={styles.noData}>
          <img src={DevelopEmpty} alt="暂无数据" />
        </div>
      )}
    </>
  );
};

export default ListRuntime;

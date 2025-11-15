import React, { Fragment, useEffect } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import {
  EDITOR_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
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

  useEffect(() => {
    if (pageSetId) {
      startLoadPageSet({ pageSetId });
    }
  }, [pageSetId]);

  return (
    <>
      {listComponents.value.map((cp: GridItem) => (
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
                pageType={EDITOR_TYPES.LIST_EDITOR}
                pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                runtime={runtime}
                showFromPageData={showFromPageData}
                refresh={refresh}
              />
            </div>
          )}
        </Fragment>
      ))}
    </>
  );
};

export default ListRuntime;
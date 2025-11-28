import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect } from 'react';
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
      {listComponents.value.map((cp: GridItem) => {
        const schema = listPageComponentSchemas.value[cp.id];
        const sanitizedSchema = {
          ...schema,
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
      })}
    </>
  );
};

export default ListRuntime;

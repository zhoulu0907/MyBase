import {
  PreviewRender,
  startLoadPageSet,
  getWorkbenchComponentWidth,
  useWorkbenchEditorSignal,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect } from 'react';
import styles from './index.module.less';

interface WorkbenchRuntimeProps {
  pageSetId: string;
  runtime: boolean;
}

const WorkbenchRuntime: React.FC<WorkbenchRuntimeProps> = ({ pageSetId, runtime }) => {
  useSignals();

  const { components: workbenchComponents, pageComponentSchemas: workbenchPageComponentSchemas } =
    useWorkbenchEditorSignal;

  useEffect(() => {
    console.log('workbench runtime pageSetId: ', pageSetId);
    if (pageSetId) {
      startLoadPageSet({ pageSetId, runtime: true });
    }
  }, [pageSetId]);

  return (
    <>
      {workbenchComponents.value.map((cp: GridItem) => {
        const schema = workbenchPageComponentSchemas.value[cp.id];
        const sanitizedSchema = {
          ...schema
        };
        console.log('cp: ', sanitizedSchema);
        return (
          <Fragment key={cp.id}>
            <div
              key={cp.id}
              className={styles.componentItem}
              style={{
                width: `calc(${getWorkbenchComponentWidth(sanitizedSchema, cp.type as WorkbenchComponentType)} - 8px)`,
                margin: '4px'
              }}
            >
              <PreviewRender
                cpId={cp.id}
                cpType={cp.type}
                // pageType={EDITOR_TYPES.WORKBENCH_EDITOR}
                pageComponentSchema={sanitizedSchema}
                runtime={runtime}
                preview={true}
              />
            </div>
          </Fragment>
        );
      })}
    </>
  );
};

export default WorkbenchRuntime;

import {
  getWorkbenchComponentWidth,
  PreviewRender,
  startLoadWorkbenchPageSet,
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

  const { workbenchComponents, wbComponentSchemas } = useWorkbenchEditorSignal;

  useEffect(() => {
    console.log('workbench runtime pageSetId: ', pageSetId);
    if (pageSetId) {
      startLoadWorkbenchPageSet({ pageSetId });
    }
  }, [pageSetId]);

  useEffect(() => {
    const ele = document.getElementById('runtime-content');
    if (ele && ele.style) {
      ele.style.backgroundColor = '#F2F3F5';
    }
  }, []);

  return (
    <>
      {workbenchComponents.value.map((cp: GridItem) => {
        const schema = wbComponentSchemas.value[cp.id];
        const sanitizedSchema = {
          ...schema
        };
        // console.log('cp: ', sanitizedSchema);
        return (
          <Fragment key={cp.id}>
            <div
              key={cp.id}
              className={styles.componentItem}
              style={{
                width: `calc(${getWorkbenchComponentWidth(sanitizedSchema, cp.type as WorkbenchComponentType)} - 8px)`,
                margin: '10px 0'
              }}
            >
              <PreviewRender
                cpId={cp.id}
                cpType={cp.type}
                // pageType={EDITOR_TYPES.WORKBENCH_EDITOR}
                pageComponentSchema={sanitizedSchema}
                runtime={runtime}
                preview={false}
              />
            </div>
          </Fragment>
        );
      })}
    </>
  );
};

export default WorkbenchRuntime;

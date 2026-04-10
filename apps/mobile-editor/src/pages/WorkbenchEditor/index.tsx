import { Form as MobileForm } from '@arco-design/mobile-react';
import { Fragment } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WorkbenchPanel,
  useWorkbenchSignal
} from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';
import WorkbenchWorkspace from '../../components/wb-workspace';
import type { EditorWorkspaceProps } from '../../components/wb-workspace/types/workbench-component';
import styles from './index.module.less';

/**
 * 编辑模式渲染
 */
const EditorMode: React.FC<{ workspaceProps: EditorWorkspaceProps['props'] }> = ({ workspaceProps }) => (
  <div className={styles.wbEditorPage}>
    <WorkbenchPanel props={workspaceProps} />
    <WorkbenchWorkspace props={workspaceProps} />
  </div>
);

const WorkbenchEditor: React.FC<EditorWorkspaceProps & { instanceId: string }> = ({ instanceId, props }) => {
  useSignals();

  const workbenchSignal = useWorkbenchSignal();
  const isPreview = instanceId.includes('preview');
  const components = props.workbenchComponents ?? workbenchSignal.workbenchComponents ?? [];
  const schemas = props.wbComponentSchemas ?? workbenchSignal.wbComponentSchemas ?? {};

  const workspaceProps = {
    ...props,
    workbenchComponents: components,
    wbComponentSchemas: schemas,
    setWorkbenchComponents: props.setWorkbenchComponents ?? workbenchSignal.setWorkbenchComponents,
    setWbComponentSchemas: props.setWbComponentSchemas ?? workbenchSignal.setWbComponentSchemas,
    delWbComponentSchemas: props.delWbComponentSchemas ?? workbenchSignal.delWbComponentSchemas,
    delWorkbenchComponents: props.delWorkbenchComponents ?? workbenchSignal.delWorkbenchComponents,
    curComponentID: props.curComponentID ?? workbenchSignal.curComponentID,
    setCurComponentID: props.setCurComponentID ?? workbenchSignal.setCurComponentID,
    clearCurComponentID: props.clearCurComponentID ?? workbenchSignal.clearCurComponentID,
    setCurComponentSchema: props.setCurComponentSchema ?? workbenchSignal.setCurComponentSchema,
    showDeleteButton: props.showDeleteButton ?? workbenchSignal.showDeleteButton,
    setShowDeleteButton: props.setShowDeleteButton ?? workbenchSignal.setShowDeleteButton,
  };

  /**
   * 预览模式渲染
   */
  if (isPreview) {
    return (
      <MobileForm layout="inline">
        {components
          .filter(cp => schemas[cp.id]?.config?.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN])
          .map(cp => (
            <Fragment key={cp.id}>
              <div key={cp.id} className={styles.componentItem} style={{ width: '100%', margin: '4px' }}>
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageComponentSchema={schemas[cp.id]}
                  runtime={true}
                />
              </div>
            </Fragment>
          ))}
      </MobileForm>
    );
  }

  return <EditorMode workspaceProps={workspaceProps} />;
};

export { WorkbenchEditor };

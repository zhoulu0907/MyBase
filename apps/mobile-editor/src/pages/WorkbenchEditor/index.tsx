import { Form as MobileForm } from '@arco-design/mobile-react';
import React, { Fragment, useMemo } from 'react';
import { useSignals } from '@preact/signals-react/runtime';
import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  WorkbenchPanel,
  useWorkbenchSignal,
  type GridItem
} from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';
import WorkbenchWorkspace from '../../components/wb-workspace';
import type { EditorWorkspaceProps } from '../../components/wb-workspace/types/workbench-component';
import styles from './index.module.less';

/**
 * 合并 props 和 signal 数据，props 优先
 */
function useMergedWorkbenchData(props: EditorWorkspaceProps['props']) {
  const workbenchSignal = useWorkbenchSignal();

  console.log('--2--props: ', props);

  const currentComponents = useMemo(() => {
    return props.workbenchComponents ?? workbenchSignal.workbenchComponents ?? [];
  }, [props.workbenchComponents, workbenchSignal.workbenchComponents]) as GridItem[];

  const currentSchemas = useMemo(() => {
    return props.wbComponentSchemas ?? workbenchSignal.wbComponentSchemas ?? {};
  }, [props.wbComponentSchemas, workbenchSignal.wbComponentSchemas]) as Record<string, any>;

  // 合并后的 workspace props
  const workspaceProps = useMemo(() => ({
    ...props,
    wbComponentSchemas: currentSchemas,
    workbenchComponents: currentComponents,
    setWorkbenchComponents: props.setWorkbenchComponents ?? workbenchSignal.setWorkbenchComponents,
    setWbComponentSchemas: props.setWbComponentSchemas ?? workbenchSignal.setWbComponentSchemas,
    delWbComponentSchemas: props.delWbComponentSchemas ?? workbenchSignal.delWbComponentSchemas,
    delWorkbenchComponents: props.delWorkbenchComponents ?? workbenchSignal.delWorkbenchComponents,
    // 关键：组件选中相关的函数和状态
    curComponentID: props.curComponentID ?? workbenchSignal.curComponentID,
    setCurComponentID: props.setCurComponentID ?? workbenchSignal.setCurComponentID,
    clearCurComponentID: props.clearCurComponentID ?? workbenchSignal.clearCurComponentID,
    setCurComponentSchema: props.setCurComponentSchema ?? workbenchSignal.setCurComponentSchema,
    showDeleteButton: props.showDeleteButton ?? workbenchSignal.showDeleteButton,
    setShowDeleteButton: props.setShowDeleteButton ?? workbenchSignal.setShowDeleteButton,
  }), [props, currentSchemas, currentComponents, workbenchSignal]);

  return { currentComponents, currentSchemas, workspaceProps };
}

/**
 * 预览模式渲染
 */
const PreviewMode: React.FC<{ components: GridItem[]; schemas: Record<string, any> }> = ({
  components,
  schemas
}) => (
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

/**
 * 编辑模式渲染
 */
const EditorMode: React.FC<{ workspaceProps: EditorWorkspaceProps['props'] }> = ({ workspaceProps }) => (
  <div className={styles.wbEditorPage}>
    <WorkbenchPanel />
    <WorkbenchWorkspace props={workspaceProps} />
  </div>
);

const WorkbenchEditor: React.FC<EditorWorkspaceProps & { instanceId: string }> = ({ instanceId, props }) => {
  useSignals();

  const { currentComponents, currentSchemas, workspaceProps } = useMergedWorkbenchData(props);
  const isPreview = instanceId.includes('preview');

  if (isPreview) {
    return <PreviewMode components={currentComponents} schemas={currentSchemas} />;
  }

  return <EditorMode workspaceProps={workspaceProps} />;
};

export { WorkbenchEditor };

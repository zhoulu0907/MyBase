import type { EditorProps } from '@/common/props';
import EditorWorkspace from '@/components/workspace/Workspace';
import { Form as MobileForm } from '@arco-design/mobile-react';
import type { AppEntityField } from '@onebase/app';
import { EditorPanel, STATUS_OPTIONS, STATUS_VALUES, type GridItem } from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';
import React, { Fragment } from 'react';
import styles from './index.module.less';
interface FormEditorProps {
  props: EditorProps & {
    useEditorSignalMap: Map<string, any>;
    batchDelPageComponentSchemas: (componentIds: Set<string>) => void;
    batchDelLayoutSubComponents: (componentIds: Set<string>) => void;
    subTableComponents: Record<string, AppEntityField[]>;
    setSubTableComponents: (subTableComponentId: string, componentIds: AppEntityField[]) => void;
    batchDelSubTableComponents: (componentIds: Set<string>) => void;
    usePageViewEditorSignal: () => Map<string, any>;
    useFormEditorSignal: () => Map<string, any>;
  };
}

const FormEditor: React.FC<FormEditorProps & { instanceId: string }> = ({ instanceId, props }) => {
  if (!props.components) {
    return null;
  }
  const getFormContent = () => {
    const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = props;
    return formComponents?.map((cp: GridItem) => (
      <Fragment key={cp.id}>
        {formPageComponentSchemas[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
          <div
            key={cp.id}
            className={styles.componentItem}
            style={{
              width: `100%`,
              margin: '4px'
            }}
          >
            <PreviewRender
              cpId={cp.id}
              cpType={cp.type}
              pageComponentSchema={formPageComponentSchemas[cp.id]}
              runtime={true}
            />
          </div>
        )}
      </Fragment>
    ));
  };

  if (instanceId.indexOf('preview') !== -1) {
    return <MobileForm layout="inline">{getFormContent()}</MobileForm>;
  }

  return (
    <div className={styles.formEditorPage}>
      <EditorPanel />
      <EditorWorkspace props={props} />
    </div>
  );
};

export { FormEditor };

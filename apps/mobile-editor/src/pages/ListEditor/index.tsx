import type { EditorProps } from '@/common/props';
import { Form as MobileForm } from '@arco-design/mobile-react';
import React, { Fragment } from 'react';
import { EditorPanel } from '@onebase/ui-kit';

import {
  STATUS_OPTIONS,
  STATUS_VALUES,
  type GridItem
} from '@onebase/ui-kit';
import {
  PreviewRender
} from '@onebase/ui-kit-mobile';

import styles from './index.module.less';
import EditorWorkspace from '../../components/workspace/Workspace';

interface FormEditorProps {
  props: EditorProps & { drag: boolean };
}

const ListEditor: React.FC<FormEditorProps & { instanceId: string }> = ({ instanceId, props }) => {
  if (!props.components) {
    return null;
  }
  const getFormContent = () => {
    const {
      components: listComponents,
      pageComponentSchemas: listPageComponentSchemas
    } = props;
    return (
      listComponents?.map((cp: GridItem) => (
        <Fragment key={cp.id}>
          {listPageComponentSchemas[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
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
                pageComponentSchema={listPageComponentSchemas[cp.id]}
                runtime={true}
                preview={true}
              />
            </div>
          )}
        </Fragment>
      ))
    )
  }

  if (instanceId.indexOf('preview') !== -1) {
    return (
      <MobileForm layout="inline">
        {getFormContent()}
      </MobileForm>
    );
  }

  return (
    <div className={styles.formEditorPage}>
      {props.editMode?.value === 'mobile' && (
        <>
          <EditorPanel />
          <EditorWorkspace props={props} />
        </>
      )}
    </div>
  );
};

export { ListEditor };

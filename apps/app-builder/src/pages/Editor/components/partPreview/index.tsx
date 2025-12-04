import { Button, Drawer, Form } from '@arco-design/web-react';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useFormEditorSignal,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import classNames from 'classnames';
import { loadMicroApp, initGlobalState, type MicroApp } from "qiankun";

import React, { Fragment, useEffect, useRef } from 'react';
import styles from './index.module.less';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import { EditMode } from '@onebase/common';

interface PartPreviewProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  pageType: string;
}

/**
 * partPreview 组件
 * 用于预览页面组件的展示
 */
const PartPreview: React.FC<PartPreviewProps> = ({ visible, setVisible, pageType }) => {
  const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const { editMode } = currentEditorSignal;
  const mobileEditorPreviewRef = useRef<MicroApp | null>(null);

  const qiankunActions = initGlobalState({
    components: pageType === EDITOR_TYPES.FORM_EDITOR ? formComponents.value : listComponents.value,
    pageComponentSchemas: pageType === EDITOR_TYPES.FORM_EDITOR ? formPageComponentSchemas.value : listPageComponentSchemas.value
  })
  useEffect(() => {
    console.log("loading mobile-editor-preview-list");

    const mobileEditorPreview = loadMicroApp({
      name: "mobile-editor-preview-list",
      entry: "//localhost:4401",
      container: "#mobile-editor-preview-list",
      props: {
        instanceId: "mobile-editor-preview-list",
        onGlobalStateChange: qiankunActions.onGlobalStateChange,
        setGlobalState: qiankunActions.setGlobalState,
        offGlobalStateChange: qiankunActions.offGlobalStateChange,
      },
    });
    mobileEditorPreviewRef.current = mobileEditorPreview;

    return () => {
      mobileEditorPreview?.unmount();
    };
  }, []);

  const getFormContent = () => {
    return (
      formComponents.value.map((cp: GridItem) => (
        <Fragment key={cp.id}>
          {formPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
            <div
              key={cp.id}
              className={styles.componentItem}
              style={{
                width: `calc(${getComponentWidth(formPageComponentSchemas.value[cp.id], cp.type)} - 8px)`,
                margin: '4px'
              }}
            >
              <PreviewRender
                cpId={cp.id}
                cpType={cp.type}
                pageComponentSchema={formPageComponentSchemas.value[cp.id]}
                runtime={true}
                preview={true}
              />
            </div>
          )}
        </Fragment>
      ))
    )
  }
  return (
    <Drawer
      placement="bottom"
      height={'80vh'}
      visible={visible}
      title="预览页面"
      footer={null}
      onCancel={() => {
        setVisible(false);
      }}
      bodyStyle={{ background: '#F2F3F5', padding: '0' }}
    >
      <div className={classNames(styles.previewPage, { [styles.mobilePreview]: editMode.value === EditMode.MOBILE })}>
        <div className={styles.content}>
          {pageType == EDITOR_TYPES.LIST_EDITOR &&
            (
              editMode.value === EditMode.MOBILE ? (
                <div id="mobile-editor-preview-list" style={{ width: '100%' }}></div>
              ) :
                listComponents.value.map((cp: GridItem) => (
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
                          pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                          runtime={true}
                          preview={true}
                        />
                      </div>
                    )}
                  </Fragment>
                )))}

          {pageType == EDITOR_TYPES.FORM_EDITOR && (
            <div className={styles.fromContain}>
              <div className={styles.previewForm}>
                {editMode.value === EditMode.MOBILE ? (
                  <div id="mobile-editor-preview-list" style={{ width: '100%' }}></div>
                ) : (
                  <Form layout="inline">
                    {getFormContent()}
                  </Form>
                )}
              </div>
              <div className={styles.footer}>
                <Button type="default">取消</Button>
                <Button type="primary">提交</Button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Drawer>
  );
};

export default PartPreview;

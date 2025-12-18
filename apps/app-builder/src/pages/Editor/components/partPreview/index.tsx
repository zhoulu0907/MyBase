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
import { initGlobalState, loadMicroApp, type MicroApp } from 'qiankun';

import { EditMode, getMobileEditorURL } from '@onebase/common';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import React, { Fragment, useEffect, useRef } from 'react';
import styles from './index.module.less';

interface PartPreviewProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  pageType: string;
}

// https://github.com/umijs/qiankun/issues/2109
// 用props，会有闪屏、状态不同步等问题，等 qiankun 3.0发布后再调整方案
const warn = console.warn;
console.warn = (...args) => {
  if (args[0]?.includes("[qiankun] globalState tools will be removed in 3.0, pls don't use it!")) return;
  warn(...args);
};

/**
 * partPreview 组件
 * 用于预览页面组件的展示
 */
const PartPreview: React.FC<PartPreviewProps> = ({ visible, setVisible, pageType }) => {
  const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const { editMode } = currentEditorSignal;
  const mobileEditorPreviewRef = useRef<MicroApp | null>(null);

  //   const pageEditorSignal = usePageEditorSignal();

  const qiankunActions = initGlobalState({
    drag: false,
    components: pageType === EDITOR_TYPES.FORM_EDITOR ? formComponents.value : listComponents.value,
    pageComponentSchemas:
      pageType === EDITOR_TYPES.FORM_EDITOR ? formPageComponentSchemas.value : listPageComponentSchemas.value
  });
  useEffect(() => {
    console.log('loading mobile-editor-preview-list');
    if (editMode.value !== EditMode.MOBILE || !visible) {
      return;
    }

    const mobileEditorPreview = loadMicroApp({
      name: 'mobile-editor-preview-list',
      entry: getMobileEditorURL(),
      container: '#mobile-editor-preview-list',
      props: {
        onGlobalStateChange: qiankunActions.onGlobalStateChange,
        setGlobalState: qiankunActions.setGlobalState,
        offGlobalStateChange: qiankunActions.offGlobalStateChange
      }
    });
    mobileEditorPreviewRef.current = mobileEditorPreview;

    return () => {
      mobileEditorPreview?.unmount();
    };
  }, [editMode.value, visible]);

  const getFormContent = () => {
    return formComponents.value.map((cp: GridItem) => (
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
    ));
  };
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
            (editMode.value === EditMode.MOBILE ? (
              <div id="mobile-editor-preview-list" style={{ width: '100%' }}></div>
            ) : (
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
              ))
            ))}

          {pageType == EDITOR_TYPES.FORM_EDITOR && (
            <div className={styles.fromContain}>
              <div className={styles.previewForm}>
                {editMode.value === EditMode.MOBILE ? (
                  <div id="mobile-editor-preview-list" style={{ width: '100%' }}></div>
                ) : (
                  <Form layout="inline" labelCol={{ span: 10 }} wrapperCol={{ span: 14 }}>
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

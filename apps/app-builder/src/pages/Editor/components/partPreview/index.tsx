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
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useMemo, useRef } from 'react';
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
  useSignals(); // 确保 React 能够追踪 signal 的变化

  const { components: formComponents, pageComponentSchemas: formPageComponentSchemas } = useFormEditorSignal;
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const { editMode } = currentEditorSignal;
  const mobileEditorPreviewRef = useRef<MicroApp | null>(null);
  const qiankunActionsRef = useRef<ReturnType<typeof initGlobalState> | null>(null);

  //   const pageEditorSignal = usePageEditorSignal();

  // 使用 useMemo 安全地序列化数据，移除循环引用
  const safeData = useMemo(() => {
    try {
      const components = pageType === EDITOR_TYPES.FORM_EDITOR ? formComponents.value : listComponents.value;
      const pageComponentSchemas =
        pageType === EDITOR_TYPES.FORM_EDITOR ? formPageComponentSchemas.value : listPageComponentSchemas.value;

      // 使用 JSON 序列化来移除可能的循环引用
      // 如果序列化失败（有循环引用），会抛出错误，我们返回空数据
      const safeComponents = JSON.parse(JSON.stringify(components));
      const safePageComponentSchemas = JSON.parse(JSON.stringify(pageComponentSchemas));

      return {
        components: safeComponents,
        pageComponentSchemas: safePageComponentSchemas
      };
    } catch (error) {
      console.error('Failed to serialize data for qiankun:', error);
      // 如果序列化失败，返回空数据
      return {
        components: [],
        pageComponentSchemas: {}
      };
    }
  }, [
    pageType,
    formComponents.value,
    listComponents.value,
    formPageComponentSchemas.value,
    listPageComponentSchemas.value
  ]);

  // 初始化 qiankun globalState，只执行一次
  useEffect(() => {
    if (!qiankunActionsRef.current) {
      try {
        // 初始化时使用空数据，避免循环引用导致的问题
        qiankunActionsRef.current = initGlobalState({
          drag: false,
          components: [],
          pageComponentSchemas: {}
        });
      } catch (error) {
        console.error('Failed to initialize qiankun global state:', error);
      }
    }
  }, []);

  // 当数据变化且需要更新时，使用 setGlobalState 更新状态
  // 只在 visible 为 true 且 editMode 为 MOBILE 时才更新，因为只有这时才需要传递给子应用
  useEffect(() => {
    if (!qiankunActionsRef.current || !visible || editMode.value !== EditMode.MOBILE) {
      return;
    }

    try {
      // 使用已经序列化的安全数据
      qiankunActionsRef.current.setGlobalState({
        drag: false,
        ...safeData
      });
    } catch (error) {
      console.error('Failed to update qiankun global state:', error);
    }
  }, [safeData, visible, editMode.value]);

  useEffect(() => {
    console.log('loading mobile-editor-preview-list');
    if (editMode.value !== EditMode.MOBILE || !visible || !qiankunActionsRef.current) {
      return;
    }

    const mobileEditorPreview = loadMicroApp({
      name: 'mobile-editor-preview-list',
      entry: getMobileEditorURL(),
      container: '#mobile-editor-preview-list',
      props: {
        onGlobalStateChange: qiankunActionsRef.current.onGlobalStateChange,
        setGlobalState: qiankunActionsRef.current.setGlobalState,
        offGlobalStateChange: qiankunActionsRef.current.offGlobalStateChange
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

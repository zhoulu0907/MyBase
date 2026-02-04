import { pluginBridge } from '@/plugin/bridge';
import { Button, Drawer, Form } from '@arco-design/web-react';
import {
  EDITOR_TYPES,
  getComponentWidth,
  getWorkbenchComponentWidth,
  PreviewRender,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useFormEditorSignal,
  useListEditorSignal,
  usePageEditorSignal,
  useWorkbenchEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import classNames from 'classnames';
import { initGlobalState, loadMicroApp, type MicroApp } from 'qiankun';

import { EditMode, getHashQueryParam, getMobileEditorURL } from '@onebase/common';
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
  const { workbenchComponents, wbComponentSchemas } = useWorkbenchEditorSignal;
  const { editMode } = currentEditorSignal;
  const mobileEditorPreviewRef = useRef<MicroApp | null>(null);
  const [form] = Form.useForm();
  const [appId, setAppId] = React.useState('');
  const [pageSetId, setPageSetId] = React.useState('');

  useEffect(() => {
    const appId = getHashQueryParam('appId');
    const pageSetId = getHashQueryParam('pageSetId');
    setAppId(appId || '');
    setPageSetId(pageSetId || '');
  }, []);

  useEffect(() => {
    if (visible) {
      pluginBridge.registerContext({ form, appId, pageSetId });
      console.log('[PartPreview] Context registered', { appId, pageSetId });
    } else {
      pluginBridge.registerContext({ form: undefined });
    }
    return () => {
      if (visible) {
        pluginBridge.registerContext({ form: undefined });
      }
    };
  }, [visible, form, appId, pageSetId]);

  const {
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
    setShowDeleteButton,
    subTableComponents,
    setSubTableComponents
  } = usePageEditorSignal();

  const qiankunActions = initGlobalState({
    drag: false,
    components: pageType === EDITOR_TYPES.FORM_EDITOR ? formComponents.value : listComponents.value,
    pageComponentSchemas:
      pageType === EDITOR_TYPES.FORM_EDITOR ? formPageComponentSchemas.value : listPageComponentSchemas.value,
    curComponentID,
    setCurComponentID,
    clearCurComponentID,
    setCurComponentSchema,
    setPageComponentSchemas,
    delPageComponentSchemas,
    showDeleteButton,
    setShowDeleteButton,
    subTableComponents,
    setSubTableComponents
  });
  useEffect(() => {
    if (editMode.value !== EditMode.MOBILE || !visible) {
      return;
    }
    console.log('loading mobile-editor-preview-list');

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
    // 计算第一行索引
    let accumulatedWidth = 0;
    const firstRowIndices = new Set<number>();

    for (let i = 0; i < formComponents.value.length; i++) {
      const cp = formComponents.value[i];
      const schema = formPageComponentSchemas.value[cp.id];

      // 检查组件是否隐藏，隐藏的组件不参与第一行计算
      if (schema?.config?.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]) {
        continue;
      }

      const widthStr = schema?.config?.width;

      // 从 "25%" 中提取数字，处理可能的边界情况
      let widthValue = 0;
      if (widthStr && typeof widthStr === 'string') {
        const numStr = widthStr.replace('%', '').trim();
        widthValue = parseFloat(numStr) || 0;
      }

      // 如果累加宽度加上当前宽度不超过 100%，说明还在第一行
      if (accumulatedWidth + widthValue <= 100) {
        firstRowIndices.add(i);
        accumulatedWidth += widthValue;
      } else {
        // 超过 100%，说明已经到第二行了
        break;
      }
    }

    return formComponents.value.map((cp: GridItem, index: number) => {
      const isFirstRow = firstRowIndices.has(index);
      const tooltipPosition = isFirstRow ? 'right' : 'top';

      return (
        <Fragment key={cp.id}>
          {formPageComponentSchemas.value[cp.id]?.config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
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
                tooltipPosition={tooltipPosition}
              />
            </div>
          )}
        </Fragment>
      );
    });
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
      unmountOnExit
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
                        pageType={pageType}
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
                  <Form form={form} layout="inline" labelCol={{ span: 10 }} wrapperCol={{ span: 14 }}>
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

          {pageType == EDITOR_TYPES.WORKBENCH_EDITOR && (
            <div className={styles.fromContain}>
              <div className={styles.previewForm}>
                <Form form={form} layout="inline">
                  {workbenchComponents.value.map((cp: GridItem) => (
                    <Fragment key={cp.id}>
                      {wbComponentSchemas?.value[cp.id]?.config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                        <div
                          key={cp.id}
                          className={styles.componentItem}
                          style={{
                            width: `calc(${getWorkbenchComponentWidth(wbComponentSchemas.value[cp.id], cp.type)} - 8px)`,
                            margin: '4px'
                          }}
                        >
                          <PreviewRender
                            cpId={cp.id}
                            cpType={cp.type}
                            pageComponentSchema={wbComponentSchemas.value[cp.id]}
                            runtime={true}
                            preview={true}
                          />
                        </div>
                      )}
                    </Fragment>
                  ))}
                </Form>
              </div>
            </div>
          )}
        </div>
      </div>
    </Drawer>
  );
};

export default PartPreview;

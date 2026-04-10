import { pluginBridge } from '@/plugin/bridge';
import { Button, Drawer, Form } from '@arco-design/web-react';
import {
  EDITOR_TYPES,
  getComponentWidth,
  getOrCreatePageConfig,
  getWorkbenchComponentWidth,
  PreviewRender,
  shouldShowInWorkspace,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useFormEditorSignal,
  useListEditorSignal,
  usePageEditorSignal,
  useWorkbenchEditorSignal,
  useWorkbenchSignal,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { percentageToColSpan } from '@/pages/Editor/workbench/utils/grid-layout';

const FLOATING_COMPONENT_TYPES = ['XChatbot'];

const isFloatingComponent = (type: string): boolean => {
  return FLOATING_COMPONENT_TYPES.includes(type);
};
import classNames from 'classnames';
import { initGlobalState, loadMicroApp, type MicroApp } from 'qiankun';

import { EditMode, getHashQueryParam, getMobileEditorURL } from '@onebase/common';
import { currentEditorSignal } from '@onebase/ui-kit/src/signals/current_editor';
import React, { Fragment, useEffect, useRef } from 'react';
import styles from './index.module.less';

// 运行态布局常量（与 wb-workspace 保持一致）
const RUNTIME_SIDER_WIDTH = 200;
const RUNTIME_CONTENT_PADDING = 32;
const RUNTIME_INNER_PADDING = 32;

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

  const workbenchSignal = useWorkbenchSignal();

  const formListPreviewGlobalCommon = {
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
  };

  const previewGlobalStateByPageType: Record<string, () => Record<string, unknown>> = {
    [EDITOR_TYPES.FORM_EDITOR]: () => ({
      components: formComponents.value,
      pageComponentSchemas: formPageComponentSchemas.value,
      ...formListPreviewGlobalCommon
    }),
    [EDITOR_TYPES.LIST_EDITOR]: () => ({
      components: listComponents.value,
      pageComponentSchemas: listPageComponentSchemas.value,
      ...formListPreviewGlobalCommon
    }),
    [EDITOR_TYPES.WORKBENCH_EDITOR]: () => ({
      workbenchComponents: workbenchSignal.workbenchComponents,
      wbComponentSchemas: workbenchSignal.wbComponentSchemas,
      curComponentID: workbenchSignal.curComponentID,
      setCurComponentID: workbenchSignal.setCurComponentID,
      clearCurComponentID: workbenchSignal.clearCurComponentID,
      setCurComponentSchema: workbenchSignal.setCurComponentSchema,
      setWbComponentSchemas: workbenchSignal.setWbComponentSchemas,
      delWbComponentSchemas: workbenchSignal.delWbComponentSchemas,
      setWorkbenchComponents: workbenchSignal.setWorkbenchComponents,
      delWorkbenchComponents: workbenchSignal.delWorkbenchComponents,
      showDeleteButton: workbenchSignal.showDeleteButton,
      setShowDeleteButton: workbenchSignal.setShowDeleteButton
    })
  };

  const qiankunActions = initGlobalState({
    drag: false,
    ...(previewGlobalStateByPageType[pageType]?.() ?? previewGlobalStateByPageType[EDITOR_TYPES.LIST_EDITOR]())
  });
  useEffect(() => {
    if (editMode.value !== EditMode.MOBILE || !visible) {
      return;
    }
    console.log('loading mobile-editor-preview-list', editMode.value);

    const mobileEditorPreview = loadMicroApp({
      name: pageType == EDITOR_TYPES.WORKBENCH_EDITOR ? 'mobile-wb-editor-preview-list' : 'mobile-editor-preview-list',
      entry: getMobileEditorURL(),
      container:
        pageType == EDITOR_TYPES.WORKBENCH_EDITOR ? '#mobile-wb-editor-preview-list' : '#mobile-editor-preview-list',
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

  // 工作台预览：获取页面背景配置
  const wbPageConfig = (() => {
    if (pageType === EDITOR_TYPES.WORKBENCH_EDITOR && wbComponentSchemas.value) {
      const [, pageConfigSchema] = getOrCreatePageConfig(wbComponentSchemas.value);
      return pageConfigSchema.config;
    }
    return null;
  })();

  // 运行态内容宽度：视口宽 - 侧边栏 - padding
  const runtimeContentWidth =
    window.innerWidth -
    (wbPageConfig?.showSidebar ? RUNTIME_SIDER_WIDTH : 0) -
    RUNTIME_CONTENT_PADDING -
    RUNTIME_INNER_PADDING;

  // 预览区宽度：移动端 450px；Web 工作台 100%；Web 非工作台 60%
  let previewPageWidth = '60%';
  if (editMode.value === EditMode.MOBILE) {
    previewPageWidth = '450px';
  } else if (pageType === EDITOR_TYPES.WORKBENCH_EDITOR) {
    previewPageWidth = '100%';
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
      unmountOnExit
      bodyStyle={{ background: '#F2F3F5', padding: '0' }}
    >
      <div
        className={classNames(styles.previewPage, { [styles.mobilePreview]: editMode.value === EditMode.MOBILE })}
        style={{ width: previewPageWidth }}
      >
        <div className={styles.content}>
          {/* ── 列表页预览 ── */}
          {pageType == EDITOR_TYPES.LIST_EDITOR &&
            (editMode.value === EditMode.MOBILE ? (
              <div id="mobile-editor-preview-list" style={{ width: '100%' }}></div>
            ) : (
              <>
                {/* 浮动组件 */}
                {listComponents.value
                  .filter((cp: GridItem) => isFloatingComponent(cp.type))
                  .map((cp: GridItem) => {
                    const floatingConfig = listPageComponentSchemas.value[cp.id]?.config?.floatingConfig;
                    const right = floatingConfig?.right ?? 80;
                    const bottom = floatingConfig?.bottom ?? 80;
                    const width = floatingConfig?.width ?? 80;
                    const height = floatingConfig?.height ?? 80;

                    return (
                      <div
                        key={cp.id}
                        style={{
                          position: 'fixed',
                          right,
                          bottom,
                          width,
                          height,
                          zIndex: 100
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
                    );
                  })}

                {/* 普通组件 */}
                {listComponents.value
                  .filter((cp: GridItem) => !isFloatingComponent(cp.type))
                  .map((cp: GridItem) => (
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
                  ))}
              </>
            ))}

          {/* ── 表单页预览 ── */}
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

          {/* ── 工作台预览：以运行态真实宽度渲染，grid 布局与运行态完全一致 ── */}
          {pageType == EDITOR_TYPES.WORKBENCH_EDITOR &&
            (editMode.value === EditMode.MOBILE ? (
              <div id="mobile-wb-editor-preview-list" style={{ width: '100%' }}></div>
            ) : (
              <div className={styles.wbPreviewWrap}>
                {/* 浮动组件 */}
                {workbenchComponents.value
                  .filter((cp: GridItem) => shouldShowInWorkspace(cp.type) && isFloatingComponent(cp.type))
                  .map((cp: GridItem) => {
                    const schema = wbComponentSchemas.value[cp.id];
                    if (!schema) return null;
                    const floatingConfig = schema?.config?.floatingConfig;
                    return (
                      <div
                        key={cp.id}
                        style={{
                          position: 'fixed',
                          right: floatingConfig?.right ?? 80,
                          bottom: floatingConfig?.bottom ?? 80,
                          width: floatingConfig?.width ?? 80,
                          height: floatingConfig?.height ?? 80,
                          zIndex: 100
                        }}
                      >
                        <PreviewRender
                          cpId={cp.id}
                          cpType={cp.type}
                          pageComponentSchema={schema}
                          runtime={true}
                          preview={true}
                        />
                      </div>
                    );
                  })}

                {/* 普通组件：固定运行态宽度的 grid 画布，布局与运行态完全一致 */}
                <div
                  className={styles.wbRuntimeCanvas}
                  style={{
                    width: runtimeContentWidth,
                    backgroundColor: wbPageConfig?.pageBgColor || undefined,
                    backgroundImage: wbPageConfig?.pageBgImg ? `url(${wbPageConfig.pageBgImg})` : undefined,
                    backgroundSize: 'cover',
                    backgroundPosition: 'center',
                    backgroundRepeat: 'no-repeat'
                  }}
                >
                  {workbenchComponents.value
                    .filter((cp: GridItem) => shouldShowInWorkspace(cp.type) && !isFloatingComponent(cp.type))
                    .map((cp: GridItem) => {
                      const schema = wbComponentSchemas.value[cp.id];
                      if (!schema) return null;
                      if (schema.config?.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]) return null;
                      const widthStr = getWorkbenchComponentWidth(schema, cp.type as WorkbenchComponentType);
                      const colSpan = percentageToColSpan(widthStr);
                      const rowSpan = schema?.config?.gridLayout?.rowSpan ?? 1;
                      return (
                        <div
                          key={cp.id}
                          className={styles.wbComponentItem}
                          style={{ gridColumn: `span ${colSpan}`, gridRow: `span ${rowSpan}` }}
                        >
                          <PreviewRender
                            cpId={cp.id}
                            cpType={cp.type}
                            pageComponentSchema={schema}
                            runtime={false}
                            preview={true}
                          />
                        </div>
                      );
                    })}
                </div>
              </div>
            ))}
        </div>
      </div>
    </Drawer>
  );
};

export default PartPreview;

import { Button, Form, Input, Message, Modal } from '@arco-design/web-react';
import { IconFullscreen, IconFullscreenExit } from '@arco-design/web-react/icon';
import { PageType } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  getComponentWidth,
  normalizeFormValues,
  PreviewRender,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  usePageViewEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useCallback, useEffect, useRef, useState } from 'react';
import styles from './index.module.less';
import { initInteractionRule } from './interaction_rule';

interface EditRuntimeProps {
  form: any;
  isAdd: boolean;
  submitLoading: boolean;
  onSubmit: () => void;
  onSaveSubmit: () => void;
  onSaveDraft: () => void;
  onCancel: () => void;
  menuId: string;
  tableName: string;
}

const EditRuntime: React.FC<EditRuntimeProps> = ({
  form,
  isAdd,
  submitLoading,
  onSubmit,
  onSaveSubmit,
  onSaveDraft,
  onCancel,
  menuId: _menuId,
  tableName: _tableName
}) => {
  useSignals();

  const { pageViews, curViewId } = usePageViewEditorSignal;
  const { editPageViewId, curPage, subEntities } = pagesRuntimeSignal;

  const [cpStates, setCpStates] = useState<Record<string, any>>({});
  const isLoadingFromDraftBoxRef = useRef(false);

  const handleFormValuesChange = useCallback(
    async (_value: Partial<any>, values: Partial<any>) => {
      const states = await initInteractionRule(
        values,
        pageViews.value[curViewId.value]?.interactionRules,
        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value
      );
      setCpStates(states);
    },
    [pageViews.value, curViewId.value, editPageViewId.value]
  );

  // 载入草稿数据
  const handleLoadDraft = useCallback(
    async (draftData: any) => {
      if (draftData) {
        console.log('latestDraft: ', draftData);
        const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
        const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;
        const formValues = normalizeFormValues({
          dataItem: draftData,
          componentSchemas,
          subEntities: subEntities.value,
          subTableComponents,
          setSubTableDataLength: pagesRuntimeSignal.setSubTableDataLength
        });

        form.setFieldsValue(formValues);
        form.setFieldValue('draftId', draftData.id);

        // 触发表单值变化，更新组件状态
        await handleFormValuesChange({}, formValues);
        Message.success('已载入暂存数据');
      }
    },
    [editPageViewId.value, subEntities.value, form, handleFormValuesChange]
  );

  // 检查并载入草稿数据（仅当从草稿箱点击"继续编辑"时）
  useEffect(() => {
    if (isAdd) {
      // 如果是从草稿箱载入的，不重复处理
      if (isLoadingFromDraftBoxRef.current) {
        return;
      }

      // 只检查 localStorage 中是否有草稿数据（从草稿箱点击"继续编辑"时保存的）
      // 直接点击"添加数据"时，不自动载入服务端草稿数据
      const draftDataFromStorage = localStorage.getItem('draftData');
      if (draftDataFromStorage) {
        try {
          const draftData = JSON.parse(draftDataFromStorage);
          // 清除 localStorage 中的数据，避免重复载入
          localStorage.removeItem('draftData');
          // 标记为从草稿箱载入，避免重复提示
          isLoadingFromDraftBoxRef.current = true;
          // 载入草稿数据
          handleLoadDraft(draftData);
        } catch (error) {
          console.error('解析 localStorage 草稿数据失败:', error);
          // 解析失败时清除无效数据
          localStorage.removeItem('draftData');
        }
      }
    } else {
      // 当 isAdd 变为 false 时，重置 ref
      isLoadingFromDraftBoxRef.current = false;
    }
  }, [isAdd, handleLoadDraft]);

  const hiddenState = useCallback(
    (cpId: string) => {
      if (cpStates[cpId]?.status !== undefined) {
        return cpStates[cpId].status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN];
      } else {
        return (
          useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cpId].config.status !==
          STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
        );
      }
    },
    [cpStates, editPageViewId.value]
  );

  const [fullScreen, setFullScreen] = useState(false);

  return (
    <>
      {/* 主表单 Modal */}
      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: '40px' }}>
            <div>{isAdd ? '添加数据' : '编辑数据'}</div>
            <div className={styles.titleEditIconArea}>
              {fullScreen ? (
                <IconFullscreenExit className={styles.fullscreenIcon} onClick={() => setFullScreen(false)} />
              ) : (
                <IconFullscreen className={styles.fullscreenIcon} onClick={() => setFullScreen(true)} />
              )}
            </div>
          </div>
        }
        visible
        footer={
          <div className={styles.footer}>
            <div>
              {isAdd && (
                <Button type="default" onClick={onSaveDraft} loading={submitLoading}>
                  暂存
                </Button>
              )}
            </div>
            <div className={styles.footerRight}>
              {curPage?.value?.pageSetType === PageType.BPM && isAdd && (
                <Button type="primary" onClick={onSaveSubmit} loading={submitLoading}>
                  保存
                </Button>
              )}
              <Button type="primary" onClick={onSubmit} loading={submitLoading}>
                提交
              </Button>
              <Button
                type="default"
                onClick={() => {
                  setCpStates({});
                  onCancel();
                }}
              >
                取消
              </Button>
            </div>
          </div>
        }
        onCancel={onCancel}
        autoFocus={false}
        focusLock={false}
        style={{
          width: fullScreen ? '100vw' : '60vw',
          maxHeight: fullScreen ? '100vh' : '80vh',
          minHeight: fullScreen ? '100vh' : '20vh',
          overflow: 'auto'
        }}
        alignCenter
        wrapClassName={
          fullScreen
            ? 'runtime-preview-formpage edit-modal edit-modal-fullscreen'
            : 'runtime-preview-formpage edit-modal'
        }
      >
        <div
          className={styles.editRuntimeContent}
          style={{ maxHeight: fullScreen ? '80vh' : '55vh', minHeight: fullScreen ? '80vh' : '20vh', overflow: 'auto' }}
        >
          <Form
            layout="inline"
            labelCol={{ span: 10 }}
            wrapperCol={{ span: 14 }}
            form={form}
            onValuesChange={handleFormValuesChange}
          >
            <Form.Item field="draftId" hidden={true}>
              <Input />
            </Form.Item>

            {useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {hiddenState(cp.id) && (
                  //   {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                  //     STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: `calc(${getComponentWidth(
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id],
                        cp.type
                      )} - 8px)`,
                      margin: '4px'
                    }}
                  >
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageType={EDITOR_TYPES.FORM_EDITOR}
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                      }
                      runtime={true}
                      showFromPageData={() => {}}
                      cpState={cpStates[cp.id]}
                    />
                  </div>
                )}
              </Fragment>
            ))}
          </Form>
        </div>
      </Modal>
    </>
  );
};

export default EditRuntime;

import { Button, Form, Message, Modal } from '@arco-design/web-react';
import { IconFullscreen, IconFullscreenExit } from '@arco-design/web-react/icon';
import { PageType } from '@onebase/app';
import { getRuntimeFormCacheKey, getRuntimeFormLoadDraftKey, pagesRuntimeSignal, TokenManager } from '@onebase/common';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  usePageViewEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import dayjs from 'dayjs';
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
}

const EditRuntime: React.FC<EditRuntimeProps> = ({
  form,
  isAdd,
  submitLoading,
  onSubmit,
  onSaveSubmit,
  onSaveDraft,
  onCancel
}) => {
  useSignals();

  const { pageViews, curViewId } = usePageViewEditorSignal;
  const { editPageViewId, curPage } = pagesRuntimeSignal;

  const [cpStates, setCpStates] = useState<Record<string, any>>({});
  const [showDraftModal, setShowDraftModal] = useState(false);
  const [latestDraft, setLatestDraft] = useState<any>(null);
  const [draftTimestamp, setDraftTimestamp] = useState<number | null>(null);
  const isLoadingFromDraftBoxRef = useRef(false);

  // 获取草稿数据
  const getDrafts = useCallback(() => {
    try {
      const tokenInfo = TokenManager.getTokenInfo();
      // 如果没有必要的数据，直接跳过
      if (!tokenInfo?.userId || !curViewId.value) {
        return null;
      }

      const userId = tokenInfo.userId;
      const viewId = curViewId.value;
      const cacheKey = getRuntimeFormCacheKey(userId, viewId);
      const rawDrafts = localStorage.getItem(cacheKey);

      if (!rawDrafts) {
        return null;
      }

      try {
        const parsed = JSON.parse(rawDrafts);
        const drafts = Array.isArray(parsed) ? parsed : [parsed];
        // 过滤掉不符合格式的数据（必须有 data 和 timestamp）
        const validDrafts = drafts.filter(
          (draft: any) => draft && typeof draft === 'object' && 'data' in draft && 'timestamp' in draft
        );
        return validDrafts.length > 0 ? validDrafts : null;
      } catch {
        return null;
      }
    } catch (error) {
      console.error('获取草稿数据失败:', error);
      return null;
    }
  }, [curViewId.value]);

  const handleFormValuesChange = async (_value: Partial<any>, values: Partial<any>) => {
    const states = await initInteractionRule(
      values,
      pageViews.value[curViewId.value]?.interactionRules,
      useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value
    );
    setCpStates(states);
  };

  // 检查并提示草稿数据
  useEffect(() => {
    if (isAdd) {
      // 先检查是否有从草稿箱传递的数据
      const tokenInfo = TokenManager.getTokenInfo();
      if (tokenInfo?.userId && curViewId.value) {
        const userId = tokenInfo.userId;
        const viewId = curViewId.value;
        const loadDraftKey = getRuntimeFormLoadDraftKey(userId, viewId);
        const loadDraftData = localStorage.getItem(loadDraftKey);

        if (loadDraftData) {
          // 从草稿箱传递的数据，直接载入，不需要二次确认
          isLoadingFromDraftBoxRef.current = true;
          const loadDraft = async () => {
            try {
              const draftData = JSON.parse(loadDraftData);
              // 清除临时数据
              localStorage.removeItem(loadDraftKey);
              // 直接载入数据
              form.setFieldsValue(draftData);
              await handleFormValuesChange({}, draftData);
              Message.success('已载入暂存数据');
            } catch (error) {
              console.error('载入草稿数据失败:', error);
              Message.error('载入草稿数据失败');
            }
          };
          loadDraft();
          return;
        }
      }

      // 如果是从草稿箱载入的，不显示提示 Modal
      if (isLoadingFromDraftBoxRef.current) {
        return;
      }

      // 检查是否有自动提示的草稿数据（仅当不是从草稿箱载入时）
      const drafts = getDrafts();
      if (drafts && drafts.length > 0) {
        const latest = drafts[0]; // 最新的草稿在数组第一位
        setLatestDraft(latest.data); // 只保存表单数据
        setDraftTimestamp(latest.timestamp); // 保存时间戳用于显示
        setShowDraftModal(true);
      }
    } else {
      // 当 isAdd 变为 false 时，重置 ref
      isLoadingFromDraftBoxRef.current = false;
    }
  }, [isAdd, getDrafts]);

  // 载入草稿数据
  const handleLoadDraft = async () => {
    if (latestDraft) {
      form.setFieldsValue(latestDraft);
      // 触发表单值变化，更新组件状态
      await handleFormValuesChange({}, latestDraft);
      Message.success('已载入暂存数据');
    }
    setShowDraftModal(false);
  };

  // 取消载入草稿
  const handleCancelLoadDraft = () => {
    setShowDraftModal(false);
    setLatestDraft(null);
    setDraftTimestamp(null);
  };

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
      {/* 草稿数据载入提示 Modal */}
      <Modal
        title="暂存数据载入"
        style={{ width: '400px' }}
        visible={showDraftModal}
        onCancel={handleCancelLoadDraft}
        footer={
          <>
            <Button onClick={handleCancelLoadDraft}>取消</Button>
            <Button type="primary" onClick={handleLoadDraft}>
              确认
            </Button>
          </>
        }
      >
        <div>
          当前表单存在{draftTimestamp ? dayjs(draftTimestamp).format('YYYY-MM-DD HH:mm:ss') : ''}{' '}
          暂存的未提交数据，是否加载并继续编辑？
        </div>
      </Modal>

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
            <Button type="default" onClick={onSaveDraft} loading={submitLoading}>
              暂存
            </Button>
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
        focusLock={true}
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

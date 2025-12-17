import { Button, Form, Modal } from '@arco-design/web-react';
import { IconFullscreen, IconFullscreenExit } from '@arco-design/web-react/icon';
import { PageType } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
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
import React, { Fragment, useCallback, useState } from 'react';
import styles from './index.module.less';
import { initInteractionRule } from './interaction_rule';

interface EditRuntimeProps {
  form: any;
  isAdd: boolean;
  submitLoading: boolean;
  onSubmit: () => void;
  onSaveSubmit: () => void;
  onCancel: () => void;
}

const EditRuntime: React.FC<EditRuntimeProps> = ({ form, isAdd, submitLoading, onSubmit, onSaveSubmit, onCancel }) => {
  useSignals();

  const { pageViews, curViewId } = usePageViewEditorSignal;
  const { editPageViewId, curPage } = pagesRuntimeSignal;

  const [cpStates, setCpStates] = useState<Record<string, any>>({});

  const handleFormValuesChange = async (_value: Partial<any>, values: Partial<any>) => {
    const states = await initInteractionRule(
      values,
      pageViews.value[curViewId.value]?.interactionRules,
      useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value
    );
    setCpStates(states);
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
    <Modal
      title={
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', height: '40px' }}>
          <div>表单信息</div>
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
        fullScreen ? 'runtime-preview-formpage edit-modal edit-modal-fullscreen' : 'runtime-preview-formpage edit-modal'
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
  );
};

export default EditRuntime;

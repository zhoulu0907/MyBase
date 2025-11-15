import React, { Fragment, useState } from 'react';
import { Button, Drawer, Form } from '@arco-design/web-react';
import { IconFullscreen, IconFullscreenExit, IconEdit } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { pagesRuntimeSignal } from '@onebase/common';
import { getComponentWidth, PreviewRender, STATUS_OPTIONS, STATUS_VALUES, useEditorSignalMap, type GridItem } from '@onebase/ui-kit';
import styles from './index.module.less';

interface DetailRuntimeProps {
  visible: boolean;
  onCancel: () => void;
  form: any;
  detailMode: boolean;
  onUpdate: () => void;
  onCancelUpdate: () => void;
  showFromPageData: (id: string, toFormPage?: boolean) => void;
  editTargetId: string;
}

const DetailRuntime: React.FC<DetailRuntimeProps> = ({ visible, onCancel, form, detailMode, onUpdate, onCancelUpdate, showFromPageData, editTargetId }) => {
  useSignals();

  const { detailPageViewId } = pagesRuntimeSignal;

  const [fullScreen, setFullScreen] = useState(false);

  return (
    <Drawer
      width={fullScreen ? '98vw' : '60vw'}
      title={
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div className="predictTitle">详情</div>
          <div className={styles.titleDetailIconArea}>
            <IconEdit className={styles.fullscreenIcon} onClick={() => showFromPageData(editTargetId, true)} />
            {fullScreen ? (
              <IconFullscreenExit className={styles.fullscreenIcon} onClick={() => setFullScreen(false)} />
            ) : (
              <IconFullscreen className={styles.fullscreenIcon} onClick={() => setFullScreen(true)} />
            )}
          </div>
        </div>
      }
      visible={visible}
      placement="right"
      onCancel={onCancel}
      footer={null}
      className={fullScreen ? 'detail-drawer detail-drawer-fullscreen' : 'detail-drawer'}
    >
      <div className={styles.content}>
        <Form layout="inline" form={form} requiredSymbol={{ position: 'end' }}>
          {useEditorSignalMap.get(detailPageViewId.value)?.components.value.map((cp: GridItem) => (
            <Fragment key={cp.id}>
              {useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: `calc(${getComponentWidth(
                      useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id],
                      cp.type
                    )} - 8px)`,
                    margin: '4px'
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id]}
                    runtime={true}
                    detailMode={detailMode}
                    showFromPageData={showFromPageData}
                  />
                </div>
              )}
            </Fragment>
          ))}

          {!detailMode && (
            <div className={styles.footer}>
              <Button type="primary" onClick={onUpdate}>更新</Button>
              <Button type="default" onClick={onCancelUpdate}>取消</Button>
            </div>
          )}
        </Form>
      </div>
    </Drawer>
  );
};

export default DetailRuntime;
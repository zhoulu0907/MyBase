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
import React, { Fragment, useEffect, useState } from 'react';
import styles from './index.module.less';

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
  const preview = true;

  const [previewPageType, setPreviewPageType] = useState(pageType);

  useEffect(() => {
    setPreviewPageType(pageType);
  }, [pageType]);

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPreviewPageType(EDITOR_TYPES.LIST_EDITOR);
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
      <div className={styles.previewPage}>
        <div className={styles.content}>
          {previewPageType == EDITOR_TYPES.LIST_EDITOR &&
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
                      preview={preview}
                      showFromPageData={() => {
                        setPreviewPageType(EDITOR_TYPES.FORM_EDITOR);
                      }}
                    />
                  </div>
                )}
              </Fragment>
            ))}

          {previewPageType == EDITOR_TYPES.FORM_EDITOR && (
            <div className={styles.fromContain}>
              <div className={styles.previewForm}>
                <Form layout="inline">
                  {formComponents.value.map((cp: GridItem) => (
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
                            showFromPageData={() => {
                              setPreviewPageType(EDITOR_TYPES.FORM_EDITOR);
                            }}
                            preview={preview}
                          />
                        </div>
                      )}
                    </Fragment>
                  ))}
                </Form>
              </div>
              <div className={styles.footer}>
                <Button type="default" onClick={cancelSubmitForm}>
                  取消
                </Button>
                <Button disabled={preview} type="primary">
                  提交
                </Button>
              </div>
            </div>
          )}
        </div>
      </div>
    </Drawer>
  );
};

export default PartPreview;

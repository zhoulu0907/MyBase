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

import React, { Fragment } from 'react';
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
            listComponents.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {listPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: editMode.value === EditMode.MOBILE ? '100%' : `calc(${getComponentWidth(listPageComponentSchemas.value[cp.id], cp.type)} - 8px)`,
                      margin: '4px'
                    }}
                  >
                    {
                      editMode.value === EditMode.MOBILE ? (
                        <PreviewRender
                          cpId={cp.id}
                          cpType={cp.type}
                          pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                          runtime={true}
                          preview={true}
                        />
                      ) : (
                        <PreviewRender
                          cpId={cp.id}
                          cpType={cp.type}
                          pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                          runtime={true}
                          preview={true}
                        />
                      )
                    }
                  </div>
                )}
              </Fragment>
            ))}

          {pageType == EDITOR_TYPES.FORM_EDITOR && (
            <div className={styles.fromContain}>
              <div className={styles.previewForm}>
                {editMode.value === EditMode.MOBILE ? (
                  <Form layout="inline">
                    {getFormContent()}
                  </Form>
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

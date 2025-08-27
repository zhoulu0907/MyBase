import { Button, Drawer, Form } from '@arco-design/web-react';
import {
  EDITOR_TYPES,
  getComponentWidth,
  PreviewRender,
  useFormEditorSignal,
  useListEditorSignal,
  type GridItem
} from '@onebase/ui-kit';
import React from 'react';
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

  return (
    <Drawer
      placement="bottom"
      height={'80vh'}
      visible={visible}
      title={null}
      footer={null}
      onCancel={() => {
        setVisible(false);
      }}
    >
      <div className={styles.previewPage}>
        <div className={styles.content}>
          {pageType == EDITOR_TYPES.LIST_EDITOR &&
            listComponents.value.map((cp: GridItem) => (
              <div
                key={cp.id}
                className={styles.componentItem}
                style={{
                  width: getComponentWidth(listPageComponentSchemas.value[cp.id], cp.type)
                }}
              >
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                  runtime={false}
                />
              </div>
            ))}

          {pageType == EDITOR_TYPES.FORM_EDITOR && (
            <Form layout="inline">
              {formComponents.value.map((cp: GridItem) => (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: getComponentWidth(formPageComponentSchemas.value[cp.id], cp.type)
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={formPageComponentSchemas.value[cp.id]}
                    runtime={false}
                  />
                </div>
              ))}

              <div className={styles.footer}>
                <Button type="primary">提交</Button>
                <Button type="default">取消</Button>
              </div>
            </Form>
          )}
        </div>
      </div>
    </Drawer>
  );
};

export default PartPreview;

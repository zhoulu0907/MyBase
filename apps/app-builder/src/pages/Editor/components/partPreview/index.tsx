import { useFromEditorStore, useListEditorStore } from '@/store';
import { Button, Drawer, Form } from '@arco-design/web-react';
import React from 'react';
import { getComponentWidth } from '../../utils/app_resource';
import { EDITOR_TYPES, type GridItem } from '../../utils/const';
import PreviewRender from '../render/PreviewRender';
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
  const { pageComponentSchemas: formPageComponentSchemas, components: formComponents } = useFromEditorStore();

  const { pageComponentSchemas: listPageComponentSchemas, components: listComponents } = useListEditorStore();

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
            listComponents.map((cp: GridItem) => (
              <div
                key={cp.id}
                className={styles.componentItem}
                style={{
                  width: getComponentWidth(listPageComponentSchemas.get(cp.id), cp.type)
                }}
              >
                <PreviewRender
                  cpId={cp.id}
                  cpType={cp.type}
                  pageComponentSchema={listPageComponentSchemas.get(cp.id)}
                  runtime={false}
                />
              </div>
            ))}

          {pageType == EDITOR_TYPES.FORM_EDITOR && (
            <Form layout="inline">
              {formComponents.map((cp: GridItem) => (
                <div
                  key={cp.id}
                  className={styles.componentItem}
                  style={{
                    width: getComponentWidth(formPageComponentSchemas.get(cp.id), cp.type)
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageComponentSchema={formPageComponentSchemas.get(cp.id)}
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

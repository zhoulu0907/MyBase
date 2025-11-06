import { Button, Drawer } from '@arco-design/web-react';
import { IconArrowLeft, IconEdit } from '@arco-design/web-react/icon';
import { EditorRenderer, FreeLayoutEditorProvider } from '@flowgram.ai/free-layout-editor';
import { etlEditorSignal, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import ETLFlowPanel from './components/panel';
import { useEditorProps } from './hooks/use-editor-props';
import styles from './index.module.less';
import { FlowNodeRegistries } from './nodes';

const ETLFlowEditorPage: React.FC = () => {
  useSignals();

  const navigate = useNavigate();
  const refWrapper = useRef<HTMLDivElement>(null);

  const editorProps = useEditorProps(FlowNodeRegistries);

  const backToDataFactory = () => {
    const appId = getHashQueryParam('appId');

    navigate(`/onebase/create-app/data-factory?appId=${appId}`);
  };

  return (
    <div className={styles.etlFlowEditorPage}>
      <div className={styles.etlFlowEditorHeader}>
        <div className={styles.etlFlowEditorHeaderLeft}>
          <IconArrowLeft onClick={backToDataFactory} />

          <div className={styles.flowName}>数据流名称</div>
          <IconEdit />
        </div>
        <div className={styles.etlFlowEditorHeaderRight}>
          <Button type="primary">保存</Button>
        </div>
      </div>
      <div className={styles.etlFlowEditorContent}>
        <FreeLayoutEditorProvider {...editorProps}>
          <div className={styles.sidebar}>
            <ETLFlowPanel />
          </div>
          <div className={styles.main} ref={refWrapper}>
            <EditorRenderer />
            <Drawer
              height={600}
              title={<span>Basic Information </span>}
              getPopupContainer={() => refWrapper && refWrapper?.current!}
              visible={etlEditorSignal.curNode.value.id}
              footer={null}
              placement={'bottom'}
              style={{ zIndex: 100 }}
              onOk={() => {
                etlEditorSignal.clearCurNode();
              }}
              onCancel={() => {
                etlEditorSignal.clearCurNode();
              }}
            >
              <div>Here is an example text. </div>
              <div>Here is an example text.</div>
            </Drawer>
          </div>
        </FreeLayoutEditorProvider>
      </div>
    </div>
  );
};

export { ETLFlowEditorPage };

import { loadMicroApp, type MicroApp } from 'qiankun';
import React, { useEffect, useRef } from 'react';
import EditorConfig from '../../components/config';
// import EditorPanel from '../../components/panel/Panel';
import globalState from '@/store/microService/state';
import { EditMode } from '@onebase/common';
import { currentEditorSignal, EditorPanel } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import EditorWorkspace from '../../components/workspace/Workspace';
import styles from './index.module.less';

const FormEditor: React.FC = () => {
  useSignals();

  const { editMode } = currentEditorSignal;

  const microAppRef = useRef<MicroApp | null>(null);

  // 监听全局状态变化
  useEffect(() => {
    globalState.onGlobalStateChange(
      (state: any, prev: any) => {
        console.log('主应用接收到全局状态变化', state, prev);
      },
      true // 立即执行一次
    );

    // 返回清理函数
    return () => {
      globalState.offGlobalStateChange();
    };
  }, []);

  useEffect(() => {
    if (editMode.value === EditMode.MOBILE) {
      const microApp = loadMicroApp({
        name: 'mobile-editor',
        entry: '//localhost:4400',
        container: '#mobile-editor',
        props: {
          onGlobalStateChange: globalState.onGlobalStateChange,
          setGlobalState: globalState.setGlobalState,
          offGlobalStateChange: globalState.offGlobalStateChange
        }
      });
      microAppRef.current = microApp;

      return () => {
        microApp?.unmount();
      };
    }
  }, [editMode.value]);

  return (
    <>
      <div className={styles.formEditorPage}>
        <div
          className={styles.leftContainer}
          style={{
            display: editMode.value == EditMode.PC ? 'flex' : 'none'
          }}
        >
          <EditorPanel />
          <EditorWorkspace />
        </div>

        <div
          className={styles.leftContainer}
          style={{
            display: editMode.value == EditMode.MOBILE ? 'flex' : 'none'
          }}
        >
          <div id="mobile-editor"></div>
        </div>

        <div className={styles.rightContainer}>
          <EditorConfig />
        </div>
      </div>
    </>
  );
};

export { FormEditor };

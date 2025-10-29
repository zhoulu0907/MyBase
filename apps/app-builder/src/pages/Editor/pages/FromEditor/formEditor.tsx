import { loadMicroApp, type MicroApp } from 'qiankun';
import React, { useEffect, useRef } from 'react';
import EditorConfig from '../../components/config';
import EditorPanel from '../../components/panel/Panel';
import EditorWorkspace from '../../components/workspace/Workspace';

import globalState from '@/store/microService/state';
import { EditMode } from '@onebase/common';
import { currentEditorSignal } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
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
        name: 'mobile-editor-container',
        entry: '//localhost:4400',
        container: '#mobile-editor-container',
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
    <div className={styles.formEditorPage}>
      {editMode.value === EditMode.PC && (
        <>
          <EditorPanel />
          <EditorWorkspace />
        </>
      )}
      {editMode.value === EditMode.MOBILE && (
        <>
          <div id="mobile-editor-container"></div>
        </>
      )}
      {/* <EditorPanel />
      <EditorWorkspace /> */}
      <EditorConfig />
    </div>
  );
};

export { FormEditor };

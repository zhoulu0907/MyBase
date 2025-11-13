/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useState, useEffect } from 'react';
import { useRefresh } from '@flowgram.ai/free-layout-editor';
import { useClientContext } from '@flowgram.ai/free-layout-editor';
import { IconUndo, IconRedo } from '@douyinfe/semi-icons';
import { ZoomSelect } from './zoom-select';
import { ToolContainer, ToolSection } from './styles';
import { AutoLayout } from './auto-layout';
import { GlobalConfig } from './globalConfig';
import styles from './index.module.less';

export const DemoTools = ({ onSave }: { onSave: () => void }) => {
  const { history, playground } = useClientContext();
  const [canUndo, setCanUndo] = useState(false);
  const [canRedo, setCanRedo] = useState(false);

  useEffect(() => {
    const disposable = history.undoRedoService.onChange(() => {
      setCanUndo(history.canUndo());
      setCanRedo(history.canRedo());
    });
    return () => disposable.dispose();
  }, [history]);
  const refresh = useRefresh();

  useEffect(() => {
    const disposable = playground.config.onReadonlyOrDisabledChange(() => refresh());
    return () => disposable.dispose();
  }, [playground]);

  return (
    <ToolContainer className={styles.demoFreeLayoutTools}>
      <ToolSection>
        <ZoomSelect />
        <GlobalConfig />
        <AutoLayout />
        <div className={styles.toolsItem}>
          <div className={styles.toolButton} onClick={() => history.undo()}>
            <IconUndo />
          </div>
        </div>
        <div className={styles.toolsItem}>
          <div className={styles.toolButton} onClick={() => history.redo()}>
            <IconRedo />
          </div>
        </div>
      </ToolSection>
    </ToolContainer>
  );
};

/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useCallback } from 'react';
import { usePlaygroundTools } from '@flowgram.ai/free-layout-editor';
import { IconAutoLayout } from '../../assets/icon-auto-layout';
import styles from './index.module.less';

export const AutoLayout = () => {
  const tools = usePlaygroundTools();
  const autoLayout = useCallback(async () => {
    await tools.autoLayout();
  }, [tools]);

  return (
    <div className={styles.toolsItem}>
      <div className={styles.autoLayout} onClick={autoLayout}>
        <div className={styles.iconAutoLayout}>
          <IconAutoLayout />
        </div>
        自动布局
      </div>
    </div>
  );
};

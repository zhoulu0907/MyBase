/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useState } from 'react';
import { GlobalConfig as GlobalConfigContent } from '../globalConfig';
import { IconSettings } from '@arco-design/web-react/icon';
import styles from './index.module.less';

export const GlobalConfig = () => {
  const [globalVisible, setGlobalVisible] = useState(false);
  return (
    <div className={styles.toolsItem}>
      <div onClick={() => setGlobalVisible(true)} className={styles.globalConfig}>
        <IconSettings className={styles.iconSettings} />
        全局配置
      </div>
      <GlobalConfigContent visible={globalVisible} onClose={() => setGlobalVisible(false)} />
    </div>
  );
};

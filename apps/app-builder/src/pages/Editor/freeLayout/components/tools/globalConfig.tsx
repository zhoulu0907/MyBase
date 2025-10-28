/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { Button } from '@douyinfe/semi-ui';
import { GlobalConfig as GlobalConfigContent } from '../globalConfig';
import { useState } from 'react';

export const GlobalConfig = () => {
  const [globalVisible, setGlobalVisible] = useState(false);
  return (
    <div>
      <Button onClick={() => setGlobalVisible(true)}>全局配置</Button>
      <GlobalConfigContent visible={globalVisible} onClose={() => setGlobalVisible(false)} />
    </div>
  );
};

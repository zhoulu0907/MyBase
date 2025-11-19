/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useState } from 'react';

import { usePlayground, usePlaygroundTools } from '@flowgram.ai/free-layout-editor';
import { Dropdown } from '@douyinfe/semi-ui';
import { IconZoomIn, IconZoomOut } from '@arco-design/web-react/icon';
import { SelectZoom } from './styles';
import styles from './index.module.less';

export const ZoomSelect = ({ minZoom = 0.5 }: { minZoom?: number }) => {
  const tools = usePlaygroundTools({ maxZoom: 2, minZoom });
  const playground = usePlayground();
  const [dropDownVisible, openDropDown] = useState(false);
  return (
    <div className={styles.toolsItem}>
      <IconZoomIn className={styles.zoomIn} onClick={() => tools.zoomin()} />
      <div className={styles.zoomSelect}>
        <Dropdown
          position="bottom"
          trigger="custom"
          visible={dropDownVisible}
          onClickOutSide={() => openDropDown(false)}
          render={
            <Dropdown.Menu>
              <Dropdown.Item onClick={() => playground.config.updateZoom(0.5)}>50%</Dropdown.Item>
              <Dropdown.Item onClick={() => playground.config.updateZoom(1)}>100%</Dropdown.Item>
              <Dropdown.Item onClick={() => playground.config.updateZoom(1.5)}>150%</Dropdown.Item>
              <Dropdown.Item onClick={() => playground.config.updateZoom(2.0)}>200%</Dropdown.Item>
            </Dropdown.Menu>
          }
        >
          <SelectZoom onClick={() => openDropDown(true)}>{Math.floor(tools.zoom * 100)}%</SelectZoom>
        </Dropdown>
      </div>

      <IconZoomOut className={styles.zoomOut} onClick={() => tools.zoomout()} />
      <div className={styles.reset} onClick={() => playground.config.updateZoom(1)}>
        重置
      </div>
    </div>
  );
};

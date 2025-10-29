/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useContext } from 'react';
import { Divider, Button } from '@arco-design/web-react';
import styles from './index.module.less';
import { SidebarContext, NodeRenderContext } from '../../context';

export default function BottomBtn({handleSubmit}: any) {
  const { setNodeId } = useContext(SidebarContext);
  // const { configForm, setconfigFormForm } = useContext(NodeRenderContext); // 在这里设置和获取form
  const handleClose = () => {
    setNodeId(undefined);
  };
  return (
    <div className={styles.bottomBtn}>
      <Divider />
      <div className={styles.btnBox}>
        <Button className={styles.submit} type="primary" onClick={handleSubmit}>
          确定
        </Button>
        <Button type="secondary" onClick={handleClose}>
          取消
        </Button>
      </div>
    </div>
  );
}

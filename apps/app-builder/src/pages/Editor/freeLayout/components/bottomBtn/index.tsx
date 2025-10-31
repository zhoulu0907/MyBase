/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useContext } from 'react';
import { Divider, Button } from '@arco-design/web-react';
import styles from './index.module.less';
import { SidebarContext } from '../../context';

export default function BottomBtn({ handleSubmit }: any) {
  const { setNodeId } = useContext(SidebarContext);
  const handleClose = () => {
    setNodeId(undefined);
  };
  const submit = () => {
    handleSubmit();
    handleClose();
  };
  return (
    <div className={styles.bottomBtn}>
      <Divider />
      <div className={styles.btnBox}>
        <Button className={styles.submit} type="primary" onClick={submit}>
          确定
        </Button>
        <Button type="secondary" onClick={handleClose}>
          取消
        </Button>
      </div>
    </div>
  );
}

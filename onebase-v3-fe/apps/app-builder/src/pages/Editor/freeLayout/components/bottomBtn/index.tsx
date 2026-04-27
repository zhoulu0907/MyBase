/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */
import { useContext } from 'react';
import { Divider, Button } from '@arco-design/web-react';
import styles from './index.module.less';
import { SidebarContext } from '../../context';

export default function BottomBtn({ handleSubmit, submitOnly }: any) {
  const { setNodeId, setLineData } = useContext(SidebarContext);
  const handleClose = () => {
    setNodeId(undefined);
    setLineData(undefined);
  };
  const submit = () => {
    handleSubmit();
    !submitOnly && handleClose();
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

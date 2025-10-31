/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useContext } from 'react';

import { getIcon } from './utils';
import { useNodeRenderContext } from '../../hooks';
import close from '../../assets/close.svg';
import edit from '../../assets/edit.svg';
import styles from './index.module.less';
import { SidebarContext } from '../../context';

export default function Header() {
  const { node, data } = useNodeRenderContext();
  const { setNodeId } = useContext(SidebarContext);
  const handleClose = () => {
    setNodeId(undefined);
  };
  return (
    <div className={styles.sidebarHeader}>
      <div className={styles.leftTitle}>
        <div className={styles.icon}> {getIcon(node)}</div>
        <div className={styles.title}>{data.name}</div>
        <img className={styles.edit} src={edit} />
      </div>
      <img className={styles.close} src={close} alt="" onClick={handleClose} />
    </div>
  );
}

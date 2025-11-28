/**
 * Copyright (c) 2025 Bytedance Ltd. and/or its affiliates
 * SPDX-License-Identifier: MIT
 */

import { useContext, useState } from 'react';
import { Input } from '@arco-design/web-react';
import { getIcon } from './utils';
import { useNodeRenderContext } from '../../hooks';
import close from '../../assets/close.svg';
import edit from '../../assets/edit.svg';
import styles from './index.module.less';
import { SidebarContext } from '../../context';
export default function Header({ changeName }: { changeName?: (name: string) => void }) {
  const { node, data } = useNodeRenderContext();
  const { setNodeId } = useContext(SidebarContext);
  const [isEditing, setIsEditing] = useState(false);
  const [editValue, setEditValue] = useState(data.name);
  const handleClose = () => {
    setNodeId(undefined);
  };
  const handleEdit = () => {
    setIsEditing(true);
    setEditValue(data.name);
  };

  const handleSave = () => {
    setIsEditing(false);
    changeName && changeName(editValue);
  };

  const handleCancel = () => {
    setEditValue(data.name);
    setIsEditing(false);
  };
  return (
    <div className={styles.sidebarHeader}>
      <div className={styles.leftTitle}>
        <div className={styles.icon}> {getIcon(node)}</div>
        {isEditing ? (
          <Input
            value={editValue}
            onChange={setEditValue}
            onBlur={handleSave}
            onPressEnter={handleSave}
            onKeyDown={(e) => {
              if (e.key === 'Escape') {
                handleCancel();
              }
            }}
            size="small"
            style={{ margin: '0 8px' }}
            className={styles.titleInput}
            autoFocus
          />
        ) : (
          <div className={styles.title}>{editValue}</div>
        )}
        {!isEditing && <img className={styles.edit} src={edit} onClick={handleEdit} />}
      </div>
      <img className={styles.close} src={close} alt="" onClick={handleClose} />
    </div>
  );
}

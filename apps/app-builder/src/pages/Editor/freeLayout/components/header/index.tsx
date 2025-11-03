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
import { type NodeRenderReturnType } from '@flowgram.ai/free-layout-editor';
interface HeaderProps {
  nodeRender: NodeRenderReturnType;
}
export default function Header() {
  // export default function Header({ nodeRender }: HeaderProps) {
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
    // // setData({ ...data, name: editValue });
    // nodeRender?.form?.setValueIn('name', editValue);
    // setIsEditing(false);
    // console.log(nodeRender);
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
            className={styles.titleInput}
            autoFocus
          />
        ) : (
          <div className={styles.title}>{data.name}</div>
        )}
        {!isEditing && <img className={styles.edit} src={edit} onClick={handleEdit} />}
      </div>
      <img className={styles.close} src={close} alt="" onClick={handleClose} />
    </div>
  );
}

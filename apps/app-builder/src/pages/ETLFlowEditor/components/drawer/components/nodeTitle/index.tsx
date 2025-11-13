import { Input } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import { etlEditorSignal } from '@onebase/common';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface NodeTitleProps {
  title: string;
}

const NodeTitle: React.FC<NodeTitleProps> = ({ title }) => {
  const { curNode, nodeData, setNodeData } = etlEditorSignal;
  const [isEditing, setIsEditing] = useState(false);
  const [titleValue, setTitleValue] = useState(title);

  useEffect(() => {
    setTitleValue(title);
  }, [title]);

  const handleEdit = () => {
    if (isEditing) {
      setIsEditing(false);
    } else {
      setIsEditing(true);
    }
  };

  const handleBlur = () => {
    setIsEditing(false);
    console.log('titleValue: ', titleValue);
    console.log('curNode.value.id: ', curNode.value.id);

    setNodeData(curNode.value.id, {
      ...nodeData.value[curNode.value.id],
      title: titleValue
    });
  };
  const handleChange = (value: string) => {
    setTitleValue(value);
  };

  return (
    <div className={styles.nodeTitle}>
      {isEditing ? <Input value={titleValue} onChange={handleChange} onBlur={handleBlur} /> : <span>{titleValue}</span>}
      <IconEdit onClick={handleEdit} />
    </div>
  );
};

export default NodeTitle;

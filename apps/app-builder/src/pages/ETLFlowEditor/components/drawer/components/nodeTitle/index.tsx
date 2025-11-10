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
    console.log('edit');
    if (isEditing) {
      setIsEditing(false);
      setNodeData(curNode.value.id, {
        ...nodeData.value[curNode.value.id],
        title: titleValue
      });
    } else {
      setIsEditing(true);
    }
  };

  const handleChange = (value: string) => {
    console.log('change', value);
    setTitleValue(value);
  };

  return (
    <div className={styles.nodeTitle}>
      {isEditing ? <Input value={titleValue} onChange={handleChange} /> : <span>{titleValue}</span>}
      <IconEdit onClick={handleEdit} />
    </div>
  );
};

export default NodeTitle;

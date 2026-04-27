import { Input } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import React, { useState, type Dispatch } from 'react';
import styles from './index.module.less';

interface NodeTitleProps {
  title: string;
  onChange: Dispatch<any>;
}

const NodeTitle: React.FC<NodeTitleProps> = ({ title, onChange }) => {
  const [isEditing, setIsEditing] = useState(false);

  const handleEdit = () => {
    if (isEditing) {
      setIsEditing(false);
    } else {
      setIsEditing(true);
    }
  };

  const handleBlur = () => {
    setIsEditing(false);
  };
  const handleChange = (value: string) => {
    onChange(value);
  };

  return (
    <div className={styles.nodeTitle}>
      {isEditing ? <Input value={title} onChange={handleChange} onBlur={handleBlur} /> : <span>{title}</span>}
      <IconEdit onClick={handleEdit} />
    </div>
  );
};

export default NodeTitle;

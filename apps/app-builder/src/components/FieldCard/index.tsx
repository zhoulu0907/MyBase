import { Button, Typography } from '@arco-design/web-react';
import React from 'react';
import styles from './index.module.less';

interface FieldCardProps {
  displayName: string;
  type: string;
  id: string;

  tableName: string;
  fieldName: string;
  label: string;
}

const FieldCard: React.FC<FieldCardProps> = ({ displayName, type, id, tableName, fieldName, label }) => {
  return (
    <div
      className={styles.fieldCard}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
      data-table-name={tableName}
      data-field-name={fieldName}
      data-label={label}
    >
      <Button key={id} id={id} type="outline" className={styles.fieldItem}>
        <Typography.Ellipsis className={styles.ellipsis} showTooltip>
          {label}
        </Typography.Ellipsis>
      </Button>
    </div>
  );
};

export default FieldCard;

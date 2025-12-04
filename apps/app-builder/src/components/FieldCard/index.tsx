import { Button, Typography } from '@arco-design/web-react';
import React from 'react';
import styles from './index.module.less';

interface FieldCardProps {
  displayName: string;
  type: string;
  id: string;
  //TODO(mickey): 使用entityName和fieldName，后续移除entityID、fieldID
  //   fieldID: string;
  //   entityID: string;

  entityName: string;
  fieldName: string;
  label: string;
}

const FieldCard: React.FC<FieldCardProps> = ({
  displayName,
  type,
  id,
  //   fieldID,
  //   entityID,
  entityName,
  fieldName,
  label
}) => {
  return (
    <div
      className={styles.fieldCard}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
      //   data-field-id={fieldID}
      //   data-entity-id={entityID}
      data-entity-name={entityName}
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

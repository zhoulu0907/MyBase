import { Button } from '@arco-design/web-react';
import { IconAlignRight } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

interface FieldCardProps {
  displayName: string;
  type: string;
  id: string;
  fieldID: string;
  entityID: string;
  label: string;
}

const FieldCard: React.FC<FieldCardProps> = ({ displayName, type, id, fieldID, entityID, label }) => {
  return (
    <div
      className={styles.fieldCard}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
      data-field-id={fieldID}
      data-entity-id={entityID}
      data-label={label}
    >
      <Button key={id} id={id} type="outline" icon={<IconAlignRight />} className={styles.fieldItem}>
        {label}
      </Button>
    </div>
  );
};

export default FieldCard;

import { Button, Typography } from '@arco-design/web-react';
import React from 'react';
import './index.css';

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
      className="fieldCard"
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
      data-field-id={fieldID}
      data-entity-id={entityID}
      data-label={label}
    >
      <Button key={id} id={id} type="outline" className="fieldItem">
        <Typography.Ellipsis className="ellipsis" showTooltip>
          {label}
        </Typography.Ellipsis>
      </Button>
    </div>
  );
};

export default FieldCard;

import { Button } from "@arco-design/web-react";
import { IconAlignRight } from "@arco-design/web-react/icon";
import React from "react";
import styles from "./index.module.less";

interface FieldCardProps {
  displayName: string;
  type: string;
  id: string;
}

const FieldCard: React.FC<FieldCardProps> = ({ displayName, type, id }) => {
  return (
    <div
      className={styles.fieldCard}
      data-cp-type={type}
      data-cp-displayname={displayName}
      data-cp-id={id}
    >
      <Button
        key={id}
        id={id || `${type}-${Date.now()}`}
        type="outline"
        icon={<IconAlignRight />}
        className={styles.fieldItem}
      >
        {displayName}
      </Button>
    </div>
  );
};

export default FieldCard;

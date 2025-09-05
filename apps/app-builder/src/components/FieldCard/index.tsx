import { Button, Tooltip } from '@arco-design/web-react';
import { IconAlignRight } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
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
  const [isExceed,serIsExceed] = useState<boolean>(false)

  useEffect(()=>{
    judgeExceed();
  },[label])
  const judgeExceed = ()=>{
    const element = document.querySelector(`#${id} span`);
    if(element){
      const textWidth = element.scrollWidth; // 获取元素内容的实际宽度（包括溢出部分）
      const containerWidth = element.clientWidth; // 获取元素的宽度（不包括溢出部分）
      serIsExceed(textWidth > containerWidth)
    }
  }

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
      <Tooltip content={label} disabled={!isExceed}>
        <Button key={id} id={id} type="outline" icon={<IconAlignRight />} className={styles.fieldItem}>
          {label}
        </Button>
      </Tooltip>
      
    </div>
  );
};

export default FieldCard;

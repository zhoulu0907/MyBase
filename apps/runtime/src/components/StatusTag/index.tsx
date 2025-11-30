import React from 'react';
import s from './index.module.less';
import { Tag } from '@arco-design/web-react';
import { statusColorMap, StatusEnum, StatusLabelEnum } from '@/constants';

export interface StatusTagProps {
  status: StatusEnum;
  type?: 'tag' | 'text';
}



export const getStatusLabel = (status: StatusEnum): string => {
  const isEnable = status === StatusEnum.ENABLE;
  const text = isEnable ? StatusLabelEnum.ENABLE : (status === StatusEnum.EXPIRED ? StatusLabelEnum.EXPIRED : StatusLabelEnum.DISABLE);
  return text;
};

export const StatusTag: React.FC<StatusTagProps> = ({ status, type = 'text' }) => {
  const isEnable = status === StatusEnum.ENABLE;
  const text = getStatusLabel(status);
  const dotClass = isEnable ? s.enable : (status === StatusEnum.EXPIRED ? s.expired : s.disable);

  return type === 'tag' ? (
    <Tag color={statusColorMap[status]}>{text}</Tag>
  ) : (
    <div className={s.statusTag}>
      <div className={`${s.dot} ${dotClass}`}></div>
      <span>{text}</span>
    </div>
  );
};

export default StatusTag;

import React from 'react';
import { StatusEnum } from '@onebase/platform-center';
import s from './index.module.less';

export interface StatusTagProps {
  status: StatusEnum;
}

export enum StatusLabelEnum {
  ENABLE = '启用',
  DISABLE = '禁用'
}

export const getStatusLabel = (status: StatusEnum): string => {
  const isEnable = status === StatusEnum.ENABLE;
  const text = isEnable ? StatusLabelEnum.ENABLE : StatusLabelEnum.DISABLE;
  return text;
};

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const isEnable = status === StatusEnum.ENABLE;
  const text = getStatusLabel(status);
  const dotClass = isEnable ? s.enable : s.disable;

  return (
    <div className={s.statusTag}>
      <div className={`${s.dot} ${dotClass}`}></div>
      <span>{text}</span>
    </div>
  );
};

export default StatusTag;

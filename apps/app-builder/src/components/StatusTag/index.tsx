import React from 'react';
import { StatusEnum } from '@onebase/platform-center/src/types/common';
import s from './index.module.less';

export interface StatusTagProps {
  status: StatusEnum;
}

export const StatusTag: React.FC<StatusTagProps> = ({ status }) => {
  const isEnable = status === StatusEnum.ENABLE;
  const text = isEnable ? '启用' : '未启用';
  const dotClass = isEnable ? s.enable : s.disable;

  return (
    <div className={s.statusTag}>
      <div className={`${s.dot} ${dotClass}`}></div>
      <span>{text}</span>
    </div>
  );
};

export default StatusTag;
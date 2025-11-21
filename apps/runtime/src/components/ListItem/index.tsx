import { Typography } from '@arco-design/web-react';
import React from 'react';
import s from './index.module.less';

export interface ListItemProps {
  title: string;
  active?: boolean;
  onClick?: () => void;
  children?: React.ReactNode;
}

const ListItem: React.FC<ListItemProps> = ({ title, active = false, onClick, children }) => {
  return (
    <div
      className={`${s.listItem} ${active ? s.active : ''}`}
      onClick={() => {
        onClick && onClick();
      }}
    >
      <div className={s.listItemTitle}>
        <Typography.Ellipsis showTooltip>{title}</Typography.Ellipsis>
      </div>
      {children}
    </div>
  );
};

export default ListItem;

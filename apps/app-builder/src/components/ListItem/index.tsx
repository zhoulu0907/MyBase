import { Typography } from '@arco-design/web-react';
import React from 'react';
import './index.less';

export interface ListItemProps {
  title: string;
  active?: boolean;
  onClick?: () => void;
  children?: React.ReactNode;
}

const ListItem: React.FC<ListItemProps> = ({
  title,
  active = false,
  onClick,
  children
}) => {
  return (
    <div
      className={`list-item-with-dropdown${active ? ' active' : ''}`}
      onClick={onClick}
    >
      <div className="list-item-with-dropdown__title">
        <Typography.Ellipsis showTooltip>{title}</Typography.Ellipsis>
      </div>
      {children}
    </div>
  );
};

export default ListItem;
import { Dropdown, Typography } from '@arco-design/web-react';
import { IconMore } from '@arco-design/web-react/icon';
import React from 'react';

import './index.less';

export interface ListItemWithDropdownProps {
  title: string;
  droplist?: React.ReactNode;
  active?: boolean;
  onClick?: () => void;
}

const ListItemWithDropdown: React.FC<ListItemWithDropdownProps> = ({ title, droplist, active = false, onClick }) => {
  return (
    <div
      className={`list-item-with-dropdown${active ? ' active' : ''}`}
      onClick={() => {
        onClick && onClick();
      }}
    >
      <div className="list-item-with-dropdown__title">
        <Typography.Ellipsis showTooltip>{title}</Typography.Ellipsis>
      </div>
      <div className="list-item-with-dropdown__actions">
        <Dropdown position="bl" trigger="click" droplist={droplist} triggerProps={{ style: { minWidth: '100px' } }}>
          <IconMore />
        </Dropdown>
      </div>
    </div>
  );
};

export default ListItemWithDropdown;

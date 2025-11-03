import React from 'react';
import { Tag, Typography } from '@arco-design/web-react';
import { RoleType } from '@onebase/platform-center';
import './index.less';

export interface InfoPanelProps {
  title: React.ReactNode;
  type: React.ReactNode;
  description?: React.ReactNode;
  leftChildren?: React.ReactNode; // panel左侧自定义内容
  rightChildren?: React.ReactNode; // panel右侧自定义内容
  titleChildren?: React.ReactNode; // 标题右侧的自定义内容
  wrapperClassName?: string;
  canDescCopy?: boolean; //
}

const InfoPanel: React.FC<InfoPanelProps> = ({
  title,
  type,
  description,
  titleChildren,
  leftChildren,
  rightChildren,
  wrapperClassName
}) => {
  return (
    <div className={`info-panel ${wrapperClassName}`}>
      {leftChildren}
      <div className="info-panel__main">
        <div className="info-panel__title-wrapper">
          <div className="info-panel__title">{title}</div>
          {titleChildren && <div className="info-panel__title-children">{titleChildren}</div>}
          {type === RoleType.SYSTEM && (
            <Tag color="cyan">
              系统
            </Tag>
          )}
        </div>
        {description && <Typography.Paragraph className="info-panel__description">{description}</Typography.Paragraph>}
      </div>
      {rightChildren && <div className="info-panel__right">{rightChildren}</div>}
    </div>
  );
};

export default InfoPanel;

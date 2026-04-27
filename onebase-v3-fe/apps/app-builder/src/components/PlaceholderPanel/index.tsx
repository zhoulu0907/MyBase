import React from 'react';
import { Spin } from '@arco-design/web-react';
import EmptyState from '@/components/EmptyState';

/**
 * 用于查询的占位
 * 三种状态： 无权限、加载中、无数据
 */

interface PlaceholderPanelProps {
  hasPermission?: boolean;
  isLoading?: boolean;
  isEmpty?: boolean;
  children?: React.ReactNode;
  className?: string;
  style?: React.CSSProperties;
  spinStyle?: React.CSSProperties;
}
const PlaceholderPanel: React.FC<PlaceholderPanelProps> = ({
  isLoading,
  isEmpty,
  hasPermission,
  children,
  className,
  style,
  spinStyle
}) => {
  return (
    <div className={`${className ? className : ''} placeholder-panel`} style={style}>
      {!hasPermission ? (
        <EmptyState type="table" description="无权限" />
      ) : isLoading ? (
        <Spin size={40} style={spinStyle}>{children}</Spin>
      ) : isEmpty ? (
        <EmptyState type="table" description="暂无数据" />
      ) : children}
    </div>
  );
};

export default PlaceholderPanel;

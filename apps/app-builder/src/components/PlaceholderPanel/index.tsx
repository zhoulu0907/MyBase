import React from 'react';
import { Spin, Empty } from '@arco-design/web-react';

/**
 * 用于查询的占位
 * 三种状态： 无权限、加载中、无数据
 */

interface PlaceholderPanelProps {
  hasPermission?: boolean;
  isLoading?: boolean;
  isEmpty?: boolean;
  children?: React.ReactNode;
}
const PlaceholderPanel: React.FC<PlaceholderPanelProps> = ({
  isLoading,
  isEmpty,
  hasPermission,
  children
}) => {
  return (
    <div className="placeholder-panel">
      {!hasPermission ? (
        <Empty description="无权限" />
      ) : isLoading ? (
        <Spin size={40}>{children}</Spin>
      ) : isEmpty ? (
        <Empty description="暂无数据" />
      ) :  children}
    </div>
  );
};

export default PlaceholderPanel;

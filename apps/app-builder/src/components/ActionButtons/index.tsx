import { Space, Divider } from '@arco-design/web-react';
import type { SpaceProps } from '@arco-design/web-react';
import React from 'react';
import styles from './index.module.less';

/**
 * 表格操作列按钮容器组件
 * 自动在按钮之间添加分割线
 *
 * 设计规范：
 * - 分割线颜色：#EAEEEF (Grey1)
 * - 分割线尺寸：宽度 1px，高度 10px
 * - 按钮与分割线间距：8px
 */
const ActionButtons: React.FC<SpaceProps> = ({ children, style, className, ...props }) => {
  return (
    <Space
      size={0}
      className={`${styles.actionButtons} ${className || ''}`}
      style={{ ...style }}
      split={
        <Divider
          type="vertical"
          style={{
            width: 1,
            height: 10,
            backgroundColor: '#EAEEEF',
            margin: '0 8px'
          }}
        />
      }
      {...props}
    >
      {children}
    </Space>
  );
};

export default ActionButtons;
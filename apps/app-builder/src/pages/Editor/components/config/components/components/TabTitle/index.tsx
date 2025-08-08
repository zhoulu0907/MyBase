import React from 'react';
import styles from './index.module.less';

interface TabTitleProps {
  title: React.ReactNode;
}

/**
 * TabTitle 组件用于在 Tabs 中自定义标签标题内容
 * 支持可选图标和自定义样式
 */
const TabTitle: React.FC<TabTitleProps> = ({ title }) => {
  return <div className={styles.tabTitle}>{title}</div>;
};

export default TabTitle;

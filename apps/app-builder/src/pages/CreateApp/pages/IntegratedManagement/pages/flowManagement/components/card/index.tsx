import { Button, Switch } from '@arco-design/web-react';
import { IconRobot } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './index.module.less';

/**
 * FlowCard 组件
 * 用于流程管理页面的卡片展示
 */
const FlowCard: React.FC = () => {
  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderLeft}>
          <div className={styles.cardHeaderLeftIcon}>
            <IconRobot />
          </div>
          <div className={styles.cardHeaderLeftContent}>
            <div className={styles.cardHeaderLeftContentTitle}>用户注册流程</div>
            <div className={styles.cardHeaderLeftContentDesc}>flow-wudh-001</div>
          </div>
        </div>
        <div className={styles.cardHeaderRight}>
          <div className={styles.cardHeaderRightTitle}>交互流执行</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 创建时间</div>
          <div className={styles.cardBodyRowContent}> 2025-08-25 12:23:12</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 最后执行</div>
          <div className={styles.cardBodyRowContent}> 3天前</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 执行次数</div>
          <div className={styles.cardBodyRowContent}> 4次</div>
        </div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}>
          <Switch /> 已启用
        </div>
        <div className={styles.cardFooterRight}>
          <Button type="text" size="small">
            编辑
          </Button>
          <Button type="text" size="small">
            调试
          </Button>
          <Button type="text" size="small">
            更多
          </Button>
        </div>
      </div>
    </div>
  );
};

export default FlowCard;

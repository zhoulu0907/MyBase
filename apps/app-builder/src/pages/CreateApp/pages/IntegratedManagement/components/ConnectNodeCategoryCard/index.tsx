import jsNodeIcon from '@/assets/flow/connect/js_node.svg';
import { Typography } from '@arco-design/web-react';
import { type ConnectFlowNode } from '@onebase/app';
import React from 'react';
import styles from './index.module.less';

/**
 * FlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface CardProps {
  data: ConnectFlowNode;
}

const ConnectNodeCategoryCard: React.FC<CardProps> = ({ data }) => {
  return (
    <div className={styles.card}>
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderIcon}>{data.typeCode === 'script' && <img src={jsNodeIcon} alt="" />}</div>
        <div className={styles.cardHeaderContent}>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentTitle}>
            {data.typeName}
          </Typography.Text>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentDesc}>
            v1.0.0
          </Typography.Text>
        </div>
        <div className={styles.cardHeaderTag}>
          <div className={styles.cardHeaderTagTitle}>系统预设</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}></div>
          <div className={styles.cardBodyRowContent}>{/* todo 暂不展示数据 */}</div>
        </div>
      </div>
      <div className={styles.cardFooter}></div>
    </div>
  );
};

export default ConnectNodeCategoryCard;

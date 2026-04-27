import { type FlowNodeEntity } from '@flowgram.ai/fixed-layout-editor';
import { type FlowNodeRegistry } from '../../typings';
import styles from './index.module.less';

export const getIcon = (node: FlowNodeEntity) => {
  const icon = node.getNodeRegistry<FlowNodeRegistry>().info?.icon;
  if (!icon) return null;
  return <img src={icon} className={styles.icon} />;
};

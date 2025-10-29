import { IconLeft, IconUp } from '@arco-design/web-react/icon';
import { type FlowNodeEntity, FlowNodeRenderData, FlowNodeTransformData } from '@flowgram.ai/fixed-layout-editor';
import styles from './index.module.less';

export interface CollapseProps {
  activateNode: FlowNodeEntity;
  collapseNode: FlowNodeEntity;
  hoverActivated: boolean;
  node: FlowNodeEntity;
}

export function CollapseNode(props: CollapseProps): JSX.Element {
  const { activateNode, collapseNode, hoverActivated } = props;
  const activateData = activateNode?.getData(FlowNodeRenderData);
  const transform = collapseNode.getData(FlowNodeTransformData);

  const collapseBlock = () => {
    transform.collapsed = true;
    activateData?.toggleMouseLeave();
  };

  const openBlock = () => {
    transform.collapsed = false;
  };

  const getContainer = () => {
    // 收起状态 展示数字
    if (transform.collapsed) {
      const childCount = collapseNode.allCollapsedChildren.filter(
        (child) => !child.hidden && child !== activateNode
      ).length;
      return (
        <div
          className={styles.collapseStyle}
          style={{ background: hoverActivated ? 'rgb(var(--primary-4))' : '#BBBFC4' }}
          onClick={openBlock}
        >
          {childCount}
        </div>
      );
    }
    // 展开状态 展示箭头
    return (
      <div
        className={styles.collapseStyle}
        style={{ background: hoverActivated ? 'rgb(var(--primary-4))' : '#BBBFC4' }}
        onClick={collapseBlock}
      >
        {activateNode?.isVertical ? <IconUp /> : <IconLeft />}
      </div>
    );
  };

  return <>{getContainer()}</>;
}

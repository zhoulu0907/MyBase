import { Tabs } from '@arco-design/web-react';
import { FlowNodeEntity, FlowNodeRegistry, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { FlowNodeRegistries } from '../nodes';
import styles from './index.module.less';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { NodeType } from '../nodes/const';

function Node(props: { label: string; icon: JSX.Element; onClick: () => void }) {
  return (
    <div className={styles.nodeWrap} onClick={props.onClick}>
      <div style={{ fontSize: 14 }}>{props.icon}</div>
      <div className={styles.nodeLabel}>{props.label}</div>
    </div>
  );
}

export function NodeList(props: { onSelect: (meta: any) => void; from: FlowNodeEntity }) {
  const context = useClientContext();
  const handleClick = (registry: FlowNodeRegistry) => {
    const addProps = registry.onAdd(context, props.from);
    props.onSelect?.(addProps);
  };

  const controlNodes = FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).filter(
    (registry) => registry.category === 'control'
  );
  const dataNodes = FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).filter(
    (registry) => registry.category === 'data'
  );
  const interactionNodes = FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).filter(
    (registry) => registry.category === 'interaction'
  );
  const otherNodes = FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).filter(
    (registry) => registry.category === 'other'
  );

  const showNodes = (nodeList: FlowNodeRegistry[]) => {
    return (
      <div className={styles.nodePanel}>
        {nodeList.map(
          (registry) =>
            (registry.canAdd?.(context, props.from) ?? true) && (
              <Node
                key={registry.type}
                icon={<img style={{ width: 10, height: 10, borderRadius: 4 }} src={registry.info.icon} />}
                label={registry.title as string}
                onClick={() => handleClick(registry)}
              />
            )
        )}
      </div>
    );
  };

  const { nodes } = triggerEditorSignal;

  return (
    <div className={styles.nodeList} style={{ width: 400 }}>
      <Tabs>
        <Tabs.TabPane key="control" title="控制节点">
          {showNodes(controlNodes)}
        </Tabs.TabPane>
        <Tabs.TabPane key="data" title="数据节点">
          {showNodes(dataNodes)}
        </Tabs.TabPane>
        {nodes.value[0]?.type === NodeType.START_FORM && (
          <Tabs.TabPane key="interaction" title="交互节点">
            {showNodes(interactionNodes)}
          </Tabs.TabPane>
        )}
        <Tabs.TabPane key="other" title="其他节点">
          {showNodes(otherNodes)}
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
}

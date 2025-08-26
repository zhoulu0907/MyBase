import { FlowNodeEntity, FlowNodeRegistry, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import styled from 'styled-components';

import { Tabs } from '@arco-design/web-react';
import { FlowNodeRegistries } from '../nodes';
import styles from './index.module.less';

const NodeWrap = styled.div`
  width: 100%;
  height: 32px;
  border-radius: 5px;
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 19px;
  padding: 0 15px;
  &:hover {
    background-color: hsl(252deg 62% 55% / 9%);
    color: hsl(252 62% 54.9%);
  },
`;

const NodeLabel = styled.div`
  font-size: 12px;
  margin-left: 10px;
`;

function Node(props: { label: string; icon: JSX.Element; onClick: () => void }) {
  return (
    <NodeWrap onClick={props.onClick}>
      <div style={{ fontSize: 14 }}>{props.icon}</div>
      <NodeLabel>{props.label}</NodeLabel>
    </NodeWrap>
  );
}

const NodesWrap = styled.div`
  max-height: 500px;
  overflow: auto;
  &::-webkit-scrollbar {
    display: none;
  }
`;

export function NodeList(props: { onSelect: (meta: any) => void; from: FlowNodeEntity }) {
  const context = useClientContext();
  const handleClick = (registry: FlowNodeRegistry) => {
    const addProps = registry.onAdd(context, props.from);
    props.onSelect?.(addProps);
  };
  return (
    // <NodesWrap style={{ width: 80 * 2 + 20 }}>
    //   {FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).map(
    //     (registry) =>
    //       (registry.canAdd?.(context, props.from) ?? true) && (
    //         <Node
    //           key={registry.type}
    //           icon={<img style={{ width: 10, height: 10, borderRadius: 4 }} src={registry.info.icon} />}
    //           label={registry.title as string}
    //           onClick={() => handleClick(registry)}
    //         />
    //       )
    //   )}
    // </NodesWrap>
    <div className={styles.nodeList} style={{ width: 450 }}>
      <Tabs>
        <Tabs.TabPane key="trigger" title="触发节点">
          {FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).map(
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
        </Tabs.TabPane>
        <Tabs.TabPane key="control" title="控制节点">
          {FlowNodeRegistries.filter((registry) => !registry.meta?.addDisable).map(
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
        </Tabs.TabPane>
        <Tabs.TabPane key="data" title="数据节点">
          无
        </Tabs.TabPane>
        <Tabs.TabPane key="interaction" title="交互节点">
          无
        </Tabs.TabPane>
        <Tabs.TabPane key="other" title="其他节点">
          无
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
}

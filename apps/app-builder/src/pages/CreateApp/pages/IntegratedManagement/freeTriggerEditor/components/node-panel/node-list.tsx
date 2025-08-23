import React, { type FC } from 'react';

import { useClientContext, type WorkflowNodeEntity } from '@flowgram.ai/free-layout-editor';
import { type NodePanelRenderProps } from '@flowgram.ai/free-node-panel-plugin';
import styled from 'styled-components';

import { nodeRegistries } from '../../node';
import { type FlowNodeRegistry } from '../../typings';

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
  }
`;

const NodeLabel = styled.div`
  font-size: 12px;
  margin-left: 10px;
`;

interface NodeProps {
  label: string;
  icon: JSX.Element;
  onClick: React.MouseEventHandler<HTMLDivElement>;
  disabled: boolean;
}

function Node(props: NodeProps) {
  return (
    <NodeWrap
      data-testid={`demo-free-node-list-${props.label}`}
      onClick={props.disabled ? undefined : props.onClick}
      style={props.disabled ? { opacity: 0.3 } : {}}
    >
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

interface NodeListProps {
  onSelect: NodePanelRenderProps['onSelect'];
  containerNode?: WorkflowNodeEntity;
}

export const NodeList: FC<NodeListProps> = (props) => {
  const { onSelect, containerNode } = props;
  const context = useClientContext();
  const handleClick = (e: React.MouseEvent, registry: FlowNodeRegistry) => {
    const json = registry.onAdd?.(context);
    onSelect({
      nodeType: registry.type as string,
      selectEvent: e,
      nodeJSON: json
    });
  };

  return (
    <NodesWrap style={{ width: 80 * 2 + 20, height: 100 }}>
      {nodeRegistries
        .filter((register) => register.meta.nodePanelVisible !== false)
        .filter((register) => {
          if (register.meta.onlyInContainer) {
            return register.meta.onlyInContainer === containerNode?.flowNodeType;
          }
          return true;
        })
        .map((registry) => (
          <Node
            key={registry.type}
            disabled={!(registry.canAdd?.(context) ?? true)}
            icon={<img style={{ width: 10, height: 10, borderRadius: 4 }} src={registry.info?.icon} />}
            label={registry.info?.description ?? ''}
            onClick={(e) => handleClick(e, registry)}
          />
        ))}
    </NodesWrap>
  );
};

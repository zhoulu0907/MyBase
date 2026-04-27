import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Input, Tabs } from '@arco-design/web-react';
import { FlowNodeEntity, FlowNodeRegistry, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { debounce } from 'lodash-es';
import { useCallback, useState } from 'react';
import { FlowNodeRegistries } from '../nodes';
import { NodeType } from '@onebase/common';
import './index.less';

interface AllNodeRegistry {
  label: string;
  type: string;
  nodeList: FlowNodeRegistry[];
}

function Node(props: { label: string; icon: JSX.Element; onClick: () => void }) {
  return (
    <div className="nodeWrap" onClick={props.onClick}>
      <div style={{ width: 18, height: 18 }}>{props.icon}</div>
      <div className="nodeLabel">{props.label}</div>
    </div>
  );
}

export function NodeList(props: { onSelect: (meta: any) => void; from: FlowNodeEntity }) {
  const context = useClientContext();
  const { nodes } = triggerEditorSignal;

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
  // nodes.value?.[0]?.type === NodeType.START_FORM

  const allNodes = [
    { label: '控制节点', type: 'control', nodeList: controlNodes },
    { label: '数据节点', type: 'data', nodeList: dataNodes },
    {
      label: '交互节点',
      type: 'interaction',
      nodeList: nodes.value?.[0]?.type === NodeType.START_FORM ? interactionNodes : []
    },
    { label: '其他节点', type: 'other', nodeList: otherNodes }
  ];

  const [showNodeList, setShowNodeList] = useState<FlowNodeRegistry[]>([]);
  const [showAllNodeList, setAllShowNodeList] = useState<AllNodeRegistry[]>(allNodes);
  const [activeTab, setActiveTab] = useState<string>('all');
  const [searchValue, setSearchValue] = useState<string>('');

  const showNodes = (nodeList: FlowNodeRegistry[]) => {
    return (
      <div className="nodePanel">
        {nodeList.map(
          (registry) =>
            (registry.canAdd?.(context, props.from) ?? true) && (
              <Node
                key={registry.type}
                icon={<img style={{ width: 18, height: 18, borderRadius: 4 }} src={registry.info.icon} />}
                label={registry.title as string}
                onClick={() => handleClick(registry)}
              />
            )
        )}
      </div>
    );
  };

  const showAllNodes = () => {
    return (
      <div>
        {showAllNodeList.map((itemNodes, index) =>
          itemNodes.nodeList?.length ? (
            <div className="allItem" key={index}>
              <div className="label">{itemNodes.label}</div>
              {showNodes(itemNodes.nodeList)}
            </div>
          ) : null
        )}
      </div>
    );
  };

  // 搜索条件变更
  const onSearchChange = useCallback(
    debounce((type, value) => {
      getShowData(type, value);
    }, 500),
    []
  );

  // 根据tab类型获取数据
  const getShowData = (type: string, value: string) => {
    if (type === 'all') {
      setShowNodeList([]);
      setAllShowNodeList([
        {
          label: '控制节点',
          type: 'control',
          nodeList: controlNodes.filter((item) => !value || item.title.indexOf(value) !== -1)
        },
        {
          label: '数据节点',
          type: 'data',
          nodeList: dataNodes.filter((item) => !value || item.title.indexOf(value) !== -1)
        },
        {
          label: '交互节点',
          type: 'interaction',
          nodeList:
            nodes.value?.[0]?.type === NodeType.START_FORM
              ? interactionNodes.filter((item) => !value || item.title.indexOf(value) !== -1)
              : []
        },
        {
          label: '其他节点',
          type: 'other',
          nodeList: otherNodes.filter((item) => !value || item.title.indexOf(value) !== -1)
        }
      ]);
    } else if (type === 'control') {
      setShowNodeList(controlNodes.filter((item) => !value || item.title.indexOf(value) !== -1));
      setAllShowNodeList([]);
    } else if (type === 'data') {
      setShowNodeList(dataNodes.filter((item) => !value || item.title.indexOf(value) !== -1));
      setAllShowNodeList([]);
    } else if (type === 'interaction') {
      setShowNodeList(interactionNodes.filter((item) => !value || item.title.indexOf(value) !== -1));
      setAllShowNodeList([]);
    } else if (type === 'other') {
      setShowNodeList(otherNodes.filter((item) => !value || item.title.indexOf(value) !== -1));
      setAllShowNodeList([]);
    }
  };

  const getHeight = (activeTab: string) => {
    if (activeTab == 'all') {
      return '646px';
    } else if (activeTab == 'control') {
      return '185px';
    } else if (activeTab == 'data') {
      return '185px';
    } else if (activeTab == 'interaction') {
      return '185px';
    } else if (activeTab == 'other') {
      return '185px';
    }
  };

  return (
    <div className="nodeList" style={{ width: 412 }}>
      <div className="search">
        <Input.Search
          placeholder="搜索节点"
          className="searchWrapper"
          value={searchValue}
          onChange={(e) => {
            setSearchValue(e || '');
            onSearchChange(activeTab, e);
          }}
        />
      </div>
      <div
        style={{
          height: getHeight(activeTab),
          transition: 'height 0.3s ease-in-out',
          overflow: 'hidden'
        }}
      >
        <Tabs
          activeTab={activeTab}
          onChange={(e) => {
            setActiveTab(e);
            getShowData(e, searchValue);
          }}
        >
          <Tabs.TabPane key="all" title="全部">
            {showAllNodes()}
          </Tabs.TabPane>
          <Tabs.TabPane key="control" title="控制节点">
            {showNodes(showNodeList)}
          </Tabs.TabPane>
          <Tabs.TabPane key="data" title="数据节点">
            {showNodes(showNodeList)}
          </Tabs.TabPane>
          {nodes.value?.[0]?.type === NodeType.START_FORM && (
            <Tabs.TabPane key="interaction" title="交互节点">
              {showNodes(showNodeList)}
            </Tabs.TabPane>
          )}
          <Tabs.TabPane key="other" title="其他节点">
            {showNodes(showNodeList)}
          </Tabs.TabPane>
        </Tabs>
      </div>
    </div>
  );
}

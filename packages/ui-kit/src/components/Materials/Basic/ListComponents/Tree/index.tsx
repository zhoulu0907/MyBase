import { Tree } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import {
  dataMethodPageV2,
  PageMethodV2Params
} from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import './index.css';
import type { XTreeConfig } from './schema';
import { IconCaretDown } from '@arco-design/web-react/icon';

const XTree = memo(
  (
    props: XTreeConfig & {
      runtime?: boolean;
    }
  ) => {
    useSignals();

    const { runtime = true } = props;

    const {
      id,
      status,
      metaData,
      treeFields,
      defaultExpandLevel,
      showLine
    } = props;

    const [treeData, setTreeData] = useState<any[]>([]);
    const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

    const getDefaultPreviewData = () => {
      return [
        {
          key: '1',
          title: '根节点',
          children: [
            {
              key: '1-1',
              title: '子节点',
              children: [
                {
                  key: '1-1-1',
                  title: '叶子节点 1-1-1'
                },
                {
                  key: '1-1-2',
                  title: '叶子节点 1-1-2'
                }
              ]
            },
            {
              key: '1-2',
              title: '子节点 1-2'
            }
          ]
        },
        {
          key: '2',
          title: '根节点 2',
          children: [
            {
              key: '2-1',
              title: '子节点 2-1'
            }
          ]
        }
      ];
    };

    useEffect(() => {
      if (isRuntimeEnv()) {
        if (metaData && treeFields && treeFields.length > 0) {
          loadTreeData();
        } else {
          setTreeData(getDefaultPreviewData());
        }
      } else {
        setTreeData(getDefaultPreviewData());
      }
    }, [metaData, treeFields]);

    const loadTreeData = async () => {
      try {
        const params: PageMethodV2Params = {
          entityUuid: metaData,
          pageNo: 1,
          pageSize: 1000,
          filterCondition: {}
        };

        const res = await dataMethodPageV2(params);

        if (res?.data?.list) {
          const treeStructure = buildTreeStructure(res.data.list, treeFields);
          setTreeData(treeStructure);

          const defaultExpanded = getDefaultExpandedKeys(treeStructure, defaultExpandLevel || 2);
          setExpandedKeys(defaultExpanded);
        }
      } catch (error) {
        console.error('加载树数据失败:', error);
      }
    };

    const buildTreeStructure = (data: any[], fields: any[]): any[] => {
      if (!data || data.length === 0) return [];

      const levelFieldMap = new Map();
      fields.forEach(field => {
        levelFieldMap.set(field.level, field.fieldName);
      });

      const maxLevel = fields.length;
      const rootNodes: any[] = [];
      const nodeMap = new Map<string, any>();

      data.forEach(item => {
        for (let level = 1; level <= maxLevel; level++) {
          const fieldName = levelFieldMap.get(level);
          if (!fieldName) continue;

          const fieldValue = item[fieldName];
          const nodeKey = `${level}-${fieldValue}`;

          if (level === 1) {
            if (!nodeMap.has(nodeKey)) {
              const rootNode = {
                key: nodeKey,
                title: fieldValue,
                level: level
              };
              nodeMap.set(nodeKey, rootNode);
              rootNodes.push(rootNode);
            }
          } else {
            const parentField = levelFieldMap.get(level - 1);
            const parentValue = item[parentField];
            const parentKey = `${level - 1}-${parentValue}`;
            const parentNode = nodeMap.get(parentKey);

            if (parentNode) {
              if (!nodeMap.has(nodeKey)) {
                const childNode = {
                  key: nodeKey,
                  title: fieldValue,
                  level: level,
                  data: item
                };
                nodeMap.set(nodeKey, childNode);

                if (!parentNode.children) {
                  parentNode.children = [];
                }
                parentNode.children.push(childNode);
              }
            }
          }
        }
      });

      return rootNodes;
    };

    const getDefaultExpandedKeys = (nodes: any[], level: number): string[] => {
      const keys: string[] = [];

      const traverse = (nodeList: any[], currentLevel: number) => {
        if (currentLevel > level) return;

        nodeList.forEach(node => {
          keys.push(node.key);
          if (node.children && node.children.length > 0) {
            traverse(node.children, currentLevel + 1);
          }
        });
      };

      traverse(nodes, 1);
      return keys;
    };

    const handleSelect = (selectedKeys: string[], info: any) => {
      setSelectedKeys(selectedKeys);
      console.log('选中节点:', selectedKeys, info);
    };

    const handleExpand = (expandedKeys: string[], info: any) => {
      setExpandedKeys(expandedKeys);
    };

    if (status === 'hidden') {
      return null;
    }

    return (
      <div className="x-tree-container">
        <Tree
          treeData={treeData}
          icons={{
            switcherIcon: <IconCaretDown />
          }}
          showLine
          autoExpandParent
          onSelect={handleSelect}
          onExpand={handleExpand}
        />
      </div>
    );
  }
);

XTree.displayName = 'XTree';

export default XTree;

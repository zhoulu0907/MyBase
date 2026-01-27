import { Tree } from '@arco-design/web-react';
import { memo, useEffect, useState } from 'react';
import {
  CATEGORY_TYPE,
  dataMethodPageV2,
  menuSignal,
  PageMethodV2Params,
  type AppEntityField
} from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { ENTITY_FIELD_TYPE } from '../../../../DataFactory/const';
import './index.css';
import type { XTreeConfig } from './schema';

const XTree = memo(
  (
    props: XTreeConfig & {
      runtime?: boolean;
      preview?: boolean;
      pageSetType?: number;
    }
  ) => {
    useSignals();

    const { runtime = true, preview, pageSetType } = props;
    const { curMenu } = menuSignal;

    const {
      id,
      label,
      status,
      defaultValue,
      metaData,
      tableName,
      treeFields,
      defaultExpandLevel,
      border,
      showLine,
      hover
    } = props;

    const [treeData, setTreeData] = useState<any[]>([]);
    const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<React.Key[]>([]);
    const [loading, setLoading] = useState(false);

    const getDefaultPreviewData = () => {
      return [
        {
          key: '1',
          title: '根节点 1',
          children: [
            {
              key: '1-1',
              title: '子节点 1-1',
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
      setLoading(true);
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
      } finally {
        setLoading(false);
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
        let currentNode: any = {
          key: item.id,
          title: item[levelFieldMap.get(1)],
          children: []
        };

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
                children: [],
                level: level
              };
              nodeMap.set(nodeKey, rootNode);
              rootNodes.push(rootNode);
            }
            currentNode = nodeMap.get(nodeKey);
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
                  children: [],
                  level: level,
                  data: item
                };
                nodeMap.set(nodeKey, childNode);
                parentNode.children.push(childNode);
              }
              currentNode = nodeMap.get(nodeKey);
            }
          }
        }
      });

      return cleanEmptyChildren(rootNodes);
    };

    const cleanEmptyChildren = (nodes: any[]): any[] => {
      return nodes.map(node => {
        if (node.children && node.children.length > 0) {
          node.children = cleanEmptyChildren(node.children);
        } else {
          delete node.children;
        }
        return node;
      });
    };

    const getDefaultExpandedKeys = (nodes: any[], level: number): React.Key[] => {
      const keys: React.Key[] = [];

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

    const handleSelect = (selectedKeys: React.Key[], info: any) => {
      setSelectedKeys(selectedKeys);
      console.log('选中节点:', selectedKeys, info);
    };

    const handleExpand = (expandedKeys: React.Key[], info: any) => {
      setExpandedKeys(expandedKeys);
    };

    if (status === 'hidden') {
      return null;
    }

    return (
      <div className="x-tree-container">
        {label?.display && (
          <div className="x-tree-label" style={{ marginBottom: '8px' }}>
            {label.text}
          </div>
        )}
        <Tree
          data={treeData}
          showLine={showLine}
          bordered={border}
          hoverable={hover}
          defaultExpandAll={false}
          defaultExpandedKeys={expandedKeys}
          selectedKeys={selectedKeys}
          onSelect={handleSelect}
          onExpand={handleExpand}
          loading={loading}
          disabled={status === 'readonly'}
          blockNode
        />
      </div>
    );
  }
);

XTree.displayName = 'XTree';

export default XTree;

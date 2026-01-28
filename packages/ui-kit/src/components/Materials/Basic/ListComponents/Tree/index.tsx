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

    // 根据配置的字段生成预览数据
    const generatePreviewDataFromFields = (fields: any[]): any[] => {
      if (!fields || fields.length === 0) {
        return getDefaultPreviewData();
      }

      // 按层级排序字段
      const sortedFields = [...fields].sort((a, b) => a.level - b.level);
      
      // 生成预览数据
      const generateLevelData = (level: number, parentKey: string = ''): any[] => {
        if (level > sortedFields.length) return [];
        
        const fieldConfig = sortedFields[level - 1];
        const levelCount = level === 1 ? 2 : 2; // 根节点2个，其他层级2个
        
        return Array.from({ length: levelCount }, (_, index) => {
          const key = parentKey ? `${parentKey}-${index + 1}` : `${index + 1}`;
          // 只显示字段的displayName或fieldName，不添加额外的节点字样
          const title = fieldConfig.displayName || fieldConfig.fieldName;
          
          const children = generateLevelData(level + 1, key);
          
          return {
            key,
            title,
            level: level,
            fieldName: fieldConfig.fieldName,
            fieldValue: title,
            ...(children.length > 0 && { children })
          };
        });
      };

      return generateLevelData(1);
    };

    useEffect(() => {
    if (isRuntimeEnv()) {
      // 运行时环境：有配置就加载真实数据，没配置就给默认数据
      if (metaData && treeFields && treeFields.length > 0) {
        loadTreeData();
      } else {
        setTreeData(getDefaultPreviewData());
      }
    } else {
      // 编辑/预览环境：根据treeFields配置生成预览数据
      if (treeFields && treeFields.length > 0) {
        // 根据配置的字段生成结构化的预览数据
        const previewData = generatePreviewDataFromFields(treeFields);
        setTreeData(previewData);
      } else {
        setTreeData(getDefaultPreviewData());
      }
    }
  }, [metaData, treeFields, defaultExpandLevel]);

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

      // 按层级排序字段配置
      const sortedFields = [...fields].sort((a, b) => a.level - b.level);
      const levelFieldMap = new Map();
      sortedFields.forEach(field => {
        levelFieldMap.set(field.level, field);
      });

      const rootNodes: any[] = [];
      const nodeMap = new Map<string, any>();

      data.forEach((item, itemIndex) => {
        let parentNode = null;
        
        // 按层级顺序处理每个层级的字段
        for (let i = 0; i < sortedFields.length; i++) {
          const fieldConfig = sortedFields[i];
          const level = fieldConfig.level;
          const fieldName = fieldConfig.fieldName;
          const fieldValue = item[fieldName];
          
          if (!fieldValue) continue; // 跳过空值
          
          const nodeKey = `${level}-${fieldValue}-${itemIndex}`; // 添加itemIndex避免重复
          
          // 查找或创建节点
          let currentNode = nodeMap.get(nodeKey);
          if (!currentNode) {
            currentNode = {
              key: nodeKey,
              title: fieldValue,
              level: level,
              fieldName: fieldName,
              fieldValue: fieldValue,
              data: item,
              children: []
            };
            nodeMap.set(nodeKey, currentNode);
            
            // 建立父子关系
            if (level === 1) {
              // 根节点
              rootNodes.push(currentNode);
            } else if (parentNode) {
              // 子节点，添加到父节点的children中
              if (!parentNode.children) {
                parentNode.children = [];
              }
              // 避免重复添加
              if (!parentNode.children.some(child => child.key === currentNode.key)) {
                parentNode.children.push(currentNode);
              }
            }
          }
          
          // 更新父节点引用，用于下一层级
          parentNode = currentNode;
        }
      });

      // 清理空children数组
      rootNodes.forEach(node => {
        if (node.children && node.children.length === 0) {
          delete node.children;
        }
      });

      // 递归清理所有节点的空children
      const cleanEmptyChildren = (nodes: any[]) => {
        nodes.forEach(node => {
          if (node.children && node.children.length === 0) {
            delete node.children;
          } else if (node.children) {
            cleanEmptyChildren(node.children);
          }
        });
      };
      cleanEmptyChildren(rootNodes);

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

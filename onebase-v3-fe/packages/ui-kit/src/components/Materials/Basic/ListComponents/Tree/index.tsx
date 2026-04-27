import { Tree, Input } from '@arco-design/web-react';
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
      enableMinHeight,
      enableMaxHeight,
      minHeight,
      maxHeight
    } = props || {};

    const [treeData, setTreeData] = useState<any[]>([]);
    const [expandedKeys, setExpandedKeys] = useState<string[]>([]);
    const [selectedKeys, setSelectedKeys] = useState<string[]>([]);
    const [searchValue, setSearchValue] = useState<string>('');
    const [filteredTreeData, setFilteredTreeData] = useState<any[]>([]);

    const getDefaultPreviewData = () => {
      return [];
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
        const levelCount = 1; // 只生成一个节点

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
      // 编辑/预览环境：根据treeFields配置生成预览数据
      if (treeFields && treeFields.length > 0) {
        // 根据配置的字段生成结构化的预览数据
        const previewData = generatePreviewDataFromFields(treeFields);
        setTreeData(previewData);
        // 设置默认展开层级
        const expandedKeys = getDefaultExpandedKeys(previewData, defaultExpandLevel || 2);
        setExpandedKeys(expandedKeys);
      } else {
        setTreeData(getDefaultPreviewData());
        // 设置默认展开层级
        const expandedKeys = getDefaultExpandedKeys(getDefaultPreviewData(), defaultExpandLevel || 2);
        setExpandedKeys(expandedKeys);
      }
    }, [metaData, treeFields, defaultExpandLevel]);

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

    // 搜索过滤逻辑
    useEffect(() => {
      if (!treeData || treeData.length === 0) {
        setFilteredTreeData([]);
        return;
      }

      if (!searchValue || searchValue.trim() === '') {
        setFilteredTreeData(treeData);
        return;
      }

      const searchTerm = searchValue.toLowerCase();

      // 递归过滤树节点
      const filterNodes = (nodes: any[]): any[] => {
        return nodes
          .map(node => {
            const match = node.title.toLowerCase().includes(searchTerm);
            const children = node.children ? filterNodes(node.children) : [];
            const hasMatch = match || children.length > 0;

            if (hasMatch) {
              return {
                ...node,
                children: children.length > 0 ? children : undefined
              };
            }
            return null;
          })
          .filter(Boolean);
      };

      const filtered = filterNodes(treeData);
      setFilteredTreeData(filtered);
    }, [treeData, searchValue]);

    if (status === 'hidden') {
      return null;
    }

    // 计算容器样式
    const containerStyle: React.CSSProperties = {};
    if (enableMinHeight !== undefined && enableMinHeight && minHeight !== null && minHeight !== undefined) {
      containerStyle.minHeight = `${minHeight}px`;
    }
    if (enableMaxHeight !== undefined && enableMaxHeight && maxHeight !== null && maxHeight !== undefined) {
      containerStyle.maxHeight = `${maxHeight}px`;
      containerStyle.overflowY = 'auto';
    } else {
      containerStyle.overflowY = 'visible';
    }

    return (
      <div className="x-tree-container" style={containerStyle}>
        <div style={{ marginBottom: '12px' }}>
          <Input.Search
            placeholder="搜索树节点..."
            value={searchValue}
            onChange={setSearchValue}
            style={{ width: '100%' }}
          />
        </div>
        <Tree
          treeData={filteredTreeData.length > 0 ? filteredTreeData : treeData}
          expandedKeys={expandedKeys}
          icons={{
            switcherIcon: <IconCaretDown />
          }}
          onSelect={handleSelect}
          onExpand={handleExpand}
        />
      </div>
    );
  }
);

XTree.displayName = 'XTree';

export default XTree;

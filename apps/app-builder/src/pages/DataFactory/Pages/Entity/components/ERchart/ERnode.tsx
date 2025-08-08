import { Node } from '@antv/x6';
import { Button, Popover, Space } from '@arco-design/web-react';
import { IconCaretDown, IconCaretUp, IconMoreVertical, IconSync } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import { type EntityNode } from '../../../../utils/interface';
import styles from './ERnode.module.less';
import { ENTITY_FIELD_TYPE } from '../../../../utils/constans';
// X6 节点组件接口
interface X6NodeProps {
  node: Node;
}

// 节点数据接口
interface NodeData {
  data: EntityNode;
  onNodeEdit?: (data: Partial<EntityNode>) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
  onNodeAddField?: (data: Partial<EntityNode>) => void;
  onNodeAddRelation?: (id: string) => void;
  onFieldClick?: (fieldId: string) => void;
}

const EntityNodeComponent: React.FC<X6NodeProps> = ({ node }) => {
  const [nodeCollapsed, setNodeCollapsed] = useState({
    system: false,
    custom: false
  });
  // 从 node 的 data 中获取节点数据
  const nodeData = (node.getData() as NodeData)?.data;

  if (!nodeData) {
    console.error('nodeData is undefined');
    return <div style={{ padding: '10px', color: 'red' }}>No data</div>;
  }

  const nodeId = nodeData.entityId;

  // 分离系统字段和自定义字段
  const systemFields = nodeData?.fields?.filter((field) => field.isSystemField);
  const customFields = nodeData?.fields?.filter((field) => !field.isSystemField);

  // 折叠逻辑
  const handleToggleSection = (sectionType: 'system' | 'custom', e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setNodeCollapsed({
      ...nodeCollapsed,
      [sectionType]: !nodeCollapsed[sectionType]
    });
  };

  const handleRefresh = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handleAddField = (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onNodeAddField = data?.onNodeAddField;
    if (onNodeAddField && nodeData) {
      onNodeAddField(nodeData);
    }
  };

  const handleAddRelation = (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onNodeAddRelation = data?.onNodeAddRelation;
    if (onNodeAddRelation && nodeData) {
      onNodeAddRelation(nodeData.entityId);
    }
  };

  const handleEdit = (e: Event) => {
    e.stopPropagation(); // 阻止事件冒泡
    // 从 node 的 data 中获取回调函数
    const data = node.getData() as NodeData;
    const onNodeEdit = data?.onNodeEdit;
    if (onNodeEdit && nodeData) {
      onNodeEdit(nodeData);
    }
  };

  const handleDelete = (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    // 从 node 的 data 中获取回调函数
    const data = node.getData() as NodeData;
    const onNodeDelete = data?.onNodeDelete;
    if (onNodeDelete) {
      onNodeDelete(nodeId);
    }
  };

  const handleFieldClick = (fieldId: string, e: React.MouseEvent) => {
    console.log('handleFieldClick', fieldId, e)
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onFieldClick = data?.onFieldClick;
    if (onFieldClick) {
      onFieldClick(fieldId);
    }
  };

  return (
    <div className={styles['node-content']}>
      {/* 节点头部 */}
      <div className={styles['node-header']}>
        <IconSync className={styles['refresh-icon']} onClick={handleRefresh} />
        <span className={styles['node-title']}>{nodeData.entityName || '未命名实体'}</span>
        <Popover
          trigger="hover"
          position="rt"
          className={styles['more-icon-popover']}
          content={
            <Space direction="vertical">
              <Button type="text" onClick={handleEdit}>
                编辑
              </Button>
              <Button type="text" onClick={handleDelete}>
                删除
              </Button>
            </Space>
          }
        >
          <IconMoreVertical className={styles['more-icon']} />
        </Popover>
      </div>

      {/* 节点主体 */}
      <div className={styles['node-body']}>
        {/* 渲染系统字段 */}
        {systemFields?.length > 0 && (
          <div className={styles['field-section']}>
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>系统字段</span>
              <span className={styles['section-count']}>({systemFields.length})</span>
              <div className={`${styles['collapse-icon']}`} onClick={(e) => handleToggleSection('system', e)}>
                {nodeCollapsed.system ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles['field-section-content']} ${nodeCollapsed.system ? styles['collapsed'] : ''}`}>
              {systemFields.map((field, index) => (
                <div 
                  key={index} 
                  className={`${styles['field-item']} ${styles['system-field']} ${styles['clickable-field']}`}
                  onClick={(e) => handleFieldClick(field.fieldId, e)}
                >
                  <span className={styles['field-name']}>{field.fieldName}</span>
                  <span className={styles['field-type']}>{ENTITY_FIELD_TYPE[field.fieldType as keyof typeof ENTITY_FIELD_TYPE]}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 渲染自定义字段 */}
        {customFields?.length > 0 && (
          <div className={styles['field-section']}>
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>自定义字段</span>
              <span className={styles['section-count']}>({customFields.length})</span>
              <div className={`${styles['collapse-icon']}`} onClick={(e) => handleToggleSection('custom', e)}>
                {nodeCollapsed.custom ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles['field-section-content']} ${nodeCollapsed.custom ? styles['collapsed'] : ''}`}>
              {customFields.map((field, index) => (
                <div 
                  key={index} 
                  className={`${styles['field-item']} ${styles['custom-field']} ${styles['clickable-field']}`}
                  onClick={(e) => handleFieldClick(field.fieldId, e)}
                >
                  <span className={styles['field-name']}>{field.fieldName}</span>
                  <span className={styles['field-type']}>
                    {ENTITY_FIELD_TYPE[field.fieldType as keyof typeof ENTITY_FIELD_TYPE] || field.fieldType}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* 节点底部 */}
      <div className={styles['node-footer']}>
        <Button type="text" onClick={handleAddField} className={styles['node-footer-button']}>
          字段配置
        </Button>
        <Button type="text" onClick={handleAddRelation} className={styles['node-footer-button']}>
          添加关系
        </Button>
      </div>
    </div>
  );
};

export default EntityNodeComponent;

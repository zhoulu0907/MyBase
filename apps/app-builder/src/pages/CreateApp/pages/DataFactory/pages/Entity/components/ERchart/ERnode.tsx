import { ENTITY_FIELD_TYPE, SYSTEM_FIELD_MAP } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { FIELD_TYPE } from '@/pages/CreateApp/pages/DataFactory/utils/const';
import { Node } from '@antv/x6';
import { Button, Popover, Space } from '@arco-design/web-react';
import { IconCaretDown, IconCaretUp, IconSync } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import styles from './ERnode.module.less';
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
  onNodeAddMasterDetail?: (id: string) => void;
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

  // const nodeId = nodeData.entityId;

  // 分离系统字段和自定义字段
  const systemFields = nodeData?.fields?.filter((field) => field.isSystemField === FIELD_TYPE.SYSTEM);
  const customFields = nodeData?.fields?.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM);

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

  const handleAddMasterDetail = (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onNodeAddMasterDetail = data?.onNodeAddMasterDetail;
    if (onNodeAddMasterDetail && nodeData) {
      onNodeAddMasterDetail(nodeData.entityId);
    }
  };

  // const handleEdit = (e: Event) => {
  //   console.log('handleEdit', e, nodeData);
  //   e.stopPropagation(); // 阻止事件冒泡
  //   // 从 node 的 data 中获取回调函数
  //   const data = node.getData() as NodeData;
  //   const onNodeEdit = data?.onNodeEdit;
  //   if (onNodeEdit && nodeData) {
  //     onNodeEdit(nodeData);
  //   }
  // };

  // const handleDelete = (e: Event) => {
  //   e.preventDefault();
  //   e.stopPropagation();
  //   // 从 node 的 data 中获取回调函数
  //   const data = node.getData() as NodeData;
  //   const onNodeDelete = data?.onNodeDelete;
  //   if (onNodeDelete) {
  //     onNodeDelete(nodeId);
  //   }
  // };

  const handleFieldClick = (fieldId: string, e: React.MouseEvent) => {
    console.log('handleFieldClick', fieldId, e);
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onFieldClick = data?.onFieldClick;
    if (onFieldClick) {
      onFieldClick(fieldId);
    }
  };

  const handleNodeClick = (e: React.MouseEvent) => {
    console.log('handleNodeClick', e);
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onNodeEdit = data?.onNodeEdit;
    if (onNodeEdit && nodeData) {
      onNodeEdit(nodeData);
    }
  };

  return (
    <div className={styles['node-content']} onClick={handleNodeClick}>
      {/* 节点头部 */}
      <div className={styles['node-header']}>
        <IconSync className={styles['refresh-icon']} onClick={handleRefresh} />
        <span className={styles['node-title']}>{nodeData.entityName || '未命名实体'}</span>
        {/* 改为点击节点打开编辑弹窗 */}
        {/* <Popover
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
        </Popover> */}
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
                  <span className={styles['field-name']}>
                    {SYSTEM_FIELD_MAP[field.fieldName as keyof typeof SYSTEM_FIELD_MAP] || field.fieldName}
                  </span>
                  <span className={styles['field-type']}>
                    {ENTITY_FIELD_TYPE[field.fieldType as keyof typeof ENTITY_FIELD_TYPE]}
                  </span>
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
                  <span className={styles['field-name']}>{field.displayName}</span>
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
        <Popover
          trigger="hover"
          position="rt"
          content={
            <Space direction="vertical">
              <Button type="text" onClick={handleAddMasterDetail}>
                添加主子关系
              </Button>
              <Button type="text" onClick={handleAddRelation}>
                添加关联关系
              </Button>
            </Space>
          }
        >
          <Button type="text" className={styles['node-footer-button']}>
            添加关系
          </Button>
        </Popover>
      </div>
    </div>
  );
};

export default EntityNodeComponent;

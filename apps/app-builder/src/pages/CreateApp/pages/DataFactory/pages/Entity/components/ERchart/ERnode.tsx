import type { EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Node } from '@antv/x6';
import { Button, Popover, Space, Switch } from '@arco-design/web-react';
import { IconCaretDown, IconCaretUp, IconSync } from '@arco-design/web-react/icon';
import { ENTITY_STATUS, FIELD_TYPE, SYSTEM_FIELD_MAP, useNewNodeStore } from '@onebase/ui-kit';
import { useFieldStore } from '@/store/store_field';
import React, { useState } from 'react';
import styles from './ERnode.module.less';
import { useSignals } from '@preact/signals-react/runtime';
import { newFieldSignal } from '@/store/singals/new_field';
// X6 节点组件接口
interface X6NodeProps {
  node: Node;
}

// 节点数据接口
interface NodeData {
  data: EntityNode;
  selected?: boolean;
  onNodeEdit?: (data: Partial<EntityNode>) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
  onNodeAddField?: (data: Partial<EntityNode>) => void;
  onNodeAddRelation?: (id: string) => void;
  onNodeAddMasterDetail?: (id: string) => void;
  onFieldClick?: (fieldId: string, entityId?: string) => void;
  onStatusChange?: (data: Partial<EntityNode>) => void;
  onUpdatePorts?: (nodeId: string, section: 'system' | 'custom', isCollapsed: boolean) => void;
}

const EntityNodeComponent: React.FC<X6NodeProps> = ({ node }) => {
  useSignals();

  const [nodeCollapsed, setNodeCollapsed] = useState({
    system: true,
    custom: false
  });
  // 从 node 的 data 中获取节点数据
  const nodeData = (node.getData() as NodeData)?.data;
  const isSelected = Boolean((node.getData() as NodeData)?.selected);
  const { newNodes } = useNewNodeStore();

  const { fieldTypes } = useFieldStore();

  if (!nodeData) {
    console.error('nodeData is undefined');
    return <div style={{ padding: '10px', color: 'red' }}>No data</div>;
  }

  // 分离系统字段和自定义字段
  const systemFields = nodeData?.fields?.filter((field) => field.isSystemField === FIELD_TYPE.SYSTEM);
  const customFields = nodeData?.fields?.filter((field) => field.isSystemField === FIELD_TYPE.CUSTOM);

  // 折叠逻辑
  const handleToggleSection = (sectionType: 'system' | 'custom', e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();

    const isNowCollapsed = !nodeCollapsed[sectionType];
    setNodeCollapsed({
      ...nodeCollapsed,
      [sectionType]: isNowCollapsed
    });

    const data = node.getData() as NodeData;
    data?.onUpdatePorts?.(data.data?.entityId, sectionType, isNowCollapsed); // 触发边重连
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
    if (onFieldClick && nodeData) {
      onFieldClick(fieldId, nodeData.entityId);
    }
  };

  const handleStatusChange = (checked: boolean, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onStatusChange = data?.onStatusChange;
    if (onStatusChange && nodeData) {
      onStatusChange({ ...nodeData, status: checked ? ENTITY_STATUS.ENABLE : ENTITY_STATUS.DISABLE });
    }
  };

  return (
    <div className={`${styles.nodeContent} ${isSelected ? styles.nodeSelected : ''}`}>
      {/* 节点头部 */}
      <div className={styles.nodeHeader}>
        <IconSync className={styles.refreshIcon} onClick={handleRefresh} />
        <span className={styles.nodeTitle}>
          {nodeData.entityName || '未命名资产'}
          {newNodes.includes(nodeData.entityId) && <span className={styles.nodeIsNew} />}
        </span>
        <Switch
          checked={nodeData.status === ENTITY_STATUS.ENABLE}
          onChange={(value, e) => handleStatusChange(value, e)}
          checkedText="已启用"
          uncheckedText="已禁用"
          id="status-change-icon"
        />
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
          <IconMoreVertical className={styles.moreIcon} />
        </Popover> */}
      </div>

      {/* 节点主体 */}
      <div className={styles.nodeBody}>
        {/* 渲染系统字段 */}
        {systemFields?.length > 0 && (
          <div className={styles.fieldSection}>
            <div className={styles.fieldSectionHeader}>
              <span className={styles.sectionTitle}>系统字段</span>
              <span className={styles.sectionCount}>({systemFields.length})</span>
              <div
                className={`${styles.collapseIcon}`}
                id="collapse-icon"
                onClick={(e) => handleToggleSection('system', e)}
              >
                {nodeCollapsed.system ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles.fieldSectionContent} ${nodeCollapsed.system ? styles.collapsed : ''}`}>
              {systemFields.map((field, index) => (
                <div
                  key={index}
                  className={`${styles.fieldItem} ${styles.systemField} ${styles.clickableField}`}
                  onClick={(e) => handleFieldClick(field.fieldId, e)}
                >
                  <span className={styles.fieldName}>
                    {SYSTEM_FIELD_MAP[field.fieldName as keyof typeof SYSTEM_FIELD_MAP] || field.fieldName}
                  </span>
                  <span className={styles.fieldType}>
                    {fieldTypes.find((item) => item.fieldType === field.fieldType)?.displayName || field.fieldType}
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 渲染自定义字段 */}
        {customFields?.length > 0 && (
          <div className={styles.fieldSection}>
            <div className={styles.fieldSectionHeader}>
              <span className={styles.sectionTitle}>自定义字段</span>
              <span className={styles.sectionCount}>({customFields.length})</span>
              <div
                className={`${styles.collapseIcon}`}
                id="collapse-icon"
                onClick={(e) => handleToggleSection('custom', e)}
              >
                {nodeCollapsed.custom ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles.fieldSectionContent} ${nodeCollapsed.custom ? styles.collapsed : ''}`}>
              {customFields.map((field, index) => {
                const isNew = newFieldSignal.isNewField(nodeData.entityId, field.fieldId);
                return (
                  <div
                    key={index}
                    className={`${styles.fieldItem} ${styles.customField} ${styles.clickableField}`}
                    onClick={(e) => handleFieldClick(field.fieldId, e)}
                  >
                    <span className={styles.fieldName}>
                      {isNew && <span className={styles.fieldIsNew} />}
                      {field.displayName}
                    </span>
                    <span className={styles.fieldType}>
                      {fieldTypes.find((item) => item.fieldType === field.fieldType)?.displayName || field.fieldType}
                    </span>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </div>

      {/* 节点底部 */}
      <div id="node-footer">
        <Button type="text" onClick={handleAddField} className={styles.nodeFooterButton}>
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
          <Button type="text" className={styles.nodeFooterButton}>
            添加关系
          </Button>
        </Popover>
      </div>
    </div>
  );
};

export default EntityNodeComponent;

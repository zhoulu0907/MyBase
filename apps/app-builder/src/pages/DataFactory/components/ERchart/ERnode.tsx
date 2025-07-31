import React, { useEffect, useState } from 'react';
import { Button, Popover, Space } from '@arco-design/web-react';
import { IconSync, IconMoreVertical, IconCaretDown, IconCaretUp } from '@arco-design/web-react/icon';
import { type EntityNode } from '../../utils/interface';
import styles from './ERnode.module.less';


interface EntityNodeComponentProps {
  node?: {
    getData: () => EntityNode;
  };
  nodeData?: EntityNode; // 保持向后兼容
  mode?: 'view' | 'edit';
  onNodeEdit?: (id: string, data: EntityNode) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;
}

const EntityNodeComponent: React.FC<EntityNodeComponentProps> = ({
  node,
  nodeData,
  mode = 'view',
  onNodeEdit,
  onNodeDelete,
  // onNodeAdd
}) => {
  const [nodeCollapsed, setNodeCollapsed] = useState({ system: false, custom: false });

  // 优先从 node.getData() 获取数据，如果没有则使用 nodeData
  const actualNodeData = node?.getData() || nodeData;
  console.log('EntityNodeComponent rendering:', actualNodeData);
  console.log('Mode:', mode);

  // 确保 actualNodeData 存在
  if (!actualNodeData) {
    console.error('nodeData is undefined');
    return <div style={{ padding: '10px', color: 'red' }}>No data</div>;
  }

  const nodeId = actualNodeData.id;

  // 分离系统字段和自定义字段
  const systemFields = actualNodeData.fields.filter(field => field.isSystem);
  const customFields = actualNodeData.fields.filter(field => !field.isSystem);

  // 折叠逻辑
  const handleToggleSection = (sectionType: 'system' | 'custom', e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('handleToggleSection', sectionType, nodeCollapsed[sectionType]);
    setNodeCollapsed({ ...nodeCollapsed, [sectionType]: !nodeCollapsed[sectionType] });
  };

  const handleRefresh = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('refresh');
  };

  const handleAddField = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('add field');
  };

  const handleAddRelation = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('add relation');
  };

  const handleEdit = (e: React.MouseEvent) => {
    // e.preventDefault();
    e.stopPropagation(); // 阻止事件冒泡
    actualNodeData?.onNodeEdit?.(nodeId, actualNodeData);
  };

  const handleDelete = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    console.log('delete');
    // 删除节点
    actualNodeData?.onNodeDelete?.(nodeId);
  };
 
  useEffect(() => {
    console.log('EntityNodeComponent mounted');
  }, []);

  return (
    <div className={styles['node-content']} >
      <div className={styles['node-header']}>
        <IconSync className={styles['refresh-icon']} onClick={handleRefresh} />
        <span className={styles['node-title']}>
          {actualNodeData.title || '未命名实体'}
        </span>

        <Popover content="更多操作" trigger="hover" position='rt' className={styles['more-icon-popover']} content={
          <Space direction='vertical'>
            <Button type="text" onClick={(e) => handleEdit(e)}>
              编辑
            </Button>
            <Button type="text" onClick={(e) => handleDelete(e)}>
              删除
            </Button>
          </Space>
        }>
          <IconMoreVertical className={styles['more-icon']}/>
        </Popover>
        
        {/* {mode === 'edit' && (
          <IconMoreVertical className={styles['more-icon']} onClick={handleEdit}/>
        )} */}
      </div>
      <div className={styles['node-body']} >
        {systemFields.length > 0 && (
          <div
            className={styles['field-section']}
          >
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>
                系统字段
              </span>
              <span
                className={styles['section-count']}
              >
                ({systemFields.length})
              </span>
              <div
                className={`${styles['collapse-icon']}`}
                onClick={(e) => {
                  handleToggleSection('system', e);
                }}
              >
                {nodeCollapsed.system ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles['field-section-content']} ${nodeCollapsed.system ? styles['collapsed'] : ''}`}>
              {systemFields.map((field, index) => (
                <div
                  key={index}
                  className={`${styles['field-item']} ${styles['system-field']}`}
                >
                  <span className={styles['field-name']}>{field.name}</span>
                  <span className={styles['field-type']}>{field.type}</span>
                </div>
              ))}
            </div>
          </div>
        )}
        {customFields.length > 0 && (
          <div
            className={styles['field-section']}
          >
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>
                自定义字段
              </span>
              <span className={styles['section-count']} >
                ({customFields.length})
              </span>
              <div
                className={`${styles['collapse-icon']}`}
                onClick={(e) => {
                  handleToggleSection('custom', e);
                }}
              >
                {nodeCollapsed.custom ? <IconCaretDown /> : <IconCaretUp />}
              </div>
            </div>
            <div className={`${styles['field-section-content']} ${nodeCollapsed.custom ? styles['collapsed'] : ''}`}>
              {customFields.map((field, index) => (
                <div
                  key={index}
                  className={`${styles['field-item']} ${styles['custom-field']}`}
                >
                  <span className={styles['field-name']}>{field.name}</span>
                  <span className={styles['field-type']}>{field.type}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        <div className={styles['node-footer']}>
          <Button type="text" onClick={(e) => handleAddField(e)}>
            添加字段
          </Button>
          <Button type="text" onClick={(e) => handleAddRelation(e)}>
            添加关系
          </Button>
        </div>
      </div>
    </div>
  );
};

export default EntityNodeComponent;
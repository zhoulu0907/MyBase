import React, { useState } from 'react';
import { Button, Popover, Space } from '@arco-design/web-react';
import { IconSync, IconMoreVertical, IconCaretDown, IconCaretUp } from '@arco-design/web-react/icon';
import { Node } from '@antv/x6';
import { type EntityNode } from '../../utils/interface';
import styles from './ERnode.module.less';

// X6 节点组件接口
interface X6NodeProps {
  node: Node;
}

// 节点数据接口
interface NodeData {
  data: EntityNode;
  onNodeEdit?: (data: EntityNode) => void;
  onNodeAdd?: () => void;
  onNodeDelete?: (id: string) => void;  
  onNodeAddField?: (id: string) => void;
  onNodeAddRelation?: (id: string) => void;
}

const typeMap = {
  'TEXT': '常规短文本',
  'LONG_TEXT': '长文本内容',
  'EMAIL': '邮箱地址',
  'PHONE': '电话号码',
  'URL': '网址链接',
  'ADDRESS': '详细地址',
  'NUMBER': '通用数字',
  'CURRENCY': '货币金额',
  'DATE': '日期',
  'DATETIME': '日期时间',
  'BOOLEAN': '布尔值',
  'PICKLIST': '单选列表',
  'MULTI_PICKLIST': '多选列表',
  'AUTO_CODE': '自动编码',
  'USER': '用户引用',
  'DEPARTMENT': '部门引用',
  'DATA_SELECTION': '数据选择',
  'RELATION': '关联关系',
  'STRUCTURE': '结构化对象',
  'ARRAY': '数组列表',
  'FILE': '文件',
  'IMAGE': '图片',
  'GEOGRAPHY': '地理位置',
  'PASSWORD': '密码',
  'ENCRYPTED': '加密字段',
  'AGGREGATE': '聚合统计',
};

const EntityNodeComponent: React.FC<X6NodeProps> = ({ node }) => {
  const [nodeCollapsed, setNodeCollapsed] = useState({ system: false, custom: false });
  // 从 node 的 data 中获取节点数据
  const nodeData = (node.getData() as NodeData)?.data;

  if (!nodeData) {
    console.error('nodeData is undefined');
    return <div style={{ padding: '10px', color: 'red' }}>No data</div>;
  }

  const nodeId = nodeData.id;

  // 分离系统字段和自定义字段
  const systemFields = nodeData.fields.filter(field => field.isSystem);
  const customFields = nodeData.fields.filter(field => !field.isSystem);

  // 折叠逻辑
  const handleToggleSection = (sectionType: 'system' | 'custom', e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setNodeCollapsed({ ...nodeCollapsed, [sectionType]: !nodeCollapsed[sectionType] });
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
      onNodeAddField(nodeData.id);
    }
  };

  const handleAddRelation = (e: Event) => {
    e.preventDefault();
    e.stopPropagation();
    const data = node.getData() as NodeData;
    const onNodeAddRelation = data?.onNodeAddRelation;
    if (onNodeAddRelation && nodeData) {
      onNodeAddRelation(nodeData.id);
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

  return (
    <div className={styles['node-content']}>
      {/* 节点头部 */}
      <div className={styles['node-header']}>
        <IconSync className={styles['refresh-icon']} onClick={handleRefresh} />
        <span className={styles['node-title']}>
          {nodeData.title || '未命名实体'}
        </span>
        <Popover
          trigger="hover"
          position='rt'
          className={styles['more-icon-popover']}
          content={
            <Space direction='vertical'>
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
        {systemFields.length > 0 && (
          <div className={styles['field-section']}>
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>系统字段</span>
              <span className={styles['section-count']}>({systemFields.length})</span>
              <div
                className={`${styles['collapse-icon']}`}
                onClick={(e) => handleToggleSection('system', e)}
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
                  <span className={styles['field-type']}>{typeMap[field.type as keyof typeof typeMap]}</span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 渲染自定义字段 */}
        {customFields.length > 0 && (
          <div className={styles['field-section']}>
            <div className={styles['field-section-header']}>
              <span className={styles['section-title']}>自定义字段</span>
              <span className={styles['section-count']}>({customFields.length})</span>
              <div
                className={`${styles['collapse-icon']}`}
                onClick={(e) => handleToggleSection('custom', e)}
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
                  <span className={styles['field-type']}>{typeMap[field.type as keyof typeof typeMap] || field.type}</span>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>

      {/* 节点底部 */}
      <div className={styles['node-footer']}>
        <Button type="text" onClick={handleAddField} className={styles['node-footer-button']}>
          添加字段
        </Button>
        <Button type="text" onClick={handleAddRelation} className={styles['node-footer-button']}>
          添加关系
        </Button>
      </div>
    </div>
  );
};

export default EntityNodeComponent;
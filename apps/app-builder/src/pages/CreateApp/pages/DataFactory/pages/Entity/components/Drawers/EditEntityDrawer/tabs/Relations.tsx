import { type EntityNode } from '@/pages/CreateApp/pages/DataFactory/utils/interface';
import { Collapse, Empty, Spin } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import { getEntityRelations } from '@onebase/app';
import { ALL_RELATIONSHIP_TYPE_MAP, type RelationshipType } from '@/pages/CreateApp/pages/DataFactory/utils/relation';
import styles from './Relations.module.less';

// 数据方法接口定义
interface DataRelation {
  id: string;
  relationName: string;
  relationshipType: string;
  sourceEntityName: string;
  sourceEntityId: string;
  sourceFieldId: string;
  targetEntityName: string;
  targetEntityId: string;
  targetFieldId: string;
}

interface DataMethodsProps {
  node: EntityNode;
}

const relationKeyMap: Record<string, string> = {
  sourceEntityName: '左关联资产',
  targetEntityName: '右关联资产',
  sourceFieldDisplayName: '左关联字段',
  targetFieldDisplayName: '右关联字段',
  relationshipType: '关联关系'
};

const DataMethods: React.FC<DataMethodsProps> = ({ node }) => {
  const [relations, setRelations] = useState<DataRelation[]>([]);
  const [loading, setLoading] = useState(false);

  // 获取资产方法数据
  const loadRelations = async () => {
    if (!node?.entityId) return;

    setLoading(true);
    try {
      const response = await getEntityRelations({ entityId: node.entityId });
      console.log('getEntityRelations', response);
      if (response?.list?.length > 0) {
        setRelations(response.list);
      }
    } catch (error) {
      console.error('获取数据方法失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (node?.entityId) {
      loadRelations();
    }
  }, [node?.entityId]);

  return (
    <div className={styles['relations']}>
      <div className={styles.header}>
        <h3>关联关系</h3>
      </div>

      <div className={styles.content}>
        {loading ? (
          <div className={styles.loading}>
            <Spin size={24} />
            <span>加载中...</span>
          </div>
        ) : relations.length === 0 ? (
          <Empty description="暂无关联关系" />
        ) : (
          <Collapse
            expandIconPosition="right"
            bordered={false}
            defaultActiveKey={relations[0].id}
            className={styles['relations-collapse']}
          >
            {relations.map((relation) => (
              <Collapse.Item
                key={relation.id}
                name={relation.id}
                header={
                  <div className={styles['relation-header']}>
                    <span className={styles['relation-name']}>
                      {ALL_RELATIONSHIP_TYPE_MAP[relation.relationshipType as RelationshipType]}
                    </span>
                  </div>
                }
                className={styles['relation-item']}
              >
                <div className={styles['relation-content']}>
                  {Object.keys(relationKeyMap).map((key) => (
                    <div className={styles['description-section']} key={key}>
                      <div className={styles.label}>{relationKeyMap[key]}:</div>
                      {key === 'relationshipType' ? (
                        <div className={styles.text}>
                          {ALL_RELATIONSHIP_TYPE_MAP[relation[key as keyof DataRelation] as RelationshipType]}
                        </div>
                      ) : (
                        <div className={styles.text}>{relation[key as keyof DataRelation]}</div>
                      )}
                    </div>
                  ))}
                </div>
              </Collapse.Item>
            ))}
          </Collapse>
        )}
      </div>
    </div>
  );
};

export default DataMethods;

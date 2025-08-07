import React, { useState, useEffect } from 'react';
import { Layout, Message } from '@arco-design/web-react';
import type { EntityNode } from '../../../../utils/interface';
import { getEntityPage } from '@onebase/app/src/services/entity';
import EntityList from './EntityList';
import EntityDetail from './EntityDetail';
import styles from './index.module.less';

const { Sider, Content } = Layout;

const EntityTable: React.FC = () => {
  const [selectedEntity, setSelectedEntity] = useState<EntityNode | null>(null);
  const [entities, setEntities] = useState<EntityNode[]>([]);
  const [loading, setLoading] = useState(false);

  // 加载实体列表
  const loadEntities = async () => {
    try {
      setLoading(true);
      // TODO id暂时写死
      const response = await getEntityPage({ pageNo: 1, pageSize: 100, datasourceId: '542234204218462208' });
      console.log(response)
      if (response?.list) {
        setEntities(response.list);
        // 如果有实体数据，默认选择第一个
        if (response.list.length > 0 && !selectedEntity) {
          setSelectedEntity(response.list[0]);
        }
      }
    } catch (error) {
      console.error('加载实体列表失败:', error);
      Message.error('加载实体列表失败');
    } finally {
      setLoading(false);
    }
  };

  // 加载实体详情
  // const loadEntityDetail = async (entityId: string) => {
  //   try {
  //     const response = await getEntity(entityId);
  //     console.log('getEntity', response)
  //     if (response.data) {
  //       setSelectedEntity(response.data);
  //     }
  //   } catch (error) {
  //     console.error('加载实体详情失败:', error);
  //     Message.error('加载实体详情失败');
  //   }
  // };

  useEffect(() => {
    loadEntities();
  }, []);

  const handleEntitySelect = async (entity: EntityNode) => {
    setSelectedEntity(entity);
    // 可以在这里调用loadEntityDetail来获取最新的实体详情
    // await loadEntityDetail(entity.id);
  };

  return (
    <Layout className={styles.entityTableContainer}>
      <Sider width={300} className={styles.sider}>
        <EntityList 
          entities={entities}
          selectedEntity={selectedEntity}
          onEntitySelect={handleEntitySelect}
          loading={loading}
        />
      </Sider>
      <Content className={styles.content}>
        {selectedEntity ? (
          <EntityDetail entity={selectedEntity} />
        ) : (
          <div className={styles.emptyState}>
            请选择一个实体查看详情
          </div>
        )}
      </Content>
    </Layout>
  );
};

export default EntityTable; 
import React, { useState } from 'react';
import { List, Avatar, Spin, Button } from '@arco-design/web-react';
import { IconList } from '@arco-design/web-react/icon';
import type { EntityNode } from '../../../../utils/interface';
import CreateEntityPage from '../Modals/CreateEntityModal';
import styles from './EntityList.module.less';

interface EntityListProps {
  entities: EntityNode[];
  selectedEntity: EntityNode | null;
  onEntitySelect: (entity: EntityNode) => void;
  loading?: boolean;
}

const EntityList: React.FC<EntityListProps> = ({ entities, selectedEntity, onEntitySelect, loading = false }) => {
  const [createEntityModalVisible, setCreateEntityModalVisible] = useState(false);

  const handleOpenAddModal = () => {
    setCreateEntityModalVisible(true);
  };

  const successCallback = () => {
    // setCreateEntityModalVisible(false);
  };

  return (
    <div className={styles.entityList}>
      <div className={styles.header}>
        <h3>实体列表</h3>
        <span className={styles.count}>{entities.length} 个实体</span>
        <Button type="primary" onClick={handleOpenAddModal}>
          创建实体
        </Button>
      </div>
      <Spin loading={loading}>
        <List
          className={styles.list}
          dataSource={entities}
          render={(entity: EntityNode) => (
            <List.Item
              key={entity.id}
              className={`${styles.listItem} ${selectedEntity?.id === entity.id ? styles.selected : ''}`}
              onClick={() => onEntitySelect(entity)}
            >
              <div className={styles.itemContent}>
                <Avatar className={styles.avatar}>
                  <IconList />
                </Avatar>
                <div className={styles.itemInfo}>
                  <div className={styles.entityName}>{entity.title}</div>
                  <div className={styles.entityId}>{entity.id}</div>
                  <div className={styles.fieldCount}>{entity.fields?.length || 0} 个字段</div>
                </div>
              </div>
            </List.Item>
          )}
        />
      </Spin>

      <CreateEntityPage
        visible={createEntityModalVisible}
        setVisible={setCreateEntityModalVisible}
        successCallback={successCallback}
      />
    </div>
  );
};

export default EntityList;

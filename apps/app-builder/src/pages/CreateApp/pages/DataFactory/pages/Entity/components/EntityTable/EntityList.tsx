import { Button, List, Popover, Space, Spin } from '@arco-design/web-react';
import { IconMoreVertical, IconPlus } from '@arco-design/web-react/icon';
import React from 'react';
import styles from './EntityList.module.less';
import type { EntityListItem } from '@/pages/CreateApp/pages/DataFactory/utils/interface';

interface EntityListProps {
  entities: EntityListItem[];
  selectedEntity: EntityListItem | null;
  onEntitySelect: (entity: EntityListItem) => void;
  loading?: boolean;
  handleDelete: (entity: EntityListItem) => void;
  handleOpenAddModal: () => void;
}

const EntityList: React.FC<EntityListProps> = ({
  entities,
  selectedEntity,
  onEntitySelect,
  loading = false,
  handleDelete,
  handleOpenAddModal
}) => {
  const handleEdit = (entity: EntityListItem) => {
    console.log(entity);
  };

  return (
    <div className={styles.entityList}>
      <div className={styles.header}>
        <h3>业务实体</h3>
        <Button type="primary" onClick={handleOpenAddModal}>
          <IconPlus />
        </Button>
      </div>
      <Spin loading={loading}>
        <List
          className={styles.list}
          dataSource={entities}
          render={(entity) => (
            <List.Item
              key={entity.id}
              className={`${styles.listItem} ${selectedEntity?.id === entity.id ? styles.selected : ''}`}
              onClick={() => onEntitySelect(entity)}
            >
              <div className={styles.itemContent}>
                <div className={styles.entityName}>{entity.displayName}</div>
                <Popover
                  trigger="hover"
                  position="right"
                  className={styles['more-icon-popover']}
                  content={
                    <Space direction="vertical">
                      <Button type="text" onClick={() => handleEdit(entity)}>
                        编辑
                      </Button>
                      <Button type="text" onClick={() => handleDelete(entity)}>
                        删除
                      </Button>
                    </Space>
                  }
                >
                  <IconMoreVertical />
                </Popover>
              </div>
            </List.Item>
          )}
        />
      </Spin>
    </div>
  );
};

export default EntityList;

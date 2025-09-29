import { Button, Input, Modal } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconPlusCircle } from '@arco-design/web-react/icon';

import { listApplicationTag } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

interface TagModalProps {
  visible: boolean;
  onOk: Function;
  onCancel: Function;
}

const TagModal: React.FC<TagModalProps> = ({ visible, onOk, onCancel }) => {
  // TODO(mickey): 目前Mock 从后端获取标签列表
  const [data, setData] = useState<any[]>([
    {
      id: 1,
      tagName: '测试',
      appCount: 10
    },
    {
      id: 2,
      tagName: '测试2',
      appCount: 20
    }
  ]);

  const [editingTagId, setEditingTagId] = useState<number>();
  const [editingTagName, setEditingTagName] = useState<string>();

  useEffect(() => {
    if (visible) {
      handleGetTagList();
    }
  }, [visible]);

  const handleGetTagList = async () => {
    const res = await listApplicationTag({ tagName: '' });
    console.log('res: ', res);
  };

  return (
    <Modal
      visible={visible}
      onOk={() => {
        onOk();
      }}
      onCancel={() => {
        onCancel();
      }}
      title="管理标签"
      style={{
        width: '450px',
        padding: '10px'
      }}
    >
      <div className={styles.tagModal}>
        <div className={styles.header}>
          <div className={styles.headerTagName}>标签名称</div>
          <div className={styles.headerAppCount}>应用数</div>
          <div className={styles.headerOperation}></div>
        </div>

        <div className={styles.tagList}>
          {data.map((tagItem) => (
            <div className={styles.item} key={tagItem.tagName}>
              <div className={styles.tagName}>
                {editingTagId === tagItem.id ? (
                  <Input
                    value={editingTagName}
                    onBlur={() => {
                      setData(
                        data.map((item) => (item.id === editingTagId ? { ...item, tagName: editingTagName } : item))
                      );
                      setEditingTagId(undefined);
                      setEditingTagName(undefined);
                    }}
                    onPressEnter={() => {
                      setEditingTagId(undefined);
                      setEditingTagName(undefined);
                    }}
                    onChange={(e) => {
                      console.log(e);
                      setEditingTagName(e);
                    }}
                  />
                ) : (
                  <div className={styles.tagNameText}>{tagItem.tagName}</div>
                )}

                <div className={styles.tagNameIcon}>
                  <IconEdit
                    onClick={() => {
                      if (editingTagId === tagItem.id) {
                        setEditingTagId(undefined);
                        setEditingTagName(undefined);
                      } else {
                        setEditingTagId(tagItem.id);
                        setEditingTagName(tagItem.tagName);
                      }
                    }}
                  />
                </div>
              </div>
              <div className={styles.appCount}>{tagItem.appCount}</div>
              <div className={styles.operation}>
                <Button
                  type="text"
                  style={{ color: '#c9cdd4' }}
                  icon={<IconDelete />}
                  onClick={() => {
                    setData(data.filter((item) => item.id !== tagItem.id));
                  }}
                />
              </div>
            </div>
          ))}
        </div>

        <div>
          <Button
            type="text"
            icon={<IconPlusCircle />}
            onClick={() => {
              setData([...data, { id: data.length + 1, tagName: '新标签', appCount: 0 }]);
              // 新增标签后让 tagList 滚动到底部
              setTimeout(() => {
                const tagList = document.querySelector(`.${styles.tagList}`);
                if (tagList) {
                  tagList.scrollTop = tagList.scrollHeight;
                }
              }, 0);
            }}
          >
            添加标签
          </Button>
        </div>
      </div>
    </Modal>
  );
};

export default TagModal;

import { Button, Input, Modal, Form, Grid, Message, Popconfirm } from '@arco-design/web-react';
import { IconDelete, IconPlusCircle } from '@arco-design/web-react/icon';
import { getApplicationTagGroupCount, updateApplicationTag, type ListTagReq } from '@onebase/app';
import React, { useEffect } from 'react';
import styles from './index.module.less';

interface TagModalProps {
  visible: boolean;
  onOk: Function;
  onCancel: Function;
}

const TagModal: React.FC<TagModalProps> = ({ visible, onOk, onCancel }) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (visible) {
      handleGetTagList();
    }
  }, [visible]);

  const handleConfirm = async () => {
    await form.validate();
    const tagList = form.getFieldValue('tagList');

    const res = await updateApplicationTag(tagList);
    if (res) {
      Message.success('更新成功');
      onOk();
    }
  };

  const handleGetTagList = async () => {
    const res = await getApplicationTagGroupCount();
    const tagList = res || [];
    form.setFieldValue('tagList', tagList);
  };

  return (
    <Modal
      visible={visible}
      onOk={handleConfirm}
      onCancel={() => {
        onCancel();
      }}
      title="管理标签"
      unmountOnExit
      className={styles.tagModal}
    >
      <div className={styles.tagForm}>
        <Form form={form} layout="vertical">
          <Grid.Row gutter={8} className={styles.header}>
            <Grid.Col span={11}>
              <div className={styles.headerTagName}>标签名称</div>
            </Grid.Col>
            <Grid.Col span={11}>
              <div className={styles.headerAppCount}>应用数</div>
            </Grid.Col>
            <Grid.Col span={2}></Grid.Col>
          </Grid.Row>
          <Form.List field="tagList">
            {(fields, { add, remove }) => {
              return (
                <>
                  {fields.map((item, index) => {
                    return (
                      <Grid.Row key={item.key} gutter={8}>
                        <Grid.Col span={11}>
                          <Form.Item
                            field={item.field + '.tagName'}
                            rules={[
                              { required: true, message: '请输入标签名称' },
                              {
                                validator: (value, cb) => {
                                  const tagList = form.getFieldValue('tagList');
                                  const repeatTags = tagList.filter((ele: ListTagReq) => ele.tagName === value);
                                  if (repeatTags.length > 1) {
                                    return cb('标签名称不能重复');
                                  }
                                  return cb();
                                }
                              }
                            ]}
                          >
                            <Input placeholder="请输入" />
                          </Form.Item>
                        </Grid.Col>
                        <Grid.Col span={11}>
                          <Form.Item field={item.field + '.tagCount'}>
                            <Input readOnly />
                          </Form.Item>
                        </Grid.Col>
                        <Grid.Col span={2}>
                          <Popconfirm
                            focusLock
                            title="确认删除"
                            content="该标签正在被应用使用，确定要删除吗?"
                            onOk={() => {
                              remove(index)
                            }}
                          >
                            <Button type="text" status="danger" icon={<IconDelete />} />
                          </Popconfirm>
                        </Grid.Col>
                      </Grid.Row>
                    );
                  })}
                  <div>
                    <Button
                      type="text"
                      icon={<IconPlusCircle />}
                      onClick={() => {
                        add({
                          tagName: undefined
                        });
                      }}
                    >
                      添加标签
                    </Button>
                  </div>
                </>
              );
            }}
          </Form.List>
        </Form>
      </div>
    </Modal>
  );
};

export default TagModal;

import { Button, Form, Grid, Radio, Select, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus } from '@arco-design/web-react/icon';
import { SortType, type ConditionField, type SortData } from '@onebase/app';
import { nanoid } from 'nanoid';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';

export interface SortByEditorProps {
  fields: ConditionField[];
  data: SortData[];
  form: FormInstance;
}

const SortByEditor: React.FC<SortByEditorProps> = ({ data, fields, form }) => {
  const [sortList, setSortList] = useState<SortData[]>(data || []);

  // 排序改变
  const handleSort = (newSortList: SortData[]) => {
    setSortList(newSortList || []);
    form.setFieldValue('sortBy', newSortList || []);
  };

  useEffect(() => {
    setSortList(data || []);
    form.setFieldValue('sortBy', data || []);
  }, []);

  return (
    <ReactSortable list={sortList} setList={handleSort} animation={200} handle=".sortby-item-handle">
      <Form.Item noStyle validateTrigger={['onChange']}>
        <Form.List field="sortBy">
          {(field, { add, remove }) => {
            return (
              <>
                {field.map((item: any, index: number) => {
                  return (
                    <Grid.Row key={item.key} gutter={8} className={styles.sortRow}>
                      <Grid.Col span={1} className={styles.sortCol}>
                        <IconDragDotVertical
                          // 支持拖拽的图标
                          className="sortby-item-handle"
                          style={{
                            cursor: 'move',
                            color: '#555'
                          }}
                        />
                      </Grid.Col>
                      <Grid.Col span={13} className={styles.sortCol}>
                        <Form.Item
                          field={item.field + '.sortField'}
                          noStyle
                          rules={[{ required: true, message: '请选择排序字段' }]}
                        >
                          <Select
                            allowClear
                            onChange={() => {
                              setSortList(form.getFieldValue('sortBy'));
                            }}
                            options={fields.map((field) => {
                              const disabled = sortList?.some((f) => f?.sortField === field.value);
                              return { ...field, disabled };
                            })}
                          ></Select>
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={8} className={styles.sortCol}>
                        <Form.Item
                          field={item.field + '.sortType'}
                          noStyle
                          rules={[{ required: true, message: '请选择排序规则' }]}
                        >
                          <Radio.Group
                            onChange={() => {
                              setSortList(form.getFieldValue('sortBy'));
                            }}
                          >
                            <Radio value={SortType.ASC}>升序</Radio>
                            <Radio value={SortType.DESC}>降序</Radio>
                          </Radio.Group>
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={2} className={styles.sortCol}>
                        <Button
                          type="text"
                          onClick={() => {
                            remove(index);
                            setSortList(form.getFieldValue('sortBy'));
                          }}
                          icon={<IconDelete style={{ color: '#4E5969' }} />}
                        />
                      </Grid.Col>
                    </Grid.Row>
                  );
                })}

                <Button
                  type="dashed"
                  icon={<IconPlus />}
                  disabled={(sortList || [])?.length >= fields?.length}
                  onClick={() => {
                    const temp = {
                      id: nanoid(),
                      sortField: '',
                      sortType: ''
                    };
                    add(temp);
                    setSortList((prev) => [...prev, temp]);
                  }}
                >
                  添加字段
                </Button>
              </>
            );
          }}
        </Form.List>
      </Form.Item>
    </ReactSortable>
  );
};

export default SortByEditor;

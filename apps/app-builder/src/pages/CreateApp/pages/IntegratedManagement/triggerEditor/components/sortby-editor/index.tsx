import { Button, Form, Grid, Select, Radio, type FormInstance } from '@arco-design/web-react';
import { IconDelete, IconPlus, IconDragDotVertical } from '@arco-design/web-react/icon';
import { SortType, type Sort, type ConfitionField } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';
import { nanoid } from 'nanoid';

export interface ConditionEditorProps {
  fields: ConfitionField[];
  data?: Sort[];
  onChange?: (value: Sort[]) => void;
  form: FormInstance;
  clearSortByNum?: number;
}

const SortByEditor: React.FC<ConditionEditorProps> = ({ data, onChange, fields, form, clearSortByNum }) => {
  const [sortList, setSortList] = useState<Sort[]>([]);

  // 排序改变
  const handleSort = (newSortList: Sort[]) => {
    console.log('handleSort', newSortList);
    setSortList(newSortList || []);
    form.setFieldValue('sortList', newSortList || []);
  };

  useEffect(() => {
    if (onChange) {
      onChange(sortList);
    }
  }, [sortList]);
  useEffect(()=>{
    setSortList([]);
    form.clearFields(['sortList'])
  },[clearSortByNum])

  return (
    <ReactSortable list={sortList} setList={handleSort} animation={200} handle=".sortby-item-handle">
      <Form.Item noStyle validateTrigger={['onChange']}>
        <Form.List field="sortList" initialValue={data}>
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
                        <Form.Item field={item.field + '.sortField'} noStyle>
                          <Select
                            onChange={() => {
                              setSortList(form.getFieldValue('sortList'));
                            }}
                            options={fields}
                          ></Select>
                        </Form.Item>
                      </Grid.Col>
                      <Grid.Col span={8} className={styles.sortCol}>
                        <Form.Item field={item.field + '.sortType'} noStyle>
                          <Radio.Group
                            onChange={() => {
                              setSortList(form.getFieldValue('sortList'));
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
                            setSortList(form.getFieldValue('sortList'));
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

import { Button, Radio, Input, Select, Grid } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import  { SortType, type Sort, type ConfitionField } from '@onebase/app';
import { nanoid } from 'nanoid';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';
import { ReactSortable } from 'react-sortablejs';

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface ConditionEditorProps {
  fields: ConfitionField[];
  data?: Sort[];
  onChange?: (value: Sort[]) => void;
}

/**
 * 条件编辑器组件初始化
 */
const SortByEditor: React.FC<ConditionEditorProps> = ({ data, onChange, fields }) => {
  // 数据
  const [sortList, setSortList] = useState<Sort[]>([]);

  useEffect(() => {
    if (data) {
      setSortList(data);
    }
  }, []);
  useEffect(() => {
    if(onChange){
      onChange(sortList);
    }
  }, [sortList]);

  const addSort = () => {
    const pid = nanoid();
    const newSortList = [...sortList];
    newSortList.push({
      id: pid,
      sortField: '',
      sortType: ''
    });
    setSortList(newSortList);
  };
  const deleteSort = (id: string) => {
    const newSortList = [...sortList];
    const index = newSortList.findIndex((ele) => ele.id === id);
    newSortList.splice(index, 1);
    setSortList(newSortList);
  };

  // 内容改变
  const handleOnChange = (item: Sort, field: string, value: any) => {
    const newSortList = sortList.map((ele)=>{
      if (ele.id === item.id) {
        return { ...ele, [field]: value };
      }
      return ele;
    });
    setSortList(newSortList);
  };
  // 排序改变
  const handleSort = (newSortList: Sort[]) => {
    console.log('handleSort', newSortList);
    setSortList(newSortList);
  };

  return (
    <div>
      {sortList && (
        <ReactSortable list={sortList} setList={handleSort} animation={200} handle=".sortby-item-handle">
          {sortList.map((item) => {
            return (
              <Grid.Row key={item.id} align="center" gutter={8} className={styles.sortRow}>
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
                <Grid.Col span={13}>
                  <Select onChange={(e) => handleOnChange(item, 'sortField', e)} options={fields}></Select>
                </Grid.Col>
                <Grid.Col span={8} className={styles.sortCol}>
                  <Radio.Group onChange={(e) => handleOnChange(item, 'sortType', e)}>
                    <Radio value={SortType.ASC}>升序</Radio>
                    <Radio value={SortType.DESC}>降序</Radio>
                  </Radio.Group>
                </Grid.Col>
                <Grid.Col span={2} className={styles.sortCol}>
                  <Button
                    type="text"
                    onClick={() => {
                      deleteSort(item.id);
                    }}
                    icon={<IconDelete style={{ color: '#4E5969' }} />}
                  />
                </Grid.Col>
              </Grid.Row>
            );
          })}
        </ReactSortable>
      )}
      <Button type="text" size="small" onClick={addSort}>
        + 添加排序字段
      </Button>
    </div>
  );
};

export default SortByEditor;

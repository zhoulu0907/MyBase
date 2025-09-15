import { Button, Form, Input, Space } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import React, { useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicOptionsConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicOptionsConfig: React.FC<DynamicOptionsConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const selectKey = 'defaultValue';
  const [selectOptionsConfig, setSelectOptionsConfig] = useState<any[]>(configs[selectKey] || []);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'自定义配置'} className={styles.formItem}>
        <Form.List initialValue={configs[selectKey]} field={`${id}-${selectKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[selectKey]}
                setList={() => {}}
                group={{
                  name: 'table-col-item'
                }}
                swap
                sort={true}
                handle=".table-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  console.log(e);
                  const newList = [...selectOptionsConfig];
                  // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
                  const { oldIndex, newIndex } = e;
                  console.log(oldIndex, newIndex);
                  if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
                    // 复制一份新数组
                    const movedList = [...newList];
                    // 取出被移动的元素
                    const [movedItem] = movedList.splice(oldIndex, 1);
                    // 插入到新位置
                    movedList.splice(newIndex, 0, movedItem);
                    // 更新属性
                    handlePropsChange(selectKey, movedList);
                  }
                }}
              >
                {configs[selectKey].map((_col: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
                    <Space>
                      <IconDragDotVertical
                        // 支持拖拽的图标，别误删了：）
                        className="table-col-item-handle"
                        style={{
                          cursor: 'move',
                          color: '#555'
                        }}
                      />
                      {/* <Radio
                        checked={configs[radioKey][idx].chosen || false}
                        onChange={(e) => {
                          let newList = [...radioConfig];
                          newList = newList.map((item) => ({ ...item, chosen: false }));
                          newList[idx] = {
                            ...newList[idx],
                            chosen: true
                          };
                          setRadioConfig(newList);
                          handlePropsChange(radioKey, newList);
                        }}
                      /> */}
                      <Input
                        size="small"
                        value={configs[selectKey][idx].label}
                        onChange={(e) => {
                          const newList = [...selectOptionsConfig];
                          newList[idx] = {
                            ...newList[idx],
                            label: e
                          };
                          setSelectOptionsConfig(newList);
                          handlePropsChange(selectKey, newList);
                        }}
                        className={styles.tableColumnItemInput}
                        // TODO(mickey): 国际化
                        placeholder={'新选项'}
                      />
                      <Button
                        icon={<IconDelete />}
                        shape="circle"
                        size="mini"
                        status="danger"
                        className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...selectOptionsConfig];
                          newList.splice(idx, 1);
                          setSelectOptionsConfig(newList);
                          handlePropsChange(selectKey, newList);
                          remove(idx);
                        }}
                      />
                    </Space>
                  </div>
                ))}
              </ReactSortable>

              <Button
                type="outline"
                onClick={() => {
                  const newLabel = '新选项';
                  const newValue = _fields[_fields.length - 1].field;
                  const newList = [
                    ...selectOptionsConfig,
                    { label: item.displayName || newLabel, value: item.fieldName || newValue }
                  ];
                  console.log('newList: ', newList, _fields);
                  add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                  setSelectOptionsConfig(newList);
                  handlePropsChange(selectKey, newList);
                }}
              >
                添加一项
              </Button>
            </div>
          )}
        </Form.List>
      </FormItem>
    </>
  );
};

export default DynamicOptionsConfig;

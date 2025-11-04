import { Button, Form, Input, Radio, Space } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
// import { type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import React, { useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicRadioConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicRadioConfig: React.FC<DynamicRadioConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const radioKey = 'defaultOptions';
  const [radioConfig, setRadioConfig] = useState<any[]>(configs[radioKey] || []);

  console.log({
    radioConfig,
    configs,
    item
  });

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'自定义配置'} className={styles.formItem}>
        <Form.List initialValue={configs[radioKey]} field={`${id}-${radioKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[radioKey]}
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
                  const newList = [...radioConfig];
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
                    handlePropsChange(radioKey, movedList);
                  }
                }}
              >
                {configs[radioKey].map((_col: any, idx: number) => (
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
                      <Radio
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
                      />
                      <Input
                        size="small"
                        value={configs[radioKey][idx].label}
                        onChange={(e) => {
                          const newList = [...radioConfig];
                          newList[idx] = {
                            ...newList[idx],
                            label: e
                          };
                          setRadioConfig(newList);
                          handlePropsChange(radioKey, newList);
                        }}
                        className={styles.tableColumnItemInput}
                        placeholder={'新选项'}
                      />
                      <Button
                        icon={<IconDelete />}
                        shape="circle"
                        size="mini"
                        status="danger"
                        disabled={radioConfig.length <= 2}
                        className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...radioConfig];
                          newList.splice(idx, 1);
                          setRadioConfig(newList);
                          handlePropsChange(radioKey, newList);
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
                  const newLabel = `新选项_${Array.from({ length: 6 }, () => String.fromCharCode(97 + Math.floor(Math.random() * 26))).join('')}`;
                  const newValue = _fields?.[_fields.length - 1]?.field || `${configs.id}-${radioKey}[0]`;
                  const newList = [
                    ...radioConfig,
                    { label: item.displayName || newLabel, value: item.fieldName || newValue }
                  ];
                  console.log('newList: ', newList, _fields);
                  add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                  setRadioConfig(newList);
                  handlePropsChange(radioKey, newList);
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

export default DynamicRadioConfig;

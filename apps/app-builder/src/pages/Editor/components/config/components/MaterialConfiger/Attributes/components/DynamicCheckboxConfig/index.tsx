import { Button, Checkbox, Form, Input, Message, Space } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
// import { type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
const FormItem = Form.Item;

export interface DynamicCheckboxConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicCheckboxConfig: React.FC<DynamicCheckboxConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const checkboxKey = 'defaultOptions';
  const [checkboxConfig, setCheckboxConfig] = useState<any[]>(configs[checkboxKey] || []);

  console.debug({
    checkboxConfig,
    configs
  });

  useEffect(() => {
    const allCheckedOrNot = checkboxConfig.map((op) => ({ ...op, chosen: configs?.['allChecked'] }));
    handlePropsChange(checkboxKey, allCheckedOrNot);
  }, [configs?.['allChecked']]);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'自定义配置'} className={styles.formItem}>
        <Form.List initialValue={configs[checkboxKey]} field={`${id}-${checkboxKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[checkboxKey]}
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
                  const newList = [...checkboxConfig];
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
                    handlePropsChange(checkboxKey, movedList);
                  }
                }}
              >
                {configs[checkboxKey].map((_col: any, idx: number) => (
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
                      <Checkbox
                        checked={configs['allChecked'] ? true : configs[checkboxKey][idx].chosen || false}
                        onChange={(e) => {
                          let newList = [...checkboxConfig];
                          newList[idx] = {
                            ...newList[idx],
                            chosen: e
                          };
                          if (newList.filter((op) => op.chosen).length > configs['verify']['maxChecked']) {
                            Message.warning('选中数量超过最大可选数量');
                            return false;
                          }
                          setCheckboxConfig(newList);
                          handlePropsChange(checkboxKey, newList);
                        }}
                      />
                      <Input
                        size="small"
                        value={configs[checkboxKey][idx].label}
                        onChange={(e) => {
                          const newList = [...checkboxConfig];
                          newList[idx] = {
                            ...newList[idx],
                            label: e
                          };
                          setCheckboxConfig(newList);
                          handlePropsChange(checkboxKey, newList);
                        }}
                        className={styles.tableColumnItemInput}
                        placeholder={'新选项'}
                      />
                      <Button
                        icon={<IconDelete />}
                        shape="circle"
                        size="mini"
                        status="danger"
                        disabled={configs[checkboxKey].length <= 2}
                        className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...checkboxConfig];
                          newList.splice(idx, 1);
                          setCheckboxConfig(newList);
                          handlePropsChange(checkboxKey, newList);
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
                  const newValue = _fields?.[_fields.length - 1]?.field || `${configs.id}-${checkboxKey}[0]`;
                  const newList = [
                    ...checkboxConfig,
                    { label: item.displayName || newLabel, value: item.fieldName || newValue }
                  ];
                  console.log('newList: ', newList, _fields);
                  add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                  setCheckboxConfig(newList);
                  handlePropsChange(checkboxKey, newList);
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

export default DynamicCheckboxConfig;

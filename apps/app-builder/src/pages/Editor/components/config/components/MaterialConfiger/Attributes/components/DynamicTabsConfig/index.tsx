import { Button, Form, Input, Space } from '@arco-design/web-react';
import { IconCopy, IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { nanoid } from 'nanoid';
import React, { useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
const FormItem = Form.Item;

export interface DynamicTabsConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicTabsConfig: React.FC<DynamicTabsConfigProps> = ({
  handlePropsChange,
  handleMultiPropsChange,
  item,
  configs,
  id
}) => {
  const componentKey = 'defaultValue';

  const [tabsConfig, setTabsConfig] = useState<any[]>(configs[componentKey] || []);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={item.name} className={styles.formItem}>
        <Form.List initialValue={tabsConfig} field={`${id}-${componentKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={tabsConfig}
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
                  const newList = [...tabsConfig];
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
                    handlePropsChange(componentKey, movedList);
                  }
                }}
              >
                {tabsConfig?.map((comp: any, idx: number) => (
                  <div key={comp.key} className={styles.tableColumnItem}>
                    <Space style={{ width: '100%' }}>
                      <Input
                        size="small"
                        value={comp.title}
                        onChange={(e) => {
                          const newList = [...tabsConfig];
                          newList[idx] = {
                            ...newList[idx],
                            title: e
                          };
                          setTabsConfig(newList);
                          handlePropsChange(componentKey, newList);
                        }}
                        className={styles.tableColumnItemInput}
                        placeholder={'页签组件标题名称'}
                      />
                      <IconDragDotVertical
                        // 支持拖拽的图标，别误删了：）
                        className="table-col-item-handle"
                        style={{
                          cursor: 'move',
                          color: '#555'
                        }}
                      />
                      <Button
                        icon={<IconCopy />}
                        shape="circle"
                        size="mini"
                        // className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...tabsConfig];
                          newList.splice(idx + 1, 0, { ...comp, key: idx + '_拷贝', title: comp.title + '_拷贝' });
                          setTabsConfig(newList);
                          handlePropsChange(componentKey, newList);
                          add(idx);
                        }}
                      />
                      <Button
                        icon={<IconDelete />}
                        shape="circle"
                        size="mini"
                        status="danger"
                        // className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...tabsConfig];
                          newList.splice(idx, 1);
                          setTabsConfig(newList);
                          handlePropsChange(componentKey, newList);
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
                  const newTitle = '新标签';
                  const newKey = nanoid();
                  const newList = [...tabsConfig, { title: newTitle, key: newKey }];
                  add({ title: newTitle, key: newKey });
                  setTabsConfig(newList);
                  handlePropsChange(componentKey, newList);
                  handleMultiPropsChange([
                    { key: componentKey, value: newList },
                    { key: 'colCount', value: newList.length }
                  ]);
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

export default DynamicTabsConfig;

registerConfigRenderer(CONFIG_TYPES.TABS, ({ id, handlePropsChange, handleMultiPropsChange, item, configs }) => (
  <DynamicTabsConfig id={id} handlePropsChange={handlePropsChange} handleMultiPropsChange={handleMultiPropsChange} item={item} configs={configs} />
));

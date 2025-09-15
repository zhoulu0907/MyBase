// import { useAppEntityStore } from '@/store/store_entity';
import { Button, Form, Input, Checkbox, Space, Message } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical, IconPlus } from '@arco-design/web-react/icon';
// import { type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { usePageEditorSignal, getComponentSchema } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { nanoid } from 'nanoid';
const FormItem = Form.Item;

export interface DynamicCheckboxConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicCheckboxConfig: React.FC<DynamicCheckboxConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const componentKey = 'childrenTable';

  const {
    pageComponentSchemas,
    layoutSubComponents,
    setLayoutSubComponents
  } = usePageEditorSignal();

  const [allComponents, setAllComponents] = useState<any[]>(layoutSubComponents[id]?.[0] || []);


  useEffect(() => {
    if (id && layoutSubComponents[id])
      setAllComponents(layoutSubComponents[id]?.[0]);
  }, [id, layoutSubComponents, pageComponentSchemas]);

  console.error({
    pageComponentSchemas,
    allComponents,
    configs,
  }, layoutSubComponents[id]);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'子字段配置'} className={styles.formItem}>
        <Form.List initialValue={allComponents} field={`${id}-${componentKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={allComponents}
                setList={() => { }}
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
                  const newList = [...allComponents];
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
                    setLayoutSubComponents(id, [movedList]);
                  }
                }}
              >
                {allComponents?.map((comp: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
                    <Space style={{ width: '100%' }}>
                      <IconDragDotVertical
                        // 支持拖拽的图标，别误删了：）
                        className="table-col-item-handle"
                        style={{
                          cursor: 'move',
                          color: '#555'
                        }}
                      />

                      <Input
                        size="small"
                        value={comp.displayName}
                        onChange={(e) => {
                          const newList = [...allComponents];
                          newList[idx] = {
                            ...newList[idx],
                            displayName: e
                          };
                          setAllComponents(newList);
                          setLayoutSubComponents(id, [newList]);
                        }}
                        className={styles.tableColumnItemInput}
                        // TODO(mickey): 国际化
                        placeholder={'子组件表头名称'}
                      />
                      <Button
                        icon={<IconPlus />}
                        shape="circle"
                        size="mini"
                        className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...allComponents];
                          newList.splice(idx, 0, { ...comp, id: `${comp.type}${nanoid()}` });
                          setAllComponents(newList);
                          setLayoutSubComponents(id, [newList]);
                          add(idx);
                        }}
                      />
                      <Button
                        icon={<IconDelete />}
                        shape="circle"
                        size="mini"
                        status="danger"
                        className={styles.tableColumnItemButton}
                        onClick={() => {
                          const newList = [...allComponents];
                          newList.splice(idx, 1);
                          setAllComponents(newList);
                          setLayoutSubComponents(id, [newList]);
                          remove(idx);
                        }}
                      />
                    </Space>
                  </div>
                ))}
              </ReactSortable>

              <Button
                type="outline"
                disabled
                onClick={() => {
                  const newLabel = '新选项';
                  const newValue = _fields[_fields.length - 1].field;
                  const newList = [
                    ...allComponents,
                    { label: item.displayName || newLabel, value: item.fieldName || newValue }
                  ];
                  console.log('newList: ', newList, _fields);
                  add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                  setAllComponents(newList);
                  // handlePropsChange(componentKey, newList);
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

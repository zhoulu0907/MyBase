import { Button, Checkbox, Form, Input, InputNumber, Message, Select } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { getEntityListByApp, type MetadataEntityPair } from '@onebase/app';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicTableConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
}

/**
 * 动态下拉选择组件
 * @param props 组件属性
 */
const DynamicTableConfig: React.FC<DynamicTableConfigProps> = ({ handlePropsChange, item, configs }) => {
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const columnsKey = 'columns';
  const searchItemsKey = 'searchItems';

  const columnsConfig = configs[columnsKey] || [];
  const searchItemsConfig = configs[searchItemsKey] || [];

  useEffect(() => {
    console.log(item);
    getEntityList();
  }, []);

  const getEntityList = async () => {
    const res = await getEntityListByApp('1');
    console.log('res: ', res);

    setEntityList(res);
  };

  return (
    <>
      <FormItem layout="horizontal" labelAlign="left" label={item.name} className={styles.formItem}>
        <Select
          placeholder={`请选择${item.name}`}
          value={configs[item.key]}
          onChange={(value) => {
            handlePropsChange(item.key, value);
          }}
        >
          {entityList.map((item) => (
            <Select.Option key={item.entityId} value={item.entityId}>
              {item.entityName}
            </Select.Option>
          ))}
        </Select>
      </FormItem>

      {/* 表头配置 */}
      <FormItem layout="vertical" labelAlign="left" label={'表头配置'} className={styles.formItem}>
        <Form.List initialValue={configs[columnsKey] || []} field={columnsKey}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[columnsKey]}
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
                onSort={(e) => {
                  console.log(e);
                  const newList = [...columnsConfig];
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
                    handlePropsChange(columnsKey, movedList);
                  }
                }}
              >
                {configs[columnsKey].map((_col: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
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
                      value={configs[columnsKey][idx].title}
                      onChange={(e) => {
                        const newList = [...columnsConfig];
                        newList[idx] = {
                          ...newList[idx],
                          title: e,
                          dataIndex: e
                        };
                        handlePropsChange(columnsKey, newList);
                      }}
                      className={styles.tableColumnItemInput}
                      // TODO(mickey): 国际化
                      placeholder={`请输入第${idx + 1}项`}
                    />
                    <InputNumber
                      size="small"
                      max={500}
                      min={50}
                      value={configs[columnsKey][idx].width}
                      className={styles.tableColumnItemInput}
                      onChange={(e) => {
                        const newList = [...columnsConfig];
                        newList[idx] = {
                          ...newList[idx],
                          width: e
                        };
                        handlePropsChange(columnsKey, newList);
                      }}
                      // TODO(mickey): 国际化
                      placeholder="宽度"
                    />
                    <Checkbox
                      checked={configs[columnsKey][idx].fixed || false}
                      onChange={(e) => {
                        const newList = [...columnsConfig];
                        if (newList[idx].width === undefined) {
                          // TODO(mickey): 国际化
                          Message.error('请先设置宽度');
                          return;
                        }
                        newList[idx] = {
                          ...newList[idx],
                          fixed: e ? 'left' : false
                        };
                        handlePropsChange(columnsKey, newList);
                      }}
                    >
                      固定
                    </Checkbox>
                    <Button
                      icon={<IconDelete />}
                      shape="circle"
                      size="mini"
                      status="danger"
                      className={styles.tableColumnItemButton}
                      onClick={() => {
                        const newList = [...columnsConfig];
                        newList.splice(idx, 1);
                        handlePropsChange(item.key, newList);
                        remove(idx);
                      }}
                    ></Button>
                  </div>
                ))}
              </ReactSortable>
              <Button
                type="outline"
                onClick={() => {
                  const newList = [...columnsConfig, { title: '', dataIndex: '' }];
                  add({ title: '', dataIndex: '' });
                  handlePropsChange(columnsKey, newList);
                }}
              >
                新增列
              </Button>
            </div>
          )}
        </Form.List>
      </FormItem>

      {/* 搜索项 */}
      <FormItem layout="vertical" labelAlign="left" label={'搜索项'} className={styles.formItem}>
        <Form.List initialValue={configs[searchItemsKey]} field={item.key}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={searchItemsConfig}
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
                onSort={(e) => {
                  console.log(e);
                  const newList = [...searchItemsConfig];
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
                    handlePropsChange(item.key, movedList);
                  }
                }}
              >
                {configs[searchItemsKey].map((_col: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
                    <IconDragDotVertical
                      // 支持拖拽的图标，别误删了：）
                      className="table-col-item-handle"
                      style={{
                        cursor: 'move',
                        color: '#555'
                      }}
                    />
                    <Select
                      size="small"
                      value={configs[searchItemsKey][idx].label}
                      onChange={(e, option: any) => {
                        const newList = [...configs[searchItemsKey]];
                        newList[idx] = {
                          ...newList[idx],
                          label: option.children,
                          value: e
                        };

                        handlePropsChange(searchItemsKey, newList);

                        console.log(e);
                        console.log(option.children);
                        console.log(configs[searchItemsKey]);
                      }}
                      className={styles.tableColumnItemInput}
                      placeholder={`请输入第${idx + 1}项`}
                      options={configs['columns']
                        .filter(
                          (col: any) =>
                            // 过滤掉已在 configs[item.key] 中被选中的 dataIndex
                            !searchItemsConfig.some((selected: any) => selected.value === col.dataIndex)
                        )
                        .map((item: any) => ({
                          label: item.title,
                          value: item.dataIndex
                        }))}
                    />
                    <Button
                      icon={<IconDelete />}
                      shape="circle"
                      size="mini"
                      status="danger"
                      className={styles.tableColumnItemButton}
                      onClick={() => {
                        const newList = [...searchItemsConfig];
                        newList.splice(idx, 1);
                        handlePropsChange(searchItemsKey, newList);
                        remove(idx);
                      }}
                    ></Button>
                  </div>
                ))}
              </ReactSortable>
              <Button
                type="outline"
                onClick={() => {
                  const newList = [...searchItemsConfig, { label: '', value: '' }];
                  add({ label: '', value: '' });
                  handlePropsChange(searchItemsKey, newList);
                }}
              >
                新增搜索项
              </Button>
            </div>
          )}
        </Form.List>
      </FormItem>
    </>
  );
};

export default DynamicTableConfig;

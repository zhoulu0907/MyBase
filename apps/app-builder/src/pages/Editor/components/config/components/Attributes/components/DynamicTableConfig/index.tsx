import { Button, Checkbox, Dropdown, Form, Input, InputNumber, Menu, Message, Select } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { getEntityFields, getEntityListByApp, type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
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
  const [entityId, setEntityId] = useState<string>('');
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);
  const columnsKey = 'columns';
  const searchItemsKey = 'searchItems';

  const [columnsConfig, setColumnsConfig] = useState<any[]>(configs[columnsKey] || []);
  const [searchItemsConfig, setSearchItemsConfig] = useState<any[]>(configs[searchItemsKey] || []);

  const [enableAddColumn, setEnableAddColumn] = useState<boolean>(false);
  const [enableAddSearchItem, setEnableAddSearchItem] = useState<boolean>(false);

  useEffect(() => {
    console.log(item);
    getEntityList();
  }, []);

  useEffect(() => {
    if (entityId) {
      getFieldList();
      setSearchItemsConfig([]);
      handlePropsChange(searchItemsKey, []);
    }
  }, [entityId]);

  useEffect(() => {
    setEnableAddColumn(
      fieldList
        .filter((item: MetadataEntityField) => !item.isSystemField)
        .filter((item: MetadataEntityField) => !columnsConfig.some((col: any) => col.dataIndex === item.fieldName))
        .length > 0
    );
  }, [fieldList, columnsConfig]);

  useEffect(() => {
    setEnableAddSearchItem(
      fieldList
        .filter((item: MetadataEntityField) => !item.isSystemField)
        .filter((item: MetadataEntityField) => !searchItemsConfig.some((col: any) => col.value === item.fieldName))
        .length > 0
    );
  }, [fieldList, searchItemsConfig]);

  const getEntityList = async () => {
    const res = await getEntityListByApp('1');
    console.log('res: ', res);

    setEntityList(res);
  };

  const getFieldList = async () => {
    const res = await getEntityFields({ entityId });
    console.log('res: ', res);
    setFieldList(res);

    const newColumns = res
      .filter((item: MetadataEntityField) => !item.isSystemField)
      .map((item: MetadataEntityField) => ({ title: item.displayName, dataIndex: item.fieldName }));

    console.log('newColumns: ', newColumns);
    setColumnsConfig(newColumns);
    handlePropsChange(columnsKey, newColumns);
  };

  return (
    <>
      <FormItem layout="horizontal" labelAlign="left" label={item.name} className={styles.formItem}>
        <Select
          placeholder={`请选择${item.name}`}
          value={configs[item.key]}
          onChange={(value) => {
            setEntityId(value);
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
        <Form.List initialValue={configs[columnsKey]} field={columnsKey}>
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
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
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
                          title: e
                          //   dataIndex: e
                        };
                        setColumnsConfig(newList);
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
                        setColumnsConfig(newList);
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
                        setColumnsConfig(newList);
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
                        setColumnsConfig(newList);
                        handlePropsChange(columnsKey, newList);
                        remove(idx);
                      }}
                    ></Button>
                  </div>
                ))}
              </ReactSortable>

              <Dropdown
                position={'tl'}
                trigger="click"
                droplist={
                  <Menu>
                    {fieldList
                      .filter((item: MetadataEntityField) => !item.isSystemField)
                      .filter(
                        (item: MetadataEntityField) =>
                          !columnsConfig.some((col: any) => col.dataIndex === item.fieldName)
                      )
                      .map((item: MetadataEntityField) => (
                        <Menu.Item
                          key={item.fieldName}
                          onClick={() => {
                            const newList = [...columnsConfig, { title: item.displayName, dataIndex: item.fieldName }];
                            setColumnsConfig(newList);
                            handlePropsChange(columnsKey, newList);
                          }}
                        >
                          {item.displayName}
                        </Menu.Item>
                      ))}
                  </Menu>
                }
              >
                <Button type={enableAddColumn ? 'outline' : 'secondary'} disabled={!enableAddColumn}>
                  新增列
                </Button>
              </Dropdown>
            </div>
          )}
        </Form.List>
      </FormItem>

      {/* 搜索项 */}
      <FormItem layout="vertical" labelAlign="left" label={'搜索项'} className={styles.formItem}>
        <Form.List initialValue={configs[searchItemsKey]} field={searchItemsKey}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={searchItemsConfig}
                setList={setSearchItemsConfig}
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
                    handlePropsChange(searchItemsKey, movedList);
                  }
                }}
              >
                {searchItemsConfig.map((_col: any, idx: number) => (
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
                      value={searchItemsConfig[idx].label}
                      onChange={(e, option: any) => {
                        const newList = [...searchItemsConfig];
                        newList[idx] = {
                          ...newList[idx],
                          label: option.children,
                          value: e
                        };

                        handlePropsChange(searchItemsKey, newList);

                        console.log(e);
                        console.log(option.children);
                        console.log(searchItemsConfig);
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

                        remove(idx);
                        setSearchItemsConfig(newList);
                        handlePropsChange(searchItemsKey, newList);
                      }}
                    ></Button>
                  </div>
                ))}
              </ReactSortable>

              <Dropdown
                position={'tl'}
                trigger="click"
                droplist={
                  <Menu>
                    {fieldList
                      .filter((item: MetadataEntityField) => !item.isSystemField)
                      .filter(
                        (item: MetadataEntityField) =>
                          !searchItemsConfig.some((col: any) => col.value === item.fieldName)
                      )
                      .map((item: MetadataEntityField) => (
                        <Menu.Item
                          key={item.fieldName}
                          onClick={() => {
                            const newList = [...searchItemsConfig, { label: item.displayName, value: item.fieldName }];
                            console.log('newList: ', newList);
                            add({ label: item.displayName, value: item.fieldName });
                            setSearchItemsConfig(newList);
                            handlePropsChange(searchItemsKey, newList);
                          }}
                        >
                          {item.displayName}
                        </Menu.Item>
                      ))}
                  </Menu>
                }
              >
                <Button type={enableAddSearchItem ? 'outline' : 'secondary'} disabled={!enableAddSearchItem}>
                  新增搜索项
                </Button>
              </Dropdown>
            </div>
          )}
        </Form.List>
      </FormItem>
    </>
  );
};

export default DynamicTableConfig;

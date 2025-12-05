import { Button, Checkbox, Dropdown, Form, Input, InputNumber, Menu, Message, Select } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { FilterEntityFields, getEntityFields, type MetadataEntityField, type MetadataEntityPair } from '@onebase/app';
import { CONFIG_TYPES, ENTITY_FIELD_TYPE, getPopupContainer, useAppEntityStore } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;
export interface DynamicTableConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

// 暂时不能在表格展示的数据类型
export const hiddenFieldTypes = [
  ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE,
  ENTITY_FIELD_TYPE.RELATION.VALUE,
  ENTITY_FIELD_TYPE.STRUCTURE.VALUE,
  ENTITY_FIELD_TYPE.ARRAY.VALUE,
  ENTITY_FIELD_TYPE.FILE.VALUE,
  ENTITY_FIELD_TYPE.IMAGE.VALUE,
  ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE,
  ENTITY_FIELD_TYPE.PASSWORD.VALUE,
  ENTITY_FIELD_TYPE.ENCRYPTED.VALUE,
  ENTITY_FIELD_TYPE.AGGREGATE.VALUE,
  ENTITY_FIELD_TYPE.MULTI_USER.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE
];

const DynamicTableConfig: React.FC<DynamicTableConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const { mainEntity, subEntities } = useAppEntityStore();

  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [entityId, setEntityId] = useState<string>('');
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);

  const columnsKey = 'columns';
  const searchItemsKey = 'searchItems';

  const [columnsConfig, setColumnsConfig] = useState<any[]>(configs[columnsKey] || []);
  const [searchItemsConfig, setSearchItemsConfig] = useState<any[]>(configs[searchItemsKey] || []);

  const [enableAddColumn, setEnableAddColumn] = useState<boolean>(false);
  const [enableAddSearchItem, setEnableAddSearchItem] = useState<boolean>(false);

  // 获取当前表格关联的实体id
  useEffect(() => {
    if (id != configs.id) {
      return;
    }

    if (configs[item.key]) {
      setEntityId(configs[item.key]);
    }
  }, []);

  // 如果实体id变化，重新获取字段列表
  useEffect(() => {
    if (entityId) {
      getFieldList();
    }
  }, [entityId]);

  // 获取实体列表
  useEffect(() => {
    const newEntityList = [];
    if (mainEntity) {
      newEntityList.push({
        entityId: mainEntity.entityId,
        entityName: mainEntity.entityName
      });
    }
    if (subEntities) {
      newEntityList.push(
        ...subEntities.entities.map((entity: any) => ({
          entityId: entity.entityId,
          entityName: entity.entityName
        }))
      );
    }

    setEntityList(newEntityList);
  }, [mainEntity, subEntities]);

  // 设置允许的列
  useEffect(() => {
    const res = fieldList.some(
      (item: MetadataEntityField) => !columnsConfig.some((col: any) => col.dataIndex == item.id)
    );

    setEnableAddColumn(res);
  }, [fieldList, columnsConfig]);

  // 设置允许的搜索项
  useEffect(() => {
    const res = fieldList.some(
      (item: MetadataEntityField) => !searchItemsConfig.some((col: any) => col.value == item.fieldName)
    );

    setEnableAddSearchItem(res);
  }, [fieldList, searchItemsConfig]);

  // 获取字段列表
  const getFieldList = async () => {
    const res = await getEntityFields({ entityId });
    console.log('fieldList res: ', res);
    res.forEach((item: MetadataEntityField) => {
      if (item.fieldType && hiddenFieldTypes.includes(item.fieldType)) {
        item.disabled = true;
      }
    });

    const newFieldList = res.filter((item: MetadataEntityField) => !FilterEntityFields.includes(item.fieldName));
    const newFieldListNotSystemField = res.filter(
      (item: MetadataEntityField) => item.isSystemField !== 1 && !item.disabled
    );

    setFieldList(newFieldList);

    if (configs.metaData === entityId) {
      return;
    }

    const newColumns = newFieldListNotSystemField.map((item: MetadataEntityField) => ({
      // 保留已有的命名，如果没有则使用字段展示名称
      title:
        configs[columnsKey].find((col: any) => col.dataIndex === item.id && configs.metaData === entityId)?.title ||
        item.displayName,
      dataIndex: item.id,
      disabled: item.disabled,
      id: item.id
    }));

    setColumnsConfig(newColumns);
    handlePropsChange(columnsKey, newColumns);
  };

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label="数据" className={styles.formItem}>
        <Select
          placeholder={`请选择${item.name}`}
          value={configs[item.key]}
          getPopupContainer={getPopupContainer}
          onChange={(value) => {
            handleMultiPropsChange([
              { key: item.key, value: value },
              { key: searchItemsKey, value: [] },
              { key: columnsKey, value: [] }
            ]);

            setEntityId(value);
            setSearchItemsConfig([]);
            setColumnsConfig([]);
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
        <Form.List initialValue={configs[columnsKey]} field={`${id}-${columnsKey}`}>
          {(_fields, { remove }) => (
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
                  const newList = [...configs[columnsKey]];
                  console.log('configs[columnsKey]', configs[columnsKey]);
                  // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
                  const { oldIndex, newIndex } = e;
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
                      placeholder="宽度"
                    />
                    <Checkbox
                      checked={configs[columnsKey][idx].fixed || false}
                      onChange={(e) => {
                        const newList = [...columnsConfig];
                        if (newList[idx].width === undefined) {
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
                      .filter(
                        (item: MetadataEntityField) => !columnsConfig.some((col: any) => col.dataIndex === item.id)
                      )
                      .map((item: MetadataEntityField) => (
                        <Menu.Item
                          key={item.fieldName}
                          disabled={item?.disabled}
                          onClick={() => {
                            const newList = [...columnsConfig, { title: item.displayName, dataIndex: item.id }];
                            setColumnsConfig(newList);
                            handlePropsChange(columnsKey, newList);
                          }}
                        >
                          {item.displayName}
                        </Menu.Item>
                      ))}
                  </Menu>
                }
                getPopupContainer={getPopupContainer}
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
        <Form.List initialValue={configs[searchItemsKey]} field={`${id}-${searchItemsKey}`}>
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
                      getPopupContainer={getPopupContainer}
                      onChange={(e, option: any) => {
                        const newList = [...searchItemsConfig];
                        newList[idx] = {
                          ...newList[idx],
                          label: option.children,
                          value: e
                        };
                        setSearchItemsConfig(newList);
                        handlePropsChange(searchItemsKey, newList);
                      }}
                      className={styles.tableColumnItemInput}
                      options={configs['columns']
                        .filter(
                          (col: any) =>
                            // 过滤掉已在 configs[item.key] 中被选中的 dataIndex
                            !searchItemsConfig.some((selected: any) => selected.value === col.dataIndex)
                        )
                        .map((item: any) => ({
                          label: item.title,
                          value: item.id
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
                      .filter(
                        (item: MetadataEntityField) =>
                          !searchItemsConfig.some((col: any) => col.value === item.fieldName)
                      )
                      .map((item: MetadataEntityField) => (
                        <Menu.Item
                          key={item.fieldName}
                          onClick={() => {
                            const newList = [...searchItemsConfig, { label: item.displayName, value: item.id }];
                            console.log('newList: ', newList);
                            console.log('item: ', item);
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
                getPopupContainer={getPopupContainer}
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

registerConfigRenderer(CONFIG_TYPES.TABLE_DATA, ({ id, handleMultiPropsChange, handlePropsChange, item, configs }) => (
  <DynamicTableConfig
    id={id}
    handleMultiPropsChange={handleMultiPropsChange}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));

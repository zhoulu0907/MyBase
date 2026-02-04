import { Checkbox, Form, Input, Select, Dropdown, Menu, Button } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';
import {
  CONFIG_TYPES,
  ENTITY_FIELD_TYPE,
  SELECT_OPTIONS_BPM,
  getPopupContainer,
  useAppEntityStore
} from '@onebase/ui-kit';
import {
  getEntityFields,
  FilterEntityFields,
  menuSignal,
  PageType,
  type MetadataEntityPair,
  type MetadataEntityField
} from '@onebase/app';
import { registerConfigRenderer } from '../../registry';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  item: any;
  configs: any;
}

// 不能在卡片展示的数据类型
export const hiddenFieldTypes = [
  ENTITY_FIELD_TYPE.RELATION.VALUE,
  ENTITY_FIELD_TYPE.STRUCTURE.VALUE,
  ENTITY_FIELD_TYPE.ARRAY.VALUE,
  ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE,
  ENTITY_FIELD_TYPE.PASSWORD.VALUE,
  ENTITY_FIELD_TYPE.ENCRYPTED.VALUE,
  ENTITY_FIELD_TYPE.AGGREGATE.VALUE,
  ENTITY_FIELD_TYPE.MULTI_USER.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE
];

// 不能放在搜索条件的数据类型
export const hiddenSearchFieldTypes = [
  ENTITY_FIELD_TYPE.RELATION.VALUE,
  ENTITY_FIELD_TYPE.STRUCTURE.VALUE,
  ENTITY_FIELD_TYPE.ARRAY.VALUE,
  ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE,
  ENTITY_FIELD_TYPE.PASSWORD.VALUE,
  ENTITY_FIELD_TYPE.ENCRYPTED.VALUE,
  ENTITY_FIELD_TYPE.AGGREGATE.VALUE,
  ENTITY_FIELD_TYPE.MULTI_USER.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE,
  ENTITY_FIELD_TYPE.FILE.VALUE,
  ENTITY_FIELD_TYPE.IMAGE.VALUE
];

const DynamicCardDataConfig = ({ handlePropsChange, handleMultiPropsChange, item, configs }: Props) => {
  const { mainEntity, subEntities } = useAppEntityStore();
  const { curMenu } = menuSignal;
  const { form } = Form.useFormContext();

  // 数据绑定列表
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  // 显示字段  自动带出所绑定实体中的前3个自定义字段 支持增删显示字段（不超过7个）
  const [columnsConfig, setColumnsConfig] = useState<any[]>(configs['columns'] || []);
  // 实体字段列表
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);
  // 是否能添加显示字段
  const [enableAddColumn, setEnableAddColumn] = useState<boolean>(false);
  // 搜索项
  const [searchItemsConfig, setSearchItemsConfig] = useState<any[]>(configs['searchItems'] || []);
  // 是否能添加搜索项
  const [enableAddSearchItem, setEnableAddSearchItem] = useState<boolean>(false);
  // 搜索条件配置
  const [searchItems, setSearchItems] = useState<string[]>([]);

  useEffect(() => {
    getEntityList();
    if (configs['metaData']) {
      getFieldListAndColumns();
    }
  }, []);

  // 设置允许的列
  useEffect(() => {
    const res =
      fieldList.some(
        (item: MetadataEntityField) => !columnsConfig.some((col: any) => col.dataIndex == item.fieldName)
      ) && columnsConfig.length < 7;
    setEnableAddColumn(res);
  }, [fieldList, columnsConfig]);

  // 设置允许的搜索项
  useEffect(() => {
    const res = fieldList.some(
      (item: MetadataEntityField) => !searchItemsConfig.some((col: any) => col.value == item.fieldName)
    );

    setEnableAddSearchItem(res);
  }, [fieldList, searchItemsConfig]);

  // 获取数据绑定列表
  const getEntityList = () => {
    const newEntityList = [];
    if (mainEntity) {
      newEntityList.push({
        entityId: mainEntity.entityId,
        entityUuid: mainEntity.entityUuid,
        tableName: mainEntity.tableName,
        entityName: mainEntity.entityName
      });
    }
    if (subEntities) {
      newEntityList.push(
        ...subEntities.entities.map((entity: any) => ({
          entityId: entity.entityId,
          entityUuid: entity.entityUuid,
          tableName: entity.tableName,
          entityName: entity.entityName
        }))
      );
    }
    setEntityList(newEntityList);
  };

  // 获取字段列表
  const getFieldListAndColumns = async (entityUuid?: string) => {
    const res = await getEntityFields({ entityUuid: entityUuid || configs['metaData'] });

    res.forEach((item: MetadataEntityField) => {
      if (item.fieldType && hiddenFieldTypes.includes(item.fieldType)) {
        item.disabled = true;
      }
    });

    const newFieldList = res
      .filter((item: MetadataEntityField) => !FilterEntityFields.includes(item.fieldName))
      .concat(curMenu?.value?.pagesetType === PageType.BPM ? SELECT_OPTIONS_BPM : []);
    setFieldList(newFieldList);

    if (!entityUuid) {
      return;
    }

    const newFieldListNotSystemField = res.filter(
      (item: MetadataEntityField) => item.isSystemField !== 1 && !item.disabled
    );

    const newColumns = newFieldListNotSystemField.map((item: MetadataEntityField) => ({
      // 保留已有的命名，如果没有则使用字段展示名称
      title: configs['columns'].find((col: any) => col.dataIndex === item.fieldName)?.title || item.displayName,
      dataIndex: item.fieldName,
      disabled: item.disabled,
      id: item.id
    }));

    if (curMenu?.value?.pagesetType === PageType.BPM) {
      const bpmColumn = SELECT_OPTIONS_BPM.map((item: any) => {
        return {
          title: item.displayName,
          dataIndex: item.fieldName,
          disabled: false,
          id: ''
        };
      });
      // 自动带出所绑定实体中的前3个自定义字段
      const bpmNewColumns = bpmColumn.concat(newColumns).slice(0, 3);
      setColumnsConfig(bpmNewColumns);
      return bpmNewColumns;
    } else {
      setColumnsConfig(newColumns.slice(0, 3));
      return newColumns.slice(0, 3);
    }
  };

  /**
   * 对实体字段进行排序：系统字段排在后面，非系统字段排在前面
   */
  const sortEntityFields = (a: MetadataEntityField, b: MetadataEntityField): number => {
    if (a.isSystemField !== b.isSystemField) {
      return a.isSystemField ? 1 : -1;
    }
    return 0;
  };

  return (
    <>
      <Form.Item layout="vertical" labelAlign="left" required className={styles.formItem} label="数据绑定">
        <Select
          placeholder="请选择数据"
          value={configs?.metaData}
          getPopupContainer={getPopupContainer}
          onChange={async (value) => {
            // 数据绑定变更 字段相关配置置空
            handleMultiPropsChange([
              { key: 'metaData', value: value },
              { key: 'tableName', value: entityList.find((item) => item.entityUuid === value)?.tableName || '' },
              { key: 'columns', value: (await getFieldListAndColumns(value)) || [] },
              { key: 'titleField', value: '' },
              { key: 'searchItems', value: [] },
              { key: 'coverField', value: '' },
              { key: 'sortBy', value: [] },
              { key: 'filterCondition', value: [] },
              { key: 'groupFilter', value: '' }
            ]);
          }}
        >
          {entityList.map((item) => (
            <Select.Option key={item.entityUuid} value={item.entityUuid}>
              {item.entityName}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>

      <Form.Item
        className={styles.formItem}
        required
        label={
          <>
            <span>显示字段</span>
            <Checkbox
              checked={configs?.showFields}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange('showFields', value);
              }}
            >
              显示文本内容
            </Checkbox>
          </>
        }
      >
        <Form.List field="columns">
          {(_fields, { remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={columnsConfig}
                setList={setColumnsConfig}
                group={{
                  name: 'card-col-item'
                }}
                swap
                sort={true}
                handle=".card-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  const newList = [...configs['columns']];
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
                    handlePropsChange('columns', movedList);
                  }
                }}
              >
                {columnsConfig.map((_col: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
                    <IconDragDotVertical
                      // 支持拖拽的图标，别误删了：）
                      className="card-col-item-handle"
                      style={{
                        cursor: 'move',
                        color: '#555'
                      }}
                    />
                    <Input
                      size="small"
                      value={_col.title}
                      onChange={(e) => {
                        const newList = columnsConfig;
                        newList[idx] = {
                          ...newList[idx],
                          title: e
                        };
                        setColumnsConfig(newList);
                        handlePropsChange('columns', [...newList]);
                      }}
                      className={styles.tableColumnItemInput}
                      placeholder={`请输入第${idx + 1}项`}
                    />
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
                        handlePropsChange('columns', newList);
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
                      .sort(sortEntityFields)
                      .filter(
                        (item: MetadataEntityField) =>
                          !columnsConfig.some((col: any) => col.dataIndex === item.fieldName)
                      )
                      .map((item: MetadataEntityField) => (
                        <Menu.Item
                          key={item.fieldName}
                          disabled={item?.disabled}
                          onClick={() => {
                            const newList = [...columnsConfig, { title: item.displayName, dataIndex: item.fieldName }];
                            setColumnsConfig(newList);
                            handlePropsChange('columns', newList);
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
      </Form.Item>

      <Form.Item className={styles.formItem} label="卡片标题字段">
        <Select
          placeholder="请选择卡片标题字段"
          allowClear
          value={configs?.titleField}
          getPopupContainer={getPopupContainer}
          onChange={(value) => {
            handlePropsChange('titleField', value);
          }}
        >
          {fieldList
            .filter((item: MetadataEntityField) => {
              return !hiddenSearchFieldTypes.includes(item.fieldType);
            })
            .map((item) => (
              <Select.Option key={item.fieldName} value={item.fieldName}>
                {item.displayName}
              </Select.Option>
            ))}
        </Select>
      </Form.Item>

      <Form.Item layout="vertical" labelAlign="left" label={'搜索项'} className={styles.formItem}>
        <Form.List initialValue={configs['searchItems']} field="searchItems">
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={searchItemsConfig}
                setList={setSearchItemsConfig}
                group={{
                  name: 'card-col-item'
                }}
                swap
                sort={true}
                handle=".card-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onSort={(e) => {
                  const newList = [...searchItemsConfig];
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
                    handlePropsChange('searchItems', movedList);
                  }
                }}
              >
                {configs['searchItems'].map((_col: any, idx: number) => (
                  <div key={idx} className={styles.tableColumnItem}>
                    <IconDragDotVertical
                      // 支持拖拽的图标，别误删了：）
                      className="card-col-item-handle"
                      style={{
                        cursor: 'move',
                        color: '#555'
                      }}
                    />
                    <Select
                      size="small"
                      value={_col.label}
                      getPopupContainer={getPopupContainer}
                      onChange={(e, option: any) => {
                        const newList = [...searchItemsConfig];
                        newList[idx] = {
                          ...newList[idx],
                          label: option.children,
                          value: e
                        };
                        setSearchItemsConfig(newList);
                        handlePropsChange('searchItems', newList);
                      }}
                      className={styles.tableColumnItemInput}
                      options={configs['columns'].map((item: any) => {
                        return {
                          label: item.title,
                          value: item.dataIndex,
                          disabled: searchItemsConfig.some((selected: any) => selected.value === item.dataIndex)
                        };
                      })}
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
                        handlePropsChange('searchItems', newList);
                      }}
                    ></Button>
                  </div>
                ))}
              </ReactSortable>

              <Select
                getPopupContainer={getPopupContainer}
                value={searchItems}
                mode="multiple"
                triggerElement={
                  <Button type={enableAddSearchItem ? 'outline' : 'secondary'} disabled={!enableAddSearchItem}>
                    新增搜索项
                  </Button>
                }
                onChange={(value) => {
                  setSearchItems(value);
                }}
                triggerProps={{
                  autoAlignPopupWidth: false,
                  autoAlignPopupMinWidth: true
                }}
                onVisibleChange={(visible) => {
                  if (!visible) {
                    // 下拉框收起时 回显数据
                    const newList = searchItems.map((ele: string) => {
                      const currentField = fieldList.find((e) => e.fieldName === ele);
                      return { label: currentField?.displayName, value: ele };
                    });
                    form.setFieldValue('searchItems', newList);
                    setSearchItemsConfig(newList);
                    handlePropsChange('searchItems', newList);
                  }
                }}
              >
                {fieldList
                  .sort(sortEntityFields)
                  .filter((item: MetadataEntityField) => {
                    return !hiddenSearchFieldTypes.includes(item.fieldType);
                  })
                  .map((item: MetadataEntityField) => (
                    <Select.Option key={item.fieldName} value={item.fieldName}>
                      {item.displayName}
                    </Select.Option>
                  ))}
              </Select>
            </div>
          )}
        </Form.List>
      </Form.Item>
    </>
  );
};

export default DynamicCardDataConfig;

registerConfigRenderer(CONFIG_TYPES.CARD_DATA, ({ handlePropsChange, handleMultiPropsChange, item, configs }) => (
  <DynamicCardDataConfig
    handlePropsChange={handlePropsChange}
    handleMultiPropsChange={handleMultiPropsChange}
    item={item}
    configs={configs}
  />
));

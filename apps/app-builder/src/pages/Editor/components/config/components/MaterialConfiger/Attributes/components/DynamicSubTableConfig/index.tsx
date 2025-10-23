import { useAppEntityStore } from '@/store';
import { Button, Dropdown, Form, Input, Menu, Select } from '@arco-design/web-react';
import { IconDragDotVertical } from '@arco-design/web-react/icon';
import { IconDelete } from '@douyinfe/semi-icons';
import { FilterEntityFields, getEntityFields, type AppEntity, type MetadataEntityField } from '@onebase/app';
import { ENTITY_FIELD_TYPE } from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

export interface DynamicSubTableConfigProps {
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

const DynamicSubTableConfig: React.FC<DynamicSubTableConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const columnsKey = 'columns';

  const { subEntities } = useAppEntityStore();

  const [subEntityId, setSubEntityId] = useState<string>(configs[item.key] || '');
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);

  const [columnsConfig, setColumnsConfig] = useState<any[]>(configs[columnsKey] || []);

  const [enableAddColumn, setEnableAddColumn] = useState<boolean>(false);

  // 如果实体id变化，重新获取字段列表
  useEffect(() => {
    if (subEntityId) {
      getFieldList();
    }
  }, [subEntityId]);

  // 获取当前表格关联的实体id
  useEffect(() => {
    if (id != configs.id) {
      return;
    }

    if (configs[item.key]) {
      setSubEntityId(configs[item.key]);
    }
  }, []);

  // 设置允许的列
  useEffect(() => {
    const res = fieldList.some(
      (item: MetadataEntityField) => !columnsConfig.some((col: any) => col.dataIndex == item.id)
    );

    setEnableAddColumn(res);
  }, [fieldList, columnsConfig]);

  // 获取字段列表
  const getFieldList = async () => {
    const res = await getEntityFields({ entityId: subEntityId });

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

    if (configs.subTable === subEntityId) {
      return;
    }

    const newColumns = newFieldListNotSystemField.map((item: MetadataEntityField) => ({
      // 保留已有的命名，如果没有则使用字段展示名称
      title:
        configs[columnsKey].find((col: any) => col.dataIndex === item.id && configs.subTable === subEntityId)?.title ||
        item.displayName,
      dataIndex: item.id,
      disabled: item.disabled,
      id: item.id,
      dataType: item.fieldType
    }));

    setColumnsConfig(newColumns);
    handlePropsChange(columnsKey, newColumns);
  };

  return (
    <>
      <Form.Item layout="vertical" labelAlign="left" label={'子实体配置'} className={styles.formItem}>
        <Select
          value={configs[item.key]}
          onChange={(value) => {
            handlePropsChange(item.key, value);
            setSubEntityId(value);
            setColumnsConfig([]);
          }}
          options={(subEntities?.entities).map((entity: AppEntity) => ({
            value: entity.entityId,
            label: entity.entityName
          }))}
        />
      </Form.Item>

      {/* 表头配置 */}
      <Form.Item layout="vertical" labelAlign="left" label={'子字段配置'} className={styles.formItem}>
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
                  const newList = [...configs[columnsKey]];
                  // console.log('configs[columnsKey]', configs[columnsKey])
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
                        color: '#555',
                        marginRight: 8
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
                    />
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
                            const newList = [
                              ...columnsConfig,
                              {
                                title: item.displayName,
                                dataIndex: item.id,
                                id: item.id,
                                dataType: item.fieldType
                              }
                            ];
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
      </Form.Item>
    </>
  );
};

export default DynamicSubTableConfig;

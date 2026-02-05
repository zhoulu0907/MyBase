import { Checkbox, Form, Input, Button, Grid, Modal, Select } from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit, IconDelete } from '@arco-design/web-react/icon';
import { useEffect, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import {
  CONFIG_TYPES,
  ENTITY_FIELD_TYPE,
  INDICATOR_CARD_STYLE_TYPE,
  INDICATOR_CALCULATE_TYPE,
  INDICATOR_TIME_DEMENSION,
  INDICATOR_COMPARE_CALCULATE_METHOD,
  INDICATOR_COMPARE_CALCULATE_TYPE,
  getPopupContainer,
  useAppEntityStore
} from '@onebase/ui-kit';
import {
  VALIDATION_TYPE,
  getEntityFields,
  getFieldCheckTypeApi,
  type MetadataEntityPair,
  type MetadataEntityField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';
import { read } from 'fs';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

// 暂时不能展示的数据类型
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

const DynamicIndicatorCardConfig = ({ handlePropsChange, item, configs, id }: Props) => {
  const indicatorKey = 'indicatorList';
  useSignals();
  const { mainEntity, subEntities } = useAppEntityStore();

  const [indicatorList, setIndicatorList] = useState<any[]>(configs[indicatorKey] || []);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentIndex, setCurrentIndex] = useState<number | null>(null);
  const [currentIndicator, setCurrentIndicator] = useState<any>({});
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  // 字段列表
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);
  const [validationTypes, setValidationTypes] = useState<EntityFieldValidationTypes[]>([]);

  useEffect(() => {
    getEntityList();
  }, []);

  useEffect(() => {
    if (currentIndicator.metaData) {
      getFieldList();
    } else {
      setFieldList([]);
    }
  }, [currentIndicator.metaData]);

  // 获取实体列表
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
  const getFieldList = async () => {
    const res = await getEntityFields({ entityUuid: currentIndicator.metaData });

    res.forEach((item: MetadataEntityField) => {
      if (item.fieldType && hiddenFieldTypes.includes(item.fieldType)) {
        item.disabled = true;
      }
    });
    setFieldList(res);
    const fieldIds = res.map((ele:any)=>ele.fieldId)
    debugger
    getValidationTypes(fieldIds)
  };

  const getValidationTypes = async (fieldIds: string[]) => {
    if (!fieldIds || fieldIds.length === 0) {
      setValidationTypes([])
      return;
    }
    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);

  };

  const updateIndicator = () => {
    const newList = [...indicatorList];
    newList[currentIndex || 0] = currentIndicator;
    setIndicatorList(newList);
    setCurrentIndex(null);
    handlePropsChange(indicatorKey, newList);
    setModalVisible(false);
    setCurrentIndicator({});
  };

  return (
    <>
      <Form.Item className={styles.formItem} label="指标配置">
        <Form.List field={`${id}-'indicatorList'`}>
          {(_fields, { remove }) => (
            <div>
              <ReactSortable
                list={indicatorList}
                setList={setIndicatorList}
                group={{
                  name: 'indicator-col-item'
                }}
                swap
                sort={true}
                handle=".indicator-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  console.log(e);
                  const newList = [...configs[indicatorKey]];
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
                    handlePropsChange(indicatorKey, movedList);
                  }
                }}
              >
                {indicatorList.map((_col: any, idx: number) => (
                  <Grid.Row key={idx} gutter={[4, 16]} align="center">
                    <Grid.Col span={2}>
                      <IconDragDotVertical
                        // 支持拖拽的图标，别误删了：）
                        className="indicator-col-item-handle"
                        style={{
                          cursor: 'move',
                          color: '#555'
                        }}
                      />
                    </Grid.Col>
                    <Grid.Col span={16}>
                      <Input readOnly value={_col.label?.text} />
                    </Grid.Col>
                    <Grid.Col span={6}>
                      <Button
                        type="text"
                        icon={<IconEdit />}
                        onClick={() => {
                          setCurrentIndex(idx);
                          setCurrentIndicator({ ..._col });
                          setModalVisible(true);
                        }}
                      ></Button>
                      <Button
                        type="text"
                        status="danger"
                        icon={<IconDelete />}
                        disabled={!indicatorList || indicatorList.length < 2}
                        onClick={() => {
                          const newList = [...indicatorList];
                          newList.splice(idx, 1);
                          setIndicatorList(newList);
                          handlePropsChange(indicatorKey, newList);
                          remove(idx);
                        }}
                      ></Button>
                    </Grid.Col>
                  </Grid.Row>
                ))}
              </ReactSortable>
            </div>
          )}
        </Form.List>
      </Form.Item>

      <Modal
        visible={modalVisible}
        title="编辑指标"
        onCancel={() => {
          setModalVisible(false);
          setCurrentIndicator({});
        }}
        className={styles.indicatorModal}
        footer={
          <>
            <Button
              onClick={() => {
                setModalVisible(false);
                setCurrentIndicator({});
              }}
            >
              取消
            </Button>
            <Button type="primary" onClick={updateIndicator}>
              确定
            </Button>
          </>
        }
      >
        {/* 指标标题、指标描述 */}
        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item
              label={
                <>
                  <span>
                    指标标题<span style={{ color: 'red' }}> *</span>
                  </span>
                  <Checkbox
                    style={{ float: 'right' }}
                    checked={currentIndicator.label?.display}
                    onChange={(value) => {
                      const label = { ...currentIndicator.label, display: value };
                      setCurrentIndicator((prev: any) => ({ ...prev, label }));
                    }}
                  ></Checkbox>
                </>
              }
            >
              <Input
                placeholder="请输入"
                value={currentIndicator.label?.text}
                onChange={(value) => {
                  const label = { ...currentIndicator.label, text: value };
                  setCurrentIndicator((prev: any) => ({ ...prev, label }));
                }}
              />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item label="指标描述">
              <Input.TextArea
                placeholder="请输入"
                value={currentIndicator.describe}
                maxLength={500}
                onChange={(value) => {
                  setCurrentIndicator((prev: any) => ({ ...prev, describe: value }));
                }}
              />
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        {/* 数据源 指标字段 */}
        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item label="数据源" required>
              <Select
                getPopupContainer={getPopupContainer}
                placeholder="请选择"
                value={currentIndicator.metaData}
                onChange={(value) => {
                  const tableName = entityList.find((item) => item.entityUuid === value)?.tableName || '';
                  setCurrentIndicator((prev: any) => ({ ...prev, metaData: value, tableName, calculateField: 'id' }));
                }}
              >
                {entityList.map((item) => (
                  <Select.Option key={item.entityUuid} value={item.entityUuid}>
                    {item.entityName}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item label="指标字段" required>
              <Grid.Row gutter={8}>
                <Grid.Col span={12}>
                  <Select
                    getPopupContainer={getPopupContainer}
                    placeholder="请选择"
                    value={currentIndicator.calculateField}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, calculateField: value }));
                    }}
                  >
                    {fieldList
                      .filter(
                        (item: MetadataEntityField) => item.fieldType && !hiddenFieldTypes.includes(item.fieldType)
                      )
                      .map((item: MetadataEntityField) => (
                        <Select.Option key={item.fieldName} value={item.fieldName}>
                          {item.displayName}
                        </Select.Option>
                      ))}
                  </Select>
                </Grid.Col>
                <Grid.Col span={12}>
                  <Select
                    getPopupContainer={getPopupContainer}
                    placeholder="请选择"
                    value={currentIndicator.calculateType}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, calculateType: value }));
                    }}
                  >
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.SUM}>求和</Select.Option>
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.AVERAGE}>平均值</Select.Option>
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.MAX}>最大值</Select.Option>
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.MIN}>最小值</Select.Option>
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.COUNT}>计数</Select.Option>
                    <Select.Option value={INDICATOR_CALCULATE_TYPE.DEDUCE}>去重计数</Select.Option>
                  </Select>
                </Grid.Col>
              </Grid.Row>
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        {/* 数据过滤 */}
        <Grid.Row gutter={8}>
          <Grid.Col span={24}>
            <Form.Item label="数据过滤">
              {currentIndicator.filterCondition?.map((ele: any, index: number) => (
                <div key={index} className={styles.dataFilter}>
                  <div className={styles.items}>
                    <div className={styles.tag}>且</div>
                    <div>
                      {ele.conditions?.map((item: any, i: number) => (
                        <Grid.Row key={`${index}-${i}`} gutter={[8, 16]} align="center">
                          <Grid.Col span={8}>
                            <Select getPopupContainer={getPopupContainer} placeholder="请选择" value={item.fieldKey}>
                              {fieldList.map((e: MetadataEntityField) => (
                                <Select.Option key={e.fieldName} value={e.fieldName}>
                                  {e.displayName}
                                </Select.Option>
                              ))}
                            </Select>
                          </Grid.Col>
                          {/* 操作 */}
                          <Grid.Col span={4}>
                            <Select getPopupContainer={getPopupContainer} placeholder="请选择" value={item.op}>
                              {item.fieldKey &&
                                fieldList.map((e: MetadataEntityField) => (
                                  <Select.Option key={e.fieldName} value={e.fieldName}>
                                    {e.displayName}
                                  </Select.Option>
                                ))}
                            </Select>
                          </Grid.Col>
                          {/* 操作不为空和为空不需要选择操作类型 */}
                          {item.op !== VALIDATION_TYPE.IS_EMPTY && item.op !== VALIDATION_TYPE.IS_NOT_EMPTY && (
                            <>
                              <Grid.Col span={3}>1</Grid.Col>
                              <Grid.Col span={8}>1</Grid.Col>
                            </>
                          )}
                          <Grid.Col span={1}>
                            <IconDelete
                              style={{ fontSize: '15px', color: 'red' }}
                              onClick={() => {
                                const filterCondition = [...currentIndicator.filterCondition];
                                if (filterCondition[index].conditions?.length === 1) {
                                  filterCondition.splice(index, 1);
                                } else {
                                  filterCondition[index].conditions.splice(i, 1);
                                }
                                setCurrentIndicator((prev: any) => ({ ...prev, filterCondition }));
                              }}
                            />
                          </Grid.Col>
                        </Grid.Row>
                      ))}

                      <Button
                        type="text"
                        size="small"
                        style={{ marginTop: '10px' }}
                        onClick={() => {
                          const filterCondition = [...currentIndicator.filterCondition];
                          filterCondition[index].conditions.push({});
                          setCurrentIndicator((prev: any) => ({ ...prev, filterCondition }));
                        }}
                      >
                        + 添加且条件
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
              <Button
                type="text"
                onClick={() => {
                  const filterCondition = [...currentIndicator.filterCondition, { conditions: [{}] }];
                  setCurrentIndicator((prev: any) => ({ ...prev, filterCondition }));
                }}
              >
                + 添加或条件
              </Button>
            </Form.Item>
          </Grid.Col>
        </Grid.Row>
        <Grid.Row gutter={8}>
          <Grid.Col span={12}></Grid.Col>
          <Grid.Col span={12}></Grid.Col>
        </Grid.Row>
        <Grid.Row gutter={8}>
          <Grid.Col span={12}></Grid.Col>
          <Grid.Col span={12}></Grid.Col>
        </Grid.Row>

        {currentIndicator.compareLimit && <></>}

        <Grid.Row gutter={8}>
          <Grid.Col span={12}></Grid.Col>
          <Grid.Col span={12}></Grid.Col>
        </Grid.Row>
      </Modal>
    </>
  );
};

export default DynamicIndicatorCardConfig;

registerConfigRenderer(CONFIG_TYPES.INDICATOR_CARD_CONFIG, ({ handlePropsChange, item, configs, id }) => (
  <DynamicIndicatorCardConfig handlePropsChange={handlePropsChange} item={item} configs={configs} id={id} />
));

import {
  Checkbox,
  Form,
  Input,
  Button,
  Grid,
  Modal,
  Select,
  InputNumber,
  Switch,
  DatePicker,
  Dropdown,
  Menu,
  ColorPicker,
  Radio
} from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit, IconDelete, IconLaunch, IconStarFill } from '@arco-design/web-react/icon';
import { useEffect, useRef, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import {
  CONFIG_TYPES,
  ENTITY_FIELD_TYPE,
  COMPONENT_FIELD_MAP,
  COMPONENT_MAP,
  STATUS_VALUES,
  STATUS_OPTIONS,
  WIDTH_VALUES,
  WIDTH_OPTIONS,
  INDICATOR_CALCULATE_TYPE,
  INDICATOR_TIME_DEMENSION,
  INDICATOR_COMPARE_CALCULATE_METHOD,
  INDICATOR_COMPARE_CALCULATE_TYPE,
  getPopupContainer,
  useAppEntityStore,
  webMenuIcons
} from '@onebase/ui-kit';
import {
  FieldType,
  VALIDATION_TYPE,
  getEntityFields,
  getFieldCheckTypeApi,
  type MetadataEntityPair,
  type MetadataEntityField,
  type EntityFieldValidationTypes
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import { registerConfigRenderer } from '../../registry';
import AsyncDeptSelectField from './component/AsyncDeptSelectField';
import AsyncSelectField from './component/AsyncSelectField';
import AsyncUserSelectField from './component/AsyncUserSelectField';
import { FormulaEditor } from '@/components/FormulaEditor';
import { ReactSVG } from 'react-svg';
import styles from '../../index.module.less';

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
  const [formulaData, setFormulaData] = useState<string>('');
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaKey, setFormulaKey] = useState<{ index: null | number; i: null | number }>({
    index: null,
    i: null
  });
  // 图标下拉内容
  const allWebMenuIcons = webMenuIcons.map((ele) => ele.children).reduce((acc, current) => acc.concat(current), []);
  const opCodeOptions = [
    {
      label: '公式',
      value: FieldType.FORMULA
    },
    {
      label: '静态值',
      value: FieldType.VALUE
    },
    {
      label: '变量',
      value: FieldType.VARIABLES
    }
  ];

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
    const fieldIds = res?.map((ele: any) => ele.id);
    getValidationTypes(fieldIds, res);
  };

  const getValidationTypes = async (fieldIds: string[], fields: MetadataEntityField[]) => {
    if (!fieldIds || fieldIds.length === 0) {
      setValidationTypes([]);
      return;
    }
    const newValidationTypes = await getFieldCheckTypeApi(fieldIds);
    newValidationTypes.forEach((item: EntityFieldValidationTypes) => {
      const fieldName = fields.find((field) => field.id == item.fieldId)?.fieldName || '';
      item.fieldKey = fieldName;
    });

    setValidationTypes(newValidationTypes);
  };

  const updateIndicator = () => {
    const newList = [...indicatorList];
    newList[currentIndex || 0] = currentIndicator;
    setIndicatorList(newList);
    setCurrentIndex(null);
    handlePropsChange(indicatorKey, newList);
    setModalVisible(false);
  };

  const handleFormulaConfirm = (formulaData: string, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    const filterCondition = [...currentIndicator.filterCondition];
    filterCondition[formulaKey.index || 0].conditions[formulaKey.i || 0].value = formulaData;
    filterCondition[formulaKey.index || 0].conditions[formulaKey.i || 0].formattedFormula = formattedFormula;
    setCurrentIndicator((prev: any) => ({
      ...prev,
      filterCondition: filterCondition
    }));
    setFormulaData('');
  };

  const openFormulaEditor = (item: any, index: number, i: number) => {
    setFormulaVisible(true);
    setFormulaKey({
      index,
      i
    });
    setFormulaData(item.value);
  };

  const StaticValueComponent = (item: any, fieldKey: string, op: string, index: number, i: number) => {
    const fieldValidationType = validationTypes.find((cc) => cc.fieldKey == fieldKey);

    if (
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.TEXT.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.LONG_TEXT.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.EMAIL.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.PHONE.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.URL.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.ADDRESS.VALUE
    ) {
      return (
        <Input
          value={item.value}
          placeholder="请输入静态值"
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.NUMBER.VALUE) {
      // 范围
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <Grid.Row gutter={8}>
            <Grid.Col span={12}>
              <InputNumber
                value={item.value?.[0]}
                style={{ width: '100%' }}
                onChange={(value) => {
                  const filterCondition = [...currentIndicator.filterCondition];
                  filterCondition[index].conditions[i].value[0] = value;
                  setCurrentIndicator((prev: any) => ({
                    ...prev,
                    filterCondition: filterCondition
                  }));
                }}
              />
            </Grid.Col>
            <Grid.Col span={12}>
              <InputNumber
                value={item.value?.[1]}
                style={{ width: '100%' }}
                onChange={(value) => {
                  const filterCondition = [...currentIndicator.filterCondition];
                  filterCondition[index].conditions[i].value[1] = value;
                  setCurrentIndicator((prev: any) => ({
                    ...prev,
                    filterCondition: filterCondition
                  }));
                }}
              />
            </Grid.Col>
          </Grid.Row>
        );
      }

      return (
        <InputNumber
          value={item.value}
          placeholder="请输入静态值"
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.BOOLEAN.VALUE) {
      return (
        <Switch
          checked={item.value}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.DATE.VALUE) {
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <DatePicker.RangePicker
            value={item.value}
            onChange={(value) => {
              const filterCondition = [...currentIndicator.filterCondition];
              filterCondition[index].conditions[i].value = value;
              setCurrentIndicator((prev: any) => ({
                ...prev,
                filterCondition: filterCondition
              }));
            }}
          />
        );
      }
      return (
        <DatePicker
          placeholder="请输入静态值"
          value={item.value}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.DATETIME.VALUE) {
      if (op == VALIDATION_TYPE.RANGE) {
        return (
          <DatePicker.RangePicker
            value={item.value}
            showTime
            onChange={(value) => {
              const filterCondition = [...currentIndicator.filterCondition];
              filterCondition[index].conditions[i].value = value;
              setCurrentIndicator((prev: any) => ({
                ...prev,
                filterCondition: filterCondition
              }));
            }}
          />
        );
      }

      return (
        <DatePicker
          placeholder="请输入静态值"
          showTime
          value={item.value}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.RADIO.VALUE ||
      fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.SELECT.VALUE
    ) {
      return (
        <AsyncSelectField
          value={item.value}
          fieldName={'value'}
          fieldKey={`${currentIndicator.tableName}.${fieldKey}`}
          entityFieldValidationTypes={validationTypes}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.USER.VALUE) {
      return (
        <AsyncUserSelectField
          value={item.value}
          fieldName={'value'}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    if (fieldValidationType?.fieldTypeCode == ENTITY_FIELD_TYPE.DEPARTMENT.VALUE) {
      return (
        <AsyncDeptSelectField
          value={item.value}
          fieldName={'value'}
          onChange={(value) => {
            const filterCondition = [...currentIndicator.filterCondition];
            filterCondition[index].conditions[i].value = value;
            setCurrentIndicator((prev: any) => ({
              ...prev,
              filterCondition: filterCondition
            }));
          }}
        />
      );
    }

    return (
      <Input
        placeholder="请输入静态值"
        value={item.value}
        onChange={(value) => {
          const filterCondition = [...currentIndicator.filterCondition];
          filterCondition[index].conditions[i].value = value;
          setCurrentIndicator((prev: any) => ({
            ...prev,
            filterCondition: filterCondition
          }));
        }}
      />
    );
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
                {indicatorList?.map((_col: any, idx: number) => (
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
        onCancel={() => setModalVisible(false)}
        unmountOnExit
        className={styles.indicatorModal}
        footer={
          <>
            <Button onClick={() => setModalVisible(false)}>取消</Button>
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
                {entityList?.map((item) => (
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
                      ?.filter(
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
                <div key={`filterCondition-${index}`} className={styles.dataFilter}>
                  <div className={styles.items}>
                    <div className={styles.tag}>且</div>
                    <div>
                      {ele.conditions?.map((item: any, i: number) => (
                        <Grid.Row key={`${index}-${i}`} gutter={[8, 16]} align="center" style={{ width: '100%' }}>
                          <Grid.Col span={6}>
                            <Select
                              getPopupContainer={getPopupContainer}
                              placeholder="请选择"
                              value={item.fieldKey}
                              onChange={(value) => {
                                const filterCondition = [...currentIndicator.filterCondition];
                                filterCondition[index].conditions[i] = {
                                  fieldKey: value,
                                  op: undefined,
                                  operatorType: undefined,
                                  value: undefined,
                                  formattedFormula: ''
                                };
                                setCurrentIndicator((prev: any) => ({ ...prev, filterCondition: filterCondition }));
                              }}
                            >
                              {fieldList?.map((e: MetadataEntityField) => (
                                <Select.Option key={e.fieldName} value={e.fieldName}>
                                  {e.displayName}
                                </Select.Option>
                              ))}
                            </Select>
                          </Grid.Col>
                          {/* 操作 */}
                          <Grid.Col span={5}>
                            <Select
                              getPopupContainer={getPopupContainer}
                              placeholder="请选择"
                              value={item.op}
                              onChange={(value) => {
                                const filterCondition = [...currentIndicator.filterCondition];
                                filterCondition[index].conditions[i] = {
                                  fieldKey: item.fieldKey,
                                  op: value,
                                  operatorType: undefined,
                                  value: undefined,
                                  formattedFormula: ''
                                };
                                setCurrentIndicator((prev: any) => ({ ...prev, filterCondition: filterCondition }));
                              }}
                            >
                              {item.fieldKey &&
                                validationTypes
                                  ?.find((cc) => cc.fieldKey == item.fieldKey)
                                  ?.validationTypes?.map((operator: any) => (
                                    <Select.Option key={operator.code} value={operator.code}>
                                      {operator.name}
                                    </Select.Option>
                                  ))}
                            </Select>
                          </Grid.Col>
                          {/* 操作不为空和为空不需要选择操作类型 */}
                          {item.op !== VALIDATION_TYPE.IS_EMPTY && item.op !== VALIDATION_TYPE.IS_NOT_EMPTY && (
                            <>
                              <Grid.Col span={5}>
                                <Select
                                  getPopupContainer={getPopupContainer}
                                  value={item.operatorType}
                                  disabled={item.op === undefined}
                                  options={opCodeOptions}
                                  onChange={(value) => {
                                    const filterCondition = [...currentIndicator.filterCondition];
                                    filterCondition[index].conditions[i] = {
                                      fieldKey: item.fieldKey,
                                      op: item.op,
                                      operatorType: value,
                                      value: item.op === VALIDATION_TYPE.RANGE ? [undefined, undefined] : undefined,
                                      formattedFormula: ''
                                    };
                                    setCurrentIndicator((prev: any) => ({
                                      ...prev,
                                      filterCondition: filterCondition
                                    }));
                                  }}
                                ></Select>
                              </Grid.Col>
                              <Grid.Col span={7}>
                                {item.operatorType === undefined && <Input placeholder="请输入" disabled />}

                                {item.operatorType === FieldType.VALUE &&
                                  StaticValueComponent(item, item.fieldKey, item.op, index, i)}

                                {item.operatorType === FieldType.VARIABLES && (
                                  <Select
                                    getPopupContainer={getPopupContainer}
                                    placeholder="请选择"
                                    value={item.value}
                                    onChange={(value) => {
                                      const filterCondition = [...currentIndicator.filterCondition];
                                      filterCondition[index].conditions[i].value = value;
                                      setCurrentIndicator((prev: any) => ({
                                        ...prev,
                                        filterCondition: filterCondition
                                      }));
                                    }}
                                  >
                                    {fieldList
                                      ?.filter((ele) => {
                                        const fieldType = fieldList.find(
                                          (e) => e.fieldName === item.fieldKey
                                        )?.fieldType;
                                        if (fieldType) {
                                          const cpTypes = COMPONENT_FIELD_MAP[COMPONENT_MAP[fieldType]];
                                          return cpTypes?.includes(ele.fieldType);
                                        }
                                        return true;
                                      })
                                      .map((e: MetadataEntityField) => (
                                        <Select.Option key={e.fieldName} value={e.fieldName}>
                                          {e.displayName}
                                        </Select.Option>
                                      ))}
                                  </Select>
                                )}

                                {item.operatorType === FieldType.FORMULA && (
                                  <Button
                                    onClick={() => openFormulaEditor(item, index, i)}
                                    long
                                    className={styles.formulaBtn}
                                  >
                                    {item.value ? item?.formattedFormula : 'ƒx 编辑公式'}
                                    {item.value ? <IconLaunch /> : ''}
                                  </Button>
                                )}
                              </Grid.Col>
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
                          filterCondition[index].conditions.push({
                            fieldKey: undefined,
                            op: undefined,
                            operatorType: undefined,
                            value: undefined,
                            formattedFormula: ''
                          });
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
                  const filterCondition = [
                    ...currentIndicator.filterCondition,
                    {
                      conditions: [
                        {
                          fieldKey: undefined,
                          op: undefined,
                          operatorType: undefined,
                          value: undefined,
                          formattedFormula: ''
                        }
                      ]
                    }
                  ];
                  setCurrentIndicator((prev: any) => ({ ...prev, filterCondition }));
                }}
              >
                + 添加或条件
              </Button>
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        {/* 格式、图标样式 */}
        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item label="格式">
              <Grid.Row gutter={8} align="center">
                <Grid.Col span={8}>
                  <Checkbox
                    checked={currentIndicator.precisionLimit}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, precisionLimit: value }));
                    }}
                  >
                    保留小数点
                  </Checkbox>
                </Grid.Col>
                <Grid.Col span={12}>
                  <InputNumber
                    value={currentIndicator.precision}
                    size="mini"
                    min={0}
                    max={10}
                    precision={0}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, precision: value }));
                    }}
                  />
                </Grid.Col>
              </Grid.Row>
              <Grid.Row gutter={8} align="center">
                <Grid.Col span={24}>
                  <Checkbox
                    checked={currentIndicator.percent}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, percent: value }));
                    }}
                  >
                    显示为百分比
                  </Checkbox>
                </Grid.Col>
              </Grid.Row>

              <Grid.Row gutter={8} align="center">
                <Grid.Col span={8}>
                  <Checkbox
                    checked={currentIndicator.unitLimit}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, unitLimit: value }));
                    }}
                  >
                    显示单位
                  </Checkbox>
                </Grid.Col>
                <Grid.Col span={12}>
                  <Input
                    size="mini"
                    value={currentIndicator.unit}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, unit: value }));
                    }}
                  />
                </Grid.Col>
              </Grid.Row>

              <Grid.Row gutter={8} align="center">
                <Grid.Col span={24}>
                  <Checkbox
                    checked={currentIndicator.thousandsSeparator}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, thousandsSeparator: value }));
                    }}
                  >
                    使用千分位分隔符
                  </Checkbox>
                </Grid.Col>
              </Grid.Row>
              <Grid.Row gutter={8} align="center">
                <Grid.Col span={24}>
                  <Checkbox
                    checked={currentIndicator.absoluteValue}
                    onChange={(value) => {
                      setCurrentIndicator((prev: any) => ({ ...prev, absoluteValue: value }));
                    }}
                  >
                    显示为绝对值
                  </Checkbox>
                </Grid.Col>
              </Grid.Row>
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item
              label={
                <>
                  <span>
                    图标样式<span style={{ color: 'red' }}> *</span>
                  </span>
                  <Checkbox
                    style={{ float: 'right' }}
                    checked={currentIndicator.icon?.display}
                    onChange={(value) => {
                      const icon = { ...currentIndicator.icon, display: value };
                      setCurrentIndicator((prev: any) => ({ ...prev, icon }));
                    }}
                  ></Checkbox>
                </>
              }
            >
              <Grid.Row gutter={8} align="center">
                <Grid.Col span={3}>
                  <Dropdown
                    droplist={
                      <Menu>
                        {allWebMenuIcons?.map((iconItem) => (
                          <Menu.Item
                            key={iconItem.code}
                            onClick={() => {
                              const icon = { ...currentIndicator.icon, name: iconItem.code };
                              setCurrentIndicator((prev: any) => ({ ...prev, icon }));
                            }}
                          >
                            <img style={{ width: 'auto', height: '18px', fill: '#333' }} src={iconItem.icon} alt="" />
                          </Menu.Item>
                        ))}
                      </Menu>
                    }
                  >
                    <div
                      style={{
                        width: '32px',
                        height: '32px',
                        backgroundColor: 'var(--color-secondary)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        borderRadius: '4px'
                      }}
                    >
                      <ReactSVG
                        style={{ height: '20px' }}
                        src={allWebMenuIcons.find((ele) => ele.code === currentIndicator.icon?.name)?.icon || ''}
                        beforeInjection={(svg) => {
                          const fillColor = currentIndicator.icon?.color || 'rgb(var(--primary-6))';
                          svg.querySelectorAll('*').forEach((el) => {
                            if (el.getAttribute('fill') === 'black') {
                              el.setAttribute('fill', fillColor);
                            }
                          });
                          svg.setAttribute('width', '20px');
                          svg.setAttribute('height', '20px');
                        }}
                      />
                    </div>
                  </Dropdown>
                </Grid.Col>
                <Grid.Col span={21}>
                  <ColorPicker
                    style={{ width: '100%' }}
                    value={currentIndicator.icon?.color}
                    showText
                    onChange={(value) => {
                      const icon = { ...currentIndicator.icon, color: value };
                      setCurrentIndicator((prev: any) => ({ ...prev, icon }));
                    }}
                  />
                </Grid.Col>
              </Grid.Row>
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        {/* 背景颜色、同环比 */}
        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item label="背景颜色">
              <ColorPicker
                style={{ width: '100%' }}
                value={currentIndicator.backgroundColor}
                showText
                onChange={(value) => {
                  setCurrentIndicator((prev: any) => ({ ...prev, backgroundColor: value }));
                }}
              />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item label="同环比">
              <Switch
                checked={currentIndicator.compareLimit}
                onChange={(value) => {
                  setCurrentIndicator((prev: any) => ({ ...prev, compareLimit: value }));
                }}
              />
            </Form.Item>
          </Grid.Col>
        </Grid.Row>

        {/* 同环比描述、时间字段、时间维度、计算方式、计算类型 */}
        {currentIndicator.compareLimit && (
          <Grid.Row gutter={8}>
            <Grid.Col span={12}>
              <Form.Item label="同环比描述" required>
                <Input
                  placeholder="请输入"
                  value={currentIndicator.compareDescribe}
                  onChange={(value) => {
                    setCurrentIndicator((prev: any) => ({ ...prev, compareDescribe: value }));
                  }}
                />
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="时间字段" required>
                <Select
                  getPopupContainer={getPopupContainer}
                  value={currentIndicator.timeField}
                  onChange={(value) => {
                    setCurrentIndicator((prev: any) => ({ ...prev, timeField: value }));
                  }}
                >
                  {fieldList
                    .filter(
                      (ele) =>
                        ele.fieldType === ENTITY_FIELD_TYPE.DATE.VALUE ||
                        ele.fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE
                    )
                    .map((e: MetadataEntityField) => (
                      <Select.Option key={e.fieldName} value={e.fieldName}>
                        {e.displayName}
                      </Select.Option>
                    ))}
                </Select>
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="时间维度" required>
                <Select
                  getPopupContainer={getPopupContainer}
                  value={currentIndicator.timeDimension}
                  onChange={(value) => {
                    setCurrentIndicator((prev: any) => ({ ...prev, timeDimension: value }));
                  }}
                >
                  {currentIndicator.timeField &&
                    fieldList.find((ele) => ele.fieldName === currentIndicator.timeField)?.fieldType ===
                      ENTITY_FIELD_TYPE.DATETIME.VALUE && (
                      <Select.Option value={INDICATOR_TIME_DEMENSION.HOUR}>时</Select.Option>
                    )}
                  <Select.Option value={INDICATOR_TIME_DEMENSION.DAY}>天</Select.Option>
                  <Select.Option value={INDICATOR_TIME_DEMENSION.WEEK}>周</Select.Option>
                  <Select.Option value={INDICATOR_TIME_DEMENSION.MONTH}>月</Select.Option>
                  <Select.Option value={INDICATOR_TIME_DEMENSION.YEAR}>年</Select.Option>
                </Select>
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="计算方式" required>
                <Select
                  getPopupContainer={getPopupContainer}
                  value={currentIndicator.compareCalculate}
                  onChange={(value) => {
                    setCurrentIndicator((prev: any) => ({ ...prev, compareCalculate: value }));
                  }}
                >
                  <Select.Option value={INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE}>环比</Select.Option>

                  {currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.HOUR && (
                    <Select.Option value={INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE_DAY}>日同比</Select.Option>
                  )}

                  {(currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.HOUR ||
                    currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.DAY) && (
                    <Select.Option value={INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE_WEEK}>周同比</Select.Option>
                  )}

                  {currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.DAY && (
                    <Select.Option value={INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE_MONTH}>月同比</Select.Option>
                  )}

                  {(currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.DAY ||
                    currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.WEEK ||
                    currentIndicator.timeDimension === INDICATOR_TIME_DEMENSION.MONTH) && (
                    <Select.Option value={INDICATOR_COMPARE_CALCULATE_METHOD.COMPARE_YEAR}>年同比</Select.Option>
                  )}
                </Select>
              </Form.Item>
            </Grid.Col>
            <Grid.Col span={12}>
              <Form.Item label="计算类型" required>
                <Select
                  getPopupContainer={getPopupContainer}
                  value={currentIndicator.compareCalculateType}
                  onChange={(value) => {
                    setCurrentIndicator((prev: any) => ({ ...prev, compareCalculateType: value }));
                  }}
                >
                  <Select.Option value={INDICATOR_COMPARE_CALCULATE_TYPE.RATE}>差异率</Select.Option>
                  <Select.Option value={INDICATOR_COMPARE_CALCULATE_TYPE.DIFFERENCE}>差值</Select.Option>
                  <Select.Option value={INDICATOR_COMPARE_CALCULATE_TYPE.VALUE}>原始值</Select.Option>
                </Select>
              </Form.Item>
            </Grid.Col>
          </Grid.Row>
        )}

        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item label="显示状态" required>
              <Radio.Group
                type="button"
                value={currentIndicator.status}
                onChange={(value) => {
                  setCurrentIndicator((prev: any) => ({ ...prev, status: value }));
                }}
                style={{ width: '100%', display: 'flex' }}
              >
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={STATUS_VALUES[STATUS_OPTIONS.DEFAULT]}
                >
                  {STATUS_OPTIONS.DEFAULT}
                </Radio>
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={STATUS_VALUES[STATUS_OPTIONS.READONLY]}
                >
                  {STATUS_OPTIONS.READONLY}
                </Radio>
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={STATUS_VALUES[STATUS_OPTIONS.HIDDEN]}
                >
                  {STATUS_OPTIONS.HIDDEN}
                </Radio>
              </Radio.Group>
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item label="卡片宽度">
              <Radio.Group
                type="button"
                value={currentIndicator.width}
                onChange={(value) => {
                  setCurrentIndicator((prev: any) => ({ ...prev, width: value }));
                }}
                style={{ width: '100%', display: 'flex' }}
              >
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={WIDTH_VALUES[WIDTH_OPTIONS.QUARTER]}
                >
                  {WIDTH_OPTIONS.QUARTER}
                </Radio>
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={WIDTH_VALUES[WIDTH_OPTIONS.THIRD]}
                >
                  {WIDTH_OPTIONS.THIRD}
                </Radio>
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={WIDTH_VALUES[WIDTH_OPTIONS.HALF]}
                >
                  {WIDTH_OPTIONS.HALF}
                </Radio>
                <Radio
                  style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}
                  value={WIDTH_VALUES[WIDTH_OPTIONS.FULL]}
                >
                  整行
                </Radio>
              </Radio.Group>
            </Form.Item>
          </Grid.Col>
        </Grid.Row>
      </Modal>
      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </>
  );
};

export default DynamicIndicatorCardConfig;

registerConfigRenderer(CONFIG_TYPES.INDICATOR_CARD_CONFIG, ({ handlePropsChange, item, configs, id }) => (
  <DynamicIndicatorCardConfig handlePropsChange={handlePropsChange} item={item} configs={configs} id={id} />
));

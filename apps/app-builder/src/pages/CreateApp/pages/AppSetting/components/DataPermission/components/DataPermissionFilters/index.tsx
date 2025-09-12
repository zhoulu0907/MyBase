import { Form, Select, Input, Button } from '@arco-design/web-react';
import { IconPlus, IconClose } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { FieldValueType, type AppEntityField, type AuthDataFilterVO, type FilterFieldCheckType } from '@onebase/app';
// import { useState } from 'react';

const FormItem = Form.Item;
const Option = Select.Option;

// 字段值类型
const fieldValueType = [
  {
    label: '静态值',
    value: FieldValueType.static
  },
  {
    label: '变量',
    value: FieldValueType.variable
  }
];

interface DataFilterProps {
  dataFilters: Array<AuthDataFilterVO[]>;
  appEntityFields: AppEntityField[];
  filterFieldCheckType: FilterFieldCheckType[];
  getFieldCheckType: (fieldId: string) => void;
  onChange: (dataFilters: Array<AuthDataFilterVO[]>) => void;
  entitySelected: boolean;
}

const DataFilter = (props: DataFilterProps) => {
  const { dataFilters, appEntityFields, filterFieldCheckType, getFieldCheckType, onChange, entitySelected } = props;

  // const [conditions, setConditions] = useState<AuthDataFilterVO[]>([]);

  const addCondition = () => {};

  const updateDataFilters = (newFilters: Array<AuthDataFilterVO[]>) => {
    onChange(newFilters);
  };

  return (
    <div className={styles.dataPermissionFilters}>
      {dataFilters && dataFilters.length > 0 ? (
        <>
          {dataFilters.map((group, index) => {
            return (
              <div key={index}>
                {index > 0 && <p style={{ margin: '8px 0', color: '#666', fontSize: 14 }}>或者</p>}
                <div className={styles.dataFilter}>
                  {group.map((item, idx) => (
                    <div className={styles.dataFilterItem} key={idx}>
                      <FormItem
                        field={`dataFilters[${index}][${idx}].fieldId`}
                        className={styles.dataFilterItemFieldBox}
                      >
                        <Select
                          placeholder="归档状态"
                          className={styles.dataFilterItemField}
                          onChange={(value) => {
                            console.log('字段 value:', value);
                            getFieldCheckType(value);
                          }}
                        >
                          {appEntityFields
                            .filter((option) => option.fieldID)
                            .map((option) => (
                              <Option key={option.fieldID} value={option.fieldID || ''}>
                                {option.displayName}
                              </Option>
                            ))}
                        </Select>
                      </FormItem>
                      <FormItem
                        field={`dataFilters[${index}][${idx}].fieldOperator`}
                        className={styles.dataFilterItemBox}
                      >
                        <Select placeholder="比较操作" className={styles.dataFilterItem}>
                          {filterFieldCheckType?.map((option) => (
                            <Option key={option.code} value={option.code || ''}>
                              {option.name}
                            </Option>
                          ))}
                        </Select>
                      </FormItem>
                      <FormItem
                        field={`dataFilters[${index}][${idx}].fieldValueType`}
                        className={styles.dataFilterItemBox}
                      >
                        <Select
                          placeholder="字段类型"
                          className={styles.dataFilterItem}
                          onChange={(value) => {
                            // 更新字段值类型
                            const newConditionGroup = [...dataFilters];
                            if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                              // 只更新 fieldValueType 和 fieldValue，保留其他字段
                              newConditionGroup[index][idx] = {
                                ...newConditionGroup[index][idx],
                                fieldValueType: value,
                                fieldValue: undefined
                              };
                              updateDataFilters(newConditionGroup);
                            }
                          }}
                        >
                          {fieldValueType.map((option) => (
                            <Option key={option.value} value={option.value}>
                              {option.label}
                            </Option>
                          ))}
                        </Select>
                      </FormItem>
                      {item.fieldValueType === FieldValueType.static ? (
                        <FormItem
                          field={`dataFilters[${index}][${idx}].fieldValue`}
                          className={styles.dataFilterItemValueBox}
                        >
                          <Input
                            placeholder="请输入值"
                            className={styles.dataFilterItemValue}
                            value={item.fieldValue || ''}
                            onChange={(value) => {
                              // 更新静态值
                              console.log('输入的变量值', value);
                              const newConditionGroup = [...(dataFilters || [])];
                              if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                                newConditionGroup[index][idx].fieldValue = value;
                                updateDataFilters(newConditionGroup);
                              }
                            }}
                          />
                        </FormItem>
                      ) : (
                        <FormItem
                          field={`dataFilters[${index}][${idx}].fieldValue`}
                          className={styles.dataFilterItemValueBox}
                        >
                          <Select
                            placeholder="请选择变量"
                            className={styles.dataFilterItemValue}
                            value={item.fieldValue || undefined}
                            onChange={(value) => {
                              // 更新变量值
                              console.log('现在的变量值:', value);
                              const newConditionGroup = [...(dataFilters || [])];
                              if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                                newConditionGroup[index][idx].fieldValue = value;
                                updateDataFilters(newConditionGroup);
                              }
                            }}
                          >
                            {appEntityFields
                              .filter((option) => option.fieldID)
                              .map((option) => (
                                <Option key={option.fieldID} value={option.fieldName || ''}>
                                  {option.displayName}
                                </Option>
                              ))}
                          </Select>
                        </FormItem>
                      )}
                      <IconClose
                        className={styles.dataFilterItemIcon}
                        onClick={() => {
                          console.log('删除当前条件 dataFilters', dataFilters);
                          // 删除当前条件
                          const newFilters = [...dataFilters];

                          // 确保索引有效
                          if (newFilters[index] && newFilters[index][idx]) {
                            // 删除指定条件
                            newFilters[index].splice(idx, 1);

                            // 如果当前组为空，删除该组
                            if (newFilters[index].length === 0) {
                              newFilters.splice(index, 1);
                            }

                            // 如果所有组都为空，重置为初始状态
                            if (newFilters.length === 0) {
                              updateDataFilters([]);
                            } else {
                              updateDataFilters(newFilters);
                            }
                          }
                        }}
                      />
                    </div>
                  ))}
                  <Button
                    type="outline"
                    size="mini"
                    icon={<IconPlus />}
                    className={styles.dataFilterAndBtn}
                    onClick={() => {
                      console.log('并且按钮 dataFilters', dataFilters);
                      addCondition();
                      // // 并且按钮 添加当前的条件
                      // const newFilters = [...dataFilters];
                      // // 确保索引有效
                      // if (newFilters[index]) {
                      //   // 向当前组添加新条件
                      //   const newCondition: AuthDataFilterVO = {
                      //     conditionGroup: index + 1,
                      //     conditionOrder: newFilters[index].length + 1,
                      //     fieldId: undefined,
                      //     fieldOperator: undefined,
                      //     fieldValue: undefined,
                      //     fieldValueType: undefined,
                      //     id: new Date().getTime().toString()
                      //   };
                      //   console.log('并且按钮 id:', newCondition.id);
                      //   newFilters[index] = [...newFilters[index], newCondition];
                      //   updateDataFilters(newFilters);
                      // }
                    }}
                  >
                    并且
                  </Button>
                </div>
              </div>
            );
          })}
          <Button
            type="outline"
            size="small"
            icon={<IconPlus />}
            onClick={() => {
              console.log('或者按钮 dataFilters:', dataFilters);
              addCondition();
              // updateDataFilters([
              //   ...(dataFilters || []),
              //   [
              //     {
              //       conditionGroup: (dataFilters?.length || 0) + 1,
              //       conditionOrder: 1,
              //       fieldId: undefined,
              //       fieldOperator: undefined,
              //       fieldValue: undefined,
              //       fieldValueType: undefined,
              //       id: new Date().getTime().toString()
              //     }
              //   ]
              // ]);
            }}
          >
            或者
          </Button>
        </>
      ) : (
        <Button
          type="outline"
          icon={<IconPlus />}
          onClick={() => {
            console.log('添加条件组按钮 dataFilters:', dataFilters);
            addCondition();
            // const newCondition = {
            //   conditionGroup: (dataFilters?.length || 0) + 1,
            //   conditionOrder: 1,
            //   fieldId: undefined,
            //   fieldOperator: undefined,
            //   fieldValue: undefined,
            //   fieldValueType: undefined,
            //   id: new Date().getTime().toString()
            // };

            // // 在这里打印新创建的对象的 ID
            // console.log('添加条件组 id:', newCondition.id);

            // updateDataFilters([...(dataFilters || []), [newCondition]]);
          }}
          disabled={!entitySelected}
        >
          添加条件组
        </Button>
      )}
    </div>
  );
};

export default DataFilter;

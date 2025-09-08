import { useEffect, useState } from 'react';
import { Checkbox, Button, Modal, Form, Input, Select } from '@arco-design/web-react';
import { IconPlus, IconClose } from '@arco-design/web-react/icon';
import {
  DataOperationEnum,
  FieldValueType,
  type AppEntity,
  type AppEntityField,
  type AuthDataGroupVO,
  // type AuthDataFilterVO,
  type AuthDataPermissionPersonVO,
  type FilterFieldCheckType,
  type ScopeType
} from '@onebase/app';
import styles from './index.module.less';

const Option = Select.Option;
const FormItem = Form.Item;

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

const conditionData = {
  status: '',
  condition: '',
  value: '',
  fieldId: 0,
  fieldOperator: '',
  fieldValueType: FieldValueType.static,
  fieldValue: ''
};

interface IConditionData {
  status: string;
  condition: string;
  value: string;
  fieldId: number;
  fieldOperator: string;
  fieldValueType: string;
  fieldValue: string;
}

interface IProps {
  // form: any;
  appEntities: AppEntity[];
  appEntityFields: AppEntityField[];
  dataPermissionPerson: AuthDataPermissionPersonVO[];
  filterFieldCheckType: FilterFieldCheckType[];
  status: 'edit' | 'create';
  visible: boolean;
  changeEntity: (params: { entityId: string }) => void;
  getFieldCheckType: (fieldId: string) => void;
  handleModalSubmit: (values: AuthDataGroupVO) => void;
  handleModalCancel: () => void;
}

// 数据权限弹窗
const PermissionModal = (props: IProps) => {
  const {
    appEntities,
    status,
    visible = false,
    appEntityFields,
    dataPermissionPerson,
    filterFieldCheckType,
    changeEntity,
    getFieldCheckType,
    handleModalSubmit,
    handleModalCancel
  } = props;
  const [form] = Form.useForm();

  const [value, setValue] = useState<DataOperationEnum[]>([DataOperationEnum.examine, DataOperationEnum.operate]);
  const [checkAll, setCheckAll] = useState<boolean>(true);
  const [indeterminate, setIndeterminate] = useState<boolean>(false); // 操作权限
  const [conditionGroup, setConditionGroup] = useState<IConditionData[][]>(); // 条件组
  const [entitySelected, setEntitySelected] = useState<boolean>(false);
  const [scopeOwner, setScopeOwner] = useState<string>('');
  const [scopeValue, setScopeValue] = useState<string>('');

  const [scopeInfo, setScopeInfo] = useState<{
    owner: string;
    value: string;
  }>({
    owner: '',
    value: ''
  });
  const dataPermissionScope = [
    { label: '本人', value: 'self' },
    { label: '本人及下属员工', value: 'selfAndSubordinates' },
    { label: '当前员工所在主部门', value: 'mainDepartment' },
    { label: '当前员工所在主部门及下级部门', value: 'mainDepartmentAndSubs' },
    { label: '指定部门', value: 'specifiedDepartment' },
    { label: '指定人员', value: 'specifiedPerson' },
    { label: '当前人员身份信息', value: 'identityInfo' },
    { label: '全部', value: 'all' }
  ];
  // const validateScopeLevel = (rule: any, value: any, callback: any) => {
  //   if (!value || !value.owner || !value.value) {
  //     callback(new Error('请选择权限范围'));
  //   } else {
  //     callback();
  //   }
  // };

  useEffect(() => {
    // 当 scopeOwner 或 scopeValue 变化时，同步更新 scopeInfo
    if (scopeOwner && scopeValue) {
      setScopeInfo({
        owner: scopeOwner,
        value: scopeValue
      });
    }
  }, [scopeOwner, scopeValue]);

  useEffect(() => {
    // 初始化时检查entity字段是否有值
    const entityValue = form.getFieldValue('scopeFieldId');
    setEntitySelected(!!entityValue);
  }, [form]);

  useEffect(() => {
    // 当 visible 状态改变时，如果是关闭模态框，则重置表单
    if (!visible) {
      // 使用 setTimeout 确保在下一个事件循环中执行，避免同步卸载问题
      const timer = setTimeout(() => {
        form.resetFields();
      }, 0);
      return () => clearTimeout(timer);
    }
  }, [visible, form]);

  function onChangeAll(checked: boolean) {
    if (checked) {
      setIndeterminate(false);
      setCheckAll(true);
      setValue([DataOperationEnum.examine, DataOperationEnum.operate]);
    } else {
      setIndeterminate(true);
      setCheckAll(false);
      setValue([DataOperationEnum.examine]);
    }
  }

  const handleOk = () => {
    // 添加调试信息
    console.log('当前表单值:', form.getFieldsValue());
    form
      .validate()
      .then((values: any) => {
        console.log('form values:', values);
        // // 构造要传递给父组件的数据
        // const submitData: AuthDataGroupVO = {
        //   groupName: values.groupName,
        //   description: values.description,
        //   scopeFieldId: Number(values.scopeFieldId),
        //   dataFilters: values.dataFilters,
        //   isOperable: value.includes(DataOperationEnum.operate) ? 1 : 0
        // };
        // // 处理权限范围
        // if (scopeInfo.owner && scopeInfo.value) {
        //   submitData.scopeLevel = {
        //     PersonId: scopeInfo.owner,
        //     scopeType: scopeInfo.value as ScopeType
        //     // assignId:
        //   };
        // }
        // // 调用父组件传递的处理函数
        // handleModalSubmit(submitData);
      })
      .catch((error: any) => {
        console.error('表单验证失败:', error);
        if (error.errors) {
          error.errors.forEach((err: any) => {
            console.error('字段:', err.field, '错误:', err.message);
          });
        }
      });
  };

  // console.log(conditionGroup, 'conditionGroup');

  return (
    <>
      {/* 添加、编辑数据权限组 */}
      <Modal
        title={<div className={styles.dataPermissionModalTitle}>{status === 'create' ? '添加' : '编辑'}数据权限组</div>}
        visible={visible}
        onOk={handleOk}
        onCancel={() => {
          // 使用异步方式关闭模态框，避免同步卸载问题
          setTimeout(() => {
            handleModalCancel();
          }, 0);
        }}
        autoFocus={false}
        focusLock={true}
        okText="创建"
        className={styles.dataPermissionModal}
        // 添加 unmountOnExit 属性确保组件正确卸载
        unmountOnExit={true}
      >
        <Form
          form={form}
          layout="vertical"
          className={styles.dataPermissionForm}
          onValuesChange={(changedValues) => {
            // 当entity字段值发生变化时，更新entitySelected状态
            if (Object.prototype.hasOwnProperty.call(changedValues, 'scopeFieldId')) {
              setEntitySelected(!!changedValues.scopeFieldId);
            }

            // 当权限范围字段变化时，更新对应的state
            if (Object.prototype.hasOwnProperty.call(changedValues, 'scopeOwner')) {
              setScopeOwner(changedValues.scopeOwner);
            }
            if (Object.prototype.hasOwnProperty.call(changedValues, 'scopeValue')) {
              setScopeValue(changedValues.scopeValue);
            }
          }}
        >
          <FormItem field="groupName" label="权限组名称" rules={[{ required: true, message: '请输入权限组名称' }]}>
            <Input placeholder="请输入权限组名称" />
          </FormItem>
          <FormItem field="description" label="说明">
            <Input placeholder="请输入权限组说明" />
          </FormItem>
          <FormItem field="scopeFieldId" label="业务实体" rules={[{ required: true, message: '请选择业务实体' }]}>
            <Select
              placeholder="请选择业务实体"
              onChange={(value) => {
                if (typeof changeEntity === 'function') {
                  changeEntity({ entityId: value });
                }
              }}
            >
              {appEntities
                .filter((appEntity: AppEntity) => appEntity.entityID)
                .map((appEntity: AppEntity) => (
                  <Option key={appEntity.entityID} value={appEntity.entityID || ''}>
                    {appEntity.entityName}
                  </Option>
                ))}
            </Select>
          </FormItem>
          <FormItem field="scopeOwner" label="权限范围" rules={[{ required: true, message: '请选择权限范围' }]}>
            <div className={styles.dataPermissionScope}>
              <Select
                placeholder="选择拥有者"
                className={styles.scopeRoles}
                disabled={!entitySelected}
                onChange={(value) => {
                  setScopeOwner(value);
                  // 同时更新 scopeInfo
                  setScopeInfo((prev) => ({ ...prev, owner: value }));
                }}
              >
                {dataPermissionPerson
                  .filter((option) => option.PersonId)
                  .map((option) => (
                    <Option key={option.PersonId} value={option.fieldName || ''}>
                      {option.displayName}
                    </Option>
                  ))}
              </Select>
              是
              <Select
                placeholder="本人"
                className={styles.scopePerson}
                disabled={!entitySelected}
                onChange={(value) => {
                  setScopeValue(value);
                  // 同时更新 scopeInfo
                  setScopeInfo((prev) => ({ ...prev, value }));
                }}
              >
                {dataPermissionScope.map((option) => (
                  <Option key={option.value} value={option.value}>
                    {option.label}
                  </Option>
                ))}
              </Select>
            </div>
          </FormItem>
          <FormItem field="dataFilters" label="数据过滤">
            <div className={styles.dataPermissionFilters}>
              {conditionGroup && conditionGroup[0]?.length > 0 ? (
                <>
                  {conditionGroup.map((group, index) => {
                    return (
                      <div key={index}>
                        {index > 0 && <p style={{ margin: '8px 0', color: '#666', fontSize: 14 }}>或者</p>}
                        <div key={index} className={styles.dataFilter}>
                          {group.map((item, idx: number) => (
                            <div key={idx} className={styles.dataFilterItem}>
                              <FormItem
                                field={`dataFilters[${index}][${idx}].fieldId`}
                                className={styles.dataFilterItemFieldBox}
                              >
                                <Select
                                  placeholder="归档状态"
                                  className={styles.dataFilterItemField}
                                  onChange={(value) => getFieldCheckType(value)}
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
                                    const newConditionGroup = [...(conditionGroup || [])];
                                    if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                                      newConditionGroup[index][idx].fieldValueType = value;
                                      newConditionGroup[index][idx].fieldValue = ''; // 重置值
                                      setConditionGroup(newConditionGroup);
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
                                      const newConditionGroup = [...(conditionGroup || [])];
                                      if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                                        newConditionGroup[index][idx].fieldValue = value;
                                        setConditionGroup(newConditionGroup);
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
                                      const newConditionGroup = [...(conditionGroup || [])];
                                      if (newConditionGroup[index] && newConditionGroup[index][idx]) {
                                        newConditionGroup[index][idx].fieldValue = value;
                                        setConditionGroup(newConditionGroup);
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
                                  // 删除当前条件
                                  const newGroup = [...(conditionGroup || [])];
                                  if (newGroup[index] && newGroup[index].length > 0) {
                                    newGroup[index].splice(idx, 1);
                                    // 如果当前组为空，则删除该组
                                    if (newGroup[index].length === 0) {
                                      newGroup.splice(index, 1);
                                    }
                                    setConditionGroup(newGroup);
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
                              const newGroup = [...(conditionGroup || [])];
                              if (!newGroup[index]) {
                                newGroup[index] = [];
                              }
                              newGroup[index].push({ ...conditionData });
                              setConditionGroup(newGroup);
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
                    onClick={() => setConditionGroup((pre) => [...(pre || []), [{ ...conditionData }]])}
                  >
                    或者
                  </Button>
                </>
              ) : (
                <Button
                  type="outline"
                  icon={<IconPlus />}
                  onClick={() => setConditionGroup((pre) => [...(pre || []), [{ ...conditionData }]])}
                  disabled={!entitySelected}
                >
                  添加条件组
                </Button>
              )}
            </div>
          </FormItem>
          <FormItem field="isOperable" label="操作权限">
            <div className={styles.dataPermissionOperableBox}>
              <Checkbox onChange={onChangeAll} checked={checkAll} indeterminate={indeterminate}>
                操作权限
              </Checkbox>
            </div>
            <div className={styles.dataPermissionOperable}>
              <Checkbox checked={true} disabled>
                可查看
              </Checkbox>
              <Checkbox
                checked={value.includes(DataOperationEnum.operate)}
                onChange={(checked) => {
                  if (checked) {
                    setValue([DataOperationEnum.examine, DataOperationEnum.operate]);
                  } else {
                    setValue([DataOperationEnum.examine]);
                  }
                  // 更新全选状态
                  setCheckAll(checked);
                  setIndeterminate(!checked); // 当只选中"可查看"时为部分选中状态
                }}
              >
                可操作
              </Checkbox>
            </div>
          </FormItem>
        </Form>
      </Modal>
    </>
  );
};

export default PermissionModal;

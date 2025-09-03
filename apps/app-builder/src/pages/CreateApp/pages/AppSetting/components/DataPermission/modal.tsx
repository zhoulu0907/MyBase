import { useEffect, useState } from 'react';
import { Checkbox, Button, Modal, Form, Input, Select } from '@arco-design/web-react';
import { IconPlus, IconClose } from '@arco-design/web-react/icon';
import { type AppEntity, type AppEntityField, type AuthDataGroupVO } from '@onebase/app';
const Option = Select.Option;
// const CheckboxGroup = Checkbox.Group;
const FormItem = Form.Item;

const options = ['Beijing', 'Shanghai'];

// 字段比较操作符
const fieldCompareOperators = [
  {
    label: '等于',
    value: 'equal'
  },
  {
    label: '不等于',
    value: 'unequal'
  },
  {
    label: '大于',
    value: 'greaterThan'
  },
  {
    label: '小于',
    value: 'lessThan'
  },
  {
    label: '不小于',
    value: 'greaterThanOrEqual'
  },
  {
    label: '不小于',
    value: 'lessThanOrEqual'
  }
];
// 字段值类型
const fieldValueType = [
  {
    label: '静态值',
    value: 'static'
  },
  {
    label: '变量',
    value: 'variable'
  }
];

// const options2 = [
//   {
//     label: '可查看',
//     value: '1'
//   },
//   {
//     label: '可操作',
//     value: '2'
//   }
// ];

const conditionData = {
  status: '',
  condition: '',
  value: '',
  fieldId: 0,
  fieldOperator: 'equal',
  fieldValueType: 'static',
  fieldValue: ''
};

// // 固定的“是”部分选项
// const [secondSelectOptions, setSecondSelectOptions] = useState([
//   { label: '本人', value: 'self' },
//   { label: '本人及下属员工', value: 'selfAndSubordinates' },
//   { label: '当前员工所在主部门', value: 'mainDepartment' },
//   { label: '当前员工所在主部门及下级部门', value: 'mainDepartmentAndSubs' },
//   { label: '指定部门', value: 'specifiedDepartment' },
//   { label: '指定人员', value: 'specifiedPerson' },
//   { label: '当前人员身份信息', value: 'identityInfo' },
//   { label: '全部', value: 'all' }
// ]);

// 系统预设的“人员”字段
// const systemPersonFields = [
//   { label: '拥有者', value: 'owner' },
//   { label: '创建人', value: 'creator' },
//   { label: '更新人', value: 'updater' }
// ];

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
  status: 'edit' | 'create';
  visible: boolean;
  changeEntity: (params: { entityId: string }) => void;
  handleModelSubmit: (values: AuthDataGroupVO) => void;
  handleModelCancel: () => void;
}

// 数据权限弹窗
const PermissionModal = (props: IProps) => {
  const {
    appEntities,
    status,
    visible = false,
    appEntityFields,
    changeEntity,
    handleModelSubmit,
    handleModelCancel
  } = props;
  const [form] = Form.useForm();

  const [value, setValue] = useState<string[]>(['1', '2']);
  const [checkAll, setCheckAll] = useState<boolean>(true);
  const [indeterminate, setIndeterminate] = useState<boolean>(false); // 操作权限
  const [conditionGroup, setConditionGroup] = useState<IConditionData[][]>(); // 条件组
  const [entitySelected, setEntitySelected] = useState<boolean>(false);

  useEffect(() => {
    // 初始化时检查entity字段是否有值
    const entityValue = form.getFieldValue('scopeFieldId');
    setEntitySelected(!!entityValue);
  }, [form]);

  function onChangeAll(checked: boolean) {
    if (checked) {
      setIndeterminate(false);
      setCheckAll(true);
      setValue(['1', '2']);
    } else {
      setIndeterminate(true);
      setCheckAll(false);
      setValue([]);
    }
  }

  const handleOk = () => {
    form
      .validate()
      .then((values: AuthDataGroupVO) => {
        // 可以在这里添加额外的处理逻辑
        handleModelSubmit(values);
      })
      .catch((error) => {
        console.error('表单验证失败:', error);
      });
  };

  // console.log(conditionGroup, 'conditionGroup');

  return (
    <>
      {/* 添加、编辑数据权限组 */}
      <Modal
        title={<div style={{ textAlign: 'left' }}>{status === 'create' ? '添加' : '编辑'}数据权限组</div>}
        visible={visible}
        onOk={handleOk}
        onCancel={handleModelCancel}
        autoFocus={false}
        focusLock={true}
        okText="创建"
        style={{ width: 750 }}
      >
        <Form
          form={form}
          layout="vertical"
          style={{ padding: '0 65px', boxSizing: 'border-box' }}
          onValuesChange={(changedValues) => {
            // 当entity字段值发生变化时，更新entitySelected状态
            if (Object.prototype.hasOwnProperty.call(changedValues, 'scopeFieldId')) {
              setEntitySelected(!!changedValues.scopeFieldId);
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
              {appEntities.map((appEntity: AppEntity) => (
                <Option key={appEntity.entityID} value={appEntity.entityID}>
                  {appEntity.entityName}
                </Option>
              ))}
            </Select>
          </FormItem>
          <FormItem field="scopeLevel" label="权限范围" rules={[{ required: true, message: '请选择权限范围' }]}>
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between'
              }}
            >
              <Select placeholder="拥有者" style={{ width: 150 }} disabled={!entitySelected}>
                {options.map((option, index) => (
                  <Option key={option} disabled={index === 3} value={option}>
                    {option}
                  </Option>
                ))}
              </Select>
              是
              <Select placeholder="本人" style={{ width: 360 }} disabled={!entitySelected}>
                {options.map((option, index) => (
                  <Option key={option} disabled={index === 3} value={option}>
                    {option}
                  </Option>
                ))}
              </Select>
            </div>
          </FormItem>
          <FormItem field="DataFilters" label="数据过滤">
            <div
              style={{
                border: '1px solid #E5E6EB',
                borderRadius: 4,
                padding: 18,
                boxSizing: 'border-box',
                overflow: 'auto',
                maxHeight: 300
              }}
            >
              {conditionGroup && conditionGroup[0]?.length > 0 ? (
                <>
                  {conditionGroup.map((group, index) => {
                    return (
                      <div key={index}>
                        {index > 0 && <p style={{ margin: '8px 0', color: '#666', fontSize: 14 }}>或者</p>}
                        <div
                          key={index}
                          style={{
                            display: 'flex',
                            flexWrap: 'wrap',
                            alignItems: 'center',
                            padding: '9px 18px 9px 9px',
                            boxSizing: 'border-box',
                            borderRadius: 4,
                            background: 'rgb(225 227 231)',
                            marginBottom: 15,
                            position: 'relative'
                          }}
                        >
                          {group.map((item, idx: number) => (
                            <div key={idx} style={{ marginBottom: 8, display: 'flex', alignItems: 'center' }}>
                              <FormItem
                                field={`dataFilters[${index}][${idx}].fieldId`}
                                style={{ marginBottom: 0, width: 112 }}
                              >
                                <Select placeholder="归档状态" style={{ width: 100, marginRight: 12, marginBottom: 8 }}>
                                  {appEntityFields.map((option) => (
                                    <Option key={option.fieldID} value={option.fieldID}>
                                      {option.displayName}
                                    </Option>
                                  ))}
                                </Select>
                              </FormItem>
                              <FormItem
                                field={`dataFilters[${index}][${idx}].fieldOperator`}
                                style={{ marginBottom: 0, width: 112 }}
                              >
                                <Select placeholder="比较操作" style={{ width: 100, marginRight: 12, marginBottom: 8 }}>
                                  {fieldCompareOperators.map((option) => (
                                    <Option key={option.value} value={option.value}>
                                      {option.label}
                                    </Option>
                                  ))}
                                </Select>
                              </FormItem>
                              <FormItem
                                field={`dataFilters[${index}][${idx}].fieldValueType`}
                                style={{ marginBottom: 0, width: 112 }}
                              >
                                <Select
                                  placeholder="字段类型"
                                  style={{ width: 100, marginRight: 12, marginBottom: 8 }}
                                  onChange={(value) => {
                                    // 更新字段值类型
                                    const newConditionGroup = [...conditionGroup];
                                    newConditionGroup[index][idx].fieldValueType = value;
                                    newConditionGroup[index][idx].fieldValue = ''; // 重置值
                                    setConditionGroup(newConditionGroup);
                                  }}
                                >
                                  {fieldValueType.map((option) => (
                                    <Option key={option.value} value={option.value}>
                                      {option.label}
                                    </Option>
                                  ))}
                                </Select>
                              </FormItem>
                              {item.fieldValueType === 'static' ? (
                                <FormItem
                                  field={`dataFilters[${index}][${idx}].fieldValue`}
                                  style={{ marginBottom: 0, width: 152 }}
                                >
                                  <Input
                                    placeholder="请输入值"
                                    style={{ width: 140, marginRight: 12, marginBottom: 8 }}
                                    value={item.fieldValue}
                                    onChange={(value) => {
                                      // 更新静态值
                                      console.log('输入的变量值', value);
                                    }}
                                  />
                                </FormItem>
                              ) : (
                                <FormItem
                                  field={`dataFilters[${index}][${idx}].fieldValue`}
                                  style={{ marginBottom: 0 }}
                                >
                                  <Select
                                    placeholder="请选择变量"
                                    style={{ width: 140, marginRight: 12, marginBottom: 8 }}
                                    // value={item.fieldValue}
                                    onChange={(value) => {
                                      // 更新变量值
                                      console.log('现在的变量值:', value);
                                    }}
                                  >
                                    {appEntityFields.map((option) => (
                                      <Option key={option.fieldID} value={option.fieldName}>
                                        {option.displayName}
                                      </Option>
                                    ))}
                                  </Select>
                                </FormItem>
                              )}
                              <IconClose
                                style={{ cursor: 'pointer', fontSize: 16, paddingBottom: 8 }}
                                onClick={() => {
                                  // 删除当前条件
                                  const newGroup = [...conditionGroup];
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
                            style={{ marginTop: 5 }}
                            onClick={() => {
                              const newGroup = [...conditionGroup];
                              newGroup[index].push(conditionData);
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
                    onClick={() => setConditionGroup((pre) => [...(pre || []), [conditionData]])}
                  >
                    或者
                  </Button>
                </>
              ) : (
                <Button
                  type="outline"
                  icon={<IconPlus />}
                  onClick={() => setConditionGroup((pre) => [...(pre || []), [conditionData]])}
                  disabled={!entitySelected}
                >
                  添加条件组
                </Button>
              )}
            </div>
          </FormItem>
          <FormItem field="isOperable" label="操作权限">
            <div style={{ marginBottom: 20 }}>
              <Checkbox onChange={onChangeAll} checked={checkAll} indeterminate={indeterminate}>
                操作权限
              </Checkbox>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 36 }}>
              <Checkbox checked={true} disabled>
                可查看
              </Checkbox>
              <Checkbox
                checked={value.includes('2')}
                onChange={(checked) => {
                  if (checked) {
                    setValue(['1', '2']);
                  } else {
                    setValue(['1']);
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

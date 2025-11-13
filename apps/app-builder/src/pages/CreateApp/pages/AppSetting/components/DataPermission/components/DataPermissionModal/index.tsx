import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';
import { Button, Checkbox, Form, Input, Modal, Popover, Select, Tag } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { IconEdit } from '@arco-design/web-react/icon';
import {
  getDeptUser,
  type AppEntityField,
  type AuthDataFilterVO,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  type EntityFieldValidationTypes,
  type GetDeptUserReq,
  type MetadataEntityPair,
  type ScopeTypeOption
} from '@onebase/app';
import { AddMembers } from '@onebase/common';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;

interface IProps {
  roleId: string;
  initialFormValues: AuthDataGroupVO;
  modalVisible: boolean;
  status: 'create' | 'edit';
  dataPermissionEntity: MetadataEntityPair | undefined;
  dataPermissionPerson: AuthDataPermissionPersonVO[];
  appEntityFields: AppEntityField[];
  filterFieldCheckType: EntityFieldValidationTypes[];
  variableOptions: TreeSelectDataType[];
  handleModalSubmit: (values: AuthDataGroupVO) => void;
  handleModalCancel: () => void;
}

// 权限范围 Array
const ALLDATA = 'allData';
const CUSTOMCONDITION = 'customCondition';
const PERMISSIONSCOPE_DICT = [
  { value: 'allData', label: '全部数据' },
  { value: 'ownSubmit', label: '本人提交' },
  { value: 'departmentSubmit', label: '本部门提交' },
  { value: 'subDepartmentSubmit', label: '下级部门提交' }
];
const OperationOptions = [
  { value: 'edit', label: '可编辑', disabled: false },
  { value: 'delete', label: '可删除', disabled: false }
];
const dataPermissionScope: ScopeTypeOption[] = [
  { label: '本人', value: 'self' },
  { label: '本人及下属员工', value: 'selfAndSubordinates' },
  { label: '当前员工所在主部门', value: 'mainDepartment' },
  { label: '当前员工所在主部门及下级部门', value: 'mainDepartmentAndSubs' },
  { label: '指定部门', value: 'specifiedDepartment' },
  { label: '指定人员', value: 'specifiedPerson' }
];

const DataPermissionModal = (props: IProps) => {
  const {
    roleId,
    initialFormValues,
    modalVisible,
    status,
    dataPermissionEntity,
    dataPermissionPerson,
    appEntityFields,
    filterFieldCheckType,
    variableOptions,
    handleModalSubmit,
    handleModalCancel
  } = props;

  type Member = {
    key: string;
    name: string;
    department: string;
  };

  const [form] = Form.useForm();
  const Option = Select.Option;

  //权限范围
  const [customChecked, setCustomChecked] = useState(false); //自定义权限
  const [scopeTags, setScopeTags] = useState<any[]>([]);

  const [checkAll, setCheckAll] = useState<boolean>(); // 操作权限
  const [dataFilters, setDataFilters] = useState<Array<AuthDataFilterVO[]>>(initialFormValues.dataFilters || []);
  const [conditionFields, setConditionFields] = useState<any[]>([]);
  const [scopeType, setScopeType] = useState('');

  // 部门用户信息
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [deptData, setDeptData] = useState<Member>();
  const [selectedMembers, setSelectedMembers] = useState<Member[]>([]);

  useEffect(() => {
    if (!modalVisible && !initialFormValues.id) {
      form.resetFields(); // 清空所有字段
    }
  }, [modalVisible]);

  useEffect(() => {
    if (!modalVisible) {
      // 无论新建还是编辑，关闭时都重置
      formReset();
    }
  }, [modalVisible]);
  useEffect(() => {
    // 将 appEntityFields 转换为 ConditionField 格式
    const convertedFields = appEntityFields.map((field) => ({
      title: field.displayName,
      key: field.fieldId,
      fieldType: field.fieldType
    }));
    setConditionFields([
      {
        key: dataPermissionEntity?.entityId,
        title: dataPermissionEntity?.entityName,
        children: convertedFields
      }
    ]);

    if (initialFormValues.scopeLevel) {
      setScopeType(initialFormValues.scopeLevel);
    }

    // 如果 initialFormValues 有 scopeValue，需要将其转换为成员对象数组
    if (initialFormValues.scopeValue && Array.isArray(initialFormValues.scopeValue)) {
      // 对于指定人员/部门的情况，scopeValue可能包含两种格式的数据：
      // 1. 简单的ID字符串数组
      // 2. 包含key/name属性的对象数组

      // 判断是否已经是对象数组格式
      const isFirstItemObject =
        initialFormValues.scopeValue.length > 0 &&
        typeof initialFormValues.scopeValue[0] === 'object' &&
        initialFormValues.scopeValue[0] !== null &&
        'key' in initialFormValues.scopeValue[0];

      if (isFirstItemObject) {
        // 已经是对象数组格式，直接使用
        setSelectedMembers(initialFormValues.scopeValue);
      } else {
        // 是ID字符串数组，需要转换为对象数组
        const memberObjects = initialFormValues.scopeValue.map((id: string) => {
          // 查找是否有匹配的人员信息
          const person = dataPermissionPerson.find((p) => p.PersonId === id);
          // 如果找不到匹配的人员信息，则使用ID作为name
          return {
            key: id,
            name: person ? person.displayName : id
          };
        });
        setSelectedMembers(memberObjects);
      }
    }
    setDataFilters(dataFilters);
    const scopeTags = initialFormValues.scopeTags;
    setScopeTags(scopeTags || []);
    if (scopeTags?.includes(CUSTOMCONDITION)) {
      initialFormValues.customCondition = true;
      setCustomChecked(true);
    }
    resetScope(scopeTags || []);

    setCheckAll(initialFormValues.operationTags?.length == OperationOptions.length);
  }, [appEntityFields, dataFilters, initialFormValues, dataPermissionPerson]);
  // 操作权限 全选反选
  function onChangeAll(checked: boolean) {
    if (checked) {
      // 全选：选中所有操作权限
      const allOperationValues = OperationOptions?.map((option) => option.value) || [];
      form.setFieldValue('operationTags', allOperationValues);
    } else {
      // 取消全选：清空所有操作权限
      form.setFieldValue('operationTags', []);
    }

    setCheckAll(checked);
    form.setFieldValue('isOperable', checked ? 1 : 0);
  }

  const changeOperationPermission = (values: string[]) => {
    if (values.length == OperationOptions.length) {
      setCheckAll(true);
    } else {
      setCheckAll(false);
    }
  };

  const changeScopePermission = (values: string[]) => {
    //切换时去掉error message
    form.setFields({
      scopeTags: {
        error: { message: '' }
      }
    });
    resetScope(values);
  };

  const resetScope = (values: string[]) => {
    const added = values.filter((tag) => !scopeTags.includes(tag));
    if (added && added.includes(ALLDATA)) {
      form.setFieldsValue({
        scopeTags: [ALLDATA],
        scopeFieldId: undefined,
        scopeLevel: undefined,
        scopeValue: undefined,
        customCondition: false
      });
      initialFormValues.scopeLevel = undefined;
      initialFormValues.scopeFieldId = undefined;
      setScopeType('');
      setSelectedMembers([]);
      setCustomChecked(false);
      setScopeTags([ALLDATA]);
    } else {
      const allDataIdx = values.indexOf(ALLDATA);
      if (allDataIdx >= 0) values.splice(allDataIdx, 1);
      form.setFieldValue('scopeTags', values);
      setScopeTags(values);
    }
  };
  // 权限范围选择指定人员/部门
  // 'specifiedDepartment' | 'specifiedPerson' 指定部门/人员
  const specifiedModalVisible = async () => {
    await getDeptUsers({});
    setMembersVisible(true);
  };
  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setMemberLoading(true);
    try {
      const params: GetDeptUserReq = {
        roleId: roleId,
        deptId,
        keywords
      };
      const res = await getDeptUser(params);
      setDeptData(res);
    } catch (error) {
      console.error('获取部门用户信息 error:', error);
    } finally {
      setMemberLoading(false);
    }
  };

  // 展开下级
  const handleExpand = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getDeptUsers({ keywords: value });
    }, 500),
    []
  );

  // 添加成员/部门
  const handleAddScope = async (scopeSpecified: Member[]) => {
    console.log('scopeSpecified', scopeSpecified);
    // 更新已选择的成员状态
    setSelectedMembers(scopeSpecified);
    form.setFieldValue('scopeValue', scopeSpecified);
    // 关闭弹窗
    setMembersVisible(false);
  };

  const handleTagClose = (id: string) => {
    // 从 selectedMembers 中移除指定ID的成员
    const newSelectedMembers = selectedMembers.filter((member) => member.key !== id);
    setSelectedMembers(newSelectedMembers);

    // 同时更新scopeValue字段
    const newScopeValue = newSelectedMembers.map((member) => member.key);
    form.setFieldValue('scopeValue', newScopeValue);
  };

  const handleUpdateSelectedMembers = (members: Member[]) => {
    console.log('members', members);
    setSelectedMembers(members);

    // 同时更新scopeValue字段
    const ids = members.map((item) => item.key);
    form.setFieldValue('scopeValue', ids);
  };

  const handleCustomChecked = (value: boolean) => {
    setCustomChecked(value);
    if (value) {
      const tags = form.getFieldValue('scopeTags');
      const allDataIdx = tags.indexOf(ALLDATA);
      if (allDataIdx >= 0) tags.splice(allDataIdx, 1);
      tags.push(CUSTOMCONDITION);
      form.setFieldsValue({
        scopeTags: tags,
        scopeFieldId: dataPermissionPerson[0].PersonId
      });
      setScopeTags(tags);
    } else {
      const tags = form.getFieldValue('scopeTags');
      tags.splice(tags.indexOf(CUSTOMCONDITION), 1);
      form.setFieldValue('scopeTags', tags);
      setScopeTags(tags);
    }
  };

  // 重置表单
  const formReset = () => {
    form.resetFields();
    setScopeType('');
    setDataFilters([]);
    setSelectedMembers([]);
    setCheckAll(false);
    setCustomChecked(false);
  };

  const handleOk = async () => {
    try {
      const values = await form.validate();
      // scope 表单验证
      const formValues = await form.getFieldsValue();
      // 如果开启自定义范围，先检查基础配置是否完整
      if (formValues.customCondition) {
        if (!formValues.scopeFieldId || !formValues.scopeLevel) {
          form.setFields({
            scopeTags: {
              error: { message: '自定义权限范围时请配置条件' }
            }
          });
          return;
        }

        // 根据 scopeLevel 检查 selectedMembers（不同级别可有不同提示）
        const levelMsgMap: Record<string, string> = {
          specifiedPerson: '自定义范围时请选择人员',
          specifiedDepartment: '自定义范围时请选择部门'
        };

        const needListCheck = !!levelMsgMap[formValues.scopeLevel];
        if (needListCheck && (!Array.isArray(selectedMembers) || selectedMembers.length === 0)) {
          form.setFields({
            scopeTags: {
              error: { message: levelMsgMap[formValues.scopeLevel] }
            }
          });
          return;
        }
      }

      console.log('提交数据 values:', values);
      handleModalSubmit(values);
      handleModalCancel();
    } catch (error) {
      console.log('提交数据失败 error:', error);
    }
  };

  const handleCancel = () => {
    formReset();
    handleModalCancel();
  };

  return (
    <>
      <Modal
        style={{ width: '850px' }}
        className={styles.dataPermissionModal}
        title={<div className={styles.dataPermissionModalTitle}>{status === 'create' ? '添加' : '编辑'}数据权限组</div>}
        visible={modalVisible}
        autoFocus={false}
        focusLock={true}
        okText={status === 'create' ? '创建' : '编辑'}
        onOk={handleOk}
        onCancel={handleCancel}
        unmountOnExit={true}
      >
        <Form
          key={String(modalVisible)}
          form={form}
          initialValues={initialFormValues}
          layout="vertical"
          className={styles.dataPermissionForm}
        >
          <FormItem field="groupName" label="权限组名称" rules={[{ required: true, message: '请输入权限组名称' }]}>
            <Input placeholder="请输入权限组名称" maxLength={40} />
          </FormItem>
          <FormItem field="description" label="说明">
            <Input placeholder="请输入权限组说明" maxLength={200} />
          </FormItem>
          <FormItem label="权限范围" field="scopeTags" rules={[{ required: true, message: '至少设置一个权限范围' }]}>
            <div className={styles.dataPermissionScope}>
              <FormItem field="scopeTags" noStyle>
                <CheckboxGroup options={PERMISSIONSCOPE_DICT} onChange={(values) => changeScopePermission(values)} />
              </FormItem>
              <div className={styles.scopeRow}>
                <FormItem field="customCondition" noStyle>
                  <Checkbox checked={customChecked} onChange={(checked: boolean) => handleCustomChecked(checked)}>
                    自定义条件
                  </Checkbox>
                </FormItem>
                {customChecked && (
                  <div className={styles.selfRow}>
                    <FormItem field="scopeFieldId" className={styles.scopeRoles}>
                      <Select
                        placeholder="请选择"
                        onChange={(value) => {
                          console.log('选择拥有者 value:', value);
                        }}
                      >
                        {dataPermissionPerson
                          .filter((option) => option.PersonId)
                          .map((option) => (
                            <Option key={option.PersonId} value={option.PersonId || ''}>
                              {option.displayName}
                            </Option>
                          ))}
                      </Select>
                    </FormItem>
                    是
                    <FormItem field="scopeLevel" className={styles.scopePerson}>
                      <Select
                        placeholder="请选择"
                        onChange={(value) => {
                          setScopeType(value);
                          setSelectedMembers([]);
                          // 同时更新scopeValue字段
                          form.setFieldValue('scopeValue', value);
                        }}
                      >
                        {dataPermissionScope.map((option) => (
                          <Option key={option.value} value={option.value} title={option.label}>
                            {option.label}
                          </Option>
                        ))}
                      </Select>
                    </FormItem>
                    {(scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment') && (
                      <div className={styles.scopeAssign}>
                        <FormItem field="scopeValue" noStyle style={{ pointerEvents: 'none' }}>
                          {(scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment') &&
                          selectedMembers &&
                          selectedMembers.length > 0 ? (
                            <div className={styles.assignIdTag}>
                              <div className={styles.tagContainer}>
                                {selectedMembers.slice(0, 2).map((member) => (
                                  <Tag
                                    title={member.name}
                                    className={styles.tag}
                                    key={member.key}
                                    closable
                                    onClose={(e) => {
                                      e.preventDefault();
                                      handleTagClose(member.key);
                                    }}
                                  >
                                    <span>{member.name}</span>
                                  </Tag>
                                ))}
                                {selectedMembers.length - 2 > 0 && (
                                  <Popover
                                    trigger="hover"
                                    content={selectedMembers.slice(2).map((member) => (
                                      <div>{member.name}</div>
                                    ))}
                                  >
                                    <Tag>+{selectedMembers.length - 2}</Tag>
                                  </Popover>
                                )}
                              </div>
                              <IconEdit className={styles.tagBtn} onClick={() => specifiedModalVisible()}></IconEdit>
                            </div>
                          ) : (
                            <Button type="outline" style={{ width: '100%' }} onClick={() => specifiedModalVisible()}>
                              {scopeType === 'specifiedPerson' ? '添加人员' : '添加部门'}
                            </Button>
                          )}
                        </FormItem>
                      </div>
                    )}
                    {/* {(scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment') && (
                      <div className={styles.scopeAssign}>
                        <FormItem field="scopeValue" noStyle>
                          {(scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment') &&
                          selectedMembers &&
                          selectedMembers.length > 0 ? (
                            <div className={styles.assignIdTag}>
                              <div className={styles.tagContainer}>
                                {selectedMembers.map((member) => (
                                  <Tag
                                    className={styles.tag}
                                    key={member.key}
                                    closable
                                    onClose={(e) => {
                                      e.preventDefault();
                                      handleTagClose(member.key);
                                    }}
                                  >
                                    <span>{member.name}</span>
                                  </Tag>
                                ))}
                              </div>
                              <IconEdit className={styles.tagBtn} onClick={() => specifiedModalVisible()}></IconEdit>
                            </div>
                          ) : (
                            <Button type="primary" style={{ width: '100%' }} onClick={() => specifiedModalVisible()}>
                              {scopeType === 'specifiedPerson' ? '添加人员' : '添加部门'}
                            </Button>
                          )}
                        </FormItem>
                      </div>
                    )} */}
                  </div>
                )}
              </div>
            </div>
          </FormItem>
          {/* 数据过滤 */}
          <ConditionEditor
            nodeId=""
            form={form}
            label="数据过滤"
            required={false}
            fields={conditionFields}
            entityFieldValidationTypes={filterFieldCheckType}
            variableOptions={variableOptions}
          />
          <FormItem field="isOperable" noStyle />
          <FormItem label="操作权限">
            <div className={styles.dataPermissionOperableBox}>
              <Checkbox onChange={onChangeAll} checked={checkAll} indeterminate={!checkAll}>
                操作权限
              </Checkbox>
            </div>
            <div>
              <Checkbox checked={true} disabled className={styles.dataPermissionOperable}>
                可查看
              </Checkbox>
              <FormItem field="operationTags" noStyle>
                <CheckboxGroup options={OperationOptions} onChange={(values) => changeOperationPermission(values)} />
              </FormItem>
            </div>
          </FormItem>
        </Form>
      </Modal>
      <AddMembers
        visible={membersVisible}
        title={scopeType}
        data={deptData}
        loading={memberLoading}
        selectedMembers={selectedMembers}
        onExpand={handleExpand}
        onSearch={debouncedUpdate}
        onConfirm={handleAddScope}
        onUpdateSelectedMembers={handleUpdateSelectedMembers}
        onCancel={() => setMembersVisible(false)}
      ></AddMembers>
    </>
  );
};

export default DataPermissionModal;

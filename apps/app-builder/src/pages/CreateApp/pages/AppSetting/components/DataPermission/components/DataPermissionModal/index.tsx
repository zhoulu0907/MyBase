import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';
import { Button, Checkbox, Form, Input, Modal, Select, Tag } from '@arco-design/web-react';
import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import { IconEdit } from '@arco-design/web-react/icon';
import {
  // DataOperationEnum,
  getDeptUser,
  IsOperable,
  // type AppEntity,
  type AppEntityField,
  type AuthDataFilterVO,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  // type Condition,
  type ConditionField,
  type EntityFieldValidationTypes,
  // type FilterFieldCheckType,
  type GetDeptUserReq,
  type ScopeTypeOption
  // type RoleAddUserReq
} from '@onebase/app';
import { AddMembers } from '@onebase/common';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import styles from './index.module.less';

const FormItem = Form.Item;

interface IProps {
  roleId: string;
  initialFormValues: AuthDataGroupVO;
  modalVisible: boolean;
  status: 'create' | 'edit';
  // appEntity: AppEntity;
  dataPermissionEntityName: string;
  dataPermissionPerson: AuthDataPermissionPersonVO[];
  appEntityFields: AppEntityField[];
  filterFieldCheckType: EntityFieldValidationTypes[];
  dataPermissionScope: ScopeTypeOption[];
  variableOptions: TreeSelectDataType[];
  handleModalSubmit: (values: AuthDataGroupVO) => void;
  handleModalCancel: () => void;
  // changeEntity: (params: { entityId: string }) => void;
}

const DataPermissionModal = (props: IProps) => {
  const {
    roleId,
    initialFormValues,
    modalVisible,
    status,
    // appEntity,
    dataPermissionEntityName,
    dataPermissionPerson,
    appEntityFields,
    filterFieldCheckType,
    dataPermissionScope,
    variableOptions,
    handleModalSubmit,
    handleModalCancel
  } = props;

  const [form] = Form.useForm();
  const Option = Select.Option;

  // const [entitySelected, setEntitySelected] = useState<boolean>(false);
  const [checkAll, setCheckAll] = useState<boolean>(!!initialFormValues.isOperable); // 操作权限
  const [dataFilters, setDataFilters] = useState<Array<AuthDataFilterVO[]>>(initialFormValues.dataFilters || []);
  // const [conditionData, setConditionData] = useState<Condition[]>([]);
  const [conditionFields, setConditionFields] = useState<ConditionField[]>([]);
  const [scopeType, setScopeType] = useState('');

  // 部门用户信息
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [deptData, setDeptData] = useState<any>();
  const [selectedMembers, setSelectedMembers] = useState<any[]>([]);

  useEffect(() => {
    if (!modalVisible && !initialFormValues.id) {
      form.resetFields(); // 清空所有字段
    }
  }, [modalVisible]);

  useEffect(() => {
    // 将 appEntityFields 转换为 ConditionField 格式
    const convertedFields = appEntityFields.map((field) => ({
      label: field.displayName,
      value: field.fieldId,
      fieldType: field.fieldType
    }));
    setConditionFields(convertedFields);

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
  }, [appEntityFields, dataFilters, initialFormValues, dataPermissionPerson]);
  // 操作权限 全选反选
  function onChangeAll(checked: boolean) {
    setCheckAll(checked);
    form.setFieldValue('isOperable', checked ? 1 : 0);
  }

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
  const handleAddScope = async (scopeSpecified: any[]) => {
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

  const handleUpdateSelectedMembers = (members: any[]) => {
    setSelectedMembers(members);

    // 同时更新scopeValue字段
    const ids = members.map((item) => item.key);
    form.setFieldValue('scopeValue', ids);
  };

  // 重置表单
  const formReset = () => {
    form.resetFields();
    setScopeType('');
    setDataFilters([]);
    setSelectedMembers([]);
    setCheckAll(true);
  };

  const handleOk = async () => {
    try {
      const values = await form.validate();
      console.log('提交数据 values:', values);
      values.isOperable = checkAll ? IsOperable.allowed : IsOperable.notAllowed;
      handleModalSubmit(values);
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
        <Form form={form} initialValues={initialFormValues} layout="vertical" className={styles.dataPermissionForm}>
          <FormItem field="groupName" label="权限组名称" rules={[{ required: true, message: '请输入权限组名称' }]}>
            <Input placeholder="请输入权限组名称" />
          </FormItem>
          <FormItem field="description" label="说明">
            <Input placeholder="请输入权限组说明" />
          </FormItem>
          <FormItem label="业务实体" rules={[{ required: true, message: '请选择业务实体' }]}>
            <Input value={dataPermissionEntityName} readOnly />
          </FormItem>
          <FormItem label="权限范围" rules={[{ required: true, message: '请选择权限范围' }]}>
            <div className={styles.dataPermissionScope}>
              <div className={styles.scopeRow}>
                <FormItem
                  field="scopeFieldId"
                  className={styles.scopeRoles}
                  rules={[{ required: true, message: '请选择权限范围' }]}
                >
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
                <FormItem
                  field="scopeLevel"
                  className={styles.scopePerson}
                  rules={[{ required: true, message: '请选择权限范围' }]}
                >
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
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                </FormItem>
              </div>
              {(scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment') && (
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
              )}
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
            <div className={styles.dataPermissionOperable}>
              <Checkbox checked={true} disabled>
                可查看
              </Checkbox>
              <Checkbox checked={checkAll} onChange={onChangeAll}>
                可操作
              </Checkbox>
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

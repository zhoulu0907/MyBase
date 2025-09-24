import { Modal, Form, Input, Select, Checkbox, Button, Tag } from '@arco-design/web-react';
import { IconEdit } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import {
  DataOperationEnum,
  getDeptUser,
  type AppEntity,
  type AppEntityField,
  type AuthDataFilterVO,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  // type FilterFieldCheckType,
  type GetDeptUserReq,
  type Condition,
  type ConfitionField,
  type EntityFieldValidationTypes,
  type ScopeTypeOption
  // type RoleAddUserReq
} from '@onebase/app';
import { AddMembers } from '@onebase/common';
import { debounce } from 'lodash-es';
import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';
import { useCallback, useEffect, useState } from 'react';

const FormItem = Form.Item;

interface IProps {
  roleId: string;
  initialFormValues: AuthDataGroupVO;
  modalVisible: boolean;
  status: 'create' | 'edit';
  appEntity: AppEntity;
  dataPermissionPerson: AuthDataPermissionPersonVO[];
  appEntityFields: AppEntityField[];
  filterFieldCheckType: EntityFieldValidationTypes[];
  dataPermissionScope: ScopeTypeOption[];
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
    appEntity,
    dataPermissionPerson,
    appEntityFields,
    filterFieldCheckType,
    dataPermissionScope,
    handleModalSubmit,
    handleModalCancel
  } = props;
  const [operatePermission, setOperatePermission] = useState<DataOperationEnum[]>([
    DataOperationEnum.examine,
    DataOperationEnum.operate
  ]); // 操作权限 是否禁用

  const [form] = Form.useForm();
  const Option = Select.Option;

  // const [entitySelected, setEntitySelected] = useState<boolean>(false);
  const [checkAll, setCheckAll] = useState<boolean>(true);
  const [indeterminate, setIndeterminate] = useState<boolean>(false); // 操作权限
  const [dataFilters, setDataFilters] = useState<Array<AuthDataFilterVO[]>>(initialFormValues.dataFilters || []);
  const [conditionData, setConditionData] = useState<Condition[]>([]);
  const [conditionFields, setConditionFields] = useState<ConfitionField[]>([]);
  const [scopeType, setScopeType] = useState('');

  // 部门用户信息
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [deptData, setDeptData] = useState<any>();
  const [selectedMembers, setSelectedMembers] = useState<any[]>(initialFormValues.scopeValue || []);

  useEffect(() => {
    // 将 appEntityFields 转换为 ConditionField 格式
    const convertedFields = appEntityFields.map((field) => ({
      label: field.displayName,
      value: field.fieldId,
      fieldType: field.fieldType
    }));
    setConditionFields(convertedFields);

    setDataFilters(dataFilters);
  }, [appEntityFields, dataFilters]);
  // 操作权限 全选反选
  function onChangeAll(checked: boolean) {
    if (checked) {
      setIndeterminate(false);
      setCheckAll(true);
      setOperatePermission([DataOperationEnum.examine, DataOperationEnum.operate]);
    } else {
      setIndeterminate(true);
      setCheckAll(false);
      setOperatePermission([DataOperationEnum.examine]);
    }
  }

  // // 数据过滤
  // const changeDataFilters = (value: any[]) => {
  //   setDataFilters(dataFilters);
  //   // 同时更新表单字段值
  //   form.setFieldValue('dataFilters', value);
  // };

  // 权限范围选择指定人员/部门
  // 'specifiedDepartment' | 'specifiedPerson' 指定部门/人员
  const specifiedModalVisible = async (scopeType: 'specifiedDepartment' | 'specifiedPerson') => {
    console.log('权限范围 指定弹窗 scopeType:', scopeType);
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
      console.log('获取部门用户信息 resq', res);
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
    console.log('添加成员 userIds:', scopeSpecified);
    // 更新已选择的成员状态
    setSelectedMembers(scopeSpecified);

    console.log('setSelectedMembers:', scopeSpecified);
    // 提取ID数组并设置为scopeValue
    const ids = scopeSpecified.map((item) => item.key);
    form.setFieldValue('scopeValue', ids);
    // 关闭弹窗
    setMembersVisible(false);
  };

  const handleTagClose = (id: string) => {
    console.log('handleTagClose id:', id);
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
    setOperatePermission([DataOperationEnum.examine, DataOperationEnum.operate]);
    setCheckAll(true);
    setIndeterminate(false);
  };

  const handleOk = async () => {
    try {
      const values = await form.validate();
      console.log('提交数据 values:', values);
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
        okText="创建"
        onOk={handleOk}
        onCancel={handleCancel}
        unmountOnExit={true}
      >
        <Form
          form={form}
          initialValues={initialFormValues}
          layout="vertical"
          className={styles.dataPermissionForm}
          onValuesChange={(changedValues) => {
            console.log(`Form ${Object.keys(changedValues)} changeValues:`, changedValues);
          }}
        >
          <FormItem field="groupName" label="权限组名称" rules={[{ required: true, message: '请输入权限组名称' }]}>
            <Input placeholder="请输入权限组名称" />
          </FormItem>
          <FormItem field="description" label="说明">
            <Input placeholder="请输入权限组说明" />
          </FormItem>
          <FormItem label="业务实体" rules={[{ required: true, message: '请选择业务实体' }]}>
            <Input value={appEntity?.entityName} readOnly />
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
                    placeholder="选择拥有者"
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
                    placeholder="本人"
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
                    {selectedMembers && selectedMembers.length > 0 ? (
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
                        <IconEdit className={styles.tagBtn} onClick={() => specifiedModalVisible(scopeType)}></IconEdit>
                      </div>
                    ) : (
                      <Button type="primary" style={{ width: '100%' }} onClick={() => specifiedModalVisible(scopeType)}>
                        {scopeType === 'specifiedPerson' ? '添加人员' : '添加部门'}
                      </Button>
                    )}
                  </FormItem>
                </div>
              )}
            </div>
          </FormItem>
          {/* 数据过滤 */}
          {/* <FormItem field="dataFilters" label="数据过滤"> */}
          <ConditionEditor
            form={form}
            label="数据过滤"
            required={true}
            // data={conditionData}
            fields={conditionFields}
            entityFieldValidationTypes={filterFieldCheckType}
            // onChange={changeDataFilters}
          />
          {/* </FormItem> */}
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
                checked={operatePermission.includes(DataOperationEnum.operate)}
                onChange={(checked) => {
                  if (checked) {
                    setOperatePermission([DataOperationEnum.examine, DataOperationEnum.operate]);
                  } else {
                    setOperatePermission([DataOperationEnum.examine]);
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

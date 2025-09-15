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
  type FilterFieldCheckType,
  type GetDeptUserReq
  // type RoleAddUserReq
} from '@onebase/app';
import { useState, useEffect, useCallback } from 'react';
import { AddMembers } from '@onebase/common';
import { debounce } from 'lodash-es';
// import DataFilters from '../DataPermissionFilters';
import ConditionEditor from '@/pages/CreateApp/pages/IntegratedManagement/triggerEditor/components/condition-editor';

const FormItem = Form.Item;

interface IProps {
  roleId: string;
  initialFormValues: AuthDataGroupVO;
  modalVisible: boolean;
  status: 'create' | 'edit';
  appEntities: AppEntity[];
  dataPermissionPerson: AuthDataPermissionPersonVO[];
  appEntityFields: AppEntityField[];
  filterFieldCheckType: FilterFieldCheckType[];
  changeEntity: (params: { entityId: string }) => void;
  getFieldCheckType: (fieldId: string) => void;
  handleModalSubmit: (values: AuthDataGroupVO) => void;
  handleModalCancel: () => void;
}

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

const DataPermissionModal = (props: IProps) => {
  const {
    roleId,
    initialFormValues,
    modalVisible,
    status,
    appEntities,
    dataPermissionPerson,
    appEntityFields,
    filterFieldCheckType,
    changeEntity,
    getFieldCheckType,
    handleModalSubmit,
    handleModalCancel
  } = props;
  const [operatePermission, setOperatePermission] = useState<DataOperationEnum[]>([
    DataOperationEnum.examine,
    DataOperationEnum.operate
  ]); // 操作权限 是否禁用

  const [form] = Form.useForm();
  const Option = Select.Option;

  const [entitySelected, setEntitySelected] = useState<boolean>(false);
  const [checkAll, setCheckAll] = useState<boolean>(true);
  const [indeterminate, setIndeterminate] = useState<boolean>(false); // 操作权限
  const [dataFilters, setDataFilters] = useState<Array<AuthDataFilterVO[]>>(initialFormValues.dataFilters || []);
  const [scopeType, setScopeType] = useState('');

  // 部门用户信息
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [deptData, setDeptData] = useState<any>();
  const [selectedMembers, setSelectedMembers] = useState<any[]>(initialFormValues.scopeLevel?.assignIds || []);

  useEffect(() => {
    // 初始化时检查entity字段是否有值
    const entityValue = form.getFieldValue('scopeFieldId');
    setEntitySelected(!!entityValue);
  }, [selectedMembers, scopeType, form]);
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

  // 数据过滤
  const changeDataFilters = (value: AuthDataFilterVO[]) => {
    console.log('数据过滤 changeDataFilters', value);
    // setDataFilters(value)
  };

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
    // 提取ID数组
    const ids = scopeSpecified.map((item) => item.key);
    // 设置表单字段值
    form.setFieldValue('scopeLevel.assignIds', ids);
    // 获取当前scopeLevel的值
    const currentScopeLevel = form.getFieldsValue().scopeLevel || {};
    // 合并更新scopeLevel对象，保留现有属性
    form.setFieldsValue({
      scopeLevel: {
        ...currentScopeLevel,
        assignIds: ids
      }
    });
    // 关闭弹窗
    setMembersVisible(false);
  };

  const handleTagClose = (id: string) => {
    console.log('handleTagClose id:', id);
    // 从 selectedMembers 中移除指定ID的成员
    const newSelectedMembers = selectedMembers.filter((member) => member.key !== id);
    setSelectedMembers(newSelectedMembers);

    // 同时更新表单字段值
    const currentAssignIds = form.getFieldValue('scopeLevel.assignIds') || [];
    const newAssignIds = currentAssignIds.filter((assignIds: string) => assignIds !== id);
    form.setFieldValue('scopeLevel.assignIds', newAssignIds);
  };

  const handleUpdateSelectedMembers = (members: any[]) => {
    setSelectedMembers(members);

    // 同时更新表单字段值
    const ids = members.map((item) => item.key);
    form.setFieldValue('scopeLevel.assignIds', ids);

    // 获取当前scopeLevel的值
    const currentScopeLevel = form.getFieldsValue().scopeLevel || {};
    // 合并更新scopeLevel对象，保留现有属性
    form.setFieldsValue({
      scopeLevel: {
        ...currentScopeLevel,
        assignIds: ids
      }
    });
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
            // console.log(`Form scopeType changeValues:`, form.getFieldValue('scopeType'));
            // 当scopeFieldId字段值发生变化时，更新entitySelected状态
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
                console.log('业务实体 change value:', value);
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
          <FormItem label="权限范围" rules={[{ required: true, message: '请选择权限范围' }]}>
            <div className={styles.dataPermissionScope}>
              <div className={styles.scopeRow}>
                <FormItem
                  field="scopeLevel.personId"
                  className={styles.scopeRoles}
                  rules={[{ required: true, message: '请选择权限范围' }]}
                >
                  <Select
                    placeholder="选择拥有者"
                    disabled={!entitySelected}
                    onChange={(value) => {
                      console.log('选择拥有者 value:', value);
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
                </FormItem>
                是
                <FormItem
                  field="scopeLevel.scopeType"
                  className={styles.scopePerson}
                  rules={[{ required: true, message: '请选择权限范围' }]}
                >
                  <Select
                    placeholder="本人"
                    disabled={!entitySelected}
                    onChange={(value) => {
                      setScopeType(value);
                      setSelectedMembers([]);
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
                  <FormItem
                    field="scopeLevel.assignIds"
                    noStyle
                    // rules={[
                    //   {
                    //     required: scopeType === 'specifiedPerson' || scopeType === 'specifiedDepartment' ? true : false,
                    //     message:
                    //       scopeType === 'specifiedPerson'
                    //         ? '请添加指定人员'
                    //         : scopeType === 'specifiedDepartment'
                    //           ? '请添加指定部门'
                    //           : '请选择权限范围'
                    //   }
                    // ]}
                  >
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
                              {/* <Avatar size={16}>
                                {scopeType === 'specifiedPerson' ? member.name?.slice(0, 1) || 'U' : '部'}
                              </Avatar> */}
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
          <FormItem field="dataFilters" label="数据过滤">
            <div className={styles.dataPermissionFilters}>
              <ConditionEditor
                data={dataFilters}
                fields={appEntityFields}
                entityFieldValidationTypes={filterFieldCheckType}
                onChange={() => changeDataFilters}
              />
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

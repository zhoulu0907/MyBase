import { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { Radio, Form, Select } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { type ApproverConfig } from '../constant';
import { userType } from '../../../constants';
import { getUserPage, type PageParam } from '@onebase/platform-center';
import { getAppIdByPageSetId, listRole, type ListRoleReq } from '@onebase/app';
import { useAppStore } from '@/store/store_app';
import { PUBLISH_MODULE } from '@onebase/common';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const userMaxCount = 100;
const roleMaxCount = 10;
const enum UserOptsEnum {
  Initial = 0,
  Ready = 1,
  Done = 2
}
const SimpleMode = ({ setApprovalConfigData, approverConfig }: ApproverConfig) => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';

  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [roleOptions, setRoleOptions] = useState<any[]>([]);
  const [simpleCkType, setSimpleCkType] = useState<string>(approverConfig?.handlerType || 'user');
  const [form] = Form.useForm();
  const [selectedUser, setSelectedUser] = useState<string[]>([]);
  const [selectedRole, setSelectedRole] = useState<string[]>([]);
  const { curAppInfo } = useAppStore();
  const [userNoPower, setUserNoPower] = useState<boolean>(false);
  // 标记获取userOptions列表数据的接口已经执行完成
  const [userOptsStatus, setUserOptsStatus] = useState<number>(UserOptsEnum.Initial);

  const handleChangeUser = (val: string[]) => {
    if (val.length <= userMaxCount) {
      setSelectedUser(val);
    }
  };
  const handleChangeRole = (val: string[]) => {
    if (val.length <= roleMaxCount) {
      setSelectedRole(val);
    }
  };
  // 校验规则
  const approverFormRules = {
    user: [{ required: true, message: '请选择审批人' }],
    role: [{ required: true, message: '请选择角色' }]
  };
  const [formRes, setFormRes] = useState<any>({});

  const prevUserIdsRef = useRef<any>();

  function initUserData() {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100,
      userType: userType.INNER
    };
    if (curAppInfo.publishModel && curAppInfo.publishModel === PUBLISH_MODULE.SASS) {
      params.userType = userType.SAAS;
    }
    setUserOptsStatus(UserOptsEnum.Ready);
    getUserPage(params)
      .then((res: any) => {
        if (userNoPower) {
          setUserNoPower(false);
        }
        if (Array.isArray(res?.list)) {
          const selectArr: any[] = [];
          res.list?.forEach((item: any) => {
            selectArr.push({
              userId: item.id,
              name: item.nickname
            });
          });
          setUserOptions(selectArr);
        }
        setUserOptsStatus(UserOptsEnum.Done);
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
        if (typeof err === 'string' && err.indexOf('没有该操作权限') > -1) {
          setUserNoPower(true);
        }
        setUserOptsStatus(UserOptsEnum.Done);
      });
  }
  function initRoleData() {
    if (!pageSetId) {
      console.error('Api getAppIdByPageSetId param is Error.');
      return;
    }
    getAppIdByPageSetId({ pageSetId })
      .then((appId: any) => {
        if (appId) {
          const params: ListRoleReq = {
            applicationId: appId
          };
          listRole(params)
            .then((roleRes: any) => {
              if (Array.isArray(roleRes)) {
                const selectArr: any[] = [];
                roleRes?.forEach((item: any) => {
                  selectArr.push({
                    roleId: item.id,
                    roleName: item.roleName
                  });
                });
                setRoleOptions(selectArr);
              }
            })
            .catch((err: any) => {
              console.error('Api listRole Error:', err);
            });
        }
      })
      .catch((err: any) => {
        console.error('Api getAppIdByPageSetId Error:', err);
      });
  }

  function changeSimpleType(val: string) {
    setSimpleCkType(val);
    form.clearFields();
    setFormRes({});
  }

  useEffect(() => {
    let selOptions: any[] = [];
    let itemKey = '';
    let dataKey = simpleCkType === 'user' ? 'users' : 'roles'; // 动态决定数据键名
    if (simpleCkType === 'user') {
      selOptions = userOptions;
      itemKey = 'userId';
    } else if (simpleCkType === 'role') {
      selOptions = roleOptions;
      itemKey = 'roleId';
    }
    const obj: any = {
      handlerType: simpleCkType
    };
    if (selOptions?.length > 0) {
      obj[dataKey] = selOptions.filter((item: any) => {
        if (Array.isArray(formRes[simpleCkType])) {
          return formRes[simpleCkType].indexOf(item[itemKey]) > -1;
        }
        return false;
      });
    }
    setApprovalConfigData('approverConfig', obj);
  }, [simpleCkType, formRes]);

  function needFormFill(configList: any, formList: any, idField = 'userId') {
    if (configList?.length !== formList?.length) {
      return true;
    }
    const configIds = configList?.map((item: any) => item[idField]);
    const isSame =
      configIds.length === formList.length &&
      configIds.every((id: any) => formList.includes(id)) &&
      formList.every((id: any) => configIds.includes(id));
    return !isSame;
  }
  useEffect(() => {
    if (approverConfig?.handlerType && userOptsStatus === 2) {
      const configMap = {
        role: { key: 'roles', formField: 'role', idField: 'roleId' },
        user: { key: 'users', formField: 'user', idField: 'userId' }
      } as const;
      const config = configMap[approverConfig?.handlerType as keyof typeof configMap];
      const dataArray = config ? approverConfig?.[config.key] : undefined;
      if (dataArray && dataArray.length > 0) {
        const formData = form.getFieldsValue([config.formField]);
        const isChange = needFormFill(dataArray, formData?.[config.formField], config.idField);
        if (isChange) {
          setInitData();
        }
      }
    }
  }, [approverConfig, userOptsStatus]);

  useEffect(() => {
    initUserData();
    initRoleData();
  }, []);

  const setInitData = () => {
    const { handlerType, users = [], roles = [] } = approverConfig;
    if (handlerType) {
      setSimpleCkType(handlerType);
      if (handlerType === 'user') {
        let userArr = users.map((item: any) => item.userId);
        // 如果审批人下拉列表有数据，需要进行过滤，把不存在于列表的项，删除
        if (userOptions?.length > 0) {
          const listUserIds = userOptions.map((item: any) => item.userId);
          userArr = userArr.filter((uid: any) => {
            return listUserIds.indexOf(uid) > -1;
          });
        }
        prevUserIdsRef.current = userArr;
        form.setFieldsValue({
          user: userArr
        });
      } else if (handlerType === 'role') {
        form.setFieldsValue({
          role: roles.map((item: any) => item.roleId)
        });
      }
    }
  };

  return (
    <>
      <RadioGroup className={styles.approverRadioGroup} value={simpleCkType} onChange={changeSimpleType}>
        <Radio value="user">指定成员</Radio>
        <Radio value="role">
          指定角色
          <IconQuestionCircle />
        </Radio>
        <Radio value="deptManager" disabled>
          部门负责人
        </Radio>
        <Radio value="multistageManager" disabled>
          多级主管
        </Radio>
        <Radio value="directManager" disabled>
          直属主管
        </Radio>
        <Radio value="deptContact" disabled>
          部门接口人
        </Radio>
        <Radio value="initiator" disabled>
          发起人本人
        </Radio>
        <Radio value="initiatorChoice" disabled>
          发起人自选
        </Radio>
        <Radio value="formMember" disabled>
          表单内成员字段
        </Radio>
      </RadioGroup>
      <div className={styles.configTitle}></div>
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
        onValuesChange={() => {
          setFormRes(form.getFieldsValue());
        }}
      >
        {simpleCkType === 'user' && (
          <FormItem
            className={styles.approverForm}
            label="选择审批人"
            field="user"
            rules={approverFormRules.user}
            wrapperCol={{ style: { width: '100%' } }}
          >
            {!userNoPower ? (
              <Select
                mode="multiple"
                placeholder="选择审批人"
                value={selectedUser}
                onChange={handleChangeUser}
                filterOption={(inputValue, option) =>
                  option.props.children?.toLowerCase().indexOf(inputValue?.toLowerCase()) >= 0
                }
                allowClear
              >
                {userOptions?.map((option: any) => (
                  <Option
                    key={option?.userId}
                    value={option?.userId}
                    disabled={selectedUser.length === userMaxCount && !selectedUser.includes(option.userId)}
                  >
                    {option.name}
                  </Option>
                ))}
              </Select>
            ) : (
              <Select disabled mode="multiple" placeholder="选择审批人" defaultValue={['no_power']}>
                <Option value="no_power">无权限</Option>
              </Select>
            )}
          </FormItem>
        )}
        {simpleCkType === 'role' && (
          <FormItem
            className={styles.approverForm}
            label="选择角色"
            field="role"
            rules={approverFormRules.role}
            wrapperCol={{ style: { width: '100%' } }}
          >
            <Select
              mode="multiple"
              placeholder="选择角色"
              value={selectedRole}
              onChange={handleChangeRole}
              filterOption={(inputValue, option) =>
                option.props.children?.toLowerCase().indexOf(inputValue?.toLowerCase()) >= 0
              }
              allowClear
            >
              {roleOptions?.map((option: any) => (
                <Option
                  key={option?.roleId}
                  value={option?.roleId}
                  disabled={selectedRole.length === roleMaxCount && !selectedRole.includes(option.roleId)}
                >
                  {option.roleName}
                </Option>
              ))}
            </Select>
          </FormItem>
        )}
      </Form>
    </>
  );
};

export default SimpleMode;

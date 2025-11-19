import { useState, useEffect, useRef } from 'react';
import { useLocation } from 'react-router-dom';
import { Radio, Form, Select } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import { getUserPage, type PageParam } from '@onebase/platform-center';
import { getAppIdByPageSetId, listRole, type ListRoleReq } from '@onebase/app';
import styles from './index.module.less';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const userMaxCount = 100;
const roleMaxCount = 10;
const SimpleMode = ({ setCcRecipientsConfigData, copyReceiverConfig }) => {
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';

  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [roleOptions, setRoleOptions] = useState<any[]>([]);
  const [simpleCkType, setSimpleCkType] = useState<string>(copyReceiverConfig?.handlerType || 'user');
  const [form] = Form.useForm();
  const [selectedUser, setSelectedUser] = useState<string[]>([]);
  const [selectedRole, setSelectedRole] = useState<string[]>([]);
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
  
  const ccRecipientsFormRules = {
    user: [{ required: true, message: '请选择审批人' }],
    role: [{ required: true, message: '请选择角色' }]
  };
  const [formRes, setFormRes] = useState<any>({});

  const prevUserIdsRef = useRef<any>();

  function initUserData() {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100
    };
    getUserPage(params)
      .then((res: any) => {
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
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
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
    setCcRecipientsConfigData('copyReceiverConfig', obj);
  }, [simpleCkType, formRes]);

  useEffect(() => {
    let curUserList: any = copyReceiverConfig?.users
    let prevUIdsList: any = prevUserIdsRef?.current;
    let isChange = false;
    if (prevUIdsList?.length === curUserList?.length) {
      for (let u = 0; u < curUserList?.length; u++) {
        if (curUserList[u]?.userId && prevUIdsList.indexOf(curUserList[u]?.userId) < 0) {
          isChange = true;
          break;
        }
      }
    } else {
      isChange = true;
    }
    if (isChange) {
      setInitData();
    }
  }, [copyReceiverConfig?.users]);

  useEffect(() => {
    initUserData();
    initRoleData();
  }, []);

  const setInitData = () => {
    const { handlerType, users = [], roles = [] } = copyReceiverConfig;
    if (handlerType) {
      setSimpleCkType(handlerType);
      if (handlerType === 'user') {
        const userArr = users.map((item: any) => item.userId);
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
      <RadioGroup className={styles.ccResRadioGroup} value={simpleCkType} onChange={changeSimpleType}>
        <Radio value="user">指定成员</Radio>
        <Radio value="role">
          指定角色
          <IconQuestionCircle />
        </Radio>
        <Radio value="deptManager" disabled>
          部门主管
        </Radio>
        <Radio value="deptContact" disabled>
          部门接口人
        </Radio>
        <Radio value="initiator" disabled>
          发起人本人
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
            label="选择抄送人"
            field="user"
            rules={ccRecipientsFormRules.user}
            wrapperCol={{ style: { width: '100%' } }}
          >
            <Select
              mode="multiple"
              placeholder="选择抄送人"
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
          </FormItem>
        )}
        {simpleCkType === 'role' && (
          <FormItem
            className={styles.approverForm}
            label="选择角色"
            field="role"
            rules={ccRecipientsFormRules.role}
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

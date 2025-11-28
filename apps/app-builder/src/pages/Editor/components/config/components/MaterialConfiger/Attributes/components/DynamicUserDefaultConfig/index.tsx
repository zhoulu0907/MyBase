import React, { useCallback, useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES, getPopupContainer } from '@onebase/ui-kit';
import { FormulaEditor } from '@/components/FormulaEditor';
import { Button, Form, Select, Switch } from '@arco-design/web-react';
import { getSimpleUserPage } from '@onebase/platform-center';
import { debounce } from 'lodash-es';
import { AddMembers } from '@onebase/common';
import { getDeptUser, type AuthRoleUsersPageRespVO, type DeptAndUsersRespDTO, type GetDeptUserReq } from '@onebase/app';

export interface DynamicUserDefaultConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const FormItem = Form.Item;
const Option = Select.Option;

const DEFAULTUSERVALUE = 'defaultUserValue';
const ISSELECTSCOPE = 'isSelectScope';
const SELECTSCOPE = 'selectScope';

const DynamicUserDefaultConfig: React.FC<DynamicUserDefaultConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  //   const [defaultValueMode, setDefaultValueMode] = useState(configs[item.key]);
  const [defaultUserValue, setDefaultUserValue] = useState<string | undefined>(configs[DEFAULTUSERVALUE]);
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  const [formulaData, setFormulaData] = useState<string>('');

  const [userData, setUserData] = useState<any[]>([]);
  // 分页
  const [pageNo, setPageNo] = useState<number>(1);
  const [total, setTotal] = useState<number | string>(0);
  // 搜索条件
  const [keywords, setKeywords] = useState<string>('');
  // 是否加载中
  const [fetching, setFetching] = useState<boolean>(false);

  //选择范围
  const [showButton, setShowButton] = useState(configs[ISSELECTSCOPE]);
  const [membersVisible, setMembersVisible] = useState<boolean>(false);
  const [memberData, setMemberData] = useState<DeptAndUsersRespDTO>();
  const [memberLoading, setMemberLoading] = useState<boolean>(false);
  const [selectedMembers, setSelectedMembers] = useState<any[]>(configs[SELECTSCOPE] || []);

  useEffect(() => {
    configs[DEFAULTUSERVALUE];
    if (selectedMembers?.length > 0) {
      formatUserScope(selectedMembers);
      setTotal(0);
      setPageNo(1);
      setKeywords('');
    } else {
      getUserData('');
    }
  }, [selectedMembers]);

  const debouncedSearch = useCallback(
    debounce((value) => {
      if (selectedMembers.length === 0) {
        getUserData(value);
      }
    }, 500),
    []
  );

  // 滚动加载
  const scrollHandler = async (element: HTMLDivElement) => {
    const { scrollTop, scrollHeight, clientHeight } = element;
    const scrollBottom = scrollHeight - (scrollTop + clientHeight);

    if (scrollBottom < 10 && !fetching && Number(total) > userData.length) {
      setFetching(true);
      const param = {
        pageNo: pageNo + 1,
        pageSize: 20,
        keywords: keywords
      };
      const { list, total } = await getSimpleUserPage(param);
      setPageNo(pageNo + 1);
      setTotal(total);
      setUserData((prev) => [...prev, ...list]);
      setFetching(false);
    }
  };

  const getUserData = async (inputValue: string) => {
    setFetching(true);
    setKeywords(inputValue);
    const param = {
      pageNo: 1,
      pageSize: 20,
      keywords: inputValue
    };
    const { list, total } = await getSimpleUserPage(param);
    setPageNo(1);
    setTotal(total);
    setUserData(list || []);
    setFetching(false);
  };

  const handleFormulaConfirm = (formulaData: any, formattedFormula: string, params: any) => {
    setFormulaVisible(false);
    // form.setFieldValue(
    //   formulaFieldKey,
    //   {formulaData: formulaData, formula: formattedFormula, parameters: params}
    // );
    setFormulaData('');
    // setFormulaFieldKey('');
  };

  const handleBtnSwitch = (checked: boolean) => {
    if (checked) {
      //   getDeptUsers({});
    }
    setShowButton(checked);
    handlePropsChange(ISSELECTSCOPE, checked);
  };

  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setMemberLoading(true);
    try {
      //   if (!roleInfo?.id) return;
      const params: GetDeptUserReq = {
        roleId: '37775560235057153', //TODO
        deptId,
        keywords
      };
      const res = await getDeptUser(params);
      console.log('获取部门用户信息 res:', res);
      setMemberData(res);
    } catch (error) {
      console.error('获取部门用户信息失败 error:', error);
    } finally {
      setMemberLoading(false);
    }
  };

  const handleMembersVisible = async () => {
    await getDeptUsers({});
    setMembersVisible(true);
  };

  // 展开下级
  const handleExpand = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  // 添加成员/部门
  const handleAddMembers = async (scopeSpecified: any[]) => {
    console.log('scopeSpecified', scopeSpecified);
    // 更新已选择的成员状态
    formatUserScope(scopeSpecified);
    setSelectedMembers(scopeSpecified);
    handlePropsChange(SELECTSCOPE, scopeSpecified);
    // 关闭弹窗
    setMembersVisible(false);
  };

  const formatUserScope = (members: any[]) => {
    if (members.length > 0) {
      const selectMembers = members.map((member) => ({
        id: member.key,
        // deptName: member.deptName
        nickname: member.name,
        email: member.email
      }));
      setUserData(selectMembers);
    }
  };

  const handleUpdateSelectedMembers = useCallback((depts: AuthRoleUsersPageRespVO[]) => {
    setSelectedMembers(depts);
  }, []);

  useEffect(() => {
    return () => debouncedSearch.cancel();
  }, [debouncedSearch]);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'默认值'} style={{ marginBottom: '8px' }}>
        <Select defaultValue={configs[item.key]} onChange={(value) => handlePropsChange(item.key, value)}>
          <Select.Option value="custom">自定义</Select.Option>
          <Select.Option value="formula">公式计算</Select.Option>
        </Select>
      </FormItem>
      {configs[item.key] === 'custom' ? (
        <FormItem>
          <Select
            placeholder="请选择"
            allowClear
            showSearch={true}
            filterOption={
              selectedMembers.length > 0
                ? (input, option) => String(option?.props?.children ?? '').includes(input)
                : false // 远程搜索时不做本地过滤
            }
            defaultValue={configs[DEFAULTUSERVALUE]}
            onSearch={debouncedSearch}
            onPopupScroll={scrollHandler}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handlePropsChange(DEFAULTUSERVALUE, value)}
          >
            {userData.map((option) => (
              <Option key={option.id} value={option.id}>
                {option.nickname}
              </Option>
            ))}
          </Select>
        </FormItem>
      ) : (
        <FormItem>
          <Button long onClick={() => setFormulaVisible(true)}>
            ƒx 编辑公式
          </Button>
        </FormItem>
      )}

      {/* 选择范围 */}
      <FormItem
        style={{ marginBottom: '8px' }}
        layout="horizontal"
        labelAlign="left"
        labelCol={{
          span: 21
        }}
        wrapperCol={{
          span: 1
        }}
        label={'可选范围'}
      >
        <Switch size="small" checked={showButton} onChange={(checked) => handleBtnSwitch(checked)} />
      </FormItem>
      {showButton && (
        <FormItem>
          <Button long onClick={() => handleMembersVisible()}>
            {selectedMembers.length > 0 ? '已配置可选范围' : '设置'}
          </Button>
        </FormItem>
      )}

      <FormulaEditor
        initialFormula={formulaData}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />

      <AddMembers
        visible={membersVisible}
        data={memberData}
        title={'specifiedPerson'}
        loading={memberLoading}
        selectedMembers={selectedMembers || []}
        selectMemberCanEmpty={true}
        onExpand={handleExpand}
        onSearch={debouncedSearch}
        onConfirm={handleAddMembers}
        onUpdateSelectedMembers={handleUpdateSelectedMembers}
        onCancel={() => {
          setMembersVisible(false);
        }}
      />
    </>
  );
};

export default DynamicUserDefaultConfig;

registerConfigRenderer(CONFIG_TYPES.USER_DEFAULT_VALUE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicUserDefaultConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

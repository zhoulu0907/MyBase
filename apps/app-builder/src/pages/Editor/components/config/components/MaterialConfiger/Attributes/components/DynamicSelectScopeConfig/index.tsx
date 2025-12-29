import { Button, Form, Switch } from '@arco-design/web-react';
import { type AuthRoleUsersPageRespVO, type DeptAndUsersRespDTO } from '@onebase/app';
import { AddMembers } from '@onebase/common';
import { getDeptUser, type GetDeptUserReq } from '@onebase/platform-center';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { debounce } from 'lodash-es';
import { useCallback, useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';

export interface DynamicSelectScopeConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  handleMultiPropsChange: (updates: { key: string; value: any }[]) => void;
  item: any;
  configs: any;
  id: string;
}

const FormItem = Form.Item;

const DynamicSelectScopeConfig: React.FC<DynamicSelectScopeConfigProps> = ({
  handlePropsChange,
  handleMultiPropsChange,
  item,
  configs,
  id
}) => {
  const [showButton, setShowButton] = useState(configs['isSelectScope']);
  const [deptsVisible, setDeptsVisible] = useState<boolean>(false);
  const [deptData, setDeptData] = useState<DeptAndUsersRespDTO>();
  const [deptLoading, setDeptLoading] = useState<boolean>(false);
  const [selectedDepts, setSelectedDepts] = useState<AuthRoleUsersPageRespVO[]>(configs[item.key] || []);

  useEffect(() => {
    if (showButton === true || deptsVisible) {
      getDeptUsers({});
    }
  }, [deptsVisible]);

  const handleBtnSwitch = (checked: boolean) => {
    setShowButton(checked);

    if (!checked) {
      getDeptUsers({});
      setSelectedDepts([]);
      handleMultiPropsChange?.([
        { key: 'isSelectScope', value: checked },
        { key: item.key, value: [] }
      ]);
    } else {
      handlePropsChange('isSelectScope', checked);
    }
  };

  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    setDeptLoading(true);
    try {
      const params: GetDeptUserReq = {
        deptId,
        keywords
      };
      const res = await getDeptUser(params);
      setDeptData(res);
    } catch (error) {
      console.error('获取部门用户信息失败 error:', error);
    } finally {
      setDeptLoading(false);
    }
  };

  // 展开下级
  const handleExpand = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  // 添加成员/部门
  const handleAddDept = async (scopeSpecified: AuthRoleUsersPageRespVO[]) => {
    console.log('scopeSpecified', scopeSpecified);
    // 更新已选择的成员状态
    setSelectedDepts(scopeSpecified);
    handlePropsChange(item.key, scopeSpecified);
    // 关闭弹窗
    setDeptsVisible(false);
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getDeptUsers({ keywords: value });
    }, 500),
    []
  );

  useEffect(() => {
    return () => debouncedUpdate.cancel();
  }, [debouncedUpdate]);

  const handleUpdateSelectedDepts = useCallback((depts: AuthRoleUsersPageRespVO[]) => {
    setSelectedDepts(depts);
  }, []);

  return (
    <>
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
          <Button long onClick={() => setDeptsVisible(true)}>
            {selectedDepts.length > 0 ? '已配置可选范围' : '设置'}
          </Button>
        </FormItem>
      )}

      <AddMembers
        visible={deptsVisible}
        data={deptData}
        title={'specifiedDepartment'}
        loading={deptLoading}
        selectedMembers={selectedDepts || []}
        selectMemberCanEmpty={true}
        onExpand={handleExpand}
        onSearch={debouncedUpdate}
        onConfirm={handleAddDept}
        onUpdateSelectedMembers={handleUpdateSelectedDepts}
        onCancel={() => {
          setDeptsVisible(false);
          setSelectedDepts([]);
        }}
      />
    </>
  );
};

export default DynamicSelectScopeConfig;

registerConfigRenderer(
  CONFIG_TYPES.DEPT_SELECT_SCOPE,
  ({ id, handlePropsChange, handleMultiPropsChange, item, configs }) => (
    <DynamicSelectScopeConfig
      id={id}
      handlePropsChange={handlePropsChange}
      handleMultiPropsChange={handleMultiPropsChange}
      item={item}
      configs={configs}
    />
  )
);

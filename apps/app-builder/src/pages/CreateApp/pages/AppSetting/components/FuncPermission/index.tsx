import { useState, useEffect, type FC } from 'react';
import { Radio, Checkbox, Form } from '@arco-design/web-react';
import {
  getFuncPermission,
  updatePagePermission,
  updateOperationPermission,
  updateViewPermission,
  FunPermissionViewVisit,
  FunOperationPermission,
  FunViewPermission,
  FunViewCustomPermission,
  type AuthOperationVO,
  type GetPermissionReq,
  type FuncPermissionResponse,
  type UpdatePagePermissionReq,
  type UpdateOperationPermissionReq,
  type AuthViewVO,
  type UpdateViewPermissionReq
} from '@onebase/app';
import styles from './index.module.less';
import { IconEmpty } from '@arco-design/web-react/icon';

const RadioGroup = Radio.Group;
const CheckboxGroup = Checkbox.Group;

interface IProps {
  appId: string;
  menuId: string;
  roleId: string;
}

// 操作权限 Array
const PERMISSION_DICT = [
  { value: 'create', label: '新增' },
  { value: 'edit', label: '编辑' },
  { value: 'delete', label: '删除' },
  { value: 'import', label: '导入' },
  { value: 'export', label: '导出' },
  { value: 'share', label: '分享' }
];

// 功能权限
const FuncPermission: FC<IProps> = ({ appId, menuId, roleId }: IProps) => {
  const [form] = Form.useForm();
  const isPageAllowed = Form.useWatch('isPageAllowed', form);
  const isAllViewsAllowed = Form.useWatch('isAllViewsAllowed', form); // 监听字段变化

  const [operationOptions, setOperationOptions] = useState<any[]>();
  const [funcPermission, setFuncPermission] = useState<FuncPermissionResponse>(); // 功能权限

  // 视图权限
  const [viewPermissionOptions, setViewPermissionOptions] = useState<any[]>();
  const [isCustomAllViewsAllowed, setIsCustomAllViewsAllowed] = useState<boolean>(false); // 默认所有视图权限
  const [viewPermIndeterminate, setViewPermIndeterminate] = useState<boolean>(false);

  useEffect(() => {
    if (appId && menuId && roleId) {
      getApplicationPermission();
    }
  }, [appId, menuId, roleId]);

  useEffect(() => {
    if (funcPermission) {
      const authViews = funcPermission.authViewVO.authViews ?? [];
      const viewOptions: Array<any> = [];
      const viewDefaultChecked: Array<number | undefined> = [];
      authViews.forEach((view: any) => {
        viewOptions.push({
          label: view.viewDisplayName,
          value: view.viewUuid,
          isAllowed: view.isAllowed
        });

        if (view.isAllowed === FunPermissionViewVisit.canVisit) {
          viewDefaultChecked.push(view.viewUuid);
        }
      });
      setViewPermIndeterminate(!!(viewDefaultChecked.length && viewDefaultChecked.length !== authViews.length));
      setIsCustomAllViewsAllowed(viewDefaultChecked.length === authViews.length);
      setViewPermissionOptions(viewOptions);
      // const viewOptions = funcPermission.authViewVO.authViews?.map((item: AuthViewVO) => ({
      //   label: item.viewDisplayName,
      //   value: item.viewId,
      //   isAllowed: item.isAllowed
      // }));
      // setViewPermissionOptions(viewOptions);
      // const viewDefaultChecked = funcPermission.authViewVO.authViews
      //   ?.filter((item: AuthViewVO) => item.isAllowed === FunPermissionViewVisit.canVisit)
      //   .map((item: AuthViewVO) => item.viewId);
      form.setFieldsValue({
        isPageAllowed: funcPermission.isPageAllowed,
        authOperations: funcPermission.authOperationTags,
        isAllViewsAllowed: funcPermission.authViewVO.isAllViewsAllowed,
        authViews: viewDefaultChecked,
        authViewVO: funcPermission.authViewVO.authViews
      });
    }
  }, [funcPermission]);

  /* 获取权限信息 */
  const getApplicationPermission = async () => {
    const params: GetPermissionReq = {
      applicationId: appId,
      menuId,
      roleId
    };
    const res = await getFuncPermission(params);
    setFuncPermission(res);
  };

  const changeViewVisitPermission = async (val: FunPermissionViewVisit.canVisit | FunPermissionViewVisit.notVisit) => {
    const params: UpdatePagePermissionReq = {
      isPageAllowed: val,
      permissionReq: {
        applicationId: appId,
        menuId,
        roleId
      }
    };
    try {
      await updatePagePermission(params);
      // 立即更新表单值，确保 UI 显示正确
      form.setFieldsValue({ isPageAllowed: val });
      // 可选：重新拉取完整权限数据以保持一致性
      await getApplicationPermission();
    } catch (error) {
      console.error('更新失败:', error);
      // 回滚表单值
      form.setFieldsValue({ isPageAllowed: !val });
    }
  };

  const changeOperationPermission = async (values: any) => {
    const params: UpdateOperationPermissionReq = {
      operationTags: values,
      permissionReq: {
        applicationId: appId,
        menuId,
        roleId
      }
    };
    await updateOperationPermission(params);
    await getApplicationPermission();
  };

  const changeViewPermission = (value: any) => {
    if (value) {
      form.setFieldValue('isAllViewsAllowed', FunViewPermission.AllViewVisitAllowed);
      // const allViewIds = viewPermissionOptions?.map((option) => option.value) || [];
      form.setFieldValue('authViews', []);
      udateViewPermission();
    } else {
      form.setFieldValue('isAllViewsAllowed', FunViewPermission.ViewCustomFieldPermission);
    }
  };

  const changeAllViewPermission = (checked: any) => {
    // 实现全选/取消全选逻辑
    setIsCustomAllViewsAllowed(checked);
    setViewPermIndeterminate(false);
    if (checked) {
      // 全选：选中所有视图权限
      const allViewIds = viewPermissionOptions?.map((option) => option.value) || [];
      form.setFieldValue('authViews', allViewIds);
    } else {
      // 取消全选：清空所有视图权限
      form.setFieldValue('authViews', []);
    }
    udateViewPermission();
  };

  const handleViewPermissionChange = (values: any) => {
    setIsCustomAllViewsAllowed(viewPermissionOptions?.length === values.length);
    setViewPermIndeterminate(!!(values.length && viewPermissionOptions?.length !== values.length));
    form.setFieldValue('authViews', values);
    udateViewPermission();
  };

  const udateViewPermission = async () => {
    const authViews = form.getFieldValue('authViews');
    const isAllViewsAllowed = form.getFieldValue('isAllViewsAllowed');
    const params: UpdateViewPermissionReq = {
      permissionReq: {
        applicationId: appId,
        menuId,
        roleId
      },
      isAllViewsAllowed: isAllViewsAllowed
    };

    if (!isAllViewsAllowed) {
      const viewPermission = viewPermissionOptions?.map((item: any) => ({
        viewDisplayName: item.label,
        viewUuid: item.value,
        isAllowed: authViews.includes(item.value)
          ? FunViewCustomPermission.canViewAllowed
          : FunViewCustomPermission.notViewAllowed
      }));
      params.authViews = viewPermission;
    }
    await updateViewPermission(params);
  };

  return (
    <>
      {menuId && (
        <Form form={form}>
          <div className={styles.formItem}>
            <div className={styles.itemHeader}>
              <div className={styles.left}>页面权限</div>
              <div className={styles.right}>{funcPermission?.isPageAllowed ? '可访问' : '无权限'}</div>
            </div>

            <div className={styles.itemContent}>
              <Form.Item field="isPageAllowed" noStyle>
                <RadioGroup type="button" name="lang" onChange={(values) => changeViewVisitPermission(values)}>
                  <Radio value={FunPermissionViewVisit.canVisit}>可访问</Radio>
                  <Radio value={FunPermissionViewVisit.notVisit}>无权限</Radio>
                </RadioGroup>
              </Form.Item>
            </div>
          </div>

          <div className={styles.formItem}>
            <div className={styles.itemHeader}>
              <div className={styles.left}>操作权限</div>
              <div className={styles.right}>
                {PERMISSION_DICT.every((op) => funcPermission?.authOperationTags?.includes(op.value)) && '全部可操作'}
              </div>
            </div>
            <div className={styles.itemContent}>
              <Form.Item field="authOperations" noStyle>
                <CheckboxGroup
                  disabled={!isPageAllowed}
                  options={PERMISSION_DICT}
                  onChange={(values) => changeOperationPermission(values)}
                />
              </Form.Item>
            </div>
          </div>

          {/*  视图权限 */}
          <div className={styles.formItem}>
            <div className={styles.itemHeader}>
              <div className={styles.left}>视图权限</div>
              <div className={styles.right}>{isAllViewsAllowed ? '全部可访问' : '部分可访问'}</div>
            </div>
            <div className={styles.viewItemContent}>
              <Form.Item field="isAllViewsAllowed" noStyle>
                <RadioGroup
                  disabled={!isPageAllowed}
                  direction="vertical"
                  onChange={(value) => changeViewPermission(value)}
                >
                  <Radio value={FunViewPermission.AllViewVisitAllowed}>默认所有视图均可访问</Radio>
                  <Radio value={FunViewPermission.ViewCustomFieldPermission}>自定义权限</Radio>
                </RadioGroup>
              </Form.Item>
              {isAllViewsAllowed === FunViewPermission.ViewCustomFieldPermission && (
                <div className={styles.checkboxGroup}>
                  <Checkbox
                    style={{ marginBottom: '5px' }}
                    checked={isCustomAllViewsAllowed}
                    indeterminate={viewPermIndeterminate}
                    onChange={(value) => changeAllViewPermission(value)}
                  >
                    <span>全选</span>
                  </Checkbox>
                  <Form.Item field="authViews" noStyle>
                    <CheckboxGroup
                      direction="vertical"
                      options={viewPermissionOptions || []}
                      onChange={(values) => {
                        handleViewPermissionChange(values);
                      }}
                    />
                  </Form.Item>
                </div>
              )}
            </div>
          </div>
        </Form>
      )}
    </>
  );
};

export default FuncPermission;

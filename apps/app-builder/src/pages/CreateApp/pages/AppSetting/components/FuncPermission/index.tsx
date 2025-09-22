import { useState, useEffect, type FC } from 'react';
import { Radio, Checkbox, Form } from '@arco-design/web-react';
import {
  getFuncPermission,
  updatePagePermission,
  updateOperationPermission,
  FunPermissionViewVisit,
  FunOperationPermission,
  FunViewPermission,
  type AuthOperationVO,
  type GetPermissionReq,
  type FuncPermissionResponse,
  type UpdatePagePermissionReq,
  type UpdateOperationPermissionReq,
  type AuthViewVO
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

// 功能权限
const FuncPermission: FC<IProps> = ({ appId, menuId, roleId }: IProps) => {
  const [form] = Form.useForm();
  const isPageAllowed = Form.useWatch('isPageAllowed', form);
  const isAllViewsAllowed = Form.useWatch('isAllViewsAllowed', form); // 监听字段变化

  const [operationOptions, setOperationOptions] = useState<any[]>();
  const [funcPermission, setFuncPermission] = useState<FuncPermissionResponse>(); // 功能权限
  const [viewPermissionOptions, setViewPermissionOptions] = useState<any[]>();
  const [isCustomAllViewsAllowed, setIsCustomAllViewsAllowed] = useState<boolean>(true);
  // 自定义权限是否全选

  useEffect(() => {
    if (appId && menuId && roleId) {
      getApplicationPermission();
    }
  }, [appId, menuId, roleId]);

  useEffect(() => {
    if (funcPermission) {
      // 组装 options 操作权限
      const operationOptions = funcPermission.authOperations.map((item: AuthOperationVO) => ({
        label: item.displayName,
        value: item.operationCode
      }));
      const viewOptions = funcPermission.authEntity.authViews?.map((item: AuthViewVO) => ({
        label: item.viewDisplayName,
        value: item.viewId,
        isAllowed: item.isAllowed
      }));
      setOperationOptions(operationOptions); // 存在 state 里
      setViewPermissionOptions(viewOptions);

      // 设置默认值
      const defaultChecked = funcPermission.authOperations
        .filter((item: AuthOperationVO) => item.isAllowed === FunOperationPermission.canOperateAllowed)
        .map((item: AuthOperationVO) => item.operationCode);
      const viewDefaultChecked = funcPermission.authEntity.authViews
        ?.filter((item: AuthViewVO) => item.isAllowed === FunPermissionViewVisit.canVisit)
        .map((item: AuthViewVO) => item.viewId);
      form.setFieldsValue({
        isPageAllowed: funcPermission.isPageAllowed,
        authOperations: defaultChecked,
        isAllViewsAllowed: funcPermission.authEntity.isAllViewsAllowed,
        authViews: viewDefaultChecked
        // authEntity: funcPermission.authEntity.authViews
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

  const changeViewVisitPermission = (val: FunPermissionViewVisit.canVisit | FunPermissionViewVisit.notVisit) => {
    const params: UpdatePagePermissionReq = {
      isPageAllowed: val,
      permissionReq: {
        applicationId: appId,
        menuId,
        roleId
      }
    };
    updatePagePermission(params);
  };

  const changeOperationPermission = async (values: any) => {
    const updateOperations = funcPermission?.authOperations
      .map((op: AuthOperationVO) => {
        const newIsAllowed = values.includes(op.operationCode)
          ? FunOperationPermission.canOperateAllowed
          : FunOperationPermission.notOperateAllowed;
        return newIsAllowed !== op.isAllowed ? { ...op, isAllowed: newIsAllowed } : null;
      })
      .filter(Boolean);
    const params: UpdateOperationPermissionReq = {
      authOperations: updateOperations,
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
      form.setFieldValue('isAllViewsAllowed', value);
      const allViewIds = viewPermissionOptions?.map((option) => option.value) || [];
      form.setFieldValue('authViews', allViewIds);
    } else {
      form.setFieldValue('isAllViewsAllowed', value);
    }
  };

  const changeAllViewPermission = (checked: any) => {
    // 实现全选/取消全选逻辑
    setIsCustomAllViewsAllowed(checked);
    if (checked) {
      // 全选：选中所有视图权限
      const allViewIds = viewPermissionOptions?.map((option) => option.value) || [];
      form.setFieldValue('authViews', allViewIds);
    } else {
      // 取消全选：清空所有视图权限
      form.setFieldValue('authViews', []);
    }
  };

  return (
    <>
      {!menuId ? (
        <div className={styles.permissionEmpty}>
          <IconEmpty fontSize={50} />
          暂无页面功能权限，请先添加页面
        </div>
      ) : (
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
                {funcPermission?.authOperations.every(
                  (op: AuthOperationVO) => op.isAllowed === FunOperationPermission.canOperateAllowed
                ) && '全部可操作'}
              </div>
            </div>
            <div className={styles.itemContent}>
              <Form.Item field="authOperations" noStyle>
                <CheckboxGroup
                  disabled={!isPageAllowed}
                  options={operationOptions}
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
              {/* 单选框 默认所有视图均可访问/自定义权限 */}
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
              {/* 当单选为自定义权限时 并且 authViews不为空时 显示 多选框 */}
              {isAllViewsAllowed === FunViewPermission.ViewCustomFieldPermission &&
                !!form.getFieldValue('authViews')?.length && (
                  <div className={styles.checkboxGroup}>
                    {/* 全选框 */}
                    <Checkbox
                      style={{ marginBottom: '5px' }}
                      checked={isCustomAllViewsAllowed}
                      onChange={(value) => changeAllViewPermission(value)}
                    >
                      <span>全选</span>
                    </Checkbox>

                    {/* 视图权限多选框列表 */}
                    <Form.Item field="authViews" noStyle>
                      <CheckboxGroup
                        direction="vertical"
                        options={viewPermissionOptions || []}
                        onChange={(values) => {
                          setIsCustomAllViewsAllowed(viewPermissionOptions?.length === values.length);
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

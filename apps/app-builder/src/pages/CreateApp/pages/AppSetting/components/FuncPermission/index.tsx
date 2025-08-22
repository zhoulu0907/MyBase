import { useState, useEffect, type FC } from 'react';
import { Radio, Checkbox, Form } from '@arco-design/web-react';
import {
  getFuncPermission,
  updatePagePermission,
  updateOperationPermission,
  type AuthOperationVO,
  type GetPermissionReq,
  type FuncPermissionResponse,
  type UpdatePagePermissionReq,
  type UpdateOperationPermissionReq
} from '@onebase/app';
import styles from './index.module.less';

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

  const [operationOptions, setOperationOptions] = useState<any[]>();
  const [funcPermission, setFuncPermission] = useState<FuncPermissionResponse>(); // 功能权限

  useEffect(() => {
    if (appId && menuId && roleId) {
      getApplicationPermission();
    }
  }, [appId, menuId, roleId]);

  useEffect(() => {
    if (funcPermission) {
      // 组装 options
      const operationOptions = funcPermission.authOperations.map((item: AuthOperationVO) => ({
        label: item.displayName,
        value: item.operationCode
      }));

      setOperationOptions(operationOptions); // 存在 state 里

      // 设置默认值
      const defaultChecked = funcPermission.authOperations
        .filter((item: AuthOperationVO) => item.isAllowed === 1)
        .map((item: AuthOperationVO) => item.operationCode);

      form.setFieldsValue({
        isPageAllowed: funcPermission.isPageAllowed,
        authOperations: defaultChecked,
        isAllViewsAllowed: true
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
    console.log('获取权限信息', res);
    setFuncPermission(res);
  };

  return (
    <Form form={form}>
      <div className={styles.formItem}>
        <div className={styles.itemHeader}>
          <div className={styles.left}>页面权限</div>
          <div className={styles.right}>{funcPermission?.isPageAllowed ? '可访问' : '无权限'}</div>
        </div>

        <div className={styles.itemContent}>
          <Form.Item field="isPageAllowed" noStyle>
            <RadioGroup
              type="button"
              name="lang"
              onChange={(val) => {
                const params: UpdatePagePermissionReq = {
                  isPageAllowed: val,
                  permissionReq: {
                    applicationId: appId,
                    menuId,
                    roleId
                  }
                };
                updatePagePermission(params);
              }}
            >
              <Radio value={1}>可访问</Radio>
              <Radio value={0}>无权限</Radio>
            </RadioGroup>
          </Form.Item>
        </div>
      </div>

      <div className={styles.formItem}>
        <div className={styles.itemHeader}>
          <div className={styles.left}>操作权限</div>
          <div className={styles.right}>
            {funcPermission?.authOperations.every((op: AuthOperationVO) => op.isAllowed === 1) && '全部可操作'}
          </div>
        </div>
        <div className={styles.itemContent}>
          <Form.Item field="authOperations" noStyle>
            <CheckboxGroup
              options={operationOptions}
              onChange={async (values) => {
                const updateOperations = funcPermission?.authOperations
                  .map((op: AuthOperationVO) => {
                    const newIsAllowed = values.includes(op.operationCode) ? 1 : 0;
                    return newIsAllowed !== op.isAllowed ? { ...op, isAllowed: newIsAllowed } : null;
                  })
                  .filter(Boolean);
                console.log(values, updateOperations);
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
              }}
            />
          </Form.Item>
        </div>
      </div>

      {/*  暂时没有数据，后面迭代 */}
      {/* <div>
          <div>视图权限</div>
          <Form.Item field="isAllViewsAllowed" noStyle>
            <Checkbox onChange={onChangeAll}>
              {funcPermission?.authEntity.isAllViewsAllowed ? '取消全选' : '全选'}
            </Checkbox>
          </Form.Item>

          <Form.Item field="authEntity">
            <CheckboxGroup direction="vertical" />
          </Form.Item>
        </div> */}
    </Form>
  );
};

export default FuncPermission;

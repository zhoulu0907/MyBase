import { Checkbox, Divider, Empty, Form, Grid, Radio } from '@arco-design/web-react';
import {
  FieldDownloadable,
  FieldEdit,
  FieldRead,
  getFieldPermission,
  RoleAllFieldPermission,
  updateFieldPermission,
  Visibility,
  type AuthFieldVO,
  type GetPermissionReq,
  type UpdateFieldPermissionReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';

import { IconAttachment, IconEmpty } from '@arco-design/web-react/icon';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const RadioGroup = Radio.Group;

interface IProps {
  appId: string;
  menuId: string;
  roleId: string;
}

// 字段权限
const FieldPermission: FC<IProps> = ({ appId, menuId, roleId }: IProps) => {
  const [form] = Form.useForm();

  const [checkReadableAll, setCheckReadableAll] = useState(false);
  const [checkEditableAll, setCheckEditableAll] = useState(false);
  const [checkDownloadableAll, setCheckDownloadableAll] = useState(false);

  const [indeterminateReadable, setIndeterminateReadable] = useState(true);
  const [indeterminateEditable, setIndeterminateEditable] = useState(true);
  const [indeterminateDownloadable, setIndeterminateDownloadable] = useState(true);

  const [fieldPermission, setFieldPermission] = useState<AuthFieldVO[]>([]); // 字段权限
  const [isAllFieldsAllowed, setIsAllFieldsAllowed] = useState<number>(1);
  const [operationConfig, setOperationConfig] = useState<AuthFieldVO[]>([]);

  const [showEmpty, setShowEmpty] = useState(false);

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
    }
  }, [appId, menuId, roleId]);

  useEffect(() => {
    // 检查是否有任何字段权限数据
    const hasFieldPermission = fieldPermission && fieldPermission.length > 0;
    const hasOperationConfig = operationConfig && operationConfig.length > 0;

    // 如果没有任何权限数据，则显示空状态
    if (!hasFieldPermission && !hasOperationConfig && !isAllFieldsAllowed) {
      setShowEmpty(true);
    } else {
      setShowEmpty(false);
    }
  }, [fieldPermission, operationConfig, isAllFieldsAllowed]);

  useEffect(() => {
    if (fieldPermission) {
      const formattedFields = fieldPermission.reduce(
        (acc, field) => {
          acc[field.fieldId] = {
            isCanRead: field.isCanRead === FieldRead.canRead,
            isCanEdit: field.isCanEdit === FieldEdit.canEdit,
            isCanDownload: field.isCanDownload === FieldDownloadable.canDownloadable
          };
          return acc;
        },
        {} as Record<string, { isCanRead: boolean; isCanEdit: boolean; isCanDownload: boolean }>
      );

      form.setFieldsValue({
        authFields: formattedFields
      });
      handleFieldPermissionChange();
    }
  }, [fieldPermission]);

  useEffect(() => {
    if (operationConfig) {
      const formattedOperationFields = operationConfig.reduce(
        (acc, field) => {
          acc[field.fieldId] = {
            isCanDownload: field.isCanDownload === FieldDownloadable.canDownloadable
          };
          return acc;
        },
        {} as Record<string, { isCanDownload: boolean }>
      );

      form.setFieldsValue({
        operationPermissions: formattedOperationFields
      });
      handleOptPermissionChange();
    }
  }, [operationConfig]);

  /* 获取权限信息 */
  const getFieldsPermission = async () => {
    const res: any = await generatePermFields();

    const isAllFieldsAllowed = res.isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission;
    setIsAllFieldsAllowed(isAllFieldsAllowed);
    form.setFieldValue('isAllFieldsAllowed', isAllFieldsAllowed);
  };

  /* 获取权限信息 */
  const generatePermFields = async () => {
    const params: GetPermissionReq = {
      applicationId: appId,
      menuId,
      roleId
    };
    const res = await getFieldPermission(params);
    setFieldPermission(res.authFieldsRD);
    setOperationConfig(res.authFieldsDL);
    return res;
  };

  /* 更新字段权限 */
  const updateFieldsPermission = async (authFields: AuthFieldVO[], isAllFieldsAllowed: number) => {
    const params: UpdateFieldPermissionReq = {
      permissionReq: {
        applicationId: appId,
        menuId,
        roleId
      },
      isAllFieldsAllowed,
      authFields
    };
    const res = await updateFieldPermission(params);
    console.log('更新字段权限', res);
  };

  // 可阅读全部选中
  const onChangeReadableAll = (checked: boolean) => {
    const formData = form.getFieldsValue();

    // 可编辑时，默认可阅读
    const updatedFieldPermissions = Object.fromEntries(
      Object.entries(formData.authFields as Record<string, { isCanRead: boolean; isCanEdit: boolean }>).map(
        ([key, value]) => [key, { ...value, isCanRead: value.isCanEdit || checked }]
      )
    );

    setCheckReadableAll(checked);
    setIndeterminateReadable(false);
    form.setFieldValue('authFields', updatedFieldPermissions);

    const updateFields = fieldPermission?.map((field) => ({
      ...field,
      isCanRead: field.isCanEdit || +checked
    }));
    setFieldPermission(updateFields);
    updateFieldsPermission(updateFields || [], isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission);
    console.log(fieldPermission, updateFields);
  };

  // 可编辑全部选中
  const onChangeEditableAll = (checked: boolean) => {
    const formData = form.getFieldsValue();

    const updatedFieldPermissions = Object.fromEntries(
      Object.entries(formData.authFields as Record<string, { isCanRead: boolean; isCanEdit: boolean }>).map(
        ([key, value]) => [key, { ...value, isCanEdit: checked, isCanRead: checked ? checked : value.isCanRead }]
      )
    );

    setCheckEditableAll(checked);
    setCheckReadableAll(checked);
    setIndeterminateEditable(false);
    form.setFieldValue('authFields', updatedFieldPermissions);
    const updateFields = fieldPermission?.map((field) => ({
      ...field,
      isCanEdit: +checked,
      isCanRead: checked ? +checked : field.isCanRead
    }));
    setFieldPermission(updateFields);
    updateFieldsPermission(updateFields || [], isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission);
    console.log(fieldPermission, updateFields);
  };

  // 更新全部选中的值
  const onChangeSelectAll = () => {
    // 处理字段权限的半选状态
    handleFieldPermissionChange();

    // 处理可下载的半选状态
    handleOptPermissionChange();
  };

  // 处理字段权限的半选状态
  const handleFieldPermissionChange = () => {
    const formData = form.getFieldsValue();
    const currentValues = formData.authFields || {};
    const curValues = Object.values(currentValues);
    const allCheckedRead = curValues.length > 0 && curValues.every((field: any) => field.isCanRead);
    const allUnCheckedRead = curValues.every((field: any) => !field.isCanRead);
    setCheckReadableAll(allCheckedRead);
    setIndeterminateReadable(!allCheckedRead && !allUnCheckedRead);

    const allCheckedEdit = curValues.length > 0 && curValues.every((field: any) => field.isCanEdit);
    const allUnCheckedEdit = curValues.every((field: any) => !field.isCanEdit);
    setCheckEditableAll(allCheckedEdit);
    setIndeterminateEditable(!allCheckedEdit && !allUnCheckedEdit);
  };

  // 处理可下载的半选状态
  const handleOptPermissionChange = () => {
    const formData = form.getFieldsValue();
    const currentOperationValues = formData.operationPermissions || {};
    const curOptValues = Object.values(currentOperationValues);
    const allCheckedDownload = curOptValues.length > 0 && curOptValues.every((field: any) => field.isCanDownload);
    const allUnCheckedDownload = curOptValues.every((field: any) => !field.isCanDownload);
    setCheckDownloadableAll(allCheckedDownload);
    setIndeterminateDownloadable(!allCheckedDownload && !allUnCheckedDownload);
  };

  // 可下载全部选中
  const onChangeDownloadableAll = (checked: boolean) => {
    const formData = form.getFieldsValue();

    const updatedOperationPermissions = Object.fromEntries(
      Object.entries(formData.operationPermissions as Record<string, { isCanDownload: boolean }>).map(
        ([key, value]: [string, any]) => [key, { ...value, isCanDownload: checked }]
      )
    );
    setCheckDownloadableAll(checked);
    setIndeterminateDownloadable(false);
    form.setFieldValue('operationPermissions', updatedOperationPermissions);
    const updateFields = operationConfig?.map((field) => ({
      ...field,
      isCanDownload: +checked
    }));
    setOperationConfig(updateFields);
    updateFieldsPermission(updateFields || [], isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission);
  };

  const handleIsAllFieldsAllowedChange = (value: number) => {
    setIsAllFieldsAllowed(value);
    if (value) {
      updateFieldsPermission(fieldPermission || [], value);
    } else {
      // 切换自定义时默认全选中
      const defaultAuthFields = fieldPermission.map((field) => ({
        ...field,
        isCanEdit: 1,
        isCanRead: 1
      }));
      form.setFieldValue('authFields', defaultAuthFields);
      setCheckEditableAll(true);
      setCheckReadableAll(true);
      setFieldPermission(defaultAuthFields);
      updateFieldsPermission(defaultAuthFields, value); //更新数据
    }
  };

  return (
    <>
      {menuId && (
        <div className={styles.fieldPermissions}>
          <Form
            form={form}
            onChange={(value) => {
              const changeField = Object.entries(value);
              const getChangeFieldKey = changeField[0];
              const getChangeFieldValue = Object.values(value)[0];
              const getChangeFieldName = getChangeFieldKey[0].trim().split('.');
              const changeFieldAuthFields = getChangeFieldName.length > 0 ? getChangeFieldName[0] : '';
              const changeFieldId = getChangeFieldName.length > 1 ? getChangeFieldName[1] : '';
              const changeFieldValueName = getChangeFieldName.length > 2 ? getChangeFieldName[2] : '';

              if (changeFieldAuthFields === 'authFields') {
                const updateField = fieldPermission?.map((field) => {
                  if (field.fieldId === changeFieldId) {
                    return {
                      ...field,
                      isCanRead: Number(getChangeFieldValue) || field.isCanRead,
                      [changeFieldValueName]: Number(getChangeFieldValue)
                    };
                  }
                  return field;
                });

                setFieldPermission(updateField);

                const modifiedField = updateField?.filter((field) => field.fieldId === changeFieldId) || [];
                updateFieldsPermission(
                  modifiedField,
                  isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission
                );
              } else if (changeFieldAuthFields === 'operationPermissions') {
                const updateField = operationConfig?.map((field) => {
                  if (field.fieldId === changeFieldId) {
                    return {
                      ...field,
                      [changeFieldValueName]: Number(getChangeFieldValue)
                    };
                  }
                  return field;
                });

                setOperationConfig(updateField);

                const modifiedField = updateField?.filter((field) => field.fieldId === changeFieldId) || [];
                updateFieldsPermission(
                  modifiedField,
                  isAllFieldsAllowed || RoleAllFieldPermission.FieldCustomFieldPermission
                );
              }

              // 更新单个字段
              form.setFieldValue(getChangeFieldKey + '', changeField[1]);
              onChangeSelectAll();
            }}
          >
            <Form.Item field="isAllFieldsAllowed" noStyle>
              <RadioGroup
                direction="vertical"
                // value={isAllFieldsAllowed}
                onChange={(value) => {
                  handleIsAllFieldsAllowedChange(value);
                }}
              >
                <Radio value={RoleAllFieldPermission.AllFieldPermissionAllow}>所有字段内容可操作</Radio>
                <Radio value={RoleAllFieldPermission.FieldCustomFieldPermission}>自定义权限</Radio>
              </RadioGroup>
            </Form.Item>
            {!isAllFieldsAllowed && (
              <div>
                {showEmpty && <Empty description="暂无数据" />}
                {fieldPermission && fieldPermission.length > 0 && (
                  <Form.Item
                    field="authFields"
                    label="字段内容权限"
                    layout="vertical"
                    shouldUpdate
                    style={{
                      marginTop: 12
                    }}
                  >
                    <div className={styles.table}>
                      <Row>
                        <Col span={8}></Col>
                        <Col span={4}>
                          <Checkbox
                            onChange={onChangeReadableAll}
                            checked={checkReadableAll}
                            indeterminate={indeterminateReadable}
                          >
                            可阅读
                          </Checkbox>
                        </Col>
                        <Col span={4}>
                          <Checkbox
                            onChange={onChangeEditableAll}
                            checked={checkEditableAll}
                            indeterminate={indeterminateEditable}
                          >
                            可编辑
                          </Checkbox>
                        </Col>
                      </Row>
                      <Divider />
                      {fieldPermission?.map((field) => {
                        return (
                          <Row className={styles.rowItem} key={field.fieldId}>
                            <Col span={8}>
                              <span>{field.fieldDisplayName}</span>
                            </Col>

                            {/* 可阅读权限 */}
                            <Col span={4}>
                              <Form.Item
                                field={`authFields.${field.fieldId}.isCanRead`}
                                trigger="onChange"
                                triggerPropName="checked"
                                noStyle
                              >
                                <Checkbox disabled={field.isCanEdit === FieldEdit.canEdit} />
                              </Form.Item>
                            </Col>

                            {/* 可编辑权限 */}
                            <Col span={4}>
                              <Form.Item
                                field={`authFields.${field.fieldId}.isCanEdit`}
                                triggerPropName="checked"
                                noStyle
                              >
                                <Checkbox
                                  className={`${field.isCanRead === FieldRead.notRead ? styles.checkboxGray : ''} ${field.isCanEdit === FieldEdit.canEdit ? styles.checkboxGreen : ''}`}
                                />
                                {/* field.editDisabled */}
                              </Form.Item>
                            </Col>
                          </Row>
                        );
                      })}
                    </div>
                  </Form.Item>
                )}

                {operationConfig && operationConfig.length > 0 && (
                  <Form.Item field="operationPermissions" label="操作权限" layout="vertical" shouldUpdate>
                    <div className={styles.table}>
                      <Row className={styles.tableTitle}>
                        <Col span={8}></Col>
                        <Col span={4}>
                          <Checkbox
                            onChange={onChangeDownloadableAll}
                            checked={checkDownloadableAll}
                            indeterminate={indeterminateDownloadable}
                          >
                            可下载
                          </Checkbox>
                        </Col>
                      </Row>
                      <Divider />
                      {operationConfig?.map((field: any) => {
                        return (
                          <Row className={styles.rowItem} key={field.fieldId}>
                            <Col span={8}>
                              <IconAttachment style={{ marginRight: 8 }} />
                              <span>{field.fieldDisplayName}</span>
                            </Col>

                            <Col span={4}>
                              <Form.Item
                                field={`operationPermissions.${field.fieldId}.isCanDownload`}
                                trigger="onChange"
                                triggerPropName="checked"
                                noStyle
                              >
                                <Checkbox />
                              </Form.Item>
                            </Col>
                          </Row>
                        );
                      })}
                    </div>
                  </Form.Item>
                )}
              </div>
            )}
          </Form>
        </div>
      )}
    </>
  );
};

export default FieldPermission;

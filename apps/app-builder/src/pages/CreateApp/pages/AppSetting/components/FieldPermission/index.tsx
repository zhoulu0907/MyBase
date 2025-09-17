import { useEffect, useState, type FC } from 'react';
import { Radio, Checkbox, Divider, Grid, Form } from '@arco-design/web-react';
import {
  getFieldPermission,
  updateFieldPermission,
  RoleAllFieldPermission,
  FieldRead,
  FieldEdit,
  FieldDownloadable,
  type AuthFieldVO,
  type GetPermissionReq,
  type UpdateFieldPermissionReq
} from '@onebase/app';

import styles from './index.module.less';
import { IconAttachment, IconEmpty } from '@arco-design/web-react/icon';

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

  const [fieldPermission, setFieldPermission] = useState<AuthFieldVO[]>(); // 字段权限
  const [isAllFieldsAllowed, setIsAllFieldsAllowed] = useState<number>();

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
    }
    console.log('字段权限 menuId: ', menuId);
  }, [appId, menuId, roleId]);

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
      onChangeSelectAll();
    }
  }, [fieldPermission]);

  /* 获取权限信息 */
  const getFieldsPermission = async () => {
    const params: GetPermissionReq = {
      applicationId: appId,
      menuId,
      roleId
    };
    const res = await getFieldPermission(params);
    const addDisabled = res.authFields.map((field: AuthFieldVO) => ({
      ...field
    }));
    setFieldPermission(addDisabled);
    setIsAllFieldsAllowed(res.isAllFieldsAllowed || RoleAllFieldPermission.CustomFieldPermission);
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
        ([key, value]) => [key, { ...value, isCanRead: value.isCanEdit }]
      )
    );

    setCheckReadableAll(checked);
    setIndeterminateReadable(false);
    form.setFieldValue('authFields', updatedFieldPermissions);

    const updateFields = fieldPermission?.map((field) => ({
      ...field,
      isCanRead: field.isCanEdit || +(field.isCanRead === FieldRead.notRead)
    }));
    setFieldPermission(updateFields);
    updateFieldsPermission(updateFields || [], isAllFieldsAllowed || RoleAllFieldPermission.CustomFieldPermission);
    console.log(fieldPermission, updateFields);
  };

  // 可编辑全部选中
  const onChangeEditableAll = (checked: boolean) => {
    const formData = form.getFieldsValue();

    const updatedFieldPermissions = Object.fromEntries(
      Object.entries(formData.authFields as Record<string, { isCanRead: boolean; isCanEdit: boolean }>).map(
        ([key, value]) => [key, { ...value, isCanEdit: checked, isCanRead: checked }]
      )
    );

    setCheckEditableAll(checked);
    setIndeterminateEditable(false);
    form.setFieldValue('authFields', updatedFieldPermissions);
    const updateFields = fieldPermission?.map((field) => ({
      ...field,
      isCanEdit: +checked,
      isCanRead: +checked || +(field.isCanRead === FieldRead.notRead)
    }));
    setFieldPermission(updateFields);
    updateFieldsPermission(updateFields || [], isAllFieldsAllowed || RoleAllFieldPermission.CustomFieldPermission);
    console.log(fieldPermission, updateFields);
  };

  // 更新全部选中的值
  const onChangeSelectAll = () => {
    const currentValues = form.getFieldsValue().authFields;

    const allCheckedRead = Object.values(currentValues).every((field: any) => field.isCanRead);
    const allUnCheckedRead = Object.values(currentValues).every((field: any) => !field.isCanRead);
    setCheckReadableAll(allCheckedRead);
    setIndeterminateReadable(!allCheckedRead && !allUnCheckedRead);

    const allCheckedEdit = Object.values(currentValues).every((field: any) => field.isCanEdit);
    const allUnCheckedEdit = Object.values(currentValues).every((field: any) => !field.isCanEdit);
    setCheckEditableAll(allCheckedEdit);
    setIndeterminateEditable(!allCheckedEdit && !allUnCheckedEdit);
  };

  return (
    <>
      {!menuId ? (
        <div className={styles.permissionEmpty}>
          <IconEmpty fontSize={50} />
          暂无页面字段权限，请先添加页面
        </div>
      ) : (
        <div className={styles.fieldPermissions}>
          <Form
            form={form}
            onChange={(value) => {
              const changeField = Object.entries(value);
              const getChangeFieldKey = changeField[0];
              const getChangeFieldValue = Object.values(value)[0];
              const getChangeFieldName = getChangeFieldKey[0].trim().split('.');

              console.log(changeField, 'changeField');

              const updateField = fieldPermission?.map((field) => {
                if (field.fieldId === getChangeFieldName[1]) {
                  return {
                    ...field,
                    isCanRead: Number(getChangeFieldValue) || field.isCanRead,
                    [getChangeFieldName[2]]: Number(getChangeFieldValue)
                  };
                }
                return field;
              });

              setFieldPermission(updateField);

              const modifiedField = updateField?.filter((field) => field.fieldId === getChangeFieldName[1]) || [];
              updateFieldsPermission(modifiedField, isAllFieldsAllowed || RoleAllFieldPermission.CustomFieldPermission);

              // 更新单个字段
              form.setFieldValue(getChangeFieldKey + '', changeField[1]);
              onChangeSelectAll();
            }}
          >
            <RadioGroup
              direction="vertical"
              value={isAllFieldsAllowed}
              onChange={(value) => {
                setIsAllFieldsAllowed(value);
                updateFieldsPermission(fieldPermission || [], value);
              }}
            >
              <Radio value={RoleAllFieldPermission.AllFieldPermissionAllow}>所有字段内容可操作</Radio>
              <Radio value={RoleAllFieldPermission.CustomFieldPermission}>自定义权限</Radio>
            </RadioGroup>

            <Form.Item
              field="authFields"
              label="字段内容权限"
              layout="vertical"
              shouldUpdate
              style={{
                marginTop: 12,
                visibility: isAllFieldsAllowed === RoleAllFieldPermission.CustomFieldPermission ? 'visible' : 'hidden'
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
                  console.log(field);
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
                        <Form.Item field={`authFields.${field.fieldId}.isCanEdit`} triggerPropName="checked" noStyle>
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

            {/* <Form.Item field="operationPermissions" label="操作权限" layout="vertical" shouldUpdate>
          <div className={styles.table}>
            <Row className={styles.tableTitle}>
              <Col span={8}></Col>
              <Col span={4}>
                <Checkbox
                  // onChange={onChangeDownloadableAll}
                  checked={checkDownloadableAll}
                  indeterminate={indeterminateDownloadable}
                >
                  可下载
                </Checkbox>
              </Col>
            </Row>
            <Divider />
            {operationConfig.map((field) => (
              <Row className={styles.rowItem} key={field.key}>
                <Col span={8}>
                  <IconAttachment style={{ marginRight: 8 }} />
                  <span>{field.name}</span>
                </Col>

                <Col span={4}>
                  <Form.Item field={`operationPermissions.${field.key}.downloadable`} triggerPropName="checked" noStyle>
                    <Checkbox />
                  </Form.Item>
                </Col>
              </Row>
            ))}
          </div>
        </Form.Item> */}
          </Form>
        </div>
      )}
    </>
  );
};

export default FieldPermission;

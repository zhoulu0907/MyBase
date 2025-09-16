import { Button, Divider, Popconfirm, Space, Tag } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconPlusCircle } from '@arco-design/web-react/icon';
import {
  // updateDataGroupPermission,
  // deleteDataGroup,
  // getEntityFieldsWithChildren
  getAppEntities,
  getDataPermission,
  getEntityFields,
  getFieldCheckTypeApi,
  // type UpdateDataGroupPermissionReq,
  type AppEntities,
  type AppEntity,
  // type AppEntityField,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  // type FilterFieldCheckType,
  type GetPermissionReq,
  type EntityFieldValidationTypes,
  type ConfitionField
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import DataPermissionModal from './components/DataPermissionModal';

import styles from './index.module.less';

const initialFormValues: AuthDataGroupVO = {
  id: '',
  groupName: '',
  description: '',
  scopeFieldId: undefined,
  scopeLevel: {
    personId: undefined,
    scopeType: undefined,
    assignIds: []
  },
  dataFilters: [],
  isOperable: 1
};

const permission = [
  {
    name: '默认权限',
    subTitle: '系统提供的默认权限',
    operation: {
      isOwn: true, // 本人
      purview: 'owner', // 权限范围-拥有者
      viewable: true,
      operable: true
    }
  }
];

interface IProps {
  appId: string;
  menuId: string;
  roleId: string;
}

// 数据权限
const DataPermission: FC<IProps> = ({ appId, menuId, roleId }: IProps) => {
  // const [form] = Form.useForm();
  const [status, setStatus] = useState<'create' | 'edit'>('create');
  const [appEntities, setAppEntities] = useState<AppEntity[]>([]);
  const [appEntityFields, setAppEntityFields] = useState<ConfitionField[]>([]);
  const [dataPermissionPerson, setDataPermissionPerson] = useState<AuthDataPermissionPersonVO[]>([]);
  const [filterFieldCheckType, setFilterFieldCheckType] = useState<EntityFieldValidationTypes[]>([]);

  const [modalVisible, setModelVisible] = useState<boolean>(false);

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
    }
  }, [appId, menuId, roleId]);

  /* 获取权限信息 */
  const getFieldsPermission = async () => {
    const params: GetPermissionReq = {
      applicationId: appId,
      menuId,
      roleId
    };
    const res = await getDataPermission(params);
    console.log('数据权限', res);
    // const addDisabled = res.authFields.map((field: AuthFieldVO) => ({
    //   ...field
    // }));
    // setFieldPermission(addDisabled);
    // setIsAllFieldsAllowed(res.isAllFieldsAllowed || 0);
  };

  // 打开model
  const handleModal = async (status: 'create' | 'edit', id?: string) => {
    console.log('Modal id', id);
    setStatus(status);
    setModelVisible(true);

    GetModelInitData();
  };

  const GetModelInitData = async () => {
    try {
      const entitiesResq: AppEntities = await getAppEntities(appId);
      console.log('业务实体 entitiesResq:', entitiesResq);
      setAppEntities(entitiesResq.entities);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };

  const changeEntity = async (params: { entityId: string }) => {
    console.log('改变业务实体 entityId;', params.entityId);
    getDataPermissionFields(params);
    getDataPermissionRoles(params);
  };

  // 获取数据权限数据字段
  const getDataPermissionFields = async (params: { entityId: string }) => {
    try {
      const entityFieldsResq = await getEntityFields({ entityId: params.entityId, isSystemField: 0 });
      console.log('根据实体ID获取数据字段权限 entityFieldsResq:', entityFieldsResq);
      // entityFieldsResq 返回的数据 是 id 但是 appEntityField 中 是 fieldID
      entityFieldsResq.forEach((field: any) => {
        field.fieldId = field.id;
      });
      // 批量获取字段可选校验类型
      const getFieldCheckTypeParams: string[] = [];
      entityFieldsResq.forEach((item: any) => {
        getFieldCheckTypeParams.push(item.fieldId);
      });
      getFieldCheckType(getFieldCheckTypeParams);
      setAppEntityFields(entityFieldsResq);
      console.log('setEntityFields', appEntityFields);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };
  // 获取数据权限角色
  const getDataPermissionRoles = async (params: { entityId: string }) => {
    try {
      const dataPermissionRoles = await getEntityFields({ entityId: params.entityId, isPerson: 1 });
      console.log('获取数据权限角色 dataPermissionRoles:', dataPermissionRoles);
      // 将获取到的数据转换为正确的格式
      const formattedData = dataPermissionRoles.map((item: any) => ({
        PersonId: item.id,
        fieldName: item.fieldName,
        displayName: item.displayName,
        entityID: item.entityId
      }));

      // 更新状态
      setDataPermissionPerson(formattedData);
    } catch (error) {
      console.error('获取数据权限角色失败', error);
    }
  };
  // 批量获取字段可选校验类型
  const getFieldCheckType = async (fieldIds: string[]) => {
    const fieldCheckTypeResq = await getFieldCheckTypeApi(fieldIds);
    setFilterFieldCheckType(fieldCheckTypeResq);
  };

  const handleModalSubmit = async (values?: AuthDataGroupVO) => {
    console.log('handleModalSubmit values:', values);
  };
  const handleModalCancel = () => {
    console.log('取消创建数据权限');
    setModelVisible(false);
  };
  return (
    <div className={styles.dataPermission}>
      {permission.map((perm, index) => (
        <div className={styles.permItem} key={index}>
          <div className={styles.top}>
            <div className={styles.left}>
              <div className={styles.title}>{perm.name}</div>
              <div className={styles.subtitle}>{perm.subTitle}</div>
            </div>
            <div className={styles.right}>
              <IconEdit
                style={{ fontSize: 20, color: '#4E5969', cursor: 'pointer' }}
                onClick={() => {
                  handleModal('edit', perm.id);
                }}
              />
              <Popconfirm
                focusLock
                title="删除数据权限"
                content="确定要删除这条数据吗？"
                onOk={() => {
                  console.log('确认删除');
                }}
                onCancel={() => {
                  console.log('取消删除');
                }}
              >
                <IconDelete
                  style={{
                    fontSize: 20,
                    color: '#F53F3F',
                    marginLeft: 10,
                    cursor: 'pointer'
                  }}
                />
              </Popconfirm>
            </div>
          </div>
          <Divider />
          <div className={styles.bottom}>
            <span className={styles.name}>操作权限：</span>
            <span className={styles.desc}>
              <Space wrap>
                当前角色可
                <Tag color="#F2F3F5" style={{ color: '#1D2129' }}>
                  查看
                </Tag>
                <Tag color="#F2F3F5" style={{ color: '#1D2129' }}>
                  操作
                </Tag>
                <Tag color="#E8F3FF" style={{ color: '#3C7EFF' }}>
                  拥有者
                </Tag>
                是
                <Tag color="#FFF7E8" style={{ color: '#FF7D00' }}>
                  本人
                </Tag>
                且
                <Tag color="#E8FFEA" style={{ color: '#00B42A' }}>
                  归档状态 等于 已归档
                </Tag>
                <Tag color="#E8FFEA" style={{ color: '#00B42A' }}>
                  归档人 等于 巫炘
                </Tag>
                的数据
              </Space>
            </span>
          </div>
        </div>
      ))}
      <Button
        type="outline"
        size="large"
        icon={<IconPlusCircle fontSize={20} />}
        style={{ display: 'flex', alignItems: 'center' }}
        onClick={() => handleModal('create')}
      >
        添加权限组
      </Button>
      <DataPermissionModal
        roleId={roleId}
        initialFormValues={initialFormValues}
        modalVisible={modalVisible}
        status={status}
        appEntities={appEntities}
        dataPermissionPerson={dataPermissionPerson}
        appEntityFields={appEntityFields}
        filterFieldCheckType={filterFieldCheckType}
        changeEntity={changeEntity}
        handleModalSubmit={(values: AuthDataGroupVO) => handleModalSubmit(values)}
        handleModalCancel={() => handleModalCancel()}
      />
    </div>
  );
};

export default DataPermission;

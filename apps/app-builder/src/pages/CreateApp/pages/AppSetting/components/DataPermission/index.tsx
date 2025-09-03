import { useState, useEffect, type FC } from 'react';
import { Divider, Tag, Space, Button, Form, Popconfirm } from '@arco-design/web-react';
import { IconEdit, IconDelete, IconPlusCircle } from '@arco-design/web-react/icon';
import {
  getDataPermission,
  updateDataGroupPermission,
  deleteDataGroup,
  // getEntityFieldsWithChildren
  getAppEntities,
  getEntityFields,
  type GetPermissionReq,
  type UpdateDataGroupPermissionReq,
  type AppEntities,
  type AppEntity,
  type AppEntityField
} from '@onebase/app';
import PermissionModal from './modal';

import styles from './index.module.less';

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
  const [form] = Form.useForm();
  const [visible, setVisible] = useState<boolean>(false);
  const [status, setStatus] = useState<'create' | 'edit'>('create');
  const [appEntities, setAppEntities] = useState<AppEntity[]>([]);
  const [appEntityFields, setAppEntityFields] = useState<AppEntityField[]>([]);

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
  const handleModel = async (status: 'create' | 'edit', id?: string) => {
    setVisible(true);
    setStatus(status);

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
  };

  // 获取数据权限数据字典
  const getDataPermissionFields = async (params: { entityId: string }) => {
    try {
      const entityFieldsResq = await getEntityFields(params);
      console.log('根据实体ID获取数据字段权限 entityFieldsResq:', entityFieldsResq);
      // entityFieldsResq 返回的数据 是 id 但是 appEntityField 中 是 fieldID
      entityFieldsResq.forEach((field: any) => {
        field.fieldID = field.id;
      });
      setAppEntityFields(entityFieldsResq);
      console.log('setEntityFields', appEntityFields);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };
  // 获取数据权限角色
  const getDataPermissionRoles = async (params: { entityId: string }) => {
    try {
      const dataPermissionRoles = await getEntityFields(params);
      console.log('获取数据权限角色 dataPermissionRoles:', dataPermissionRoles);
    } catch (error) {
      console.error('获取数据权限角色失败', error);
    }
  };
  const handleModelSubmit = async (values: any) => {
    console.log('创建数据权限 values:', values);
    // setVisible(false);
  };
  const handleModelCancel = () => {
    console.log('取消创建数据权限');
    setVisible(false);
  };
  return (
    <div className={styles.dataPermission}>
      {permission.map((perm) => (
        <div className={styles.permItem}>
          <div className={styles.top}>
            <div className={styles.left}>
              <div className={styles.title}>{perm.name}</div>
              <div className={styles.subtitle}>{perm.subTitle}</div>
            </div>
            <div className={styles.right}>
              <IconEdit
                style={{ fontSize: 20, color: '#4E5969', cursor: 'pointer' }}
                // onClick={() => handleModel('edit', perm.id)}
                onClick={() => {
                  handleModel('edit', perm.id);
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
        onClick={() => handleModel('create')}
      >
        添加权限组
      </Button>
      <PermissionModal
        // form={form}
        status={status}
        visible={visible}
        // onClose={() => setVisible(false)}
        appEntities={appEntities}
        appEntityFields={appEntityFields}
        changeEntity={changeEntity}
        handleModelSubmit={handleModelSubmit}
        handleModelCancel={handleModelCancel}
      />
    </div>
  );
};

export default DataPermission;

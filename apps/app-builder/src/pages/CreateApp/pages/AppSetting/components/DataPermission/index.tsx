import { Button, Divider, Popconfirm, Space, Tag } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconEmpty, IconPlusCircle } from '@arco-design/web-react/icon';
import {
  updateDataGroupPermission,
  // deleteDataGroup,
  // getEntityFieldsWithChildren
  // getAppEntities,
  getDataPermission,
  getEntityFields,
  getFieldCheckTypeApi,
  getScopeTypeApi,
  type UpdateDataGroupPermissionReq,
  // type AppEntities,
  type AppEntity,
  // type AppEntityField,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  // type FilterFieldCheckType,
  type GetPermissionReq,
  type EntityFieldValidationTypes,
  // type ConfitionField,
  type AppEntityField,
  type AuthDataFilterVO,
  type ScopeTypeOption
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import DataPermissionModal from './components/DataPermissionModal';

import styles from './index.module.less';

const initialFormValues: AuthDataGroupVO = {
  id: '',
  groupName: '',
  description: '',
  scopeFieldId: undefined,
  scopeLevel: '',
  scopeValue: [],
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
  const [appEntity, setAppEntity] = useState<AppEntity>();
  const [appEntityFields, setAppEntityFields] = useState<AppEntityField[]>([]);
  const [dataPermissionPerson, setDataPermissionPerson] = useState<AuthDataPermissionPersonVO[]>([]);
  const [filterFieldCheckType, setFilterFieldCheckType] = useState<EntityFieldValidationTypes[]>([]);
  const [dataPermissionScopeType, SetDataPermissionScopeType] = useState<ScopeTypeOption[]>([]);

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

    // GetModelInitData();
    getViewDataEntity();
    getScopeType();
  };

  const getScopeType = async () => {
    try {
      const scopeTypeResq = await getScopeTypeApi();
      SetDataPermissionScopeType(scopeTypeResq);
    } catch (error) {
      console.log('获取权限范围失败 error:', error);
    }
  };

  // 获取页面数据实体
  const getViewDataEntity = async () => {
    // 暂时没有获取页面表单绑定主实体的接口 mock数据
    // const dataEntityResq = await getViewDataEntityApi();
    const dataEntityResq = {
      entityId: '16935056057237504',
      entityName: '尝试创建页面',
      entityType: '独立表',
      tableName: 'iarg_ceshi',
      fields: [
        {
          fieldId: '29169768621965312',
          fieldName: 'owner',
          fieldType: 'USER',
          isSystemField: 0,
          displayName: '拥有者'
        },
        {
          fieldId: '29205846347251715',
          fieldName: 'auditor',
          fieldType: 'USER',
          isSystemField: 0,
          displayName: '审核员'
        },
        {
          fieldId: '16935056057237505',
          fieldName: 'id',
          fieldType: 'ID',
          isSystemField: 1,
          displayName: 'id'
        },
        {
          fieldId: '30699051858460678',
          fieldName: 'num',
          fieldType: 'NUMBER',
          isSystemField: 0,
          displayName: '数字'
        },
        {
          fieldId: '16935056057237506',
          fieldName: 'owner_id',
          fieldType: 'USER',
          isSystemField: 1,
          displayName: 'owner_id'
        },
        {
          fieldId: '16935056057237507',
          fieldName: 'owner_dept',
          fieldType: 'DEPARTMENT',
          isSystemField: 1,
          displayName: 'owner_dept'
        },
        {
          fieldId: '16935056057237508',
          fieldName: 'creator',
          fieldType: 'USER',
          isSystemField: 1,
          displayName: 'creator'
        },
        {
          fieldId: '16935056057237509',
          fieldName: 'updater',
          fieldType: 'USER',
          isSystemField: 1,
          displayName: 'updater'
        },
        {
          fieldId: '16935056057237510',
          fieldName: 'created_time',
          fieldType: 'DATETIME',
          isSystemField: 1,
          displayName: 'created_time'
        },
        {
          fieldId: '16935056057237511',
          fieldName: 'updated_time',
          fieldType: 'DATETIME',
          isSystemField: 1,
          displayName: 'updated_time'
        },
        {
          fieldId: '16935056057237512',
          fieldName: 'lock_version',
          fieldType: 'NUMBER',
          isSystemField: 1,
          displayName: 'lock_version'
        },
        {
          fieldId: '16935056057237513',
          fieldName: 'deleted',
          fieldType: 'NUMBER',
          isSystemField: 1,
          displayName: 'deleted'
        },
        {
          fieldId: '16935056057237514',
          fieldName: 'parent_id',
          fieldType: 'NUMBER',
          isSystemField: 1,
          displayName: 'parent_id'
        }
      ]
    };

    setAppEntity(dataEntityResq);
    getDataPermissionFields(dataEntityResq.entityId);
    getDataPermissionRoles(dataEntityResq.entityId);
  };

  // 获取数据权限数据字段
  const getDataPermissionFields = async (entityId: string) => {
    try {
      const entityFieldsResq = await getEntityFields({ entityId, isSystemField: 0 });
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
      console.log('setAppEntityFields', appEntityFields);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };
  // 获取数据权限角色
  const getDataPermissionRoles = async (entityId: string) => {
    try {
      const dataPermissionRoles = await getEntityFields({ entityId, isPerson: 1 });
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

    if (!values) return;

    // 创建符合后端要求的数据结构
    const submitData = { ...values };

    // 处理权限范围数据以满足后端要求
    processScopeLevelData(values, submitData);

    // 处理数据过滤条件，将Condition格式转换为AuthDataFilterVO格式
    processDataFilters(values, submitData);

    // 构造完整的请求参数
    const requestData: UpdateDataGroupPermissionReq = {
      authDataGroup: submitData,
      permissionReq: {
        applicationId: appId,
        menuId: menuId || '',
        roleId: roleId || ''
      }
    };

    console.log('处理后的提交数据:', requestData);
    // 调用后端API提交数据
    try {
      await updateDataGroupPermission(requestData);
      // 提交成功后刷新数据或关闭模态框
      setModelVisible(false);
    } catch (error) {
      console.error('提交数据权限失败:', error);
    }
  };

  /**
   * 处理权限范围数据以满足后端要求
   * @param values 原始表单数据
   * @param submitData 处理后的提交数据
   */
  const processScopeLevelData = (values: AuthDataGroupVO, submitData: any) => {
    console.log('进入并打印原始表单数据 values', values.scopeValue);
    console.log('原始表单数据 values.scopeType', values.scopeLevel);
    // 如果选择的是指定成员或者指定部门，将数据转为JSON字符串给scopeValue
    if (values.scopeLevel === 'specifiedPerson' || values.scopeLevel === 'specifiedDepartment') {
      console.log('进入权限范围是指定的条件判断');
      if (values.scopeValue && values.scopeValue.length > 0) {
        console.log('123');
        submitData.scopeValue = values.scopeValue.join(',');
      }
    }
    // }
  };

  /**
   * 处理数据过滤条件，将Condition格式转换为AuthDataFilterVO格式
   * @param values 原始表单数据
   * @param submitData 处理后的提交数据
   */
  const processDataFilters = (values: AuthDataGroupVO, submitData: any) => {
    if (values.dataFilters) {
      // 检查dataFilters是否为数组
      if (Array.isArray(values.dataFilters)) {
        const convertedDataFilters: Array<AuthDataFilterVO[]> = [];

        // 遍历每个条件组
        values.dataFilters.forEach((conditionGroup: any, groupIndex: number) => {
          // 检查conditionGroup是否为数组
          if (Array.isArray(conditionGroup)) {
            const filterGroup: AuthDataFilterVO[] = [];

            // 遍历组内的每个条件
            conditionGroup.forEach((condition: any, conditionIndex: number) => {
              console.log(`条件 ${conditionIndex}:`, condition);

              // 提取规则中的字段信息
              const rules = condition.rules;
              if (rules && Array.isArray(rules)) {
                rules.forEach((rule: any) => {
                  const filter: AuthDataFilterVO = {
                    conditionGroup: groupIndex,
                    conditionOrder: conditionIndex,
                    fieldId: rule.fieldId ? Number(rule.fieldId) : undefined,
                    fieldOperator: rule.op,
                    fieldValue: rule.value ? rule.value.join(',') : undefined,
                    fieldValueType: rule.operatorType
                  };
                  filterGroup.push(filter);
                });
              }
            });

            convertedDataFilters.push(filterGroup);
          } else {
            console.warn(`条件组 ${groupIndex} 不是数组:`, conditionGroup);

            // 如果conditionGroup不是数组，尝试处理它
            if (conditionGroup && typeof conditionGroup === 'object') {
              // 可能是单个条件对象，将其包装成数组
              const filterGroup: AuthDataFilterVO[] = [];
              const rules = conditionGroup.rules;
              if (rules && Array.isArray(rules)) {
                rules.forEach((rule: any, ruleIndex: number) => {
                  const filter: AuthDataFilterVO = {
                    conditionGroup: groupIndex,
                    conditionOrder: ruleIndex,
                    fieldId: rule.fieldId ? Number(rule.fieldId) : undefined,
                    fieldOperator: rule.op,
                    fieldValue: rule.value ? rule.value.join(',') : undefined,
                    fieldValueType: rule.operatorType
                  };
                  filterGroup.push(filter);
                });
              }
              convertedDataFilters.push(filterGroup);
            }
          }
        });

        submitData.dataFilters = convertedDataFilters;
        console.log('转换后的dataFilters:', convertedDataFilters);
      } else {
        console.warn('dataFilters不是数组:', values.dataFilters);

        // 如果dataFilters不是数组，尝试处理它
        if (values.dataFilters && typeof values.dataFilters === 'object') {
          // 可能是单个条件组，将其包装成数组
          const convertedDataFilters: Array<AuthDataFilterVO[]> = [];
          const conditionGroup = values.dataFilters;

          if (Array.isArray(conditionGroup)) {
            const filterGroup: AuthDataFilterVO[] = [];

            conditionGroup.forEach((condition: any) => {
              const rules = condition.rules;
              if (rules && Array.isArray(rules)) {
                rules.forEach((rule: any, ruleIndex: number) => {
                  const filter: AuthDataFilterVO = {
                    conditionGroup: 0,
                    conditionOrder: ruleIndex,
                    fieldId: rule.fieldId ? Number(rule.fieldId) : undefined,
                    fieldOperator: rule.op,
                    fieldValue: rule.value ? rule.value.join(',') : undefined,
                    fieldValueType: rule.operatorType
                  };
                  filterGroup.push(filter);
                });
              }
            });

            convertedDataFilters.push(filterGroup);
            submitData.dataFilters = convertedDataFilters;
          } else if (conditionGroup && typeof conditionGroup === 'object') {
            // 单个条件对象
            const filterGroup: AuthDataFilterVO[] = [];
            const rules = conditionGroup.rules;
            if (rules && Array.isArray(rules)) {
              rules.forEach((rule: any, ruleIndex: number) => {
                const filter: AuthDataFilterVO = {
                  conditionGroup: 0,
                  conditionOrder: ruleIndex,
                  fieldId: rule.fieldId ? Number(rule.fieldId) : undefined,
                  fieldOperator: rule.op,
                  fieldValue: rule.value ? rule.value.join(',') : undefined,
                  fieldValueType: rule.operatorType
                };
                filterGroup.push(filter);
              });
            }
            convertedDataFilters.push(filterGroup);
            submitData.dataFilters = convertedDataFilters;
          }

          console.log('转换后的dataFilters:', convertedDataFilters);
        }
      }
    }
  };

  const handleModalCancel = () => {
    console.log('取消创建数据权限');
    setModelVisible(false);
  };
  return (
    <>
      {!menuId ? (
        <div className={styles.permissionEmpty}>
          <IconEmpty fontSize={50} />
          暂无页面权限权限，请先添加页面
        </div>
      ) : (
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
            appEntity={appEntity}
            dataPermissionPerson={dataPermissionPerson}
            appEntityFields={appEntityFields}
            filterFieldCheckType={filterFieldCheckType}
            dataPermissionScope={dataPermissionScopeType}
            // changeEntity={changeEntity}
            handleModalSubmit={(values: AuthDataGroupVO) => handleModalSubmit(values)}
            handleModalCancel={() => handleModalCancel()}
          />
        </div>
      )}
    </>
  );
};

export default DataPermission;

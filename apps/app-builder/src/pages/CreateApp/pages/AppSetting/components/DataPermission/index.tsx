import { Button, Divider, Popconfirm, Space, Tag } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconEmpty, IconPlusCircle } from '@arco-design/web-react/icon';
import {
  updateDataGroupPermission,
  deleteDataGroup,
  // getEntityFieldsWithChildren
  // getAppEntities,
  getDataPermission,
  getEntityFields,
  getFieldCheckTypeApi,
  getScopeTypeApi,
  // loadPageSet,
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
  // getPageSetId,
  // type GetPageSetIdReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import DataPermissionModal from './components/DataPermissionModal';

import styles from './index.module.less';

const initialFormValues: AuthDataGroupVO = {
  id: '',
  groupName: '',
  description: '',
  scopeFieldId: undefined,
  scopeLevel: undefined,
  scopeValue: '',
  dataFilters: [],
  isOperable: 1
};

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
  const [DataPermission, setDataPermission] = useState<AuthDataGroupVO[]>([
    {
      groupName: '默认权限',
      description: '系统提供的默认权限',
      entityId: 3,
      entityName: '',
      isOperable: 0,
      scopeFieldId: 1,
      scopeLevel: 'self',
      scopeValue: ''
    }
  ]);

  const [editingPermData, setEditingPermData] = useState<any>(null);
  const [modalVisible, setModelVisible] = useState<boolean>(false);

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
      getScopeType();
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
    console.log('数据权限', res.authDataGroups);
    getViewDataEntity();
    const addDisabled = res.authDataGroups.map((field: AuthDataGroupVO) => ({
      ...field
    }));
    setDataPermission(addDisabled);
    // setIsAllFieldsAllowed(res.isAllFieldsAllowed || 0);
  };

  // 打开model
  const handleModal = async (status: 'create' | 'edit', id?: string) => {
    console.log('Modal id', id);
    setStatus(status);
    setModelVisible(true);

    if (id) {
      // 查找要编辑的权限组
      const permToEdit = DataPermission.find((perm) => perm.id === id);
      if (permToEdit && permToEdit.dataFilters) {
        // 将后端数据格式转换为condition-editor组件需要的格式
        const conditionFormat = convertBackendDataToConditionFormat(permToEdit.dataFilters);
        // 设置正在编辑的数据
        setEditingPermData({
          ...permToEdit,
          filterCondition: conditionFormat
        });
      }
    } else {
      // 创建模式下清空编辑数据
      setEditingPermData(null);
    }

    // getViewDataEntity();
  };

  const getScopeType = async () => {
    try {
      const scopeTypeResq = await getScopeTypeApi();
      SetDataPermissionScopeType(scopeTypeResq);
      console.log('scopeTypeResq:', scopeTypeResq);
    } catch (error) {
      console.log('获取权限范围失败 error:', error);
    }
  };

  // const getPageSetId = async (params: GetPageSetIdReq) => {
  //   try {
  //     const resq = await getPageSetId(params);
  //     console.log('获取页面数据集id resq:', resq);
  //   } catch (error) {
  //     console.log('获取页面数据集id失败 error:', error);
  //   }
  // };
  // 获取页面数据实体
  const getViewDataEntity = async () => {
    // 暂时没有获取页面表单绑定主实体的接口 mock数据
    // console.log('menuId:', menuId);
    // console.log('appId:', appId);
    // console.log('roleId:', roleId);
    // getPageSetId({ menuId });
    // try {
    //   // const params = { id: menuId };
    //   // const resq = await loadPageSet(params);
    // } catch (error) {
    //   console.log('获取页面集失败 error:', error);
    // }
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
      // console.log('根据实体ID获取数据字段权限 entityFieldsResq:', entityFieldsResq);
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
      // console.log('setAppEntityFields', appEntityFields);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };
  // 获取数据权限角色
  const getDataPermissionRoles = async (entityId: string) => {
    try {
      const dataPermissionRoles = await getEntityFields({ entityId, isPerson: 1 });
      // console.log('获取数据权限角色 dataPermissionRoles:', dataPermissionRoles);
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

  /**
   * 处理权限范围数据以满足后端要求
   * @param values 原始表单数据
   * @param submitData 处理后的提交数据
   */
  const processScopeLevelData = (values: AuthDataGroupVO, submitData: any) => {
    // 如果选择的是指定成员或者指定部门，将数据转为JSON字符串给scopeValue
    if (values.scopeLevel === 'specifiedPerson' || values.scopeLevel === 'specifiedDepartment') {
      console.log('进入权限范围是指定的条件判断');
      if (values.scopeValue && values.scopeValue.length > 0) {
        console.log('123');
        submitData.scopeValue = values.scopeValue.join(',');
      }
    }
  };

  const processDataFilters = (values: any, submitData: any) => {
    // 使用新添加的转换方法来处理filterCondition
    if (values.filterCondition) {
      submitData.dataFilters = convertConditionDataToBackendFormat(values.filterCondition);
    }
  };

  /**
   * 将condition-editor组件生成的数据结构转换为后端需要的数据结构
   * @param filterCondition 组件生成的数据结构
   * @returns 后端需要的数据结构
   */
  const convertConditionDataToBackendFormat = (filterCondition: any[]): Array<AuthDataFilterVO[]> => {
    if (!Array.isArray(filterCondition) || filterCondition.length === 0) {
      return [];
    }

    const result: Array<AuthDataFilterVO[]> = [];

    filterCondition.forEach((orGroup, groupIndex) => {
      if (!orGroup || !Array.isArray(orGroup.conditions)) {
        return;
      }

      const convertedGroup: AuthDataFilterVO[] = [];

      orGroup.conditions.forEach((andCondition: any, conditionIndex: number) => {
        if (!andCondition) {
          return;
        }

        // 处理值的格式
        let fieldValue = andCondition.value;
        if (Array.isArray(fieldValue)) {
          // 如果是数组（如范围值），转换为逗号分隔的字符串
          fieldValue = fieldValue.join(',');
        } else if (typeof fieldValue === 'object' && fieldValue !== null) {
          // 如果是对象（如日期范围），转换为适当的格式
          if (fieldValue.begin !== undefined && fieldValue.end !== undefined) {
            fieldValue = `${fieldValue.begin},${fieldValue.end}`;
          } else {
            fieldValue = JSON.stringify(fieldValue);
          }
        }

        const convertedCondition: AuthDataFilterVO = {
          conditionGroup: groupIndex + 1, // 条件组编号从1开始
          conditionOrder: conditionIndex + 1, // 条件顺序从1开始
          fieldId: andCondition.fieldId ? Number(andCondition.fieldId) : undefined,
          fieldOperator: andCondition.op || '',
          fieldValue: fieldValue !== undefined ? fieldValue : '',
          fieldValueType: andCondition.operatorType || 'value',
          id: '' // 新建时id为空
        };

        convertedGroup.push(convertedCondition);
      });

      if (convertedGroup.length > 0) {
        result.push(convertedGroup);
      }
    });

    return result;
  };

  /**
   * 将后端返回的数据结构转换为condition-editor组件需要的数据结构
   * @param backendData 后端返回的数据结构
   * @returns condition-editor组件需要的数据结构
   */
  const convertBackendDataToConditionFormat = (backendData: Array<AuthDataFilterVO[]> | undefined): any[] => {
    if (!backendData || !Array.isArray(backendData) || backendData.length === 0) {
      return [];
    }

    // 按条件组分组数据
    const groupedData: { [key: number]: AuthDataFilterVO[] } = {};

    backendData.forEach((group) => {
      if (Array.isArray(group)) {
        group.forEach((condition) => {
          const groupNumber = condition.conditionGroup || 0;
          if (!groupedData[groupNumber]) {
            groupedData[groupNumber] = [];
          }
          groupedData[groupNumber].push(condition);
        });
      }
    });

    // 转换为前端需要的格式
    const result: any[] = [];

    Object.keys(groupedData)
      .sort((a, b) => Number(a) - Number(b)) // 按组号排序
      .forEach((groupNumber) => {
        const groupConditions = groupedData[Number(groupNumber)];

        // 按条件顺序排序
        groupConditions.sort((a, b) => (a.conditionOrder || 0) - (b.conditionOrder || 0));

        // 转换每个条件为前端格式
        const convertedConditions = groupConditions.map((condition) => {
          // 处理字段值
          let value = condition.fieldValue || '';

          // 根据字段值类型处理特殊值格式
          if (condition.fieldValueType === 'value') {
            // 如果是逗号分隔的值，尝试转换为数组
            if (value.includes(',')) {
              // 检查是否是范围值 (begin,end 格式)
              const rangeValues = value.split(',');
              if (rangeValues.length === 2 && !isNaN(Number(rangeValues[0])) && !isNaN(Number(rangeValues[1]))) {
                // 可能是范围值，但需要更多信息才能确定
                // 这里我们保守处理，仍然作为字符串数组
                value = rangeValues;
              } else {
                // 普通数组值
                value = rangeValues;
              }
            }
          }

          return {
            fieldId: condition.fieldId ? String(condition.fieldId) : '',
            op: condition.fieldOperator || '',
            operatorType: condition.fieldValueType || 'value',
            value: value
          };
        });

        result.push({
          conditions: convertedConditions
        });
      });

    return result;
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

    // 删除原始的filterCondition数据，因为我们已经转换为dataFilters了
    delete submitData.filterCondition;

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

  const handleModalCancel = () => {
    console.log('取消创建数据权限');
    setModelVisible(false);
  };

  const handleDelete = async (id: string) => {
    console.log('删除数据权限 id:', id);
    // await deleteDataGroup(id);
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
          {DataPermission.map((perm, index) => (
            <div className={styles.permItem} key={index}>
              <div className={styles.top}>
                <div className={styles.left}>
                  <div className={styles.title}>{perm.groupName}</div>
                  <div className={styles.subtitle}>{perm.description || '-'}</div>
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
                      handleDelete(perm.id);
                    }}
                    // onCancel={() => {
                    //   console.log('取消删除');
                    // }}
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
                    <Tag visible={!perm.isOperable} color="#F2F3F5" style={{ color: '#1D2129' }}>
                      操作
                    </Tag>
                    <Tag color="#E8F3FF" style={{ color: '#3C7EFF' }}>
                      {perm.scopeFieldId}
                    </Tag>
                    是
                    <Tag color="#FFF7E8" style={{ color: '#FF7D00' }}>
                      {(perm.scopeLevel &&
                        dataPermissionScopeType.find((item) => item.value === perm.scopeLevel)?.label) ||
                        perm.scopeLevel}
                    </Tag>
                    {perm.dataFilters && perm.dataFilters.length > 0 && (
                      <>
                        且
                        {perm.dataFilters.map((group, groupIndex: number) => (
                          <span key={groupIndex}>
                            {group.map((filter, filterIndex: number) => (
                              <Tag
                                color="#E8FFEA"
                                style={{ color: '#00B42A', margin: '0 4px' }}
                                key={`${groupIndex}-${filterIndex}`}
                              >
                                {filter.fieldId} {filter.fieldOperator} {filter.fieldValue}
                              </Tag>
                            ))}
                            {groupIndex < perm.dataFilters.length - 1 && <span>或</span>}
                          </span>
                        ))}
                      </>
                    )}
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
            initialFormValues={editingPermData || initialFormValues}
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

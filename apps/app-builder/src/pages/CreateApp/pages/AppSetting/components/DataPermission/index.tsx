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
  getPageSetId,
  loadPageSet,
  getEntityById,
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
  type ScopeTypeOption,
  type LoadPageSetReq,
  FieldType
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

const opCodeOptions = [
  {
    label: '公式',
    value: FieldType.FORMULA
  },
  {
    label: '静态值',
    value: FieldType.VALUE
  },
  {
    label: '变量',
    value: FieldType.VARIABLES
  }
];

const operatorOptions = [
  { label: '等于', value: 'EQUALS' },
  { label: '不等于', value: 'NOT_EQUALS' },
  { label: '包含', value: 'CONTAINS' },
  { label: '不包含', value: 'NOT_CONTAINS' },
  { label: '存在于', value: 'EXISTS' },
  { label: '不存在于', value: 'NOT_EXISTS' },
  { label: '大于', value: 'GREATER_THAN' },
  { label: '大于等于', value: 'GREATER_EQUAL' },
  { label: '小于', value: 'LESS_THAN' },
  { label: '小于等于', value: 'LESS_EQUAL' },
  { label: '晚于', value: 'LATER_THAN' },
  { label: '早于', value: '' },
  { label: '包含全部', value: 'CONTAINS_ALL' },
  { label: '不包含全部', value: 'NOT_CONTAINS_ALL' },
  { label: '包含任一', value: 'CONTAINS_ANY' },
  { label: '不包含任一', value: 'NOT_CONTAINS_ANY' },
  { label: '不为空', value: 'IS_NOT_EMPTY' },
  { label: '为空', value: 'IS_EMPTY' }
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
  const [DataPermission, setDataPermission] = useState<AuthDataGroupVO[]>([
    {
      groupName: '默认权限',
      description: '系统提供的默认权限',
      entityId: '3',
      entityName: '',
      isOperable: 0,
      scopeFieldName: '拥有者',
      scopeFieldId: 1,
      scopeLevel: 'self',
      scopeValue: ''
    }
  ]);

  const [editingPermData, setEditingPermData] = useState<any>(null);
  const [modalVisible, setModelVisible] = useState<boolean>(false);
  const [dataPermissionEntityName, setDataPermissionEntityName] = useState<string>('');

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
      getScopeType();
      getSetIdFromMenuId();
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
    // getViewDataEntity();
    const addDisabled = res.authDataGroups.map((field: AuthDataGroupVO) => ({
      ...field
    }));
    setDataPermission((prevDataPermission) => {
      // 保留第一个默认权限组，将获取到的数据添加到后面
      const defaultPermission = prevDataPermission[0];
      return [defaultPermission, ...addDisabled];
    });
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

    // getSetIdFromMenuId();
    // getViewDataEntity();

    // getViewDataEntity({ id: '16935056057237504' });
    // getViewDataEntity({ id: menuId });
  };

  const getScopeType = async () => {
    try {
      const scopeTypeResq = await getScopeTypeApi();
      SetDataPermissionScopeType(scopeTypeResq);
      // console.log('scopeTypeResq:', scopeTypeResq);
    } catch (error) {
      console.log('获取权限范围失败 error:', error);
    }
  };

  const getSetIdFromMenuId = async () => {
    try {
      const resq = await getPageSetId({ menuId: menuId });
      console.log('getSetIdFromMenuId resq:', resq);
      // getEntityInfoById(resq);
      loadPageSetForId({ id: resq });
    } catch (error) {
      console.log('获取数据集id失败 error:', error);
    }
  };

  // loadPageSet
  const loadPageSetForId = async (params: LoadPageSetReq) => {
    console.log('载入数据集获取id params:', params);
    try {
      const resq = await loadPageSet(params);
      console.log('载入数据集获取id resq:', resq);
      console.log('载入数据集获取id resq.mainMetadata:', resq.mainMetadata);
      getViewDataEntity(resq.mainMetadata);
    } catch (error) {
      console.log('载入数据集获取id失败 error:', error);
    }
  };

  // 获取页面数据实体 getEntityById
  const getViewDataEntity = async (id: string) => {
    try {
      const resq = await getEntityById(id);
      console.log('根据ID获取业务实体详细信息 resq:', resq.id);
      setDataPermissionEntityName(resq.displayName);
      getDataPermissionFields(resq.id);
      getDataPermissionRoles(resq.id);
    } catch (error) {
      console.log('获取数据集详细信息 error:', error);
    }
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
    console.log('批量获取字段可选校验类型 fieldCheckTypeResq:', fieldCheckTypeResq);
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
    console.log('改变为后端需要数据结构 filterCondition:', filterCondition);
    if (!Array.isArray(filterCondition) || filterCondition.length === 0) {
      return [];
    }

    const result: Array<AuthDataFilterVO[]> = [];

    filterCondition.forEach((orGroup, groupIndex) => {
      if (!orGroup || !Array.isArray(orGroup.conditions)) {
        return;
      }
      console.log('改变为后端需要数据结构 orGroup:', orGroup);

      const convertedGroup: AuthDataFilterVO[] = [];

      orGroup.conditions.forEach((andCondition: any, conditionIndex: number) => {
        console.log('改变为后端需要数据结构 andCondition:', andCondition);
        if (!andCondition) {
          return;
        }

        // 处理值的格式
        let fieldValue = andCondition.value;
        console.log('fieldValue.fieldId:', andCondition.fieldId);
        console.log('fieldValue.value:', andCondition.value);
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
          fieldId: andCondition.fieldId,
          fieldOperator: andCondition.op || '',
          fieldValue: fieldValue !== undefined ? fieldValue : '',
          fieldValueType: andCondition.operatorType || 'value',
          id: '' // 新建时id为空
        };
        console.log('改变为后端需要数据结构 convertedCondition fieldId:', convertedCondition.fieldId);
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
    console.log('editingPermData?.id:', editingPermData);

    if (!values) return;

    // 创建符合后端要求的数据结构
    const submitData = { ...values };

    // 如果是编辑模式，确保ID被包含在提交数据中
    if (status === 'edit' && editingPermData?.id) {
      submitData.id = editingPermData.id;
    }

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
    // 调用后端API提交数据;
    try {
      await updateDataGroupPermission(requestData);
      // 提交成功后刷新数据或关闭模态框
      setModelVisible(false);
      // 提交后刷新数据
      await getFieldsPermission();
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
    await deleteDataGroup(id);
    // 提交后刷新数据
    await getFieldsPermission();
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
                      handleDelete(perm.id!);
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
                      {perm.scopeFieldName}
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
                          <span key={`groupIndex-${groupIndex}`}>
                            {group.map((filter) => (
                              <Tag color="#E8FFEA" style={{ color: '#00B42A', margin: '0 4px' }} key={filter.id}>
                                {filter.fieldName}
                                {/* {filter.fieldOperator} */}{' '}
                                {operatorOptions.find((option) => option.value === filter.fieldOperator)?.label ||
                                  filter.fieldOperator}{' '}
                                {filter.fieldValueType
                                  ? opCodeOptions.find((option) => option.value === filter.fieldValueType)?.label ||
                                    filter.fieldValueType
                                  : ''}{' '}
                                {filter.fieldValue}
                              </Tag>
                            ))}
                            {groupIndex < perm.dataFilters!.length - 1 && <span>或</span>}
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
            // appEntity={appEntity}
            dataPermissionEntityName={dataPermissionEntityName}
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

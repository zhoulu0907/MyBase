import { Button, Divider, Message, Popconfirm, Space, Tag } from '@arco-design/web-react';
import { IconDelete, IconEdit, IconEmpty, IconPlusCircle } from '@arco-design/web-react/icon';
import {
  deleteDataGroup,
  getDataPermission,
  getEntityById,
  getEntityFields,
  getEntityFieldsWithChildren,
  getFieldCheckTypeApi,
  getPageSetId,
  loadPageSet,
  updateDataGroupPermission,
  type AppEntityField,
  type AuthDataFilterVO,
  type AuthDataGroupVO,
  type AuthDataPermissionPersonVO,
  type EntityFieldValidationTypes,
  type EntityWithChildren,
  type GetPermissionReq,
  type LoadPageSetReq,
  type MetadataEntityPair,
  type UpdateDataGroupPermissionReq
} from '@onebase/app';
import { useEffect, useState, type FC } from 'react';
import DataPermissionModal from './components/DataPermissionModal';

import type { TreeSelectDataType } from '@arco-design/web-react/es/TreeSelect/interface';
import styles from './index.module.less';
import { OPERATION_OPTIONS, PERMISSION_SCOPE } from '@onebase/common';

const initialFormValues: AuthDataGroupVO = {
  id: '',
  groupName: '',
  description: '',
  scopeTags: ['ownSubmit'],
  scopeFieldUuid: undefined,
  scopeLevel: undefined,
  scopeValue: '',
  dataFilters: [],
  filterCondition: [],
  operationTags: []
};

interface IProps {
  appId: string;
  menuId: string;
  roleId: string;
}

// 数据权限
const DataPermission: FC<IProps> = ({ appId, menuId, roleId }: IProps) => {
  const [status, setStatus] = useState<'create' | 'edit'>('create');
  const [appEntityFields, setAppEntityFields] = useState<AppEntityField[]>([]);
  const [dataPermissionPerson, setDataPermissionPerson] = useState<AuthDataPermissionPersonVO[]>([]);
  const [filterFieldCheckType, setFilterFieldCheckType] = useState<EntityFieldValidationTypes[]>([]);
  const [DataPermission, setDataPermission] = useState<AuthDataGroupVO[]>([]);

  const [editingPermData, setEditingPermData] = useState<AuthDataGroupVO | null>(null);
  const [variableOptions, setVariableOptions] = useState<TreeSelectDataType[]>([]);
  const [modalVisible, setModelVisible] = useState<boolean>(false);
  const [dataPermissionEntity, setDataPermissionEntity] = useState<MetadataEntityPair>();

  useEffect(() => {
    if (appId && menuId && roleId) {
      getFieldsPermission();
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
    const addDisabled = res.authDataGroups.map((field: AuthDataGroupVO) => ({
      ...field
    }));
    // 后端返回默认权限组
    // setDataPermission(addDisabled);
    // 前端生成默认权限组
    setDataPermission(() => {
      // 保留第一个默认权限组，将获取到的数据添加到后面
      // const defaultPermission = prevDataPermission[0];
      return [...addDisabled];
    });
  };

  // 打开model
  const handleModal = async (status: 'create' | 'edit', id?: string) => {
    setStatus(status);
    setModelVisible(true);

    if (id) {
      // 查找要编辑的权限组
      const permToEdit = DataPermission.find((perm) => perm.id === id);
      if (permToEdit) {
        // 创建编辑数据对象
        const editingData: AuthDataGroupVO = { ...permToEdit };
        // 处理数据过滤条件
        if (permToEdit.dataFilters) {
          // 将后端数据格式转换为condition-editor组件需要的格式
          editingData.filterCondition = (permToEdit.dataFilters ?? []).map((item: any) => ({
            // 假设原来是 item.conditions 或类似字段，改成你真实的字段名
            conditions: (item ?? []).map(normalizeCondition)
          }));
        }

        // 处理指定成员或指定部门的情况，将scopeValue从JSON字符串转回数组
        if (
          (permToEdit.scopeLevel === 'specifiedPerson' || permToEdit.scopeLevel === 'specifiedDepartment') &&
          permToEdit.scopeValue
        ) {
          let scopeValue = permToEdit.scopeValue;
          if (typeof scopeValue === 'string') {
            try {
              scopeValue = JSON.parse(scopeValue);
            } catch (e) {
              console.error('解析scopeValue失败:', e);
              // 如果解析失败，保持原值
            }
          }
          editingData.scopeValue = scopeValue;
        }

        // 设置正在编辑的数据
        setEditingPermData(editingData);
      }
    } else {
      // 创建模式下清空编辑数据
      // setEditingPermData(null);
      setEditingPermData({ ...initialFormValues });
    }
  };

  const normalizeCondition = (c: any) => ({
    fieldId: c?.fieldId != null ? String(c.fieldId) : '',
    op: c?.fieldOperator ?? '',
    operatorType: c?.fieldValueType ?? 'value',
    value: c?.fieldValue
  });

  const getSetIdFromMenuId = async () => {
    try {
      const resq = await getPageSetId({ menuId: menuId });
      // getEntityInfoById(resq);
      loadPageSetForId({ id: resq });
    } catch (error) {
      console.error('获取数据集id失败 error:', error);
    }
  };

  // loadPageSet
  const loadPageSetForId = async (params: LoadPageSetReq) => {
    try {
      const resq = await loadPageSet(params);
      getViewDataEntity(resq.mainMetadata);
      getEntityInfoById(resq.mainMetadata);
    } catch (error) {
      console.error('载入数据集获取id失败 error:', error);
    }
  };

  // 获取页面数据实体 getEntityById
  const getViewDataEntity = async (id: string) => {
    try {
      const resq = await getEntityById(id);
      setDataPermissionEntity({
        entityId: id,
        entityName: resq.displayName,
        tableName: resq.tableName
      });
      getDataPermissionFields(resq.id, resq.tableName);
      getDataPermissionRoles(resq.id);
    } catch (error) {
      console.error('获取数据集详细信息 error:', error);
    }
  };

  // 根据实体ID查询实体名称及其关联的子表信息
  const getEntityInfoById = async (entityId: string) => {
    try {
      const entityInfoResq = await getEntityFieldsWithChildren(entityId);
      const treeSelectData = convertEntityToTreeSelectData(entityInfoResq);
      setVariableOptions(treeSelectData);
      // entityInfoResq 获取的 entityId 是 id 但是 appEntityField 中 是 fieldID
      // entityInfoResq.forEach((field: any) => {
      //   field.fieldId = field.id;
      // });
    } catch (error) {
      console.error('获取数据集详细信息 error:', error);
    }
  };

  const convertEntityToTreeSelectData = (entityData: EntityWithChildren): TreeSelectDataType[] => {
    const result: TreeSelectDataType[] = [];

    // 添加主实体节点
    if (entityData.entityId && entityData.entityName) {
      const mainEntityNode: TreeSelectDataType = {
        key: `entity-${entityData.entityId}`, // 使用 entity- 前缀避免冲突
        title: entityData.entityName,
        value: entityData.entityCode,
        disabled: true,
        children: []
      };

      // 添加父表字段
      if (entityData.parentFields && entityData.parentFields.length > 0) {
        entityData.parentFields.forEach((field: AppEntityField) => {
          if (field.fieldId && field.fieldName) {
            mainEntityNode.children?.push({
              key: `entity-${entityData.entityId}.${field.fieldId}`, // 关键：使用 "父节点ID.字段ID" 格式
              title: field.displayName || field.fieldName,
              value: field.fieldName
            });
          }
        });
      }

      result.push(mainEntityNode);
    }

    // 添加子表实体
    if (entityData.childEntities && entityData.childEntities.length > 0) {
      entityData.childEntities.forEach((child: any) => {
        console.log('添加子表实体 child:', child);
        if (child.childEntityId && child.childEntityName) {
          const childEntityNode: TreeSelectDataType = {
            key: `child-${child.childEntityId}`,
            title: child.childEntityName,
            value: child.childEntityCode,
            disabled: true,
            children: []
          };

          // 添加子表字段
          if (child.childFields && child.childFields.length > 0) {
            child.childFields.forEach((field: any) => {
              if (field.fieldId && field.fieldName) {
                childEntityNode.children?.push({
                  key: `child-${child.childEntityId}.${field.fieldId}`, // 关键：使用 "父节点ID.字段ID" 格式
                  title: field.displayName || field.fieldName,
                  value: field.fieldName
                });
              }
            });
          }

          result.push(childEntityNode);
        }
      });
    }

    return result;
  };

  // 获取数据权限数据字段
  const getDataPermissionFields = async (entityId: string, tableName: string) => {
    try {
      const entityFieldsResq = await getEntityFields({ entityId });
      // entityFieldsResq 返回的数据 是 id 但是 appEntityField 中 是 fieldID
      entityFieldsResq.forEach((field: AppEntityField) => {
        field.fieldId = field.id;
      });

      // 批量获取字段可选校验类型
      const getFieldCheckTypeParams: string[] = [];
      entityFieldsResq.forEach((item: AppEntityField) => {
        if (item.fieldId) {
          getFieldCheckTypeParams.push(item.fieldId);
        }
      });

      // 添加空数组检查，避免空参数调用接口
      if (getFieldCheckTypeParams.length > 0) {
        getFieldCheckType(getFieldCheckTypeParams, entityFieldsResq, tableName);
      } else {
        // 如果没有字段需要获取校验类型，直接设置空数组
        setFilterFieldCheckType([]);
      }

      setAppEntityFields(entityFieldsResq);
    } catch (error) {
      console.error('获取权限信息失败', error);
    }
  };

  // 获取数据权限角色
  const getDataPermissionRoles = async (entityId: string) => {
    try {
      const dataPermissionRoles = await getEntityFields({ entityId, isPerson: 1 });
      // 将获取到的数据转换为正确的格式
      const formattedData = dataPermissionRoles.map((item: AuthDataPermissionPersonVO) => ({
        PersonId: item.id,
        fieldName: item.fieldName,
        displayName: item.displayName?.replace(/\s*id$/i, ''),
        entityID: item.entityId
      }));

      // 更新状态
      setDataPermissionPerson(formattedData);
    } catch (error) {
      console.error('获取数据权限角色失败', error);
    }
  };
  // 批量获取字段可选校验类型
  const getFieldCheckType = async (fieldIds: string[], entityFieldsResq: any[], tableName: string) => {
    try {
      const fieldCheckTypeResq = await getFieldCheckTypeApi(fieldIds);
      fieldCheckTypeResq.forEach((item: EntityFieldValidationTypes) => {
        const fieldName =
          entityFieldsResq.find((field: AppEntityField) => field.fieldId == item.fieldId)?.fieldName || '';
        item.fieldKey = `${tableName}.${fieldName}`;
      });
      setFilterFieldCheckType(fieldCheckTypeResq);
    } catch (error) {
      console.error('批量获取字段可选校验类型 error:', error);
    }
  };

  /**
   * 处理权限范围数据以满足后端要求
   * @param values 原始表单数据
   * @param submitData 处理后的提交数据
   */
  const processScopeLevelData = (values: AuthDataGroupVO, submitData: AuthDataGroupVO) => {
    // 如果选择的是指定成员或者指定部门，将数据转为JSON字符串给scopeValue
    if (values.scopeLevel === 'specifiedPerson' || values.scopeLevel === 'specifiedDepartment') {
      if (values.scopeValue && values.scopeValue.length > 0) {
        submitData.scopeValue = JSON.stringify(values.scopeValue);
      }
    }
  };

  const processDataFilters = (values: AuthDataGroupVO, submitData: AuthDataGroupVO) => {
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

    filterCondition.forEach((orGroup) => {
      if (!orGroup || !Array.isArray(orGroup.conditions)) {
        return;
      }

      const convertedGroup: AuthDataFilterVO[] = [];

      orGroup.conditions.forEach((andCondition: any) => {
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
          fieldId: andCondition.fieldId,
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
        console.log('前端展示需要的数据 convertedConditions:', convertedConditions);
        result.push({
          conditions: convertedConditions
        });
      });

    return result;
  };

  const handleModalSubmit = async (values?: AuthDataGroupVO) => {
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
      Message.success(status === 'edit' ? '修改数据权限成功' : '添加数据权限成功');
    } catch (error) {
      console.error('提交数据权限失败:', error);
    }
  };

  const handleModalCancel = () => {
    setModelVisible(false);
    setEditingPermData(null);
  };

  const handleDelete = async (id: string) => {
    await deleteDataGroup(id);
    // 提交后刷新数据
    await getFieldsPermission();
  };
  return (
    <>
      {menuId && (
        <div className={styles.dataPermission}>
          {DataPermission.length > 0 &&
            DataPermission.map((perm, index) => (
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
                      disabled={DataPermission.length <= 1}
                    >
                      <IconDelete
                        style={{
                          fontSize: 20,
                          color: DataPermission.length <= 1 ? '#C9CDD4' : '#F53F3F',
                          marginLeft: 10,
                          cursor: DataPermission.length <= 1 ? 'not-allowed' : 'pointer'
                        }}
                        // disabled={!perm.id}
                      />
                    </Popconfirm>
                  </div>
                </div>
                <Divider style={{ margin: '10px 0' }} />
                <div className={styles.bottom}>
                  <Space direction="vertical">
                    <Space>
                      <span className={styles.name}>操作范围：</span>
                      {perm.scopeTags?.map((tag: string) => (
                        <Tag key={tag}>{PERMISSION_SCOPE[tag]}</Tag>
                      ))}
                    </Space>
                    <Space>
                      <span className={styles.name}>数据过滤：</span>
                      {perm.dataFilters && perm.dataFilters.length > 0 ? (
                        <span className={styles.name}>自定义</span>
                      ) : (
                        '-'
                      )}
                    </Space>
                    <Space>
                      <span className={styles.name}>操作权限：</span>
                      <Tag>查看</Tag>
                      {perm.operationTags?.map((tag: string) => (
                        <Tag key={tag}>{OPERATION_OPTIONS[tag.toLowerCase()]}</Tag>
                      ))}
                    </Space>
                  </Space>
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
            dataPermissionEntity={dataPermissionEntity}
            dataPermissionPerson={dataPermissionPerson}
            appEntityFields={appEntityFields}
            filterFieldCheckType={filterFieldCheckType}
            variableOptions={variableOptions}
            handleModalSubmit={(values: AuthDataGroupVO) => handleModalSubmit(values)}
            handleModalCancel={() => handleModalCancel()}
          />
        </div>
      )}
    </>
  );
};

export default DataPermission;

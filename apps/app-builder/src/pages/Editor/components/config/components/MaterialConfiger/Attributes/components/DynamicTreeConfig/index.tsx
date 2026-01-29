import { Button, Checkbox, Form, Input, InputNumber, Message, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import {
  FilterEntityFields,
  getEntityFields,
  type MetadataEntityField,
  type MetadataEntityPair,
  menuSignal,
  PageType
} from '@onebase/app';
import {
  CONFIG_TYPES,
  ENTITY_FIELD_TYPE,
  getPopupContainer,
  useAppEntityStore,
  SELECT_OPTIONS_BPM
} from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;

export interface DynamicTreeConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

// 树组件支持的字段类型
export const supportedTreeFieldTypes = [
  ENTITY_FIELD_TYPE.TEXT.VALUE,
  ENTITY_FIELD_TYPE.NUMBER.VALUE,
  ENTITY_FIELD_TYPE.DATE.VALUE,
  ENTITY_FIELD_TYPE.DATETIME.VALUE,
  ENTITY_FIELD_TYPE.USER.VALUE,
  ENTITY_FIELD_TYPE.DEPARTMENT.VALUE,
  ENTITY_FIELD_TYPE.DATA_SELECTION.VALUE
];

const DynamicTreeConfig: React.FC<DynamicTreeConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const { mainEntity, subEntities } = useAppEntityStore();
  const { form } = Form.useFormContext();

  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [entityFields, setEntityFields] = useState<MetadataEntityField[]>([]);
  const [entityUuid, setEntityUuid] = useState<string>('');
  const [treeFieldsConfig, setTreeFieldsConfig] = useState<any[]>([]);

  const { curMenu } = menuSignal;

  // 实体字段过滤
  const filterEntityFields = (fields: MetadataEntityField[]) => {
    return fields.filter((field) => {
      const isSupported = supportedTreeFieldTypes.includes(field.fieldType);
      const isSystemField = field.fieldName.startsWith('_');
      return isSupported && !isSystemField;
    });
  };

  // 获取实体列表
  useEffect(() => {
    const newEntityList = [];
    if (mainEntity) {
      newEntityList.push({
        entityId: mainEntity.entityId,
        entityUuid: mainEntity.entityUuid,
        tableName: mainEntity.tableName,
        entityName: mainEntity.entityName
      });
    }
    if (subEntities) {
      newEntityList.push(
        ...subEntities.entities.map((entity: any) => ({
          entityId: entity.entityId,
          entityUuid: entity.entityUuid,
          tableName: entity.tableName,
          entityName: entity.entityName
        }))
      );
    }

    console.log('Tree newEntityList: ', newEntityList);
    setEntityList(newEntityList);
  }, [mainEntity, subEntities]);

  // 获取当前组件关联的实体id
  useEffect(() => {
    if (id != configs.id) {
      return;
    }

    if (configs[item.key]) {
      setEntityUuid(configs[item.key]);
    }
  }, []);

  useEffect(() => {
    const treeFields = configs.treeFields || [];
    if (treeFields && JSON.stringify(treeFields) !== JSON.stringify(treeFieldsConfig)) {
      setTreeFieldsConfig(treeFields);
    }
  }, [configs.treeFields]);

  useEffect(() => {
    if (entityUuid) {
      const loadEntityFields = async () => {
        try {
          const fields = await getEntityFields({ entityUuid });

          // 标记禁用的字段类型
          fields.forEach((item: MetadataEntityField) => {
            if (item.fieldType && !supportedTreeFieldTypes.includes(item.fieldType)) {
              item.disabled = true;
            }
          });

          const filteredFields = filterEntityFields(fields);
          setEntityFields(filteredFields);
        } catch (error) {
          console.error('获取实体字段失败:', error);
          Message.error('获取实体字段失败');
        }
      };
      loadEntityFields();
    }
  }, [entityUuid]);

  // 监听树字段配置变化，实时更新组件显示
  useEffect(() => {
    console.log('树字段配置发生变化:', treeFieldsConfig);
  }, [treeFieldsConfig]);

  // 处理实体变更
  const handleEntityChange = (value: string) => {
    const entity = entityList.find((item) => item.entityUuid === value);
    const tableName = entity?.tableName || '';

    handleMultiPropsChange([
      { key: item.key, value: value },
      { key: 'tableName', value: tableName },
      { key: 'treeFields', value: [] }
    ]);

    setEntityUuid(value);
    setTreeFieldsConfig([]);
  };

  // 添加树字段
  const handleAddTreeField = () => {
    const newField = {
      level: treeFieldsConfig.length + 1,
      fieldName: '',
      displayName: '',
      fieldType: ''
    };
    const newConfig = [...treeFieldsConfig, newField];
    setTreeFieldsConfig(newConfig);
    handlePropsChange('treeFields', newConfig);
  };

  // 更新树字段
  const handleTreeFieldChange = (index: number, field: any) => {
    const newConfig = [...treeFieldsConfig];
    newConfig[index] = field;
    setTreeFieldsConfig(newConfig);
    handlePropsChange('treeFields', newConfig);
  };

  // 删除树字段
  const handleDeleteTreeField = (index: number) => {
    const newConfig = treeFieldsConfig.filter((_, idx) => idx !== index);
    // 重新计算层级
    const updatedConfig = newConfig.map((field, idx) => ({
      ...field,
      level: idx + 1
    }));
    setTreeFieldsConfig(updatedConfig);
    handlePropsChange('treeFields', updatedConfig);
  };

  return (
    <div className={styles.dynamicTreeConfig}>
      {/* 数据源选择 */}
      <FormItem layout="vertical" labelAlign="left" label="数据绑定" className={styles.formItem}>
        <Select
          placeholder="请选择数据源"
          value={configs[item.key]}
          getPopupContainer={getPopupContainer}
          onChange={handleEntityChange}
        >
          {entityList.map((item) => (
            <Select.Option key={item.entityUuid} value={item.entityUuid}>
              {item.entityName}
            </Select.Option>
          ))}
        </Select>
      </FormItem>

      {/* 树字段配置 */}
      {entityUuid && (
        <FormItem layout="vertical" labelAlign="left" label="目录字段" className={styles.formItem}>
          <div className={styles.treeFieldsContainer}>
            {treeFieldsConfig.map((field, index) => (
              <div key={index} className={styles.treeFieldItem} style={{ marginBottom: '8px' }}>
                <div className={styles.treeFieldContent} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span>{field.level} 级</span>
                  <Select
                    placeholder="选择字段"
                    value={field.fieldName}
                    style={{ flex: 1 }}
                    onChange={(value) => {
                      const selectedField = entityFields.find((f) => f.fieldName === value);
                      handleTreeFieldChange(index, {
                        ...field,
                        fieldName: value,
                        displayName: selectedField?.displayName || value,
                        fieldType: selectedField?.fieldType || ''
                      });
                    }}
                  >
                    {entityFields.map((field) => (
                      <Select.Option key={field.fieldName} value={field.fieldName}>
                        {field.displayName}
                      </Select.Option>
                    ))}
                  </Select>
                  <Button icon={<IconDelete />} size="small" type="text" onClick={() => handleDeleteTreeField(index)} />
                </div>
              </div>
            ))}
            <Button
              type={treeFieldsConfig.length < 5 ? 'outline' : 'secondary'}
              onClick={handleAddTreeField}
              disabled={treeFieldsConfig.length >= 5}
              style={{ marginTop: '8px' }}
            >
              新增目录字段
            </Button>
          </div>
        </FormItem>
      )}

      {/* 默认展开层级 */}
      <FormItem
        layout="horizontal"
        labelAlign="left"
        label="默认展开层级"
        labelCol={{ span: 9 }}
        wrapperCol={{ span: 5, offset: 10 }}
      >
        <InputNumber
          placeholder="请输入展开层级"
          value={configs.defaultExpandLevel}
          min={1}
          max={5}
          step={1}
          onChange={(value) => handlePropsChange('defaultExpandLevel', value)}
        />
      </FormItem>
    </div>
  );
};

// 注册 Tree 数据配置渲染器
registerConfigRenderer(CONFIG_TYPES.TREE_DATA, ({ id, handleMultiPropsChange, handlePropsChange, item, configs }) => (
  <DynamicTreeConfig
    id={id}
    handleMultiPropsChange={handleMultiPropsChange}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));

export default DynamicTreeConfig;

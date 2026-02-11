import { Button, Form, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import {
  getEntityFields,
  type MetadataEntityField,
  type MetadataEntityPair,
  menuSignal,
  PageType
} from '@onebase/app';
import {
  CONFIG_TYPES,
  getPopupContainer,
  useAppEntityStore
} from '@onebase/ui-kit';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

const FormItem = Form.Item;

export interface DynamicWebViewParamsConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicWebViewParamsConfig: React.FC<DynamicWebViewParamsConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const { mainEntity, subEntities } = useAppEntityStore();
  const { curMenu } = menuSignal;

  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [entityFields, setEntityFields] = useState<MetadataEntityField[]>([]);
  const [entityUuid, setEntityUuid] = useState<string>('');
  const [paramsConfig, setParamsConfig] = useState<any[]>([]);

  const supportedFieldTypes = [
    'TEXT',
    'NUMBER',
    'DATE',
    'DATETIME',
    'USER',
    'DEPARTMENT',
    'DATA_SELECTION'
  ];

  const filterEntityFields = (fields: MetadataEntityField[]) => {
    return fields.filter((field) => {
      const isSupported = supportedFieldTypes.includes(field.fieldType);
      const isSystemField = field.fieldName.startsWith('_');
      return isSupported && !isSystemField;
    });
  };

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
    setEntityList(newEntityList);
  }, [mainEntity, subEntities]);

  useEffect(() => {
    if (id != configs.id) {
      return;
    }

    if (configs[item.key]) {
      setEntityUuid(configs[item.key]);
    }
  }, []);

  useEffect(() => {
    const params = configs.params || [];
    if (params && JSON.stringify(params) !== JSON.stringify(paramsConfig)) {
      setParamsConfig(params);
    }
  }, [configs.params]);

  useEffect(() => {
    const initEntity = async () => {
      const firstEntity = entityList[0];
      if (firstEntity && !entityUuid) {
        const entity = firstEntity;
        const tableName = entity?.tableName || '';

        handleMultiPropsChange([
          { key: item.key, value: entity.entityUuid },
          { key: 'tableName', value: tableName },
          { key: 'params', value: [] }
        ]);

        setEntityUuid(entity.entityUuid);
        setParamsConfig([]);

        try {
          const fields = await getEntityFields({ entityUuid: entity.entityUuid });
          const filteredFields = filterEntityFields(fields);
          setEntityFields(filteredFields);
        } catch (error) {
          console.error('获取实体字段失败:', error);
        }
      }
    };

    if (entityList.length > 0 && !entityUuid) {
      initEntity();
    }
  }, [entityList, entityUuid]);

  useEffect(() => {
    if (entityUuid) {
      const loadEntityFields = async () => {
        try {
          const fields = await getEntityFields({ entityUuid });
          const filteredFields = filterEntityFields(fields);
          setEntityFields(filteredFields);
        } catch (error) {
          console.error('获取实体字段失败:', error);
        }
      };
      loadEntityFields();
    }
  }, [entityUuid]);

  const handleAddParam = () => {
    const newParam = {
      key: '',
      displayName: '',
      fieldType: ''
    };
    const newConfig = [...paramsConfig, newParam];
    setParamsConfig(newConfig);
    handlePropsChange('params', newConfig);
  };

  const handleParamChange = (index: number, field: any) => {
    const newConfig = [...paramsConfig];
    newConfig[index] = field;
    setParamsConfig(newConfig);
    handlePropsChange('params', newConfig);
  };

  const handleDeleteParam = (index: number) => {
    const newConfig = paramsConfig.filter((_, idx) => idx !== index);
    setParamsConfig(newConfig);
    handlePropsChange('params', newConfig);
  };

  return (
    <div className={styles.dynamicWebViewParamsConfig}>
      <FormItem layout="vertical" labelAlign="left" label="参数列表" className={styles.formItem}>
        <div className={styles.paramsContainer}>
          {paramsConfig.map((param, index) => (
            <div key={index} className={styles.paramItem} style={{ marginBottom: '8px' }}>
              <div className={styles.paramContent} style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Select
                  placeholder="选择字段"
                  value={param.key}
                  style={{ flex: 1 }}
                  onChange={(value) => {
                    const selectedField = entityFields.find((f) => f.fieldName === value);
                    handleParamChange(index, {
                      key: value,
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
                <Button icon={<IconDelete />} size="small" type="text" onClick={() => handleDeleteParam(index)} />
              </div>
            </div>
          ))}
          <Button
            type="outline"
            onClick={handleAddParam}
            style={{ marginTop: '8px' }}
          >
            新增参数
          </Button>
        </div>
      </FormItem>
    </div>
  );
};

registerConfigRenderer(CONFIG_TYPES.WEB_VIEW_PARAMS, ({ id, handlePropsChange, handleMultiPropsChange, item, configs }) => (
  <DynamicWebViewParamsConfig
    id={id}
    handleMultiPropsChange={handleMultiPropsChange}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));

export default DynamicWebViewParamsConfig;

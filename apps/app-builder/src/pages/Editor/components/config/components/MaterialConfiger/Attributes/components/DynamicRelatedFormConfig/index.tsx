import { getPopupContainer, useAppEntityStore } from '@onebase/ui-kit';
import { Form, Select, Space } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicRelatedFormConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
}

const DynamicRelatedFormConfig: React.FC<DynamicRelatedFormConfigProps> = ({ handlePropsChange, item, configs }) => {
  const { mainEntity, appEntities } = useAppEntityStore();

  const [entityOptions, setEntityOptions] = useState<Array<{ value: string; label: string }>>([]);
  const [fieldOptions, setFieldOptions] = useState<Array<{ value: string; label: string }>>([]);
  const [selectedEntity, setSelectedEntity] = useState<string>('');
  const [selectedField, setSelectedField] = useState<string>('');

  useEffect(() => {
    console.log(appEntities);
    initEntityOptions();
  }, [appEntities]);

  // 初始化实体选项
  const initEntityOptions = async () => {
    const entityList = appEntities.entities?.filter((entity) => entity.entityID !== mainEntity.entityID);

    const newEntityOptions = entityList?.map((entity) => ({
      value: entity.entityID,
      label: entity.entityName
    }));

    setEntityOptions(newEntityOptions);
  };

  // 当选择实体时，更新字段选项
  const handleEntityChange = (entityId: string) => {
    setSelectedEntity(entityId);
    setSelectedField(''); // 清空字段选择

    // 找到对应的实体并设置字段选项
    const selectedEntityData = appEntities.entities.find((entity) => entity.entityID === entityId);
    if (selectedEntityData) {
      const newFieldOptions = selectedEntityData.fields.map((field) => ({
        value: field.fieldID,
        label: field.displayName
      }));
      setFieldOptions(newFieldOptions);
    } else {
      setFieldOptions([]);
    }

    // 清空配置值
    handlePropsChange(item.key, '');
  };

  // 当选择字段时，更新配置
  const handleFieldChange = (fieldId: string) => {
    setSelectedField(fieldId);

    // 将实体ID和字段ID组合成数组传递给父组件
    const combinedValue = [selectedEntity, fieldId];
    handlePropsChange(item.key, combinedValue);
  };

  // 从配置中解析当前选中的值
  useEffect(() => {
    if (configs[item.key] && Array.isArray(configs[item.key]) && configs[item.key].length === 2) {
      const [entityId, fieldId] = configs[item.key];
      //   setSelectedEntity(entityId);
      //   setSelectedField(fieldId);

      // 设置字段选项
      const selectedEntityData = appEntities.entities.find((entity) => entity.entityID === entityId);
      if (selectedEntityData) {
        const newFieldOptions = selectedEntityData.fields.map((field) => ({
          value: field.fieldID,
          label: field.displayName
        }));
        setFieldOptions(newFieldOptions);
      }
    }
  }, [configs, item.key, appEntities]);

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={item.name} className={styles.formItem}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <Select
            value={selectedEntity}
            placeholder="请选择关联实体"
            style={{ width: '100%' }}
            options={entityOptions}
            onChange={handleEntityChange}
            allowClear
            getPopupContainer={getPopupContainer}
          />
          <Select
            value={selectedField}
            placeholder="请选择关联字段"
            style={{ width: '100%' }}
            options={fieldOptions}
            onChange={handleFieldChange}
            disabled={!selectedEntity}
            allowClear
            getPopupContainer={getPopupContainer}
          />
        </Space>
      </FormItem>
    </>
  );
};

export default DynamicRelatedFormConfig;

import { Form, Button, Image, Select, Switch } from '@arco-design/web-react';
import { IconArrowLeft, IconSwap, IconDelete, IconPlus } from '@arco-design/web-react/icon';
import {
  FilterEntityFields,
  getEntityFields,
  type MetadataEntityField,
  type MetadataEntityPair,
  type AppEntity,
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
import React, { useState, useEffect } from 'react';
import styles from './index.module.less';
import { registerConfigRenderer } from '../../registry';
import CanvasCardType1Image from '@/assets/images/cp/CanvasCardType1.png';
import CanvasCardType2Image from '@/assets/images/cp/CanvasCardType2.png';

const FormItem = Form.Item;

export interface DynamicCanvasCardConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | unknown[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | unknown[]) => void;
  item?: any;
  configs: Record<string, unknown>;
  id?: string;
}

interface FieldConfig {
  fieldName: string;
  displayName: string;
}

interface DisplayFieldsConfig {
  mainImage?: string;
  categoryTags?: string[];
  mainTitle?: string;
  cardContent?: string;
  auxiliaryInfo?: string[];
  countHint?: string;
  avatar?: string;
  cardFields?: string[];
}

const hiddenFieldTypes = [
  ENTITY_FIELD_TYPE.RELATION.VALUE,
  ENTITY_FIELD_TYPE.STRUCTURE.VALUE,
  ENTITY_FIELD_TYPE.ARRAY.VALUE,
  ENTITY_FIELD_TYPE.GEOGRAPHY.VALUE,
  ENTITY_FIELD_TYPE.PASSWORD.VALUE,
  ENTITY_FIELD_TYPE.ENCRYPTED.VALUE,
  ENTITY_FIELD_TYPE.AGGREGATE.VALUE,
  ENTITY_FIELD_TYPE.MULTI_USER.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DEPARTMENT.VALUE,
  ENTITY_FIELD_TYPE.MULTI_DATA_SELECTION.VALUE
];

const FIELD_TYPE_CONFIG = {
  MAIN_IMAGE: [ENTITY_FIELD_TYPE.IMAGE.VALUE],
  AVATAR: [ENTITY_FIELD_TYPE.IMAGE.VALUE],
  CATEGORY_TAGS: [ENTITY_FIELD_TYPE.SELECT.VALUE, ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE],
  MAIN_TITLE: [ENTITY_FIELD_TYPE.TEXT.VALUE, ENTITY_FIELD_TYPE.AUTO_CODE.VALUE],
  CARD_CONTENT: [
    ENTITY_FIELD_TYPE.TEXT.VALUE,
    ENTITY_FIELD_TYPE.LONG_TEXT.VALUE,
    ENTITY_FIELD_TYPE.ADDRESS.VALUE,
    ENTITY_FIELD_TYPE.AUTO_CODE.VALUE,
    ENTITY_FIELD_TYPE.EMAIL.VALUE,
    ENTITY_FIELD_TYPE.PHONE.VALUE,
    ENTITY_FIELD_TYPE.URL.VALUE
  ],
  COUNT_HINT: [ENTITY_FIELD_TYPE.NUMBER.VALUE],
  CARD_FIELDS: []
};

const sortEntityFields = (a: MetadataEntityField, b: MetadataEntityField): number => {
  if (a.isSystemField !== b.isSystemField) {
    return a.isSystemField ? 1 : -1;
  }
  return 0;
};

const DynamicCanvasCardConfig: React.FC<DynamicCanvasCardConfigProps> = ({ handleMultiPropsChange, handlePropsChange, configs }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentComponent, setCurrentComponent] = useState((configs.componentName as string) || 'CanvasCardType1');
  const [displayFields, setDisplayFields] = useState<DisplayFieldsConfig>((configs.displayFields as DisplayFieldsConfig) || {});
  const hiddenDraft = (configs.hiddenDraft as boolean) || false;
  const showAddBtn = (configs.showAddBtn as boolean) !== false;

  const { mainEntity, subEntities } = useAppEntityStore();
  const [entityList, setEntityList] = useState<MetadataEntityPair[]>([]);
  const [entityUuid, setEntityUuid] = useState<string>('');
  const [fieldList, setFieldList] = useState<MetadataEntityField[]>([]);
  const [initialized, setInitialized] = useState(false);

  const tableNameKey = 'tableName';
  const metaDataKey = 'metaData';

  const { curMenu } = menuSignal;

  useEffect(() => {
    setCurrentComponent((configs.componentName as string) || 'CanvasCardType1');
  }, [configs.componentName]);

  useEffect(() => {
    const componentName = (configs.componentName as string) || 'CanvasCardType1';
    const currentDisplayFields = (configs.displayFields as DisplayFieldsConfig) || {};
    setDisplayFields(currentDisplayFields);
  }, [configs.componentName, configs.displayFields]);

  useEffect(() => {
    if (configs[metaDataKey] && configs[metaDataKey] !== entityUuid) {
      setEntityUuid(configs[metaDataKey] as string);
    } else if (!entityUuid && configs[metaDataKey]) {
      setEntityUuid(configs[metaDataKey] as string);
    }
  }, [configs[metaDataKey], entityUuid]);

  useEffect(() => {
    const syncEntityList = () => {
      if (!mainEntity) {
        return;
      }

      const newEntityList: MetadataEntityPair[] = [];
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
          ...subEntities.entities.map((entity: AppEntity) => ({
            entityId: entity.entityId,
            entityUuid: entity.entityUuid,
            tableName: entity.tableName,
            entityName: entity.entityName
          }))
        );
      }

      setEntityList(newEntityList);
      setInitialized(true);
    };

    syncEntityList();
  }, [mainEntity, subEntities]);

  useEffect(() => {
    if (entityUuid && initialized && fieldList.length === 0) {
      getFieldList();
    }
  }, [entityUuid, initialized, fieldList.length]);

  const getFieldList = async () => {
    const res = await getEntityFields({ entityUuid });

    res.forEach((item: MetadataEntityField) => {
      if (item.fieldType && hiddenFieldTypes.includes(item.fieldType)) {
        item.disabled = true;
      }
    });

    const newFieldList = res
      .filter((item: MetadataEntityField) => !FilterEntityFields.includes(item.fieldName))
      .concat(curMenu?.value?.pagesetType === PageType.BPM ? SELECT_OPTIONS_BPM : []);

    setFieldList(newFieldList);
  };

  const handleStyleChange = (componentName: string) => {
    setCurrentComponent(componentName);
    handlePropsChange('componentName', componentName);
  };

  const handleFieldChange = (fieldPath: string, value: string | string[]) => {
    const newDisplayFields: DisplayFieldsConfig = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: DisplayFieldsConfig | string[] = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      const key = keys[i];
      if (!current[key as keyof typeof current]) {
        current[key as keyof typeof current] = Array.isArray(current) ? [] : {};
      }
      current = current[key as keyof typeof current] as DisplayFieldsConfig | string[];
    }

    (current as Record<string, unknown>)[keys[keys.length - 1]] = value;
    setDisplayFields(newDisplayFields);
    handlePropsChange('displayFields', newDisplayFields);
  };

  const handleAddArrayField = (fieldPath: string, maxCount: number) => {
    const newDisplayFields: DisplayFieldsConfig = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: DisplayFieldsConfig | string[] = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      const key = keys[i];
      if (!current[key as keyof typeof current]) {
        current[key as keyof typeof current] = Array.isArray(current) ? [] : {};
      }
      current = current[key as keyof typeof current] as DisplayFieldsConfig | string[];
    }

    const array = (current as Record<string, string[]>)[keys[keys.length - 1]] || [];
    if (array.length < maxCount) {
      (current as Record<string, unknown>)[keys[keys.length - 1]] = [...array, ''];
      setDisplayFields(newDisplayFields);
      handlePropsChange('displayFields', newDisplayFields);
    }
  };

  const handleRemoveArrayField = (fieldPath: string, index: number) => {
    const newDisplayFields: DisplayFieldsConfig = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: DisplayFieldsConfig | string[] = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      const key = keys[i];
      current = current[key as keyof typeof current] as DisplayFieldsConfig | string[];
    }

    const array = (current as Record<string, string[]>)[keys[keys.length - 1]] || [];
    (current as Record<string, unknown>)[keys[keys.length - 1]] = array.filter((_: string, i: number) => i !== index);
    setDisplayFields(newDisplayFields);
    handlePropsChange('displayFields', newDisplayFields);
  };

  const renderFieldOptions = (allowedTypes?: string[], excludedTypes?: string[]) => {
    return fieldList
      .sort(sortEntityFields)
      .filter((item: MetadataEntityField) => {
        if (allowedTypes && allowedTypes.length > 0 && !allowedTypes.includes(item.fieldType)) {
          return false;
        }
        if (excludedTypes && excludedTypes.length > 0 && excludedTypes.includes(item.fieldType)) {
          return false;
        }
        return true;
      })
      .map((item: MetadataEntityField) => (
        <Select.Option key={item.fieldName} value={item.fieldName} disabled={item?.disabled}>
          {item.displayName}
        </Select.Option>
      ));
  };

  const renderStylePreview = () => {
    return (
      <div className={styles.canvasCardStylePreview}>
        <div className={styles.canvasCardStylePreviewImage}>
          <Image
            src={currentComponent === 'CanvasCardType1' ? CanvasCardType1Image : CanvasCardType2Image}
            alt="样式预览"
            style={{ width: '100%', height: 'auto' }}
            preview={false}
          />
        </div>
        <Button
          type="outline"
          icon={<IconSwap />}
          onClick={() => setIsEditing(true)}
          style={{ marginTop: '16px', width: '100%' }}
        >
          更改样式
        </Button>
      </div>
    );
  };

  const renderStyleSelection = () => {
    return (
      <div className={styles.canvasCardStyleSelection}>
        <div className={styles.canvasCardStyleSelectionHeader}>
          <Button type="text" icon={<IconArrowLeft />} onClick={() => setIsEditing(false)} />
          <div className={styles.canvasCardStyleSelectionTitle}>切换外观样式</div>
        </div>
        <div className={styles.canvasCardStyleList}>
          <div
            className={`${styles.canvasCardStyleItem} ${currentComponent === 'CanvasCardType1' ? styles.active : ''}`}
            onClick={() => handleStyleChange('CanvasCardType1')}
          >
            <Image src={CanvasCardType1Image} alt="样式1" style={{ width: '100%', height: 'auto' }} preview={false} />
          </div>
          <div
            className={`${styles.canvasCardStyleItem} ${currentComponent === 'CanvasCardType2' ? styles.active : ''}`}
            onClick={() => handleStyleChange('CanvasCardType2')}
          >
            <Image src={CanvasCardType2Image} alt="样式2" style={{ width: '100%', height: 'auto' }} preview={false} />
          </div>
        </div>
      </div>
    );
  };

  const renderStyle1Fields = () => {
    const categoryTags = displayFields.categoryTags || [];
    const auxiliaryInfo = displayFields.auxiliaryInfo || [];

    return (
      <div className={styles.displayFieldsConfig}>
        <FormItem layout="vertical" labelAlign="left" label="主图" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.mainImage}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('mainImage', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.MAIN_IMAGE)}
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="分类标签" className={styles.formItem}>
          <div className={styles.arrayFieldsContainer}>
            {categoryTags.map((tag: string, index: number) => (
              <div key={index} className={styles.arrayFieldItem}>
                <Select
                  placeholder="请选择字段"
                  value={tag}
                  getPopupContainer={getPopupContainer}
                  onChange={(value) => {
                    const newTags = [...categoryTags];
                    newTags[index] = value;
                    handleFieldChange('categoryTags', newTags);
                  }}
                >
                  {renderFieldOptions(FIELD_TYPE_CONFIG.CATEGORY_TAGS)}
                </Select>
                {index >= 1 && (
                  <Button
                    icon={<IconDelete />}
                    size="small"
                    type="text"
                    onClick={() => handleRemoveArrayField('categoryTags', index)}
                  />
                )}
              </div>
            ))}
            {categoryTags.length < 3 && (
              <Button
                type="outline"
                onClick={() => handleAddArrayField('categoryTags', 3)}
                style={{ marginTop: '8px', width: '88px' }}
              >
                新增字段
              </Button>
            )}
          </div>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="主标题" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.mainTitle}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('mainTitle', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.MAIN_TITLE)}
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="卡片正文" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.cardContent}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('cardContent', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.CARD_CONTENT)}
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="辅助信息" className={styles.formItem}>
          <div className={styles.arrayFieldsContainer}>
            {auxiliaryInfo.map((info: string, index: number) => (
              <div key={index} className={styles.arrayFieldItem}>
                <Select
                  placeholder="请选择字段"
                  value={info}
                  getPopupContainer={getPopupContainer}
                  onChange={(value) => {
                    const newInfo = [...auxiliaryInfo];
                    newInfo[index] = value;
                    handleFieldChange('auxiliaryInfo', newInfo);
                  }}
                >
                  {renderFieldOptions(undefined, [ENTITY_FIELD_TYPE.FILE.VALUE, ENTITY_FIELD_TYPE.IMAGE.VALUE])}
                </Select>
                {index >= 0 && (
                  <Button
                    icon={<IconDelete />}
                    size="small"
                    type="text"
                    onClick={() => handleRemoveArrayField('auxiliaryInfo', index)}
                  />
                )}
              </div>
            ))}
            {auxiliaryInfo.length < 2 && (
              <Button
                type="outline"
                onClick={() => handleAddArrayField('auxiliaryInfo', 2)}
                style={{ marginTop: '8px', width: '88px' }}
              >
                新增字段
              </Button>
            )}
          </div>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="数量提示" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.countHint}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('countHint', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.COUNT_HINT)}
          </Select>
        </FormItem>
      </div>
    );
  };

  const renderStyle2Fields = () => {
    const categoryTags = displayFields.categoryTags || [];
    const cardFields = displayFields.cardFields || [];

    return (
      <div className={styles.displayFieldsConfig}>
        <FormItem layout="vertical" labelAlign="left" label="头像" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.avatar}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('avatar', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.AVATAR)}
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="主标题" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.mainTitle}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('mainTitle', value)}
          >
            {renderFieldOptions(FIELD_TYPE_CONFIG.MAIN_TITLE)}
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="分类标签" className={styles.formItem}>
          <div className={styles.arrayFieldsContainer}>
            {categoryTags.map((tag: string, index: number) => (
              <div key={index} className={styles.arrayFieldItem}>
                <Select
                  placeholder="请选择字段"
                  value={tag}
                  getPopupContainer={getPopupContainer}
                  onChange={(value) => {
                    const newTags = [...categoryTags];
                    newTags[index] = value;
                    handleFieldChange('categoryTags', newTags);
                  }}
                >
                  {renderFieldOptions(FIELD_TYPE_CONFIG.CATEGORY_TAGS)}
                </Select>
                {index >= 0 && (
                  <Button
                    icon={<IconDelete />}
                    size="small"
                    type="text"
                    onClick={() => handleRemoveArrayField('categoryTags', index)}
                  />
                )}
              </div>
            ))}
            {categoryTags.length < 3 && (
              <Button
                type="outline"
                onClick={() => handleAddArrayField('categoryTags', 3)}
                style={{ marginTop: '8px', width: '88px' }}
              >
                新增字段
              </Button>
            )}
          </div>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="卡片正文" className={styles.formItem}>
          <div className={styles.arrayFieldsContainer}>
            {cardFields.map((field: string, index: number) => (
              <div key={index} className={styles.arrayFieldItem}>
                <Select
                  placeholder="请选择字段"
                  value={field}
                  getPopupContainer={getPopupContainer}
                  onChange={(value) => {
                    const newFields = [...cardFields];
                    newFields[index] = value;
                    handleFieldChange('cardFields', newFields);
                  }}
                >
                  {renderFieldOptions()}
                </Select>
                {index >= 4 && (
                  <Button
                    icon={<IconDelete />}
                    size="small"
                    type="text"
                    onClick={() => handleRemoveArrayField('cardFields', index)}
                  />
                )}
              </div>
            ))}
            {cardFields.length < 9 && (
              <Button
                type="outline"
                onClick={() => handleAddArrayField('cardFields', 9)}
                style={{ marginTop: '8px', width: '88px' }}
              >
                新增字段
              </Button>
            )}
          </div>
        </FormItem>
      </div>
    );
  };

  return (
    <div>
      <FormItem layout="vertical" labelAlign="left" label="数据" className={styles.formItem}>
        <Select
          placeholder="请选择数据实体"
          value={entityUuid || undefined}
          getPopupContainer={getPopupContainer}
          onChange={(value) => {
            const selectedEntity = entityList.find((item) => item.entityUuid === value);
            handleMultiPropsChange([
              { key: metaDataKey, value: value },
              { key: tableNameKey, value: selectedEntity?.tableName || '' }
            ]);

            setEntityUuid(value);
          }}
        >
          {entityList.map((item) => (
            <Select.Option key={item.entityUuid} value={item.entityUuid}>
              {item.entityName}
            </Select.Option>
          ))}
        </Select>
      </FormItem>

      <FormItem layout="vertical" labelAlign="left" label="样式库" className={styles.formItem}>
        {isEditing ? renderStyleSelection() : renderStylePreview()}
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="显示字段" className={styles.formItem}>
        {currentComponent === 'CanvasCardType1' ? renderStyle1Fields() : renderStyle2Fields()}
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="添加数据按钮" className={styles.formItem}>
        <Switch
          checked={showAddBtn}
          onChange={(checked) => handlePropsChange('showAddBtn', checked)}
        />
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="草稿箱" className={styles.formItem}>
        <Switch
          checked={!hiddenDraft}
          onChange={(checked) => handlePropsChange('hiddenDraft', !checked)}
        />
      </FormItem>
    </div>
  );
};

export default DynamicCanvasCardConfig;

registerConfigRenderer(CONFIG_TYPES.CANVAS_CARD_CONFIG, ({ id, handleMultiPropsChange, handlePropsChange, item, configs }) => (
  <DynamicCanvasCardConfig
    id={id}
    handleMultiPropsChange={handleMultiPropsChange}
    handlePropsChange={handlePropsChange}
    item={item}
    configs={configs}
  />
));

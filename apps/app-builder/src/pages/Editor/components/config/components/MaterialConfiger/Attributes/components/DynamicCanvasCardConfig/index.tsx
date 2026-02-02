import { Form, Button, Image, Select } from '@arco-design/web-react';
import { IconArrowLeft, IconSwap, IconDelete, IconPlus } from '@arco-design/web-react/icon';
import { getPopupContainer } from '@onebase/ui-kit';
import React, { useState, useEffect } from 'react';
import styles from './index.module.less';
import { registerConfigRenderer } from '../../registry';
import CanvasCardType1Image from '@/assets/images/cp/CanvasCardType1.png';
import CanvasCardType2Image from '@/assets/images/cp/CanvasCardType2.png';

const FormItem = Form.Item;

export interface DynamicCanvasCardConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | unknown[]) => void;
  configs: Record<string, unknown>;
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

const DynamicCanvasCardConfig: React.FC<DynamicCanvasCardConfigProps> = ({ handlePropsChange, configs }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentComponent, setCurrentComponent] = useState((configs.componentName as string) || 'CanvasCardType1');
  const displayFields = (configs.displayFields as DisplayFieldsConfig) || {};

  useEffect(() => {
    setCurrentComponent((configs.componentName as string) || 'CanvasCardType1');
  }, [configs.componentName]);

  useEffect(() => {
    const componentName = (configs.componentName as string) || 'CanvasCardType1';
    const currentDisplayFields = (configs.displayFields as DisplayFieldsConfig) || {};
    const newDisplayFields = { ...currentDisplayFields };

    if (componentName === 'CanvasCardType1') {
      if (!newDisplayFields.categoryTags || newDisplayFields.categoryTags.length < 1) {
        newDisplayFields.categoryTags = [''];
      }
      if (!newDisplayFields.auxiliaryInfo) {
        newDisplayFields.auxiliaryInfo = [];
      }
    } else if (componentName === 'CanvasCardType2') {
      if (!newDisplayFields.categoryTags) {
        newDisplayFields.categoryTags = [];
      }
      if (!newDisplayFields.cardFields || newDisplayFields.cardFields.length < 4) {
        newDisplayFields.cardFields = ['', '', '', ''];
      }
    }

    handlePropsChange('displayFields', newDisplayFields);
  }, [configs.componentName]);

  const handleStyleChange = (componentName: string) => {
    console.log('handleStyleChange called with:', componentName);
    setCurrentComponent(componentName);
    handlePropsChange('componentName', componentName);
  };

  const handleFieldChange = (fieldPath: string, value: string | string[]) => {
    const newDisplayFields = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: any = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      if (!current[keys[i]]) {
        current[keys[i]] = Array.isArray(current[keys[i - 1]]) ? [] : {};
      }
      current = current[keys[i]];
    }

    current[keys[keys.length - 1]] = value;
    handlePropsChange('displayFields', newDisplayFields);
  };

  const handleAddArrayField = (fieldPath: string, maxCount: number) => {
    const newDisplayFields = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: any = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      if (!current[keys[i]]) {
        current[keys[i]] = Array.isArray(current[keys[i - 1]]) ? [] : {};
      }
      current = current[keys[i]];
    }

    const array = current[keys[keys.length - 1]] || [];
    if (array.length < maxCount) {
      current[keys[keys.length - 1]] = [...array, ''];
      handlePropsChange('displayFields', newDisplayFields);
    }
  };

  const handleRemoveArrayField = (fieldPath: string, index: number) => {
    const newDisplayFields = { ...displayFields };
    const keys = fieldPath.split('.');
    let current: any = newDisplayFields;

    for (let i = 0; i < keys.length - 1; i++) {
      current = current[keys[i]];
    }

    const array = current[keys[keys.length - 1]] || [];
    current[keys[keys.length - 1]] = array.filter((_: unknown, i: number) => i !== index);
    handlePropsChange('displayFields', newDisplayFields);
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
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
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
                  <Select.Option value="field1">字段1</Select.Option>
                  <Select.Option value="field2">字段2</Select.Option>
                  <Select.Option value="field3">字段3</Select.Option>
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
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="卡片正文" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.cardContent}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('cardContent', value)}
          >
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
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
                  <Select.Option value="field1">字段1</Select.Option>
                  <Select.Option value="field2">字段2</Select.Option>
                  <Select.Option value="field3">字段3</Select.Option>
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
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
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
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
          </Select>
        </FormItem>

        <FormItem layout="vertical" labelAlign="left" label="主标题" className={styles.formItem}>
          <Select
            placeholder="请选择字段"
            value={displayFields.mainTitle}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleFieldChange('mainTitle', value)}
          >
            <Select.Option value="field1">字段1</Select.Option>
            <Select.Option value="field2">字段2</Select.Option>
            <Select.Option value="field3">字段3</Select.Option>
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
                  <Select.Option value="field1">字段1</Select.Option>
                  <Select.Option value="field2">字段2</Select.Option>
                  <Select.Option value="field3">字段3</Select.Option>
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
                  <Select.Option value="field1">字段1</Select.Option>
                  <Select.Option value="field2">字段2</Select.Option>
                  <Select.Option value="field3">字段3</Select.Option>
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
      <FormItem layout="vertical" labelAlign="left" label="样式库" className={styles.formItem}>
        {isEditing ? renderStyleSelection() : renderStylePreview()}
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="显示字段" className={styles.formItem}>
        {currentComponent === 'CanvasCardType1' ? renderStyle1Fields() : renderStyle2Fields()}
      </FormItem>
    </div>
  );
};

registerConfigRenderer('CanvasCardConfig', ({ handlePropsChange, configs }) => (
  <DynamicCanvasCardConfig handlePropsChange={handlePropsChange} configs={configs} />
));

export default DynamicCanvasCardConfig;

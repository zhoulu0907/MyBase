import React, { useState, useEffect } from 'react';
import { Button, Steps, Form, Input, Checkbox, ColorPicker } from '@arco-design/web-react';
import { IconArrowLeft, IconSwap, IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import { ReactSortable } from 'react-sortablejs';
import styles from './index.module.less';

const { Step } = Steps;
const FormItem = Form.Item;

type StepsStyleType = 'default' | 'navigation' | 'arrow' | 'dot';
type StepsLabelPlacementType = 'horizontal' | 'vertical';

function hexToRgb(hex: string): string {
  if (!hex) return '';
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  if (result) {
    return `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`;
  }
  return hex;
}

function colorToRgbValue(color: string): string {
  if (!color) return '';
  if (color.startsWith('#')) {
    return hexToRgb(color);
  }
  if (color.startsWith('rgb')) {
    const match = color.match(/^rgb\(?\s*(\d+)\s*,\s*(\d+)\s*,\s*(\d+)\s*\)?$/);
    if (match) {
      return `${match[1]}, ${match[2]}, ${match[3]}`;
    }
  }
  return color;
}

function getDefaultThemeColor(): string {
  const bodyStyle = getComputedStyle(document.body);
  const primaryColor = bodyStyle.getPropertyValue('--primary-6').trim();
  if (primaryColor) {
    return colorToRgbValue(primaryColor);
  }
  return '';
}

function getThemeColorForDisplay(color: string): string {
  if (!color) {
    const defaultColor = getDefaultThemeColor();
    return defaultColor ? `rgb(${defaultColor})` : '';
  }
  return `rgb(${color})`;
}

interface StepsStyleItem {
  key: string;
  label: string;
  value: StepsStyleType;
}

export interface DynamicStepsConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | unknown[]) => void;
  handleMultiPropsChange?: (updates: { key: string; value: string | number | boolean | unknown[] }[]) => void;
  item?: any;
  configs: Record<string, unknown>;
  id?: string;
}

const STEPS_STYLES: StepsStyleItem[] = [
  {
    key: 'default',
    label: '默认样式',
    value: 'default'
  },
  {
    key: 'navigation',
    label: '导航样式',
    value: 'navigation'
  },
  {
    key: 'arrow',
    label: '箭头样式',
    value: 'arrow'
  },
  {
    key: 'dot',
    label: '点状样式',
    value: 'dot'
  }
];

const DEFAULT_STEPS = [
  {
    title: '步骤一',
    key: '1',
    description: '这是步骤1的描述'
  },
  {
    title: '步骤二',
    key: '2',
    description: '这是步骤2的描述'
  },
  {
    title: '步骤三',
    key: '3',
    description: '这是步骤3的描述'
  }
];

const DynamicStepsConfig: React.FC<DynamicStepsConfigProps> = ({ handlePropsChange, handleMultiPropsChange, configs }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentStyle, setCurrentStyle] = useState<StepsStyleType>((configs.type as StepsStyleType) || 'default');
  const [currentLabelPlacement, setCurrentLabelPlacement] = useState<StepsLabelPlacementType>(
    (configs.labelPlacement as StepsLabelPlacementType) || 'horizontal'
  );
  const [stepsConfig, setStepsConfig] = useState<any[]>([]);
  const [showDescription, setShowDescription] = useState(false);

  useEffect(() => {
    setCurrentStyle((configs.type as StepsStyleType) || 'default');
    setCurrentLabelPlacement((configs.labelPlacement as StepsLabelPlacementType) || 'horizontal');
    if (configs && configs.defaultValue) {
      setStepsConfig(configs.defaultValue as any[]);
    }
    setShowDescription(!!configs.showDescription);
  }, [configs.type, configs.labelPlacement, configs.defaultValue, configs.showDescription]);

  const handleStyleChange = (style: StepsStyleType) => {
    setCurrentStyle(style);
    handlePropsChange('type', style);
    setIsEditing(false);
  };

  const handleStepTitleChange = (index: number, value: string) => {
    const newList = [...stepsConfig];
    newList[index] = {
      ...newList[index],
      title: value
    };
    setStepsConfig(newList);
    handlePropsChange('defaultValue', newList);
  };

  const handleStepDescriptionChange = (index: number, value: string) => {
    const newList = [...stepsConfig];
    newList[index] = {
      ...newList[index],
      description: value
    };
    setStepsConfig(newList);
    handlePropsChange('defaultValue', newList);
  };

  const handleAddStep = () => {
    const newStepNumber = stepsConfig.length + 1;
    const newStep = {
      title: `步骤${newStepNumber}`,
      key: String(newStepNumber),
      description: ''
    };
    const newList = [...stepsConfig, newStep];
    setStepsConfig(newList);
    handlePropsChange('defaultValue', newList);
    if (handleMultiPropsChange) {
      handleMultiPropsChange([
        { key: 'defaultValue', value: newList },
        { key: 'colCount', value: newList.length }
      ]);
    }
  };

  const handleDeleteStep = (index: number) => {
    const newList = [...stepsConfig];
    newList.splice(index, 1);
    setStepsConfig(newList);
    handlePropsChange('defaultValue', newList);
    if (handleMultiPropsChange) {
      handleMultiPropsChange([
        { key: 'defaultValue', value: newList },
        { key: 'colCount', value: newList.length }
      ]);
    }
  };

  const renderStylePreview = () => {
    return (
      <div className={styles.stepsStylePreview}>
        <div className={styles.stepsStylePreviewContent}>
          <Steps current={1} type={currentStyle} labelPlacement={currentLabelPlacement}>
            {stepsConfig.map((step: any, index: number) => (
              <Step key={index} title={step.title} description={step.description} />
            ))}
          </Steps>
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
      <div className={styles.stepsStyleSelection}>
        <div className={styles.stepsStyleSelectionHeader}>
          <Button type="text" icon={<IconArrowLeft />} onClick={() => setIsEditing(false)} />
          <div className={styles.stepsStyleSelectionTitle}>切换外观样式</div>
        </div>
        <div className={styles.stepsStyleList}>
          {STEPS_STYLES.map((style) => (
            <div
              key={style.key}
              className={`${styles.stepsStyleItem} ${currentStyle === style.value ? styles.active : ''}`}
              onClick={() => handleStyleChange(style.value)}
            >
              <div className={styles.stepsStyleItemLabel}>{style.label}</div>
              <div className={styles.stepsStyleItemPreview}>
                <Steps current={1} type={style.value} labelPlacement={currentLabelPlacement} size="small">
                  <Step title="步骤1" />
                  <Step title="步骤2" />
                  <Step title="步骤3" />
                </Steps>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  };

  const renderStepsConfig = () => {
    return (
      <div className={styles.stepsConfigList}>
        <ReactSortable
          list={stepsConfig}
          setList={() => {}}
          group={{
            name: 'steps-item'
          }}
          swap
          sort={true}
          handle=".steps-item-handle"
          className={styles.stepsSortableList}
          forceFallback={true}
          animation={150}
          onSort={(e) => {
            const { oldIndex, newIndex } = e;
            if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
              const movedList = [...stepsConfig];
              const [movedItem] = movedList.splice(oldIndex, 1);
              movedList.splice(newIndex, 0, movedItem);
              setStepsConfig(movedList);
              handlePropsChange('defaultValue', movedList);
            }
          }}
        >
          {stepsConfig?.map((step: any, idx: number) => (
            <div key={step.key} className={styles.stepsConfigItem}>
              <div style={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                <IconDragDotVertical
                  className="steps-item-handle"
                  style={{
                    cursor: 'move',
                    color: '#555',
                    marginRight: '8px'
                  }}
                />
                <div style={{ flex: 1 }}>
                  <Input
                    size="small"
                    value={step.title}
                    onChange={(e) => handleStepTitleChange(idx, e)}
                    className={styles.stepsConfigItemInput}
                    placeholder="步骤标题"
                    style={{ marginBottom: showDescription ? '8px' : 0, width: '100%' }}
                  />
                  {showDescription && (
                    <Input
                      size="small"
                      value={step.description || ''}
                      onChange={(e) => handleStepDescriptionChange(idx, e)}
                      className={styles.stepsConfigItemInput}
                      placeholder="步骤描述"
                      style={{ width: '100%' }}
                    />
                  )}
                </div>
                <Button
                  icon={<IconDelete />}
                  shape="circle"
                  size="mini"
                  status="danger"
                  disabled={stepsConfig?.length < 2}
                  onClick={() => handleDeleteStep(idx)}
                  style={{ marginLeft: '8px' }}
                />
              </div>
            </div>
          ))}
        </ReactSortable>
        <Button
          type="outline"
          onClick={handleAddStep}
          style={{ marginTop: '16px', width: '88px' }}
        >
          新增步骤
        </Button>
      </div>
    );
  };

  return (
    <div className={styles.dynamicStepsConfig}>
      <FormItem layout="vertical" labelAlign="left" label="样式库" className={styles.formItem}>
        {isEditing ? renderStyleSelection() : renderStylePreview()}
      </FormItem>
      <FormItem
        layout="vertical"
        labelAlign="left"
        label={
          <>
            步骤
            <Checkbox
              checked={showDescription}
              style={{ float: 'right' }}
              onChange={(v) => {
                setShowDescription(v);
                handlePropsChange('showDescription', v);
              }}
            >
              显示描述
            </Checkbox>
          </>
        }
        className={styles.formItem}
      >
        {renderStepsConfig()}
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="校验" className={styles.formItem}>
        <Checkbox
          checked={!!configs.validateOnNext}
          onChange={(v) => {
            handlePropsChange('validateOnNext', v);
          }}
        >
          下一步时触发当前步骤校验
        </Checkbox>
      </FormItem>
      <FormItem layout="vertical" labelAlign="left" label="颜色" className={styles.formItem}>
        <ColorPicker
          value={getThemeColorForDisplay(configs.color as string)}
          format="rgb"
          disabledAlpha
          onChange={(value) => {
            const rgbValue = colorToRgbValue(value);
            handlePropsChange('color', rgbValue);
          }}
        />
      </FormItem>
    </div>
  );
};

registerConfigRenderer(CONFIG_TYPES.STEPS, ({ id, handlePropsChange, handleMultiPropsChange, item, configs }) => (
  <DynamicStepsConfig id={id} handlePropsChange={handlePropsChange} handleMultiPropsChange={handleMultiPropsChange} item={item} configs={configs} />
));

export default DynamicStepsConfig;

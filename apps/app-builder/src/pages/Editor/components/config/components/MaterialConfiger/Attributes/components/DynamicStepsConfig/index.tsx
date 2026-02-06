import React, { useState, useEffect } from 'react';
import { Button, Steps, Form } from '@arco-design/web-react';
import { IconArrowLeft, IconSwap } from '@arco-design/web-react/icon';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from './index.module.less';

const { Step } = Steps;
const FormItem = Form.Item;

type StepsStyleType = 'default' | 'navigation' | 'arrow' | 'dot';
type StepsLabelPlacementType = 'horizontal' | 'vertical';

interface StepsStyleItem {
  key: string;
  label: string;
  value: StepsStyleType;
}

export interface DynamicStepsConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | unknown[]) => void;
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

const DynamicStepsConfig: React.FC<DynamicStepsConfigProps> = ({ handlePropsChange, configs }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [currentStyle, setCurrentStyle] = useState<StepsStyleType>((configs.type as StepsStyleType) || 'default');
  const [currentLabelPlacement, setCurrentLabelPlacement] = useState<StepsLabelPlacementType>(
    (configs.labelPlacement as StepsLabelPlacementType) || 'horizontal'
  );

  useEffect(() => {
    setCurrentStyle((configs.type as StepsStyleType) || 'default');
    setCurrentLabelPlacement((configs.labelPlacement as StepsLabelPlacementType) || 'horizontal');
  }, [configs.type, configs.labelPlacement]);

  const handleStyleChange = (style: StepsStyleType) => {
    setCurrentStyle(style);
    handlePropsChange('type', style);
    setIsEditing(false);
  };

  const renderStylePreview = () => {
    return (
      <div className={styles.stepsStylePreview}>
        <div className={styles.stepsStylePreviewContent}>
          <Steps current={1} type={currentStyle} labelPlacement={currentLabelPlacement}>
            <Step title="步骤1" description="这是步骤1的描述" />
            <Step title="步骤2" description="这是步骤2的描述" />
            <Step title="步骤3" description="这是步骤3的描述" />
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

  return (
    <div className={styles.dynamicStepsConfig}>
      <FormItem layout="vertical" labelAlign="left" label="样式库" className={styles.formItem}>
        {isEditing ? renderStyleSelection() : renderStylePreview()}
      </FormItem>
    </div>
  );
};

registerConfigRenderer(CONFIG_TYPES.STEPS, ({ id, handlePropsChange, item, configs }) => (
  <DynamicStepsConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

export default DynamicStepsConfig;

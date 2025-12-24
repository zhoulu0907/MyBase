import { Form, Slider } from '@arco-design/web-react';
import { useCallback, useMemo } from 'react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbSliderConfigType } from '@onebase/ui-kit';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IWbSliderConfigType;
  configs: Record<string, unknown>;
}

const WbSliderConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentValue = useMemo(() => {
    const nextValue = configs?.[item.key];
    return typeof nextValue === 'number' ? nextValue : 0;
  }, [configs, item.key]);

  const handleContentChange = useCallback(
    (value: number | number[]) => {
      handlePropsChange(item.key, value);
    },
    [handlePropsChange, item.key]
  );

  const minTextStyle = { fontSize: 12, fontWeight: 'normal' };
  const maxTextStyle = { fontSize: 18, fontWeight: 'bold' };

  return (
    <>
      <Form.Item className={styles.formItem} layout="vertical" labelAlign="left" label={item.name}>
        <div className={styles.sliderContainer}>
          <span style={minTextStyle}>Aa</span>
          <Slider
            value={currentValue}
            onChange={handleContentChange}
            min={item?.min || 0}
            max={item?.max || 100}
            step={item?.step || 1}
            className={styles.slider}
          />
          <span style={maxTextStyle}>Aa</span>
        </div>
      </Form.Item>
    </>
  );
};

export default WbSliderConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_SLIDER, ({ handlePropsChange, item, configs }) => (
  <WbSliderConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

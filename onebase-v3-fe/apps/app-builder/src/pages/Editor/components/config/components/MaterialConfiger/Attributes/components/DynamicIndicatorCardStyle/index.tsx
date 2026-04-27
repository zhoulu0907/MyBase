import indicatorCard01 from '@/assets/images/indicatorCard/indicatorCard01.svg';
import indicatorCard02 from '@/assets/images/indicatorCard/indicatorCard02.svg';
import indicatorCard03 from '@/assets/images/indicatorCard/indicatorCard03.svg';
import indicatorCard04 from '@/assets/images/indicatorCard/indicatorCard04.svg';
import indicatorCard05 from '@/assets/images/indicatorCard/indicatorCard05.svg';
import { Button, Form, Radio } from '@arco-design/web-react';
import { IconArrowLeft, IconSwap } from '@arco-design/web-react/icon';
import { useState } from 'react';
import { CONFIG_TYPES, INDICATOR_CARD_STYLE_TYPE } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicIndicatorCardStyle = ({ handlePropsChange, item, configs }: Props) => {
  const styleList = [
    { icon: indicatorCard01, value: INDICATOR_CARD_STYLE_TYPE.ONE },
    { icon: indicatorCard02, value: INDICATOR_CARD_STYLE_TYPE.TWO },
    { icon: indicatorCard03, value: INDICATOR_CARD_STYLE_TYPE.THREE },
    { icon: indicatorCard04, value: INDICATOR_CARD_STYLE_TYPE.FOUR },
    { icon: indicatorCard05, value: INDICATOR_CARD_STYLE_TYPE.FIVE }
  ];

  const [showRadio, setShowRadio] = useState(false);

  return (
    <Form.Item className={styles.formItem} label="样式库">
      {showRadio ? (
        <>
          <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
            <Button type="text" icon={<IconArrowLeft />} onClick={() => setShowRadio(false)}></Button>
            <div style={{ marginLeft: '60px' }}>切换外观样式</div>
          </div>
          <Radio.Group
            value={configs.styleType}
            direction="vertical"
            onChange={(value) => handlePropsChange('styleType', value)}
          >
            {styleList.map((ele) => (
              <Radio key={ele.value} value={ele.value}>
                {({ checked }) => (
                  <div
                    className={styles.indicatorCardStyle}
                    style={{ borderColor: checked ? 'rgb(var(--primary-6))' : 'transparent' }}
                  >
                    <img src={ele.icon} alt="" />
                  </div>
                )}
              </Radio>
            ))}
          </Radio.Group>
        </>
      ) : (
        <>
          <div className={styles.indicatorCardStyle}>
            <img src={styleList.find((ele) => ele.value === configs.styleType)?.icon} alt="" />
          </div>
          <Button type="outline" long icon={<IconSwap />} onClick={() => setShowRadio(true)}>
            更改样式
          </Button>
        </>
      )}
    </Form.Item>
  );
};

export default DynamicIndicatorCardStyle;

registerConfigRenderer(CONFIG_TYPES.INDICATOR_CARD_STYLE, ({ handlePropsChange, item, configs }) => (
  <DynamicIndicatorCardStyle handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

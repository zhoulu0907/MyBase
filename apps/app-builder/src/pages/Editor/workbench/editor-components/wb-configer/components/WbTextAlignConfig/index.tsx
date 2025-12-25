import { Form, Radio } from '@arco-design/web-react';
import { IconAlignLeft, IconAlignCenter, IconAlignRight } from '@arco-design/web-react/icon';
import { registerConfigRenderer } from '../../registry';
import {
  WORKBENCH_CONFIG_TYPES,
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  VERTICAL_ALIGN_OPTIONS,
  type IWbTextAlignConfigType
} from '@onebase/ui-kit';

import IconAlignTop from '@assets/workbench/text-align/top.svg';
import IconAlignMiddle from '@assets/workbench/text-align/middle.svg';
import IconAlignBottom from '@assets/workbench/text-align/bottom.svg';

import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: { horizontal: string; vertical: string }) => void;
  item: IWbTextAlignConfigType;
  configs: Record<string, unknown>;
}

const WbTextAlignConfig = ({ handlePropsChange, item, configs }: Props) => {
  // 获取当前的 textAlign 值，如果不存在则使用默认值
  const currentTextAlign = (configs[item.key] as { horizontal?: string; vertical?: string }) || {
    horizontal: ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
    vertical: VERTICAL_ALIGN_OPTIONS.MIDDLE
  };

  // 处理水平对齐变化
  const handleHorizontalChange = (value: string) => {
    handlePropsChange(item.key, {
      horizontal: value,
      vertical: currentTextAlign.vertical || VERTICAL_ALIGN_OPTIONS.MIDDLE
    });
  };

  // 处理垂直对齐变化
  const handleVerticalChange = (value: string) => {
    handlePropsChange(item.key, {
      horizontal: currentTextAlign.horizontal || ALIGN_VALUES[ALIGN_OPTIONS.LEFT],
      vertical: value
    });
  };

  return (
    <>
      {/* 水平对齐 */}
      <Form.Item className={styles.formItem} label={item.name} style={{ marginBottom: 0 }}>
        <Radio.Group
          type="button"
          direction="horizontal"
          size="mini"
          value={currentTextAlign.horizontal}
          onChange={handleHorizontalChange}
          className={styles.radioGroup}
        >
          <Radio key={ALIGN_OPTIONS.LEFT} value={ALIGN_VALUES[ALIGN_OPTIONS.LEFT]} className={styles.widthRadio}>
            <IconAlignLeft className={styles.alignIcon} />
          </Radio>
          <Radio key={ALIGN_OPTIONS.CENTER} value={ALIGN_VALUES[ALIGN_OPTIONS.CENTER]} className={styles.widthRadio}>
            <IconAlignCenter className={styles.alignIcon} />
          </Radio>
          <Radio key={ALIGN_OPTIONS.RIGHT} value={ALIGN_VALUES[ALIGN_OPTIONS.RIGHT]} className={styles.widthRadio}>
            <IconAlignRight className={styles.alignIcon} />
          </Radio>
        </Radio.Group>
      </Form.Item>

      {/* 垂直对齐 */}
      <Form.Item className={styles.formItem}>
        <Radio.Group
          type="button"
          direction="horizontal"
          size="mini"
          value={currentTextAlign.vertical}
          onChange={handleVerticalChange}
          className={styles.radioGroup}
        >
          <Radio key={VERTICAL_ALIGN_OPTIONS.TOP} value={VERTICAL_ALIGN_OPTIONS.TOP} className={styles.widthRadio}>
            <img src={IconAlignTop} alt="IconAlignTop" className={styles.alignIcon} />
          </Radio>
          <Radio
            key={VERTICAL_ALIGN_OPTIONS.MIDDLE}
            value={VERTICAL_ALIGN_OPTIONS.MIDDLE}
            className={styles.widthRadio}
          >
            <img src={IconAlignMiddle} alt="IconAlignMiddle" className={styles.alignIcon} />
          </Radio>
          <Radio
            key={VERTICAL_ALIGN_OPTIONS.BOTTOM}
            value={VERTICAL_ALIGN_OPTIONS.BOTTOM}
            className={styles.widthRadio}
          >
            <img src={IconAlignBottom} alt="IconAlignBottom" className={styles.alignIcon} />
          </Radio>
        </Radio.Group>
      </Form.Item>
    </>
  );
};

export default WbTextAlignConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_TEXT_ALIGN, ({ handlePropsChange, item, configs }) => (
  <WbTextAlignConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

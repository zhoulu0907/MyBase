import { useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { ColorPicker, Form, Select } from '@arco-design/web-react';
import type { GradientColor } from '@arco-design/web-react/es/ColorPicker/interface';

import { CONFIG_TYPES } from '@onebase/ui-kit/src/components/Materials/constants';

import styles from './index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const KEY = {
  COLOR: 'color',
  TITLECOLOR: 'titleColor',
  DESCRIPTIONCOLOR: 'descriptionColor'
};

const fieldStyles = [
  { value: 'style1', label: '样式1' },
  { value: 'style2', label: '样式2' },
  { value: 'style3', label: '样式3' },
  { value: 'style4', label: '样式4' },
  { value: 'style5', label: '样式5' },
  { value: 'style6', label: '样式6' }
  //   { value: 'style7', label: '样式7' },
  //   { value: 'style8', label: '样式8' }
];

const DynamicDividerStyleConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [color, setColor] = useState<string | GradientColor[]>(configs[KEY.COLOR]);
  const [titleColor, setTitleColor] = useState<string | GradientColor[]>(configs[KEY.TITLECOLOR]);
  const [descriptionColor, setdDscriptionColor] = useState<string | GradientColor[]>(configs[KEY.DESCRIPTIONCOLOR]);

  const handleStyleChange = (value: string) => {
    handlePropsChange(item.key, value);
  };

  const handleColorChange = (key: string, value: string | GradientColor[]) => {
    switch (key) {
      case KEY.COLOR:
        setColor(value);
        break;
      case KEY.TITLECOLOR:
        setTitleColor(value);
        break;
      default:
        setdDscriptionColor(value);
        break;
    }

    handlePropsChange(key, value);
  };

  return (
    <>
      <Form.Item className={styles.formItem} label={item.name}>
        <Select
          defaultValue={configs[item.key]}
          placeholder="请选择字段样式"
          className={styles.dividerStyle}
          getPopupContainer={() => document.body}
          onChange={(value) => handleStyleChange(value)}
          options={fieldStyles.map((option, index) => ({
            label: (
              <div className={styles.dropdownWrapper}>
                <div className={styles.previewWrapper}>
                  <div
                    className={[4, 5].includes(index) ? styles[`decor-title-${option.value}`] : styles.title}
                    style={{ color: `${titleColor}`, borderLeftColor: `${color}`, borderBottomColor: `${color}` }}
                  >
                    字段标题
                  </div>
                  {/* 不同风格的装饰条 */}
                  <div className={styles[`decor-${option.value}`]} style={{ borderColor: `${color}` }} />
                  <div className={styles.desc} style={{ color: `${descriptionColor}` }}>
                    这里是字段的描述信息
                  </div>
                </div>
              </div>
            ),
            value: option.value
          }))}
        />
      </Form.Item>
      <Form.Item
        label="配色"
        layout="horizontal"
        labelAlign="left"
        labelCol={{
          span: 7
        }}
        wrapperCol={{
          span: 17
        }}
      >
        <ColorPicker
          defaultValue={color}
          showText
          className={styles.dividerColorPicker}
          onChange={(value) => handleColorChange(KEY.COLOR, value)}
        />
      </Form.Item>
      <Form.Item
        label="标题颜色"
        layout="horizontal"
        labelAlign="left"
        labelCol={{
          span: 7
        }}
        wrapperCol={{
          span: 17
        }}
      >
        <ColorPicker
          defaultValue={titleColor}
          showText
          className={styles.dividerColorPicker}
          onChange={(value) => handleColorChange(KEY.TITLECOLOR, value)}
        />
      </Form.Item>
      <Form.Item
        label="描述颜色"
        layout="horizontal"
        labelAlign="left"
        labelCol={{
          span: 7
        }}
        wrapperCol={{
          span: 17
        }}
      >
        <ColorPicker
          defaultValue={descriptionColor}
          showText
          className={styles.dividerColorPicker}
          onChange={(value) => handleColorChange(KEY.DESCRIPTIONCOLOR, value)}
        />
      </Form.Item>
    </>
  );
};

export default DynamicDividerStyleConfig;

registerConfigRenderer(CONFIG_TYPES.FORM_DIVIDER_STYLE_TYPE, ({ handlePropsChange, item, configs }) => (
  <DynamicDividerStyleConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

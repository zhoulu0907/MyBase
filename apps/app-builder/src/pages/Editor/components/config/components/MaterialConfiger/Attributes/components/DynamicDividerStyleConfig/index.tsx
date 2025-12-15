import { ColorPicker, Form, Select } from '@arco-design/web-react';
import type { GradientColor } from '@arco-design/web-react/es/ColorPicker/interface';
import { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';

import { CONFIG_TYPES } from '@onebase/ui-kit/src/components/Materials/constants';

import styles from './index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
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
  { value: 'style6', label: '样式6' },
  { value: 'style7', label: '样式7' },
  { value: 'style8', label: '样式8' },
  { value: 'style9', label: '样式9' },
  { value: 'style10', label: '样式10' }
];

const DynamicDividerStyleConfig = ({ handlePropsChange, item, configs, id }: Props) => {
  const [color, setColor] = useState<string | GradientColor[]>(configs[KEY.COLOR]);
  const [titleColor, setTitleColor] = useState<string | GradientColor[]>(configs[KEY.TITLECOLOR]);
  const [descriptionColor, setdDscriptionColor] = useState<string | GradientColor[]>(configs[KEY.DESCRIPTIONCOLOR]);

  // 当 configs 变化时，同步更新本地状态
  useEffect(() => {
    setColor(configs[KEY.COLOR]);
    setTitleColor(configs[KEY.TITLECOLOR]);
    setdDscriptionColor(configs[KEY.DESCRIPTIONCOLOR]);
  }, [configs[KEY.COLOR], configs[KEY.TITLECOLOR], configs[KEY.DESCRIPTIONCOLOR], id]);

  const handleStyleChange = (value: string) => {
    handlePropsChange(item.key, value);
  };

  const hexToRgba = (hex: any, alpha = 1) => {
    const cleaned = hex.replace('#', '');
    const full =
      cleaned.length === 3
        ? cleaned
            .split('')
            .map((c: any) => c + c)
            .join('')
        : cleaned;

    const r = parseInt(full.slice(0, 2), 16);
    const g = parseInt(full.slice(2, 4), 16);
    const b = parseInt(full.slice(4, 6), 16);

    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
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

  const optionRender = (option: any, index: number) => {
    if (index < 6) {
      return (
        <>
          <div
            className={[4, 5].includes(index) ? styles[`decor-title-${option.value}`] : styles.title}
            style={{ color: `${titleColor}`, borderLeftColor: `${color}`, borderBottomColor: `${color}` }}
          >
            字段标题
          </div>
          {/* 不同风格的装饰条 */}
          <div className={styles[`decor-${option.value}`]} style={{ borderColor: `${color}` }} />
        </>
      );
    } else if (index === 6) {
      return (
        <div className={styles[`decor-title-${option.value}`]} style={{ borderBottomColor: `${color}` }}>
          <div className={styles['label']} style={{ backgroundColor: `${color}`, color: `${titleColor}` }}>
            字段标题
          </div>
          <div className={styles['decorator1']} style={{ backgroundColor: `${color}` }}></div>
          <div className={styles['decorator2']} style={{ backgroundColor: `${color}` }}></div>
          <div className={styles['decorator3']} style={{ backgroundColor: `${color}` }}></div>
        </div>
      );
    } else if (index === 7) {
      return (
        <div className={styles[`decor-${option.value}`]} style={{ backgroundColor: hexToRgba(color, 0.2) }}>
          <div
            className={styles[`decor-title-${option.value}`]}
            style={{ backgroundColor: `${color}`, color: `${titleColor}` }}
          >
            字段标题
          </div>
        </div>
      );
    } else if (index === 8) {
      return (
        <div
          className={styles[`decor-${option.value}`]}
          style={
            {
              backgroundColor: hexToRgba(color, 0.2),
              '--before-bg': hexToRgba(color, 0.6),
              '--after-bg': hexToRgba(color, 0.6)
            } as React.CSSProperties
          }
        >
          <div className={styles.tabActive} style={{ backgroundColor: `${color}`, color: `${titleColor}` }}>
            字段标题
          </div>
        </div>
      );
    } else {
      return (
        <div className={styles[`decor-${option.value}`]}>
          <div className={styles.leftArrows}>
            <span className={styles.leftArrow1} style={{ backgroundColor: hexToRgba(color, 0.6) }} />
            <span className={styles.leftArrow2} style={{ backgroundColor: hexToRgba(color, 0.2) }} />
          </div>

          <div className={styles.center} style={{ backgroundColor: `${color}`, color: `${titleColor}` }}>
            字段标题
          </div>

          <div className={styles.rightArrows}>
            <span className={styles.rightArrow1} style={{ backgroundColor: hexToRgba(color, 0.6) }} />
            <span className={styles.rightArrow2} style={{ backgroundColor: hexToRgba(color, 0.2) }} />
          </div>
        </div>
      );
    }
  };

  return (
    <>
      <Form.Item className={styles.formItem} label={item.name}>
        <Select
          key={`${id}-${item.key}-style`}
          value={configs[item.key]}
          placeholder="请选择字段样式"
          className={styles.dividerStyle}
          getPopupContainer={() => document.body}
          onChange={(value) => handleStyleChange(value)}
          options={fieldStyles.map((option, index) => ({
            label: (
              <div className={styles.dropdownWrapper}>
                <div className={styles.previewWrapper}>
                  {optionRender(option, index)}
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
          key={`${id}-${KEY.COLOR}`}
          value={color}
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
          key={`${id}-${KEY.TITLECOLOR}`}
          value={titleColor}
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
          key={`${id}-${KEY.DESCRIPTIONCOLOR}`}
          value={descriptionColor}
          showText
          className={styles.dividerColorPicker}
          onChange={(value) => handleColorChange(KEY.DESCRIPTIONCOLOR, value)}
        />
      </Form.Item>
    </>
  );
};

export default DynamicDividerStyleConfig;

registerConfigRenderer(CONFIG_TYPES.DIVIDER_STYLE_TYPE, ({ handlePropsChange, item, configs, id }) => (
  <DynamicDividerStyleConfig handlePropsChange={handlePropsChange} item={item} configs={configs} id={id} />
));

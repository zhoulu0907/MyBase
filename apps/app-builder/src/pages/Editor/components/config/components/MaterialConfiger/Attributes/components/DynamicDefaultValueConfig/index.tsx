import { Form, Select, Input, Button, Switch, DatePicker, TimePicker, InputNumber } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { IconLaunch } from '@arco-design/web-react/icon';
import { registerConfigRenderer } from '../../registry';
import {
  CONFIG_TYPES,
  DEFAULT_VALUE_TYPES,
  DEFAULT_VALUE_TYPES_LABELS,
  PHONE_TYPE,
  getPopupContainer
} from '@onebase/ui-kit';
import { FormulaEditor } from '@/components/FormulaEditor';
import styles from '../../index.module.less';

export interface DynamicDefaultValueConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDefaultValueConfig: React.FC<DynamicDefaultValueConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const defaultValueConfigKey = item.key || 'defaultValueConfig';

  const [defaultValueConfig, setDefaultValueConfig] = useState({
    type: '',
    customValue: undefined,
    formulaValue: undefined
  });
  const [errorText, setErrorText] = useState('');
  // 公式计算弹窗
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);

  useEffect(() => {
    if (item.valueType === 'boolean') {
      const newValue = configs[defaultValueConfigKey].customValue === true ? 'true' : 'false';
      setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey], customValue: newValue }));
    } else {
      setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey] }));
    }
  }, [configs[defaultValueConfigKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[defaultValueConfigKey], [key]: value };
    handlePropsChange(defaultValueConfigKey, newConfig);
  };

  // 打开公式编辑器弹窗
  const openFormulaEditor = () => {
    setFormulaVisible(true);
  };

  // 公式编辑器弹窗确定
  const handleFormulaConfirm = (formulaData: string) => {
    setFormulaVisible(false);
    handleChange('formulaValue', formulaData);
  };

  return (
    <>
      <Form.Item layout="vertical" label={item.name || '默认值'} className={styles.formItem}>
        <Select
          getPopupContainer={getPopupContainer}
          onChange={(value) => {
            setErrorText(``);
            const newConfig = { ...configs[defaultValueConfigKey], type: value, formulaValue: '', customValue: '' };
            handlePropsChange(defaultValueConfigKey, newConfig);
          }}
          value={defaultValueConfig?.type}
          options={[
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.CUSTOM], value: DEFAULT_VALUE_TYPES.CUSTOM },
            { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.FORMULA], value: DEFAULT_VALUE_TYPES.FORMULA }
            // { label: DEFAULT_VALUE_TYPES_LABELS[DEFAULT_VALUE_TYPES.LINKAGE], value: DEFAULT_VALUE_TYPES.LINKAGE }
          ]}
        ></Select>
      </Form.Item>
      {/* 自定义 */}
      {defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM && (
        <Form.Item layout="vertical" className={styles.formItem}>
          {item.valueType === 'boolean' && (
            <Select
              options={[
                { label: '开启', value: 'true' },
                { label: '关闭', value: 'false' }
              ]}
              getPopupContainer={getPopupContainer}
              onChange={(value) => {
                const newValue = value === 'true' ? true : false;
                handleChange('customValue', newValue);
              }}
            ></Select>
          )}
          {item.valueType === 'string' && (
            <Input
              value={defaultValueConfig?.customValue}
              onChange={(value) => handleChange('customValue', value)}
              placeholder="请输入"
            />
          )}
          {item.valueType === 'date' && (
            <DatePicker
              style={{ width: '100%' }}
              getPopupContainer={getPopupContainer}
              format="YYYY-MM-DD"
              onChange={(value) => handleChange('customDateValue', value)}
            ></DatePicker>
          )}
          {item.valueType === 'dateTime' && (
            <DatePicker
              showTime
              style={{ width: '100%' }}
              getPopupContainer={getPopupContainer}
              format="YYYY-MM-DD HH:mm:ss"
              onChange={(value) => handleChange('customDateTimeValue', value)}
            ></DatePicker>
          )}
          {item.valueType === 'time' && (
            <TimePicker
              style={{ width: '100%' }}
              getPopupContainer={getPopupContainer}
              format="HH:mm:ss"
              onChange={(value) => handleChange('customTimeValue', value)}
            ></TimePicker>
          )}

          {item.valueType === 'phone' && (
            <Input
              value={defaultValueConfig?.customValue}
              maxLength={configs.phoneType === PHONE_TYPE.MOBILE ? 11 : 15}
              status={errorText ? 'error' : undefined}
              onChange={(value) => {
                if (configs.phoneType === PHONE_TYPE.MOBILE) {
                  if (value && !/^1[3-9]\d{9}$/.test(value)) {
                    setErrorText(`请输入有效的11位中国大陆手机号`);
                  } else {
                    setErrorText(``);
                  }
                }
                if (configs.phoneType === PHONE_TYPE.LANDLINE) {
                  if (value && !/^\(?0[0-9]{2,3}\)?-?[0-9]{7,8}$/.test(value)) {
                    setErrorText(`请输入有效的座机号`);
                  } else {
                    setErrorText(``);
                  }
                }
                handleChange('customValue', value);
              }}
              placeholder="请输入"
            />
          )}
          {item.valueType === 'number' && (
            <InputNumber
              step={configs.step}
              min={configs.verify?.numberLimit ? configs.verify?.min : undefined}
              max={configs.verify?.numberLimit ? configs.verify?.max : undefined}
              precision={configs?.numberFormat.showPrecision ? configs.numberFormat.precision : 0}
              formatter={(value) => {
                return configs?.numberFormat.useThousandsSeparator
                  ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                  : value.toString();
              }}
              parser={(value) => value.replace(/,/g, '')}
              suffix={configs?.numberFormat.showUnit ? configs.numberFormat.unitValue : ''}
            />
          )}

          {errorText && <div style={{ color: '#f53f3f', fontSize: '12px' }}>{errorText}</div>}
        </Form.Item>
      )}
      {/* 公式计算 */}
      {defaultValueConfig?.type === DEFAULT_VALUE_TYPES.FORMULA && (
        <Button onClick={openFormulaEditor} long style={{ marginBottom: '20px' }}>
          {defaultValueConfig?.formulaValue ? (
            <>
              <span>已设置公式</span>
              <IconLaunch />
            </>
          ) : (
            <>ƒx 编辑公式</>
          )}
        </Button>
      )}
      <FormulaEditor
        initialFormula={defaultValueConfig?.formulaValue}
        visible={formulaVisible}
        onCancel={() => setFormulaVisible(false)}
        onConfirm={handleFormulaConfirm}
      />
    </>
  );
};
export default DynamicDefaultValueConfig;

registerConfigRenderer(CONFIG_TYPES.DEFAULT_VALUE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicDefaultValueConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

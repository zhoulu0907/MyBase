import { Form, Select, Input, Button, Switch, DatePicker, TimePicker } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { IconLaunch } from '@arco-design/web-react/icon';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES, DEFAULT_VALUE_TYPES, DEFAULT_VALUE_TYPES_LABELS, getPopupContainer } from '@onebase/ui-kit';
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
          onChange={(value) => handleChange('type', value)}
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
            // <Switch
            //   checked={defaultValueConfig?.customValue}
            //   onChange={(value) => handleChange('customValue', value)}
            // />
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

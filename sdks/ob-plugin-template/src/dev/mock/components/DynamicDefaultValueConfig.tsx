import { Button, DatePicker, Form, Input, Radio, Space } from '@arco-design/web-react';
import styles from '../styles/index.module.less';
import { DATE_OPTIONS, DATE_VALUES } from '@ob/plugin/sdk';

export interface DynamicDefaultValueConfigProps {
  onChange: (value: any) => void;
  item: any;
  value: any;
  dateType?: string;
}

const DynamicDefaultValueConfig: React.FC<DynamicDefaultValueConfigProps> = ({
  onChange,
  item,
  value,
  dateType
}) => {
  const config = value || { type: 'CUSTOM', customValue: '', formulaValue: '' };

  const handleChange = (key: string, v: any) => {
    onChange({ ...config, [key]: v });
  };

  const renderDatePicker = () => {
    const { YearPicker, MonthPicker } = DatePicker;
    // Simple mock check for date types, assuming constants match
    switch (dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return (
          <YearPicker
            value={config.customValue}
            style={{ width: '100%' }}
            onChange={(v) => handleChange('customValue', v)}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return (
          <MonthPicker
            value={config.customValue}
            style={{ width: '100%' }}
            onChange={(v) => handleChange('customValue', v)}
          />
        );
      default:
        return (
          <DatePicker
            value={config.customValue}
            style={{ width: '100%' }}
            onChange={(v) => handleChange('customValue', v)}
          />
        );
    }
  };

  return (
    <Form.Item className={styles.formItem} label={item.name || '默认值'}>
      <Space direction="vertical" style={{ width: '100%' }}>
        <Radio.Group
          type="button"
          value={config.type}
          options={[
            { label: '自定义', value: 'CUSTOM' },
            { label: '公式编辑', value: 'FORMULA' },
            { label: '数据联动', value: 'LINKAGE' },
            { label: '自定义关联', value: 'CUSTOM_LINKAGE' }
          ].filter(opt => ['CUSTOM', 'FORMULA'].includes(opt.value))} // Filter for mock
          onChange={(v) => handleChange('type', v)}
        />

        {config.type === 'CUSTOM' && (
          <>
            {item.valueType === 'date' ? (
              renderDatePicker()
            ) : item.valueType === 'boolean' ? (
               <Radio.Group 
                 value={config.customValue} 
                 onChange={(v) => handleChange('customValue', v)}
                 options={[
                   { label: '选中', value: 'true' },
                   { label: '未选中', value: 'false' }
                 ]}
               />
            ) : (
              <Input
                placeholder="请输入默认值"
                value={config.customValue}
                onChange={(v) => handleChange('customValue', v)}
              />
            )}
          </>
        )}

        {config.type === 'FORMULA' && (
          <Input.TextArea
            placeholder="请输入公式"
            value={config.formulaValue}
            onChange={(v) => handleChange('formulaValue', v)}
          />
        )}
      </Space>
    </Form.Item>
  );
};

export default DynamicDefaultValueConfig;

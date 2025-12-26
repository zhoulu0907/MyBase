import { FormulaEditor } from '@/components/FormulaEditor';
import { Button, DatePicker, Form, Input, InputNumber, Select, TimePicker } from '@arco-design/web-react';
import { IconLaunch } from '@arco-design/web-react/icon';
import {
  CONFIG_TYPES,
  DATE_OPTIONS,
  DATE_VALUES,
  DEFAULT_VALUE_TYPES,
  DEFAULT_VALUE_TYPES_LABELS,
  getPopupContainer,
  PHONE_TYPE,
  TIME_12_FORMAT,
  TIME_FORMAT,
  FORM_COMPONENT_TYPES,
  usePageEditorSignal,
  getFieldOptionsConfig,
  useAppEntityStore
} from '@onebase/ui-kit';
import type { DictData } from '@onebase/platform-center';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

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
  useSignals();
  const { mainEntity, subEntities } = useAppEntityStore();

  const defaultValueConfigKey = item.key || 'defaultValueConfig';

  const { pageComponentSchemas } = usePageEditorSignal();
  const componentSchema = pageComponentSchemas[id];

  const [defaultValueConfig, setDefaultValueConfig] = useState({
    type: '',
    customValue: undefined,
    formulaValue: undefined
  });
  const [errorText, setErrorText] = useState('');
  // 公式计算弹窗
  const [formulaVisible, setFormulaVisible] = useState<boolean>(false);
  // 单选、多选下拉
  const [options, setOptions] = useState<DictData[]>([]);

  useEffect(() => {
    if (
      componentSchema?.type === FORM_COMPONENT_TYPES.RADIO ||
      componentSchema?.type === FORM_COMPONENT_TYPES.CHECKBOX ||
      componentSchema?.type === FORM_COMPONENT_TYPES.SELECT_ONE ||
      componentSchema?.type === FORM_COMPONENT_TYPES.SELECT_MUTIPLE
    ) {
      getOptions();
    }
  }, [configs.dataField]);

  useEffect(() => {
    if (componentSchema?.type === FORM_COMPONENT_TYPES.SWITCH) {
      const newValue = configs[defaultValueConfigKey].customValue === true ? 'true' : 'false';
      setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey], customValue: newValue }));
    } else {
      setDefaultValueConfig((prev) => ({ ...prev, ...configs[defaultValueConfigKey] }));
    }
  }, [configs[defaultValueConfigKey]]);

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(configs.dataField, mainEntity, subEntities);
    setOptions(newOptions);
  };

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

  const renderCustomValue = () => {
    switch (componentSchema.type) {
      case FORM_COMPONENT_TYPES.SWITCH:
        return (
          <Select
            value={defaultValueConfig?.customValue}
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
        );
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return renderDatePicker();
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return (
          <DatePicker
            value={defaultValueConfig?.customValue}
            showTime
            style={{ width: '100%' }}
            getPopupContainer={getPopupContainer}
            format="YYYY-MM-DD HH:mm:ss"
            onChange={(value) => handleChange('customValue', value)}
          ></DatePicker>
        );
      case FORM_COMPONENT_TYPES.TIME_PICKER:
        return (
          <TimePicker
            value={defaultValueConfig?.customValue}
            use12Hours={!configs.use24Hours}
            format={
              configs.use24Hours
                ? TIME_FORMAT[configs.dateType as keyof typeof TIME_FORMAT]
                : TIME_12_FORMAT[configs.dateType as keyof typeof TIME_12_FORMAT]
            }
            style={{ width: '100%' }}
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleChange('customValue', value)}
          ></TimePicker>
        );
      case FORM_COMPONENT_TYPES.INPUT_PHONE:
        return (
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
        );
      case FORM_COMPONENT_TYPES.INPUT_NUMBER:
        return (
          <InputNumber
            step={configs.step}
            min={configs.verify?.numberLimit ? configs.verify?.min : undefined}
            max={configs.verify?.numberLimit ? configs.verify?.max : undefined}
            precision={configs?.numberFormat?.showPrecision ? configs.numberFormat.precision : 0}
            formatter={(value) => {
              return configs?.numberFormat?.useThousandsSeparator
                ? `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                : value.toString();
            }}
            parser={(value) => value.replace(/,/g, '')}
            suffix={configs?.numberFormat?.showUnit ? configs.numberFormat.unitValue : ''}
          />
        );
      case FORM_COMPONENT_TYPES.RADIO:
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return (
          <Select
            value={defaultValueConfig?.customValue}
            getPopupContainer={getPopupContainer}
            onChange={(value) => {
              handleChange('customValue', value);
            }}
          >
            {options.map((ele, index: number) => (
              <Select.Option key={index} value={ele.id}>
                {ele.label}
              </Select.Option>
            ))}
          </Select>
        );

      case FORM_COMPONENT_TYPES.CHECKBOX:
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return (
          <Select
            mode="multiple"
            value={defaultValueConfig?.customValue}
            getPopupContainer={getPopupContainer}
            onChange={(value) => {
              handleChange('customValue', value);
            }}
          >
            {options.map((ele, index: number) => (
              <Select.Option key={index} value={ele.id}>
                {ele.label}
              </Select.Option>
            ))}
          </Select>
        );

      default:
        return (
          <Input
            value={defaultValueConfig?.customValue}
            minLength={configs.verify?.lengthLimit ? configs.verify?.minLength : undefined}
            maxLength={configs.verify?.lengthLimit ? configs.verify?.maxLength : undefined}
            onChange={(value) => handleChange('customValue', value)}
            placeholder="请输入"
          />
        );
    }
  };

  const renderDatePicker = () => {
    const { YearPicker, MonthPicker } = DatePicker;
    switch (configs.dateType) {
      case DATE_VALUES[DATE_OPTIONS.YEAR]:
        return (
          <YearPicker
            value={defaultValueConfig?.customValue}
            format="YYYY"
            getPopupContainer={getPopupContainer}
            style={{ width: '100%' }}
            onChange={(value) => handleChange('customValue', value)}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.MONTH]:
        return (
          <MonthPicker
            value={defaultValueConfig?.customValue}
            format="YYYY-MM"
            getPopupContainer={getPopupContainer}
            style={{ width: '100%' }}
            onChange={(value) => handleChange('customValue', value)}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.DATE]:
        return (
          <DatePicker
            value={defaultValueConfig?.customValue}
            format="YYYY-MM-DD"
            getPopupContainer={getPopupContainer}
            style={{ width: '100%' }}
            onChange={(value) => handleChange('customValue', value)}
          />
        );
      case DATE_VALUES[DATE_OPTIONS.FULL]:
        return (
          <DatePicker
            value={defaultValueConfig?.customValue}
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            getPopupContainer={getPopupContainer}
            style={{ width: '100%' }}
            onChange={(value) => handleChange('customValue', value)}
          />
        );
      default:
        // 默认显示日期选择器
        return (
          <DatePicker
            value={defaultValueConfig?.customValue}
            style={{ width: '100%' }}
            format="YYYY-MM-DD"
            getPopupContainer={getPopupContainer}
            onChange={(value) => handleChange('customValue', value)}
          />
        );
    }
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
          {componentSchema?.type && renderCustomValue()}

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

import { Button, Form, InputNumber, Message, Radio, Select } from '@arco-design/web-react';
import type { ReactNode } from 'react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../registry';
import styles from '../index.module.less';
import StaticCarouselList from './StaticCarouselList';

const FormItem = Form.Item;
const Option = Select.Option;

interface CarouselFieldMeta {
  key: string;
  label: string;
  placeholder?: string;
  options?: Array<{ label?: ReactNode; value: string }>;
}

interface CarouselContentMeta {
  modeField?: {
    key: string;
    defaultValue?: string;
    options?: Array<{ key: string; text: string; value: string }>;
  };
  dynamicFields?: CarouselFieldMeta[];
  filterField?: {
    key: string;
    label: string;
    buttonText?: string;
  };
  displayCountField?: {
    key: string;
    label: string;
    min?: number;
    max?: number;
    defaultValue?: number;
  };
  staticFieldKey?: string;
}

interface Props {
  id: string;
  item: {
    key?: string;
    meta?: CarouselContentMeta;
  };
  configs: Record<string, unknown>;
  handlePropsChange: (key: string, value: unknown) => void;
}

interface CarouselItem {
  image?: string;
  text?: string;
  url?: string;
  [key: string]: unknown;
}

interface VerifyConfig {
  maxSize?: number;
  maxCount?: number;
}

const WorkbenchCarouselContentConfig = ({ item, configs, handlePropsChange }: Props) => {
  const meta: CarouselContentMeta = item.meta ?? {};
  const modeField = meta.modeField ?? { key: 'dataSourceMode', options: [] };
  const dynamicFields = meta.dynamicFields ?? [];
  const filterField = meta.filterField;
  const displayCountField = meta.displayCountField;
  const staticFieldKey = meta.staticFieldKey ?? 'carouselConfig';

  const currentMode =
    (typeof configs[modeField.key] === 'string' ? (configs[modeField.key] as string) : undefined) ??
    modeField.defaultValue ??
    'dynamic';

  const handleModeChange = (value: string) => {
    handlePropsChange(modeField.key, value);
  };

  const renderDynamicFields = () =>
    dynamicFields.map((field) => (
      <FormItem key={field.key} className={styles.formItem} label={field.label}>
        <Select
          allowClear
          placeholder={field.placeholder}
          value={typeof configs[field.key] === 'string' ? (configs[field.key] as string) : undefined}
          onChange={(value) => handlePropsChange(field.key, value)}
        >
          {(field.options ?? []).map((option) => (
            <Option key={option.value} value={option.value}>
              {option.label ?? option.value}
            </Option>
          ))}
        </Select>
      </FormItem>
    ));

  const renderFilterField = () =>
    filterField ? (
      <FormItem className={styles.formItem} label={filterField.label}>
        <Button
          type="outline"
          onClick={() => {
            Message.info('筛选条件配置功能开发中');
          }}
        >
          {filterField.buttonText ?? '设置条件'}
        </Button>
      </FormItem>
    ) : null;

  const renderDisplayCountField = () =>
    displayCountField ? (
      <FormItem className={styles.formItem} label={displayCountField.label}>
        <InputNumber
          min={displayCountField.min ?? 1}
          max={displayCountField.max ?? 50}
          value={
            typeof configs[displayCountField.key] === 'number'
              ? (configs[displayCountField.key] as number)
              : (displayCountField.defaultValue ?? 10)
          }
          onChange={(value) => {
            const nextValue =
              typeof value === 'number' ? value : (displayCountField.defaultValue ?? displayCountField.min ?? 1);
            handlePropsChange(displayCountField.key, nextValue);
          }}
        />
      </FormItem>
    ) : null;

  return (
    <div>
      <FormItem className={styles.formItem} label="数据来源">
        <Radio.Group type="button" value={currentMode} onChange={handleModeChange} style={{ width: '100%' }}>
          {(modeField.options ?? []).map((option) => (
            <Radio key={option.key} value={option.value} style={{ flex: 1, textAlign: 'center' }}>
              {option.text}
            </Radio>
          ))}
        </Radio.Group>
      </FormItem>

      {currentMode === 'dynamic' ? (
        <>
          {renderDynamicFields()}
          {renderFilterField()}
          {renderDisplayCountField()}
        </>
      ) : (
        <StaticCarouselList
          carouselConfig={(configs[staticFieldKey] as CarouselItem[]) || []}
          maxSizeMB={
            configs.verify && typeof (configs.verify as VerifyConfig).maxSize === 'number'
              ? (configs.verify as VerifyConfig).maxSize
              : 5
          }
          maxCount={
            configs.verify && typeof (configs.verify as VerifyConfig).maxCount === 'number'
              ? (configs.verify as VerifyConfig).maxCount
              : 10
          }
          onConfigChange={(newConfig) => {
            handlePropsChange(staticFieldKey, newConfig);
          }}
        />
      )}
    </div>
  );
};

registerConfigRenderer(CONFIG_TYPES.CAROUSEL_CONTENT, ({ id, item, configs, handlePropsChange }) => (
  <WorkbenchCarouselContentConfig id={id} item={item} configs={configs} handlePropsChange={handlePropsChange} />
));

export default WorkbenchCarouselContentConfig;

import { useEffect, useState } from 'react';
import { Form, InputNumber, Radio, Select } from '@arco-design/web-react';
import { WORKBENCH_CONFIG_TYPES } from '@onebase/ui-kit';
import { getEntityFields } from '@onebase/app';
import { useAppEntitiesStore } from '@/store/store_appEntities';
import { registerConfigRenderer } from '../../registry';
import StaticCarouselList from './StaticCarouselList';
import type { CarouselItem, Props, CarouselContentMeta, VerifyConfig } from './types';
import styles from '../../index.module.less';

const FormItem = Form.Item;
const Option = Select.Option;

const WbCarouselContentConfig = ({ item, configs, handlePropsChange }: Props) => {
  const meta: CarouselContentMeta = item.meta ?? {};
  const modeField = meta.modeField ?? { key: 'dataSourceMode', options: [] };
  const dynamicFields = meta.dynamicFields ?? [];
  // const filterField = meta.filterField;
  const displayCountField = meta.displayCountField;
  const staticFieldKey = meta.staticFieldKey ?? 'carouselConfig';
  const [entityOptions, setEntityOptions] = useState([]);
  const [fieldOptions, setFieldOptions] = useState([]);
  const { appEntities } = useAppEntitiesStore();

  const currentMode =
    (typeof configs[modeField.key] === 'string' ? (configs[modeField.key] as string) : undefined) ??
    modeField.defaultValue ??
    'dynamic';

  // 获取字段列表
  const getFieldList = async (entityUuid: string) => {
    try {
      const res = await getEntityFields({ entityUuid });
      if (res) {
        setFieldOptions(res);
      }
    } catch (err) {
      console.log(err);
    }
  };

  const handleModeChange = (value: string) => {
    handlePropsChange(modeField.key, value);
  };

  const onOptionChange = (key: string, value: string) => {
    if (key === 'contentSource') {
      const option = (entityOptions as Array<{ entityUuid: string; displayName: string; tableName: string }>).find(
        (opt) => opt.entityUuid === value
      );
      handlePropsChange(key, {
        entityUuid: option?.entityUuid,
        displayName: option?.displayName,
        tableName: option?.tableName
      });
      getFieldList(value);
    } else {
      const option = (fieldOptions as Array<{ fieldUuid: string; fieldName: string; displayName: string }>).find(
        (opt) => opt.fieldUuid === value
      );
      handlePropsChange(key, {
        fieldUuid: option?.fieldUuid,
        fieldName: option?.fieldName,
        displayName: option?.displayName
      });
    }
  };

  const renderDynamicFields = () =>
    dynamicFields.map((field) => {
      let selectValue = undefined;
      if (field.key === 'contentSource') {
        selectValue = (configs[field.key] as { entityUuid: string })?.entityUuid;
      } else {
        selectValue = (configs[field.key] as { fieldUuid: string })?.fieldUuid;
      }

      return (
        <FormItem key={field.key} className={styles.formItem} label={field.label}>
          <Select
            allowClear
            placeholder={field.placeholder}
            value={selectValue}
            onChange={(value) => onOptionChange(field.key, value)}
          >
            {field.key === 'contentSource' &&
              entityOptions.map((option: { entityUuid: string; displayName: string; tableName: string }) => (
                <Option key={option.entityUuid} value={option.entityUuid}>
                  {option.displayName ?? option.entityUuid}
                </Option>
              ))}
            {field.key !== 'contentSource' &&
              fieldOptions.map((option: { fieldUuid: string; fieldName: string; displayName: string }) => (
                <Option key={option.fieldUuid} value={option.fieldUuid}>
                  {option.displayName ?? option.fieldName ?? option.fieldUuid}
                </Option>
              ))}
          </Select>
        </FormItem>
      );
    });

  // 获取实体列表
  useEffect(() => {
    if (appEntities.length > 0) {
      setEntityOptions(appEntities);

      const enUuid = (configs.contentSource as { entityUuid: string })?.entityUuid;
      if (enUuid) {
        getFieldList(enUuid);
      }
    }
  }, [appEntities]);

  // const renderFilterField = () =>
  //   filterField ? (
  //     <FormItem className={styles.formItem} label={filterField.label}>
  //       <Button
  //         type="outline"
  //         onClick={() => {
  //           Message.info('筛选条件配置功能开发中');
  //         }}
  //       >
  //         {filterField.buttonText ?? '设置条件'}
  //       </Button>
  //     </FormItem>
  //   ) : null;

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
          {/* 后期开发 */}
          {/* {renderFilterField()} */}
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

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_CAROUSEL_CONTENT, ({ id, item, configs, handlePropsChange }) => (
  <WbCarouselContentConfig id={id} item={item} configs={configs} handlePropsChange={handlePropsChange} />
));

export default WbCarouselContentConfig;

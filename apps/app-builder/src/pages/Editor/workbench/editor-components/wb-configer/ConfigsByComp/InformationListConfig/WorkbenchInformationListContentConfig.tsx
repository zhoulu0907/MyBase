import { Form, Radio, Select } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { WORKBENCH_CONFIG_TYPES } from '@onebase/ui-kit';
import { getEntityFields } from '@onebase/app';
import { useAppEntitiesStore } from '@/store/store_appEntities';
import { registerConfigRenderer } from '../../registry';
import StaticInformationList from './StaticInformationList';
import type { InformationListItem, Props, InformationListContentMeta, VerifyConfig } from './types';
import styles from '../../index.module.less';

const FormItem = Form.Item;
const Option = Select.Option;

const FORM_KEY = {
  contentSource: 'contentSource'
};

const WbInformationListContentConfig = ({ item, configs, handlePropsChange, handleMultiPropsChange }: Props) => {
  const meta: InformationListContentMeta = item.meta ?? {};
  const modeField = meta.modeField ?? { key: 'dataSourceMode', options: [] };
  const dynamicFields = meta.dynamicFields ?? [];
  // const filterField = meta.filterField;
  const staticFieldKey = meta.staticFieldKey ?? 'dynamicInformationList';
  const [entityOptions, setEntityOptions] = useState([]);
  const [fieldOptions, setFieldOptions] = useState([]);
  const { appEntities } = useAppEntitiesStore();

  const currentMode =
    (typeof configs[modeField.key] === 'string' ? (configs[modeField.key] as string) : undefined) ??
    modeField.defaultValue ??
    'dynamic';

  // 获取实体列表
  useEffect(() => {
    if (appEntities.length > 0) {
      setEntityOptions(appEntities);

      const enUuid = (configs[FORM_KEY.contentSource] as { entityUuid: string })?.entityUuid;
      if (enUuid) {
        getFieldList(enUuid);
      }
    }
  }, [appEntities]);

  const handleModeChange = (value: string) => {
    handlePropsChange(modeField.key, value);
  };

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

  const onOptionChange = (key: string, value: string) => {
    // 内容来源变化时，清空其他字段
    if (key === FORM_KEY.contentSource) {
      const option = (entityOptions as Array<{ entityUuid: string; displayName: string; tableName: string }>).find(
        (opt: { entityUuid: string }) => opt?.entityUuid === value
      );

      if (option) {
        const updates: { key: string; value: { entityUuid: string; displayName: string; tableName: string } }[] = [
          {
            key: FORM_KEY.contentSource,
            value: {
              entityUuid: option.entityUuid,
              displayName: option.displayName,
              tableName: option.tableName
            }
          }
        ];

        dynamicFields.forEach((field) => {
          if (field.key !== FORM_KEY.contentSource) {
            updates.push({ key: field.key, value: {} });
          }
        });

        handleMultiPropsChange?.(updates);
        // 查询实体下字段列表
        getFieldList(value);
      }
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
      if (field.key === FORM_KEY.contentSource) {
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
          {/* 暂时隐藏，后期开发 */}
          {/* {renderFilterField()} */}
        </>
      ) : (
        <StaticInformationList
          staticInformationList={(configs[staticFieldKey] as InformationListItem[]) || []}
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

registerConfigRenderer(
  WORKBENCH_CONFIG_TYPES.WB_INFORMATION_LIST_CONTENT,
  ({ id, item, configs, handlePropsChange, handleMultiPropsChange }) => (
    <WbInformationListContentConfig
      id={id}
      item={item}
      configs={configs}
      handlePropsChange={handlePropsChange}
      handleMultiPropsChange={handleMultiPropsChange}
    />
  )
);

export default WbInformationListContentConfig;

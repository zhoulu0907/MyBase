import { Button, Form, Select } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { useAppStore } from '@/store/store_app';
import { getEntityGraph } from '@onebase/app';
import DataSelectionProcessConfig from './components/DataSelectionProcessConfig';
import DropdownRender from './components/DropdownRender';
import FillingRuleSettingsModal from './components/FillingRuleSettingsModal';
import styles from './index.module.less';
import { useResourceStore } from '@/store/store_resource';
import { hiddenFieldTypes } from '../DynamicTableConfig';
import { getPopupContainer } from '@onebase/ui-kit';

const FormItem = Form.Item;
const Option = Select.Option;

const ATTR_KEY = {
  IS_SETTED: 'isSetted',
  DISPLAYFIELDS: 'displayFields',
  DISPLAYFIELDSOPTIONS: 'displayFieldsOptions',
  FILLFORMFIELDOPTIONS: 'fillFormFieldOptions',
  FILLRULESETTING: 'fillRuleSetting',
  DATAFIELDS: 'dataFields',
  SELECTDATAFIELDS: 'selectDataFields',
  DYNAMICTABLECONFIG: 'dynamicTableConfig',
  FILTERCONDITION: 'filterCondition'
};

export interface DynamicSelectDataSourceConfigProps {
  handleMultiPropsChange?: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDataSourceConfig: React.FC<DynamicSelectDataSourceConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const { curAppId } = useAppStore();
  const { curDataSourceId } = useResourceStore();
  const tableConfig = configs[ATTR_KEY.DYNAMICTABLECONFIG];

  const [dataSourceOptions, setDataSourceOptions] = useState<any[]>([]); // 数据源
  const [selectedDataSource, setSelectedDataSource] = useState(configs[item.key] || null); // 选择的数据源
  const [fieldsMap, setFieldsMap] = useState<Map<string, any[]>>(new Map()); // 预构造选择的数据源

  const [selectDataVisible, setSelectDataVisibleVisible] = useState(false); //数据选择过程 popup
  const [isSetted, setIsSetted] = useState(configs[ATTR_KEY.IS_SETTED]);

  // 设置显示字段
  const [displayFieldOptions, setDisplayFieldOptions] = useState<any[]>(configs[ATTR_KEY.DISPLAYFIELDSOPTIONS]);
  const [filteredFieldsData, setFilteredFieldsData] = useState<any[]>(configs[ATTR_KEY.FILLFORMFIELDOPTIONS]);
  const [selected, setSelected] = useState<string[]>([]);

  const [ruleSettingVisible, setRuleSettingVisible] = useState(false); //填充规则设置popup
  const [selectRule, setSelectRule] = useState<any[]>(configs[ATTR_KEY.FILLRULESETTING]);

  useEffect(() => {
    curAppId && curDataSourceId && getDataSource();
  }, [curAppId, curDataSourceId]);

  useEffect(() => {
    const displayFields = configs[ATTR_KEY.DISPLAYFIELDS];
    if (displayFields.length > 0) {
      const selectItems = displayFields.map((item: any) => item.value);
      setSelected(selectItems);
    }
  }, []);

  useEffect(() => {
    const fieldsMap = new Map<string, any[]>(dataSourceOptions.map((d: any) => [d.entityId, d.fields ?? []] as const));
    setFieldsMap(fieldsMap);
  }, [dataSourceOptions]);

  const getDataSource = async () => {
    const res = await getEntityGraph(curDataSourceId);
    setDataSourceOptions(res.entities);
  };

  const handleSourceChange = (value: any) => {
    const dataSource = dataSourceOptions.find((data) => data.entityId === value);
    const data = {
      entityId: value,
      entityName: dataSource.entityName
    };
    setSelectedDataSource(data);
    // table元数据 初始化
    tableConfig.metaData = value;
    tableConfig.showOpearate = false;
    tableConfig.sortByObject = {
      fieldName: undefined,
      sortBy: 1
    };

    // 查找
    const displayFieldOptions = (fieldsMap.get(value) ?? []).filter(
      (item: any) => !hiddenFieldTypes.includes(item.fieldType)
    );
    setDisplayFieldOptions(displayFieldOptions);

    // 去掉系统字段 和需要隐藏的字段
    const filteredFieldsData = (fieldsMap.get(value) ?? []).filter(
      (f: any) => !f.isSystemField && !hiddenFieldTypes.includes(f.fieldType)
    );
    setFilteredFieldsData(filteredFieldsData);

    //reset 数据选择过程 显示在表单中 填充规则
    setIsSetted(false);
    setSelected([]);
    setSelectRule([]);
    const displayFields: any[] = [];

    // 选择数据时的显示字段 默认选中.  TODO
    const selectedFields = filteredFieldsData.map((item: any) => item.fieldName);

    handleMultiPropsChange?.([
      {
        key: item.key,
        value: data as any
      },
      { key: ATTR_KEY.DISPLAYFIELDSOPTIONS, value: displayFieldOptions },
      { key: ATTR_KEY.DATAFIELDS, value: displayFieldOptions },
      { key: ATTR_KEY.FILLFORMFIELDOPTIONS, value: filteredFieldsData },
      { key: ATTR_KEY.SELECTDATAFIELDS, value: selectedFields },
      { key: ATTR_KEY.DYNAMICTABLECONFIG, value: tableConfig },
      { key: ATTR_KEY.DISPLAYFIELDS, value: displayFields },
      // reset
      { key: ATTR_KEY.IS_SETTED, value: false },
      { key: ATTR_KEY.FILLRULESETTING, value: [] },
      { key: ATTR_KEY.FILTERCONDITION, value: [] }
    ]);
  };

  const toSetting = () => {
    setSelectDataVisibleVisible(true);
    if (!isSetted) {
      setIsSetted(true);
      handlePropsChange(ATTR_KEY.IS_SETTED, true);
    }
  };

  const handleDisplayFieldOptions = (options: any) => {
    setDisplayFieldOptions(options);
    const displayFields = options.reduce((fields: any[], item: any) => {
      if (selected.includes(item.fieldName)) {
        fields.push({
          label: item.displayName,
          value: item.fieldName
        });
      }
      return fields;
    }, []);
    handleMultiPropsChange?.([
      { key: ATTR_KEY.DISPLAYFIELDSOPTIONS, value: options },
      { key: ATTR_KEY.DISPLAYFIELDS, value: displayFields }
    ]);
  };

  const handleSelectedChange = (value: any) => {
    setSelected(value);
    const displayFields = displayFieldOptions.reduce((fields: any[], option: any) => {
      if (value.includes(option.fieldName)) {
        fields.push({
          label: option.displayName,
          value: option.fieldName
        });
      }
      return fields;
    }, []);
    handlePropsChange(ATTR_KEY.DISPLAYFIELDS, displayFields);
  };

  const handleOKModal = (values: any) => {
    setSelectRule(values);
    handlePropsChange(ATTR_KEY.FILLRULESETTING, values);
    setRuleSettingVisible(false);
  };

  return (
    <>
      <div className={styles.dataSourceContainer}>
        {/* 选择数据源 */}
        <FormItem layout="vertical" labelAlign="left" label={'数据源'} className={styles.formItem}>
          <Select
            placeholder="请选择"
            defaultValue={configs[item.key].entityId}
            getPopupContainer={getPopupContainer}
            onChange={(value) => {
              handleSourceChange(value);
            }}
          >
            {dataSourceOptions.map((option) => (
              <Option key={option.entityId} value={option.entityId}>
                {option.entityName}
              </Option>
            ))}
          </Select>
        </FormItem>

        {/* 数据选择过程 */}
        {selectedDataSource.entityId && (
          <div>
            <FormItem layout="vertical" labelAlign="left" label={'数据选择过程'} className={styles.formItem}>
              <Button long onClick={toSetting}>
                {isSetted ? '已设置' : '设置'}
              </Button>
              <DataSelectionProcessConfig
                visible={selectDataVisible}
                id={id}
                item={item}
                configs={configs}
                handlePropsChange={handlePropsChange}
                handleMultiPropsChange={handleMultiPropsChange}
                setVisible={() => setSelectDataVisibleVisible(false)}
              />
            </FormItem>

            {/* 数据选择后 */}
            <FormItem layout="vertical" labelAlign="left" label={'数据选择后'} className={styles.formItem}>
              <FormItem layout="vertical" labelAlign="left" label={'显示在表单中'} className={styles.formItem}>
                <Select
                  value={selected}
                  onChange={(e) => handleSelectedChange(e)}
                  placeholder="设置显示字段"
                  getPopupContainer={getPopupContainer}
                  renderFormat={() => (selected.length > 0 ? `显示 ${selected.length} 个字段` : '设置显示字段')}
                  dropdownRender={() => (
                    <div className={styles.dropdownRender}>
                      <DropdownRender
                        selected={selected}
                        setSelected={handleSelectedChange}
                        displayFieldOptions={displayFieldOptions}
                        hasEditLabel={false}
                        setDisplayFieldOptions={handleDisplayFieldOptions}
                      />
                    </div>
                  )}
                />
              </FormItem>
              <FormItem layout="vertical" labelAlign="left" label={'填充到表单字段'} className={styles.noMarginBottom}>
                <Button long onClick={() => setRuleSettingVisible(true)}>
                  {selectRule.length > 0 ? '已设置填充规则' : '填充规则设置'}
                </Button>
                <FillingRuleSettingsModal
                  visible={ruleSettingVisible}
                  fieldOptions={filteredFieldsData}
                  selectRule={selectRule}
                  onCancel={() => setRuleSettingVisible(false)}
                  onOk={(values: any) => handleOKModal(values)}
                />
              </FormItem>
            </FormItem>
          </div>
        )}
      </div>
    </>
  );
};

export default DynamicDataSourceConfig;

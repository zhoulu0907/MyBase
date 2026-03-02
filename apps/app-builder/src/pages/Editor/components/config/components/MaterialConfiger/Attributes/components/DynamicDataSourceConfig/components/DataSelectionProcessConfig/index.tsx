import { Button, Drawer, Form, Grid, Select } from '@arco-design/web-react';
import React, { useEffect, useMemo, useState } from 'react';

import { getPopupContainer, ListComp } from '@onebase/ui-kit';

import type { DynamicSelectDataSourceConfigProps } from '../..';
import styles from '../../index.module.less';
import DropdownRender from '../DropdownRender';
import FilterDataModal from '../FilterDataModal';

interface DataSelectionProcessConfigProps extends DynamicSelectDataSourceConfigProps {
  visible: boolean;
  setVisible: any;
}

const FormItem = Form.Item;
const Option = Select.Option;

const SUB_ATTR_KEY = {
  DEFAULTVALUE: 'defaultValue',
  DATAFIELDS: 'dataFields',
  SELECTDATAFIELDS: 'selectDataFields',
  FILTERDATA: 'filterData',
  FILTERCONDITION: 'filterCondition',
  OPERATIONAUTH: 'operationAuth',
  FASTFILTER: 'fastFilter',
  DYNAMICTABLECONFIG: 'dynamicTableConfig',
  COLUMNS: 'columns',
  SORTBYOBJECT: 'sortByObject',
  SELECTEDDATASOURCE: 'selectedDataSource',
  SEARCHITEMS: 'searchItems'
};

const sortOptions = [
  { value: 'ASC', label: '升序' },
  { value: 'DESC', label: '降序' }
];

const DataSelectionProcessConfig: React.FC<DataSelectionProcessConfigProps> = ({
  visible,
  setVisible,
  item,
  configs,
  id,
  handlePropsChange,
  handleMultiPropsChange
}) => {
  const isDropdown = configs?.selectMethod === 'dropdown';
  const tableConfig = configs[SUB_ATTR_KEY.DYNAMICTABLECONFIG];
  const [filterDataVisible, setFilterDataVisible] = useState(false); //添加过滤条件popup

  const [displayFieldOptions, setDisplayFieldOptions] = useState(configs[SUB_ATTR_KEY.DATAFIELDS]);
  const [selected, setSelected] = useState(configs[SUB_ATTR_KEY.SELECTDATAFIELDS]);
  const [tableHeader, setTableHeader] = useState<any[]>(tableConfig[SUB_ATTR_KEY.COLUMNS]); // table header
  const [tableSearch, setTableSearch] = useState<any[]>(
    tableConfig[SUB_ATTR_KEY.SEARCHITEMS]?.map((ele: any) => ele.value) || []
  );

  const [filterCondition, setFilterCondition] = useState(configs[SUB_ATTR_KEY.FILTERCONDITION]);

  const [sortFieldValue, setSortFieldValue] = useState<string>(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.[0]?.fieldName);
  const [sortValue, setSortValue] = useState<number>(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.[0]?.sortBy);

  // 回显字段：用于排除和预览显示
  const echoField = useMemo(() => {
    return Array.isArray(configs?.displayFields) ? configs.displayFields[0]?.value : undefined;
  }, [configs?.displayFields]);

  const echoFieldObj = useMemo(() => {
    return Array.isArray(configs?.displayFields) ? configs.displayFields[0] : undefined;
  }, [configs?.displayFields]);

  useEffect(() => {
    if (visible) {
      setDisplayFieldOptions(configs[SUB_ATTR_KEY.DATAFIELDS]);
      const initialSelected = configs[SUB_ATTR_KEY.SELECTDATAFIELDS] || [];
      //   .filter((f: any) => f !== echoField);
      setSelected(initialSelected);
      setFilterCondition(configs[SUB_ATTR_KEY.FILTERCONDITION]);
      setSortFieldValue(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.[0]?.fieldName);
      setSortValue(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.[0]?.sortBy);
      tableConfig.metaData = configs[SUB_ATTR_KEY.SELECTEDDATASOURCE].entityUuid;
      tableConfig.tableName = configs[SUB_ATTR_KEY.SELECTEDDATASOURCE].tableName;
      handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
    }
  }, [visible, echoField]);

  useEffect(() => {
    handleOptionsChange();
  }, [displayFieldOptions, selected]);

  const handleOptionsChange = () => {
    const selectableOptions =
      isDropdown && echoField
        ? displayFieldOptions || []
        : // .filter((opt: any) => opt.fieldName !== echoField)
          displayFieldOptions || [];
    const header = selectableOptions.reduce((fields: any[], option: any) => {
      if (selected.includes(option.fieldName)) {
        fields.push({
          title: option.displayName,
          dataIndex: option.fieldName
        });
      }
      return fields;
    }, []);
    setTableHeader(header);
    tableConfig.columns = header;
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
  };

  const handleDisplayFieldOptions = (value: any) => {
    setDisplayFieldOptions(value);
    handlePropsChange(SUB_ATTR_KEY.DATAFIELDS, value);
  };

  const handleSelectedChange = (value: any) => {
    setSelected(value);
    const searchs = tableSearch.filter((e) => value.includes(e));
    setTableSearch(searchs);
    const newSearchItems = searchs.map((ele: string) => {
      const fielditem = displayFieldOptions.find((e: any) => e.fieldName === ele);
      return {
        label: fielditem.displayName,
        value: fielditem.fieldName
      };
    });
    tableConfig.searchItems = newSearchItems;

    handleMultiPropsChange?.([
      { key: SUB_ATTR_KEY.SELECTDATAFIELDS, value: value },
      { key: SUB_ATTR_KEY.DYNAMICTABLECONFIG, value: { ...tableConfig, searchItems: newSearchItems } }
    ]);
  };

  const handleSelectedChangeSingle = (value: any) => {
    // const next = value && value !== echoField ? [value] : [];
    const next = value ? [value] : [];
    setSelected(next);
    const selectableOptions = isDropdown && echoField ? displayFieldOptions || [] : displayFieldOptions || [];
    const header = selectableOptions.reduce((fields: any[], option: any) => {
      if (selected.includes(option.fieldName)) {
        fields.push({
          title: option.displayName,
          dataIndex: option.fieldName
        });
      }
      return fields;
    }, []);
    setTableHeader(header);
    tableConfig.columns = header;

    handleMultiPropsChange?.([
      { key: SUB_ATTR_KEY.SELECTDATAFIELDS, value: next },
      { key: SUB_ATTR_KEY.DYNAMICTABLECONFIG, value: tableConfig }
    ]);
  };

  const handleSearcItemsChange = (value: any) => {
    setTableSearch(value);
    const newSearchItems = value?.map((ele: string) => {
      const fielditem = displayFieldOptions.find((e: any) => e.fieldName === ele);
      return {
        label: fielditem.displayName,
        value: fielditem.fieldName
      };
    });
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, { ...tableConfig, searchItems: newSearchItems });
  };

  const handlSortFieldValueChange = (value: string) => {
    const sortBy = value ? sortValue : 1;
    tableConfig.sortByObject = [
      {
        fieldName: value,
        sortBy: sortBy
      }
    ];

    setSortFieldValue(value);
    setSortValue(sortBy);
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
  };

  const handleSortValueChange = (value: number) => {
    setSortValue(value);
    tableConfig.sortByObject = [
      {
        fieldName: sortFieldValue,
        sortBy: value
      }
    ];
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
  };

  const handleOKModal = (values: any) => {
    const newFilterCondition:any = getConditionChildren(values);
    tableConfig.filterCondition = newFilterCondition?.children || [];
    handleMultiPropsChange?.([
      { key: SUB_ATTR_KEY.FILTERCONDITION, value: values },
      { key: SUB_ATTR_KEY.DYNAMICTABLECONFIG, value: tableConfig }
    ]);
    setFilterCondition(values);
    setFilterDataVisible(false);
  };

  const formatConditions = (condition: any) => {
    const newConditions = condition.conditions.map((item: any) => ({
      nodeType: 'CONDITION',
      fieldName: item.fieldKey.split('.')[1],
      operator: item.op,
      fieldValue: typeof item.value === 'object' ? [item.value?.id] : [item.value]
    }));

    return {
      nodeType: 'GROUP',
      combinator: 'AND',
      children: newConditions
    };
  };

  const getConditionChildren = (conditions: any[]) => {
    if (conditions.length === 0) {
      return {};
    }
    if (conditions.length === 1) {
      return formatConditions(conditions[0]);
    }
    const formatChildren: any[] = [];
    conditions.forEach((item: any) => {
      const newConditions = formatConditions(item);
      formatChildren.push(newConditions);
    });
    return {
      nodeType: 'GROUP',
      combinator: 'OR',
      children: conditions.map(formatConditions)
    };
  };

  return (
    <>
      <Drawer
        placement="bottom"
        height={'80vh'}
        visible={visible}
        headerStyle={{ justifyContent: 'center' }}
        bodyStyle={{ padding: 0 }}
        title="数据选择过程"
        className={styles.drawerContainer}
        footer={null}
        onCancel={setVisible}
      >
        <div className={styles.container}>
          <div className={styles.leftColumn}>
            {isDropdown ? (
              <div className={styles.dropdownPreview}>
                <span className={styles.previewTitle}>图例</span>
                <div className={styles.previewCard}>
                  <div className={styles.cardHeader}>
                    {(() => {
                      const echoLabel =
                        echoFieldObj?.label ||
                        (displayFieldOptions || []).find((opt: any) => opt.fieldName === echoFieldObj?.value)
                          ?.displayName ||
                        '标题字段';
                      return echoLabel;
                    })()}
                  </div>
                  <div className={styles.cardSubTitle}>
                    {(() => {
                      const aux = selected || [];
                      //   .filter((f: any) => f !== echoFieldObj?.value);
                      const labels = aux
                        .map(
                          (f: any) => (displayFieldOptions || []).find((opt: any) => opt.fieldName === f)?.displayName
                        )
                        .filter(Boolean);
                      return labels.length > 0 ? labels[0] : '描述字段';
                    })()}
                  </div>
                </div>
              </div>
            ) : (
              <div className={styles.rightFlexTable}>
                <ListComp.XTable
                  cpName={id}
                  id={id}
                  {...tableConfig}
                  columns={tableHeader}
                  runtime={true}
                  showAddBtn={false}
                />
              </div>
            )}
          </div>
          <div className={styles.rightColumn}>
            <Form layout="vertical">
              {/* <FormItem label="按钮文字">
                <Input
                  placeholder="请输入按钮文字"
                  value={configs[SUB_ATTR_KEY.DEFAULTVALUE]}
                  onChange={(value) => {
                    handlePropsChange(SUB_ATTR_KEY.DEFAULTVALUE, value);
                  }}
                />
              </FormItem> */}
              <FormItem
                label={isDropdown ? '辅助字段' : '选择数据时的显示字段'}
                tooltip={
                  isDropdown
                    ? {
                        content: '可配置辅助字段，结合回显字段，帮助用户选择关联数据',
                        position: 'bottom'
                      }
                    : null
                }
              >
                {/* <div>{JSON.stringify(displayFieldOptions)}</div> */}
                {isDropdown ? (
                  <Select
                    value={selected?.[0]}
                    onChange={(e) => handleSelectedChangeSingle(e)}
                    placeholder="请选择"
                    allowClear
                  >
                    {(displayFieldOptions || [])
                      .filter((opt: any) => opt.fieldName !== echoField)
                      .map((option: any) => (
                        <Option key={option.fieldName} value={option.fieldName}>
                          {option.displayName}
                        </Option>
                      ))}
                  </Select>
                ) : (
                  <Select
                    value={selected}
                    onChange={(e) => handleSelectedChange(e)}
                    placeholder="设置显示字段"
                    renderFormat={() => (selected.length > 0 ? `显示 ${selected.length} 个字段` : '设置显示字段')}
                    dropdownRender={() => (
                      <div className={styles.dropdownRender}>
                        <DropdownRender
                          selected={selected}
                          setSelected={handleSelectedChange}
                          displayFieldOptions={displayFieldOptions}
                          handleOptionsChange={handleOptionsChange}
                          setDisplayFieldOptions={handleDisplayFieldOptions}
                        />
                      </div>
                    )}
                  />
                )}
              </FormItem>
              <FormItem label="数据过滤">
                <Button type="secondary" long onClick={() => setFilterDataVisible(true)}>
                  {filterCondition.length > 0 ? '已添加过滤条件' : '添加过滤条件'}
                </Button>
                <FilterDataModal
                  visible={filterDataVisible}
                  item={item}
                  configs={configs}
                  onCancel={() => setFilterDataVisible(false)}
                  onOk={(values: any) => handleOKModal(values)}
                />
              </FormItem>
              <FormItem label="数据排序规则">
                <Grid.Row gutter={8}>
                  <Grid.Col span={sortFieldValue ? 18 : 24}>
                    <Select
                      value={sortFieldValue}
                      onChange={(e) => handlSortFieldValueChange(e)}
                      placeholder="请选择"
                      getPopupContainer={getPopupContainer}
                      allowClear
                    >
                      {(isDropdown && echoField
                        ? displayFieldOptions || []
                        : // .filter((opt: any) => opt.fieldName !== echoField)
                          displayFieldOptions
                      ).map((option: any) => (
                        <Option key={option.fieldName} value={option.fieldName}>
                          {option.displayName}
                        </Option>
                      ))}
                    </Select>
                  </Grid.Col>
                  {sortFieldValue && (
                    <Grid.Col span={6}>
                      <Select
                        defaultValue={sortValue}
                        onChange={(e) => handleSortValueChange(e)}
                        placeholder="请选择"
                        getPopupContainer={getPopupContainer}
                      >
                        {sortOptions.map((option) => (
                          <Option key={option.value} value={option.value}>
                            {option.label}
                          </Option>
                        ))}
                      </Select>
                    </Grid.Col>
                  )}
                </Grid.Row>
              </FormItem>
              {/* <FormItem label="操作权限">
                <Checkbox defaultChecked>允许新增数据源表数据</Checkbox>
                <Tooltip content="成员需要有数据源表的添加权限，才可新增数据">
                  <IconQuestionCircleFill className={styles.iconQuestionCircleFill} />
                </Tooltip>
              </FormItem> */}
              {!isDropdown && (
                <FormItem label="数据搜索项">
                  <Select
                    value={tableSearch}
                    mode="multiple"
                    onChange={handleSearcItemsChange}
                    placeholder="设置数据搜索项"
                    getPopupContainer={getPopupContainer}
                  >
                    {(displayFieldOptions || [])
                      .filter((opt: any) => selected.includes(opt.fieldName))
                      .map((option: any) => (
                        <Option key={option.fieldName} value={option.fieldName}>
                          {option.displayName}
                        </Option>
                      ))}
                  </Select>
                </FormItem>
              )}
            </Form>
          </div>
        </div>
      </Drawer>
    </>
  );
};

export default DataSelectionProcessConfig;

import { Button, Drawer, Form, Grid, Input, Radio, Select, Tree } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { IconCaretDown } from '@arco-design/web-react/icon';
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
const RadioGroup = Radio.Group;
const maxFastFilterCount = 3;

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
  SELECTEDDATASOURCE: 'selectedDataSource'
};

const sortOptions = [
  { value: 1, label: '升序' },
  { value: 2, label: '降序' }
];

const fastFilterOptions = [
  { label: '单选按钮组', value: 'radioGroup' },
  { label: '单行文本', value: 'singleText' },
  { label: '提交时间', value: 'submitTime' },
  { label: '多行文本', value: 'multiText' },
  { label: '提交人', value: 'submitPerson' },
  { label: '更新时间', value: 'updateDate' }
];

const treeData = [
  {
    title: '全部',
    key: '0-0',
    // selectable: false, // 父节点不可选
    children: [
      {
        key: 'ciki',
        title: 'Ciki',
        icon: <IconCaretDown />
      }
    ]
  }
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

  const [filterCondition, setFilterCondition] = useState(configs[SUB_ATTR_KEY.FILTERCONDITION]);

  const [sortFieldValue, setSortFieldValue] = useState<string>(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT].fieldName);
  const [sortValue, setSortValue] = useState<number>(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT].sortBy);

  const [isFastFilter, setIsFastFilter] = useState<boolean>(false);
  const [fastFilters, setFastFilters] = useState<any[]>([]);

  // const sortType = 'normal';

  // const droplist = (
  //   <Menu className={styles.hideScrollbarCommon} onClickMenuItem={(key) => handleSelectFastFilter(key)}>
  //     {fastFilterOptions.map((opt) => (
  //       <Menu.Item key={opt.value} disabled={fastFilters.includes(opt)}>
  //         {opt.label}
  //       </Menu.Item>
  //     ))}
  //   </Menu>
  // );

  useEffect(() => {
    if (visible) {
      setDisplayFieldOptions(configs[SUB_ATTR_KEY.DATAFIELDS]);
      const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0]?.value : undefined;
      const initialSelected = (configs[SUB_ATTR_KEY.SELECTDATAFIELDS] || []).filter((f: any) => f !== echoField);
      setSelected(initialSelected);
      setFilterCondition(configs[SUB_ATTR_KEY.FILTERCONDITION]);
      setSortFieldValue(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.fieldName);
      setSortValue(tableConfig[SUB_ATTR_KEY.SORTBYOBJECT]?.sortBy);
      tableConfig.tableName = configs[SUB_ATTR_KEY.SELECTEDDATASOURCE].tableName;
      handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
    }
  }, [visible]);

  useEffect(() => {
    handleOptionsChange();
  }, [displayFieldOptions, selected]);

  const handleOptionsChange = () => {
    const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0]?.value : undefined;
    const selectableOptions =
      isDropdown && echoField
        ? (displayFieldOptions || []).filter((opt: any) => opt.fieldName !== echoField)
        : displayFieldOptions || [];
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
    const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0]?.value : undefined;
    const filtered = Array.isArray(value) && echoField ? value.filter((v) => v !== echoField) : value;
    setSelected(filtered);
    handlePropsChange(SUB_ATTR_KEY.SELECTDATAFIELDS, filtered);
  };

  const handleSelectedChangeSingle = (value: any) => {
    const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0]?.value : undefined;
    const next = value && value !== echoField ? [value] : [];
    setSelected(next);
    handlePropsChange(SUB_ATTR_KEY.SELECTDATAFIELDS, next);
    handleOptionsChange();
  };

  const handlSortFieldValueChange = (value: string) => {
    const sortBy = value ? sortValue : 1;
    tableConfig.sortByObject = {
      fieldName: value,
      sortBy: sortBy
    };

    setSortFieldValue(value);
    setSortValue(sortBy);
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
  };

  const handleSortValueChange = (value: number) => {
    setSortValue(value);
    tableConfig.sortByObject = {
      fieldName: sortFieldValue,
      sortBy: value
    };
    handlePropsChange(SUB_ATTR_KEY.DYNAMICTABLECONFIG, tableConfig);
  };

  const handleOKModal = (values: any) => {
    const newFilterCondition = getConditionChildren(values);
    tableConfig.filterCondition = newFilterCondition;
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
      fieldValue: [item.value]
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

  // const handleSelectFastFilter = (key: any) => {
  //   if (fastFilters.length === maxFastFilterCount) {
  //     Message.warning('最多添加三个分组字段');
  //   } else {
  //     const obj: any = fastFilterOptions.find((opt) => opt.value === key);
  //     if (obj?.label !== '提交人') {
  //       obj.sortType = 'normal';
  //     }
  //     setFastFilters([...fastFilters, obj]);
  //   }
  // };

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
            {fastFilters.length > 0 && (
              <div className={styles.leftTree}>
                <Tree treeData={treeData}></Tree>
              </div>
            )}
            {isDropdown ? (
              <div className={styles.dropdownPreview}>
                <span className={styles.previewTitle}>图例</span>
                <div className={styles.previewCard}>
                  <div className={styles.cardHeader}>
                    {(() => {
                      const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0] : undefined;
                      const echoLabel =
                        echoField?.label ||
                        (displayFieldOptions || []).find((opt: any) => opt.fieldName === echoField?.value)
                          ?.displayName ||
                        '标题字段';
                      return echoLabel;
                    })()}
                  </div>
                  <div className={styles.cardSubTitle}>
                    {(() => {
                      const echoField = Array.isArray(configs?.displayFields) ? configs.displayFields[0] : undefined;
                      const aux = (selected || []).filter((f: any) => f !== echoField?.value);
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
              <FormItem label="按钮文字">
                <Input
                  placeholder="请输入按钮文字"
                  value={configs[SUB_ATTR_KEY.DEFAULTVALUE]}
                  onChange={(value) => {
                    handlePropsChange(SUB_ATTR_KEY.DEFAULTVALUE, value);
                  }}
                />
              </FormItem>
              <FormItem label={isDropdown ? '辅助字段' : '选择数据时的显示字段'}>
                {isDropdown ? (
                  <Select
                    value={selected?.[0]}
                    onChange={(e) => handleSelectedChangeSingle(e)}
                    placeholder="请选择"
                    allowClear
                  >
                    {(displayFieldOptions || [])
                      .filter((opt: any) => opt.fieldName !== configs?.displayFields?.[0]?.value)
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
                      {(isDropdown && Array.isArray(configs?.displayFields)
                        ? (displayFieldOptions || []).filter(
                            (opt: any) => opt.fieldName !== configs.displayFields[0]?.value
                          )
                        : displayFieldOptions
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
              {/* <FormItem label="快捷筛选" layout="horizontal" className={styles.switchLabel}>
                <Switch className={styles.switchButton} onChange={(value) => setIsFastFilter(value)} />
              </FormItem>
              {!isFastFilter ? (
                <div className={styles.tip}>开启后可添加筛选字段，表格左侧会显示字段值供成员快速选择。</div>
              ) : (
                <FormItem>
                  <Dropdown droplist={droplist} trigger="click">
                    <Button type="text" style={{ paddingLeft: 0 }}>
                      <IconPlus />
                      添加筛选字段
                    </Button>
                  </Dropdown>

                  <ReactSortable list={fastFilters} setList={setFastFilters} handle=".drag-handle" animation={150}>
                    {fastFilters.map((filter, index) => (
                      <div key={filter.value} className={styles.fastFilterDiv}>
                        <div className={styles.fastFilterContent}>
                          <span className={styles.filterLabel}>{filter.label}</span>
                          <IconDragDotVertical
                            style={{ marginRight: 8, cursor: 'move', color: '#838892' }}
                            className="drag-handle"
                          />
                          <IconDelete
                            className={styles.deleteBtn}
                            onClick={() => setFastFilters(fastFilters.filter((_, i) => i !== index))}
                          />
                        </div>
                        {filter?.sortType === 'normal' ? (
                          <RadioGroup
                            type="button"
                            name="lang"
                            defaultValue={1}
                            style={{ width: '100%', display: 'flex' }}
                          >
                            {sortOptions.map((sort) => (
                              <Radio
                                key={sort.label}
                                value={sort.value}
                                style={{
                                  flex: 1,
                                  textAlign: 'center',
                                  whiteSpace: 'nowrap'
                                }}
                              >
                                {sort.label}
                              </Radio>
                            ))}
                          </RadioGroup>
                        ) : (
                          <Button type="outline" style={{ width: '100%' }}>
                            按相同值排序
                          </Button>
                        )}
                      </div>
                    ))}
                  </ReactSortable>
                </FormItem>
              )} */}
            </Form>
          </div>
        </div>
      </Drawer>
    </>
  );
};

export default DataSelectionProcessConfig;

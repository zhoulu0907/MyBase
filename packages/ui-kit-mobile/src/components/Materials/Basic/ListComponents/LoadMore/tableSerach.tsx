import { memo, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { Button, Dropdown } from '@arco-design/mobile-react';
import { useSignals } from '@preact/signals-react/runtime';

import filterIcon from '@/assets/images/filter.svg';
import { IconSearch } from '@arco-design/mobile-react/esm/icon';
import {
  useFormEditorSignal,
  useAppEntityStore,
  getComponentSchema,
  COMPONENT_MAP,
  FORM_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  DEFAULT_VALUE_TYPES
} from '@onebase/ui-kit';
import { FormComp } from '../../FormComponents';
import './index.css';
import DraftBox from './DraftBox';

const colorConfig = {
  normal: 'rgb(var(--primary-6))',
  active: 'rgb(var(--primary-9))',
  disabled: 'rgb(var(--primary-1))'
};

const ghostBgColor = {
  normal: '#FFF',
  active: 'rgb(var(--primary-6))',
  disabled: '#FFF'
};

interface TableSearchConfig {
  searchItems?: any[];
  labelColSpan?: number;
  runtime: boolean;
  form?: any;
  queryData: any;
  onSearch?: () => void;
  showDraftBox?: boolean;
  showFromPageData?: Function;
  metaData: any;
  tableName: string;
  refresh?: number;
  tableColumns?: any[];
}

const TableSearch = memo((props: TableSearchConfig) => {
  useSignals();

  const { searchItems, labelColSpan, runtime, onSearch, form, queryData, showDraftBox, showFromPageData, metaData, tableName, tableColumns, refresh } = props;
  const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
  const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
  const { mainEntity } = useAppEntityStore();

  // 显示搜索条件
  const [showDropdown, setShowDropdown] = useState(false);

  const renderSearchBar = (firstSearchItem: any) => {
    return (
      <div className="search-bar">
        {showDraftBox && (
          <DraftBox
            onlyIcon
            refresh={refresh}
            metaData={metaData}
            tableName={tableName}
            tableColumns={tableColumns}
            showFromPageData={showFromPageData}
          />
        )}
        <div className="search-bar-icon">
          <IconSearch />
        </div>
        <div className="search-bar-input">{renderSearchItem(firstSearchItem, true)}</div>
        {searchItems?.length && searchItems.length > 1 && (
          <img
            className="search-bar-filter"
            src={filterIcon}
            alt=""
            onClick={() => {
              setShowDropdown(true);
              form?.setFieldsValue(queryData?.value || {});
            }}
          />
        )}
      </div>
    );
  };

  const renderSearchItem = (searchItem: any, first?: boolean) => {
    // 组件类型
    const fieldType = mainEntity.fields.find((field) => field.fieldName === searchItem.value)?.fieldType;

    if (!fieldType) {
      return;
    }

    // 字段配置
    const dataField = [mainEntity.tableName, searchItem.value];
    // 组件类型
    const cpType = COMPONENT_MAP[fieldType as any];
    const detailMode = false;

    // 组件配置
    let componentConfig: any = {};

    if (cpType) {
      // 组件默认配置
      const defaultSchema = getComponentSchema(cpType as any);

      componentConfig = {
        ...defaultSchema.config,
        label: {
          display: first ? false : true,
          text: searchItem.label
        },
        layout: first ? 'vertical' : 'horizontal',
        labelColSpan,
        dataField,
        defaultValueConfig: { type: DEFAULT_VALUE_TYPES.CUSTOM, customValue: null },
        defaultDeptValue: undefined, // 清空search是部门选择的默认值
        defaultUserValue: undefined, // 清空search是人员选择的默认值
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        verify: { required: false },
        tooltip: '',
        placeholder: cpType === FORM_COMPONENT_TYPES.AUTO_CODE ? '请输入' : undefined
      };
    }

    let cpId = componentSchemasKeys.find((ele) => {
      return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(searchItem.value);
    });

    if (cpId) {
      // 表单配置
      const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
      componentConfig = {
        ...currentComponentSchemas.config,
        label: {
          display: first ? false : true,
          text: searchItem.label
        },
        layout: first ? 'vertical' : 'horizontal',
        labelColSpan,
        dataField,
        defaultValueConfig: { type: DEFAULT_VALUE_TYPES.CUSTOM, customValue: null },
        defaultDeptValue: undefined, // 清空search是部门选择的默认值
        defaultUserValue: undefined, // 清空search是人员选择的默认值
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        verify: { required: false },
        tooltip: '',
        placeholder: currentComponentSchemas.config?.placeholder
      };
    } else {
      cpId = `${cpType}-${uuidv4()}`;
    }

    switch (cpType) {
      case FORM_COMPONENT_TYPES.INPUT_TEXT:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
        return (
          <FormComp.XInputTextArea
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            minRows={1}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_EMAIL:
        return (
          <FormComp.XInputEmail
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_PHONE:
        return (
          <FormComp.XInputPhone
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_NUMBER:
        return (
          <FormComp.XInputNumber
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return (
          <FormComp.XDatePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
        return (
          <FormComp.XDateRangePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.TIME_PICKER:
        return (
          <FormComp.XTimePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return (
          <FormComp.XDateTimePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.SWITCH:
        return (
          <FormComp.XSwitch cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.RADIO:
        return (
          <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.CHECKBOX:
        return (
          <FormComp.XCheckbox cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return (
          <FormComp.XSelectOne cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return (
          <FormComp.XSelectMutiple
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.USER_SELECT:
        return (
          <FormComp.XUserSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DEPT_SELECT:
        return (
          <FormComp.XDeptSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.RELATED_FORM:
        return (
          <FormComp.XRelatedForm
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.STATIC_TEXT:
        return (
          <FormComp.XStaticText
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DATA_SELECT:
        return (
          <FormComp.XDataSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.USER_SELECT:
        return (
          <FormComp.XUserSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DEPT_SELECT:
        return (
          <FormComp.XDeptSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );

      default:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
    }
  };

  // 搜索
  const handleSearch = () => {
    queryData.value = form.getFieldsValue();
    setShowDropdown(false);
    if (onSearch) {
      onSearch();
    }
  };

  // 重置
  const handleReset = () => {
    form?.resetFields();
    queryData.value = {};
    setShowDropdown(false);
    if (onSearch) {
      onSearch();
    }
  };

  return (
    <div className="table-search">
      {searchItems?.length && <div>{renderSearchBar(searchItems[0])}</div>}
      <Dropdown
        showDropdown={showDropdown}
        onCancel={() => setShowDropdown(false)}
        className="search-dropdown"
        unmountOnExit={false}
        getScrollContainer={() => document.getElementById('ob-loadmore-dropdown-scroll-search')}
      >
        <div id="ob-loadmore-dropdown-scroll-search">
          {searchItems?.length &&
            searchItems.length > 1 &&
            searchItems.slice(1).map((searchItem, index) => <div key={index}>{renderSearchItem(searchItem)}</div>)}
          <div className="searchItem searchActions">
            <Button
              type="ghost"
              color={colorConfig}
              bgColor={ghostBgColor}
              borderColor={colorConfig}
              onClick={handleReset}
              style={{ flex: 1, marginRight: '0.12rem' }}
            >
              重置
            </Button>
            <Button
              type="primary"
              bgColor={colorConfig}
              borderColor={colorConfig}
              onClick={handleSearch}
              style={{ flex: 1, marginLeft: '0.12rem' }}
            >
              确定
            </Button>
          </div>
        </div>
      </Dropdown>
    </div>
  );
});

export default TableSearch;

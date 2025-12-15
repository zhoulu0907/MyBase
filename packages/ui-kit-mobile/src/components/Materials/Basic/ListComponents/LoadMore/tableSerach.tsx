import { Button, Form, SearchBar, Dropdown, type FormInstance } from '@arco-design/mobile-react';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useState } from 'react';
import filterIcon from '@/assets/images/filter.svg';

import {
  useFormEditorSignal,
  FORM_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
} from '@onebase/ui-kit';
import { FormComp } from '../../FormComponents';
import './index.css';

interface TableSearchConfig {
  searchItems?: any[];
  labelColSpan?: number;
  runtime: boolean;
  form?: FormInstance;
  onSearch?: () => void;
  onReset?: () => void;
}


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

let formDataLocal = {}

const TableSearch = memo((props: TableSearchConfig) => {
  useSignals();

  const { searchItems, labelColSpan, runtime, onSearch, onReset, form } = props;
  const count = searchItems?.length || 0;
  const remainder = count % 4;
  const placeholderCount = remainder === 0 ? 3 : 4 - remainder - 1;
  const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
  const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});
  const [showDropdown, setShowDropdown] = useState(false);
  const [firstItemValue, setFirstItemValue] = useState<any>('');

  const renderSearchItem = (item: any) => {
    let cpType = null;
    const config = {
      id: '',
      label: item.label
    };
    if (componentSchemasKeys.length) {
      // 当前组件配置的key
      const cpId = componentSchemasKeys.find((ele) => {
        // return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(item.value);
        return fromPageComponentSchemas.value[ele]?.config?.cpName === item.label; // TODO: update from pc table
      });

      if (cpId) {
        const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
        // 组件类型
        cpType = components.value?.find((ele) => ele.id === cpId)?.type;

        //TODO: zhoumingji, 后续增加在搜索框的配置
        const placeholderOverride = cpType === FORM_COMPONENT_TYPES.AUTO_CODE ? '请输入' : undefined;

        const componentConfig = {
          ...currentComponentSchemas.config,
          layout: 'vertical',
          labelColSpan,
          defaultValue: undefined,
          status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
          verify: { required: false },
          tooltip: '',
          placeholder: placeholderOverride ?? currentComponentSchemas.config?.placeholder
        };
        config.id = componentConfig.dataField.length ? componentConfig.dataField[componentConfig.dataField.length - 1] : ''

        const detailMode = false;
        switch (cpType) {
          case FORM_COMPONENT_TYPES.INPUT_TEXT:
            return {
              content: <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />,
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
            return {
              content: (
                <FormComp.XInputTextArea
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.INPUT_EMAIL:
            return {
              content: (
                <FormComp.XInputEmail
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.INPUT_PHONE:
            return {
              content: (
                <FormComp.XInputPhone
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.INPUT_NUMBER:

            return {
              content: (
                <FormComp.XInputNumber
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.DATE_PICKER:
            return {
              content: (
                <FormComp.XDatePicker
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
            return {
              content: (
                <FormComp.XDateRangePicker
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.TIME_PICKER:
            return {
              content: (
                <FormComp.XTimePicker
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
            return {
              content: (
                <FormComp.XDateTimePicker
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.SWITCH:
            return {
              content: (
                <FormComp.XSwitch
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.RADIO:
            return {
              content: (
                <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.CHECKBOX:
            return {
              content: (
                <FormComp.XCheckbox
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.SELECT_ONE:
            return {
              content: (
                <FormComp.XSelectOne
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
            return {
              content: (
                <FormComp.XSelectMutiple
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                  form={form}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.USER_SELECT:
            return {
              content: (
                <FormComp.XUserSelect
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.DEPT_SELECT:
            return {
              content: (
                <FormComp.XDeptSelect
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.RELATED_FORM:
            return {
              content: (
                <FormComp.XRelatedForm
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.STATIC_TEXT:
            return {
              content: (
                <FormComp.XStaticText
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.CAROUSEL_FORM:
            return {
              content: (
                <FormComp.XCarouselForm
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }
          case FORM_COMPONENT_TYPES.DATA_SELECT:
            return {
              content: (
                <FormComp.XDataSelect
                  cpName={cpId}
                  id={cpId}
                  {...componentConfig}
                  runtime={runtime}
                  detailMode={detailMode}
                />
              ),
              config,
              cpType
            }

          default:
            return {
              content: <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />,
              config,
              cpType
            }
        }
      }
    }
    cpType = FORM_COMPONENT_TYPES.INPUT_TEXT;
    config.id = item.value;
    const componentConfig = {
      label: {
        display: item.label,
        text: item.label
      },
      placeholder: `请输入${item.label}`
    };
    return {
      content: <FormComp.XInputText cpName={item.label} id={item.value} {...componentConfig} runtime={runtime} />,
      config,
      cpType
    }
  };

  const getSearchItemsContent = () => {
    let firstItem: any = null;
    const otherItems = searchItems?.map((item, idx) => {
      const itemContent = renderSearchItem(item)
      if (itemContent.cpType === FORM_COMPONENT_TYPES.INPUT_TEXT && !firstItem) {
        firstItem = itemContent;
        return null;
      }
      return (
        <div key={idx} className="searchItem">
          {itemContent.content}
        </div>
      )
    })
    return {
      firstItem,
      otherItems
    }
  }

  const inputSearchBar = (e: any) => {
    setFirstItemValue(e.target.value.toString());
    form.setFieldValue(firstItem?.config.id, e.target.value);
  }

  const blurSearchBar = (e: any) => {
    setFirstItemValue(e.target.value);
    form.setFieldValue(firstItem?.config.id, e.target.value);
    onSearch?.()
  }

  const resetLocal = () => {
    setFirstItemValue('');
    form.setFieldValue(firstItem?.config.id, '');
    onReset?.()
    changeDropdown(false);
  }

  
  const searchLocal = () => {
    onSearch?.()
    changeDropdown(false);
  }

  const changeDropdown = (val: boolean) => {
    if (!val) {
      formDataLocal = form.getFieldsValue();
    }
    setShowDropdown(val);
  }

  const filterClick = () => {
    const val = !showDropdown;
    changeDropdown(val);
    if (val) {
      setTimeout(() => {
        form.setFieldsValue(formDataLocal);
      }, 10);
    }
  }

  const {
    firstItem,
    otherItems
  } = getSearchItemsContent();

  return <>
    {firstItem ? (
        <SearchBar
          actionButton={null}
          value={firstItemValue}
          onInput={inputSearchBar}
          onBlur={blurSearchBar}
          clearable={false}
          placeholder={`请输入${firstItem?.config.label}`}
        />
    ) : (
      <div className="filter-title">筛选过滤</div>
    )}
    <img className="filter-icon" src={filterIcon} alt="" onClick={filterClick} />
    <Dropdown
      showDropdown={showDropdown}
      onCancel={() => changeDropdown(false)}
      clickOtherToClose={false}
      getScrollContainer={() => document.getElementById('ob-loadmore-dropdown-scroll-search')}
    >
      <div id="ob-loadmore-dropdown-scroll-search">
        {otherItems}
        {(onSearch || onReset) && (
          <>
            {Array.from({ length: placeholderCount }).map((_, i) => (
              <div key={`placeholder-${i}`} className="searchItem placeholder" />
            ))}
            <div className={`searchItem searchActions ${placeholderCount === 3 ? 'searchActions-alone' : ''}`}>
              <Button
                type="ghost"
                color={colorConfig}
                bgColor={ghostBgColor}
                borderColor={colorConfig}
                onClick={resetLocal}
                style={{ flex: 1, marginRight: '0.12rem' }}
              >
                重置
              </Button>
              <Button
                type="primary"
                bgColor={colorConfig}
                borderColor={colorConfig}
                onClick={searchLocal}
                style={{ flex: 1, marginLeft: '0.12rem' }}
              >
                确定
              </Button>
            </div>
          </>
        )}
      </div>
    </Dropdown>
  </>
});

export default TableSearch;

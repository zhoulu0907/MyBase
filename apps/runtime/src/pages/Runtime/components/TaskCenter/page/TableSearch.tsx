import { useState, useCallback, useEffect, type FC } from 'react';
import { Input, Dropdown, Menu, Button, InputTag, Select, DatePicker, Divider } from '@arco-design/web-react';
import { FLOWSTATUS_TYPE, FlowStatusMap } from '@onebase/app';
import { getPageSetList, getListNodes } from '@onebase/app/src/services/app_runtime';
import { useParams } from 'react-router-dom';

import tbSort from '@/assets/images/task_center/tb-sort.svg';
import tbBatch from '@/assets/images/task_center/tb-batch.svg';
import tbFilter from '@/assets/images/task_center/tb-filter.svg';
import { IconCheck } from '@arco-design/web-react/icon';
import '../style/tcPage.less';
export interface FilterParams {
  flowStatus?: FLOWSTATUS_TYPE;
  businessId?: string;
  nodeCode?: string;
  dateRange?: [Date, Date];
  keyword?: string;
  sortType?:string
}

export interface OptionItem {
  id: string;
  name: string;
}
const pageSetFormType = '2';

const selectType = {
  EQUAL: 'equal',
  CONTAIN: 'contain'
};
const sortTypeEnum = {
  DESC: 'desc',
  ASC: 'asc'
};

const Option = Select.Option;
const { RangePicker } = DatePicker;
const flowStatusOptions = Object.values(FLOWSTATUS_TYPE).map((status) => ({
  value: status,
  label: FlowStatusMap[status]
}));
const TableSearch: FC<any> = ({
  uiConfig = { hasInput: true, hasFilter: true, hasSort: true, hasBatch: true },
  batchEvent,
  onFilterChange,
  onReset
}) => {
  const { appId } = useParams<{ appId?: string }>();
  const [sortCheck, setSortCheck] = useState<string>('');
  const [filters, setFilters] = useState<FilterParams>({});
  const [formOptions, setFormOptions] = useState<OptionItem[]>([]);
  const [nodeOptions, setNodeOptions] = useState<OptionItem[]>([]);
  const [loadingSecond, setLoadingSecond] = useState(false);
  const [loadingThird, setLoadingThird] = useState(false);
  const [operator, setOperator] = useState<string>(selectType.EQUAL);
  const handleSortItem=(key: string)=> {
    setSortCheck(key);
    handleFilterChange('sortType', key)
     const newFilters = {
       ...filters,
       sortType: key
     };
     const newParams = parseParams(newFilters);
     onFilterChange(newParams);
  }


  useEffect(() => {
    if (filters.businessId) {
      loadNodeOptions(filters.businessId);
    } else {
      setNodeOptions([]);
      setFilters((prev) => ({ ...prev, nodeCode: undefined }));
    }
  }, [filters.businessId]);
  const loadFormOptions = useCallback(async () => {
    setLoadingSecond(true);
    try {
      const data = await getPageSetList({ applicationId: appId, pageSetType: pageSetFormType });
      setFormOptions(data?.pageSets || []);
    } catch (error) {
      setFormOptions([]);
    } finally {
      setLoadingSecond(false);
    }
  }, []);
  const loadNodeOptions = useCallback(async (formValue: string) => {
    setLoadingThird(true);
    try {
      const data = await getListNodes({ businessId: formValue });
      setNodeOptions(data);
    } catch (error) {
      setNodeOptions([]);
    } finally {
      setLoadingThird(false);
    }
  }, []);
  const handleOperatorChange = (newOperator: string) => {
    setOperator(newOperator);
    handleFilterChange('flowStatus', '');
  };
  const handleFilterChange = useCallback(
    (key: keyof FilterParams, value: any) => {
      const newFilters = { ...filters };

      if (key === 'businessId') {
        newFilters.businessId = value;
        newFilters.nodeCode = undefined;
      } else if (key === 'dateRange') {
        newFilters.dateRange = value;
      } else {
        newFilters[key] = value;
      }
      setFilters(newFilters);
    },
    [filters]
  );
  const handleReset = useCallback(() => {
    const resetFilters: FilterParams = {
      flowStatus: undefined,
      businessId: undefined,
      nodeCode: undefined,
      dateRange: undefined,
      keyword:undefined,
      sortType:undefined
    };
    setFilters(resetFilters);
    setNodeOptions([]);
    onReset()    
  }, []);

  const parseParams = (params: any) => {
    const result: any = {};
    if (params.businessId) result.businessId = params.businessId;
    if (params.nodeCode) result.nodeCode = params.nodeCode;
    if(params.keyword) result.keyword = params.keyword;
    if (params.sortType) result.sortType = params.sortType;
    if (params.flowStatus) {
      result.flowStatus = Array.isArray(params.flowStatus) ? params.flowStatus.join(',') : params.flowStatus;
    }

    if (params.dateRange) {
      result.submitTimeStart = params.dateRange[0];
      result.submitTimeEnd = params.dateRange[1];
    }
    return result;
  };
  const handleSearch = () => {
    console.log(filters,'=============')
    const newParams = parseParams(filters);
    onFilterChange(newParams);
  };

  useEffect(() => {
    loadFormOptions();
  }, []);
  return (
    <div className="title-rgt-tb-search">
      {uiConfig?.hasInput && (
        <Input.Search
          allowClear
          placeholder="输入内容查询"
          style={{ width: 230, height: 32 }}
          value={filters.keyword}
          onChange={(value: any) => handleFilterChange('keyword', value)}
          onPressEnter={() => handleSearch()}
        />
      )}
      {uiConfig?.hasFilter && (
        <Dropdown
          position="br"
          droplist={
            <section className="tb-filter-box arco-dropdown-menu">
              <div style={{ padding: '16px' }}>
                <div className="filter-line">
                  <InputTag
                    className="fisrt-input-tag"
                    style={{ width: 150 }}
                    addBefore={<IconCheck />}
                    allowClear
                    readOnly
                    inputValue="流程状态"
                  />
                  <Select
                    className="mid-select"
                    placeholder="请选择"
                    style={{ width: 150 }}
                    value={operator}
                    onChange={handleOperatorChange}
                    allowClear={false} // 不允许清空，必须有值
                  >
                    <Option value={selectType.EQUAL}>等于</Option>
                    <Option value={selectType.CONTAIN}>包含</Option>
                  </Select>
                  {operator === selectType.EQUAL ? (
                    <Select
                      className="end-select"
                      placeholder="请选择流程状态"
                      style={{ flex: 1 }}
                      value={filters.flowStatus}
                      onChange={(value: any) => handleFilterChange('flowStatus', value)}
                      allowClear
                    >
                      {flowStatusOptions.map((option) => (
                        <Option key={option.value} value={option.value}>
                          {option.label}
                        </Option>
                      ))}
                    </Select>
                  ) : (
                    <Select
                      className="end-select"
                      placeholder="请选择流程状态"
                      style={{ flex: 1 }}
                      mode="multiple"
                      value={filters.flowStatus}
                      onChange={(value: any) => handleFilterChange('flowStatus', value)}
                      allowClear
                      maxTagCount="responsive"
                    >
                      {flowStatusOptions.map((option) => (
                        <Option key={option.value} value={option.value}>
                          {option.label}
                        </Option>
                      ))}
                    </Select>
                  )}
                </div>
                <div className="filter-line">
                  <InputTag
                    className="fisrt-input-tag"
                    style={{ width: 150 }}
                    addBefore={<IconCheck />}
                    allowClear
                    readOnly
                    inputValue="流程表单"
                  />
                  <div className="min-text">等于</div>
                  <Select
                    placeholder="请选择流程表单"
                    className="end-select"
                    value={filters.businessId}
                    onChange={(value: any) => handleFilterChange('businessId', value)}
                    style={{ flex: 1 }}
                    loading={loadingSecond}
                    allowClear
                  >
                    {formOptions.map((option: any) => (
                      <Option key={option.id} value={option.id}>
                        {option.pageSetName}
                      </Option>
                    ))}
                  </Select>
                </div>
                <div className="filter-line">
                  <InputTag
                    className="fisrt-input-tag"
                    style={{ width: 150 }}
                    addBefore={<IconCheck />}
                    allowClear
                    readOnly
                    inputValue="当前节点"
                  />
                  <div className="min-text">包含</div>

                  <Select
                    className="end-select"
                    placeholder={filters.businessId ? '请选择当前节点' : '请先选择表单'}
                    value={filters.nodeCode}
                    onChange={(value: any) => handleFilterChange('nodeCode', value)}
                    loading={loadingThird}
                    disabled={!filters.businessId}
                    style={{ flex: 1 }}
                    allowClear
                  >
                    {nodeOptions.map((option: any) => (
                      <Option key={option.nodeCode} value={option.nodeCode}>
                        {option.nodeName}
                      </Option>
                    ))}
                  </Select>
                </div>
                <Divider />
                <div className="filter-line">
                  <InputTag
                    className="fisrt-input-tag"
                    style={{ width: 150 }}
                    addBefore={<IconCheck />}
                    allowClear
                    readOnly
                    inputValue="发起时间"
                  />
                  <div className="min-text">选择范围</div>

                  <RangePicker
                    mode="date"
                    value={filters.dateRange}
                    onChange={(value: any) => handleFilterChange('dateRange', value)}
                    style={{ minWidth: 380, flex: 1 }}
                    showTime={true}
                  />
                </div>
              </div>
              <div className="filter-footer">
                <Button type="primary" status="success" onClick={() => handleSearch()}>
                  筛选
                </Button>
                <Button type="text" className="clear-filter-btn" onClick={() => handleReset()}>
                  <img src={tbFilter} alt="" />
                  清空
                </Button>
              </div>
            </section>
          }
        >
          <p>
            <img src={tbFilter} alt="" />
            筛选
          </p>
        </Dropdown>
      )}
      {uiConfig?.hasSort && (
        <Dropdown
          position="bottom"
          droplist={
            <Menu onClickMenuItem={(key: any) => handleSortItem(key)}>
              <Menu.Item
                className={sortCheck === sortTypeEnum.DESC ? 'item-actived' : 'item-no-check'}
                key={sortTypeEnum.DESC}
              >
                最新发起的
                <IconCheck className="svg" />
              </Menu.Item>
              <Menu.Item
                className={sortCheck === sortTypeEnum.ASC ? 'item-actived' : 'item-no-check'}
                key={sortTypeEnum.ASC}
              >
                最早发起的
                <IconCheck className="svg" />
              </Menu.Item>
            </Menu>
          }
        >
          <p>
            <img src={tbSort} alt="" />
            排序
          </p>
        </Dropdown>
      )}
      {uiConfig?.hasBatch && (
        <p
          onClick={() => {
            typeof batchEvent === 'function' && batchEvent(true);
          }}
        >
          <img src={tbBatch} alt="" />
          批量审批
        </p>
      )}
    </div>
  );
};

export default TableSearch;

import {
  Dialog,
  Sticky,
  Ellipsis,
  SearchBar,
  Toast,
  Button,
  LoadMore,
  Dropdown,
} from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import { memo, useEffect, useState } from 'react';
import CustomNav from '@/pages/components/Nav';
import filterIcon from '@/assets/images/filter.svg';
import {
  menuSignal,
} from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import './index.css';

type XTableSelectProps = {
  showSelect: boolean;
  selectedDataId: string | null;
  setSelectData: (value: any) => void;
};

const pageSize = 10;
const TaskList = memo(
  (
    props: {
      title: string;
      dataFetch: (params: any) => Promise<any>;
      columns: any[];
      runtime?: boolean;
      showFromPageData?: Function;
      showAddBtn?: boolean;
      refresh?: number;
      xTableSelectProps?: XTableSelectProps;
    }
  ) => {
    useSignals();

    const {
      title,
      columns,
      dataFetch,
    } = props;

    // 实际查询用的参数
    let queryData: object = {};

    const [tableData, setTableData] = useState<any[]>([]);
    const [tableTotal, setTableTotal] = useState<number>(0);
    const [tablePageNo, setTablePageNo] = useState<number>(1);
    const [loading, setLoading] = useState<boolean>(false);
    const [showDropdown, setShowDropdown] = useState(false);

    const onReachBottom = (cb: Function) => {
      console.log('onReachBottom', tablePageNo);
      if (!tableData.length) return;
      setTablePageNo(prevPageNo => prevPageNo + 1);
      cb('prepare');
    }

    useEffect(() => {
      handlePage();
    }, [tablePageNo]);

    // 查询
    const handleSearch = () => {
      queryData = form.getFieldsValue();
      setTablePageNo(1);
      handlePage();
    };

    // 重置
    const handleReset = () => {
      form.resetFields();
      queryData = {};
      setTablePageNo(1);
      handlePage();
    };

    const handlePage = async () => {
      if ((tablePageNo - 1) * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER)) {
        return;
      }

      setLoading(true);
      const req: any = {
        pageNo: tablePageNo,
        pageSize: pageSize,
        filters: queryData
      };
      const res = await dataFetch(req);

      const { list, total } = res;

      const newTableData = list || []
      setLoading(false);
      setTableData(req.pageNo === 1 ? newTableData : [...tableData, ...newTableData]);
      setTableTotal(total);
    };

    // 行点击事件
    const handleRowClick = (record: any) => {
      
    };

    const [form] = useForm();

    const [value, setValue] = useState([]);

    const filterDropdown = () => {
      return (
        <Dropdown
          showDropdown={showDropdown}
          onCancel={() => setShowDropdown(false)}
        >
          <div style={{ padding: '0.32rem' }}>
            <div className="demo-dropdown-option-desc">Group 1</div>
            <Dropdown.Options
              useColumn={3}
              multiple={true}
              selectedValue={value[0] || []}
              onOptionClick={() => { console.info('click 1'); }}
              onOptionChange={(val, item) => {
                console.info('change 1', val, item);
                setValue((oldValue) => {
                  oldValue[0] = val;
                  return [...oldValue];
                });
              }}
              options={[
                {
                  label: 'Option 1',
                  value: 0,
                  disabled: false,
                },
                {
                  label: 'Option 2',
                  value: 1,
                },
                {
                  label: 'Option 3',
                  value: 2,
                  disabled: true,
                },
                {
                  label: 'Option 4',
                  value: 3,
                }
              ]}
            ></Dropdown.Options>
            <div className="demo-dropdown-option-desc">Group 2</div>
            <Dropdown.Options
              useColumn={3}
              multiple={true}
              selectedValue={value[1] || []}
              onOptionClick={() => { console.info('click 2'); }}
              onOptionChange={(val, item) => {
                console.info('change 2', val, item);
                setValue((oldValue) => {
                  oldValue[1] = val;
                  return [...oldValue];
                });
              }}
              options={[
                {
                  label: 'Option 5',
                  value: 0,
                  disabled: false,
                },
                {
                  label: 'Option 6',
                  value: 1,
                }]}
            ></Dropdown.Options>
            <div style={{ display: "flex", justifyContent: "space-between" }}>
              <Button type='ghost' style={{ marginRight: "0.16rem", flex: 1 }}>重置</Button>
              <Button style={{ flex: 1 }}>确定</Button>
            </div>
          </div>
        </Dropdown>
      )
    }

    return (
      <div className="task-list-wrapper">
            <CustomNav
              title={title}
              style={{ background: '#fff' }}
            />
        <Sticky topOffset={0.88 * window.ROOT_FONT_SIZE} className="list-search-header">
            <SearchBar actionButton={null} placeholder={`请输入查询内容`} />
            <img className="filter-icon" src={filterIcon} alt="" onClick={() => setShowDropdown(true)} />
            {filterDropdown()}
        </Sticky>
        <div className="list-body-wrapper">
          {!tableData.length ? <div className="no-data">暂无数据</div> : null}
          {
            tableData.map((item) => (
              <div key={item.key} className="list-body-item-wrapper" onClick={() => handleRowClick(item)}>
                {columns?.map((col, index) => {
                  return <div className="list-body-item-element" key={col.dataIndex}>
                    <Ellipsis className="list-body-item-title" text={col.title} />
                    {index ? '' : '：'}
                    <Ellipsis className="list-body-item-content" text={item[col.dataIndex]} />
                  </div>
                })}
              </div>
            ))
          }
          {
            loading || tablePageNo * pageSize >= (tableTotal || Number.MAX_SAFE_INTEGER) ? null : <LoadMore
              getData={onReachBottom}
              getDataAtFirst={false}
              threshold={200}
              blockWhenLoading={false}
              throttle={300}
            />
          }
        </div>
      </div>
    );
  }
);

export default TaskList;

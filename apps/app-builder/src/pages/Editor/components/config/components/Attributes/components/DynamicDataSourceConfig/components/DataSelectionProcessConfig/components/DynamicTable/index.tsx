import { Button, Form, Input, Table } from '@arco-design/web-react';
import { memo, useState } from 'react';

interface DynamicTableProps {
  label?: string;
  columns: any[];
  dataSource: any[];
  searchItems?: Array<{ label: string; key: string }>;
  pageSize?: number;
  showTotal?: boolean;
  showHeader?: boolean;
  border?: boolean;
  borderCell?: boolean;
  stripe?: boolean;
  hover?: boolean;
  pagePosition?: 'br' | 'bl' | 'tr' | 'tl' | 'topCenter' | 'bottomCenter' | undefined;
  labelColSpan?: number;
  onSearch?: (values: Record<string, any>) => void;
  onReset?: () => void;
  onCreate?: () => void;
}

const DynamicTable = memo((props: DynamicTableProps) => {
  const {
    label,
    columns,
    dataSource,
    searchItems,
    pageSize = 10,
    showTotal = true,
    showHeader = true,
    border = true,
    borderCell = false,
    stripe = true,
    hover = true,
    pagePosition = 'br',
    labelColSpan = 100,
    onSearch,
    onReset,
    onCreate
  } = props;

  const [tablePageNo, setTablePageNo] = useState<number>(1);

  return (
    <div>
      <div className="tableHeader">
        <div className="searchGroup">
          {searchItems?.map((item, idx) => (
            <Form.Item
              key={idx}
              className="searchItem"
              label={
                <div
                  style={{
                    textAlign: 'right',
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis'
                  }}
                >
                  {`${item.label}`}
                </div>
              }
              style={{
                minWidth: '350px',
                maxWidth: '400px',
                marginBottom: 0
              }}
              layout={'horizontal'}
              labelCol={{
                style: { width: labelColSpan, flex: 'unset' }
              }}
              wrapperCol={{ style: { flex: 1 } }}
            >
              <Input placeholder={`请输入${item.label}`} />
            </Form.Item>
          ))}
        </div>

        <div className="tableHeaderButton">
          <Button type="primary" onClick={() => onSearch?.({})}>
            查询
          </Button>
          <Button type="primary" onClick={onReset}>
            重置
          </Button>
          <Button type="primary" onClick={onCreate}>
            新增
          </Button>
        </div>
      </div>
      <div>
        <Form.Item
          label={label}
          layout={'vertical'}
          style={{
            width: '100%'
          }}
        >
          <Table
            scroll={{
              x: 'max-content'
            }}
            border={border}
            borderCell={borderCell}
            showHeader={showHeader}
            stripe={stripe}
            hover={hover}
            columns={columns}
            data={dataSource}
            pagePosition={pagePosition}
            pagination={{
              pageSize,
              showTotal,
              current: tablePageNo,
              total: dataSource.length,
              onChange: (pageNo: number) => {
                setTablePageNo(pageNo);
              }
            }}
          />
        </Form.Item>
      </div>
    </div>
  );
});

export default DynamicTable;

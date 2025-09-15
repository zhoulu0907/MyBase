import {
  Button,
  Checkbox,
  Drawer,
  Form,
  Grid,
  Input,
  Popover,
  Select,
  Space,
  Switch,
  Tooltip
} from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { IconQuestionCircleFill } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import { ListComp } from '@onebase/ui-kit';

import styles from '../../index.module.less';
import SelectOptionDrag from '../DropdownRender';
import type { DynamicSelectDataSourceConfigProps } from '../..';
import DropdownRender from '../DropdownRender';

interface DataSelectionProcessConfigProps extends DynamicSelectDataSourceConfigProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}

const FormItem = Form.Item;
const Option = Select.Option;

const SUB_ATTR_KEY = {
  DEFAULTVALUE: 'defaultValue',
  SELECTDATAFIELDS: 'selectDataFields',
  FILTERDATA: 'filterData',
  SORTDATARULE: 'sortDataRule',
  OPERATIONAUTH: 'operationAuth',
  FASTFILTER: 'fastFilter',
  DYNAMICTABLECONFIG: 'dynamicTableConfig',
  COLUMNS: 'columns'
};

//mockup
const defaultOptions = [
  {
    value: 1,
    label: '提交时间'
  },
  {
    value: 2,
    label: '更新时间'
  }
];

const sortOptions = [
  { value: 1, label: '升序' },
  { value: 2, label: '降序' }
];

const initialDisplayFieldOptions = [
  { label: '标题', value: 'title', id: 1 },
  { label: '单行文本', value: 'singleText', id: 2 },
  { label: '提交时间', value: 'submitTime', id: 3 },
  { label: '多行文本', value: 'multiText', id: 4 },
  { label: '单选按钮组', value: 'radioGroup', id: 5 },
  { label: '提交人', value: 'submitter', id: 6 },
  { label: '更新时间', value: 'updateTime', id: 7 },
  {
    label: '子表单',
    value: 'subTable',
    id: 8,
    children: [
      { label: '子表单.成员单选', value: 'subTable.member', id: 81 },
      { label: '子表单.图片', value: 'subTable.image', id: 82 }
    ]
  }
];

const DataSelectionProcessConfig: React.FC<DataSelectionProcessConfigProps> = ({
  visible,
  setVisible,
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const tableConfig = configs[SUB_ATTR_KEY.DYNAMICTABLECONFIG];

  const [sortFieldOptions, setSortFieldOptions] = useState<any[]>(defaultOptions);
  const [sortOption, setSortOption] = useState<any[]>(sortOptions);

  const [displayFieldOptions, setDisplayFieldOptions] = useState(initialDisplayFieldOptions);
  const [selected, setSelected] = useState([
    'title',
    'singleText',
    'submitTime',
    'multiText',
    'radioGroup',
    'submitter'
  ]);
  const [tableHeader, setTableHeader] = useState<any[]>(tableConfig[SUB_ATTR_KEY.COLUMNS]); // table header
  const [tableDataSource, setTableDataSource] = useState([]); // table data source

  useEffect(() => {
    handleOptionsChange();
  }, [displayFieldOptions, selected]);

  const handleOptionsChange = () => {
    const header = displayFieldOptions
      .map((option: any) => {
        if (selected.includes(option.value)) {
          return {
            title: option.label,
            dataIndex: option.value
          };
        }
      })
      .filter(Boolean);
    setTableHeader(header);
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
        onCancel={() => {
          setVisible(false);
        }}
      >
        <div className={styles.container}>
          <div className={styles.leftColumn}>
            <ListComp.XTable cpName={id} id={id} {...tableConfig} columns={tableHeader} />
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
              <FormItem label="选择数据时的显示字段">
                <Select
                  mode="multiple"
                  value={selected}
                  onChange={setSelected}
                  placeholder="设置显示字段"
                  getPopupContainer={(node) => node.parentNode as HTMLElement}
                  renderTag={({}, index, valueList) => {
                    const tagCount = valueList.length;
                    if (tagCount > 0) {
                      return index === 0 ? (
                        <span className={styles.fieldDisplaySpan}>{`显示 ${tagCount} 个字段`}</span>
                      ) : null;
                    }
                  }}
                  dropdownRender={() => (
                    <div className={styles.dropdownRender}>
                      <DropdownRender
                        selected={selected}
                        setSelected={setSelected}
                        displayFieldOptions={displayFieldOptions}
                        handleOptionsChange={handleOptionsChange}
                        setDisplayFieldOptions={setDisplayFieldOptions}
                      />
                    </div>
                  )}
                />
              </FormItem>
              <FormItem label="数据过滤">
                <Button type="secondary" long>
                  添加过滤条件
                </Button>
              </FormItem>
              <FormItem label="数据排序规则">
                <Grid.Row gutter={8}>
                  <Grid.Col span={18}>
                    <Select placeholder="请选择" getPopupContainer={(node) => node.parentNode as HTMLElement}>
                      {sortFieldOptions.map((option) => (
                        <Option key={option.value} value={option.value}>
                          {option.label}
                        </Option>
                      ))}
                    </Select>
                  </Grid.Col>
                  <Grid.Col span={6}>
                    <Select placeholder="请选择" getPopupContainer={(node) => node.parentNode as HTMLElement}>
                      {sortOption.map((option) => (
                        <Option key={option.value} value={option.value}>
                          {option.label}
                        </Option>
                      ))}
                    </Select>
                  </Grid.Col>
                </Grid.Row>
              </FormItem>
              <FormItem label="操作权限">
                <Checkbox defaultChecked>允许新增数据源表数据</Checkbox>
                <Tooltip content="成员需要有数据源表的添加权限，才可新增数据">
                  <IconQuestionCircleFill className={styles.iconQuestionCircleFill} />
                </Tooltip>
              </FormItem>
              <FormItem label="快捷筛选" layout="horizontal" className={styles.switchLabel}>
                <Switch className={styles.switchButton} />
              </FormItem>
              <div className={styles.tip}>开启后可添加筛选字段，表格左侧会显示字段值供成员快速选择。</div>
            </Form>
          </div>
        </div>
      </Drawer>
    </>
  );
};

export default DataSelectionProcessConfig;

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
import React, { useState } from 'react';

import styles from './index.module.less';
import { IconDragDotVertical, IconQuestionCircleFill, IconEdit } from '@arco-design/web-react/icon';
import { ReactSortable } from 'react-sortablejs';
import type { DynamicSelectDataSourceConfigProps } from '../..';
import DynamicTable from './components/DynamicTable';

interface DataSelectionProcessConfigProps extends DynamicSelectDataSourceConfigProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}

const FormItem = Form.Item;
const Option = Select.Option;

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
  { label: '更新时间', value: 'updateTime', id: 7 }
];

const columns = [
  { title: '姓名', dataIndex: 'name' },
  { title: '年龄', dataIndex: 'age' },
  { title: '城市', dataIndex: 'city' }
];

const dataSource = [
  { name: '张三', age: 18, city: '北京' },
  { name: '李四', age: 22, city: '上海' }
];

const DataSelectionProcessConfig: React.FC<DataSelectionProcessConfigProps> = ({
  visible,
  setVisible,
  handlePropsChange,
  item,
  configs,
  id
}) => {
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
  const [hovered, setHovered] = useState<string | null>(null);
  const [editIdx, setEditIdx] = useState<number | null>(null);
  const [editLabel, setEditLabel] = useState('');

  // 编辑弹窗内容
  const renderEditPopover = (idx: number) => (
    <div className={styles.popoverContainer}>
      <div className={styles.popoverContent}>
        <Space>
          <span className={styles.contentLabel}>显示名</span>
          <Input value={editLabel} onChange={setEditLabel} />
        </Space>
      </div>
      <Space>
        <Button
          onClick={() => {
            setEditIdx(null);
          }}
        >
          取消
        </Button>
        <Button
          type="primary"
          onClick={() => {
            displayFieldOptions[idx].label = editLabel;
            setEditIdx(null);
          }}
        >
          确定
        </Button>
      </Space>
    </div>
  );

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
            <DynamicTable
              label="用户列表"
              columns={columns}
              dataSource={dataSource}
              searchItems={[{ label: '姓名', key: 'name' }]}
              onCreate={() => alert('新增')}
              onSearch={(values) => alert(JSON.stringify(values))}
              onReset={() => alert('重置')}
            />
          </div>
          <div className={styles.rightColumn}>
            <Form layout="vertical">
              <FormItem label="按钮文字">
                <Input
                  placeholder="请输入按钮文字"
                  value={configs['defaultValue']}
                  onChange={(value) => {
                    handlePropsChange('defaultValue', value);
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
                      <Checkbox
                        checked={selected.length === displayFieldOptions.length}
                        indeterminate={selected.length > 0 && selected.length < displayFieldOptions.length}
                        onChange={(checked) => setSelected(checked ? displayFieldOptions.map((opt) => opt.value) : [])}
                        className={styles.headerCheckbox}
                      >
                        全选
                      </Checkbox>
                      <ReactSortable
                        list={displayFieldOptions}
                        setList={setDisplayFieldOptions}
                        handle=".drag-handle"
                        animation={150}
                      >
                        {displayFieldOptions.map((opt, idx) => (
                          <div
                            key={opt.value}
                            className={styles.displayFieldOptions}
                            style={{
                              background: hovered === opt.value ? '#f2f3f5' : '#fff'
                            }}
                            onMouseEnter={() => setHovered(opt.value)}
                            onMouseLeave={() => setHovered(null)}
                          >
                            <Checkbox
                              checked={selected.includes(opt.value)}
                              onChange={(checked) => {
                                setSelected((prev) =>
                                  checked ? [...prev, opt.value] : prev.filter((v) => v !== opt.value)
                                );
                              }}
                              className={styles.childCheckbox}
                            />
                            <span className={styles.optionSpan}>{opt.label}</span>
                            {hovered === opt.value && (
                              <div className={styles.operationDiv}>
                                <Popover
                                  trigger="click"
                                  position="tr"
                                  popupVisible={editIdx === idx}
                                  onVisibleChange={(visible) => {
                                    if (visible) {
                                      setEditIdx(idx);
                                      setEditLabel(opt.label);
                                    } else {
                                      setEditIdx(null);
                                    }
                                  }}
                                  content={renderEditPopover(idx)}
                                >
                                  <IconEdit className={styles.iconEdit} />
                                </Popover>
                                <IconDragDotVertical
                                  className="drag-handle"
                                  style={{ cursor: 'move', marginLeft: 8 }}
                                />
                              </div>
                            )}
                          </div>
                        ))}
                      </ReactSortable>
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

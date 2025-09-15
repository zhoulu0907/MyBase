import { Button, Checkbox, Dropdown, Form, Input, Select } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { useAppStore } from '@/store/store_app';
import { getPageListByAppId } from '@onebase/app';
import DataSelectionProcessConfig from './components/DataSelectionProcessConfig';
import styles from './index.module.less';
import { ReactSortable } from 'react-sortablejs';
import SelectOptionDrag from './components/DropdownRender';
import DropdownRender from './components/DropdownRender';

const FormItem = Form.Item;
const Option = Select.Option;

const ATTR_KEY = {
  IS_SETTED: 'isSetted',
  DISPLAYFIELDS: 'displayFields',
  FILLFORMFIELD: 'fillFormField'
};

// mock up
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

export interface DynamicSelectDataSourceConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDataSourceConfig: React.FC<DynamicSelectDataSourceConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const { curAppId } = useAppStore();
  const [dataSourceOptions, setDataSourceOptions] = useState<any[]>([]); // 数据源
  const [selectedDataSource, setSelectedDataSource] = useState(configs[item.key] || null); // 选择的数据源

  const [selectDataVisible, setSelectDataVisibleVisible] = useState(false); //数据选择过程 popup
  const [isSetted, setIsSetted] = useState(configs[ATTR_KEY.IS_SETTED]);

  // 设置显示字段
  const [displayFieldOptions, setDisplayFieldOptions] = useState(initialDisplayFieldOptions);
  const [selected, setSelected] = useState([
    'title',
    'singleText',
    'submitTime',
    'multiText',
    'radioGroup',
    'submitter'
  ]);

  useEffect(() => {
    curAppId && getPageList();
  }, [curAppId]);

  useEffect(() => {
    handleOptionsChange();
  }, [displayFieldOptions, selected]);

  const getPageList = async () => {
    const res = await getPageListByAppId({ appId: curAppId });
    console.log('res: ', res);
    setDataSourceOptions(res.pages);
  };

  const toSetting = () => {
    setSelectDataVisibleVisible(true);
    if (!isSetted) {
      setIsSetted(true);
      handlePropsChange(ATTR_KEY.IS_SETTED, true);
    }
  };

  const handleOptionsChange = () => {
    const displayFields = displayFieldOptions
      .map((option: any) => {
        if (selected.includes(option.value)) {
          return {
            label: option.label,
            value: option.value
          };
        }
      })
      .filter(Boolean);
    handlePropsChange(ATTR_KEY.DISPLAYFIELDS, displayFields);
    console.log(displayFields);
  };

  return (
    <>
      <div className={styles.dataSourceContainer}>
        {/* 选择数据源 */}
        <FormItem layout="vertical" labelAlign="left" label={'数据源'} className={styles.formItem}>
          <Select
            placeholder="请选择"
            defaultValue={configs[item.key]}
            getPopupContainer={(node) => node.parentNode as HTMLElement}
            onChange={(value) => {
              setSelectedDataSource(value);
              handlePropsChange(item.key, value);
            }}
          >
            {dataSourceOptions.map((option) => (
              <Option key={option.id} value={option.id}>
                {option.pageName}
              </Option>
            ))}
          </Select>
        </FormItem>

        {/* 数据选择过程 */}
        {selectedDataSource && (
          <div>
            <FormItem layout="vertical" labelAlign="left" label={'数据选择过程'} className={styles.formItem}>
              <Button long onClick={toSetting}>
                {isSetted ? '已设置' : '设置'}
              </Button>
              <DataSelectionProcessConfig
                visible={selectDataVisible}
                setVisible={() => setSelectDataVisibleVisible(false)}
                id={id}
                handlePropsChange={handlePropsChange}
                item={item}
                configs={configs}
              />
            </FormItem>

            {/* 数据选择后 */}
            <FormItem layout="vertical" labelAlign="left" label={'数据选择后'} className={styles.formItem}>
              <FormItem layout="vertical" labelAlign="left" label={'显示在表单中'} className={styles.formItem}>
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
                        hasEditLabel={false}
                        handleOptionsChange={handleOptionsChange}
                        setDisplayFieldOptions={setDisplayFieldOptions}
                      />
                    </div>
                  )}
                />
              </FormItem>
              <FormItem layout="vertical" labelAlign="left" label={'填充到表单字段'} className={styles.noMarginBottom}>
                <Button long>填充规则设置</Button>
              </FormItem>
            </FormItem>
          </div>
        )}
      </div>
    </>
  );
};

export default DynamicDataSourceConfig;

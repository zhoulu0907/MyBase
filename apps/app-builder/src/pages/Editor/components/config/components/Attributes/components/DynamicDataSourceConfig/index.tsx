import { Button, Dropdown, Form, Input, Select } from '@arco-design/web-react';
import React, { useEffect, useState } from 'react';

import { useAppStore } from '@/store/store_app';
import { getPageListByAppId } from '@onebase/app';
import DataSelectionProcessConfig from './components/DataSelectionProcessConfig';
import styles from '../../index.module.less';

const FormItem = Form.Item;
const Option = Select.Option;

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
  const [isSetted, setIsSetted] = useState(false);

  useEffect(() => {
    curAppId && getPageList();
  }, [curAppId]);

  const getPageList = async () => {
    const res = await getPageListByAppId({ appId: curAppId });
    console.log('res: ', res);
    setDataSourceOptions(res.pages);
  };

  const toSetting = () => {
    setSelectDataVisibleVisible(true);
    !isSetted && setIsSetted(true);
  };

  console.log({
    handlePropsChange,
    configs,
    item,
    id
  });

  return (
    <>
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
              <Dropdown>
                <Input readOnly value="设置显示字段" />
              </Dropdown>
            </FormItem>
            <FormItem layout="vertical" labelAlign="left" label={'填充到表单字段'} className={styles.noMarginBottom}>
              <Button long>填充规则设置</Button>
            </FormItem>
          </FormItem>
        </div>
      )}
    </>
  );
};

export default DynamicDataSourceConfig;

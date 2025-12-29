import { Form, Input, ColorPicker, Tooltip, Empty } from '@arco-design/web-react';
import { CONFIG_TYPES, useAppEntityStore, getFieldOptionsConfig } from '@onebase/ui-kit';
import { type DictData } from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

export interface DynamicOptionsProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicOptionsConfig: React.FC<DynamicOptionsProps> = ({ handlePropsChange, item, configs, id }) => {
  const { mainEntity, subEntities } = useAppEntityStore();
  
  const [options, setOptions] = useState<DictData[]>([]);

  useEffect(() => {
    if (configs.dataField?.length) {
      getOptions();
    } else {
      setOptions([]);
    }
  }, [configs.dataField]);

  // 获取当前字段配置 通过配置获取下拉选项
  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(configs.dataField, mainEntity, subEntities);
    setOptions(newOptions);
  };

  return (
    <>
      <Form.Item layout="vertical" labelAlign="left" label={item.name || '自定义配置'} className={styles.formItem}>
        {options.length ? (
          options.map((ele: any, index: number) => (
            <div key={index} className={styles.tableColumnItem}>
              <Tooltip content="如需修改请前往数据建模">
                <div style={{ width: '100%', display: 'flex', alignItems: 'center' }}>
                  <Input size="small" value={ele.label} readOnly className={styles.tableColumnItemInput} />
                  {ele.colorType &&
                    (item.type === CONFIG_TYPES.RADIO_DATA || item.type === CONFIG_TYPES.CHECKBOX_DATA) && (
                      <ColorPicker size="mini" disabled value={ele.colorType} />
                    )}
                </div>
              </Tooltip>
            </div>
          ))
        ) : (
          <Empty />
        )}
      </Form.Item>
    </>
  );
};

export default DynamicOptionsConfig;

registerConfigRenderer(CONFIG_TYPES.SELECT_OPTIONS_INPUT, ({ id, handlePropsChange, item, configs }) => (
  <DynamicOptionsConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.MUTIPLE_SELECT_OPTIONS_INPUT, ({ id, handlePropsChange, item, configs }) => (
  <DynamicOptionsConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.RADIO_DATA, ({ id, handlePropsChange, item, configs }) => (
  <DynamicOptionsConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
registerConfigRenderer(CONFIG_TYPES.CHECKBOX_DATA, ({ id, handlePropsChange, item, configs }) => (
  <DynamicOptionsConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

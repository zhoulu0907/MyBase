import { Checkbox, Form, Input } from '@arco-design/web-react';
import { useEffect, useRef, useState } from 'react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicCardDataConfig = ({ handlePropsChange, item, configs }: Props) => {
  return <Form.Item className={styles.formItem} label="卡片数据配置">
    数据绑定
    显示字段
    卡片标题字段
    搜索项
  </Form.Item>;
};

export default DynamicCardDataConfig;

registerConfigRenderer(CONFIG_TYPES.CARD_DATA, ({ handlePropsChange, item, configs }) => (
  <DynamicCardDataConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

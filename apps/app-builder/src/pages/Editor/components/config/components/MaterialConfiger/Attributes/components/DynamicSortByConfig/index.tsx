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

const DynamicSortByConfig = ({ handlePropsChange, item, configs }: Props) => {
  return <Form.Item className={styles.formItem} label="数据排序规则">ss</Form.Item>;
};

export default DynamicSortByConfig;

registerConfigRenderer(CONFIG_TYPES.DATA_SORT_BY, ({ handlePropsChange, item, configs }) => (
  <DynamicSortByConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

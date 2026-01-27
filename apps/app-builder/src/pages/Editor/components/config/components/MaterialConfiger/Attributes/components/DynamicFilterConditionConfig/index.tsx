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

const DynamicFilterConditionConfig = ({ handlePropsChange, item, configs }: Props) => {
  return <Form.Item className={styles.formItem} label="数据过滤">ss</Form.Item>;
};

export default DynamicFilterConditionConfig;

registerConfigRenderer(CONFIG_TYPES.FIELD_DATA, ({ handlePropsChange, item, configs }) => (
  <DynamicFilterConditionConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

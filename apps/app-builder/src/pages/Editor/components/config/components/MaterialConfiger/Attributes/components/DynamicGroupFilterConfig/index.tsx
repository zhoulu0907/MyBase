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

const DynamicGroupFilterConfig = ({ handlePropsChange, item, configs }: Props) => {
  return <Form.Item className={styles.formItem} label="绑定分组筛选">绑定分组筛选</Form.Item>;
};

export default DynamicGroupFilterConfig;

registerConfigRenderer(CONFIG_TYPES.GROUP_FILTER, ({ handlePropsChange, item, configs }) => (
  <DynamicGroupFilterConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

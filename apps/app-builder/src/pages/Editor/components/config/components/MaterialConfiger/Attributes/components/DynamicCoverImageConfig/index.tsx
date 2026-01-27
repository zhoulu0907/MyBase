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

const DynamicCoverImageConfig = ({ handlePropsChange, item, configs }: Props) => {
  return <Form.Item className={styles.formItem} label="封面图片">ss</Form.Item>;
};

export default DynamicCoverImageConfig;

registerConfigRenderer(CONFIG_TYPES.COVER_IMAGE, ({ handlePropsChange, item, configs }) => (
  <DynamicCoverImageConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

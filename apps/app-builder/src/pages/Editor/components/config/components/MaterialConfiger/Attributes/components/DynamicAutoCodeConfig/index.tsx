import React, { useState } from 'react';
import { Form, Upload, Message } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { uploadFile } from '@onebase/platform-center';
import styles from '../../index.module.less';

export interface DynamicFileConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicAutoCodeConfig: React.FC<DynamicFileConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const autoCodeKey = 'autoCodeConfig';
 

  return (
    <Form.Item layout="vertical" labelAlign="left" label={'编号规则配置'} className={styles.formItem}>
      <div>支持MB以内。</div>
     
    </Form.Item>
  );
};

export default DynamicAutoCodeConfig;

import { Form, Select, Radio } from '@arco-design/web-react';
import { useEffect, useRef, useState } from 'react';
import { CONFIG_TYPES, getPopupContainer } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicCoverImageConfig = ({ handlePropsChange, item, configs }: Props) => {
  const coverFieldKey = 'coverField';
  const imageFillKey = 'imageFill';

  return (
    <Form.Item className={styles.formItem} label={item.name || '封面图片'}>
      <Select
        placeholder={`请选择${item.name}`}
        value={configs[coverFieldKey]}
        getPopupContainer={getPopupContainer}
        style={{ marginBottom: '8px' }}
      ></Select>

      <Radio.Group
        value={configs[imageFillKey]}
        type="button"
        size="default"
        style={{ width: '100%', display: 'flex' }}
      >
        {item.range.map((option: any) => (
          <Radio key={option.value} value={option.value} style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}>
            {option.label}
          </Radio>
        ))}
      </Radio.Group>
    </Form.Item>
  );
};

export default DynamicCoverImageConfig;

registerConfigRenderer(CONFIG_TYPES.COVER_IMAGE, ({ handlePropsChange, item, configs }) => (
  <DynamicCoverImageConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

import { Checkbox, Form, Radio, InputNumber } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicPaginationConfig = ({ handlePropsChange, item, configs }: Props) => {
  const paginationConfigKey = item.key || 'paginationConfig';
  const range = [
    { key: 'tl', text: '左上', value: 'tl' },
    { key: 'topCenter', text: '上中', value: 'topCenter' },
    { key: 'tr', text: '右上', value: 'tr' },
    { key: 'bl', text: '左下', value: 'bl' },
    { key: 'bottomCenter', text: '下中', value: 'bottomCenter' },
    { key: 'br', text: '右下', value: 'br' }
  ];

  return (
    <>
      <Form.Item
        label="分页设置"
        layout="horizontal"
        labelAlign="left"
        labelCol={{ span: 16 }}
        wrapperCol={{ span: 8 }}
      >
        <Checkbox
          checked={configs[paginationConfigKey]?.display}
          onChange={(value) => {
            const pageSize = configs[paginationConfigKey]?.pageSize || 20;
            const pagePosition = configs[paginationConfigKey]?.pagePosition || 'br';
            handlePropsChange(item.key, { ...configs[paginationConfigKey], display: value, pageSize, pagePosition });
          }}
        >
          开启分页
        </Checkbox>
      </Form.Item>
      {configs[paginationConfigKey]?.display && (
        <>
          <Form.Item className={styles.formItem} label="每页显示数据条数">
            <InputNumber
              value={configs[paginationConfigKey]?.pageSize}
              onChange={(value) => {
                if (!value) return;
                handlePropsChange(item.key, { ...configs[paginationConfigKey], pageSize: value });
              }}
              min={1}
              step={1}
              precision={0}
            />
          </Form.Item>
          <Form.Item className={styles.formItem} label="分页位置">
            <Radio.Group
              type="button"
              size="large"
              value={configs[paginationConfigKey]?.pagePosition}
              onChange={(value) => {
                handlePropsChange(paginationConfigKey, { ...configs[paginationConfigKey], pagePosition: value });
              }}
              className={styles.pagePositionRadioGroup}
            >
              {range.map((option: any) => (
                <Radio key={option.key} value={option.value} className={styles.pagePositionRadio}>
                  {option.text}
                </Radio>
              ))}
            </Radio.Group>
          </Form.Item>
        </>
      )}
    </>
  );
};

export default DynamicPaginationConfig;

registerConfigRenderer(CONFIG_TYPES.PAGINATION_CONFIG, ({ handlePropsChange, item, configs }) => (
  <DynamicPaginationConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

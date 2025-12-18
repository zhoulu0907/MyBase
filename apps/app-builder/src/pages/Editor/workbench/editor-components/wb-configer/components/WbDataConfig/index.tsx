import { Form, Switch } from '@arco-design/web-react';
import styles from '../../attributes.module.less';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IDataConfigConfigType, type IBooleanConfigType } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IDataConfigConfigType;
  configs: Record<string, unknown>;
}

const WbDataConfig = ({ handlePropsChange, item, configs }: Props) => {
  // 获取 dataConfig 对象，如果不存在则初始化为空对象
  const dataConfig = (configs[item.key] as Record<string, boolean>) || {};

  // 处理单个开关项的值变更
  const handleSwitchChange = (rangeItemKey: string, value: boolean) => {
    // 更新整个 dataConfig 对象
    handlePropsChange(item.key, {
      ...dataConfig,
      [rangeItemKey]: value
    });
  };

  return (
    <div>
      {item.range &&
        Array.isArray(item.range) &&
        item.range.map((rangeItem: IBooleanConfigType) => (
          <Form.Item
            key={rangeItem.key}
            label={
              <div style={{ textAlign: 'left' }}>
                <span>{rangeItem.name}</span>
              </div>
            }
            labelCol={{ span: 21 }}
            wrapperCol={{ span: 1 }}
            layout="horizontal"
            className={styles.formItem}
          >
            <Switch
              size="small"
              checked={dataConfig[rangeItem.key] ?? false}
              onChange={(value) => {
                handleSwitchChange(rangeItem.key, value);
              }}
            />
          </Form.Item>
        ))}
    </div>
  );
};

export default WbDataConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_DATA_CONFIG, ({ handlePropsChange, item, configs }) => (
  <WbDataConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

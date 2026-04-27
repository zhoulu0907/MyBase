import { Form, ColorPicker } from '@arco-design/web-react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbColorConfigType } from '@onebase/ui-kit';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IWbColorConfigType;
  configs: Record<string, unknown>;
}

const WbColorConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item
      className={styles.formItem || undefined}
      label={item.name}
      layout="horizontal"
      labelCol={{ span: 7 }}
      wrapperCol={{ span: 17 }}
      labelAlign="left"
    >
      <div className={styles.colorPicker || undefined}>
        <ColorPicker
          showText={!!configs[item.key]}
          value={configs[item.key] as string | undefined}
          onChange={(value) => {
            handlePropsChange(item.key, value);
          }}
        />
      </div>
    </Form.Item>
  );
};

export default WbColorConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_COLOR, ({ handlePropsChange, item, configs }) => (
  <WbColorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

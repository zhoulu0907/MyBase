import { Checkbox, Form, Input } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  isInSubTable: boolean;
}

const DynamicLabelInputConfig = ({ handlePropsChange, item, configs, isInSubTable }: Props) => {
  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {!isInSubTable && typeof configs[item.key]['display'] === 'boolean' && (
            <Checkbox
              checked={configs[item.key]['display']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
              }}
            >
              显示标题
            </Checkbox>
          )}
        </>
      }
    >
      <Input
        placeholder={`请输入${item.name}`}
        value={configs[item.key]['text']}
        onChange={(value) => {
          handlePropsChange(item.key, { ...configs[item.key], text: value });
        }}
      />
    </Form.Item>
  );
};

export default DynamicLabelInputConfig;

registerConfigRenderer(CONFIG_TYPES.LABEL_INPUT, ({ isInSubTable, handlePropsChange, item, configs }) => (
  <DynamicLabelInputConfig isInSubTable={isInSubTable} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
import { Checkbox, Form, Input } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  isDivider?: boolean;
}

const DynamicTooltipInputConfig = ({ handlePropsChange, item, configs, isDivider }: Props) => {
  return (
    <Form.Item
      className={styles.formItem}
      label={
        <>
          {item.name}
          {isDivider && typeof configs[item.key]['display'] === 'boolean' && (
            <Checkbox
              checked={configs[item.key]['display']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
              }}
            >
              显示描述
            </Checkbox>
          )}
        </>
      }
    >
      <Input.TextArea
        placeholder={`请输入${item.name}`}
        value={!isDivider ? configs[item.key] : configs[item.key]['text']}
        onChange={(value) => {
          handlePropsChange(item.key, !isDivider ? value : { ...configs[item.key], text: value });
        }}
      />
    </Form.Item>
  );
};

export default DynamicTooltipInputConfig;

registerConfigRenderer(CONFIG_TYPES.TOOLTIP_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTooltipInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

registerConfigRenderer(CONFIG_TYPES.FORM_DIVIDER_TOOLTIP_INPUT, ({ handlePropsChange, item, configs }) => (
  <DynamicTooltipInputConfig handlePropsChange={handlePropsChange} item={item} configs={configs} isDivider={true} />
));

import { Form } from '@arco-design/web-react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbMenuSelectorConfigType } from '@onebase/ui-kit';
import MenuSelector from '@/pages/Editor/workbench/components/MenuSelector';
import styles from '../../index.module.less';

interface Props {
  handlePropsChange: (key: string, value: string) => void;
  item: IWbMenuSelectorConfigType;
  configs: Record<string, unknown>;
}

const WbMenuSelectorConfig = ({ handlePropsChange, item, configs }: Props) => {
  const currentValue = (configs[item.key] as string) || '';

  const handleMenuChange = (value: string | string[]) => {
    // 单选模式下，value 是 string
    const selectedValue = Array.isArray(value) ? value[0] || '' : value || '';
    handlePropsChange(item.key, selectedValue);
  };

  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <MenuSelector
        mode="single"
        value={currentValue}
        onChange={handleMenuChange}
        searchPlaceholder="搜索页面"
        className={styles.menuSelector}
      />
    </Form.Item>
  );
};

export default WbMenuSelectorConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_MENU_SELECTOR, ({ handlePropsChange, item, configs }) => (
  <WbMenuSelectorConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

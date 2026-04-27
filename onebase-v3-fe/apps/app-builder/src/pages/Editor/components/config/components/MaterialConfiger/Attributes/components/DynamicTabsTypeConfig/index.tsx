import { Form, Select, Tabs } from '@arco-design/web-react';
import { getPopupContainer, CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const Option = Select.Option;
const TabPane = Tabs.TabPane;

const DynamicTabsTypeConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <Select
        defaultValue={configs[item.key]}
        getPopupContainer={getPopupContainer}
        onChange={(value) => handlePropsChange(item.key, value)}
      >
        {item.range.map((option: any) => (
          <Option key={option.key} value={option.value}>
            <Tabs size="mini" defaultActiveTab="1" type={option.value} style={{ pointerEvents: 'none' }}>
              <TabPane key="1" title="标签页1" />
              <TabPane key="2" title="标签页2" />
            </Tabs>
          </Option>
        ))}
      </Select>
    </Form.Item>
  );
};

export default DynamicTabsTypeConfig;

registerConfigRenderer(CONFIG_TYPES.TABS_TYPE, ({ handlePropsChange, item, configs }) => (
  <DynamicTabsTypeConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
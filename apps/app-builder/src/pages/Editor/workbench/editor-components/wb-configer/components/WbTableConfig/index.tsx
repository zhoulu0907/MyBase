import { Button, Modal } from '@arco-design/web-react';
import { registerConfigRenderer } from '../../registry';
import { WORKBENCH_CONFIG_TYPES, type IWbColorConfigType } from '@onebase/ui-kit';
import TableSelector from '@/pages/Editor/workbench/components/TableSelector';
import styles from './index.module.less';
import { useState } from 'react';

interface Props {
  handlePropsChange: (key: string, value: unknown) => void;
  item: IWbColorConfigType;
  configs: Record<string, unknown>;
}

const WbTableConfig = ({ handlePropsChange, item, configs }: Props) => {
  const [visible, setVisible] = useState<boolean>(false);
  const [selectedItem, setSelectedItem] = useState({});
  const [selectedKey, setSelectedKey] = useState<string>('');

  const handleOpen = () => {
    const initialKey = (configs?.[item.key] as { componentId?: string } | undefined)?.componentId || '';
    setSelectedKey(initialKey);
    setVisible(true);
  };

  const handleOk = () => {
    setVisible(false);
    handlePropsChange(item.key, selectedItem);
  };

  const handleChange = (key: string, item: object) => {
    setSelectedKey(key);
    setSelectedItem(item);
  };

  return (
    <>
      <Button className={styles.configBtn} type="outline" onClick={handleOpen}>
        配置
      </Button>

      <Modal
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={handleOk}
        title={<div style={{ textAlign: 'left' }}>添加列表</div>}
      >
        <TableSelector value={selectedKey} onChange={handleChange} />
      </Modal>
    </>
  );
};

export default WbTableConfig;

registerConfigRenderer(WORKBENCH_CONFIG_TYPES.WB_TABLE_CONFIG, ({ handlePropsChange, item, configs }) => (
  <WbTableConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

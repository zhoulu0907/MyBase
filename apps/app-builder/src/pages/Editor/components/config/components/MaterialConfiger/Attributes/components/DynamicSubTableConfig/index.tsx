import { Form, Checkbox, Switch, Radio } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import styles from '../../index.module.less';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { registerConfigRenderer } from '../../registry';

export interface DynamicSubTableConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicSubTableConfig: React.FC<DynamicSubTableConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const subTableConfigKey = item.key || 'subTableConfig';

  const [subTableConfig, setSubTableConfig] = useState({
    showIndex: false,
    showOperate: true,
    editRow: true,
    deleteRow: true,
    operateFixed: true,
    pageSize: 5,
    columnFixed: 0
  });

  useEffect(() => {
    setSubTableConfig((prev) => ({ ...prev, ...configs[subTableConfigKey] }));
  }, [configs[subTableConfigKey]]);

  const handleChange = (key: string, value: boolean | string) => {
    const newConfig = { ...configs[subTableConfigKey], [key]: value };
    handlePropsChange(subTableConfigKey, newConfig);
  };

  return (
    <Form.Item layout="vertical" label={item.name || '展示样式'} className={styles.formItem}>
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>显示序号列</div>
        <Switch
          checked={subTableConfig.showIndex}
          size="small"
          onChange={(value) => handleChange('showIndex', value)}
        />
      </div>

      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>显示操作列</div>
        <Switch
          checked={subTableConfig.showOperate}
          size="small"
          onChange={(value) => handleChange('showOperate', value)}
        />
      </div>

      {subTableConfig.showOperate && (
        <div className={styles.subTableModule}>
          <div style={{ marginBottom: '8px' }}>
            <Checkbox checked={subTableConfig.editRow} onChange={(value) => handleChange('editRow', value)}>
              <span style={{ color: 'var(--color-text-2)' }}>可编辑已有数据</span>
            </Checkbox>
          </div>
          <div style={{ marginBottom: '8px' }}>
            <Checkbox checked={subTableConfig.deleteRow} onChange={(value) => handleChange('deleteRow', value)}>
              <span style={{ color: 'var(--color-text-2)' }}>可删除已有数据</span>
            </Checkbox>
          </div>
          <div>
            <Checkbox checked={subTableConfig.operateFixed} onChange={(value) => handleChange('operateFixed', value)}>
              <span style={{ color: 'var(--color-text-2)' }}>操作列冻结</span>
            </Checkbox>
          </div>
        </div>
      )}

      {/* <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>分页条数</div>
        <Radio.Group
          type="button"
          size="mini"
          value={subTableConfig.pageSize}
          onChange={(value) => handleChange('pageSize', value)}
        >
          <Radio value={5}>5</Radio>
          <Radio value={10}>10</Radio>
          <Radio value={20}>20</Radio>
          <Radio value={30}>30</Radio>
        </Radio.Group>
      </div> */}

      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
        <div style={{ flex: 1, marginRight: 'auto', color: 'var(--color-text-2)' }}>左侧冻结列</div>
        <Radio.Group
          type="button"
          size="mini"
          value={subTableConfig.columnFixed}
          onChange={(value) => handleChange('columnFixed', value)}
        >
          <Radio value={0}>无</Radio>
          <Radio value={1}>1</Radio>
          <Radio value={2}>2</Radio>
          <Radio value={3}>3</Radio>
        </Radio.Group>
      </div>
    </Form.Item>
  );
};
export default DynamicSubTableConfig;

registerConfigRenderer(
  CONFIG_TYPES.SUB_TABLE,
  ({ id, handlePropsChange, item, configs }) => (
    <DynamicSubTableConfig
      id={id}
      handlePropsChange={handlePropsChange}
      item={item}
      configs={configs}
    />
  )
);

import React from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { Form, Checkbox, Grid, Dropdown, Menu, ColorPicker, InputNumber, Switch, Button } from '@arco-design/web-react';
import styles from '../../index.module.less';

export interface DynamicRateConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicRateConfig: React.FC<DynamicRateConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const rateKey = item.key || 'rateConfig';

  // 图标下拉内容
  const dropList = [
    { lable: '1', value: '1' },
    { lable: '2', value: '2' },
    { lable: '3', value: '3' },
    { lable: '4', value: '4' }
  ];

  return (
    <>
      <Form.Item
        layout="vertical"
        labelAlign="left"
        label={
          <>
            {'样式'}
            <Checkbox
              checked={configs[rateKey]['showIcon']}
              style={{ float: 'right' }}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], display: value });
              }}
            >
              显示图标
            </Checkbox>
          </>
        }
        className={styles.formItem}
      >
        <Grid.Row gutter={8} align="center">
          <Grid.Col span={6}>
            <Dropdown
              droplist={
                <Menu>
                  {dropList.map((iconItem) => (
                    <Menu.Item
                      key={iconItem.value}
                      onClick={() => {
                        handlePropsChange(rateKey, { ...configs[rateKey], iconName: iconItem.value });
                      }}
                    >
                      {iconItem.lable}
                    </Menu.Item>
                  ))}
                </Menu>
              }
            >
              <div>{configs[rateKey]['iconName']}</div>
            </Dropdown>
          </Grid.Col>
          <Grid.Col span={18}>
            <ColorPicker
              value={configs[rateKey]['iconColor']}
              showText
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], iconColor: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
      </Form.Item>

      <Form.Item layout="vertical" labelAlign="left" label="数量" className={styles.formItem}>
        <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginBottom: '8px' }}>
          <Grid.Col span={10}>最大值</Grid.Col>
          <Grid.Col span={14}>
            <InputNumber
              min={1}
              max={10}
              step={1}
              value={configs[rateKey]['max']}
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], max: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
        <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)' }}>
          <Grid.Col span={10}>允许半星</Grid.Col>
          <Grid.Col span={14} style={{ textAlign: 'right' }}>
            <Switch
              size="small"
              checked={configs[rateKey]['allowHalf']}
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], allowHalf: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
      </Form.Item>

      <Form.Item layout="vertical" labelAlign="left" label="文字提示" className={styles.formItem}>
        <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginBottom: '8px' }}>
          <Grid.Col span={10}>显示选中结果</Grid.Col>
          <Grid.Col span={14} style={{ textAlign: 'right' }}>
            <Switch
              size="small"
              checked={configs[rateKey]['showResult']}
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], showResult: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
        <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginBottom: '8px' }}>
          <Grid.Col span={10}>自定义评分文案</Grid.Col>
          <Grid.Col span={14} style={{ textAlign: 'right' }}>
            <Switch
              size="small"
              checked={configs[rateKey]['showTooltips']}
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], showTooltips: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
        {configs[rateKey]['showTooltips'] && <Button long>配置评分文案</Button>}
      </Form.Item>
    </>
  );
};

export default DynamicRateConfig;

registerConfigRenderer(CONFIG_TYPES.RATE_CONFIG, ({ id, handlePropsChange, item, configs }) => (
  <DynamicRateConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

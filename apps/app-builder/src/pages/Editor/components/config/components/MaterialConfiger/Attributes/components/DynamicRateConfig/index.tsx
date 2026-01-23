import React, { useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import {
  Form,
  Checkbox,
  Grid,
  Dropdown,
  Menu,
  ColorPicker,
  InputNumber,
  Switch,
  Input,
  Button,
  Modal
} from '@arco-design/web-react';
import {
  IconStarFill,
  IconFaceSmileFill,
  IconBulb,
  IconSunFill,
  IconThumbUpFill,
  IconFire,
  IconHeartFill,
  IconBug,
  IconExclamationCircleFill,
  IconPushpin,
  IconSubscribe,
  IconClose,
  IconNotification,
  IconMinus
} from '@arco-design/web-react/icon';
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
    { lable: <IconStarFill />, value: 'IconStarFill' },
    { lable: <IconFaceSmileFill />, value: 'IconFaceSmileFill' },
    { lable: <IconBulb />, value: 'IconBulb' },
    { lable: <IconSunFill />, value: 'IconSunFill' },
    { lable: <IconThumbUpFill />, value: 'IconThumbUpFill' },
    { lable: <IconFire />, value: 'IconFire' },
    { lable: <IconHeartFill />, value: 'IconHeartFill' },
    { lable: <IconBug />, value: 'IconBug' },
    { lable: <IconExclamationCircleFill />, value: 'IconExclamationCircleFill' },
    { lable: <IconPushpin />, value: 'IconPushpin' },
    { lable: <IconSubscribe />, value: 'IconSubscribe' },
    { lable: <IconClose />, value: 'IconClose' },
    { lable: <IconNotification />, value: 'IconNotification' },
    { lable: <IconMinus />, value: 'IconMinus' }
  ];

  // 等级说明
  const [tooltipVisible, setTooltipVisible] = useState(false);
  const [tooltipList, setTooltipList] = useState<any[]>([]);

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
                handlePropsChange(rateKey, { ...configs[rateKey], showIcon: value });
              }}
            >
              显示图标
            </Checkbox>
          </>
        }
        className={styles.formItem}
      >
        <Grid.Row gutter={8} align="center">
          <Grid.Col span={4}>
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
                      <span style={{ fontSize: '16px' }}>{iconItem.lable}</span>
                    </Menu.Item>
                  ))}
                </Menu>
              }
            >
              <div
                style={{
                  width: '32px',
                  height: '32px',
                  backgroundColor: 'var(--color-secondary)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '24px',
                  borderRadius: '4px'
                }}
              >
                {dropList.find((ele) => ele.value === configs[rateKey]['iconName'])?.lable}
              </div>
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
                handlePropsChange(rateKey, {
                  ...configs[rateKey],
                  max: value,
                  tooltips: configs[rateKey]['tooltips'].slice(0, value)
                });
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
              checked={configs[rateKey]['showCustomTooltips']}
              onChange={(value) => {
                handlePropsChange(rateKey, { ...configs[rateKey], showCustomTooltips: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
        {configs[rateKey]['showCustomTooltips'] && (
          <Button
            long
            onClick={() => {
              const max = configs[rateKey]['max'];
              const newTooltipList = Array.from({ length: configs[rateKey]['max'] }, (_, index) => {
                return index < max ? configs[rateKey]['tooltips'][index] : undefined;
              });
              setTooltipList(newTooltipList);
              setTooltipVisible(true);
            }}
          >
            配置评分文案
          </Button>
        )}
      </Form.Item>
      <Modal
        visible={tooltipVisible}
        title="设置等级说明"
        footer={[
          <Button onClick={() => setTooltipVisible(false)}>取消</Button>,
          <Button
            type="primary"
            onClick={() => {
              handlePropsChange(rateKey, { ...configs[rateKey], tooltips: tooltipList });
              setTooltipVisible(false);
            }}
          >
            确定
          </Button>
        ]}
        onCancel={() => setTooltipVisible(false)}
      >
        <div style={{ marginBottom: '8px' }}>
          为每个等级设置说明文案。当鼠标悬停在对应等级或选中后，显示对等级的描述文案
        </div>
        <Grid.Row gutter={8}>
          <Grid.Col span={4}>等级</Grid.Col>
          <Grid.Col span={20}>文字</Grid.Col>
        </Grid.Row>
        {tooltipList.map((item, index) => (
          <Grid.Row gutter={8} key={index} style={{ marginBottom: '8px' }}>
            <Grid.Col span={4}>
              <div style={{ backgroundColor: 'var(--color-secondary)', padding: '5px 8px', borderRadius: '2px' }}>
                {index + 1}
              </div>
            </Grid.Col>
            <Grid.Col span={20}>
              <Input
                placeholder="请输入文字"
                value={item}
                onChange={(value) => {
                  const newTooltips = [...tooltipList];
                  newTooltips[index] = value;
                  setTooltipList(newTooltips);
                }}
              />
            </Grid.Col>
          </Grid.Row>
        ))}
      </Modal>
    </>
  );
};

export default DynamicRateConfig;

registerConfigRenderer(CONFIG_TYPES.RATE_CONFIG, ({ id, handlePropsChange, item, configs }) => (
  <DynamicRateConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

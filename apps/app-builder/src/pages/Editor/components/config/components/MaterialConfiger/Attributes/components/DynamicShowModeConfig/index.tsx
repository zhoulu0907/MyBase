import React, { useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES, SHOW_MODE_TYPES } from '@onebase/ui-kit';
import { Form, Radio, Input, Grid } from '@arco-design/web-react';
import styles from '../../index.module.less';

export interface DynamicShowModeConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicShowModeConfig: React.FC<DynamicShowModeConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const showModeKey = item.key || 'showMode';

  const modeList = [
    { label: '勾选框', value: SHOW_MODE_TYPES.CHECKBOX },
    { label: '开关', value: SHOW_MODE_TYPES.SWITCH },
    { label: '是/否', value: SHOW_MODE_TYPES.WHETHER }
  ];

  return (
    <Form.Item layout="vertical" labelAlign="left" label={'显示方式'} className={styles.formItem}>
      <Radio.Group
        type="button"
        size="default"
        value={configs[showModeKey]['type']}
        onChange={(value) => {
          handlePropsChange(showModeKey, { ...configs[showModeKey], type: value });
        }}
        style={{ width: '100%', display: 'flex' }}
      >
        {modeList.map((option: any) => (
          <Radio key={option.value} value={option.value} style={{ flex: 1, textAlign: 'center', whiteSpace: 'nowrap' }}>
            {option.label}
          </Radio>
        ))}
      </Radio.Group>

      {configs[showModeKey]['type'] === SHOW_MODE_TYPES.CHECKBOX && (
        <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginTop: '8px' }}>
          <Grid.Col span={6}>检查内容</Grid.Col>
          <Grid.Col span={18}>
            <Input
              value={configs[showModeKey]['checkText']}
              placeholder="请输入检查内容"
              onChange={(value) => {
                handlePropsChange(showModeKey, { ...configs[showModeKey], checkText: value });
              }}
            />
          </Grid.Col>
        </Grid.Row>
      )}
      {configs[showModeKey]['type'] === SHOW_MODE_TYPES.WHETHER && (
        <>
          <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginTop: '8px' }}>
            <Grid.Col span={2}>是</Grid.Col>
            <Grid.Col span={22}>
              <Input
                value={configs[showModeKey]['yesText']}
                placeholder="请输入"
                onChange={(value) => {
                  handlePropsChange(showModeKey, { ...configs[showModeKey], yesText: value });
                }}
              />
            </Grid.Col>
          </Grid.Row>
          <Grid.Row gutter={8} align="center" style={{ color: 'var(--color-text-1)', marginTop: '8px' }}>
            <Grid.Col span={2}>否</Grid.Col>
            <Grid.Col span={22}>
              <Input
                value={configs[showModeKey]['noText']}
                placeholder="请输入"
                onChange={(value) => {
                  handlePropsChange(showModeKey, { ...configs[showModeKey], noText: value });
                }}
              />
            </Grid.Col>
          </Grid.Row>
        </>
      )}
    </Form.Item>
  );
};

export default DynamicShowModeConfig;

registerConfigRenderer(CONFIG_TYPES.CHECK_ITEM_SHOW_MODE, ({ id, handlePropsChange, item, configs }) => (
  <DynamicShowModeConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

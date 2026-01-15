import { Checkbox, Form, Grid, InputNumber } from '@arco-design/web-react';
import styles from '../styles/index.module.less';

export interface DynamicVerifyConfigProps {
  onChange: (value: any) => void;
  item: any;
  value: any;
}

const DynamicVerifyConfig: React.FC<DynamicVerifyConfigProps> = ({ onChange, item, value }) => {
  const config = value || {};

  const handleChange = (key: string, v: any) => {
    onChange({ ...config, [key]: v });
  };

  return (
    <Form.Item layout="vertical" label={item.name || '校验'} className={styles.formItem}>
      <Grid.Row>
        <Grid.Col flex="auto" style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
          <Checkbox
            checked={config.required}
            onChange={(v) => handleChange('required', v)}
          >
            必填
          </Checkbox>

          {typeof config.noRepeat === 'boolean' && (
            <Checkbox
              checked={config.noRepeat}
              onChange={(v) => handleChange('noRepeat', v)}
            >
              不允许重复
            </Checkbox>
          )}

          {typeof config.lengthLimit === 'boolean' && (
            <Grid.Row align="center">
              <Grid.Col span={8}>
                <Checkbox
                  checked={config.lengthLimit}
                  onChange={(v) => handleChange('lengthLimit', v)}
                >
                  长度范围
                </Checkbox>
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  value={config.minLength}
                  placeholder="字数下限"
                  min={0}
                  onChange={(v) => {
                    if (v > config.maxLength && config.maxLength > 0) return;
                    handleChange('minLength', v);
                  }}
                />
              </Grid.Col>
              <Grid.Col span={2} style={{ textAlign: 'center' }}>
                -
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  placeholder="字数上限"
                  min={0}
                  value={config.maxLength}
                  onChange={(v) => {
                    if (v < config.minLength) return;
                    handleChange('maxLength', v);
                  }}
                />
              </Grid.Col>
            </Grid.Row>
          )}
          
           {typeof config.numberLimit === 'boolean' && (
            <Grid.Row align="center">
              <Grid.Col span={8}>
                <Checkbox
                  checked={config.numberLimit}
                  onChange={(v) => handleChange('numberLimit', v)}
                >
                  数值范围
                </Checkbox>
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  value={config.min}
                  placeholder="最小值"
                  onChange={(v) => {
                    if (v > config.max && config.max !== undefined) return;
                    handleChange('min', v);
                  }}
                />
              </Grid.Col>
              <Grid.Col span={2} style={{ textAlign: 'center' }}>
                -
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  placeholder="最大值"
                  value={config.max}
                  onChange={(v) => {
                    if (v < config.min) return;
                    handleChange('max', v);
                  }}
                />
              </Grid.Col>
            </Grid.Row>
          )}
        </Grid.Col>
      </Grid.Row>
    </Form.Item>
  );
};

export default DynamicVerifyConfig;

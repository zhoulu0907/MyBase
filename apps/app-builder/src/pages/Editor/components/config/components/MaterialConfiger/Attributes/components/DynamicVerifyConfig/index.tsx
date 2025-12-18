import { Checkbox, Form, Grid, Input, InputNumber } from '@arco-design/web-react';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';

export interface DynamicVerifyConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicVerifyConfig: React.FC<DynamicVerifyConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const verifyKey = 'verify';

  return (
    <Form.Item layout="vertical" label={item.name || '校验'} className={styles.formItem}>
      <Grid.Row>
        <Grid.Col flex="auto" style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
          <Checkbox
            checked={configs[verifyKey]['required']}
            onChange={(value) => {
              handlePropsChange(verifyKey, { ...configs[verifyKey], required: value });
            }}
          >
            必填
          </Checkbox>

          {typeof configs[verifyKey]['noRepeat'] === 'boolean' && (
            <Checkbox
              checked={configs[verifyKey]['noRepeat']}
              onChange={(value) => {
                handlePropsChange(verifyKey, { ...configs[verifyKey], noRepeat: value });
              }}
            >
              不允许重复
            </Checkbox>
          )}

          {typeof configs[verifyKey]['lengthLimit'] === 'boolean' && (
            <Grid.Row align="center">
              <Grid.Col span={8}>
                <Checkbox
                  checked={configs[verifyKey]['lengthLimit']}
                  onChange={(value) => {
                    handlePropsChange(verifyKey, { ...configs[verifyKey], lengthLimit: value });
                  }}
                >
                  长度范围
                </Checkbox>
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  value={configs[verifyKey]['minLength']}
                  placeholder="字数下限"
                  min={0}
                  max={configs[verifyKey]['maxLength'] || undefined}
                  onChange={(value) => {
                    if (value > configs[verifyKey]['maxLength']) return;
                    handlePropsChange(verifyKey, { ...configs[verifyKey], minLength: value });
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
                  min={configs[verifyKey]['minLength'] || 0}
                  value={configs[verifyKey]['maxLength']}
                  onChange={(value) => {
                    if (value < configs[verifyKey]['minLength']) return;
                    handlePropsChange(verifyKey, { ...configs[verifyKey], maxLength: value });
                  }}
                />
              </Grid.Col>
            </Grid.Row>
          )}

          {typeof configs[verifyKey]['numberLimit'] === 'boolean' && (
            <Grid.Row align="center">
              <Grid.Col span={8}>
                <Checkbox
                  checked={configs[verifyKey]['numberLimit']}
                  onChange={(value) => {
                    handlePropsChange(verifyKey, { ...configs[verifyKey], numberLimit: value });
                  }}
                >
                  数值范围
                </Checkbox>
              </Grid.Col>
              <Grid.Col span={7}>
                <InputNumber
                  size="mini"
                  placeholder="最小值"
                  min={0}
                  max={configs[verifyKey]['max']}
                  value={configs[verifyKey]['min']}
                  onChange={(value) => {
                    if (value > configs[verifyKey]['max']) return;
                    handlePropsChange(verifyKey, { ...configs[verifyKey], min: value });
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
                  min={configs[verifyKey]['min'] || 0}
                  value={configs[verifyKey]['max']}
                  onChange={(value) => {
                    if (value < configs[verifyKey]['max']) return;
                    handlePropsChange(verifyKey, { ...configs[verifyKey], max: value });
                  }}
                />
              </Grid.Col>
            </Grid.Row>
          )}

          {typeof configs[verifyKey]['maxChecked'] === 'number' && (
            <InputNumber
              value={configs[verifyKey]['maxChecked']}
              min={0}
              prefix="可选数量限制"
              onChange={(value) => {
                if (!value) return;
                handlePropsChange(verifyKey, { ...configs[verifyKey], maxChecked: value });
              }}
            />
          )}
          {typeof configs[verifyKey]['maxCount'] === 'number' && (
            <InputNumber
              value={configs[verifyKey]['maxCount']}
              min={1}
              prefix="上传数量限制"
              onChange={(value) => {
                if (typeof value !== 'number') return;
                handlePropsChange(verifyKey, { ...configs[verifyKey], maxCount: value });
              }}
            />
          )}
          {typeof configs[verifyKey]['maxSize'] === 'number' && (
            <InputNumber
              value={configs[verifyKey]['maxSize']}
              min={0}
              prefix="大小限制"
              suffix={configs[verifyKey]['maxSize'] ? 'MB' : ''}
              onChange={(value) => {
                if (!value) return;
                handlePropsChange(verifyKey, { ...configs[verifyKey], maxSize: value });
              }}
            />
          )}
          {typeof configs[verifyKey]['fileFormat'] === 'string' && (
            <Input
              placeholder={`请输入支持文件格式，用英文逗号分隔`}
              value={configs[verifyKey]['fileFormat']}
              onChange={(value) => {
                handlePropsChange(verifyKey, { ...configs[verifyKey], fileFormat: value });
              }}
            />
          )}
        </Grid.Col>
      </Grid.Row>
    </Form.Item>
  );
};
export default DynamicVerifyConfig;

registerConfigRenderer(CONFIG_TYPES.VERIFY, ({ id, handlePropsChange, item, configs }) => (
  <DynamicVerifyConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

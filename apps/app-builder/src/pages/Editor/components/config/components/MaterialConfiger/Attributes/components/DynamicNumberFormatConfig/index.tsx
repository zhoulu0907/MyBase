import { Checkbox, Form, Grid, Input, InputNumber } from '@arco-design/web-react';
import styles from '../../index.module.less';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';

const Row = Grid.Row;
const Col = Grid.Col;

interface Props {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
}

const DynamicNumberFormatConfig = ({ handlePropsChange, item, configs }: Props) => {
  return (
    <Form.Item className={styles.formItem} label={item.name}>
      <>
        <Row>
          <Col style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Checkbox
              checked={configs[item.key]['showPrecision']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], showPrecision: value });
              }}
              style={{ marginRight: 8 }}
            >
              保留小数点
            </Checkbox>
            <InputNumber
              size="mini"
              value={configs[item.key]['precision']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], precision: value });
              }}
              style={{ width: 80 }}
            />
          </Col>
        </Row>
        <Row>
          <Col style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Checkbox
              checked={configs[item.key]['showPercent']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], showPercent: value });
              }}
            >
              显示为百分比
            </Checkbox>
          </Col>
        </Row>
        <Row>
          <Col style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Checkbox
              checked={configs[item.key]['showUnit']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], showUnit: value });
              }}
              style={{ marginRight: 8 }}
            >
              显示单位
            </Checkbox>
            <Input
              style={{ width: 80 }}
              size="mini"
              value={configs[item.key]['unitValue']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], unitValue: value });
              }}
            />
          </Col>
        </Row>
        <Row>
          <Col style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Checkbox
              checked={configs[item.key]['useThousandsSeparator']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], useThousandsSeparator: value });
              }}
            >
              使用千分位分隔符
            </Checkbox>
          </Col>
        </Row>
      </>
    </Form.Item>
  );
};

export default DynamicNumberFormatConfig;

registerConfigRenderer(CONFIG_TYPES.NUMBER_FORMAT, ({ handlePropsChange, item, configs }) => (
  <DynamicNumberFormatConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />
));
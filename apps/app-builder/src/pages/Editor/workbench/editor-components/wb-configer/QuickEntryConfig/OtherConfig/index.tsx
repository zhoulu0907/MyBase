import { useI18n } from '@/hooks/useI18n';
import {
  Checkbox,
  ColorPicker,
  DatePicker,
  Form,
  Grid,
  Input,
  InputNumber,
  Radio,
  Select,
  Switch,
  Tooltip
} from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import { CONFIG_TYPES, usePageEditorSignal, getPopupContainer } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const FormItem = Form.Item;
const Option = Select.Option;

interface OtherConfigProps {
  cpID: string;
}

const OtherConfig = ({ cpID }: OtherConfigProps) => {
  const { t } = useI18n();

  useSignals();
  const { curComponentID, curComponentSchema, setCurComponentSchema, setPageComponentSchemas } =
    usePageEditorSignal();

  const [editData, setEditData] = useState<any>([]);
  const [configs, setConfigs] = useState<any>({});

  useEffect(() => {
    if (!cpID) {
      return;
    }

    setEditData(curComponentSchema?.editData || []);
    setConfigs(curComponentSchema?.config || {});
  }, [cpID, curComponentSchema]);

  const handlePropsChange = (key: string, value: any) => {
    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        [key]: value
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleConfigsChange = (config: any) => {
    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...config
      },
      layout: curComponentSchema.layout
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const renderEditItem = (item: any, index: number) => {
    // 过滤掉已经在其他面板中处理的配置项
    if (item.type === 'QuickEntry' || item.key === 'props') {
      return null;
    }

    switch (item.type) {
      case CONFIG_TYPES.SWITCH_INPUT:
        return (
          <FormItem
            label={
              <div style={{ textAlign: 'left' }}>
                <span>{item.name}</span>
              </div>
            }
            labelCol={{ span: 21 }}
            wrapperCol={{ span: 1 }}
            layout="horizontal"
            className={styles.formItem}
          >
            <Switch
              size="small"
              checked={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.TEXT_INPUT:
      case CONFIG_TYPES.PLACEHOLDER_INPUT:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Input
              placeholder={`请输入${item.name}`}
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.NUMBER_INPUT:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <InputNumber
              placeholder={`请输入${item.name}`}
              value={configs[item.key]}
              onChange={(value) => {
                if (value >= 0) {
                  handlePropsChange(item.key, value);
                }
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.TOOLTIP_INPUT:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Input.TextArea
              placeholder={`请输入${item.name}`}
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.COLOR:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <ColorPicker
              showText={!!configs[item.key]}
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.WIDTH_RADIO:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Radio.Group
              type="button"
              direction="horizontal"
              size="mini"
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            >
              {item.range?.map((rangeItem: any) => (
                <Radio key={rangeItem.key} value={rangeItem.value} className={styles.widthRadio}>
                  {rangeItem.text && rangeItem.text.startsWith('editor.') ? t(rangeItem.text) : rangeItem.text}
                </Radio>
              ))}
            </Radio.Group>
          </FormItem>
        );
      case CONFIG_TYPES.STATUS_RADIO:
      case CONFIG_TYPES.DATE_TYPE:
      case CONFIG_TYPES.FORM_LAYOUT:
      case CONFIG_TYPES.TEXT_ALIGN:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Radio.Group
              type="button"
              size="default"
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
              style={{
                width: '100%',
                display: 'flex'
              }}
            >
              {item.range?.map((rangeItem: any) => (
                <Radio
                  key={rangeItem.key}
                  value={rangeItem.value}
                  style={{
                    flex: 1,
                    textAlign: 'center',
                    whiteSpace: 'nowrap'
                  }}
                >
                  {rangeItem.text && rangeItem.text.startsWith('formEditor.') ? t(rangeItem.text) : rangeItem.text}
                </Radio>
              ))}
            </Radio.Group>
          </FormItem>
        );
      case CONFIG_TYPES.DATE_INPUT:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <DatePicker
              showTime={{
                defaultValue: '00:00:00'
              }}
              format="YYYY-MM-DD HH:mm:ss"
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
              style={{ width: '100%' }}
            />
          </FormItem>
        );
      default:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Input
              placeholder={`请输入${item.name}`}
              value={configs[item.key]}
              onChange={(value) => {
                handlePropsChange(item.key, value);
              }}
            />
          </FormItem>
        );
    }
  };

  return (
    <div className={styles.otherConfig}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          {editData
            .filter((item: any) => {
              // 过滤掉已经在其他面板中处理的配置项
              return item.type !== 'QuickEntry' && item.key !== 'props';
            })
            .map((item: any, index: number) => {
              const rendered = renderEditItem(item, index);
              return rendered ? <div key={index}>{rendered}</div> : null;
            })}
        </Form>
      )}
    </div>
  );
};

export default OtherConfig;


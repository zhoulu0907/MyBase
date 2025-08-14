import type { TXInputTextEditData } from '@/components/Materials/Basic/FormComponents/InputText/schema';
import { CONFIG_TYPES } from '@/components/Materials/constants';
import { useI18n } from '@/hooks/useI18n';
import { usePageEditorStore } from '@/hooks/useStore';
import { ColorPicker, Form, Input, InputNumber, Radio, Switch } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import DynamicFieldConfig from './components/DynamicFieldConfig';
import DynamicTableConfig from './components/DynamicTableConfig';
import styles from './index.module.less';

const FormItem = Form.Item;

/**
 * 属性配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface ConfigsProps {
  cpID: string;
}

const Attributes = ({ cpID }: ConfigsProps) => {
  const { t } = useI18n();

  const { curComponentSchema, setCurComponentSchema, setPageComponentSchemas } = usePageEditorStore();

  const [editData, setEditData] = useState<TXInputTextEditData>([]);
  const [configs, setConfigs] = useState<any>({});

  useEffect(() => {
    if (!cpID) {
      return;
    }
    console.debug('curComponentSchema------', curComponentSchema);
    setEditData(curComponentSchema.editData);
    setConfigs(curComponentSchema.config);
  }, [cpID, curComponentSchema]);

  const handlePropsChange = (key: string, value: string | number | boolean | any[]) => {
    console.log(`更新了属性: ${key} 值为: ${value}`);

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

    // console.log(curComponentSchema.config)
    // console.log(newCurComponentSchema.config)

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleMultiPropsChange = (updates: { key: string; value: string | number | boolean | any[] }[]) => {
    console.log(`更新了属性: ${updates}`);

    // 将 updates 数组中的每个 key-value 展开到 config 中
    const updatesObj = updates.reduce(
      (acc, cur) => {
        acc[cur.key] = cur.value;
        return acc;
      },
      {} as Record<string, any>
    );

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        ...updatesObj
      },
      layout: curComponentSchema.layout
    };

    // console.log(curComponentSchema.config);
    // console.log(newCurComponentSchema.config);

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  const handleLayoutChange = (key: string, value: string) => {
    console.log(`更新了布局属性: ${key} 值为: ${value}`);

    const newCurComponentSchema = {
      id: cpID,
      type: curComponentSchema.type,
      editData: curComponentSchema.editData,
      config: {
        ...curComponentSchema.config,
        [key]: value
      },
      layout: {
        ...curComponentSchema.layout,
        [key === 'width' ? 'w' : key === 'height' ? 'h' : key]: value
      }
    };

    setCurComponentSchema(newCurComponentSchema);
    setPageComponentSchemas(cpID, newCurComponentSchema);
  };

  // 可根据 id 获取/设置对应组件的属性，这里暂时未实现具体逻辑
  return (
    <div className={styles.attributes}>
      {cpID && (
        <Form autoComplete="off" layout="vertical">
          <FormItem
            label="组件ID"
            labelCol={{
              span: 5
            }}
          >
            <div className={styles.cpID}>{cpID}</div>
          </FormItem>

          {editData.map((item: any, index: number) => {
            if (
              item.type !== CONFIG_TYPES.SWITCH_INPUT &&
              item.type !== CONFIG_TYPES.TABLE_DATA &&
              item.type !== CONFIG_TYPES.FIELD_DATA
            ) {
              return (
                <FormItem label={item.name} key={index} className={styles.formItem}>
                  {(item.type === CONFIG_TYPES.TEXT_INPUT ||
                    item.type === CONFIG_TYPES.LABEL_INPUT ||
                    item.type === CONFIG_TYPES.TOOLTIP_INPUT ||
                    item.type === CONFIG_TYPES.PLACEHOLDER_INPUT ||
                    item.type === CONFIG_TYPES.UPLOAD_SIZE ||
                    item.type === CONFIG_TYPES.UPLOAD_LIMIT ||
                    item.type === CONFIG_TYPES.UPLOAD_COMPRESS) && (
                    <Input
                      placeholder={`请输入${item.name}`}
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                  {item.type === CONFIG_TYPES.NUMBER_INPUT && (
                    <InputNumber
                      placeholder={`请输入${item.name}`}
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                  {item.type === CONFIG_TYPES.LABEL_COL_SPAN && (
                    <InputNumber
                      placeholder={`请输入${item.name}`}
                      value={configs[item.key]}
                      max={10}
                      min={0}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                  {item.type === CONFIG_TYPES.DESCRIPTION_INPUT && (
                    <Input.TextArea
                      placeholder={`请输入${item.name}`}
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                  {item.type === CONFIG_TYPES.COLOR && (
                    <ColorPicker
                      showText
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                  {item.type === CONFIG_TYPES.WIDTH_RADIO && (
                    <Radio.Group
                      type="button"
                      direction="horizontal"
                      size="mini"
                      value={configs[item.key]}
                      onChange={(value) => {
                        handleLayoutChange(item.key, value);
                      }}
                    >
                      {item.range.map((item: any) => (
                        <Radio key={item.key} value={item.value} className={styles.widthRadio}>
                          {item.text && item.text.startsWith('editor.') ? t(item.text) : item.text}
                        </Radio>
                      ))}
                    </Radio.Group>
                  )}
                  {(item.type === CONFIG_TYPES.STATUS_RADIO ||
                    item.type === CONFIG_TYPES.DATE_TYPE ||
                    item.type === CONFIG_TYPES.FORM_LAYOUT ||
                    item.type === CONFIG_TYPES.TEXT_ALIGN) && (
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
                      {item.range.map((item: any) => (
                        <Radio
                          key={item.key}
                          value={item.value}
                          style={{
                            flex: 1,
                            textAlign: 'center',
                            whiteSpace: 'nowrap'
                          }}
                        >
                          {item.text && item.text.startsWith('formEditor.') ? t(item.text) : item.text}
                        </Radio>
                      ))}
                    </Radio.Group>
                  )}
                  {item.type === CONFIG_TYPES.COLUMN_COUNT_RADIO && (
                    <Radio.Group
                      type="button"
                      size="default"
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                      className={styles.columnCountRadioGroup}
                    >
                      {item.range.map((item: any) => (
                        <Radio key={item.key} value={item.value} className={styles.columnCountRadio}>
                          {item.text && item.text.startsWith('formEditor.') ? t(item.text) : item.text}
                        </Radio>
                      ))}
                    </Radio.Group>
                  )}
                  {item.type === CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO && (
                    <Radio.Group
                      type="button"
                      size="large"
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                      className={styles.pagePositionRadioGroup}
                    >
                      {item.range.map((item: any) => (
                        <Radio key={item.key} value={item.value} className={styles.pagePositionRadio}>
                          {item.text}
                        </Radio>
                      ))}
                    </Radio.Group>
                  )}
                  {item.type === CONFIG_TYPES.TABLE_PAGE_SIZE && (
                    <Input
                      type="number"
                      size="large"
                      value={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                </FormItem>
              );
            }
            if (item.type === CONFIG_TYPES.FIELD_DATA) {
              return (
                <DynamicFieldConfig key={index} handlePropsChange={handlePropsChange} item={item} configs={configs} />
              );
            }

            if (item.type === CONFIG_TYPES.TABLE_DATA) {
              return (
                <DynamicTableConfig
                  key={index}
                  id={cpID}
                  handleMultiPropsChange={handleMultiPropsChange}
                  handlePropsChange={handlePropsChange}
                  item={item}
                  configs={configs}
                />
              );
            }
            if (item.type === CONFIG_TYPES.SWITCH_INPUT) {
              return (
                <FormItem
                  key={index}
                  label={
                    <div
                      style={{
                        textAlign: 'left'
                      }}
                    >
                      <span>{item.name}</span>
                    </div>
                  }
                  labelCol={{
                    span: 21
                  }}
                  wrapperCol={{
                    span: 1
                  }}
                  layout="horizontal"
                  className={styles.formItem}
                >
                  {item.type === CONFIG_TYPES.SWITCH_INPUT && (
                    <Switch
                      size="small"
                      checked={configs[item.key]}
                      onChange={(value) => {
                        handlePropsChange(item.key, value);
                      }}
                    />
                  )}
                </FormItem>
              );
            }
          })}
        </Form>
      )}
    </div>
  );
};

export default Attributes;

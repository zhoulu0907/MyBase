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
  Tabs,
  Tooltip
} from '@arco-design/web-react';
import { IconCopy } from '@arco-design/web-react/icon';
import { CONFIG_TYPES, usePageEditorSignal, getPopupContainer } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useState } from 'react';
import DynamicAutoCodeConfig from './components/DynamicAutoCodeConfig';
import DynamicCarouselConfig from './components/DynamicCarouselConfig';
import DynamicCheckboxConfig from './components/DynamicCheckboxConfig';
import DynamicDataSourceConfig from './components/DynamicDataSourceConfig';
import DynamicFieldConfig from './components/DynamicFieldConfig';
import DynamicFileConfig from './components/DynamicFileConfig';
import DynamicImageConfig from './components/DynamicImageConfig';
import DynamicImageHandleConfig from './components/DynamicImageHandleConfig';
import DynamicOptionsConfig from './components/DynamicOptionsConfig';
import DynamicRadioConfig from './components/DynamicRadioConfig';
import DynamicRelatedFormConfig from './components/DynamicRelatedFormConfig';
import DynamicTableConfig from './components/DynamicTableConfig';
import DynamicTabsConfig from './components/DynamicTabsConfig';
import DynamicDateFormatConfig from './components/DynamicDateFormatConfig';
import DynamicTimeFormatConfig from './components/DynamicTimeFormatConfig';
import DynamicSwitchFillTextConfig from './components/DynamicSwitchFillTextConfig';
import DynamicDefaultValueConfig from './components/DynamicDefaultValueConfig';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const FormItem = Form.Item;
const Option = Select.Option;
const TabPane = Tabs.TabPane;

/**
 * 属性配置面板组件
 * @param props.cpID 组件唯一ID
 */
interface ConfigsProps {
  cpID: string;
}

const securityOptions = [
  {
    label: '姓名',
    value: 'name'
  },
  {
    label: '手机号',
    value: 'phone'
  },
  {
    label: '邮箱',
    value: 'email'
  },
  {
    label: '金额',
    value: 'money'
  },
  {
    label: '身份证号',
    value: 'id'
  },
  {
    label: '住址',
    value: 'address'
  },
  {
    label: 'IP地址',
    value: 'ip'
  },
  {
    label: '车牌号',
    value: 'car_id'
  }
];

const Attributes = ({ cpID }: ConfigsProps) => {
  const { t } = useI18n();

  useSignals();
  const { curComponentID, curComponentSchema, setCurComponentSchema, setPageComponentSchemas, subTableComponents } =
    usePageEditorSignal();

  const [editData, setEditData] = useState<any>([]);
  const [configs, setConfigs] = useState<any>({});
  const [isInSubTable, setIsInSubTable] = useState<boolean>(false);

  useEffect(() => {
    if (!cpID) {
      return;
    }
    // console.debug('curComponentSchema------', curComponentSchema);

    setEditData(curComponentSchema.editData);
    setConfigs(curComponentSchema.config);
  }, [cpID, curComponentSchema]);

  useEffect(() => {
    const keys = Object.keys(subTableComponents) || [];
    let inSubTable = false;
    for (let key of keys) {
      for (let item of subTableComponents[key] || []) {
        if (item.id === curComponentID) {
          inSubTable = true;
        }
      }
    }
    setIsInSubTable(inSubTable);
  }, [curComponentID]);

  const handlePropsChange = (key: string, value: any) => {
    console.log(`更新了属性: ${key} 值为: `, value);

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
    console.log(`更新了属性: config值为: `, config);

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

  const renderEditItem = (item: any, index: number) => {
    switch (item.type) {
      case CONFIG_TYPES.RELATED_FORM_DATA:
        // 关联表单
        return <DynamicRelatedFormConfig handlePropsChange={handlePropsChange} item={item} configs={configs} />;
      case CONFIG_TYPES.FIELD_DATA:
        // 数据绑定
        return (
          <DynamicFieldConfig
            handlePropsChange={handlePropsChange}
            handleConfigsChange={handleConfigsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.TABLE_DATA:
        // 数据
        return (
          <DynamicTableConfig
            id={cpID}
            handleMultiPropsChange={handleMultiPropsChange}
            handlePropsChange={handlePropsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.RADIO_DATA:
        // 自定义选项 radio
        return (
          <DynamicRadioConfig
            id={cpID}
            // handleMultiPropsChange={handleMultiPropsChange}
            handlePropsChange={handlePropsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.CHECKBOX_DATA:
        // 自定义选项 checkbox
        return (
          <DynamicCheckboxConfig
            id={cpID}
            // handleMultiPropsChange={handleMultiPropsChange}
            handlePropsChange={handlePropsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.SELECT_OPTIONS_INPUT:
        // 自定义选项 select
        return <DynamicOptionsConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />;
      case CONFIG_TYPES.CAROUSEL:
        // 轮播
        return <DynamicCarouselConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />;
      case CONFIG_TYPES.SWITCH_INPUT:
        // 开关
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
      case CONFIG_TYPES.SELECT_DATA_SOURCE:
        // 选择数据配置
        return (
          <DynamicDataSourceConfig
            id={cpID}
            handlePropsChange={handlePropsChange}
            handleMultiPropsChange={handleMultiPropsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.IMAGE_HANDLE:
        // 图片处理
        return (
          <DynamicImageHandleConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />
        );
      case CONFIG_TYPES.IMAGE:
        // 图片
        return <DynamicImageConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />;
      case CONFIG_TYPES.FILE:
        // 文件
        return <DynamicFileConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />;
      case CONFIG_TYPES.TABS:
        // 页签数据配置
        return (
          <DynamicTabsConfig
            id={cpID}
            handlePropsChange={handlePropsChange}
            handleMultiPropsChange={handleMultiPropsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.AUTO_CODE_RULES:
        // 自动编号规则配置
        return (
          <DynamicAutoCodeConfig
            id={cpID}
            handlePropsChange={handlePropsChange}
            handleConfigsChange={handleConfigsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.DATE_FORMAT:
        // 日期格式
        return (
          <DynamicDateFormatConfig
            id={cpID}
            handlePropsChange={handlePropsChange}
            handleConfigsChange={handleConfigsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.TIME_FORMAT:
        // 时间格式
        return (
          <DynamicTimeFormatConfig
            id={cpID}
            handlePropsChange={handlePropsChange}
            handleConfigsChange={handleConfigsChange}
            item={item}
            configs={configs}
          />
        );
      case CONFIG_TYPES.SWITCH_FILL_TEXT:
        // 填充文本 switch
        return (
          <DynamicSwitchFillTextConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />
        );
      case CONFIG_TYPES.DEFAULT_VALUE:
        // 默认值
        return (
          <DynamicDefaultValueConfig id={cpID} handlePropsChange={handlePropsChange} item={item} configs={configs} />
        );
      case CONFIG_TYPES.LABEL_INPUT:
        // 显示标题
        return (
          <FormItem
            className={styles.formItem}
            label={
              <>
                {item.name}
                {!isInSubTable && typeof configs[item.key]['display'] === 'boolean' && (
                  <Checkbox
                    checked={configs[item.key]['display']}
                    style={{ float: 'right' }}
                    onChange={(value) => {
                      handlePropsChange(item.key, { ...configs[item.key], display: value });
                    }}
                  >
                    显示标题
                  </Checkbox>
                )}
              </>
            }
          >
            <Input
              placeholder={`请输入${item.name}`}
              value={configs[item.key]['text']}
              onChange={(value) => {
                handlePropsChange(item.key, { ...configs[item.key], text: value });
              }}
            />
          </FormItem>
        );
      case CONFIG_TYPES.SECURITY:
        // 掩码显示
        return (
          <FormItem
            className={styles.formItem}
            label={
              <>
                {item.name}
                <Checkbox
                  checked={configs[item.key]['display']}
                  style={{ float: 'right' }}
                  onChange={(value) => {
                    handlePropsChange(item.key, { ...configs[item.key], display: value });
                  }}
                >
                  掩码显示
                </Checkbox>
              </>
            }
          >
            <Select
              addBefore="掩码方式"
              defaultValue={configs[item.key]['type']}
              getPopupContainer={getPopupContainer}
              onChange={(value) => handlePropsChange(item.key, { ...configs[item.key], type: value })}
            >
              {securityOptions.map((option, index) => (
                <Option key={index} value={option.value}>
                  {option.label}
                </Option>
              ))}
            </Select>
          </FormItem>
        );
      case CONFIG_TYPES.TEXT_INPUT:
      case CONFIG_TYPES.PLACEHOLDER_INPUT:
      case CONFIG_TYPES.UPLOAD_COMPRESS:
        // 文本输入
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
      case CONFIG_TYPES.LABEL_COL_SPAN:
        // 数字
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
        // 字段描述
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
        // 颜色
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
        // 单选
        return (
          <FormItem className={styles.formItem} label={item.name}>
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
          </FormItem>
        );
      case CONFIG_TYPES.TABS_TYPE:
        // 页签样式
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Select
              defaultValue={configs[item.key]}
              getPopupContainer={getPopupContainer}
              onChange={(value) => handlePropsChange(item.key, value)}
            >
              {item.range.map((item: any) => (
                <Option key={item.key} value={item.value}>
                  <Tabs size="mini" defaultActiveTab="1" type={item.value} style={{ pointerEvents: 'none' }}>
                    <TabPane key="1" title="标签页1" />
                    <TabPane key="2" title="标签页2" />
                  </Tabs>
                </Option>
              ))}
            </Select>
          </FormItem>
        );
      case CONFIG_TYPES.TABS_POSITION:
        // 页签位置
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Select
              defaultValue={configs[item.key]}
              getPopupContainer={getPopupContainer}
              onChange={(value) => handlePropsChange(item.key, value)}
            >
              {item.range.map((item: any) => (
                <Option key={item.key} value={item.value}>
                  {item.label}
                </Option>
              ))}
            </Select>
          </FormItem>
        );
      case CONFIG_TYPES.STATUS_RADIO:
      case CONFIG_TYPES.DATE_TYPE:
      case CONFIG_TYPES.FORM_LAYOUT:
      case CONFIG_TYPES.COLLAPSED:
      case CONFIG_TYPES.TEXT_ALIGN:
        // 组件状态 对齐方式 布局方式  按钮类型的radio
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
          </FormItem>
        );
      case CONFIG_TYPES.COLUMN_COUNT_RADIO:
        return (
          <FormItem className={styles.formItem} label={item.name}>
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
          </FormItem>
        );
      case CONFIG_TYPES.TABLE_PAGE_POSITION_RADIO:
        return (
          <FormItem className={styles.formItem} label={item.name}>
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
          </FormItem>
        );
      case CONFIG_TYPES.TABLE_PAGE_SIZE:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Input
              type="number"
              size="large"
              value={configs[item.key]}
              onChange={(value) => {
                if (!value) return;
                handlePropsChange(item.key, value);
              }}
            />
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
      case CONFIG_TYPES.VERIFY:
        return (
          <FormItem className={styles.formItem} label={item.name}>
            <Row>
              <Col flex="auto" style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
                <Checkbox
                  checked={configs[item.key]['required']}
                  onChange={(value) => {
                    handlePropsChange(item.key, { ...configs[item.key], required: value });
                  }}
                >
                  必填
                </Checkbox>
                {typeof configs[item.key]['noRepeat'] === 'boolean' && (
                  <Checkbox
                    checked={configs[item.key]['noRepeat']}
                    onChange={(value) => {
                      handlePropsChange(item.key, { ...configs[item.key], noRepeat: value });
                    }}
                  >
                    不允许重复
                  </Checkbox>
                )}
                {typeof configs[item.key]['min'] === 'number' && (
                  <InputNumber
                    value={configs[item.key]['min']}
                    prefix="最小值"
                    onChange={(value) => {
                      if (value > configs[item.key]['max']) return;
                      handlePropsChange(item.key, { ...configs[item.key], min: value });
                    }}
                  />
                )}
                {typeof configs[item.key]['max'] === 'number' && (
                  <InputNumber
                    value={configs[item.key]['max']}
                    prefix="最大值"
                    onChange={(value) => {
                      if (value < configs[item.key]['min']) return;
                      handlePropsChange(item.key, { ...configs[item.key], max: value });
                    }}
                  />
                )}
                {typeof configs[item.key]['maxChecked'] === 'number' && (
                  <InputNumber
                    value={configs[item.key]['maxChecked']}
                    min={0}
                    prefix="可选数量限制"
                    onChange={(value) => {
                      if (!value) return;
                      handlePropsChange(item.key, { ...configs[item.key], maxChecked: value });
                    }}
                  />
                )}
                {typeof configs[item.key]['maxCount'] === 'number' && (
                  <InputNumber
                    value={configs[item.key]['maxCount']}
                    min={-1}
                    prefix="上传数量限制"
                    onChange={(value) => {
                      if (typeof value !== 'number') return;
                      handlePropsChange(item.key, { ...configs[item.key], maxCount: value });
                    }}
                  />
                )}
                {typeof configs[item.key]['maxSize'] === 'number' && (
                  <InputNumber
                    value={configs[item.key]['maxSize']}
                    min={0}
                    prefix="大小限制"
                    suffix={configs['verify']['maxSize'] ? 'MB' : ''}
                    onChange={(value) => {
                      if (!value) return;
                      handlePropsChange(item.key, { ...configs[item.key], maxSize: value });
                    }}
                  />
                )}
                {typeof configs[item.key]['fileFormat'] === 'string' && (
                  <Input
                    placeholder={`请输入支持文件格式，用英文逗号分隔`}
                    value={configs[item.key]['fileFormat']}
                    onChange={(value) => {
                      if (!value) return;
                      handlePropsChange(item.key, { ...configs[item.key], fileFormat: value });
                    }}
                  />
                )}
              </Col>
            </Row>
          </FormItem>
        );
      case CONFIG_TYPES.NUMBER_FORMAT:
        return (
          <FormItem className={styles.formItem} label={item.name}>
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
            <Input
              value={cpID}
              suffix={
                <Tooltip content="复制">
                  <IconCopy style={{ cursor: 'pointer' }} />
                </Tooltip>
              }
            />
          </FormItem>

          {editData
            .filter((item: any) => !item.advanced)
            .map((item: any, index: number) => (
              <div key={index}>{renderEditItem(item, index)}</div>
            ))}
        </Form>
      )}
    </div>
  );
};

export default Attributes;

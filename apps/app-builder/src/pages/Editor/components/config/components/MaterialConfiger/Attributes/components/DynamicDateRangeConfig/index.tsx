import { Form, Grid, Checkbox, Select, DatePicker, Message } from '@arco-design/web-react';
import { useEffect, useState } from 'react';
import { WEEK_OPTIONS, WEEK_OPTIONS_LABEL, DATE_EXTREME_TYPE, DATE_DYNAMIC_TYPE } from '@onebase/ui-kit';
import styles from '../../index.module.less';

export interface DynamicDateRangeConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicDateRangeConfig: React.FC<DynamicDateRangeConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const dateRangeKey = 'dateRange';

  const [checkAll, setCheckAll] = useState(false);
  const [indeterminate, setIndeterminate] = useState(false);

  // 特定星期  星期一/星期二/星期三/星期四/星期五/星期六/星期日
  const weekOptions = [
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.MONDAY], value: WEEK_OPTIONS.MONDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.TUESDAY], value: WEEK_OPTIONS.TUESDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.WEDNESDAY], value: WEEK_OPTIONS.WEDNESDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.THURSDAY], value: WEEK_OPTIONS.THURSDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.FRIDAY], value: WEEK_OPTIONS.FRIDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.SATURDAY], value: WEEK_OPTIONS.SATURDAY },
    { label: WEEK_OPTIONS_LABEL[WEEK_OPTIONS.SUNDAY], value: WEEK_OPTIONS.SUNDAY }
  ];

  // 最早可选日期、最晚可选日期
  const extremeOptions = [
    { label: '静态值', value: DATE_EXTREME_TYPE.STATIC },
    { label: '动态值', value: DATE_EXTREME_TYPE.DYNAMIC }
    // { label: '变量', value: DATE_EXTREME_TYPE.VARIABLE }
  ];

  // 动态值：下拉单选 当天/昨天/明天/7天前/7天后/30天前/30天后
  const dynamicOptions = [
    { label: '当天', value: DATE_DYNAMIC_TYPE.TODAY },
    { label: '昨天', value: DATE_DYNAMIC_TYPE.YESTERDAY },
    { label: '明天', value: DATE_DYNAMIC_TYPE.TOMORROW },
    { label: '7天前', value: DATE_DYNAMIC_TYPE.BEFOREWEEK },
    { label: '7天后', value: DATE_DYNAMIC_TYPE.AFTERWEEK },
    { label: '30天前', value: DATE_DYNAMIC_TYPE.BEFOREMONTH },
    { label: '30天后', value: DATE_DYNAMIC_TYPE.AFTERMONTH }
  ];

  useEffect(() => {
    getIndeterminate(configs[dateRangeKey]?.week);
  }, []);

  // 获取选中状态
  const getIndeterminate = (week?: string[]) => {
    const newLength = week?.length || 0;
    if (newLength === 0) {
      setCheckAll(false);
      setIndeterminate(false);
    } else if (newLength === weekOptions.length) {
      setCheckAll(true);
      setIndeterminate(false);
    } else {
      setCheckAll(false);
      setIndeterminate(true);
    }
  };
  const handleAllChange = (checked: boolean) => {
    if (checked) {
      setIndeterminate(false);
      setCheckAll(true);
      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], week: weekOptions.map((ele) => ele.value) });
    } else {
      setIndeterminate(false);
      setCheckAll(false);
      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], week: [] });
    }
  };

  return (
    <>
      <Form.Item layout="vertical" label={item.name || '可选范围'} className={styles.formItem}>
        {typeof configs[dateRangeKey]['weekLimit'] === 'boolean' && (
          <div>
            <Checkbox
              checked={configs[dateRangeKey]['weekLimit']}
              onChange={(value) => {
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], weekLimit: value });
              }}
            >
              特定星期
            </Checkbox>
            <Select
              style={{ margin: '8px 0' }}
              placeholder="请选择"
              mode="multiple"
              value={configs[dateRangeKey]['week']}
              options={weekOptions}
              onChange={(value) => {
                getIndeterminate(value);
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], week: value });
              }}
              dropdownRender={(menu) => (
                <div>
                  <div style={{ borderBottom: '1px solid #e5e6eb', padding: '7px', marginBottom: '4px' }}>
                    <Checkbox checked={checkAll} indeterminate={indeterminate} onChange={handleAllChange}>
                      全部
                    </Checkbox>
                  </div>
                  {menu}
                </div>
              )}
            ></Select>
          </div>
        )}

        {typeof configs[dateRangeKey]['earliestLimit'] === 'boolean' && (
          <div>
            <Checkbox
              checked={configs[dateRangeKey]['earliestLimit']}
              onChange={(value) => {
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestLimit: value });
              }}
            >
              最早可选日期时间
            </Checkbox>
            <Grid.Row gutter={8} style={{ marginTop: '8px', marginBottom: '8px' }}>
              <Grid.Col span={8}>
                <Select
                  placeholder="请选择"
                  value={configs[dateRangeKey]['earliestType']}
                  options={extremeOptions}
                  onChange={(value) => {
                    handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestType: value });
                  }}
                ></Select>
              </Grid.Col>
              <Grid.Col span={16}>
                {/* 静态值 */}
                {configs[dateRangeKey]['earliestType'] === DATE_EXTREME_TYPE.STATIC && (
                  <DatePicker
                    format="YYYY-MM-DD"
                    value={configs[dateRangeKey]['earliestStaticValue']}
                    onChange={(value) => {
                      // 校验最早可选日期不得晚于最晚可选日期
                      if (configs[dateRangeKey].latestStaticValue) {
                        const latestTime = new Date(configs[dateRangeKey].latestStaticValue).getTime();
                        const earliestTime = new Date(value).getTime();
                        if (earliestTime > latestTime) {
                          Message.error('最早可选日期不得晚于最晚可选日期');
                          return;
                        }
                      }
                      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestStaticValue: value });
                    }}
                  ></DatePicker>
                )}

                {/* 动态值 */}
                {configs[dateRangeKey]['earliestType'] === DATE_EXTREME_TYPE.DYNAMIC && (
                  <Select
                    value={configs[dateRangeKey]['earliestDynamicValue']}
                    options={dynamicOptions}
                    onChange={(value) => {
                      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], earliestDynamicValue: value });
                    }}
                  ></Select>
                )}
              </Grid.Col>
            </Grid.Row>
          </div>
        )}

        {typeof configs[dateRangeKey]['latestLimit'] === 'boolean' && (
          <div>
            <Checkbox
              checked={configs[dateRangeKey]['latestLimit']}
              onChange={(value) => {
                handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestLimit: value });
              }}
            >
              最晚可选日期时间
            </Checkbox>
            <Grid.Row gutter={8} style={{ marginTop: '8px', marginBottom: '8px' }}>
              <Grid.Col span={8}>
                <Select
                  placeholder="请选择"
                  value={configs[dateRangeKey]['latestType']}
                  options={extremeOptions}
                  onChange={(value) => {
                    handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestType: value });
                  }}
                ></Select>
              </Grid.Col>
              <Grid.Col span={16}>
                {/* 静态值 */}
                {configs[dateRangeKey]['latestType'] === DATE_EXTREME_TYPE.STATIC && (
                  <DatePicker
                    format="YYYY-MM-DD"
                    value={configs[dateRangeKey]['latestStaticValue']}
                    onChange={(value) => {
                      // 校验最早可选日期不得晚于最晚可选日期 earliestLimit earliestLimit
                      if (configs[dateRangeKey].earliestStaticValue) {
                        const earliestTime = new Date(configs[dateRangeKey].earliestStaticValue).getTime();
                        const latestTime = new Date(value).getTime();
                        if (earliestTime > latestTime) {
                          Message.error('最早可选日期不得晚于最晚可选日期');
                          return;
                        }
                      }
                      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestStaticValue: value });
                    }}
                  ></DatePicker>
                )}

                {/* 动态值 */}
                {configs[dateRangeKey]['latestType'] === DATE_EXTREME_TYPE.DYNAMIC && (
                  <Select
                    value={configs[dateRangeKey]['latestDynamicValue']}
                    options={dynamicOptions}
                    onChange={(value) => {
                      handlePropsChange(dateRangeKey, { ...configs[dateRangeKey], latestDynamicValue: value });
                    }}
                  ></Select>
                )}
              </Grid.Col>
            </Grid.Row>
          </div>
        )}
      </Form.Item>
    </>
  );
};
export default DynamicDateRangeConfig;

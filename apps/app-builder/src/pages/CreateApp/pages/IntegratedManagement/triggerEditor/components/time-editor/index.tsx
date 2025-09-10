import { DatePicker, Form, Grid, Input, Select, Tabs, TimePicker } from '@arco-design/web-react';
import { isValidCron } from 'cron-validator';
import React, { useState } from 'react';
import styles from './index.module.less';

const TabPane = Tabs.TabPane;

/**
 * ConditionEditor 组件的 props 类型定义
 */
export interface TimeEditorProps {}

const repeatTypeOptions = [
  { label: '不重复', value: 'none' },
  { label: '每天', value: 'daily' },
  { label: '每周', value: 'weekly' },
  { label: '每月', value: 'monthly' },
  { label: '每年', value: 'yearly' },
  { label: '自定义', value: 'custom' }
];

const weeklyOptions = [
  { label: '周一', value: 'monday' },
  { label: '周二', value: 'tuesday' },
  { label: '周三', value: 'wednesday' },
  { label: '周四', value: 'thursday' },
  { label: '周五', value: 'friday' },
  { label: '周六', value: 'saturday' },
  { label: '周日', value: 'sunday' }
];

const monthlyOptions = [
  { label: '第一天', value: 'first' },
  { label: '每月最后一天', value: 'last' }
];

const dayOptions = [
  // 生成 01 到 31 的日期选项
  ...Array.from({ length: 31 }, (_, i) => {
    const day = (i + 1).toString().padStart(2, '0');
    return { label: day, value: day };
  })
];

/**
 * 条件编辑器组件初始化
 */
const TimeEditor: React.FC<TimeEditorProps> = ({}) => {
  const [form] = Form.useForm();

  const repeatType = Form.useWatch('repeat_type', form);
  const [cronValue, setCronValue] = useState('* * * * *');
  const [monthlyType, setMonthlyType] = useState('specified');

  return (
    <div className={styles.conditionWrapper}>
      <Form form={form} layout="horizontal">
        <Grid.Row gutter={8}>
          <Grid.Col span={12}>
            <Form.Item
              label="开始时间"
              layout="vertical"
              field="startTime"
              rules={[{ required: true, message: '请选择结束时间' }]}
            >
              <DatePicker showTime format="YYYY-MM-DD HH:mm:ss " style={{ width: '100%' }} />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            <Form.Item
              label="结束时间"
              layout="vertical"
              field="endTime"
              rules={[{ required: true, message: '请选择结束时间' }]}
            >
              <DatePicker showTime format="YYYY-MM-DD HH:mm:ss " style={{ width: '100%' }} />
            </Form.Item>
          </Grid.Col>
        </Grid.Row>
        <Grid.Row gutter={8} align="end">
          <Grid.Col span={12}>
            <Form.Item
              label="重复周期"
              field="repeat_type"
              layout="vertical"
              rules={[{ required: true, message: '请选择重复周期' }]}
              labelCol={{ span: 10 }} // 调小 label 占比
              wrapperCol={{ span: 14 }} // 明确 wrapper 占比
            >
              <Select
                options={repeatTypeOptions}
                style={{ width: '100%' }}
                onChange={(value) => {
                  console.log(value);
                  form.clearFields(['triggerTime', 'repeat_week', 'repeat_day', 'trigger_date']);
                }}
              />
            </Form.Item>
          </Grid.Col>
          <Grid.Col span={12}>
            {repeatType === 'none' && (
              <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择触发时间' }]}>
                <DatePicker showTime format="YYYY-MM-DD HH:mm:ss " />
              </Form.Item>
            )}

            {repeatType === 'daily' && (
              <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择触发时间' }]}>
                <TimePicker format="HH:mm" style={{ width: '280px' }} />
              </Form.Item>
            )}

            {repeatType === 'weekly' && (
              <Form.Item field="repeat_week">
                <Select
                  options={weeklyOptions}
                  mode="multiple"
                  maxTagCount={3}
                  style={{ width: '280px' }}
                  placeholder="请选择日期"
                />
              </Form.Item>
            )}

            {repeatType === 'monthly' && (
              <Form.Item field="repeat_day">
                <Select
                  options={monthlyType === 'specified' ? dayOptions : monthlyOptions}
                  placeholder="请选择日期"
                  mode="multiple"
                  maxTagCount={3}
                  style={{ width: '280px' }}
                  dropdownRender={(menu) => (
                    <div>
                      <Tabs defaultActiveTab="specified" onChange={(value) => setMonthlyType(value)}>
                        <TabPane key="specified" title="指定日期">
                          {menu}
                        </TabPane>
                        <TabPane key="relative" title="相对日期">
                          {menu}
                        </TabPane>
                      </Tabs>
                    </div>
                  )}
                />
              </Form.Item>
            )}

            {repeatType === 'yearly' && (
              <>
                <Form.Item field="trigger_date" rules={[{ required: true, message: '请选择触发时间' }]}>
                  <DatePicker style={{ width: '280px' }} />
                </Form.Item>
              </>
            )}

            {repeatType === 'custom' && (
              <Form.Item
                field="triggerTime"
                rules={[
                  { required: true, message: '请选择触发时间' },
                  {
                    validator: (value, callback) => {
                      console.log(isValidCron(value));
                      if (!isValidCron(value)) {
                        return callback('请输入正确的cron表达式');
                      }
                    }
                  }
                ]}
              >
                <Input
                  value={cronValue}
                  onChange={(value) => {
                    setCronValue(value);
                  }}
                  style={{ width: '280px' }}
                  placeholder="请输入cron表达式"
                />
              </Form.Item>
            )}
          </Grid.Col>
        </Grid.Row>

        <Grid.Row gutter={8}>
          {repeatType === 'monthly' ||
            repeatType === 'yearly' ||
            (repeatType === 'weekly' && (
              <Form.Item
                label="触发时间"
                layout="vertical"
                field="triggerTime"
                rules={[{ required: true, message: '请选择触发时间' }]}
              >
                <TimePicker format="HH:mm" />
              </Form.Item>
            ))}
        </Grid.Row>
      </Form>
    </div>
  );
};

export default TimeEditor;

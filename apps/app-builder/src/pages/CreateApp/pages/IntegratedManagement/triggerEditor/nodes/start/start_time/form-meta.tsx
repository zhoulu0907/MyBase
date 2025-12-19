import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { DatePicker, Form, Grid, Input, Select, Tabs, TimePicker } from '@arco-design/web-react';
import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { isValidCron } from 'cron-validator';
import { useState } from 'react';
import { FormContent, FormHeader, FormOutputs } from '../../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../../hooks';
import { type FlowNodeJSON } from '../../../typings';

const TabPane = Tabs.TabPane;

const REPEAT_TYPE_OPTIONS = {
  DAY: 'day',
  WEEK: 'week',
  MONTH: 'month',
  YEAR: 'year',
  CRON: 'cron',
  NONE: 'none'
};

const repeatTypeOptions = [
  { label: '不重复', value: REPEAT_TYPE_OPTIONS.NONE },
  { label: '每天', value: REPEAT_TYPE_OPTIONS.DAY },
  { label: '每周', value: REPEAT_TYPE_OPTIONS.WEEK },
  { label: '每月', value: REPEAT_TYPE_OPTIONS.MONTH },
  { label: '每年', value: REPEAT_TYPE_OPTIONS.YEAR },
  { label: '自定义', value: REPEAT_TYPE_OPTIONS.CRON }
];

const weeklyOptions = [
  { label: '周一', value: 'MON' },
  { label: '周二', value: 'TUE' },
  { label: '周三', value: 'WED' },
  { label: '周四', value: 'THU' },
  { label: '周五', value: 'FRI' },
  { label: '周六', value: 'SAT' },
  { label: '周日', value: 'SUN' }
];

const monthlyOptions = [
  //   { label: '第一天', value: 'first' },
  { label: '每月最后一天', value: 'last' }
];

const dayOptions = [
  // 生成 01 到 31 的日期选项
  ...Array.from({ length: 31 }, (_, i) => {
    const day = (i + 1).toString().padStart(2, '0');
    return { label: day, value: day };
  })
];

export const renderForm = ({ form }: FormRenderProps<FlowNodeJSON['data']>) => {
  const isSidebar = useIsSidebar();
  const { node } = useNodeRenderContext();

  const [payloadForm] = Form.useForm();
  const repeatType = Form.useWatch('repeatType', payloadForm);
  const [cronValue, setCronValue] = useState('* * * * *');
  const [monthlyType, setMonthlyType] = useState('specified');

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            layout="vertical"
            requiredSymbol={{ position: 'end' }}
          >
            <Grid.Row>
              <Grid.Col span={12}>
                <Form.Item label="节点ID" field="id" initialValue={node.id} rules={[{ required: true }]}>
                  <Input disabled />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>

            <Grid.Row gutter={8}>
              <Grid.Col span={12}>
                <Form.Item
                  label="开始时间"
                  layout="vertical"
                  field="startTime"
                  rules={[{ required: true, message: '请选择结束时间' }]}
                >
                  <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%' }} />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={12}>
                <Form.Item
                  label="结束时间"
                  layout="vertical"
                  field="endTime"
                  rules={[{ required: true, message: '请选择结束时间' }]}
                >
                  <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%' }} />
                </Form.Item>
              </Grid.Col>
            </Grid.Row>
            <Grid.Row gutter={8} align="end">
              <Grid.Col span={12}>
                <Form.Item
                  label="重复周期"
                  field="repeatType"
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
                      payloadForm.clearFields(['triggerTime', 'repeatWeek', 'repeatDay', 'triggerDate']);
                    }}
                  />
                </Form.Item>
              </Grid.Col>
              <Grid.Col span={12}>
                {repeatType === REPEAT_TYPE_OPTIONS.NONE && (
                  <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择触发时间' }]}>
                    <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" />
                  </Form.Item>
                )}

                {repeatType === REPEAT_TYPE_OPTIONS.DAY && (
                  <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择触发时间' }]}>
                    <TimePicker format="HH:mm" style={{ width: '280px' }} />
                  </Form.Item>
                )}

                {repeatType === REPEAT_TYPE_OPTIONS.WEEK && (
                  <Form.Item field="repeatWeek">
                    <Select
                      options={weeklyOptions}
                      mode="multiple"
                      maxTagCount={3}
                      style={{ width: '280px' }}
                      placeholder="请选择日期"
                    />
                  </Form.Item>
                )}

                {repeatType === REPEAT_TYPE_OPTIONS.MONTH && (
                  <Form.Item field="repeatDay">
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

                {repeatType === REPEAT_TYPE_OPTIONS.YEAR && (
                  <>
                    <Form.Item field="triggerDate" rules={[{ required: true, message: '请选择触发时间' }]}>
                      <DatePicker style={{ width: '280px' }} format="MM-DD" />
                    </Form.Item>
                  </>
                )}

                {repeatType === REPEAT_TYPE_OPTIONS.CRON && (
                  <Form.Item
                    field="triggerTime"
                    rules={[
                      { required: true, message: '请选择触发时间' },
                      {
                        validator: (value, callback) => {
                          if (
                            !isValidCron(value, {
                              seconds: true,
                              allowBlankDay: true,
                              alias: true,
                              allowSevenAsSunday: true
                            })
                          ) {
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

            {repeatType !== REPEAT_TYPE_OPTIONS.DAY &&
              repeatType !== REPEAT_TYPE_OPTIONS.NONE &&
              repeatType !== REPEAT_TYPE_OPTIONS.CRON && (
                <Grid.Row gutter={8}>
                  <Form.Item
                    label="触发时间"
                    layout="vertical"
                    field="triggerTime"
                    rules={[{ required: true, message: '请选择触发时间' }]}
                  >
                    <TimePicker format="HH:mm" />
                  </Form.Item>
                </Grid.Row>
              )}
          </Form>
        </FormContent>
      ) : (
        <FormContent>
          <FormOutputs />
        </FormContent>
      )}
    </>
  );
};

export const formMeta: FormMeta<FlowNodeJSON['data']> = {
  render: renderForm
  //   validateTrigger: ValidateTrigger.onChange,
  //   validate: {
  //     title: ({ value }: { value: string }) => (value ? undefined : 'Title is required')
  //   },
  //   effect: {
  //     title: syncVariableTitle,
  //     outputs: provideJsonSchemaOutputs
  //   }
};

import { type FormMeta, type FormRenderProps } from '@flowgram.ai/fixed-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Form, Grid, Input, DatePicker, Select, Tabs, TimePicker } from '@arco-design/web-react';
import TimeEditor from '../../components/time-editor';
import { FormContent, FormHeader, FormOutputs } from '../../form-components';
import { useIsSidebar, useNodeRenderContext } from '../../hooks';
import { type FlowNodeJSON } from '../../typings';
import { isValidCron } from 'cron-validator';
import { useState } from 'react';

const TabPane = Tabs.TabPane;
const repeatTypeOptions = [
  { label: '不重复', value: 'none' },
  { label: '每天', value: 'daily' },
  { label: '每周', value: 'weekly' },
  { label: '每月', value: 'monthly' },
  { label: '每年', value: 'yearly' },
  { label: '自定义', value: 'cron' }
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

  const handlePropsOnChange = (values: any) => {
    triggerEditorSignal.setNodeData(node.id, values);
  };

  const onValuesChange = (changeValue: any, values: any) => {
    console.log('onValuesChange: ', changeValue, values);

    handlePropsOnChange(values);
  };

  return (
    <>
      <FormHeader />
      {isSidebar ? (
        <FormContent>
          <Form
            form={payloadForm}
            initialValues={{ ...triggerEditorSignal.nodeData.value[node.id] }}
            onValuesChange={onValuesChange}
            layout="vertical"
          >
            <Grid.Row>
              <Grid.Col span={12}>
                <Form.Item label="节点ID" field="id" initialValue={node.id}>
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

                {repeatType === 'monthly' && (
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

                {repeatType === 'yearly' && (
                  <>
                    <Form.Item field="triggerDate" rules={[{ required: true, message: '请选择触发时间' }]}>
                      <DatePicker style={{ width: '280px' }} />
                    </Form.Item>
                  </>
                )}

                {repeatType === 'cron' && (
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

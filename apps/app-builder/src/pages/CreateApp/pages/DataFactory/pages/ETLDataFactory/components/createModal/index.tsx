import { DatePicker, Form, Grid, Input, Modal, Radio, Select, Switch, TimePicker } from '@arco-design/web-react';
import { ETL_SCHEDULE_STRATEGY } from '@onebase/app';
import { isValidCron } from 'cron-validator';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;

const ScheduleStrategyOptions = [
  { label: '定时更新-按设定时间自动更新', value: ETL_SCHEDULE_STRATEGY.FIXED },
  //   { label: '源表更新-当源表数据变化时更新', value: ETL_SCHEDULE_STRATEGY.OBSERVE },
  { label: '手动更新-需要手动触发更新', value: ETL_SCHEDULE_STRATEGY.MANUALLY }
];

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

const dayOptions = [
  // 生成 01 到 31 的日期选项
  ...Array.from({ length: 31 }, (_, i) => {
    const day = (i + 1).toString().padStart(2, '0');
    return { label: day, value: day };
  })
];

export interface CreateModalProps {
  visible: boolean; // 控制弹窗显示/隐藏
  onOk?: () => void; // 确认/提交回调
  onCancel?: () => void; // 取消/关闭回调
}

const CreateModal: React.FC<CreateModalProps> = ({ visible, onOk, onCancel }) => {
  // 控制弹窗显示隐藏

  // 表单提交事件
  const handleSubmit = () => {
    // 在这里处理表单提交逻辑
    // 校验后提交数据
    // submitData(formData).then(() => closeModal());
    onOk?.();
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel?.();
  };

  const [form] = Form.useForm();
  const scheduleStrategy = Form.useWatch('scheduleStrategy', form);
  const repeatType = Form.useWatch('repeatType', form);
  const enableStatus = Form.useWatch('enableStatus', form);

  const [cronValue, setCronValue] = useState<string>('* * * * *');

  useEffect(() => {
    if (scheduleStrategy === ETL_SCHEDULE_STRATEGY.FIXED && repeatType === undefined) {
      form.setFieldsValue({
        repeatType: REPEAT_TYPE_OPTIONS.NONE
      });
    }
  }, [scheduleStrategy]);

  return (
    <Modal
      title="新建流程"
      visible={visible}
      onOk={handleSubmit}
      onCancel={handleCancel}
      okText="提交"
      cancelText="取消"
      unmountOnExit
    >
      <div className={styles.createETLFlowModal}>
        <Form layout="vertical" form={form}>
          <Row>
            <Form.Item label="流程名称" field="flowName" rules={[{ required: true, message: '请输入流程名称' }]}>
              <Input />
            </Form.Item>
          </Row>
          <Row>
            <Form.Item
              label="调度策略"
              field="scheduleStrategy"
              rules={[{ required: true, message: '请选择调度策略' }]}
            >
              <Radio.Group direction="vertical" options={ScheduleStrategyOptions} />
            </Form.Item>
          </Row>

          {scheduleStrategy === ETL_SCHEDULE_STRATEGY.FIXED && (
            <>
              <Row align="end" gutter={8}>
                <Col span={12}>
                  <Form.Item label="定时更新设置" field="repeatType">
                    <Select options={repeatTypeOptions} />
                  </Form.Item>
                </Col>

                <Col span={12}>
                  {repeatType === REPEAT_TYPE_OPTIONS.NONE && (
                    <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择日期' }]}>
                      <DatePicker showTime format="YYYY-MM-DD HH:mm:ss" style={{ width: '100%' }} />
                    </Form.Item>
                  )}

                  {repeatType === REPEAT_TYPE_OPTIONS.DAY && (
                    <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择时间' }]}>
                      <TimePicker format="HH:mm" style={{ width: '100%' }} />
                    </Form.Item>
                  )}

                  {repeatType === REPEAT_TYPE_OPTIONS.WEEK && (
                    <Form.Item field="repeatWeek">
                      <Select
                        options={weeklyOptions}
                        mode="multiple"
                        maxTagCount={3}
                        style={{ width: '100%' }}
                        placeholder="请选择日期"
                      />
                    </Form.Item>
                  )}

                  {repeatType === REPEAT_TYPE_OPTIONS.MONTH && (
                    <Form.Item field="repeatDay">
                      <Select
                        options={dayOptions}
                        placeholder="请选择日期"
                        mode="multiple"
                        maxTagCount={3}
                        style={{ width: '100%' }}
                      />
                    </Form.Item>
                  )}

                  {repeatType === REPEAT_TYPE_OPTIONS.YEAR && (
                    <>
                      <Form.Item field="triggerDate" rules={[{ required: true, message: '请选择触发时间' }]}>
                        <DatePicker style={{ width: '100%' }} format="MM-DD" />
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
                            console.log(isValidCron(value, { seconds: true }));
                            if (!isValidCron(value, { seconds: true })) {
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
                        style={{ width: '100%' }}
                        placeholder="请输入cron表达式"
                      />
                    </Form.Item>
                  )}
                </Col>
              </Row>
              {repeatType !== REPEAT_TYPE_OPTIONS.DAY &&
                repeatType !== REPEAT_TYPE_OPTIONS.NONE &&
                repeatType !== REPEAT_TYPE_OPTIONS.CRON && (
                  <Row gutter={8}>
                    <Col span={12}>
                      <Form.Item field="triggerTime" rules={[{ required: true, message: '请选择触发时间' }]}>
                        <TimePicker format="HH:mm" style={{ width: '100%' }} />
                      </Form.Item>
                    </Col>
                  </Row>
                )}
            </>
          )}

          {scheduleStrategy === ETL_SCHEDULE_STRATEGY.MANUALLY && (
            <Row>
              <Form.Item label="手动更新规则">
                点击数据流卡片上的“<span style={{ color: '#00B42A' }}>立即更新</span>”按钮进行手动更新
              </Form.Item>
            </Row>
          )}

          <Row align="start">
            <Col span={6}>
              <Form.Item
                label="启用状态"
                field="enableStatus"
                layout="horizontal"
                triggerPropName="checked"
                labelCol={{ span: 15 }}
                wrapperCol={{ span: 9 }}
              >
                <Switch size="small" />
              </Form.Item>
            </Col>
            <Col span={18}>
              <div style={{ lineHeight: '32px' }}>{enableStatus ? '启用' : '未启用'}</div>
            </Col>
          </Row>
        </Form>
      </div>
    </Modal>
  );
};

export default CreateModal;

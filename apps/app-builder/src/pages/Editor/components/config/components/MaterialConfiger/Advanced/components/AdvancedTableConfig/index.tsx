import checkIcon from '@/assets/images/check_icon.svg';
import DynamicIcon from '@/components/DynamicIcon';
import {
  Button,
  Divider,
  Form,
  Grid,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Radio,
  Select,
  Space,
  Switch,
  Tooltip,
  Typography
} from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit, IconExclamationCircle } from '@arco-design/web-react/icon';
import { IconEditStroked } from '@douyinfe/semi-icons';
import type { PageView } from '@onebase/app';
import {
  getPopupContainer,
  iconColorList,
  iconMap,
  operationIcon,
  RedirectMethod,
  TableOperationButton,
  TableOperationButtonStyle,
  usePageViewEditorSignal
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import styles from '../../index.module.less';

const Row = Grid.Row;
const Col = Grid.Col;
const RadioGroup = Radio.Group;

interface OperationButtonConfig {
  type: string;
  buttonName: string;
  buttonIcon: string;
  iconColor: string;
  redirectPageId?: string;
  redirectMethod?: string;
  confirmText?: string;
  deletedAction?: string;
  display: boolean;
}

export interface AdvancedTableOperationConfigProps {
  handleMultiPropsChange: (updates: { key: string; value: string | number | boolean | any[] }[]) => void;
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const openTypeOptions = [
  { label: '侧边栏', value: RedirectMethod.DRAWER },
  { label: '新页签', value: RedirectMethod.NEW_TAB }
];

// 按钮显示方式
const buttonIconOptions = [
  {
    label: '图标',
    value: TableOperationButtonStyle.ICON
  },
  {
    label: '文字',
    value: TableOperationButtonStyle.TEXT
  },
  {
    label: '图标+文字',
    value: TableOperationButtonStyle.ALL
  }
];

// 按钮权限配置
const buttonPermissionOptions = [
  {
    label: '隐藏',
    value: 'hidden',
    tips: '无操作权限时，操作列不显示按钮'
  },
  {
    label: '置灰',
    value: 'disabled',
    tips: '无操作权限时，操作列显示按钮但不可点击，鼠标悬停提示“无操作权限”'
  }
];

// 编辑按钮打开方式
const openButtonTypeOptions = [
  { label: '新页签', value: RedirectMethod.NEW_TAB },
  { label: '当前页签', value: RedirectMethod.CURRENT_TAB },
  { label: '弹窗', value: RedirectMethod.MODAL },
  { label: '抽屉', value: RedirectMethod.DRAWER }
];

// 点击删除按钮后动作
const openButtonActionOptions = [
  { label: '刷新列表', value: RedirectMethod.REFRESH },
  { label: '提示跳转', value: RedirectMethod.PROMPT_JUMP }
];

const redirectPageId = 'redirectPageId';
const redirectMethod = 'redirectMethod';
const advancedRowRedirect = 'advancedRowRedirect';

const operationButton = 'operationButton'; //操作按钮集合
const buttonName = 'buttonName';
const buttonIcon = 'buttonIcon';
const confirmText = 'confirmText';
const deletedAction = 'deletedAction';

const AdvancedTableOperationConfig: React.FC<AdvancedTableOperationConfigProps> = ({
  handleMultiPropsChange,
  handlePropsChange,
  item,
  configs
}) => {
  useSignals();

  const { pageViews } = usePageViewEditorSignal;

  useEffect(() => {
    const hasPageView = pageViews.value[configs.redirectPageId]?.detailViewMode == 1;
    if (!hasPageView) {
      handleMultiPropsChange([
        { key: advancedRowRedirect, value: false },
        { key: redirectPageId, value: '' },
        { key: redirectMethod, value: RedirectMethod.DRAWER }
      ]);
    }
  }, []);

  const [modalVisible, setModalVisible] = useState(false);
  const [modalButtonVisible, setModalButtonVisible] = useState<'edit' | 'delete' | string>('');
  const [btnIcon, setBtnIcon] = useState<string>();
  const [btnIconColor, setBtnIconColor] = useState<string>();

  const [form] = Form.useForm();
  const [operationForm] = Form.useForm();

  useEffect(() => {
    if (modalButtonVisible) {
      const defaultIcon = configs[operationButton].find((op: OperationButtonConfig) => op.type === modalButtonVisible);
      setBtnIcon(defaultIcon?.buttonIcon);
      setBtnIconColor(defaultIcon?.iconColor);
    }
  }, [modalButtonVisible]);

  const handleOpenModal = () => {
    setModalVisible(true);
    form.setFieldsValue({
      redirectPageId: configs[redirectPageId],
      redirectMethod: configs[redirectMethod]
    });
  };

  const handleOpenButtonModal = (value: any) => {
    const defaultView = (Object.values(pageViews.value) as PageView[]).find(
      (item: PageView) => item.isDefaultDetailViewMode
    );
    setModalButtonVisible(value.type);
    operationForm.setFieldsValue({
      buttonName: value.buttonName,
      buttonIcon: value.buttonIcon,
      iconColor: value.iconColor,
      redirectPageId: defaultView?.pageUuid,
      redirectMethod: value.redirectMethod,
      confirmText: value.confirmText,
      deletedAction: value.deletedAction
    });
  };

  const handleCloseModal = () => {
    setModalVisible(false);
  };

  const handleCloseButtonModal = () => {
    setModalButtonVisible('');
  };

  const handleOnModal = () => {
    form.validate().then((values) => {
      try {
        console.log(values);
        handleMultiPropsChange([
          { key: redirectPageId, value: values.redirectPageId },
          { key: redirectMethod, value: values.redirectMethod }
        ]);
      } catch (e: any) {
        console.error(e.errors);
      } finally {
        handleCloseModal();
      }
    });
  };

  const handleOnButtonModal = () => {
    operationForm.validate().then((values) => {
      try {
        const newData = [...configs[operationButton]];
        const newValue = newData.map((op: OperationButtonConfig) =>
          op.type === modalButtonVisible
            ? {
              ...op,
              buttonName: values.buttonName,
              buttonIcon: values.buttonIcon,
              iconColor: values.iconColor,
              confirmText: values.confirmText,
              deletedAction: values.deletedAction,
              redirectPageId: values.redirectPageId,
              redirectMethod: values.redirectMethod
            }
            : op
        );
        handlePropsChange(operationButton, newValue);
      } catch (e: any) {
        console.error(e.errors);
      } finally {
        handleCloseButtonModal();
      }
    });
  };

  return (
    <>
      <Form.Item
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
          span: 20
        }}
        wrapperCol={{
          span: 2
        }}
        layout="horizontal"
      >
        <Switch
          size="small"
          checked={configs[item.key]}
          onChange={(value) => {
            if (!value) {
              handleMultiPropsChange([
                { key: item.key, value: value },
                { key: redirectPageId, value: '' },
                { key: redirectMethod, value: '' }
              ]);
            } else {
              // 开启行点击跳转 选择默认视图
              console.log(pageViews.value);
              const defaultView = (Object.values(pageViews.value) as PageView[]).find(
                (item: PageView) => item.isDefaultDetailViewMode
              );
              if (defaultView) {
                handleMultiPropsChange([
                  { key: item.key, value: value },
                  { key: redirectPageId, value: defaultView.pageUuid },
                  { key: redirectMethod, value: RedirectMethod.DRAWER }
                ]);
              }
            }
          }}
        />
      </Form.Item>

      <Form.Item
        labelCol={{
          span: 10
        }}
        wrapperCol={{
          span: 14
        }}
        layout="horizontal"
        labelAlign="left"
        label={'跳转至'}
        hidden={!configs[item.key]}
      >
        <div style={{ width: '100%', textAlign: 'right' }}>
          <Button type="secondary" onClick={handleOpenModal}>
            <div className={styles.rowNavBtn}>
              <div className={styles.rowNavBtnText}>
                {configs[redirectPageId] ? pageViews.value[configs[redirectPageId]]?.pageName : '请选择视图'}
              </div>
              <IconEdit style={{ marginLeft: '8px' }} />
            </div>
          </Button>
        </div>
      </Form.Item>

      <div>操作栏配置</div>
      <Form.Item label="按钮显示方式">
        <RadioGroup
          defaultValue={'all'}
          onChange={(value) => {
            handlePropsChange('operationButtonShowType', value);
          }}
        >
          {buttonIconOptions.map((button) => (
            <Radio key={button.label} value={button.value}>
              {button.label}
            </Radio>
          ))}
        </RadioGroup>
      </Form.Item>

      <Form.Item>
        <div className={styles.buttonShowRule}>
          第
          {
            <InputNumber
              defaultValue={configs['operationButtonCollpaseNumber']}
              className={`buttonShowInput ${styles.buttonInput}`}
              size="mini"
              min={1}
              max={20}
              onChange={(value) => handlePropsChange('operationButtonCollpaseNumber', +value)}
            />
          }
          个按钮开始收入“更多”菜单
        </div>

        {configs[operationButton].map((op: OperationButtonConfig, index: number) => (
          <Fragment key={index}>
            <div className={styles.buttonItem}>
              <Grid.Row align="center">
                <Grid.Col span={16} flex="1">
                  <Space align="center">
                    <IconDragDotVertical style={{ cursor: 'grab', color: '#86909c' }} />
                    <Typography.Text>{op.buttonName}</Typography.Text>
                    {
                      <DynamicIcon
                        IconComponent={iconMap[op.buttonIcon as keyof typeof iconMap]}
                        theme="outline"
                        size="16"
                        fill={op.iconColor}
                        style={{
                          margin: 'auto'
                        }}
                      />
                    }
                  </Space>
                </Grid.Col>

                <Grid.Col span={8} style={{ textAlign: 'right' }}>
                  <Space align="center">
                    <IconEditStroked
                      onClick={() => handleOpenButtonModal(op)}
                      style={{ display: 'flex', color: '#86909c', cursor: 'pointer' }}
                    />
                    <Switch
                      size="small"
                      defaultChecked={op.display}
                      onChange={(value) => {
                        const newArr = [...configs[operationButton]];
                        newArr[index] = { ...newArr[index], display: value };
                        handlePropsChange(operationButton, newArr);
                      }}
                    />
                  </Space>
                </Grid.Col>
              </Grid.Row>
            </div>
            <Divider style={{ margin: 0 }} />
          </Fragment>
        ))}
      </Form.Item>

      <Form.Item label="按钮权限配置">
        <RadioGroup
          defaultValue={configs['advancedButtonPermission']}
          onChange={(value) => {
            handlePropsChange('advancedButtonPermission', value);
          }}
        >
          {buttonPermissionOptions.map((op, index) => (
            <Radio key={index} value={op.value}>
              {op.label}
              <Tooltip content={op.tips}>
                <IconExclamationCircle style={{ marginLeft: 4 }} />
              </Tooltip>
            </Radio>
          ))}
        </RadioGroup>
      </Form.Item>

      <Modal title="行点击跳转" visible={modalVisible} onCancel={handleCloseModal} onOk={handleOnModal}>
        <Form layout="inline" form={form} className={styles.rowNavModal}>
          <Form.Item
            layout="vertical"
            label="跳转页面"
            field={redirectPageId}
            style={{ flex: 1 }}
            rules={[{ required: true, message: '请选择跳转页面' }]}
          >
            <Select
              style={{ width: '230px' }}
              getPopupContainer={getPopupContainer}
              options={(Object.values(pageViews.value) as PageView[])
                .filter((item: PageView) => item.detailViewMode === 1)
                .map((item: PageView) => ({
                  label: item.pageName,
                  value: item.pageUuid
                }))}
            />
          </Form.Item>

          <Form.Item
            layout="vertical"
            label="打开方式"
            field={redirectMethod}
            style={{ flex: 1 }}
            rules={[{ required: true, message: '请选择打开方式' }]}
          >
            <Select
              options={openTypeOptions}
              getPopupContainer={getPopupContainer}
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="操作按钮编辑"
        visible={!!modalButtonVisible}
        onCancel={handleCloseButtonModal}
        onOk={handleOnButtonModal}
      >
        <Form form={operationForm} className={styles.operationModal}>
          <Row gutter={24}>
            <Col span={12}>
              <Form.Item
                layout="vertical"
                label="按钮名称"
                field={buttonName}
                style={{ flex: 1 }}
                rules={[{ required: true, message: '请输入按钮名称' }]}
              >
                <Input defaultValue={modalButtonVisible} autoFocus={false} />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item field="iconColor" hidden>
                <Input />
              </Form.Item>
              <Form.Item
                layout="vertical"
                label="按钮图标"
                field={buttonIcon}
                style={{ flex: 1 }}
                rules={[{ required: true, message: '请选择按钮图标' }]}
              >
                <Popconfirm
                  icon={null}
                  title={null}
                  position="bottom"
                  okText="确认"
                  onOk={() => {
                    operationForm.setFieldsValue({
                      ...operationForm.getFieldsValue(),
                      buttonIcon: btnIcon,
                      iconColor: btnIconColor
                    });
                  }}
                  onCancel={() => {
                    const defaultIcon = configs[operationButton].find(
                      (op: OperationButtonConfig) => op.type === modalButtonVisible
                    );
                    setBtnIcon(defaultIcon?.buttonIcon);
                    setBtnIconColor(defaultIcon?.iconColor);
                  }}
                  style={{ width: 365, maxWidth: 500 }}
                  content={
                    <>
                      <div className={styles.avatarWrapper}>
                        {operationIcon.map((icon: string, index: number) => (
                          <div
                            className={styles.avatar}
                            key={index}
                            style={{ borderColor: icon === btnIcon ? btnIconColor : '#F2F3F5' }}
                            onClick={() => setBtnIcon(icon)}
                          >
                            <DynamicIcon
                              IconComponent={iconMap[icon as keyof typeof iconMap]}
                              theme="outline"
                              size="24"
                              fill="#4E5969"
                            />
                          </div>
                        ))}
                      </div>
                      <div className={styles.avatarColor}>
                        {iconColorList.map((color: string, index: number) => (
                          <div
                            className={styles.color}
                            key={index}
                            style={{ backgroundColor: color }}
                            onClick={() => setBtnIconColor(color)}
                          >
                            {color === btnIconColor && <img src={checkIcon} />}
                          </div>
                        ))}
                      </div>
                    </>
                  }
                >
                  <div className={styles.iconInput}>
                    <DynamicIcon
                      IconComponent={iconMap[btnIcon as keyof typeof iconMap]}
                      theme="outline"
                      size="16"
                      fill={btnIconColor}
                      style={{
                        margin: 'auto'
                      }}
                    />
                  </div>
                </Popconfirm>
              </Form.Item>
            </Col>
          </Row>

          {modalButtonVisible === TableOperationButton.EDIT ? (
            <Row gutter={24}>
              <Col span={12}>
                <Form.Item
                  layout="vertical"
                  label="跳转页面"
                  field={redirectPageId}
                  style={{ flex: 1 }}
                  rules={[{ required: true, message: '请选择跳转页面' }]}
                >
                  <Select
                    options={(Object.values(pageViews.value) as PageView[])
                      .filter((item: PageView) => item.detailViewMode === 1)
                      .map((item: PageView) => ({
                        label: item.pageName,
                        value: item.pageUuid
                      }))}
                    getPopupContainer={getPopupContainer}
                  />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  layout="vertical"
                  label="打开方式"
                  field={redirectMethod}
                  style={{ flex: 1 }}
                  rules={[{ required: true, message: '请选择打开方式' }]}
                >
                  <Select
                    defaultValue={RedirectMethod.NEW_TAB}
                    options={openButtonTypeOptions}
                    getPopupContainer={getPopupContainer}
                  />
                </Form.Item>
              </Col>
            </Row>
          ) : (
            <Row gutter={24}>
              <Col span={12}>
                <Form.Item
                  layout="vertical"
                  label="二次确认文案"
                  field={confirmText}
                  style={{ flex: 1 }}
                  rules={[{ required: true, message: '请输入二次确认文案' }]}
                >
                  <Input defaultValue="确定删除？删除后不可恢复" />
                </Form.Item>
              </Col>

              <Col span={12}>
                <Form.Item
                  layout="vertical"
                  label="删除后动作"
                  field={deletedAction}
                  style={{ flex: 1 }}
                  rules={[{ required: true, message: '请选择删除后动作' }]}
                >
                  <Select
                    defaultValue={RedirectMethod.REFRESH}
                    options={openButtonActionOptions}
                    getPopupContainer={getPopupContainer}
                  />
                </Form.Item>
              </Col>
            </Row>
          )}
        </Form>
      </Modal>
    </>
  );
};

export default AdvancedTableOperationConfig;

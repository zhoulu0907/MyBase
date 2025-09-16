import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Dropdown, Input, Menu, Modal, Select, Space } from '@arco-design/web-react';

import styles from '../../index.module.less';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';

interface FillingRuleSettingsProps {
  visible: boolean;
  fieldOptions: Array<any>;
  onCancel: any;
}

const Option = Select.Option;

// 具体看实际数据结构
function flattenOptions(options: any, parentLabel = '', parentValue = '') {
  let result: any[] = [];
  options.forEach((opt: any) => {
    if (opt.children) {
      result = result.concat(
        flattenOptions(
          opt.children,
          parentLabel ? `${parentLabel}.${opt.label}` : opt.label,
          parentValue ? `${parentValue}.${opt.value}` : opt.value
        )
      );
    } else {
      result.push({
        label: parentLabel ? `${parentLabel}.${opt.label}` : opt.label,
        value: parentValue ? `${parentValue}.${opt.value}` : opt.value
      });
    }
  });
  return result;
}

// mock up
const fillToOptions = [
  {
    value: 1,
    label: '填充到新字段'
  },
  {
    value: 2,
    label: '填充到已有字段'
  }
];

const FillingRuleSettings: React.FC<FillingRuleSettingsProps> = ({ visible, fieldOptions, onCancel }) => {
  const [selected, setSelected] = useState<string[]>([]);
  const [fillOption, setFillOption] = useState<number>(1);
  const [isToNextStep, setIsToNextStep] = useState<boolean>(false);

  useEffect(() => {
    if (visible) {
      setSelected([]);
      setFillOption(1);
      setIsToNextStep(false);
    }
  }, [visible]);

  const flatFieldOptions = flattenOptions(fieldOptions);
  const droplist = (
    <Menu
      style={{ maxHeight: 320 }}
      className={styles.hideScrollbarCommon}
      onClickMenuItem={(key) => setSelected([...selected, key])}
    >
      {flatFieldOptions.map((opt) => (
        <Menu.Item key={opt.value} disabled={selected.includes(opt.value)}>
          {opt.label}
        </Menu.Item>
      ))}
    </Menu>
  );

  // 全选/半选逻辑
  const allValues = flatFieldOptions.map((opt) => opt.value);
  const isAllChecked = selected.length === allValues.length;
  const isIndeterminate = selected.length > 0 && selected.length < allValues.length;

  const onSubmit = () => {
    if (fillOption === 1) {
      onCancel(); // todo
    } else {
      !isToNextStep ? setIsToNextStep(true) : onCancel(); //todo
    }
  };

  return (
    <>
      <Modal
        className={styles.fillingRuleSettingsPopup}
        title={<span className={styles.modalTitleLeft}>填充规则设置</span>}
        visible={visible}
        onCancel={onCancel}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
        footer={
          <>
            <Button onClick={onCancel}>取消</Button>
            <Button onClick={onSubmit} type="primary">
              {fillOption === 1 ? '确定' : !isToNextStep ? '下一步' : '完成'}
            </Button>
          </>
        }
      >
        {!isToNextStep ? (
          <div className={styles.popupContainer}>
            <div className={styles.modalLeftContainer}>
              <span className={styles.titleSpan}>1. 选择字段</span>
              <Checkbox
                checked={isAllChecked}
                indeterminate={isIndeterminate}
                onChange={(checked) => setSelected(checked ? allValues : [])}
                className={styles.checkAll}
              >
                全选
              </Checkbox>
              <div className={styles.checkboxGroup}>
                <Space direction="vertical" size={8}>
                  {flatFieldOptions.map((opt) => (
                    <Checkbox
                      key={opt.value}
                      checked={selected.includes(opt.value)}
                      onChange={(checked) =>
                        setSelected(checked ? [...selected, opt.value] : selected.filter((v) => v !== opt.value))
                      }
                    >
                      {opt.label}
                    </Checkbox>
                  ))}
                </Space>
              </div>
            </div>
            <div className={styles.modalRightContainer}>
              <span className={styles.titleSpan}>2.字段值如何处理</span>
              {selected.length > 0 && (
                <div>
                  <Select
                    placeholder="请选择"
                    getPopupContainer={(node) => node.parentNode as HTMLElement}
                    defaultValue={fillOption}
                    onChange={(value) => {
                      setFillOption(value);
                    }}
                  >
                    {fillToOptions.map((option) => (
                      <Option key={option.value} value={option.value}>
                        {option.label}
                      </Option>
                    ))}
                  </Select>
                  <span className={styles.tipSpan}>自动在表单中添加新字段并构建填充规则</span>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className={styles.nextStepContainer}>
            <span className={styles.titleSpan}>选择数据后，将按以下规则将所选字段的值填充到当前表单字段。</span>
            <Dropdown droplist={droplist} trigger="click">
              <Button type="text">
                <IconPlus />
                选择字段
              </Button>
            </Dropdown>

            {selected.map((item, index) => (
              <div className={styles.fieldItemDiv} key={item}>
                <Input
                  readOnly
                  disabled
                  className={styles.fieldInput}
                  value={flatFieldOptions.find((field) => field.value === item).label}
                />
                <span className={styles.fieldSpan}>的值填充到</span>
                {/* 目标字段 */}
                <Select
                  className={styles.fieldSelect}
                  // value={item.to}
                  // onChange={v => handleChange(idx, v)}
                  // options={fieldOptions.map(opt => ({
                  //   label: (
                  //     <span style={{ display: "flex", alignItems: "center" }}>
                  //       {opt.icon}
                  //       <span style={{ marginLeft: 8 }}>{opt.label}</span>
                  //     </span>
                  //   ),
                  //   value: opt.value,
                  // }))}
                />
                {/* 删除按钮 */}
                <Button
                  type="text"
                  icon={<IconDelete />}
                  className={styles.deleteBtn}
                  onClick={() => setSelected(selected.filter((_, i) => i !== index))}
                />
              </div>
            ))}
          </div>
        )}
      </Modal>
    </>
  );
};

export default FillingRuleSettings;

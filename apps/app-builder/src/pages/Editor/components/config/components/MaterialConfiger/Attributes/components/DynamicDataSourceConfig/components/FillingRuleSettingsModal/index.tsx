import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Dropdown, Input, Menu, Modal, Select, Space } from '@arco-design/web-react';
import { v4 as uuidv4 } from 'uuid';

import styles from '../../index.module.less';
import { IconDelete, IconPlus } from '@arco-design/web-react/icon';
import {
  COMPONENT_TYPE_DISPLAY_NAME_MAP,
  getComponentSchema,
  usePageEditorSignal,
  COMPONENT_MAP,
  getPopupContainer
} from '@onebase/ui-kit';

interface FillingRuleSettingsModalProps {
  visible: boolean;
  fieldOptions: Array<any>;
  selectRule: any[];
  onCancel: any;
  onOk: any;
}

const Option = Select.Option;

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

const FillingRuleSettingsModal: React.FC<FillingRuleSettingsModalProps> = ({
  visible,
  fieldOptions,
  selectRule,
  onCancel,
  onOk
}) => {
  const {
    setCurComponentSchema,
    setPageComponentSchemas,
    pageComponentSchemas,
    components,
    setComponents,
    setShowDeleteButton
  } = usePageEditorSignal();

  const [selected, setSelected] = useState<string[]>([]);
  const [fillOption, setFillOption] = useState<number>(1);
  const [isToNextStep, setIsToNextStep] = useState<boolean>(false);
  const [refactFieldOptions, setRefactFieldOptions] = useState<any[]>([]);
  const [selectedObject, setSelectedObject] = useState<any[]>([]);

  useEffect(() => {
    const refactFieldOptions = [...fieldOptions].reduce((newOptions, item) => {
      const cpType = COMPONENT_MAP[item.fieldType];
      console.log(Object.entries(pageComponentSchemas));

      const targetComponents = Object.entries(pageComponentSchemas)
        .filter(([key, value]) => value.type === cpType)
        .map(([key, value]) => {
          return {
            id: value.id,
            displayName: value.config?.label?.text
          };
        });
      newOptions.push({
        ...item,
        targetComponents,
        cpType
      });
      return newOptions;
    }, []);
    setRefactFieldOptions(refactFieldOptions);

    if (selectRule.length > 0) {
      const selected = selectRule.map((item) => item.fieldId);
      setSelected(selected);
      setIsToNextStep(true);
      setFillOption(2);
    } else {
      reset();
    }
  }, [fieldOptions, selectRule]);

  useEffect(() => {
    if (visible) {
      if (selectRule.length > 0) {
        const selected = selectRule.map((item) => item.fieldId);
        setSelected(selected);
        setIsToNextStep(true);
        setFillOption(2);
      } else {
        reset();
      }
    }
  }, [visible]);

  useEffect(() => {
    const usedComponentIDs = new Set();
    const selectedObject = [...selected].reduce((newSelects: any, select) => {
      const option = refactFieldOptions.find((field) => field.fieldId === select);
      if (!option) return newSelects;

      // 当前优先选 componentID
      let componentID =
        selectRule.find((rule) => rule.fieldId === select)?.selectComponentID || option.targetComponents?.[0]?.id;

      // 如果已被用过，则找同类型未用过的
      if (componentID && usedComponentIDs.has(componentID)) {
        const other = option.targetComponents?.find((tc: any) => !usedComponentIDs.has(tc.id));
        componentID = other ? other.id : undefined;
      }

      // 标记已用
      if (componentID) usedComponentIDs.add(componentID);

      return [
        ...newSelects,
        {
          fieldId: option.fieldId,
          fieldName: option.fieldName,
          displayName: option.displayName,
          targetComponents: option.targetComponents,
          cpType: option.cpType,
          selectComponentID: componentID
        }
      ];
    }, []);
    setSelectedObject(selectedObject);
  }, [selected]);

  const handleOnCancel = () => {
    if (selectRule.length === 0) reset();
    onCancel();
  };

  // 全选/半选逻辑
  const allValues = refactFieldOptions.map((opt) => opt.fieldId);
  const isAllChecked = selected.length === allValues.length;
  const isIndeterminate = selected.length > 0 && selected.length < allValues.length;

  const handleSaveToExistFields = () => {
    const result = selectedObject.reduce((acc, item) => {
      if (item.selectComponentID) {
        acc.push({
          fieldId: item.fieldId,
          fieldName: item.fieldName,
          selectComponentID: item.selectComponentID
        });
      }
      return acc;
    }, []);
    if (result.length === 0) reset();
    onOk(result);
  };

  const handleComChange = (index: number, value: string) => {
    setSelectedObject((prev) =>
      prev.map((item, i) => {
        if (i === index) {
          // 当前项赋新值
          return { ...item, selectComponentID: value };
        }
        // 其他项如果有重复则置空
        if (item.selectComponentID === value) {
          return { ...item, selectComponentID: undefined };
        }
        return item;
      })
    );
  };

  const onSubmit = () => {
    if (fillOption === 1) {
      const fillRuleSetting = addNewComponents();
      onOk(fillRuleSetting);
    } else {
      !isToNextStep ? setIsToNextStep(true) : handleSaveToExistFields();
    }
  };

  const addNewComponents = () => {
    const [newComponents, fillRuleSetting] = selectedObject.reduce(
      ([newComponents, fillRuleSetting], item) => {
        const displayName = COMPONENT_TYPE_DISPLAY_NAME_MAP[item.cpType];
        const cpID = `${item.cpType}-${uuidv4()}`;
        const schema = getComponentSchema(item.cpType as any);
        schema.config.cpName = displayName;
        schema.config.id = cpID;
        schema.config.label.text = displayName;
        const props = {
          id: cpID,
          type: item.cpType,
          ...schema
        };

        setPageComponentSchemas(cpID!, props);
        setCurComponentSchema(props);
        setShowDeleteButton(false);
        newComponents.push({
          displayName: displayName,
          id: cpID,
          selected: false,
          type: item.cpType
        });
        fillRuleSetting.push({
          fieldId: item.fieldId,
          fieldName: item.fieldName,
          selectComponentID: cpID
        });
        return [newComponents, fillRuleSetting];
      },
      [[], []]
    );
    setComponents([...components, ...newComponents]);
    return fillRuleSetting;
  };

  const reset = () => {
    setSelected([]);
    setFillOption(1);
    setIsToNextStep(false);
  };

  const droplist = (
    <Menu
      style={{ maxHeight: 320 }}
      className={styles.hideScrollbarCommon}
      onClickMenuItem={(key) => setSelected([...selected, key])}
    >
      {refactFieldOptions.map((opt) => (
        <Menu.Item key={opt.fieldId} disabled={selected.includes(opt.fieldId)}>
          {opt.displayName}
        </Menu.Item>
      ))}
    </Menu>
  );

  return (
    <>
      <Modal
        className={styles.fillingRuleSettingsModal}
        style={{ width: '600px' }}
        title={<span className={styles.modalTitleLeft}>填充规则设置</span>}
        visible={visible}
        onCancel={handleOnCancel}
        autoFocus={false}
        focusLock={true}
        escToExit={false}
        maskClosable={false}
        footer={
          <>
            <Button onClick={handleOnCancel}>取消</Button>
            <Button onClick={onSubmit} type="primary" disabled={selected.length === 0 && fillOption === 1}>
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
                  {refactFieldOptions.map((opt) => (
                    <Checkbox
                      key={opt.fieldId}
                      checked={selected.includes(opt.fieldId)}
                      onChange={(checked) =>
                        setSelected(checked ? [...selected, opt.fieldId] : selected.filter((v) => v !== opt.fieldId))
                      }
                    >
                      {opt.displayName}
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
                    getPopupContainer={getPopupContainer}
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
            <Dropdown droplist={droplist} trigger="click" getPopupContainer={getPopupContainer}>
              <Button type="text">
                <IconPlus />
                选择字段
              </Button>
            </Dropdown>

            {selectedObject.map((item, index) => (
              <div className={styles.fieldItemDiv} key={item.fieldId}>
                <Input readOnly disabled className={styles.fieldInput} value={item.displayName} />
                <span className={styles.fieldSpan}>的值填充到</span>
                {/* 目标字段 */}
                <Select
                  className={styles.fieldSelect}
                  placeholder="请选择字段"
                  value={item.selectComponentID}
                  getPopupContainer={getPopupContainer}
                  onChange={(v) => handleComChange(index, v)}
                  options={item.targetComponents.map((opt: any) => {
                    return {
                      label: opt.displayName,
                      value: opt.id
                    };
                  })}
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

export default FillingRuleSettingsModal;

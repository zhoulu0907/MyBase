import React, { useState } from 'react';
import { Button, Input, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus, IconBook } from '@arco-design/web-react/icon';
import { useAppStore } from '@/store/store_app';
import SelectDictModal from '@/components/SelectDictModal';
import type { DictItem } from '@onebase/platform-center';
import styles from '../index.module.less';

interface PicklistOptionConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: object[], dictTypeId?: string | number) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  onCancel?: () => void;
}

const CONFIG_TYPE = {
  CUSTOM: 'CUSTOM',
  DICT: 'DICT'
} as const;

const DEFAULT_OPTIONS = [
  { optionLabel: '选项1', optionValue: '选项1' },
  { optionLabel: '选项2', optionValue: '选项2' },
  { optionLabel: '选项3', optionValue: '选项3' }
];

const CONFIG_TYPE_OPTIONS = [
  { label: '自定义', value: CONFIG_TYPE.CUSTOM },
  { label: '引用字典', value: CONFIG_TYPE.DICT }
];

export const PicklistOptionConfig: React.FC<PicklistOptionConfigProps> = ({
  onVisibleChange,
  onConfirm,
  onCancel,
  initialOptions
}) => {
  const [options, setOptions] = useState(
    initialOptions && initialOptions.length > 0 ? initialOptions : DEFAULT_OPTIONS
  );
  const [optionType, setOptionType] = useState<typeof CONFIG_TYPE.CUSTOM | typeof CONFIG_TYPE.DICT>(CONFIG_TYPE.CUSTOM);
  const [selectDictModalVisible, setSelectDictModalVisible] = useState(false);
  const [selectDict, setSelectDict] = useState<DictItem | null>(null);
  const { curAppId } = useAppStore();

  const addOption = () => {
    const newOption = { optionLabel: `选项${options.length + 1}`, optionValue: `选项${options.length + 1}` };
    setOptions([...options, newOption]);
  };

  const removeOption = (index: number) => {
    if (options.length > 1) {
      const newOptions = options.filter((_, i) => i !== index);
      setOptions(newOptions);
    }
  };

  const updateOption = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = { optionLabel: value, optionValue: value };
    setOptions(newOptions);
  };

  const handleConfirm = () => {
    if (optionType === CONFIG_TYPE.DICT && selectDict) {
      const dictTypeId = selectDict.id;
      onConfirm([], dictTypeId);
    } else {
      onConfirm(options);
    }
    if (onVisibleChange) {
      onVisibleChange(false);
    }
  };

  const handleCancel = () => {
    if (onCancel) {
      onCancel();
    } else if (onVisibleChange) {
      onVisibleChange(false);
    }
  };

  const handleSelectDictOk = (dict?: DictItem) => {
    if (dict) {
      setSelectDict(dict);
    }
    setSelectDictModalVisible(false);
  };

  const handleSelectDictCancel = () => {
    setSelectDictModalVisible(false);
  };

  return (
    <div className={styles.fieldTypeConfig}>
      <h4>选项配置</h4>
      <Select value={optionType} onChange={setOptionType} style={{ width: '100%', marginBottom: 16 }}>
        {CONFIG_TYPE_OPTIONS.map((option) => (
          <Select.Option key={option.value} value={option.value}>
            {option.label}
          </Select.Option>
        ))}
      </Select>

      <div>
        {optionType === CONFIG_TYPE.CUSTOM &&
          options.map((option, index) => (
            <div key={index} className={styles.optionItem}>
              <Input
                value={option.optionLabel}
                onChange={(value) => updateOption(index, value)}
                placeholder="请输入选项内容"
                className={styles.optionInput}
              />
              {options.length > 1 && index > 1 && (
                <Button
                  type="text"
                  status="danger"
                  icon={<IconDelete />}
                  onClick={() => removeOption(index)}
                  className={styles.deleteBtn}
                />
              )}
            </div>
          ))}

        {optionType === CONFIG_TYPE.DICT && (
          <div className={styles.optionItem}>
            <Button
              type="outline"
              icon={<IconBook />}
              onClick={() => setSelectDictModalVisible(true)}
              className={styles.selectDictBtn}
            >
              选择数据字典
            </Button>
          </div>
        )}
      </div>

      {optionType === CONFIG_TYPE.CUSTOM && (
        <Button type="dashed" icon={<IconPlus />} onClick={addOption} className={styles.addOptionBtn}>
          新增选项
        </Button>
      )}

      <div className={styles.fieldTypeConfigFooter}>
        <Button type="outline" size="small" onClick={handleCancel}>
          取消
        </Button>
        <Button type="primary" size="small" onClick={handleConfirm}>
          确定
        </Button>
      </div>

      {/* 选择字典弹窗 */}
      <SelectDictModal
        appId={curAppId}
        visible={selectDictModalVisible}
        onOk={handleSelectDictOk}
        onCancel={handleSelectDictCancel}
      />
    </div>
  );
};

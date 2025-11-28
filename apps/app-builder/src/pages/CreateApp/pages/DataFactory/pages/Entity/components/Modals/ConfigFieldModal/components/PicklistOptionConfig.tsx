import React, { useState, useEffect } from 'react';
import { Button, Input, Select } from '@arco-design/web-react';
import { IconDelete, IconPlus, IconBook, IconEdit } from '@arco-design/web-react/icon';
import { useAppStore } from '@/store/store_app';
import SelectDictModal from '@/components/SelectDictModal';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import type { DictItem, DictData } from '@onebase/platform-center';
import styles from '../index.module.less';

interface PicklistOptionConfigProps {
  onVisibleChange?: (visible: boolean) => void;
  onConfirm: (options: object[], dictTypeId?: string) => void;
  initialOptions?: { optionLabel: string; optionValue: string }[];
  initialDictTypeId?: string;
  onCancel?: () => void;
  gotoDictPage?: () => void;
}

const CONFIG_TYPE = {
  CUSTOM: 'CUSTOM',
  DICT: 'DICT'
} as const;

type OptionItem = {
  optionLabel: string;
  optionValue: string;
  [key: string]: unknown;
};

const DEFAULT_OPTIONS: OptionItem[] = [
  { optionLabel: '选项1', optionValue: '选项1' },
  { optionLabel: '选项2', optionValue: '选项2' },
  { optionLabel: '选项3', optionValue: '选项3' }
];

const CONFIG_TYPE_OPTIONS = [
  { label: '自定义', value: CONFIG_TYPE.CUSTOM },
  { label: '引用字典', value: CONFIG_TYPE.DICT }
];

export const PicklistOptionConfig: React.FC<PicklistOptionConfigProps> = ({
  gotoDictPage,
  onVisibleChange,
  onConfirm,
  onCancel,
  initialOptions,
  initialDictTypeId
}) => {
  const [options, setOptions] = useState<OptionItem[]>(
    initialDictTypeId
      ? [] // 有字典ID时，初始为空，通过dictTypeId加载字典数据
      : initialOptions && initialOptions.length > 0
        ? initialOptions
        : DEFAULT_OPTIONS
  );
  const [optionType, setOptionType] = useState<typeof CONFIG_TYPE.CUSTOM | typeof CONFIG_TYPE.DICT>(
    initialDictTypeId ? CONFIG_TYPE.DICT : CONFIG_TYPE.CUSTOM
  );
  const [selectDictModalVisible, setSelectDictModalVisible] = useState(false);
  const [selectDict, setSelectDict] = useState<DictItem | null>(null);
  const [loading, setLoading] = useState(false);
  const [showAllOptions, setShowAllOptions] = useState(false);
  const { curAppId } = useAppStore();

  // 根据 dictTypeId 初始化字典数据
  useEffect(() => {
    const loadDictData = async () => {
      if (initialDictTypeId) {
        setLoading(true);
        try {
          const dictDetail = await getDictDetail(initialDictTypeId);
          setSelectDict(dictDetail);

          const dictDataList = await getDictDataListByType(dictDetail.type);

          const dictOptions: OptionItem[] = dictDataList
            .filter((item: DictData) => item.status === 1) // 只显示启用状态的字典数据
            .map((item: DictData) => ({
              optionLabel: item.label,
              optionValue: item.value,
              colorType: item.colorType
            }));

          setOptions(dictOptions);
          setOptionType(CONFIG_TYPE.DICT);
          setShowAllOptions(false); // 重置显示状态
        } catch (error) {
          console.error('加载字典数据失败:', error);
        } finally {
          setLoading(false);
        }
      }
    };

    loadDictData();
  }, [initialDictTypeId]);

  const addOption = () => {
    const newOption: OptionItem = {
      optionLabel: `选项${options.length + 1}`,
      optionValue: `选项${options.length + 1}`
    };
    setOptions((prev) => [...prev, newOption]);
  };

  const removeOption = (index: number) => {
    if (options.length > 1) {
      setOptions((prev) => prev.filter((_, i) => i !== index));
    }
  };

  const updateOption = (index: number, value: string) => {
    setOptions((prev) => {
      const newOptions = [...prev];
      const option = newOptions[index] ?? ({} as OptionItem);
      newOptions[index] = {
        ...option,
        optionLabel: value,
        optionValue: value
      };
      return newOptions;
    });
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
      // 选择字典后，加载字典数据并设置选项
      const loadDictOptions = async () => {
        try {
          const dictDataList = await getDictDataListByType(dict.type);
          const dictOptions = dictDataList
            .filter((item: DictData) => item.status === 1)
            .map((item: DictData) => ({
              optionLabel: item.label,
              optionValue: item.value
            }));
          setOptions(dictOptions);
          setShowAllOptions(false);
        } catch (error) {
          console.error('加载字典数据失败:', error);
        }
      };
      loadDictOptions();
    }
    setSelectDictModalVisible(false);
  };

  const handleSelectDictCancel = () => {
    setSelectDictModalVisible(false);
  };

  const handleOptionTypeChange = (newType: typeof CONFIG_TYPE.CUSTOM | typeof CONFIG_TYPE.DICT) => {
    setOptionType(newType);
    if (newType === CONFIG_TYPE.CUSTOM) {
      if (!initialOptions || initialOptions.length === 0) {
        setOptions(DEFAULT_OPTIONS);
      } else {
        setOptions(initialOptions);
      }
      setSelectDict(null);
    }
  };

  return (
    <div className={styles.fieldTypeConfig}>
      <h4>选项配置</h4>
      <Select value={optionType} onChange={handleOptionTypeChange} style={{ width: '100%', marginBottom: 16 }}>
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
          <div>
            {/* 字典选择输入框 */}
            <div className={styles.dictSelectWrapper}>
              {selectDict ? (
                <Input
                  readOnly
                  value={selectDict.name}
                  placeholder="请选择数据字典"
                  className={styles.dictNameInput}
                  suffix={<IconEdit className={styles.editIcon} onClick={() => setSelectDictModalVisible(true)} />}
                  onClick={() => setSelectDictModalVisible(true)}
                  disabled={loading}
                />
              ) : (
                <Button
                  type="outline"
                  size="small"
                  onClick={() => setSelectDictModalVisible(true)}
                  className={styles.selectDictBtn}
                >
                  <IconBook /> 请选择数据字典
                </Button>
              )}
              {loading && <div style={{ marginTop: 8, color: '#999', fontSize: 12 }}>加载中...</div>}
            </div>

            {/* 字典值列表 */}
            {selectDict && options.length > 0 && (
              <div className={styles.dictOptionsList}>
                {(showAllOptions ? options : options.slice(0, 3)).map((option, displayIndex) => (
                  <div key={displayIndex} className={styles.dictOptionItem}>
                    <span className={styles.optionDot} style={{ backgroundColor: option.colorType as string }} />
                    <span>{option.optionLabel}</span>
                  </div>
                ))}
                {options.length > 3 && (
                  <div className={styles.moreButtonWrapper}>
                    <Button
                      type="text"
                      size="small"
                      onClick={() => setShowAllOptions(!showAllOptions)}
                      className={styles.moreButton}
                    >
                      {showAllOptions ? '收起' : '更多'}
                    </Button>
                  </div>
                )}
              </div>
            )}
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
        dictTypeId={initialDictTypeId}
        onOk={handleSelectDictOk}
        onCancel={handleSelectDictCancel}
        gotoDictPage={gotoDictPage}
      />
    </div>
  );
};

import {
  Button,
  Form,
  Input,
  Radio,
  Space,
  Tooltip,
  Select,
  ColorPicker,
  Grid,
  Switch,
  Tag
} from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES, COLOR_MODE_TYPES } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
import { useAppEntityStore, DEFAULT_OPTIONS_TYPE, getPopupContainer } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import SelectDictModal from '@/components/SelectDictModal';
import { useAppStore } from '@/store/store_app';

export interface DynamicRadioConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicRadioConfig: React.FC<DynamicRadioConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const selectKey = 'defaultOptionsConfig';
  const { mainEntity, subEntities } = useAppEntityStore();
  useSignals();
  const { curAppId } = useAppStore();

  const [selectDisabled, setSelectDisabled] = useState<boolean>(false);
  const [selectDictModalVisible, setSelectDictModalVisible] = useState<boolean>(false);

  useEffect(() => {
    if (configs.id) {
      setSelectDisabled(false);
      getDefaultOptions();
    }
  }, []);

  const getDefaultOptions = async () => {
    const value = configs.dataField;
    const isMainEntity = value?.includes(mainEntity.entityId);
    const currentMainField = mainEntity.fields?.find((ele: any) => value.includes(ele.fieldId));
    const isSubEntity = subEntities.entities?.find((ele: any) => value?.includes(ele.entityId));
    const currentSubField = isSubEntity?.fields.find((ele: any) => value.includes(ele.fieldId));
    if (isMainEntity && currentMainField) {
      // 主表
      if (currentMainField.dictTypeId) {
        const res = await getDictDetail(currentMainField.dictTypeId);
        const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
        const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
        if (dictOptions.length) {
          const newDefaultOptionsConfig = {
            type: DEFAULT_OPTIONS_TYPE.DICT,
            disabled: true,
            dictTypeId: currentMainField.dictTypeId,
            colorMode: true,
            colorModeType: COLOR_MODE_TYPES.POINT,
            defaultOptions: dictOptions.map((e: any) => {
              if (configs[selectKey].defaultOptions?.length) {
                const oldOption = configs[selectKey].defaultOptions.find((ele: any) => ele.value === e.value);
                if (oldOption && oldOption.isChosen) {
                  return { ...e, isChosen: true };
                }
              }
              return { ...e, isChosen: false };
            })
          };
          handlePropsChange(selectKey, newDefaultOptionsConfig);
          setSelectDisabled(true);
        }
      } else if (currentMainField.options?.length) {
        const newOptions = currentMainField.options?.map((e: any) => {
          if (configs[selectKey].defaultOptions?.length) {
            const oldOption = configs[selectKey].defaultOptions.find((ele: any) => ele.value === e.value);
            if (oldOption && oldOption.isChosen) {
              return { label: e.optionLabel, value: e.optionValue, isChosen: true };
            }
          }
          return {
            label: e.optionLabel,
            value: e.optionValue,
            isChosen: false
          };
        });
        handlePropsChange(selectKey, { ...configs[selectKey], defaultOptions: newOptions, disabled: true });
        setSelectDisabled(true);
      }
    } else if (isSubEntity && currentSubField) {
      // 子表
      if (currentSubField.dictTypeId) {
        const res = await getDictDetail(currentSubField.dictTypeId);
        const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
        const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
        if (dictOptions.length) {
          const newDefaultOptionsConfig = {
            type: DEFAULT_OPTIONS_TYPE.DICT,
            disabled: true,
            dictTypeId: currentSubField.dictTypeId,
            colorMode: true,
            colorModeType: COLOR_MODE_TYPES.POINT,
            defaultOptions: dictOptions.map((e: any) => {
              if (configs[selectKey].defaultOptions?.length) {
                const oldOption = configs[selectKey].defaultOptions.find((ele: any) => ele.value === e.value);
                if (oldOption && oldOption.isChosen) {
                  return { ...e, isChosen: true };
                }
              }
              return { ...e, isChosen: false };
            })
          };
          handlePropsChange(selectKey, newDefaultOptionsConfig);
          setSelectDisabled(true);
        }
      } else if (currentSubField.options?.length) {
        const newOptions = currentSubField.options?.map((e: any) => {
          if (configs[selectKey].defaultOptions?.length) {
            const oldOption = configs[selectKey].defaultOptions.find((ele: any) => ele.value === e.value);
            if (oldOption && oldOption.isChosen) {
              return { label: e.optionLabel, value: e.optionValue, isChosen: true };
            }
          }
          return {
            label: e.optionLabel,
            value: e.optionValue,
            isChosen: false
          };
        });
        handlePropsChange(selectKey, { ...configs[selectKey], defaultOptions: newOptions, disabled: true });
        setSelectDisabled(true);
      }
    }
  };

  const handleSelectDictOk = async (dict?: any) => {
    if (dict?.type) {
      const dictDataList = await getDictDataListByType(dict.type);
      const dictOptions = dictDataList.filter((item: any) => item.status === 1);
      handlePropsChange(selectKey, {
        ...configs[selectKey],
        defaultOptions: dictOptions,
        dictTypeId: dict.id,
        colorMode: true,
        colorModeType: COLOR_MODE_TYPES.POINT
      });
      setSelectDisabled(false);
    }
    setSelectDictModalVisible(false);
  };

  const handleSelectDictCancel = () => {
    setSelectDictModalVisible(false);
  };

  return (
    <>
      <Form.Item layout="vertical" labelAlign="left" label={item.name || '自定义配置'} className={styles.formItem}>
        <Form.Item>
          <Select
            getPopupContainer={getPopupContainer}
            value={configs[selectKey].type}
            disabled={configs[selectKey].disabled}
            onChange={(value) => {
              if (value === DEFAULT_OPTIONS_TYPE.CUSTOM) {
                setSelectDisabled(false);
              }
              handlePropsChange(selectKey, { ...configs[selectKey], type: value, defaultOptions: [] });
            }}
            options={[
              { label: '自定义', value: DEFAULT_OPTIONS_TYPE.CUSTOM },
              { label: '数据字典', value: DEFAULT_OPTIONS_TYPE.DICT }
            ]}
          ></Select>
        </Form.Item>
        {configs[selectKey].type === DEFAULT_OPTIONS_TYPE.DICT && configs[selectKey].defaultOptions.length === 0 && (
          <Form.Item>
            <Button long onClick={() => setSelectDictModalVisible(true)}>
              请选择数据字典
            </Button>
          </Form.Item>
        )}
        <Form.List initialValue={configs[selectKey].defaultOptions} field={`${id}-${selectKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[selectKey].defaultOptions}
                setList={() => {}}
                group={{
                  name: 'table-col-item'
                }}
                swap
                sort={
                  !configs[selectKey].disabled &&
                  !selectDisabled &&
                  configs[selectKey].type !== DEFAULT_OPTIONS_TYPE.DICT
                }
                handle=".table-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  console.log(e);
                  const newList = [...configs[selectKey].defaultOptions];
                  // 根据 onSort 事件中的 oldIndex 和 newIndex 交换数组元素
                  const { oldIndex, newIndex } = e;
                  console.log(oldIndex, newIndex);
                  if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
                    // 复制一份新数组
                    const movedList = [...newList];
                    // 取出被移动的元素
                    const [movedItem] = movedList.splice(oldIndex, 1);
                    // 插入到新位置
                    movedList.splice(newIndex, 0, movedItem);
                    // 更新属性
                    const newConfig = { ...configs[selectKey], defaultOptions: movedList };
                    handlePropsChange(selectKey, newConfig);
                  }
                }}
              >
                {configs[selectKey].defaultOptions?.map((_col: any, idx: number) => (
                  <Tooltip
                    key={idx}
                    content={selectDisabled ? '如需修改请前往数据建模' : '如需修改请前往数据字典'}
                    disabled={!configs[selectKey].disabled && !selectDisabled}
                  >
                    <div className={styles.tableColumnItem}>
                      <Space>
                        <IconDragDotVertical
                          // 支持拖拽的图标，别误删了：）
                          className="table-col-item-handle"
                          style={{
                            cursor: 'move',
                            color: '#555'
                          }}
                        />
                        <Radio
                          checked={configs[selectKey].defaultOptions[idx].isChosen}
                          onChange={(e) => {
                            let newList = [...configs[selectKey].defaultOptions].map((item) => ({
                              ...item,
                              isChosen: false
                            }));
                            newList[idx] = {
                              ...newList[idx],
                              isChosen: true
                            };
                            const newConfig = { ...configs[selectKey], defaultOptions: newList };
                            handlePropsChange(selectKey, newConfig);
                          }}
                        />
                        <Input
                          size="small"
                          disabled={
                            configs[selectKey].disabled ||
                            selectDisabled ||
                            configs[selectKey].type === DEFAULT_OPTIONS_TYPE.DICT
                          }
                          value={configs[selectKey].defaultOptions[idx].label}
                          onChange={(e) => {
                            const newList = [...configs[selectKey].defaultOptions];
                            newList[idx] = {
                              ...newList[idx],
                              label: e
                            };
                            const newConfig = { ...configs[selectKey], defaultOptions: newList };
                            handlePropsChange(selectKey, newConfig);
                          }}
                          className={styles.tableColumnItemInput}
                          placeholder={'新选项'}
                        />
                        {configs[selectKey]['colorMode'] && (
                          <ColorPicker
                            size="mini"
                            disabled={
                              configs[selectKey].disabled ||
                              selectDisabled ||
                              configs[selectKey].type === DEFAULT_OPTIONS_TYPE.DICT
                            }
                            value={configs[selectKey].defaultOptions[idx].colorType || 'rgb(var(--primary-7))'}
                            onChange={(e) => {
                              const newList = [...configs[selectKey].defaultOptions];
                              newList[idx] = {
                                ...newList[idx],
                                colorType: e
                              };
                              const newConfig = { ...configs[selectKey], defaultOptions: newList };
                              handlePropsChange(selectKey, newConfig);
                            }}
                          ></ColorPicker>
                        )}
                        {!configs[selectKey].disabled &&
                          !selectDisabled &&
                          configs[selectKey].type !== DEFAULT_OPTIONS_TYPE.DICT && (
                            <Button
                              icon={<IconDelete />}
                              shape="circle"
                              size="mini"
                              status="danger"
                              disabled={configs[selectKey]?.defaultOptions?.length <= 2}
                              className={styles.tableColumnItemButton}
                              onClick={() => {
                                const newList = [...configs[selectKey].defaultOptions];
                                newList.splice(idx, 1);
                                const newConfig = { ...configs[selectKey], defaultOptions: newList };
                                handlePropsChange(selectKey, newConfig);
                                remove(idx);
                              }}
                            />
                          )}
                      </Space>
                    </div>
                  </Tooltip>
                ))}
              </ReactSortable>

              {!configs[selectKey].disabled &&
                !selectDisabled &&
                configs[selectKey].type !== DEFAULT_OPTIONS_TYPE.DICT && (
                  <Button
                    type="outline"
                    onClick={() => {
                      const newLabel = `新选项_${Array.from({ length: 6 }, () => String.fromCharCode(97 + Math.floor(Math.random() * 26))).join('')}`;
                      const newValue = _fields?.[_fields.length - 1]?.field || `${configs.id}-${selectKey}[0]`;
                      const newList = [
                        ...configs[selectKey].defaultOptions,
                        {
                          label: item.displayName || newLabel,
                          value: item.fieldName || newValue,
                          isChosen: false,
                          colorType: ''
                        }
                      ];
                      add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                      const newConfig = { ...configs[selectKey], defaultOptions: newList };
                      handlePropsChange(selectKey, newConfig);
                    }}
                  >
                    添加一项
                  </Button>
                )}
            </div>
          )}
        </Form.List>
        <Grid.Row gutter={8} style={{ marginTop: '4px' }}>
          <Grid.Col span={6} style={{ color: 'var(--color-text-2)' }}>
            彩色模式
          </Grid.Col>
          <Grid.Col span={4}>
            <Switch
              disabled={
                configs[selectKey].disabled || selectDisabled || configs[selectKey].type === DEFAULT_OPTIONS_TYPE.DICT
              }
              size="small"
              checked={configs[selectKey].colorMode}
              onChange={(value) => {
                handlePropsChange(selectKey, { ...configs[selectKey], colorMode: value });
              }}
            />
          </Grid.Col>
          <Grid.Col span={14}>
            <Radio.Group
              disabled={
                configs[selectKey].disabled || selectDisabled || configs[selectKey].type === DEFAULT_OPTIONS_TYPE.DICT
              }
              value={configs[selectKey].colorModeType}
              onChange={(value) => {
                handlePropsChange(selectKey, { ...configs[selectKey], colorModeType: value });
              }}
            >
              <Radio value={COLOR_MODE_TYPES.TAG} style={{ marginRight: '8px' }}>
                <Tag color="rgb(var(--primary-7))">选项</Tag>
              </Radio>
              <Radio value={COLOR_MODE_TYPES.POINT} style={{ marginRight: '0' }}>
                <span
                  style={{
                    width: '8px',
                    height: '8px',
                    borderRadius: '50%',
                    background: 'rgb(var(--primary-7))',
                    display: 'inline-block',
                    marginRight: '8px'
                  }}
                ></span>
                <span>选项</span>
              </Radio>
            </Radio.Group>
          </Grid.Col>
        </Grid.Row>
      </Form.Item>
      <SelectDictModal
        appId={curAppId}
        visible={selectDictModalVisible}
        dictTypeId={configs[selectKey].dictTypeId}
        onOk={handleSelectDictOk}
        onCancel={handleSelectDictCancel}
      />
    </>
  );
};

export default DynamicRadioConfig;

registerConfigRenderer(CONFIG_TYPES.RADIO_DATA, ({ id, handlePropsChange, item, configs }) => (
  <DynamicRadioConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

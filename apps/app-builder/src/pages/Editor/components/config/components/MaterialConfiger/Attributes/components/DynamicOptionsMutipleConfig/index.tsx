import {
  Button,
  Form,
  Input,
  Radio,
  Checkbox,
  Space,
  Grid,
  Switch,
  Tag,
  ColorPicker,
  Tooltip,
  Select
} from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { registerConfigRenderer } from '../../registry';
import { CONFIG_TYPES } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
import { COLOR_MODE_TYPES, useAppEntityStore, DEFAULT_OPTIONS_TYPE, getPopupContainer } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import SelectDictModal from '@/components/SelectDictModal';
import { useAppStore } from '@/store/store_app';

export interface DynamicSelectMutipleConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicSelectMutipleConfig: React.FC<DynamicSelectMutipleConfigProps> = ({
  handlePropsChange,
  item,
  configs,
  id
}) => {
  const selectMutipleKey = 'defaultOptionsConfig';
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
            defaultOptions: dictOptions.map((e: any) => {
              if (configs[selectMutipleKey].defaultOptions?.length) {
                const oldOption = configs[selectMutipleKey].defaultOptions.find((ele: any) => ele.value === e.value);
                if (oldOption && oldOption.isChosen) {
                  return { ...e, isChosen: true };
                }
              }
              return { ...e, isChosen: false };
            })
          };
          handlePropsChange(selectMutipleKey, newDefaultOptionsConfig);
          setSelectDisabled(true);
        }
      } else if (currentMainField.options?.length) {
        const newOptions = currentMainField.options?.map((e: any) => {
          if (configs[selectMutipleKey].defaultOptions?.length) {
            const oldOption = configs[selectMutipleKey].defaultOptions.find((ele: any) => ele.value === e.value);
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
        handlePropsChange(selectMutipleKey, {
          ...configs[selectMutipleKey],
          defaultOptions: newOptions,
          disabled: true
        });
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
            defaultOptions: dictOptions.map((e: any) => {
              if (configs[selectMutipleKey].defaultOptions?.length) {
                const oldOption = configs[selectMutipleKey].defaultOptions.find((ele: any) => ele.value === e.value);
                if (oldOption && oldOption.isChosen) {
                  return { ...e, isChosen: true };
                }
              }
              return { ...e, isChosen: false };
            })
          };
          handlePropsChange(selectMutipleKey, newDefaultOptionsConfig);
          setSelectDisabled(true);
        }
      } else if (currentSubField.options?.length) {
        const newOptions = currentSubField.options?.map((e: any) => {
          if (configs[selectMutipleKey].defaultOptions?.length) {
            const oldOption = configs[selectMutipleKey].defaultOptions.find((ele: any) => ele.value === e.value);
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
        handlePropsChange(selectMutipleKey, {
          ...configs[selectMutipleKey],
          defaultOptions: newOptions,
          disabled: true
        });
        setSelectDisabled(true);
      }
    }
  };

  const handleSelectDictOk = async (dict?: any) => {
    if (dict?.type) {
      const dictDataList = await getDictDataListByType(dict.type);
      const dictOptions = dictDataList.filter((item: any) => item.status === 1);
      handlePropsChange(selectMutipleKey, {
        ...configs[selectMutipleKey],
        defaultOptions: dictOptions,
        dictTypeId: dict.id
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
            value={configs[selectMutipleKey].type}
            disabled={configs[selectMutipleKey].disabled}
            onChange={(value) => {
              if (value === DEFAULT_OPTIONS_TYPE.CUSTOM) {
                setSelectDisabled(false);
              }
              handlePropsChange(selectMutipleKey, { ...configs[selectMutipleKey], type: value });
            }}
            options={[
              { label: '自定义', value: DEFAULT_OPTIONS_TYPE.CUSTOM },
              { label: '数据字典', value: DEFAULT_OPTIONS_TYPE.DICT }
            ]}
          ></Select>
        </Form.Item>
        {configs[selectMutipleKey].type === DEFAULT_OPTIONS_TYPE.DICT &&
          configs[selectMutipleKey].defaultOptions.length === 0 && (
            <Form.Item>
              <Button long onClick={() => setSelectDictModalVisible(true)}>
                请选择数据字典
              </Button>
            </Form.Item>
          )}
        <Form.List initialValue={configs[selectMutipleKey].defaultOptions} field={`${id}-${selectMutipleKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[selectMutipleKey].defaultOptions}
                setList={() => {}}
                group={{
                  name: 'table-col-item'
                }}
                swap
                sort={!configs[selectMutipleKey].disabled && !selectDisabled}
                handle=".table-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  console.log(e);
                  const newList = [...configs[selectMutipleKey].defaultOptions];
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
                    const newConfig = { ...configs[selectMutipleKey], defaultOptions: movedList };
                    handlePropsChange(selectMutipleKey, newConfig);
                  }
                }}
              >
                {configs[selectMutipleKey].defaultOptions?.map((_col: any, idx: number) => (
                  <Tooltip
                    key={idx}
                    content={selectDisabled ? '如需修改请前往数据建模' : '如需修改请前往数据字典'}
                    disabled={!configs[selectMutipleKey].disabled && !selectDisabled}
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
                        <Checkbox
                          checked={configs[selectMutipleKey].defaultOptions[idx].isChosen}
                          onChange={(e) => {
                            let newList = [...configs[selectMutipleKey].defaultOptions];
                            newList[idx] = {
                              ...newList[idx],
                              isChosen: e
                            };
                            const newConfig = { ...configs[selectMutipleKey], defaultOptions: newList };
                            handlePropsChange(selectMutipleKey, newConfig);
                          }}
                        />
                        <Input
                          size="small"
                          disabled={selectDisabled}
                          value={configs[selectMutipleKey].defaultOptions[idx].label}
                          onChange={(e) => {
                            const newList = [...configs[selectMutipleKey].defaultOptions];
                            newList[idx] = {
                              ...newList[idx],
                              label: e
                            };
                            const newConfig = { ...configs[selectMutipleKey], defaultOptions: newList };
                            handlePropsChange(selectMutipleKey, newConfig);
                          }}
                          className={styles.tableColumnItemInput}
                          placeholder={'新选项'}
                        />
                        {configs[selectMutipleKey]['colorMode'] && (
                          <ColorPicker
                            size="mini"
                            disabled={selectDisabled}
                            value={configs[selectMutipleKey].defaultOptions[idx].colorType || 'rgb(var(--primary-7))'}
                            onChange={(e) => {
                              const newList = [...configs[selectMutipleKey].defaultOptions];
                              newList[idx] = {
                                ...newList[idx],
                                colorType: e
                              };
                              const newConfig = { ...configs[selectMutipleKey], defaultOptions: newList };
                              handlePropsChange(selectMutipleKey, newConfig);
                            }}
                          ></ColorPicker>
                        )}
                        {!configs[selectMutipleKey].disabled && !selectDisabled && (
                          <Button
                            icon={<IconDelete />}
                            shape="circle"
                            size="mini"
                            status="danger"
                            disabled={configs[selectMutipleKey]?.defaultOptions?.length <= 2}
                            className={styles.tableColumnItemButton}
                            onClick={() => {
                              const newList = [...configs[selectMutipleKey].defaultOptions];
                              newList.splice(idx, 1);
                              const newConfig = { ...configs[selectMutipleKey], defaultOptions: newList };
                              handlePropsChange(selectMutipleKey, newConfig);
                              remove(idx);
                            }}
                          />
                        )}
                      </Space>
                    </div>
                  </Tooltip>
                ))}
              </ReactSortable>

              {!configs[selectMutipleKey].disabled &&
                !selectDisabled &&
                configs[selectMutipleKey].type !== DEFAULT_OPTIONS_TYPE.DICT && (
                  <Button
                    type="outline"
                    onClick={() => {
                      const code = Array.from({ length: 6 }, () =>
                        String.fromCharCode(97 + Math.floor(Math.random() * 26))
                      ).join('');
                      const newLabel = `新选项_${code}`;
                      const newValue = `${configs.id}-${selectMutipleKey}-${code}`;
                      const newList = [
                        ...configs[selectMutipleKey].defaultOptions,
                        {
                          label: item.displayName || newLabel,
                          value: item.fieldName || newValue,
                          isChosen: false,
                          colorType: ''
                        }
                      ];
                      add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                      const newConfig = { ...configs[selectMutipleKey], defaultOptions: newList };
                      handlePropsChange(selectMutipleKey, newConfig);
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
              disabled={configs[selectMutipleKey].disabled || selectDisabled}
              size="small"
              checked={configs[selectMutipleKey].colorMode}
              onChange={(value) => {
                handlePropsChange(selectMutipleKey, { ...configs[selectMutipleKey], colorMode: value });
              }}
            />
          </Grid.Col>
          <Grid.Col span={14}>
            <Radio.Group
              disabled={configs[selectMutipleKey].disabled || selectDisabled}
              value={configs[selectMutipleKey].colorModeType}
              onChange={(value) => {
                handlePropsChange(selectMutipleKey, { ...configs[selectMutipleKey], colorModeType: value });
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
        dictTypeId={configs[selectMutipleKey].dictTypeId}
        onOk={handleSelectDictOk}
        onCancel={handleSelectDictCancel}
      />
    </>
  );
};

export default DynamicSelectMutipleConfig;

registerConfigRenderer(CONFIG_TYPES.MUTIPLE_SELECT_OPTIONS_INPUT, ({ id, handlePropsChange, item, configs }) => (
  <DynamicSelectMutipleConfig id={id} handlePropsChange={handlePropsChange} item={item} configs={configs} />
));

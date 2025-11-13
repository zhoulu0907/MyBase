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
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';
import { COLOR_MODE_TYPES, useAppEntityStore, DEFAULT_OPTIONS_TYPE } from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import SelectDictModal from '@/components/SelectDictModal';
import { useAppStore } from '@/store/store_app';

export interface DynamicCheckboxConfigProps {
  handlePropsChange: (key: string, value: any) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicCheckboxConfig: React.FC<DynamicCheckboxConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const checkboxKey = item.key || 'defaultOptionsConfig';
  const { mainEntity, subEntities } = useAppEntityStore();
  useSignals();
  const { curAppId } = useAppStore();

  const [selectDisabled, setSelectDisabled] = useState<boolean>(false);
  const [typeDisabled, setTypeDisabled] = useState<boolean>(false);
  const [selectDictModalVisible, setSelectDictModalVisible] = useState<boolean>(false);

  useEffect(() => {
    getDefaultOptions();
  }, [configs.dataField]);

  useEffect(() => {
    setSelectDisabled(false);
    setTypeDisabled(false);
    getDefaultOptions(true);
    if (configs[checkboxKey].type === DEFAULT_OPTIONS_TYPE.DICT) {
      setSelectDisabled(true);
    }
  }, [configs.id]);

  const getDefaultOptions = async (flag?: boolean) => {
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
          if (flag) {
            const newDefaultOptionsConfig = {
              type: DEFAULT_OPTIONS_TYPE.DICT,
              dictTypeId: currentMainField.dictTypeId,
              disabled: true,
              colorMode: true,
              colorModeType: COLOR_MODE_TYPES.POINT,
              defaultOptions: dictOptions
            };
            handlePropsChange(checkboxKey, newDefaultOptionsConfig);
          }
          setSelectDisabled(true);
          setTypeDisabled(true);
        }
      } else if (currentMainField.options?.length) {
        const newOptions = currentMainField.options?.map((e: any) => ({
          label: e.optionLabel,
          value: e.optionValue
        }));
        if (flag) {
          handlePropsChange(checkboxKey, { ...configs[checkboxKey], defaultOptions: newOptions, disabled: true });
        }
        setTypeDisabled(true);
        setSelectDisabled(true);
      }
    } else if (isSubEntity && currentSubField) {
      // 子表
      if (currentSubField.dictTypeId) {
        const res = await getDictDetail(currentSubField.dictTypeId);
        const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
        const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
        if (dictOptions.length) {
          if (flag) {
            const newDefaultOptionsConfig = {
              type: DEFAULT_OPTIONS_TYPE.DICT,
              dictTypeId: currentSubField.dictTypeId,
              disabled: true,
              colorMode: true,
              colorModeType: COLOR_MODE_TYPES.POINT,
              defaultOptions: dictOptions
            };
            handlePropsChange(checkboxKey, newDefaultOptionsConfig);
          }
          setSelectDisabled(true);
          setTypeDisabled(true);
        }
      } else if (currentSubField.options?.length) {
        const newOptions = currentSubField.options?.map((e: any) => ({
          label: e.optionLabel,
          value: e.optionValue
        }));
        if (flag) {
          handlePropsChange(checkboxKey, { ...configs[checkboxKey], defaultOptions: newOptions, disabled: true });
        }
        setSelectDisabled(true);
        setTypeDisabled(true);
      }
    }
  };

  const handleSelectDictOk = async (dict?: any) => {
    if (dict?.type) {
      const dictDataList = await getDictDataListByType(dict.type);
      const dictOptions = dictDataList.filter((item: any) => item.status === 1);
      handlePropsChange(checkboxKey, {
        ...configs[checkboxKey],
        defaultOptions: dictOptions,
        disabled: true,
        dictTypeId: dict.id,
        colorMode: true,
        colorModeType: COLOR_MODE_TYPES.POINT
      });
      setTypeDisabled(false);
      setSelectDisabled(true);
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
            value={configs[checkboxKey].type}
            disabled={typeDisabled}
            onChange={(value) => {
              if (value === DEFAULT_OPTIONS_TYPE.CUSTOM) {
                setTypeDisabled(false);
                setSelectDisabled(false);
              }
              handlePropsChange(checkboxKey, {
                ...configs[checkboxKey],
                type: value,
                defaultOptions: [],
              });
            }}
            options={[
              { label: '自定义', value: DEFAULT_OPTIONS_TYPE.CUSTOM },
              { label: '数据字典', value: DEFAULT_OPTIONS_TYPE.DICT }
            ]}
          ></Select>
        </Form.Item>
        {configs[checkboxKey].type === DEFAULT_OPTIONS_TYPE.DICT &&
          configs[checkboxKey].defaultOptions.length === 0 && (
            <Form.Item>
              <Button long onClick={() => setSelectDictModalVisible(true)}>
                请选择数据字典
              </Button>
            </Form.Item>
          )}
        <Form.List initialValue={configs[checkboxKey].defaultOptions} field={`${id}-${checkboxKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={configs[checkboxKey].defaultOptions}
                setList={() => {}}
                group={{
                  name: 'table-col-item'
                }}
                swap
                sort={!selectDisabled}
                handle=".table-col-item-handle"
                className={styles.componentCollapseContent}
                forceFallback={true}
                animation={150}
                onAdd={(e) => {
                  console.log('onAdd: ', e);
                }}
                onSort={(e) => {
                  console.log(e);
                  const newList = [...configs[checkboxKey].defaultOptions];
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
                    const newConfig = { ...configs[checkboxKey], defaultOptions: movedList };
                    handlePropsChange(checkboxKey, newConfig);
                  }
                }}
              >
                {configs[checkboxKey].defaultOptions?.map((_col: any, idx: number) => (
                  <Tooltip
                    key={idx}
                    content={typeDisabled ? '如需修改请前往数据建模' : '如需修改请前往数据字典'}
                    disabled={!selectDisabled}
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
                          checked={configs[checkboxKey].defaultOptions[idx].chosen || false}
                          onChange={(e) => {
                            let newList = [...configs[checkboxKey].defaultOptions];
                            newList[idx] = {
                              ...newList[idx],
                              chosen: e
                            };
                            const newConfig = { ...configs[checkboxKey], defaultOptions: newList };
                            handlePropsChange(checkboxKey, newConfig);
                          }}
                        />
                        <Input
                          size="small"
                          disabled={selectDisabled}
                          value={configs[checkboxKey].defaultOptions[idx].label}
                          onChange={(e) => {
                            const newList = [...configs[checkboxKey].defaultOptions];
                            newList[idx] = {
                              ...newList[idx],
                              label: e
                            };
                            const newConfig = { ...configs[checkboxKey], defaultOptions: newList };
                            handlePropsChange(checkboxKey, newConfig);
                          }}
                          className={styles.tableColumnItemInput}
                          placeholder={'新选项'}
                        />
                        {configs[checkboxKey]['colorMode'] && (
                          <ColorPicker
                            size="mini"
                            disabled={selectDisabled}
                            value={configs[checkboxKey].defaultOptions[idx].colorType || 'rgb(var(--primary-7))'}
                            onChange={(e) => {
                              const newList = [...configs[checkboxKey].defaultOptions];
                              newList[idx] = {
                                ...newList[idx],
                                colorType: e
                              };
                              const newConfig = { ...configs[checkboxKey], defaultOptions: newList };
                              handlePropsChange(checkboxKey, newConfig);
                            }}
                          ></ColorPicker>
                        )}
                        {!selectDisabled && (
                          <Button
                            icon={<IconDelete />}
                            shape="circle"
                            size="mini"
                            status="danger"
                            disabled={configs[checkboxKey]?.defaultOptions?.length <= 2}
                            className={styles.tableColumnItemButton}
                            onClick={() => {
                              const newList = [...configs[checkboxKey].defaultOptions];
                              newList.splice(idx, 1);
                              const newConfig = { ...configs[checkboxKey], defaultOptions: newList };
                              handlePropsChange(checkboxKey, newConfig);
                              remove(idx);
                            }}
                          />
                        )}
                      </Space>
                    </div>
                  </Tooltip>
                ))}
              </ReactSortable>

              {!selectDisabled && configs[checkboxKey].type !== DEFAULT_OPTIONS_TYPE.DICT && (
                <Button
                  type="outline"
                  onClick={() => {
                    const newLabel = `新选项_${Array.from({ length: 6 }, () => String.fromCharCode(97 + Math.floor(Math.random() * 26))).join('')}`;
                    const newValue = _fields?.[_fields.length - 1]?.field || `${configs.id}-${checkboxKey}[0]`;
                    const newList = [
                      ...configs[checkboxKey].defaultOptions,
                      { label: item.displayName || newLabel, value: item.fieldName || newValue }
                    ];
                    add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                    const newConfig = { ...configs[checkboxKey], defaultOptions: newList };
                    handlePropsChange(checkboxKey, newConfig);
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
              disabled={selectDisabled}
              size="small"
              checked={configs[checkboxKey].colorMode}
              onChange={(value) => {
                handlePropsChange(checkboxKey, { ...configs[checkboxKey], colorMode: value });
              }}
            />
          </Grid.Col>
          <Grid.Col span={14}>
            <Radio.Group
              disabled={selectDisabled}
              value={configs[checkboxKey].colorModeType}
              onChange={(value) => {
                handlePropsChange(checkboxKey, { ...configs[checkboxKey], colorModeType: value });
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
        dictTypeId={configs[checkboxKey].dictTypeId}
        onOk={handleSelectDictOk}
        onCancel={handleSelectDictCancel}
      />
    </>
  );
};

export default DynamicCheckboxConfig;

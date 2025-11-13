import { Button, Form, Input, Space, Tooltip } from '@arco-design/web-react';
import { IconDelete, IconDragDotVertical } from '@arco-design/web-react/icon';
import React, { useEffect, useState } from 'react';
import { ReactSortable } from 'react-sortablejs';
import { useSignals } from '@preact/signals-react/runtime';
import { useAppEntityStore } from '@onebase/ui-kit';
import { getDictDetail, getDictDataListByType } from '@onebase/platform-center';
import styles from '../../index.module.less';

const FormItem = Form.Item;

export interface DynamicOptionsConfigProps {
  handlePropsChange: (key: string, value: string | number | boolean | any[]) => void;
  item: any;
  configs: any;
  id: string;
}

const DynamicOptionsConfig: React.FC<DynamicOptionsConfigProps> = ({ handlePropsChange, item, configs, id }) => {
  const selectKey = 'defaultOptions';
  useSignals();
  const { mainEntity, subEntities } = useAppEntityStore();

  const [selectOptionsConfig, setSelectOptionsConfig] = useState<any[]>([]);
  const [selectDisabled, setSelectDisabled] = useState<boolean>(false);

  useEffect(() => {
    setSelectOptionsConfig(configs[selectKey] || []);
    getDefaultOptions();
  }, [configs[selectKey]]);

  useEffect(() => {
    setSelectDisabled(false);
    getDefaultOptions(true);
  }, [configs.id]);

  const getDefaultOptions = async (flag?: boolean) => {
    const value = configs.dataField;
    const isMainEntity = value?.includes(mainEntity.entityId);
    const currentMainField = mainEntity.fields?.find((ele: any) => value.includes(ele.fieldId));
    const isSubEntity = subEntities.entities?.find((ele) => value?.includes(ele.entityId));
    const currentSubField = isSubEntity?.fields.find((ele: any) => value.includes(ele.fieldId));
    if (isMainEntity && currentMainField) {
      // 主表
      if (currentMainField.dictTypeId) {
        const res = await getDictDetail(currentMainField.dictTypeId);
        const dictDataList = res?.type ? await getDictDataListByType(res.type) : [];
        const dictOptions = dictDataList?.filter((e: any) => e.status === 1); // 只显示启用状态的字典数据
        if (dictOptions.length) {
          if (flag) {
            handlePropsChange(selectKey, dictOptions);
          }
          setSelectDisabled(true);
        }
      } else if (currentMainField.options?.length) {
        const newOptions = currentMainField.options?.map((e) => ({
          chosen: currentMainField.defaultValue && e.optionValue === currentMainField.defaultValue,
          label: e.optionLabel,
          value: e.optionValue
        }));
        if (flag) {
          handlePropsChange(selectKey, newOptions);
        }
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
            handlePropsChange(selectKey, dictOptions);
          }
          setSelectDisabled(true);
        }
      } else if (currentSubField.options?.length) {
        const newOptions = currentSubField.options?.map((e) => ({
          chosen: currentSubField.defaultValue && e.optionValue === currentSubField.defaultValue,
          label: e.optionLabel,
          value: e.optionValue
        }));
        if (flag) {
          handlePropsChange(selectKey, newOptions);
        }
        setSelectDisabled(true);
      }
    }
  };

  return (
    <>
      <FormItem layout="vertical" labelAlign="left" label={'自定义配置'} className={styles.formItem}>
        <Form.List initialValue={selectOptionsConfig} field={`${id}-${selectKey}`}>
          {(_fields, { add, remove }) => (
            <div className={styles.tableColumnList}>
              <ReactSortable
                list={selectOptionsConfig}
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
                  const newList = [...selectOptionsConfig];
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
                    handlePropsChange(selectKey, movedList);
                  }
                }}
              >
                {selectOptionsConfig.map((_col: any, idx: number) => (
                  <Tooltip key={idx} content="如需修改请前往数据建模" disabled={!selectDisabled}>
                    <div key={idx} className={styles.tableColumnItem}>
                      <Space>
                        <IconDragDotVertical
                          // 支持拖拽的图标，别误删了：）
                          className="table-col-item-handle"
                          style={{
                            cursor: 'move',
                            color: '#555'
                          }}
                        />
                        {/* <Radio
                        checked={configs[radioKey][idx].chosen || false}
                        onChange={(e) => {
                          let newList = [...radioConfig];
                          newList = newList.map((item) => ({ ...item, chosen: false }));
                          newList[idx] = {
                            ...newList[idx],
                            chosen: true
                          };
                          setRadioConfig(newList);
                          handlePropsChange(radioKey, newList);
                        }}
                      /> */}
                        <Input
                          size="small"
                          disabled={selectDisabled}
                          value={selectOptionsConfig[idx].label}
                          onChange={(e) => {
                            const newList = [...selectOptionsConfig];
                            newList[idx] = {
                              ...newList[idx],
                              label: e,
                              value: e
                            };
                            setSelectOptionsConfig(newList);
                            handlePropsChange(selectKey, newList);
                          }}
                          className={styles.tableColumnItemInput}
                          placeholder={'新选项'}
                        />
                        {!selectDisabled && (
                          <Button
                            icon={<IconDelete />}
                            shape="circle"
                            size="mini"
                            status="danger"
                            className={styles.tableColumnItemButton}
                            disabled={selectOptionsConfig.length <= 2}
                            onClick={() => {
                              const newList = [...selectOptionsConfig];
                              newList.splice(idx, 1);
                              setSelectOptionsConfig(newList);
                              handlePropsChange(selectKey, newList);
                              remove(idx);
                            }}
                          />
                        )}
                      </Space>
                    </div>
                  </Tooltip>
                ))}
              </ReactSortable>

              {!selectDisabled && (
                <Button
                  type="outline"
                  onClick={() => {
                    // 随机生成6位字母，这都能重复建议去买彩票：）
                    const newLabel = `新选项_${Array.from({ length: 6 }, () => String.fromCharCode(97 + Math.floor(Math.random() * 26))).join('')}`;
                    const newValue = newLabel;
                    const newList = [
                      ...selectOptionsConfig,
                      { label: item.displayName || newLabel, value: item.fieldName || newValue }
                    ];
                    console.log('newList: ', newList, _fields);
                    add({ label: item.displayName || newLabel, value: item.fieldName || newValue });
                    setSelectOptionsConfig(newList);
                    handlePropsChange(selectKey, newList);
                  }}
                >
                  添加一项
                </Button>
              )}
            </div>
          )}
        </Form.List>
      </FormItem>
    </>
  );
};

export default DynamicOptionsConfig;

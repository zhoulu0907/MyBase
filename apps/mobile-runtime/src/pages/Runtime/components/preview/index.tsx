// import ExecuteFlows from '@/utils/flow';
import { Button, Form, PopupSwiper, Toast } from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';
import dayjs from 'dayjs';
import {
  CATEGORY_TYPE,
  dataMethodCreateV2,
  dataMethodDetailV2,
  dataMethodUpdateV2,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  type AppEntityField,
  type DetailMethodV2Params,
  type GetPageSetIdReq,
  type InsertMethodV2Params,
  type UpdateMethodV2Params
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';

import {
  EDITOR_TYPES,
  ENTITY_FIELD_TYPE,
  FORM_COMPONENT_TYPES,
  getComponentWidth,
  getWorkbenchComponentWidth,
  SHOW_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useFormEditorSignal,
  useWorkbenchEditorSignal,
  startLoadWorkbenchPageSet,
  type GridItem,
  type WorkbenchComponentType
} from '@onebase/ui-kit';
import { getFileUrlById } from '@onebase/platform-center';

import CustomNav from '@/pages/components/Nav';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { startLoadPageSet, useEditorSignalMap, useListEditorSignal } from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import { splitByDivider } from '@/utils/tree';
import styles from './index.module.less';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
  pageSetType?: PageType;
}

const colorConfig = {
  normal: 'rgb(var(--primary-6))',
  active: 'rgb(var(--primary-9))',
  disabled: 'rgb(var(--primary-1))'
};

const ghostBgColor = {
  normal: '#FFF',
  active: 'rgb(var(--primary-6))',
  disabled: '#FFF'
};

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime, pageSetType }) => {
  useSignals();

  const [form] = useForm();

  const { pageComponentSchemas } = useFormEditorSignal;
  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;
  const { workbenchComponents, wbComponentSchemas } = useWorkbenchEditorSignal;

  const {
    curPage,
    drawerVisible,
    setDrawerVisible,
    editPageViewId,
    detailPageViewId,
    mainMetaDataFields,
    setMainMetaDataFields,
    subEntities,
    setSubEntities
  } = pagesRuntimeSignal;
  const [pageSetId, setPageSetId] = useState('');
  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [tableName, setTableName] = useState<string>('');
  const [editTargetId, setEditTargetId] = useState('');
  const [editLoading, setEditLoading] = useState(false);

  // 当前时间戳
  const [detailMode, setDetailMode] = useState(true);
  const [formDetails, setFormDetails] = useState<any>({}); // 表单数据
  const [refresh, setRefresh] = useState(Date.now());

  useEffect(() => {
    if (drawerVisible.value) {
      setDetailMode(true);
    }
  }, [drawerVisible.value]);

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaDataId = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaDataId: ', mainMetaDataId);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaDataId);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

    setTableName(entityWithChildren.tableName);

    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);
  };

  useEffect(() => {
    if (menuId) {
      handleGetPageSetId(menuId);
      setEditTargetId('');
    }
  }, [menuId]);

  useEffect(() => {
    if (editTargetId && tableName && mainMetaDataFields.value.length > 0) {
      // TODO: mainMetaData 换成 entityName
      //   handleGetData(mainMetaData, editTargetId);
    }
  }, [tableName, mainMetaDataFields.value]);

  useEffect(() => {
    if (pageSetId) {
      // 工作台页面使用专门的加载方法，不获取主表数据
      if (pageSetType === PageType.WORKBENCH) {
        startLoadWorkbenchPageSet({ pageSetId });
      } else {
        loadPageSetInfo(pageSetId);
        getMainMetaData(pageSetId);
      }
    }
    // 优先切换到列表页
    setPageType(pageSetType === PageType.WORKBENCH ? EDITOR_TYPES.WORKBENCH_EDITOR : EDITOR_TYPES.LIST_EDITOR);
  }, [pageSetId]);

  const handleGetPageSetId = async (menuId: string) => {
    const req: GetPageSetIdReq = { menuId: menuId };
    const res = await getPageSetId(req);
    setPageSetId(res);
  };

  const loadPageSetInfo = async (pageSetId: string) => {
    startLoadPageSet({ pageSetId: pageSetId });
  };

  // 信息收集弹窗
  const [flows, setFlows] = useState<any[]>([]);
  const [inputParams, setInputParams] = useState<any>({});

  const resetImageFile = (formData: any, field: { fieldType: string; fieldName: string }, value: any) => {
    const filterByUpload = ['IMAGE', 'FILE'];
    if (filterByUpload.includes(field.fieldType) && Array.isArray(value)) {
      formData[field.fieldName] = value.map((item: any) => {
        if ((item.response || item.id) && item.name) {
          return {
            name: item.name,
            id: item.response || item.id
          };
        }
        return item;
      });
      return true;
    }
  };
  // 提交表单
  const submitForm = async () => {
    await form.validateFields();
    const fields = form.getFieldsValue();

    console.log('fields: ', fields);
    console.log('mainMetaDataFields: ', mainMetaDataFields.value);
    console.log('menuId: ', menuId);

    const formData = {} as any;
    const subFormData = {} as any;
    const groups = {} as any;
    let subEntityUuid: string = '';
    Object.entries(fields).forEach(([key, value]) => {
      console.log('key: ', key, '   value: ', value);
      // 处理主表逻辑
      const field = (mainMetaDataFields.value || [])
        .filter((f) => f.isSystemField !== 1)
        .find((f: AppEntityField) => f.fieldName == key);
      if (field) {
        console.log('field: ', field);

        if (resetImageFile(formData, field, value)) {
          // do nothing
        } else if (field.fieldType === ENTITY_FIELD_TYPE.DATE.VALUE) {
          formData[field.fieldName] = value ? dayjs(value).format('YYYY-MM-DD') : '';
        } else if (field.fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE) {
          formData[field.fieldName] = value ? dayjs(value).format('YYYY-MM-DD hh:mm:ss') : '';
        } else if (field.fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE) {
          formData[field.fieldName] = {
            id: value[0],
            name: value[0]
          };
        } else if (field.fieldType === ENTITY_FIELD_TYPE.USER.VALUE) {
          if (Array.isArray(value)) {
            const userData = formDetails?.[key];
            formData[field.fieldName] = {
              id: userData?.id,
              name: value[0]
            };
          } else {
            formData[field.fieldName] = {
              id: value.id,
              name: value.name
            };
          }
        } else {
          formData[field.fieldName] = value;
        }
      }
      !(typeof value === 'object') &&
        Object.values(listPageComponentSchemas.value).forEach((item) => {
          if (!item.config.columns || item.config.status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN]) {
            return;
          }
          const indexTmp = item.config.columns.findIndex((col: any) => col.id === field?.fieldId);

          if (indexTmp === -1) {
            delete formData[field?.fieldId];
          }
        });

      // 处理子表逻辑
      if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE)) {
        const parts = key.split('.');

        subEntityUuid = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[parts[0]]?.config
          ?.subTable;

        const groupIndex = parts[parts.length - 2];
        const fieldName = parts[parts.length - 1];

        if (!groups[groupIndex]) {
          groups[groupIndex] = {};
        }

        const fieldType = subEntities.value[0].childFields.find((v) => v.fieldName === fieldName).fieldType;
        if (resetImageFile(groups[groupIndex], { fieldType, fieldName }, value)) {
        } else if (fieldType === ENTITY_FIELD_TYPE.DATE.VALUE) {
          groups[groupIndex][fieldName] = value ? dayjs(value).format('YYYY-MM-DD') : '';
        } else if (fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE) {
          groups[groupIndex][fieldName] = value ? dayjs(value).format('YYYY-MM-DD hh:mm:ss') : '';
        } else if (fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE) {
          groups[groupIndex][fieldName] = {
            id: value[0],
            name: value[0]
          };
        } else if (fieldType === ENTITY_FIELD_TYPE.USER.VALUE) {
          if (Array.isArray(value)) {
            const userData = formDetails?.[key];
            groups[groupIndex][fieldName] = {
              id: userData?.id,
              name: value[0]
            };
          } else {
            groups[groupIndex][fieldName] = {
              id: value.id,
              name: value.name
            };
          }
        } else {
          groups[groupIndex][fieldName] = value;
        }

        console.log('xxx---', fieldName, value, fieldType);
      }
    });

    const subData = Object.keys(groups)
      .sort((a, b) => Number(a) - Number(b))
      .map((k) => groups[k]);

    const subTableName = subEntities.value.find((ele: any) => ele.childEntityUuid == subEntityUuid)?.childTableName;
    console.log('subTableName', subTableName, subData);
    if (subTableName) {
      subFormData[subTableName] = subData;
    }

    console.log('formData:   ', formData);
    console.log('subFormData:   ', subFormData);
    // return;

    // 接口判断 页面触发
    const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const pageId = curFormPage?.id;
    const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
    setInputParams(formData);

    console.log('editTargetId: ', editTargetId);

    if (editTargetId) {
      const req: UpdateMethodV2Params = {
        id: editTargetId,
        ...formData,
        ...subFormData
      };
      console.log('req: ', req);
      const res = await dataMethodUpdateV2(tableName, menuId, req);
      console.log(res);

      const updateFlows = (flowRes || []).filter(
        (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.UPDATE)
      );
      setFlows(updateFlows);
      if (res) {
        Toast.success('更新成功');
        setPageType(EDITOR_TYPES.LIST_EDITOR);
      }
      setEditTargetId('');
      setDrawerVisible(false);
      setPageType(EDITOR_TYPES.LIST_EDITOR);
      setRefresh(Date.now());
    } else {
      try {
        let res = null;
        if (curPage?.value?.pageSetType === PageType.BPM) {
          const reqFlow = {
            isDraft: false,
            formName: curPage?.value?.pages?.find((page: any) => page.pageType === CATEGORY_TYPE.FORM)?.pageName || '',
            businessId: curPage?.value?.id,
            entity: {
              entityId: tableName,
              data: { ...formData, ...subFormData }
            }
          };
          res = await fetchSubmitInstance(reqFlow);
          setPageType(EDITOR_TYPES.FORM_EDITOR);
        } else {
          console.log(formData);
          const req: InsertMethodV2Params = { ...formData, ...subFormData };
          console.log(req);

          res = await dataMethodCreateV2(tableName, menuId, req);
          console.log(res);

          setPageType(EDITOR_TYPES.LIST_EDITOR);
        }

        const createFlows = (flowRes || []).filter(
          (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE)
        );
        setFlows(createFlows);

        if (res) {
          Toast.success('创建成功');
        }
        setPageType(EDITOR_TYPES.LIST_EDITOR);
      } catch (error) {
        Toast.error('创建失败');
      }
    }

    // 关闭页面后子表清空
    pagesRuntimeSignal.resetSubTableDataLength();
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
    form.resetFields();
    // 关闭页面后子表清空
    pagesRuntimeSignal.resetSubTableDataLength();
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    form.resetFields();

    if (id && id !== '') {
      // console.log('edit row id: ', id);
      setEditTargetId(id);
      if (tableName) {
        handleGetData(id);
      }
    } else {
      // id为空，属于新增，需要重置子表数据长度为0
      setEditTargetId('');
      pagesRuntimeSignal.resetSubTableDataLength();
    }

    if (toFormPage) {
      setPageType(EDITOR_TYPES.FORM_EDITOR);
    }
  };

  const handleGetData = async (id: string) => {
    setEditLoading(true);
    const req: DetailMethodV2Params = {
      id: id
    };
    const res = await dataMethodDetailV2(tableName, menuId, req);
    // console.log('xxx=====', res);

    // 遍历 res.data，将数据回填到表单
    const formValues: Record<string, any> = {};

    if (res) {
      setFormDetails(res);
      const dataItem = res;
      const fieldIdNameMap: Record<string, string> = {};
      (mainMetaDataFields.value || []).forEach((field: AppEntityField) => {
        fieldIdNameMap[field.fieldName] = field.fieldId;
      });

      if (dataItem && typeof dataItem === 'object') {
        Object.entries(dataItem).forEach(([fieldName, value]: [string, any]) => {
          const fieldType = mainMetaDataFields.value.find((v) => v.fieldId === fieldIdNameMap[fieldName])?.fieldType;
          if (fieldType) {
            if (fieldType === ENTITY_FIELD_TYPE.DATE.VALUE || fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE) {
              formValues[fieldName] = dayjs(value).valueOf();
            } else if (fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE) {
              const curComponentSchema = Object.values(pageComponentSchemas.value).find(v => value.id.includes(v.id)) || {};
              const curOptions = curComponentSchema?.config?.defaultOptionsConfig?.defaultOptions;
              const renderValue = curOptions.find(op => op.value === value.id)?.label || '-';
              formValues[fieldName] = [renderValue];
            } else if (fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE) {
              formValues[fieldName] = value.map((v) => v.id) || [];
            } else if (fieldType === ENTITY_FIELD_TYPE.USER.VALUE) {
              formValues[fieldName] = Object.entries(value).length > 0 ? [value.name] : value;
            } else if (fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE) {
              formValues[fieldName] = value;
            } else if (fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE || fieldType === ENTITY_FIELD_TYPE.FILE.VALUE) {
              formValues[fieldName] = value.map((item: any) => {
                return {
                  ...item,
                  name: item.name,
                  id: item.id,
                  response: item.response || item.id,
                  url: getFileUrlById(item.id)
                };
              });
            } else {
              formValues[fieldName] = value;
            }
          }
        });
      }

      // 子表渲染逻辑
      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;

      for (const subEntity of subEntities.value) {
        if (
          dataItem &&
          subEntity.childTableName &&
          Object.prototype.hasOwnProperty.call(dataItem, subEntity.childTableName)
        ) {
          console.log(`找到子表 ${subEntity.childTableName} 数据:`, dataItem[subEntity.childTableName]);

          const subData = dataItem[subEntity.childTableName];

          Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
            if (
              key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE) &&
              schema?.config?.subTable == subEntity.childEntityUuid
            ) {
              pagesRuntimeSignal.setSubTableDataLength(key, (subData || []).length);

              for (let idx = 0; idx < (subData || []).length; idx++) {
                const keys = Object.keys((subData || [])[idx]);
                for (let ele in componentSchemas) {
                  const config = componentSchemas[ele]?.config;
                  const fieldName = config?.dataField?.[1];
                  const fieldType = subEntity.childFields.find((v) => v.fieldName === fieldName)?.fieldType;

                  if (keys.includes(fieldName)) {
                    if (fieldType === ENTITY_FIELD_TYPE.DATE.VALUE || fieldType === ENTITY_FIELD_TYPE.DATETIME.VALUE) {
                      formValues[`${key}.${idx}.${fieldName}`] = dayjs(subData[idx]?.[fieldName]).valueOf();
                    } else if (fieldType === ENTITY_FIELD_TYPE.SELECT.VALUE) {
                      const value = subData[idx]?.[fieldName];
                      const renderValue = config.defaultOptionsConfig.defaultOptions.find(v => v.value === value.id)?.label || '-';
                      formValues[`${key}.${idx}.${fieldName}`] = [renderValue];
                    } else if (fieldType === ENTITY_FIELD_TYPE.MULTI_SELECT.VALUE) {
                      const value = subData[idx]?.[fieldName];
                      formValues[`${key}.${idx}.${fieldName}`] = value.map((v) => v.id) || [];
                    } else if (fieldType === ENTITY_FIELD_TYPE.USER.VALUE) {
                      const value = subData[idx]?.[fieldName];
                      formValues[`${key}.${idx}.${fieldName}`] =
                        Object.entries(value).length > 0 ? [value.name] : value;
                    } else if (fieldType === ENTITY_FIELD_TYPE.DEPARTMENT.VALUE) {
                      formValues[`${key}.${idx}.${fieldName}`] = subData[idx]?.[fieldName];
                    } else if (
                      fieldType === ENTITY_FIELD_TYPE.IMAGE.VALUE ||
                      fieldType === ENTITY_FIELD_TYPE.FILE.VALUE
                    ) {
                      formValues[`${key}.${idx}.${fieldName}`] = subData[idx]?.[fieldName].map((item: any) => {
                        return {
                          ...item,
                          name: item.name,
                          id: item.id,
                          response: item.response || item.id,
                          url: getFileUrlById(item.id)
                        };
                      });
                    } else {
                      formValues[`${key}.${idx}.${fieldName}`] = subData[idx]?.[fieldName];
                    }
                  }
                }
                // 补充id
                formValues[`${key}.${idx}.id`] = subData[idx]?.id;
              }
            }
          });
        }
      }
    }

    console.log('formValues: ', formValues);
    setTimeout(() => {
      form.setFieldsValue(formValues);
    }, 100);
    setTimeout(() => {
      setEditLoading(false);
    }, 200);
    return res;
  };

  const toEditMode = () => {
    setDetailMode(false);
  };

  const curFormPage =
    curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.LIST)?.pageName || '标题_列表';

  return (
    <div className={styles.previewPage}>
      {/* <Sticky topOffset={0} className={styles.previewTitle}>
        {curFormPage.slice(0, curFormPage.length - 3)}
      </Sticky> */}
      <CustomNav
        title={curFormPage.slice(0, curFormPage.length - 3)}
        style={{ background: '#fff' }}
        toBack={pageType === EDITOR_TYPES.LIST_EDITOR ? undefined : () => setPageType(EDITOR_TYPES.LIST_EDITOR)}
      />

      <div className={styles.content}>
        {/* 工作台页面渲染 */}
        {pageSetType === PageType.WORKBENCH && (
          <>
            {workbenchComponents.value.map((cp: GridItem) => {
              const schema = wbComponentSchemas.value[cp.id];
              return (
                <Fragment key={cp.id}>
                  <div
                    className={styles.componentItem}
                    style={{
                      width: `calc(${getWorkbenchComponentWidth(schema, cp.type as WorkbenchComponentType)} - 8px)`,
                      margin: '4px'
                    }}
                  >
                    <PreviewRender cpId={cp.id} cpType={cp.type} pageComponentSchema={schema} runtime={runtime} />
                  </div>
                </Fragment>
              );
            })}
          </>
        )}

        {/* 列表页面渲染 */}
        {pageSetType !== PageType.WORKBENCH && pageType === EDITOR_TYPES.LIST_EDITOR &&
          (!listComponents.value?.length ? (
            <div className={styles.noData}>暂无数据</div>
          ) : (
            listComponents.value.map((cp: GridItem, index: number) => (
              <Fragment key={cp.id}>
                {listPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div
                    key={cp.id}
                    className={styles.componentItem}
                    style={{
                      width: '100%'
                    }}
                  >
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageComponentSchema={listPageComponentSchemas.value[cp.id]}
                      runtime={runtime}
                      showFromPageData={showFromPageData}
                      refresh={refresh}
                      lastOne={index === listComponents.value.length - 1}
                    />
                  </div>
                )}
              </Fragment>
            ))
          ))}

        {pageSetType !== PageType.WORKBENCH && pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form} className={styles.formWrapper}>
            {splitByDivider(useEditorSignalMap.get(editPageViewId.value)?.components.value).map((block, index) => {
              if (block.type === SHOW_COMPONENT_TYPES.DIVIDER) {
                return (
                  <Fragment key={index}>
                    <PreviewRender
                      cpId={block.item.id}
                      cpType={block.item.type}
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[block.item.id]
                      }
                      editLoading={editLoading}
                      form={form}
                      runtime={true}
                      showFromPageData={() => {
                        setPageType(EDITOR_TYPES.FORM_EDITOR);
                      }}
                    />
                  </Fragment>
                );
              }
              return (
                <div className={styles.formComp} key={index}>
                  {block.items.map(cp => (
                    <Fragment key={cp.id}>
                      {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                        STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                        <div key={cp.id} className={styles.componentItem}>
                          <PreviewRender
                            cpId={cp.id}
                            cpType={cp.type}
                            pageComponentSchema={
                              useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                            }
                            editLoading={editLoading}
                            form={form}
                            runtime={true}
                            showFromPageData={() => {
                              setPageType(EDITOR_TYPES.FORM_EDITOR);
                            }}
                          />
                        </div>
                      )}
                    </Fragment>
                  ))}
                </div>
              );
            })}

            <div className={styles.footer}>
              <Button
                type="ghost"
                color={colorConfig}
                bgColor={ghostBgColor}
                borderColor={colorConfig}
                onClick={cancelSubmitForm}
                style={{ flex: 2 }}
              >
                取消
              </Button>
              <Button
                type="primary"
                bgColor={colorConfig}
                borderColor={colorConfig}
                onClick={submitForm}
                style={{ flex: 5 }}
              >
                提交
              </Button>
            </div>
          </Form>
        )}

        {/* 右侧详情抽屉 */}
        <PopupSwiper
          // title={
          //   <div className={styles.drawerTitle}>
          //     <div>详情</div>
          //     {/* {detailMode && (
          //       <Button type="primary" onClick={() => toEditMode()}>
          //         编辑
          //       </Button>
          //     )} */}
          //   </div>
          // }
          visible={drawerVisible.value}
          close={() => {
            setDrawerVisible(false);
          }}
        >
          <div className={styles.content}>
            <Form layout="inline" form={form}>
              {useEditorSignalMap.get(detailPageViewId.value)?.components.value.map((cp: GridItem) => (
                <Fragment key={cp.id}>
                  {useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                    STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                    <div
                      key={cp.id}
                      className={styles.componentItem}
                      style={{
                        width: getComponentWidth(
                          useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id],
                          cp.type
                        )
                      }}
                    >
                      <PreviewRender
                        cpId={cp.id}
                        cpType={cp.type}
                        pageComponentSchema={
                          useEditorSignalMap.get(detailPageViewId.value)?.pageComponentSchemas.value[cp.id]
                        }
                        runtime={true}
                        detailMode={detailMode}
                        showFromPageData={() => {}}
                      />
                    </div>
                  )}
                </Fragment>
              ))}

              {!detailMode && (
                <div className={styles.footer}>
                  <Button type="primary" onClick={submitForm}>
                    更新
                  </Button>
                  <Button type="default" onClick={cancelSubmitForm}>
                    取消
                  </Button>
                </div>
              )}
            </Form>
          </div>
        </PopupSwiper>
      </div>

      {/* 信息收集弹窗 */}
      {/* <ExecuteFlows flows={flows} inputParams={inputParams}></ExecuteFlows> */}
    </div>
  );
};

export default PreviewContainer;

import ExecuteFlows from '@/utils/flow';
import { Button, Form, PopupSwiper, Toast } from '@arco-design/mobile-react';
import { useForm } from '@arco-design/mobile-react/esm/form';

import {
  CATEGORY_TYPE,
  dataMethodData,
  dataMethodInsert,
  dataMethodUpdate,
  getEntityFieldsWithChildren,
  getPageSetId,
  getPageSetMetaData,
  PageType,
  queryFlowExecForm,
  TRIGGER_EVENTS,
  type AppEntityField,
  type DataMethodParam,
  type GetPageSetIdReq,
  type InsertMethodParams,
  type UpdateMethodParams
} from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';

import {
  EDITOR_TYPES,
  FORM_COMPONENT_TYPES,
  getComponentWidth,
  STATUS_OPTIONS,
  STATUS_VALUES,
  type GridItem
} from '@onebase/ui-kit';

import CustomNav from '@/pages/components/Nav';
import { fetchSubmitInstance } from '@onebase/app/src/services/app_runtime';
import { startLoadPageSet, useEditorSignalMap, useListEditorSignal } from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';
import { useSignals } from '@preact/signals-react/runtime';
import React, { Fragment, useEffect, useState } from 'react';
import styles from './index.module.less';

interface PreviewProps {
  menuId: string;
  runtime: boolean;
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

const PreviewContainer: React.FC<PreviewProps> = ({ menuId, runtime }) => {
  useSignals();

  const [form] = useForm();

  const { components: listComponents, pageComponentSchemas: listPageComponentSchemas } = useListEditorSignal;

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

  const [editTargetId, setEditTargetId] = useState('');

  // 当前时间戳
  const [detailMode, setDetailMode] = useState(true);
  const [refresh, setRefresh] = useState(Date.now());

  useEffect(() => {
    if (drawerVisible.value) {
      setDetailMode(true);
    }
  }, [drawerVisible.value]);

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    console.log('mainMetaData: ', mainMetaData);
    setMainMetaData(mainMetaData);

    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    console.log('当前主表及所有子表数据: ', entityWithChildren);

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
    if (editTargetId && mainMetaData && mainMetaDataFields.value.length > 0) {
      handleGetData(mainMetaData, editTargetId);
    }
  }, [mainMetaData, mainMetaDataFields.value]);

  useEffect(() => {
    if (pageSetId) {
      loadPageSetInfo(pageSetId);
      getMainMetaData(pageSetId);
    }
    // 优先切换到列表页
    setPageType(EDITOR_TYPES.LIST_EDITOR);
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

  // 提交表单
  const submitForm = async () => {
    await form.validateFields();
    const fields = form.getFieldsValue();

    // console.log('fields: ', fields);
    // console.log('mainMetaDataFields: ', mainMetaDataFields.value);
    // console.log('menuId: ', menuId);

    const formData = {} as any;
    const subFormData = [] as any;
    const groups = {} as any;
    let subEntityId: string = '';
    Object.entries(fields).forEach(([key, value]) => {
      console.log('key: ', key, '   value: ', value);

      // 处理主表逻辑
      const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldId == key);
      // listPageComponentSchemas.value[cp.id].config.status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
      if (field) {
        // console.log('field: ', field);
        formData[field.fieldId] = value;
        const filterType = ['IMAGE', 'FILE'];
        if (filterType.includes(field.fieldType) && Array.isArray(value)) {
          formData[field.fieldId] = value.map((item: any) => {
            if (item.response && item.url) {
              return {
                ...item,
                url: item.response
              };
            }
            return item;
          });
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

        subEntityId = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[parts[0]]?.config
          ?.subTable;

        const groupIndex = parts[parts.length - 2];
        const fieldId = parts[parts.length - 1];

        if (!groups[groupIndex]) {
          groups[groupIndex] = {};
        }

        groups[groupIndex][fieldId] = value;
      }
    });

    const subData = Object.keys(groups)
      .sort((a, b) => Number(a) - Number(b))
      .map((k) => groups[k]);

    if (subEntityId) {
      subFormData.push({
        subEntityId,
        subData
      });
    }

    console.log('formData:   ', formData);
    console.log('subFormData:   ', subFormData);

    // 接口判断 页面触发
    const curFormPage = curPage.value?.pages?.find((ele: any) => ele.pageType === CATEGORY_TYPE.FORM);
    const pageId = curFormPage?.id;
    const flowRes = pageId ? await queryFlowExecForm(pageId) : [];
    setInputParams(formData);

    console.log('editTargetId: ', editTargetId);

    if (editTargetId) {
      const req: UpdateMethodParams = {
        menuId: menuId,
        entityId: mainMetaData,
        id: editTargetId,
        data: formData,
        subEntities: subFormData
      };
      const res = await dataMethodUpdate(req);
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
              entityId: mainMetaData,
              data: formData
            }
          };
          res = await fetchSubmitInstance(reqFlow);
          setPageType(EDITOR_TYPES.FORM_EDITOR);
        } else {
          const req: InsertMethodParams = {
            menuId: menuId,
            entityId: mainMetaData,
            data: formData,
            subEntities: subFormData
          };
          res = await dataMethodInsert(req);

          setPageType(EDITOR_TYPES.LIST_EDITOR);
        }

        const createFlows = (flowRes || []).filter(
          (ele: any) => ele.recordTriggerEvents && ele.recordTriggerEvents.includes(TRIGGER_EVENTS.CREATE)
        );
        setFlows(createFlows);

        if (res) {
          Toast.success('创建成功');
        }
      } catch (error) {
        Toast.error('创建失败');
      }
    }
  };

  const cancelSubmitForm = () => {
    console.log('取消提交');

    setPageType(EDITOR_TYPES.LIST_EDITOR);
    setDetailMode(true);
  };

  const showFromPageData = (id: string, toFormPage: boolean = false) => {
    form.resetFields();

    if (id && id !== '') {
      // console.log('edit row id: ', id);
      setEditTargetId(id);
      if (mainMetaData) {
        handleGetData(mainMetaData, id);
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

  const handleGetData = async (entityId: string, id: string) => {
    const req: DataMethodParam = {
      menuId: menuId,
      entityId: entityId,
      id: id
    };
    const res = await dataMethodData(req);
    // console.log('xxx=====', res);

    // 遍历 res.data，将数据回填到表单
    const formValues: Record<string, any> = {};

    if (res && res.data) {
      console.log('res.data: ', res.data);
      const fieldIdNameMap: Record<string, string> = {};
      (mainMetaDataFields.value || []).forEach((field: AppEntityField) => {
        fieldIdNameMap[field.fieldName] = field.fieldId;
      });

      // console.log('fieldIdNameMap: ', fieldIdNameMap);

      // 只处理第一个数据对象（通常为单条数据）
      const dataItem = Array.isArray(res.data) ? res.data[0] : res.data;
      const arrayType = [
        'DATE',
        'DATETIME',
        'SELECT',
        'MULTI_SELECT',
        'MULTI_DEPARTMENT',
        'DATA_SELECTION',
        'MULTI_DATA_SELECTION',
        'MULTI_USER',
        'USER'
      ]; // 表单回显需要数组格式数据；

      if (dataItem && typeof dataItem === 'object') {
        Object.entries(dataItem).forEach(([fieldName, value]) => {
          const fieldID = fieldIdNameMap[fieldName];
          if (fieldID) {
            const fieldType = mainMetaDataFields.value.find((v) => v.fieldId === fieldID)?.fieldType;
            if (arrayType.includes(fieldType)) {
              formValues[fieldID] = [value];
              return;
            }
            formValues[fieldID] = value;
            console.warn('fieldID:', fieldID, 'fieldName:', fieldName, '    value:', value);
          }
        });
      }
    }

    // TODO(mickey): remove debug log
    if (res && res.subEntities) {
      console.log('subEntities: ', res.subEntities);

      console.log(useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value);

      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;

      for (const subEntity of res.subEntities) {
        const targetSubEntity = subEntities.value.find((ele: any) => ele.childEntityId == subEntity.subEntityId);
        // console.log('已找到目标子表: ', targetSubEntity);

        if (targetSubEntity) {
          Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
            if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE) && schema?.config?.subTable == subEntity.subEntityId) {
              // console.log('subEntity.data: ', subEntity.subData);

              pagesRuntimeSignal.setSubTableDataLength(key, (subEntity.subData || []).length);

              for (let idx = 0; idx < (subEntity.subData || []).length; idx++) {
                const keys = Object.keys((subEntity.subData || [])[idx]);
                for (let ele in componentSchemas) {
                  const config = componentSchemas[ele]?.config;
                  const fieldId = config?.dataField?.[1];
                  if (keys.includes(fieldId)) {
                    formValues[`${key}.${idx}.${fieldId}`] = subEntity.subData[idx]?.[fieldId];
                  }
                }
              }
            }
          });
        }
      }
    }

    console.log('formValues: ', formValues);
    form.setFieldsValue(formValues);

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
        {pageType === EDITOR_TYPES.LIST_EDITOR &&
          (!listComponents.value?.length ? (
            <div className={styles.noData}>暂无数据</div>
          ) : (
            listComponents.value.map((cp: GridItem) => (
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
                    />
                  </div>
                )}
              </Fragment>
            ))
          ))}

        {pageType == EDITOR_TYPES.FORM_EDITOR && (
          <Form layout="inline" form={form} className={styles.formWrapper}>
            {useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
              <Fragment key={cp.id}>
                {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                  STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                  <div key={cp.id} className={styles.componentItem} style={{ width: '100%' }}>
                    <PreviewRender
                      cpId={cp.id}
                      cpType={cp.type}
                      pageComponentSchema={
                        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id]
                      }
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
      <ExecuteFlows flows={flows} inputParams={inputParams}></ExecuteFlows>
    </div>
  );
};

export default PreviewContainer;

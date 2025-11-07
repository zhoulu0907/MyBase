import React, { Fragment, useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import { Form } from '@arco-design/web-react';
import { getEntityFieldsWithChildren, getPageSetMetaData, type AppEntityField } from '@onebase/app';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  EDITOR_TYPES,
  FORM_COMPONENT_TYPES,
  getComponentWidth,
  PreviewRender,
  startLoadPageSet,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  type GridItem
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';

interface PreviewProps {
  pageSetId: string;
  detailData: any;
}

const PreviewContainer: React.FC<PreviewProps> = forwardRef((props: any, ref: any) => {
  // 启用 signal 的响应式更新
  useSignals();
  // 直接访问 signal 的值，useSignals() 会确保组件在 signal 变化时重新渲染
  const { pageSetId, detailData } = props;
  const [form] = Form.useForm();
  const { editPageViewId, mainMetaDataFields, setMainMetaDataFields, subEntities, setSubEntities } = pagesRuntimeSignal;

  const [pageType, setPageType] = useState('');
  const [mainMetaData, setMainMetaData] = useState<string>('');
  const [newCompents, setNewCompents] = useState();

  useImperativeHandle(ref, () => ({
    getFormData: () => {
      const res = getFormValues();
      return res;
    }
  }));

  // 获取主表字段和子表字段
  const getMainMetaData = async (pageSetId: string) => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    setMainMetaData(mainMetaData);
    const entityWithChildren = await getEntityFieldsWithChildren(mainMetaData);
    setMainMetaDataFields(entityWithChildren.parentFields);
    setSubEntities(entityWithChildren.childEntities);
  };

  const updateComponentStatus = async () => {
    const pageComponentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
    const fieldPerm = detailData?.formData?.fieldPerm;

    if (pageComponentSchemas && fieldPerm) {
      const updatedData = {};
      Object.keys(pageComponentSchemas).forEach((key) => {
        const originalItem = pageComponentSchemas[key];
        const secondDataField = originalItem.config.dataField[1];
        const newStatus = fieldPerm[secondDataField] || originalItem.config.status;

        updatedData[key] = {
          ...originalItem,
          config: {
            ...originalItem.config,
            status: newStatus
          }
        };
      });
      setNewCompents(updatedData);
    }
  };
  const handleGetData = async () => {
    const res = detailData?.formData;
    const formValues: Record<string, any> = {};
    if (res && res.data) {
      const fieldIdNameMap: Record<string, string> = {};
      (mainMetaDataFields.value || []).forEach((field: AppEntityField) => {
        fieldIdNameMap[field.fieldName] = field.fieldId;
      });
      const dataItem = Array.isArray(res.data) ? res.data[0] : res.data;
      if (dataItem && typeof dataItem === 'object') {
        Object.entries(dataItem).forEach(([fieldName, value]) => {
          const fieldID = fieldIdNameMap[fieldName];
          if (fieldID) {
            formValues[fieldID] = value;
          }
        });
      }
    }

    if (res && res.subEntities) {
      const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
      for (const subEntity of res.subEntities) {
        const targetSubEntity = subEntities.value.find((ele: any) => ele.childEntityId == subEntity.subEntityId);
        if (targetSubEntity) {
          Object.entries(componentSchemas).forEach(([key, schema]: [string, any]) => {
            if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE) && schema?.config?.subTable == subEntity.subEntityId) {
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

    console.log('formValues: ', formValues, form);
    form.setFieldsValue(formValues);
  };
  // 提交表单
  const getFormValues = async () => {
    const fields = form.getFieldsValue();
    const formData = {} as any;
    const subFormData = [] as any;
    Object.entries(fields).forEach(([key, value]) => {
      // 处理主表逻辑
      const field = (mainMetaDataFields.value || []).find((f: AppEntityField) => f.fieldId == key);
      if (field) {
        console.log('field: ', field);
        formData[field.fieldId] = value;
      }

      if (key.startsWith(FORM_COMPONENT_TYPES.SUB_TABLE)) {
        const subEntityId = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[key]?.config
          ?.subTable;

        //   过滤空行
        const subTableRows = [] as any;
        for (const item of value) {
          if (Object.values(item).every((v: any) => v === undefined)) {
            return;
          }
          const keys = Object.keys(item);
          let temp: any = {};
          for (let key of keys) {
            const newKey = key.slice(key.lastIndexOf('.') + 1);
            temp[newKey] = item[key];
          }
          subTableRows.push(temp);
        }
        subFormData.push({
          subEntityId: subEntityId,
          subData: subTableRows
        });
      }
    });
    const dataObj = {
      entityId: mainMetaData,
      data: formData,
      subEntities: subFormData
    };
    return dataObj;
  };

  useEffect(() => {
    parseData();
  }, [mainMetaData, mainMetaDataFields.value, pageSetId]);

  const parseData = async () => {
    const res = await startLoadPageSet({ pageSetId: pageSetId });
    await updateComponentStatus();

    if (mainMetaData && mainMetaDataFields.value.length > 0) {
      await handleGetData(mainMetaData);
    }
  };

  useEffect(() => {
    getMainMetaData(pageSetId);
  }, [pageSetId]);

  return (
    <div>
      <Form layout="inline" form={form}>
        {newCompents &&
          useEditorSignalMap.get(editPageViewId.value)?.components.value.map((cp: GridItem) => (
            <Fragment key={cp.id}>
              {useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id].config.status !==
                STATUS_VALUES[STATUS_OPTIONS.HIDDEN] && (
                <div
                  key={cp.id}
                  style={{
                    width: getComponentWidth(
                      useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cp.id],
                      cp.type
                    )
                  }}
                >
                  <PreviewRender
                    cpId={cp.id}
                    cpType={cp.type}
                    pageType={pageType}
                    pageComponentSchema={newCompents && newCompents[cp.id]}
                    runtime={true}
                    showFromPageData={() => {
                      setPageType(EDITOR_TYPES.FORM_EDITOR);
                    }}
                  />
                </div>
              )}
            </Fragment>
          ))}
      </Form>
    </div>
  );
});
export default PreviewContainer;

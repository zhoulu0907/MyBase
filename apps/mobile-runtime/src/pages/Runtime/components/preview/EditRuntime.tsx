import React, { Fragment, useCallback, useEffect, useRef, useState } from 'react';
import { Button, Form, Input, Toast } from '@arco-design/mobile-react';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  SHOW_COMPONENT_TYPES,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  usePageEditorSignal,
  usePageViewEditorSignal
} from '@onebase/ui-kit';
import { PreviewRender } from '@onebase/ui-kit-mobile';

import { useSignals } from '@preact/signals-react/runtime';
import { initInteractionRule } from './interaction_rule';
import { splitByDivider, normalizeFormValues } from '@/utils';
import styles from './index.module.less';

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

interface EditRuntimeProps {
  form: any;
  isAdd: boolean;
  editLoading: boolean;
  submitLoading: boolean;
  onSubmit: () => void;
  onSaveSubmit: () => void;
  onSaveDraft: () => void;
  onCancel: () => void;
  menuId: string;
  tableName: string;

  mainEntity: any;
  subEntitiesValues: any;
  setEditLoading: (value: boolean) => void;
  showFromPageData?: Function;
}

const EditRuntime: React.FC<EditRuntimeProps> = ({
  form,
  isAdd,
  editLoading,
  submitLoading,
  onSubmit,
  onSaveSubmit,
  onSaveDraft,
  onCancel,
  menuId: _menuId,
  tableName: _tableName,

  mainEntity,
  subEntitiesValues,
  setEditLoading,
  showFromPageData
}) => {
  useSignals();

  const { pageViews, curViewId } = usePageViewEditorSignal;
  const { editPageViewId, curPage, subEntities } = pagesRuntimeSignal;

  const pageEditorSignal = usePageEditorSignal();

  const [cpStates, setCpStates] = useState<Record<string, any>>({});
  const [hasChanged, setHasChanged] = useState(false); // 防止修改表单某个值导致页面表单数据清空
  const [formValues, setFormValues] = useState({}); // form 实时改动后的值集合

  const isLoadingFromDraftBoxRef = useRef(false);

  useEffect(() => {
    if (hasChanged) {
      form.setFieldsValue(formValues);
      setHasChanged(false);
    }
  }, [formValues, hasChanged]);

  const handleFormValuesChange = useCallback(
    async (_value: Partial<any>, values: Partial<any>) => {
      const states = await initInteractionRule(
        values,
        pageViews.value[curViewId.value]?.interactionRules,
        useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value
      );
      setCpStates(states);
    },
    [pageViews.value, curViewId.value, editPageViewId.value]
  );

  // 载入草稿数据
  const handleLoadDraft = useCallback(
    async (draftData: any) => {
      if (draftData) {
        setEditLoading(true);
        console.log('latestDraft: ', draftData);
        const componentSchemas = useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value;
        const subTableComponents = useEditorSignalMap.get(editPageViewId.value)?.subTableComponents.value;
        const formValues = normalizeFormValues({
          dataItem: draftData,
          componentSchemas,
          subEntities: subEntities.value,
          subTableComponents,
          setSubTableDataLength: pagesRuntimeSignal.setSubTableDataLength
        });

        console.log('draft formValues', formValues);
        setTimeout(() => {
          form.setFieldValue('draftId', draftData.id);
          form.setFieldsValue(formValues);
        }, 100);
        setTimeout(() => {
          setEditLoading(false);
        }, 200);
        await handleFormValuesChange({}, formValues);
        // 触发表单值变化，更新组件状态
        Toast.success('已载入暂存数据');
      }
    },
    [editPageViewId.value, subEntities.value, form]
  );

  // 检查并载入草稿数据（仅当从草稿箱点击"继续编辑"时）
  useEffect(() => {
    if (isAdd) {
      // 如果是从草稿箱载入的，不重复处理
      if (isLoadingFromDraftBoxRef.current) {
        return;
      }

      // 只检查 localStorage 中是否有草稿数据（从草稿箱点击"继续编辑"时保存的）
      // 直接点击"添加数据"时，不自动载入服务端草稿数据
      const draftDataFromStorage = localStorage.getItem('draftData');
      if (draftDataFromStorage) {
        try {
          const draftData = JSON.parse(draftDataFromStorage);
          // 标记为从草稿箱载入，避免重复提示
          isLoadingFromDraftBoxRef.current = true;
          // 载入草稿数据
          handleLoadDraft(draftData);
        } catch (error) {
          console.error('解析 localStorage 草稿数据失败:', error);
        } finally {
          // 清除 localStorage 中的数据
          localStorage.removeItem('draftData');
        }
      }
    } else {
      // 当 isAdd 变为 false 时，重置 ref
      isLoadingFromDraftBoxRef.current = false;
    }
  }, [isAdd, handleLoadDraft]);

  const hiddenState = useCallback(
    (cpId: string) => {
      if (cpStates[cpId]?.status !== undefined) {
        return cpStates[cpId].status !== STATUS_VALUES[STATUS_OPTIONS.HIDDEN];
      } else {
        return (
          useEditorSignalMap.get(editPageViewId.value)?.pageComponentSchemas.value[cpId].config.status !==
          STATUS_VALUES[STATUS_OPTIONS.HIDDEN]
        );
      }
    },
    [cpStates, editPageViewId.value]
  );

  const handleValuesChange = (curValue: any, values: any) => {
    setFormValues((prev: any) => ({ ...prev, ...curValue }));
    setHasChanged(true);
    handleFormValuesChange(curValue, values);
  };

  return (
    <Form form={form} className={styles.formWrapper} layout="inline" onValuesChange={handleValuesChange}>
      <Form.Item field="draftId" label style={{ display: 'none' }}>
        <Input />
      </Form.Item>
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
                showFromPageData={showFromPageData}
              />
            </Fragment>
          );
        }
        return (
          <div className={styles.formComp} key={index}>
            {block.items.map((cp) => (
              <Fragment key={cp.id}>
                {hiddenState(cp.id) && (
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
                      formValues={formValues}
                      showFromPageData={showFromPageData}
                      useStoreSignals={{ ...pageEditorSignal, mainEntity, subEntities: subEntitiesValues }}
                    />
                  </div>
                )}
              </Fragment>
            ))}
          </div>
        );
      })}

      <div className={styles.footer}>
        {isAdd ? (
          <Button
            type="ghost"
            loading={submitLoading}
            color={colorConfig}
            bgColor={ghostBgColor}
            borderColor={colorConfig}
            onClick={onSaveDraft}
            style={{ flex: 2 }}
          >
            暂存
          </Button>
        ) : (
          <Button
            type="ghost"
            color={colorConfig}
            bgColor={ghostBgColor}
            borderColor={colorConfig}
            onClick={() => {
              setCpStates({});
              onCancel();
            }}
            style={{ flex: 2 }}
          >
            取消
          </Button>
        )}
        <Button
          type="primary"
          loading={submitLoading}
          bgColor={colorConfig}
          borderColor={colorConfig}
          onClick={onSubmit}
          style={{ flex: 5 }}
        >
          提交
        </Button>
      </div>
    </Form>
  );
};

export default EditRuntime;

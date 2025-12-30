import React from 'react';
import {
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES,
  LIST_COMPONENT_TYPES,
  SHOW_COMPONENT_TYPES,
  getComponentConfig,
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  WORKBENCH_COMPONENT_TYPES,
} from '@onebase/ui-kit';
import { ListComp } from '@/components/Materials/Basic/ListComponents';
import { FormComp } from '@/components/Materials/Basic/FormComponents';
import { ShowComp } from '@/components/Materials/Basic/ShowComponents';
import { LayoutComp } from '@/components/Materials/Basic/LayoutComponents';
import { WorkbenchComp } from '@/components/Materials/Workbench';

/**
 * 组件渲染的通用属性
 */
interface PreviewRenderProps {
  /** 组件ID */
  cpId: string;
  /** 组件类型 */
  cpType: string;
  /** 组件schema映射 */
  pageComponentSchema: any;

  runtime: boolean;

  /** 编辑预览模式 */
  editPreview?: boolean;

  // 详情视图
  detailMode?: boolean;

  showFromPageData?: Function;

  refresh?: number;

  form?: any;

  lastOne?: boolean;

  editLoading?: boolean;
  /** signal传递 */
  useStoreSignals?: any;

  /** form表单改动的值 */
  formValues?: any;
}

const LIST_LAZY_COMPONENT: string[] = [
  FORM_COMPONENT_TYPES.DATE_PICKER,
  FORM_COMPONENT_TYPES.DATE_TIME_PICKER,
  FORM_COMPONENT_TYPES.FILE_UPLOAD,
  FORM_COMPONENT_TYPES.IMG_UPLOAD,
  FORM_COMPONENT_TYPES.SELECT_MUTIPLE,
  FORM_COMPONENT_TYPES.DATA_SELECT,
  // FORM_COMPONENT_TYPES.INPUT_TEXT,
  // FORM_COMPONENT_TYPES.SUB_TABLE,
];

const PreviewRender: React.FC<PreviewRenderProps> = ({
  cpId,
  cpType,
  pageComponentSchema,
  runtime,
  editPreview,
  detailMode,
  showFromPageData,
  refresh,
  editLoading,
  lastOne,
  form,
  formValues,
  useStoreSignals
}) => {

  if (LIST_LAZY_COMPONENT.includes(cpType) && editLoading) {
    return null;
  }
  // 获取组件配置
  const componentConfig = getComponentConfig(pageComponentSchema, cpType);

  componentConfig.align = ALIGN_VALUES[ALIGN_OPTIONS.RIGHT];
  componentConfig.width = '100%';

  const extraProps = {
    form,
    formValues,
    detailMode,
  };

  const ComponentRegistry: Record<string, { component: any, extraProps?: object }> = {
    // 表单组件
    [FORM_COMPONENT_TYPES.INPUT_TEXT]: { component: FormComp.XInputText, extraProps },
    [FORM_COMPONENT_TYPES.INPUT_TEXTAREA]: { component: FormComp.XInputTextArea, extraProps },
    [FORM_COMPONENT_TYPES.INPUT_EMAIL]: { component: FormComp.XInputEmail, extraProps },
    [FORM_COMPONENT_TYPES.INPUT_PHONE]: { component: FormComp.XInputPhone, extraProps },
    [FORM_COMPONENT_TYPES.INPUT_NUMBER]: { component: FormComp.XInputNumber, extraProps },
    [FORM_COMPONENT_TYPES.DATE_PICKER]: { component: FormComp.XDatePicker, extraProps },
    [FORM_COMPONENT_TYPES.DATE_RANGE_PICKER]: { component: FormComp.XDateRangePicker, extraProps },
    [FORM_COMPONENT_TYPES.TIME_PICKER]: { component: FormComp.XTimePicker, extraProps },
    [FORM_COMPONENT_TYPES.DATE_TIME_PICKER]: { component: FormComp.XDateTimePicker, extraProps },
    [FORM_COMPONENT_TYPES.SWITCH]: { component: FormComp.XSwitch, extraProps },
    [FORM_COMPONENT_TYPES.RADIO]: { component: FormComp.XRadio, extraProps },
    [FORM_COMPONENT_TYPES.CHECKBOX]: { component: FormComp.XCheckbox, extraProps },
    [FORM_COMPONENT_TYPES.SELECT_ONE]: { component: FormComp.XSelectOne, extraProps },
    [FORM_COMPONENT_TYPES.SELECT_MUTIPLE]: { component: FormComp.XSelectMutiple, extraProps },
    [FORM_COMPONENT_TYPES.USER_SELECT]: { component: FormComp.XUserSelect, extraProps },
    [FORM_COMPONENT_TYPES.USER_MULTIPLE_SELECT]: { component: FormComp.XUserSelect, extraProps: { ...extraProps, isMultiple: true } },
    [FORM_COMPONENT_TYPES.DEPT_SELECT]: { component: FormComp.XDeptSelect, extraProps: { ...extraProps, editPreview } },
    [FORM_COMPONENT_TYPES.DEPT_MULTIPLE_SELECT]: { component: FormComp.XDeptSelect, extraProps: { ...extraProps, editPreview, isMultiple: true } },
    [FORM_COMPONENT_TYPES.FILE_UPLOAD]: { component: FormComp.XFileUpload, extraProps },
    [FORM_COMPONENT_TYPES.IMG_UPLOAD]: { component: FormComp.XImgUpload, extraProps },
    [FORM_COMPONENT_TYPES.AUTO_CODE]: { component: FormComp.XAutoCode, extraProps },
    // [FORM_COMPONENT_TYPES.RELATED_FORM]: { component: FormComp.XRelatedForm, extraProps },
    [FORM_COMPONENT_TYPES.CAROUSEL_FORM]: { component: FormComp.XCarouselForm, extraProps },
    [FORM_COMPONENT_TYPES.SUB_TABLE]: { component: FormComp.XSubTable, extraProps: { ...extraProps, editLoading, useStoreSignals, editPreview } },
    [FORM_COMPONENT_TYPES.DATA_SELECT]: { component: FormComp.XDataSelect, extraProps: { ...extraProps, editPreview } },

    //  布局组件
    [LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT]: { component: LayoutComp.XPreviewColumnLayout, extraProps: { useStoreSignals, editPreview } },
    [LAYOUT_COMPONENT_TYPES.TABS_LAYOUT]: { component: LayoutComp.XPreviewTabsLayout, extraProps: { useStoreSignals, editPreview } },
    [LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT]: { component: LayoutComp.XPreviewCollapseLayout, extraProps: { useStoreSignals, editPreview } },

    // 列表组件
    [LIST_COMPONENT_TYPES.TABLE]: { component: ListComp.XLoadMore, extraProps: { manuClick: !lastOne, showFromPageData } },
    // [LIST_COMPONENT_TYPES.CALENDAR]: { component: ListComp.XCalendar },
    // [LIST_COMPONENT_TYPES.TIMELINE]: { component: ListComp.XTimeline },
    [LIST_COMPONENT_TYPES.CAROUSEL]: { component: ListComp.XCarousel },
    // [LIST_COMPONENT_TYPES.LIST]: { component: ListComp.XList },
    // [LIST_COMPONENT_TYPES.COLLAPSE]: { component: ListComp.XCollapse },

    //  展示组件
    [SHOW_COMPONENT_TYPES.INFO_NOTICE]: { component: ShowComp.XInfoNotice },
    [SHOW_COMPONENT_TYPES.IMAGE]: { component: ShowComp.XImage },
    [SHOW_COMPONENT_TYPES.FILE]: { component: ShowComp.XFile },
    [SHOW_COMPONENT_TYPES.TEXT]: { component: ShowComp.XText },
    [SHOW_COMPONENT_TYPES.WEB_VIEW]: { component: ShowComp.XWebView },
    [SHOW_COMPONENT_TYPES.DIVIDER]: { component: ShowComp.XDivider },
    [SHOW_COMPONENT_TYPES.PLACEHOLDER]: { component: ShowComp.XPlaceholder },

    //  工作台组件
    [WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY]: { component: WorkbenchComp.XQuickEntry },
    [WORKBENCH_COMPONENT_TYPES.RICH_TEXT_WORKBENCH]: { component: WorkbenchComp.XRichTextEditorWorkbench },
    [WORKBENCH_COMPONENT_TYPES.CAROUSEL_WORKBENCH]: { component: WorkbenchComp.XCarouselWorkbench },
    [WORKBENCH_COMPONENT_TYPES.BUTTON_WORKBENCH]: { component: WorkbenchComp.XButtonWorkbench }
  };

  // 渲染对应的组件
  const renderComponent = () => {
    const commonProps = {
      id: cpId,
      cpName: cpId,
      runtime,
      ...componentConfig,
    };

    const item = ComponentRegistry[cpType];

    if (!item) return null;

    const { component: SpecificComponent, extraProps } = item;

    return (
      <SpecificComponent {...commonProps} {...extraProps} />
    );
  };

  return <>{renderComponent()}</>;
};

export default PreviewRender;

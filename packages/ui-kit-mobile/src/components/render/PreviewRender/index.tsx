import React from 'react';
import {
  FORM_COMPONENT_TYPES,
  LAYOUT_COMPONENT_TYPES,
  LIST_COMPONENT_TYPES,
  // LayoutComp,
  SHOW_COMPONENT_TYPES,
  getComponentConfig,
  ALIGN_OPTIONS,
  ALIGN_VALUES,
  WORKBENCH_COMPONENT_TYPES,
} from '@onebase/ui-kit';
import { ListComp } from '@/components/Materials/Basic/ListComponents';
import { FormComp } from '@/components/Materials/Basic/FormComponents';
import { ShowComp } from '@/components/Materials/Basic/ShowComponents';
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
}

const LIST_LAZY_COMPONENT: string[] = [
  FORM_COMPONENT_TYPES.DATE_PICKER,
  FORM_COMPONENT_TYPES.DATE_TIME_PICKER,
  FORM_COMPONENT_TYPES.FILE_UPLOAD,
  FORM_COMPONENT_TYPES.SELECT_MUTIPLE,
  FORM_COMPONENT_TYPES.DATA_SELECT,
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
  useStoreSignals
}) => {

  if (LIST_LAZY_COMPONENT.includes(cpType) && editLoading) {
    return null;
  }
  // 获取组件配置
  const componentConfig = getComponentConfig(pageComponentSchema, cpType);

  componentConfig.align = ALIGN_VALUES[ALIGN_OPTIONS.RIGHT];
  componentConfig.width = '100%';
  // 渲染对应的组件
  const renderComponent = () => {
    switch (cpType) {
      case FORM_COMPONENT_TYPES.INPUT_TEXT:
        return (
          <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
        return (
          <FormComp.XInputTextArea
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_EMAIL:
        return (
          <FormComp.XInputEmail
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_PHONE:
        return (
          <FormComp.XInputPhone
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.INPUT_NUMBER:
        return (
          <FormComp.XInputNumber
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return (
          <FormComp.XDatePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            form={form}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
        return (
          <FormComp.XDateRangePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.TIME_PICKER:
        return (
          <FormComp.XTimePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return (
          <FormComp.XDateTimePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            form={form}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.SWITCH:
        return (
          <FormComp.XSwitch cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.RADIO:
        return (
          <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.CHECKBOX:
        return (
          <FormComp.XCheckbox cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return (
          <FormComp.XSelectOne cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return (
          <FormComp.XSelectMutiple
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.USER_SELECT:
        return (
          <FormComp.XUserSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.USER_MULTIPLE_SELECT:
        return (
          <FormComp.XUserSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
            isMultiple
          />
        );
      case FORM_COMPONENT_TYPES.DEPT_SELECT:
        return (
          <FormComp.XDeptSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.DEPT_MULTIPLE_SELECT:
        return (
          <FormComp.XDeptSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
            isMultiple
          />
        );
      case FORM_COMPONENT_TYPES.FILE_UPLOAD:
        return (
          <FormComp.XFileUpload
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
            form={form}
          />
        );
      case FORM_COMPONENT_TYPES.IMG_UPLOAD:
        return (
          <FormComp.XImgUpload cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.AUTO_CODE:
        return (
          <FormComp.XAutoCode cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} form={form} />
        );
      case FORM_COMPONENT_TYPES.RELATED_FORM:
      // return (
      //   <FormComp.XRelatedForm
      //     cpName={cpId}
      //     id={cpId}
      //     {...componentConfig}
      //     runtime={runtime}
      //     detailMode={detailMode}
      //   />
      // );
      case FORM_COMPONENT_TYPES.STATIC_TEXT:
      // return (
      //   <FormComp.XStaticText
      //     cpName={cpId}
      //     id={cpId}
      //     {...componentConfig}
      //     runtime={runtime}
      //     detailMode={detailMode}
      //   />
      // );
      case FORM_COMPONENT_TYPES.RICH_TEXT:
      // return (
      //   <FormComp.XRichText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
      // );
      case FORM_COMPONENT_TYPES.CAROUSEL_FORM:
        return (
          <FormComp.XCarouselForm
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.SUB_TABLE:
        return (
          <FormComp.XSubTable cpName={cpId} id={cpId} {...componentConfig} editLoading={editLoading} runtime={runtime} detailMode={detailMode} form={form} useStoreSignals={useStoreSignals} editPreview={editPreview} />
        );
      case FORM_COMPONENT_TYPES.DATA_SELECT:
      return (
        <FormComp.XDataSelect
          cpName={cpId}
          id={cpId}
          {...componentConfig}
          runtime={runtime}
          detailMode={detailMode}
          form={form}
          editPreview={editPreview}
        />
      );

      //  布局组件
      case LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT:
      // return <LayoutComp.XPreviewColumnLayout {...componentConfig} cpName={cpId} id={cpId} />;
      case LAYOUT_COMPONENT_TYPES.TABS_LAYOUT:
      // return <LayoutComp.XPreviewTabsLayout {...componentConfig} cpName={cpId} id={cpId} />;
      case LAYOUT_COMPONENT_TYPES.COLLAPSE_LAYOUT:
        // return <LayoutComp.XPreviewCollapseLayout {...componentConfig} cpName={cpId} id={cpId} />;
        return null
      //  列表组件
      case LIST_COMPONENT_TYPES.TABLE:
        return <ListComp.XLoadMore cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} manuClick={!lastOne} showFromPageData={showFromPageData} />;
      // return (
      //   <ListComp.XTable
      //     cpName={cpId}
      //     id={cpId}
      //     {...componentConfig}
      //     runtime={runtime}
      //     showFromPageData={showFromPageData}
      //     refresh={refresh}
      //   />
      // );
      case LIST_COMPONENT_TYPES.CALENDAR:
      // return <ListComp.XCalendar cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case LIST_COMPONENT_TYPES.TIMELINE:
      // return <ListComp.XTimeline cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case LIST_COMPONENT_TYPES.CAROUSEL:
        return <ListComp.XCarousel cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case LIST_COMPONENT_TYPES.LIST:
      // return <ListComp.XList cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case LIST_COMPONENT_TYPES.COLLAPSE:
        // return <ListComp.XCollapse cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
        return <div>todo: ListComp</div>

      //  展示组件
      case SHOW_COMPONENT_TYPES.INFO_NOTICE:
        return <ShowComp.XInfoNotice cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.IMAGE:
        return <ShowComp.XImage cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.FILE:
        return <ShowComp.XFile cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.TEXT:
        return <ShowComp.XText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.WEB_VIEW:
        return <ShowComp.XWebView cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.DIVIDER:
        return <ShowComp.XDivider cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case SHOW_COMPONENT_TYPES.PLACEHOLDER:
        return <ShowComp.XPlaceholder cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;

      //  工作台组件
      case WORKBENCH_COMPONENT_TYPES.QUICK_ENTRY:
        return <WorkbenchComp.XQuickEntry cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case WORKBENCH_COMPONENT_TYPES.RICH_TEXT_WORKBENCH:
        return <WorkbenchComp.XRichTextEditorWorkbench cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case WORKBENCH_COMPONENT_TYPES.CAROUSEL_WORKBENCH:
        return <WorkbenchComp.XCarouselWorkbench cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;

      default:
        return <div>未知组件类型: {cpType}</div>;
    }
  };

  return <>{renderComponent()}</>;
};

export default PreviewRender;

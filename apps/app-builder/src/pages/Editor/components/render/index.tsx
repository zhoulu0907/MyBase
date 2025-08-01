import FormComp from '@/components/Materials/Basic/FormComponents';
import LayoutComp from '@/components/Materials/Basic/LayoutComponents';
import ListComp from '@/components/Materials/Basic/ListComponents';
import { FORM_COMPONENT_TYPES, LAYOUT_COMPONENT_TYPES, LIST_COMPONENT_TYPES } from '@/constants/componentTypes';
import React from 'react';
import { getComponentConfig } from '../utils';

/**
 * 组件渲染的通用属性
 */
interface ComponentRenderProps {
  /** 组件ID */
  cpId: string;
  /** 组件类型 */
  cpType: string;
  /** 组件schema映射 */
  pageComponentSchema: any;
}

/**
 * ComponentRender 组件
 * 用于渲染传入的组件，支持适配各类组件
 */
const ComponentRender: React.FC<ComponentRenderProps> = ({
  cpId,
  cpType,
  pageComponentSchema
}) => {
  // 获取组件配置
  const componentConfig = getComponentConfig(pageComponentSchema, cpType);

  // 渲染对应的组件
  const renderComponent = () => {
    switch (cpType) {
      case FORM_COMPONENT_TYPES.INPUT_TEXT:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
        return <FormComp.XInputTextArea cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.INPUT_EMAIL:
        return <FormComp.XInputEmail cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.INPUT_PHONE:
        return <FormComp.XInputPhone cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.INPUT_NUMBER:
        return <FormComp.XInputNumber cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return <FormComp.XDatePicker cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
        return <FormComp.XDateRangePicker cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.TIME_PICKER:
        return <FormComp.XTimePicker cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return <FormComp.XDateTimePicker cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.SWITCH:
        return <FormComp.XSwitch cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.RADIO:
        return <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.CHECKBOX:
        return <FormComp.XCheckbox cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return <FormComp.XSelectOne cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return <FormComp.XSelectMutiple cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.USER_SELECT:
        return <FormComp.XUserSelect cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.DEPT_SELECT:
        return <FormComp.XDeptSelect cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.FILE_UPLOAD:
        return <FormComp.XFileUpload cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.IMG_UPLOAD:
        return <FormComp.XImgUpload cpName={cpId} id={cpId} {...componentConfig} />;
      case FORM_COMPONENT_TYPES.AUTO_CODE:
        return <FormComp.XAutoCode cpName={cpId} id={cpId} {...componentConfig} />;
      case LIST_COMPONENT_TYPES.TABLE:
        return <ListComp.XTable cpName={cpId} id={cpId} {...componentConfig} />;

      //  布局组件
      case LAYOUT_COMPONENT_TYPES.COLUMN_LAYOUT:
        return <LayoutComp.XColumnLayout {...componentConfig} cpName={cpId} id={cpId} />;
      default:
        return <div>未知组件类型: {cpType}</div>;
    }
  };

  return (
    <>
      {renderComponent()}
    </>
  );
};

export default ComponentRender;

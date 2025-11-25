import { ALIGN_VALUES, FORM_COMPONENT_TYPES, FormComp, getComponentConfig } from 'src/components/Materials';

import {
  ALIGN_OPTIONS,
} from '../../Materials/Basic/constants';

import React from 'react';

// TODO(mickey): 解决样式隔离问题
import '@arco-design/mobile-react/dist/style.css';
import setRootPixel from '@arco-design/mobile-react/tools/flexible';
setRootPixel();

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
  /** 组件预览状态 */
  runtime: boolean;
}

/**
 * ComponentRender 组件
 * 用于渲染传入的组件，支持适配各类组件
 */
const ComponentEditRender: React.FC<ComponentRenderProps> = ({ cpId, cpType, pageComponentSchema, runtime }) => {
  // 获取组件配置
  // console.warn('aaaaaaaa==0000000==', cpType)

  const componentConfig = getComponentConfig(pageComponentSchema, cpType);
  // console.warn('aaaaaaaa==1111==', componentConfig)
  componentConfig.align = ALIGN_VALUES[ALIGN_OPTIONS.RIGHT];

  // 渲染对应的组件
  const renderComponent = () => {
    switch (cpType) {
      case FORM_COMPONENT_TYPES.INPUT_TEXT:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
        return <FormComp.XInputTextArea cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_EMAIL:
        return <FormComp.XInputEmail cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_PHONE:
        return <FormComp.XInputPhone cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_NUMBER:
        return <FormComp.XInputNumber cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return <FormComp.XSelectOne cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return <FormComp.XSelectMutiple cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.CHECKBOX:
        return <FormComp.XCheckbox cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.RADIO:
        return <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.SWITCH:
        return <FormComp.XSwitch cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return <FormComp.XDatePicker cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.TIME_PICKER:
        return <FormComp.XTimePicker cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
        return <FormComp.XDateRangePicker cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return <FormComp.XDateTimePicker cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.IMG_UPLOAD:
        return <FormComp.XImgUpload cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.CAROUSEL_FORM:
        return <FormComp.XCarouselForm cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.FILE_UPLOAD:
        return <FormComp.XFileUpload cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.AUTO_CODE:
        return <FormComp.XAutoCode cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.DEPT_SELECT:
        return <FormComp.XDeptSelect cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.USER_SELECT:
        return <FormComp.XUserSelect cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.SUB_TABLE:
        return <FormComp.XSubTable cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;

      default:
        return <div>未知组件类型: {cpType}</div>;
    }
  };

  return <>{renderComponent()}</>;
};

export default ComponentEditRender;

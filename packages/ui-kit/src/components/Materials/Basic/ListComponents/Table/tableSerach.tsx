import { useAppEntityStore } from '@/signals';
import { Button, Form, Input } from '@arco-design/web-react';
import { IconSearch, IconSync } from '@arco-design/web-react/icon';
import { useSignals } from '@preact/signals-react/runtime';
import { memo } from 'react';
import { COMPONENT_MAP, FORM_COMPONENT_TYPES, FormComp, getComponentSchema } from 'src/components/Materials';
import { useFormEditorSignal } from 'src/signals/page_editor';
import { v4 as uuidv4 } from 'uuid';
import { STATUS_OPTIONS, STATUS_VALUES } from '../../../constants';
import './index.css';

interface TableSearchConfig {
  searchItems?: any[];
  labelColSpan?: number;
  runtime: boolean;
  onSearch?: () => void;
  onReset?: () => void;
}

const TableSearch = memo((props: TableSearchConfig) => {
  useSignals();

  const { searchItems, labelColSpan, runtime, onSearch, onReset } = props;
  const count = searchItems?.length || 0;
  const remainder = count % 4;
  const placeholderCount = remainder === 0 ? 3 : 4 - remainder - 1;
  const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
  const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});

  const { mainEntity } = useAppEntityStore();

  const renderSearchItem = (item: any) => {
    // console.log('item: ', item);

    // console.log(mainEntity);

    const fieldType = mainEntity.fields.find((field) => field.fieldName === item.value)?.fieldType;
    // console.log('fieldType: ', fieldType);
    if (!fieldType) {
      return;
    }

    const dataField = [mainEntity.tableName, item.value];

    const cpType = COMPONENT_MAP[fieldType as any];
    // console.log('cpType: ', cpType);

    let componentConfig: any = {};
    const detailMode = false;

    let placeholderOverride: string | undefined = '请输入';

    if (cpType) {
      const defaultSchema = getComponentSchema(cpType as any);
      //   console.log(defaultSchema);

      placeholderOverride = cpType === FORM_COMPONENT_TYPES.AUTO_CODE ? '请输入' : undefined;

      componentConfig = {
        ...defaultSchema.config,
        layout: 'vertical',
        labelColSpan,
        dataField,
        defaultValue: undefined,
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        verify: { required: false },
        tooltip: '',
        placeholder: placeholderOverride
      };
    }

    let cpId = componentSchemasKeys.find((ele) => {
      return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(item.value);
    });

    if (cpId) {
      const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
      // 组件类型
      const cpType = components.value?.find((ele) => ele.id === cpId)?.type;

      componentConfig = {
        ...currentComponentSchemas.config,
        layout: 'vertical',
        labelColSpan,
        dataField,
        defaultValue: undefined,
        status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
        verify: { required: false },
        tooltip: '',
        placeholder: placeholderOverride ?? currentComponentSchemas.config?.placeholder
      };
    } else {
      cpId = `${cpType}-${uuidv4()}`;
    }

    switch (cpType) {
      case FORM_COMPONENT_TYPES.INPUT_TEXT:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
      case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
        return (
          <FormComp.XInputTextArea
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
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
          />
        );
      case FORM_COMPONENT_TYPES.DATE_PICKER:
        return (
          <FormComp.XDatePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
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
          />
        );
      case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
        return (
          <FormComp.XDateTimePicker
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.SWITCH:
        return (
          <FormComp.XSwitch cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.RADIO:
        return (
          <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.CHECKBOX:
        return (
          <FormComp.XCheckbox cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.SELECT_ONE:
        return (
          <FormComp.XSelectOne cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
        );
      case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
        return (
          <FormComp.XSelectMutiple
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
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
          />
        );
      case FORM_COMPONENT_TYPES.RELATED_FORM:
        return (
          <FormComp.XRelatedForm
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
      case FORM_COMPONENT_TYPES.STATIC_TEXT:
        return (
          <FormComp.XStaticText
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
          />
        );
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
      case FORM_COMPONENT_TYPES.DATA_SELECT:
        return (
          <FormComp.XDataSelect
            cpName={cpId}
            id={cpId}
            {...componentConfig}
            runtime={runtime}
            detailMode={detailMode}
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
          />
        );

      default:
        return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
    }

    // if (componentSchemasKeys.length) {
    //   // 当前组件配置的key
    //   const cpId = componentSchemasKeys.find((ele) => {
    //     return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(item.value);
    //   });

    //   if (cpId) {
    //     const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
    //     // 组件类型
    //     const cpType = components.value?.find((ele) => ele.id === cpId)?.type;

    //     //TODO: zhoumingji, 后续增加在搜索框的配置
    //     const placeholderOverride = cpType === FORM_COMPONENT_TYPES.AUTO_CODE ? '请输入' : undefined;

    //     const componentConfig = {
    //       ...currentComponentSchemas.config,
    //       layout: 'vertical',
    //       labelColSpan,
    //       defaultValue: undefined,
    //       status: STATUS_VALUES[STATUS_OPTIONS.DEFAULT],
    //       verify: { required: false },
    //       tooltip: '',
    //       placeholder: placeholderOverride ?? currentComponentSchemas.config?.placeholder
    //     };

    //     const detailMode = false;
    //     switch (cpType) {
    //       case FORM_COMPONENT_TYPES.INPUT_TEXT:
    //         return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
    //       case FORM_COMPONENT_TYPES.INPUT_TEXTAREA:
    //         return (
    //           <FormComp.XInputTextArea
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.INPUT_EMAIL:
    //         return (
    //           <FormComp.XInputEmail
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.INPUT_PHONE:
    //         return (
    //           <FormComp.XInputPhone
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.INPUT_NUMBER:
    //         return (
    //           <FormComp.XInputNumber
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DATE_PICKER:
    //         return (
    //           <FormComp.XDatePicker
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DATE_RANGE_PICKER:
    //         return (
    //           <FormComp.XDateRangePicker
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.TIME_PICKER:
    //         return (
    //           <FormComp.XTimePicker
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DATE_TIME_PICKER:
    //         return (
    //           <FormComp.XDateTimePicker
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.SWITCH:
    //         return (
    //           <FormComp.XSwitch
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.RADIO:
    //         return (
    //           <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
    //         );
    //       case FORM_COMPONENT_TYPES.CHECKBOX:
    //         return (
    //           <FormComp.XCheckbox
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.SELECT_ONE:
    //         return (
    //           <FormComp.XSelectOne
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.SELECT_MUTIPLE:
    //         return (
    //           <FormComp.XSelectMutiple
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.USER_SELECT:
    //         return (
    //           <FormComp.XUserSelect
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DEPT_SELECT:
    //         return (
    //           <FormComp.XDeptSelect
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.RELATED_FORM:
    //         return (
    //           <FormComp.XRelatedForm
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.STATIC_TEXT:
    //         return (
    //           <FormComp.XStaticText
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.CAROUSEL_FORM:
    //         return (
    //           <FormComp.XCarouselForm
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DATA_SELECT:
    //         return (
    //           <FormComp.XDataSelect
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.USER_SELECT:
    //         return (
    //           <FormComp.XUserSelect
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );
    //       case FORM_COMPONENT_TYPES.DEPT_SELECT:
    //         return (
    //           <FormComp.XDeptSelect
    //             cpName={cpId}
    //             id={cpId}
    //             {...componentConfig}
    //             runtime={runtime}
    //             detailMode={detailMode}
    //           />
    //         );

    //       default:
    //         return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
    //     }
    //   }
    // }

    return (
      <div className="formWrapper">
        <Form.Item field={item.value} label={<span className={'labelText'}>{item.label}</span>}>
          <Input placeholder={`请输入${item.label}`} />
        </Form.Item>
      </div>
    );
  };

  return (
    <>
      {searchItems?.map((item, idx) => (
        <div key={idx} className="searchItem">
          {renderSearchItem(item)}
        </div>
      ))}
      {(onSearch || onReset) && (
        <>
          {Array.from({ length: placeholderCount }).map((_, i) => (
            <div key={`placeholder-${i}`} className="searchItem placeholder" />
          ))}
          <div className={`searchItem searchActions ${placeholderCount === 3 ? 'searchActions-alone' : ''}`}>
            <Form.Item label={<span className={'labelText'}></span>}>
              <div className="actionsInner formWrapper">
                {onSearch && (
                  <Button type="primary" onClick={onSearch} icon={<IconSearch />}>
                    查询
                  </Button>
                )}
                {onReset && (
                  <Button type="default" onClick={onReset} icon={<IconSync />}>
                    重置
                  </Button>
                )}
              </div>
            </Form.Item>
          </div>
        </>
      )}
    </>
  );
});

export default TableSearch;

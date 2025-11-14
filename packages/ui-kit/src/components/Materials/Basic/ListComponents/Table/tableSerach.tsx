import { Form, Input } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { memo } from 'react';
import { FORM_COMPONENT_TYPES, FormComp } from 'src/components/Materials';
import { useFormEditorSignal } from 'src/signals/page_editor';
import './index.css';

interface TableSearchConfig {
  searchItems?: any[];
  labelColSpan?: number;
  runtime: boolean;
}

const TableSearch = memo((props: TableSearchConfig) => {
  useSignals();

  const { searchItems, labelColSpan, runtime } = props;
  const { pageComponentSchemas: fromPageComponentSchemas, components } = useFormEditorSignal;
  const componentSchemasKeys = Object.keys(fromPageComponentSchemas.value || {});

  const renderSearchItem = (item: any) => {
    if (componentSchemasKeys.length) {
      // 当前组件配置的key
      const cpId = componentSchemasKeys.find((ele) => {
        return fromPageComponentSchemas.value[ele]?.config?.dataField?.includes(item.value);
      });

      if (cpId) {
        // 当前组件配置
        const currentComponentSchemas = fromPageComponentSchemas.value[cpId];
        // 覆盖配置
        const componentConfig = {
          ...currentComponentSchemas.config,
          layout: 'horizontal',
          labelColSpan,
          defaultValue: undefined,
          verify: { required: false },
          tooltip: ''
        };
        // 组件类型
        const cpType = components.value?.find((ele) => ele.id === cpId)?.type;
        const detailMode = false;
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
              <FormComp.XSwitch
                cpName={cpId}
                id={cpId}
                {...componentConfig}
                runtime={runtime}
                detailMode={detailMode}
              />
            );
          case FORM_COMPONENT_TYPES.RADIO:
            return (
              <FormComp.XRadio cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} detailMode={detailMode} />
            );
          case FORM_COMPONENT_TYPES.CHECKBOX:
            return (
              <FormComp.XCheckbox
                cpName={cpId}
                id={cpId}
                {...componentConfig}
                runtime={runtime}
                detailMode={detailMode}
              />
            );
          case FORM_COMPONENT_TYPES.SELECT_ONE:
            return (
              <FormComp.XSelectOne
                cpName={cpId}
                id={cpId}
                {...componentConfig}
                runtime={runtime}
                detailMode={detailMode}
              />
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

          default:
            return <FormComp.XInputText cpName={cpId} id={cpId} {...componentConfig} runtime={runtime} />;
        }
      }
    }

    return (
      <div className="formWrapper">
        <Form.Item
          field={item.value}
          label={<span className={'labelText'}>{item.label}</span>}
          labelCol={{
            style: { width: labelColSpan, flex: 'unset' }
          }}
          wrapperCol={{ style: { flex: 1 } }}
          labelAlign="right"
        >
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
    </>
  );
});

export default TableSearch;

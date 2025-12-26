import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Ellipsis, Form, PopupSwiper, Cell } from '@arco-design/mobile-react';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { DictData } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema, getFieldOptionsConfig, useAppEntityStore } from '@onebase/ui-kit';

import '../index.css';
import './index.css';

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />
}
type XSelectMutipleConfig = typeof FormSchema.XSelectMutipleSchema.config;

const XSelectMutiple = memo((props: XSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean; form?: any }) => {
  const {
    label,
    dataField,
    status,
    verify,
    layout,
    defaultValueConfig,
    runtime = true,
    detailMode,
    form
  } = props;

  const { mainEntity, subEntities } = useAppEntityStore();

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`;

  const [options, setOptions] = useState<DictData[]>([]);
  const [visible, setVisible] = useState(false);
  const [popupDirection] = useState<'bottom' | 'top' | 'left' | 'right'>('bottom');
  const [selectedKeys, setSelectedKeys] = useState<string[]>(() => {
    const formValue = form?.getFieldValue(fieldId);
    if (Array.isArray(formValue) && formValue.length > 0) {
      return formValue.map(val => String(val));
    }
    return [];
  });

  useEffect(() => {
    if (dataField?.length) {
      getOptions()
    }
  }, [dataField])

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities);
    setOptions(newOptions)
  }

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`,
      validator: (value, callback) => {
        if (value?.length > verify?.maxChecked) {
          callback(`选中数量不能大于${verify?.maxChecked}`);
        } else {
          callback();
        }
      }
    }
  ];

  const handleCancel = (e: any) => {
    e.stopPropagation();
    setVisible(false);
  };

  const handleConfirm = (e: any) => {
    e.stopPropagation();
    form?.setFieldValue(fieldId, selectedKeys);
    setVisible(false);
  };

  // 获取选中的标签文本（用于显示在Cell中）
  const getSelectedLabels = () => {
    if (selectedKeys.length === 0) {
      return <span className='selectMultipleValue'>请选择</span>;
    }
    const selectedOptions = options?.filter(option => selectedKeys.includes(option.id));
    return selectedOptions?.map(opt => opt.label).join(',');
  };
  
  return (
    <Form.Item
      className="inputTextWrapperOBMobile selectMultipleWrapperOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      rules={rules}
      layout={layout}
      initialValue={defaultValueConfig?.type === DEFAULT_VALUE_TYPES.CUSTOM ? defaultValueConfig?.customValue : undefined}
      style={{
        textAlign: layout === 'vertical' ? 'left' : 'right',
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{form?.getFieldValue(fieldId) && Array.isArray(form?.getFieldValue(fieldId)) && form?.getFieldValue(fieldId).map((ele: any, index: number) => <div key={index} style={{ marginBottom: '0' }}>
          {ele?.name || options.find((e => e.id === ele || e.id === ele?.id))?.label || '--'}
          </div>)}
        </div>
      ) : (
        <Cell
          className="selectMultipleCellOBMobile"
          onClick={() => setVisible(true)}
        >
          <Ellipsis className={`selectMultipleValue ${layout === 'vertical' ? 'verticalLayout' : ''}`} text={getSelectedLabels()} maxLine={1} />
          <PopupSwiper className="selectMultiplePopupOBMobile" visible={visible} close={(e) => handleCancel(e)} direction={popupDirection}>
            <div>
              <div style={{ 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center', 
                padding: '0.24rem 0.32rem', 
                borderBottom: '1px solid #f0f0f0',
                position: 'relative'
              }}>
                <div
                  className="popup-btn popup-btn-cancel"
                  onClick={(e) => handleCancel(e)} 
                >
                  取消
                </div>
                <span style={{ fontSize: '0.3rem', fontWeight: '500' }}>{label?.text}</span>
                <div
                  className="popup-btn popup-btn-ok"
                  onClick={handleConfirm}
                >
                  确定
                </div>
              </div>
              
              <div className="checkbox-group-outer">
                <Checkbox.Group
                  className="selectCheckoutOBMobile"
                  layout='block'
                  icons={squareIcon}
                  value={selectedKeys}
                  onChange={(values) => {
                    setSelectedKeys(values);
                  }}
                >
                  {options?.map((option) => (
                    <div 
                      key={option.value} 
                      className="checkbox-item"
                      style={{ 
                        padding: '0 0.32rem', 
                        borderBottom: '1px solid #f5f5f5',
                        display: 'flex',
                        alignItems: 'center'
                      }}
                    >
                      <Checkbox
                        value={option.id}
                        icons={squareIcon}
                        style={{ marginRight: '0.2rem' }}
                      >
                      <span style={{ fontSize: 'var(--fontSize)', color: '#333' }}>{option.label}</span>
                      </Checkbox>
                    </div>
                  ))}
                </Checkbox.Group>
              </div>
            </div>
          </PopupSwiper>
        </Cell>
      )}
    </Form.Item>
  );
});

export default XSelectMutiple;

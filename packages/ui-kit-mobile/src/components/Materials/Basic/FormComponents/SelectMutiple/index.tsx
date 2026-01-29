import { memo, useEffect, useState } from 'react';
import { nanoid } from 'nanoid';
import { Checkbox, Ellipsis, Form, PopupSwiper, Cell } from '@arco-design/mobile-react';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';
import { ValidatorType, ITypeRules } from '@arco-design/mobile-utils';
import { DictData } from '@onebase/platform-center';
import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, DEFAULT_VALUE_TYPES, FormSchema, getFieldOptionsConfig, useAppEntityStore, menuDictSignal } from '@onebase/ui-kit';

import '../index.css';
import './index.css';

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />
}
type XSelectMutipleConfig = typeof FormSchema.XSelectMutipleSchema.config;

const XSelectMutiple = memo((props: XSelectMutipleConfig & { runtime?: boolean; detailMode?: boolean; form?: any; }) => {
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

  const { appDict } = menuDictSignal;
  const { mainEntity, subEntities } = useAppEntityStore();
  const textAlign = layout === 'vertical' ? 'left' : 'right';

  // 生成唯一的字段ID
  const fieldId = dataField && dataField.length > 0
    ? dataField[dataField.length - 1]
    : `${FORM_COMPONENT_TYPES.SELECT_MUTIPLE}_${nanoid()}`;

  const [options, setOptions] = useState<DictData[]>([]);
  const [visible, setVisible] = useState(false);
  const [popupDirection] = useState<'bottom' | 'top' | 'left' | 'right'>('bottom');
  const [selectedKeys, setSelectedKeys] = useState<string[]>(form?.getFieldValue(fieldId) || []);

  useEffect(() => {
    if (dataField?.length) {
      getOptions();
    }
  }, [dataField]);

  const getOptions = async () => {
    const newOptions = await getFieldOptionsConfig(dataField, mainEntity, subEntities, appDict.value);
    setOptions(newOptions);
  };

  const rules: ITypeRules<ValidatorType.Custom>[] = [
    {
      required: verify?.required,
      type: ValidatorType.Custom,
      message: `${label.text}是必填项`,
      validator: (value, callback) => {
        if (verify?.checkedLimit && value?.length > verify?.maxChecked) {
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
      return '请选择';
    }
    return selectedKeys?.map(opt => opt).join('，');
  };

  const getReadonlyLabels = () => {
    return selectedKeys.map(v => v.trim()).join('，') || '--';
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
        textAlign,
        pointerEvents: (!runtime || detailMode) ? 'none' : 'unset',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] || detailMode ? (
        <div className="readonlyText">{getReadonlyLabels()}</div>
      ) : (
        <Cell
          className="selectMultipleCellOBMobile"
          onClick={() => setVisible(true)}
        >
          <Ellipsis className={`selectMultipleValue ${selectedKeys.length ? 'hasValue' : ''} ${layout === 'vertical' ? 'verticalLayout' : ''}`} text={getSelectedLabels()} maxLine={1} />
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
                  onChange={(values: any[]) => {
                    setSelectedKeys(values);
                  }}
                >
                  {options?.map((option, index) => (
                    <div
                      key={option.value}
                      className="checkbox-item"
                      style={{
                        padding: '0 0.32rem',
                        borderBottom: index !== options.length - 1 ? '1px solid #f5f5f5' : 'none',
                        display: 'flex',
                        alignItems: 'center'
                      }}
                    >
                      <Checkbox
                        value={option.value}
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

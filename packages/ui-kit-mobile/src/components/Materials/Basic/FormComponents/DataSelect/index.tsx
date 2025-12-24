import { memo, useCallback, useEffect, useState } from 'react';
import { debounce } from 'lodash-es';
import { IconArrowBack } from '@arco-design/mobile-react/esm/icon';
import { PopupSwiper, Cell, SearchBar, Radio, Button, Checkbox, Avatar, Form, Loading, Ellipsis } from '@arco-design/mobile-react';
import { dataMethodPageV2, menuSignal, PageMethodV2Params } from '@onebase/app';

import { isRuntimeEnv } from '@onebase/common';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
type XDataSelectConfig = typeof FormSchema.XDataSelectSchema.config;

import './index.css';

const XDataSelect = memo((props: XDataSelectConfig & { runtime?: boolean; detailMode?: boolean; isMultiple: boolean; editPreview?: boolean; form?: any; }) => {
  const {
    label,
    dataField,
    status,
    layout,
    runtime = true,
    isMultiple = false,
    displayFields,
    editPreview,
    form
  } = props;
  const [visible, setVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DEPT_SELECT}_${props.id}`;

  const formValue = form?.getFieldValue(fieldId) || {};
  const [selectedKeys, setSelectedKeys] = useState<string[]>([formValue.id]);
  const [lastSelectedKeys, setLastSelectedKeys] = useState<string[]>([formValue.id]);

  const [pageNo, setPageNo] = useState<number>(1);
  const [keyword, setKeyword] = useState<string>('');
  const [options, setOptions] = useState<any[]>([]);


  const getData = async () => {
    if (!visible || !runtime || !isRuntimeEnv()) {
      return;
    }

    const tableName = props?.selectedDataSource?.tableName;
    if (!tableName) return;
    setLoading(true);
    const { curMenu } = menuSignal;
    const req: PageMethodV2Params = {
      filters: {
        keyword,
      },
      pageNo: pageNo,
      pageSize: 100
    };

    const res = await dataMethodPageV2(tableName, curMenu.value?.id, req);
    const lastKey = (displayFields || []).length ? displayFields[displayFields.length - 1]?.value : undefined;
    const list = Array.isArray(res?.list) ? res.list : [];
    const opts = list.map((item: any) => ({
      name: lastKey ? (item?.[lastKey] ?? '') : '',
      id: item?.id ?? item?.id
    }));

    setLoading(false);
    setOptions(opts);
  }

  useEffect(() => {
    getData();
  }, [pageNo, keyword, visible]);

  const handleCancel = (e: any) => {
    e.stopPropagation();
    setSelectedKeys(lastSelectedKeys);
    setVisible(false);
  };

  const handleConfirm = (e: any) => {
    e.stopPropagation();
    setLastSelectedKeys(selectedKeys);
    form.setFieldValue(fieldId, options.find((item) => item.id === selectedKeys[0]));
    setVisible(false);
  };

  const removeMember = (key: string) => {
    const newKeys = selectedKeys.filter((k) => k !== key);
    setSelectedKeys(newKeys);
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      setKeyword(value.target.value);
      setPageNo(1);
      setOptions([]);
    }, 500),
    []
  );

  const resetFetchDept = () => {
    setKeyword('');
    setPageNo(1);
    setOptions([]);
  };

  const LoadingComp = () => <div className="loading"><Loading type="circle" color="rgb(var(--primary-6))" /></div>
  const selectedParseDataName = options.find((item) => item.id === selectedKeys[0])?.name || formValue?.name;

  return (
    <Form.Item
      className="inputTextWrapperOBMobile inputDataSelectOBMobile"
      label={label.display && <Ellipsis text={label.text} maxLine={2} />}
      field={fieldId}
      layout={layout}
      style={{
        pointerEvents: runtime ? 'unset' : 'none',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      {status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? (
        <div className="readonlyText">{selectedParseDataName}</div>
      ) : (
        <Cell
          className="cellDataSelectOBMobile"
          onClick={() => setVisible(true)}
        >
          {selectedParseDataName ? <Ellipsis className={`selectValue ${layout === 'vertical' ? 'verticalLayout' : ''}`} text={selectedParseDataName} maxLine={1} /> :
            <div className={`selectValue ${layout === 'vertical' ? 'verticalLayout' : ''}`}>请选择</div>}
          <PopupSwiper visible={visible} close={(e) => handleCancel(e)} direction="bottom">
            <div className={`inputDataSelectPopupContainer ${editPreview ? 'editPreview' : ''}`}>
              <div className="popupHeaderOBMobile">
                <IconArrowBack style={{ fontSize: 'var(--fontSize)' }} onClick={(e) => handleCancel(e)} />
                <span>{label?.text}</span>
                <Button
                  inline
                  type="primary"
                  size="mini"
                  onClick={handleConfirm}
                >
                  确定
                </Button>
              </div>

              <SearchBar
                clearable
                placeholder="搜索"
                actionButton={null}
                onChange={debouncedUpdate}
                onClear={resetFetchDept}
              />

              <div className="itemContainer">
                {loading && <LoadingComp />}
                {!loading && options?.length === 0 && (
                  <div className="empty">暂无数据</div>
                )}
                {!loading && options?.length > 0 && options.map((item: any) =>
                  <Radio
                    className="item"
                    key={item.id}
                    value={item.id}
                    checked={selectedKeys.includes(item.id)}
                    onChange={() => {
                      setSelectedKeys([item.id]);
                    }}
                  >
                    <Ellipsis text={item.name} maxLine={1} />
                  </Radio>
                )}
              </div>
            </div>
          </PopupSwiper>
        </Cell >
      )}
    </Form.Item >
  );
});

export default XDataSelect;

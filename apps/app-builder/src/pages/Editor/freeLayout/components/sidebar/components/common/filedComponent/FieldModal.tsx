import { Modal, Checkbox } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import { useEffect, useState, useMemo } from 'react';

const CheckboxGroup = Checkbox.Group;

/**
 * @fmVisible 模态框显示状态
 * @setFmVisible 设置模态框显示状态
 * @curKeyArr 当前选中的字段id数组
 * @title 弹窗名称
 * @mergeDataToTable 合并数据到表格
 * @ckOptions 字段列表
 * @invert 需要排除的字段
 */
export default function FieldModal({
  fmVisible,
  setFmVisible,
  curKeyArr,
  title = '添加字段',
  mergeDataToTable,
  ckOptions = []
}: any) {
  const [ckedKey, setCkedKey] = useState(curKeyArr);
  const [checkedItem, setCheckedItem] = useState([]);
  const useCkOptions = useMemo(() => {
    return ckOptions?.filter((item: any) => item.isSystemField === 0);
  }, [ckOptions]);

  function handleCheckChange(keyArr: Array<any>) {
    setCkedKey(keyArr);
  }
  function handleDelCked(item: any) {
    let key = item.parentDisplayName ? item.parentDisplayName + item?.fieldName : item?.fieldName;
    if (key) {
      let key_arr: Array<any> = [];
      key_arr = key_arr.concat(ckedKey);
      let idx = key_arr.indexOf(key);
      if (idx > -1) {
        key_arr.splice(idx, 1);
        setCkedKey(key_arr);
      }
    }
  }
  function handleSubmit() {
    if (Array.isArray(checkedItem)) {
      mergeDataToTable && mergeDataToTable(checkedItem);
      setFmVisible(false);
    } else {
      console.error('选择的数据结构不对');
    }
  }

  useEffect(() => {
    let ckedArr: any = useCkOptions.filter((item: any) => {
      return ckedKey.indexOf(item.parentDisplayName ? item.parentDisplayName + item.fieldName : item?.fieldName) > -1;
    });
    setCheckedItem(ckedArr);
  }, [ckedKey]);

  return (
    <Modal
      className="field-modal"
      title={<div style={{ textAlign: 'left' }}>{title}</div>}
      visible={fmVisible}
      onOk={handleSubmit}
      onCancel={() => setFmVisible(false)}
    >
      <div className="out-line-box flex-btw">
        <section className="left-part">
          <div>字段列表</div>
          <div className="left-checkbox">
            <Checkbox
              indeterminate={ckedKey.length > 0 && ckedKey.length < useCkOptions.length}
              checked={ckedKey.length === useCkOptions.length}
              onChange={(e: boolean) => {
                if (e) {
                  handleCheckChange(
                    useCkOptions?.map((item: any) =>
                      item.parentDisplayName ? item.parentDisplayName + item?.fieldName : item?.fieldName
                    )
                  );
                } else {
                  handleCheckChange([]);
                }
              }}
            >
              全选
            </Checkbox>
          </div>
          <CheckboxGroup
            className="check-group-outer"
            // options={useCkOptions}
            value={ckedKey}
            onChange={handleCheckChange}
          >
            {useCkOptions?.map((item: any, i: number) => {
              return (
                <Checkbox
                  key={i}
                  value={item.parentDisplayName ? item.parentDisplayName + item?.fieldName : item?.fieldName}
                >
                  {item.parentDisplayName ? item.parentDisplayName + ' _' + item?.displayName : item?.displayName}
                </Checkbox>
              );
            })}
          </CheckboxGroup>
        </section>
        <section className="right-part">
          <div className="flex-btw">
            <span>字段列表</span>
            <span onClick={() => setCkedKey([])} style={{ color: 'rgb(var(--primary-6))', cursor: 'pointer' }}>
              清空
            </span>
          </div>
          <div className="check-group-outer">
            {checkedItem?.map((item: any, i: number) => {
              return (
                <div className="flex-btw arco-checkbox li" key={i}>
                  <span>
                    {item.parentDisplayName ? item.parentDisplayName + ' _' + item?.displayName : item?.displayName}
                  </span>
                  <IconClose onClick={() => handleDelCked(item)} />
                </div>
              );
            })}
          </div>
        </section>
      </div>
    </Modal>
  );
}

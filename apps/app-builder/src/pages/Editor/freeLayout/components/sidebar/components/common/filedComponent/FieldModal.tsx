import { Modal, Checkbox } from '@arco-design/web-react';
import { IconClose } from '@arco-design/web-react/icon';
import { useEffect, useState } from 'react';

const CheckboxGroup = Checkbox.Group;

/**
 * @fmVisible 模态框显示状态
 * @setFmVisible 设置模态框显示状态
 * @curKeyArr 当前选中的字段id数组
 * @title 弹窗名称
 * @fieldPermType 字段类型
 * @mergeDataToTable 合并数据到表格
 * @ckOptions 字段列表
 * @invert 需要排除的字段
 */
export default function FieldModal({
  fmVisible,
  setFmVisible,
  curKeyArr,
  title='添加隐藏字段',
  fieldPermType='hide',
  mergeDataToTable,
  ckOptions = [],
  invert = []
}: any) {
  const [ckedKey, setCkedKey] = useState(curKeyArr);
  const [checkedItem, setCheckedItem] = useState([]);

  const invertKey = invert.map((item: any) => {
    return item.fieldId;
  });
  const useCkOptions = ckOptions.map((item: any) => {
    return {
      label: item.displayName,
      value: item.fieldId,
      disabled: invertKey.includes(item.fieldId)
    };
  });

  function handleCheckChange(keyArr: Array<any>) {
    setCkedKey(keyArr);
  }
  function handleDelCked(item: any) {
    let key = item?.value;
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
      let resData: Array<any> = [];
      checkedItem.forEach((item: any) => {
        resData.push({
          fieldId: item.value,
          fieldName: item.label,
          fieldPermType: fieldPermType,
        });
      });
      mergeDataToTable && mergeDataToTable(resData);
      setFmVisible(false);
    } else {
      console.error('选择的数据结构不对');
    }
  }

  useEffect(() => {
    let ckedArr: any = useCkOptions.filter((item: any) => {
      return ckedKey.indexOf(item.value) > -1;
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
          <CheckboxGroup
            className="check-group-outer"
            options={useCkOptions}
            value={ckedKey}
            onChange={handleCheckChange}
          />
        </section>
        <section className="right-part">
          <div className="flex-btw">
            <span>字段列表</span>
            <span onClick={() => setCkedKey([])} style={{ color: 'rgb(var(--primary-6))', cursor: 'pointer' }}>
              清空
            </span>
          </div>
          <div className="check-group-outer">
            {checkedItem.map((item: any, i: number) => {
              return (
                <div className="flex-btw arco-checkbox li" key={i}>
                  <span>{item?.label}</span>
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

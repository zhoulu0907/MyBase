import { Button, Checkbox, Input, Popover, Space } from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit } from '@arco-design/web-react/icon';
import React, { useState } from 'react';

import { getPopupContainer } from '@onebase/ui-kit';
import { ReactSortable } from 'react-sortablejs';
import styles from '../../index.module.less';

interface DropdownRenderProps {
  selected: Array<any>;
  displayFieldOptions: Array<any>;
  hasEditLabel?: boolean;
  handleOptionsChange?: () => void;
  setDisplayFieldOptions: (value: React.SetStateAction<any[]>) => void;
  setSelected: (value: React.SetStateAction<string[]>) => void;
}

function getAllValues(options: any) {
  let values: Array<any> = [];
  options.forEach((opt: any) => {
    values.push(opt.fieldName);
  });
  return values;
}

const DropdownRender: React.FC<DropdownRenderProps> = ({
  selected,
  displayFieldOptions,
  hasEditLabel = true,
  handleOptionsChange,
  setDisplayFieldOptions,
  setSelected
}) => {
  const [hovered, setHovered] = useState<string | null>(null);
  const [editIdx, setEditIdx] = useState<number | null>(null);
  const [editLabel, setEditLabel] = useState('');

  // 编辑弹窗内容
  const renderEditPopover = (index: number) => (
    <div className={styles.popoverContainer}>
      <div className={styles.popoverContent}>
        <Space>
          <span className={styles.contentLabel}>显示名</span>
          <Input value={editLabel} onChange={setEditLabel} />
        </Space>
      </div>
      <Space>
        <Button
          onClick={() => {
            setEditIdx(null);
          }}
        >
          取消
        </Button>
        <Button
          type="primary"
          onClick={() => {
            displayFieldOptions[index].displayName = editLabel;
            handleOptionsChange?.();
            setEditIdx(null);
          }}
        >
          确定
        </Button>
      </Space>
    </div>
  );

  // 全选相关
  const allValues = getAllValues(displayFieldOptions);
  const isAllChecked = selected.length === allValues.length;
  const isIndeterminate = selected.length > 0 && selected.length < allValues.length;

  // 父节点选中/半选
  const getChecked = (opt: any) => {
    return selected.includes(opt.fieldName);
  };

  // 父节点点击
  const handleCheck = (checked: boolean, opt: any) => {
    // console.log(checked, opt);
    // console.log(selected);
    // console.log(opt.fieldName);

    const newSelected = checked ? [...selected, opt.fieldName] : selected.filter((v) => v !== opt.fieldName);
    console.log(newSelected);
    setSelected(newSelected);
  };

  // 全选点击
  const handleCheckAll = (checked: boolean) => {
    setSelected(checked ? allValues : []);
  };

  return (
    <>
      <Checkbox
        checked={isAllChecked}
        indeterminate={isIndeterminate}
        onChange={handleCheckAll}
        className={styles.headerCheckbox}
      >
        全选
      </Checkbox>
      <ReactSortable list={displayFieldOptions} setList={setDisplayFieldOptions} handle=".drag-handle" animation={150}>
        {displayFieldOptions.map((option: any, index) => (
          <div key={option.fieldName}>
            <div
              key={option.fieldName}
              className={styles.displayFieldOptions}
              style={{
                background: hovered === option.fieldName ? '#f2f3f5' : '#fff'
              }}
              onMouseEnter={() => {
                setHovered(option.fieldName);
              }}
              onMouseLeave={() => {
                setHovered(null);
              }}
            >
              <Checkbox
                checked={getChecked(option)}
                onChange={(checked) => handleCheck(checked, option)}
                className={styles.childCheckbox}
              />
              <span className={styles.optionSpan}>{option.displayName}</span>
              <div className={styles.operationDiv}>
                {selected.includes(option.fieldName) && hasEditLabel && (
                  <Popover
                    trigger="click"
                    position="tr"
                    popupVisible={editIdx === index}
                    getPopupContainer={getPopupContainer}
                    onVisibleChange={(visible) => {
                      if (visible) {
                        setEditIdx(index);
                        setEditLabel(option.displayName);
                      } else {
                        setEditIdx(null);
                      }
                    }}
                    content={renderEditPopover(index)}
                  >
                    <IconEdit
                      className={styles.iconEdit}
                      style={{
                        visibility: hovered === option.fieldName ? 'visible' : 'hidden',
                        color: '#838892'
                      }}
                    />
                  </Popover>
                )}
                <IconDragDotVertical
                  className="drag-handle"
                  style={{
                    cursor: 'move',
                    marginLeft: 8,
                    visibility: hovered === option.fieldName ? 'visible' : 'hidden',
                    color: '#838892'
                  }}
                />
              </div>
            </div>
          </div>
        ))}
      </ReactSortable>
    </>
  );
};

export default DropdownRender;

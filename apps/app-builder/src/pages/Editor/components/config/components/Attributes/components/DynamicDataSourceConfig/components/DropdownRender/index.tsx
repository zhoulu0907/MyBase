import React, { useState } from 'react';
import { Button, Checkbox, Input, Popover, Space } from '@arco-design/web-react';
import { IconDragDotVertical, IconEdit } from '@arco-design/web-react/icon';

import styles from '../../index.module.less';
import { ReactSortable } from 'react-sortablejs';

interface DropdownRenderProps {
  selected: Array<any>;
  displayFieldOptions: Array<any>;
  hasEditLabel?: boolean;
  handleOptionsChange: () => void;
  setDisplayFieldOptions: (value: React.SetStateAction<any[]>) => void;
  setSelected: (value: React.SetStateAction<string[]>) => void;
}

function getAllValues(options: any) {
  let values: Array<any> = [];
  options.forEach((opt: any) => {
    values.push(opt.value);
    if (opt.children) {
      values = values.concat(opt.children.map((child: any) => child.value));
    }
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
            displayFieldOptions[index].label = editLabel;
            handleOptionsChange();
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
  const getParentChecked = (opt: any) => {
    if (!opt.children) return selected.includes(opt.value);
    const childValues = opt.children.map((c: any) => c.value);
    const checkedCount = childValues.filter((v: any) => selected.includes(v)).length;
    if (checkedCount === 0) return false;
    if (checkedCount === childValues.length) return true;
    return 'indeterminate';
  };

  // 父节点点击
  const handleParentCheck = (checked: boolean, opt: any) => {
    if (!opt.children) {
      setSelected(checked ? [...selected, opt.value] : selected.filter((v) => v !== opt.value));
    } else {
      const childValues = opt.children.map((c: any) => c.value);
      if (checked) {
        setSelected([...selected, opt.value, ...childValues]);
      } else {
        setSelected(selected.filter((v) => v !== opt.value && !childValues.includes(v)));
      }
    }
  };

  // 子节点点击
  const handleChildCheck = (checked: boolean, child: any, parent: any) => {
    let newSelected: Array<any> = [];
    if (checked) {
      newSelected = [...selected, child.value];
    } else {
      newSelected = selected.filter((v) => v !== child.value);
    }
    // 如果所有子节点都选中，自动选中父节点
    if (parent) {
      const childValues = parent.children.map((c: any) => c.value);
      const allChecked = childValues.every((v: any) => newSelected.includes(v));
      if (allChecked) {
        newSelected = [...newSelected, parent.value];
      } else {
        newSelected = newSelected.filter((v) => v !== parent.value);
      }
    }
    setSelected(newSelected);
  };

  // 全选点击
  const handleCheckAll = (checked: boolean) => {
    setSelected(checked ? allValues : []);
  };

  // 子级拖拽
  const handleChildDrag = (parentIdx: number, newChildren: any) => {
    const newOptions = [...displayFieldOptions];
    newOptions[parentIdx].children = newChildren;
    setDisplayFieldOptions(newOptions);
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
          <div key={option.value}>
            <div
              key={option.value}
              className={styles.displayFieldOptions}
              style={{
                background: hovered === option.value ? '#f2f3f5' : '#fff'
              }}
              onMouseEnter={() => {
                setHovered(option.value);
              }}
              onMouseLeave={() => {
                setHovered(null);
              }}
            >
              <Checkbox
                checked={getParentChecked(option) === true}
                indeterminate={getParentChecked(option) === 'indeterminate'}
                onChange={(checked) => handleParentCheck(checked, option)}
                className={styles.childCheckbox}
              />
              <span className={styles.optionSpan}>{option.label}</span>
              <div className={styles.operationDiv}>
                {selected.includes(option.value) && hasEditLabel && (
                  <Popover
                    trigger="click"
                    position="tr"
                    popupVisible={editIdx === index}
                    onVisibleChange={(visible) => {
                      if (visible) {
                        setEditIdx(index);
                        setEditLabel(option.label);
                      } else {
                        setEditIdx(null);
                      }
                    }}
                    content={renderEditPopover(index)}
                  >
                    <IconEdit
                      className={styles.iconEdit}
                      style={{
                        visibility: hovered === option.value ? 'visible' : 'hidden',
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
                    visibility: hovered === option.value ? 'visible' : 'hidden',
                    color: '#838892'
                  }}
                />
              </div>
            </div>
            {option.children && (
              <div style={{ marginLeft: 32 }}>
                <ReactSortable
                  list={option.children}
                  setList={(newChildren) => handleChildDrag(index, newChildren)}
                  handle=".drag-handle"
                  animation={150}
                >
                  {option.children.map((child: any) => (
                    <div
                      key={child.value}
                      className={styles.displayFieldOptions}
                      style={{
                        background: hovered === child.value ? '#f2f3f5' : '#fff'
                      }}
                      onMouseEnter={() => {
                        setHovered(child.value);
                      }}
                      onMouseLeave={() => {
                        setHovered(null);
                      }}
                    >
                      <Checkbox
                        checked={selected.includes(child.value)}
                        onChange={(checked) => handleChildCheck(checked, child, option)}
                        className={styles.childCheckbox}
                      />
                      <span className={styles.optionSpan}>{`${option.label}.${child.label}`}</span>
                      <div className={styles.operationDiv}>
                        {selected.includes(child.value) && hasEditLabel && (
                          <Popover
                            trigger="click"
                            position="tr"
                            popupVisible={editIdx === index}
                            onVisibleChange={(visible) => {
                              if (visible) {
                                setEditIdx(index);
                                setEditLabel(child.label);
                              } else {
                                setEditIdx(null);
                              }
                            }}
                            content={renderEditPopover(index)}
                          >
                            <IconEdit
                              className={styles.iconEdit}
                              style={{
                                visibility: hovered === child.value ? 'visible' : 'hidden'
                              }}
                            />
                          </Popover>
                        )}
                        <IconDragDotVertical
                          className="drag-handle"
                          style={{
                            cursor: 'move',
                            marginLeft: 8,
                            visibility: hovered === child.value ? 'visible' : 'hidden'
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </ReactSortable>
              </div>
            )}
          </div>
        ))}
      </ReactSortable>
    </>
  );
};

export default DropdownRender;

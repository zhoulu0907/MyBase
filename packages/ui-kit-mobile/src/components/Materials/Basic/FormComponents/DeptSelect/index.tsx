import { memo, useCallback, useEffect, useState } from 'react';
import { debounce } from 'lodash-es';
import { IconArrowBack } from '@arco-design/mobile-react/esm/icon';
import { PopupSwiper, Cell, SearchBar, Radio, Button, Checkbox, Avatar, Form, Loading, Ellipsis } from '@arco-design/mobile-react';
import { getDeptUser, type GetDeptUserReq } from '@onebase/platform-center';
import IconSquareChecked from '@arco-design/mobile-react/esm/icon/IconSquareChecked';
import IconSquareUnchecked from '@arco-design/mobile-react/esm/icon/IconSquareUnchecked';
import IconSquareDisabled from '@arco-design/mobile-react/esm/icon/IconSquareDisabled';

import { formatDeptAndUsers, getDeptData, parseDeptName } from './const';
import deptIcon from '@/assets/images/dept_icon.svg';

import { FORM_COMPONENT_TYPES, STATUS_OPTIONS, STATUS_VALUES, FormSchema } from '@onebase/ui-kit';
type XDeptSelectConfig = typeof FormSchema.XDeptSelectSchema.config;

import styles from './index.module.css';

const squareIcon = {
  normal: <IconSquareUnchecked />,
  active: <IconSquareChecked />,
  disabled: <IconSquareDisabled />,
  activeDisabled: <IconSquareChecked />
}

const XDeptSelect = memo((props: XDeptSelectConfig & { runtime?: boolean; detailMode?: boolean; isMultiple: boolean; form?: any;}) => {
  const { label, dataField, status, verify, layout, runtime = true, isMultiple = false, form } = props;
  const [visible, setVisible] = useState(false);
  const [popupDirection] = useState<'bottom' | 'top' | 'left' | 'right'>('bottom');

  const [loading, setLoading] = useState(false);

  // 选中值（单选）
  const [deptData, setDeptData] = useState<any>(); // 部门数据
  const [selectedMembers, setSelectedMembers] = useState<any[]>([]); // 已经选中的数据
  const [selectedKeys, setSelectedKeys] = useState<string[]>(() => {
    return selectedMembers.map((member) => member.key);
  });

  const renderData = formatDeptAndUsers(deptData);

  const fieldId = dataField.length > 0 ? dataField[dataField.length - 1] : `${FORM_COMPONENT_TYPES.DEPT_SELECT}_${props.id}`;

  useEffect(() => {
    !deptData && getDeptUsers({});
  }, [deptData]);

  useEffect(() => {
    setTimeout(() => {
      const formData = form?.getFieldValue(fieldId);
      formData && setSelectedKeys([formData.id]);
    }, 500);
  }, [selectedMembers]);

  const handleCancel = (e: any) => {
    e.stopPropagation();
    setVisible(false);
  };

  // TODO 待完善：多选模式选择部门数据后，进入下级勾选其它数据会清除上级数据
  const handleConfirm = (e: any) => {
    e.stopPropagation();
    if (!selectedKeys.length) return;

    const curSelectDept = getDeptData(deptData?.deptList, selectedKeys);
    // console.log({ isMultiple, selectedKeys, curSelectDept, fieldId });
    form?.setFieldValue(fieldId, curSelectDept);
    setVisible(false);
  };

  const removeMember = (key: string) => {
    const newKeys = selectedKeys.filter((k) => k !== key);
    setSelectedKeys(newKeys);

    // const newSelectedMembers = selectedMembers.filter((m) => m.key !== key);
    // if (onUpdateSelectedMembers) {
    //   onUpdateSelectedMembers(newSelectedMembers);
    // }
  };

  // 获取部门用户信息
  const getDeptUsers = async ({ deptId, keywords }: { deptId?: string; keywords?: string }) => {
    try {
      const params: GetDeptUserReq = {
        deptId,
        keywords
      };
      setLoading(true);
      const res = await getDeptUser(params);
      setDeptData(res);
      setLoading(false);
    } catch (error) {
      console.error('获取部门信息 error:', error);
    } finally {
    }
  };

  const debouncedUpdate = useCallback(
    debounce((value) => {
      getDeptUsers({ keywords: value.target.value });
    }, 500),
    []
  );

  // 点击部门，进入下级
  const handleDeptClick = async (deptId: string) => {
    await getDeptUsers({ deptId });
  };

  // 重置请求
  const resetFetchDept = async () => {
    await getDeptUsers({});
  };

  const LoadingComp = () => <div className={styles.loading}><Loading type="circle" color="rgb(var(--primary-6))" /></div>
  const selectedParseDeptName = parseDeptName(deptData?.deptList, selectedKeys);

  return (
    <Form.Item
      className="inputTextWrapperOBMobile inputDeptSelectOBMobile"
      label={label.display && label.text}
      field={fieldId}
      style={{
        borderRadius: '0.16rem',
        pointerEvents: runtime ? 'unset' : 'none',
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
      }}
    >
      <Cell
        className={styles.deptCell}
        onClick={() => setVisible(true)}
      >
        {selectedParseDeptName ? <Ellipsis className={styles.selectValue} text={selectedParseDeptName} maxLine={1} /> : <div style={{ color: '#c9cdd4', fontSize: '0.32rem', textAlign: 'right' }}>请选择</div>}
        <PopupSwiper visible={visible} close={(e) => handleCancel(e)} direction={popupDirection}>
          <div className={styles.inputDeptSelectPopupContainer}>
            <div className={styles.popupHeaderOBMobile}>
              <IconArrowBack style={{ fontSize: '0.32rem' }} onClick={(e) => handleCancel(e)} />
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

            <div style={{ padding: '0.24rem 0.32rem' }}>
              <SearchBar
                clearable
                placeholder="搜索部门"
                actionButton={null}
                onChange={debouncedUpdate}
                onClear={resetFetchDept}
              />
            </div>

            <div className={styles.container}>
              {loading && <LoadingComp />}
              {!loading && renderData?.children?.length === 0 && (
                <div className={styles.empty}>暂无数据</div>
              )}
              {!loading && renderData?.children?.length > 0 && renderData?.children.map((item: any) =>
                <div
                  key={`dept-${item.key}`}
                  className={styles.item}
                >
                  <div className={styles.left}>
                    {isMultiple ? (
                      <Checkbox
                        value={item.key}
                        icons={isMultiple && squareIcon}
                        checked={selectedKeys.includes(item.key)}
                        onChange={(e) => {
                          if (e) {
                            setSelectedKeys([...selectedKeys, item.key]);
                            const newSelectedMembers = [
                              ...selectedMembers,
                              {
                                key: item.key,
                                name: item.title,
                                // department: buildDepartmentPath()
                              }
                            ];
                            // if (onUpdateSelectedMembers) {
                            //   onUpdateSelectedMembers(newSelectedMembers);
                            // }
                          } else {
                            removeMember(item.key);
                          }
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.16rem' }}>
                          <Avatar shape="square" src={deptIcon} />
                          <div className={styles.icon}>

                          </div>
                          <span className={styles.title}>{item.title}</span>
                        </div>
                      </Checkbox>
                    ) : (
                      <Radio
                        key={item.key}
                        value={item.key}
                        checked={selectedKeys.includes(item.key)}
                        onChange={() => {
                          setSelectedKeys([item.key]);
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.16rem' }}>
                          <Avatar shape="square" src={deptIcon} />
                          <div className={styles.icon}>

                          </div>
                          <span className={styles.title}>{item.title}</span>
                        </div>
                      </Radio>
                    )}
                  </div>
                  <div className={styles.stepDept} onClick={() => handleDeptClick(item.id)}>
                    下级
                  </div>
                </div>
              )}
            </div>
          </div>
        </PopupSwiper>
      </Cell>
    </Form.Item>
  );
});

export default XDeptSelect;

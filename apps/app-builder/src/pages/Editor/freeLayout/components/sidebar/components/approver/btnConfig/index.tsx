import React, { useState, useRef, useEffect, useContext, useCallback } from 'react';
import { Table, Input, Form, type FormInstance, Switch, Checkbox } from '@arco-design/web-react';
import { IconEdit, IconSettings } from '@arco-design/web-react/icon';
import SettingModal from './SettingModal';
import styles from '../approverConfig/index.module.less';
import './style.less';
import { type BtnConfig } from '../constant';
const FormItem = Form.Item;
const EditableContext = React.createContext<{ getForm?: () => FormInstance }>({});

function EditableRow(props: any) {
  const { children, record, className, ...rest } = props;
  const refForm = useRef<any>(null);

  const getForm = () => refForm.current;

  return (
    <EditableContext.Provider
      value={{
        getForm
      }}
    >
      <Form
        style={{ display: 'table-row' }}
        children={children}
        ref={refForm}
        wrapper="tr"
        wrapperProps={rest}
        className={`${className} editable-row`}
      />
    </EditableContext.Provider>
  );
}
function EditableCell(props: any) {
  const { children, className, rowData, column, onHandleSave } = props;
  const ref = useRef<any>(null);
  const refInput = useRef<any>(null);
  const { getForm } = useContext(EditableContext);
  const [editing, setEditing] = useState(false);
  const handleClick = useCallback(
    (e: any) => {
      if (
        editing &&
        column.editable &&
        ref.current &&
        !ref.current.contains(e.target) &&
        !e.target.classList.contains('js-demo-select-option')
      ) {
        cellValueChangeHandler(rowData[column.dataIndex]);
      }
    },
    [editing, rowData, column]
  );
  useEffect(() => {
    editing && refInput.current && refInput.current.focus();
  }, [editing]);
  useEffect(() => {
    document.addEventListener('click', handleClick, true);
    return () => {
      document.removeEventListener('click', handleClick, true);
    };
  }, [handleClick]);

  const cellValueChangeHandler = (value: any) => {
    if (column.dataIndex === 'salary_select') {
      const values = {
        [column.dataIndex]: value
      };
      onHandleSave && onHandleSave({ ...rowData, ...values });
      setTimeout(() => setEditing(!editing), 300);
    } else {
      const form = getForm && getForm();
      if (form) {
        form.validate([column.dataIndex], (errors, values) => {
          if (!errors || !errors[column.dataIndex]) {
            setEditing(!editing);
            onHandleSave && onHandleSave({ ...rowData, ...values });
          }
        });
      } else {
        console.error('form error ===', form);
      }
    }
  };

  if (editing) {
    return (
      <div ref={ref}>
        <FormItem
          style={{ marginBottom: 0 }}
          labelCol={{ span: 0 }}
          wrapperCol={{ span: 24 }}
          initialValue={rowData[column.dataIndex]}
          field={column.dataIndex}
          rules={[{ required: true }]}
        >
          <Input ref={refInput} onPressEnter={cellValueChangeHandler} style={{ width: '86px' }} />
        </FormItem>
      </div>
    );
  }

  return (
    <div
      className={column.editable ? `editable-cell ${className}` : className}
      onClick={() => column.editable && setEditing(!editing)}
    >
      {children}
    </div>
  );
}

export default function ApproverBtnConfig({ setApprovalConfigData, buttonConfigs }: BtnConfig) {
  const columnsData = [
    {
      key: '1',
      buttonType: 'approve',
      buttonName: '同意',
      displayName: '同意',
      name: '同意',
      defaultApprovalComment: '同意',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: true
    },
    {
      key: '2',
      buttonType: 'reject',
      buttonName: '拒绝',
      displayName: '拒绝',
      name: '拒绝',
      defaultApprovalComment: '拒绝',
      approvalCommentRequired: true,
      batchApproval: false,
      enabled: true
    },
    {
      key: '3',
      buttonName: '保存',
      displayName: '保存',
      name: '保存',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '4',
      buttonName: '转交',
      displayName: '转交',
      name: '转交',
      defaultApprovalComment: '转交',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '5',
      buttonName: '加签',
      displayName: '加签',
      name: '加签',
      defaultApprovalComment: '加签',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '6',
      buttonName: '退回',
      displayName: '退回',
      name: '退回',
      defaultApprovalComment: '退回',
      approvalCommentRequired: true,
      batchApproval: false,
      enabled: false
    },
    {
      key: '7',
      buttonName: '撤回',
      displayName: '撤回',
      name: '撤回',
      defaultApprovalComment: '撤回',
      approvalCommentRequired: false,
      batchApproval: false,
      enabled: false
    },
    {
      key: '8',
      buttonName: '弃权',
      displayName: '弃权',
      name: '弃权',
      defaultApprovalComment: '弃权',
      approvalCommentRequired: true,
      batchApproval: false,
      enabled: false
    }
  ];

  const initData = columnsData.map((item: any) => {
    buttonConfigs.forEach((config: any) => {
      if (item.buttonName === config.buttonName) {
        item = { ...item, ...config };
        item.name = item.buttonName;
      }
    });
    return item;
  });
  const [tbData, setData] = useState(initData);
  const columns = [
    {
      title: '操作按钮',
      dataIndex: 'name'
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      editable: true,
      render: (val: any, row: any) => {
        return (
          <>
            {val}
            <IconEdit />
          </>
        );
      }
    },
    {
      title: '默认审批意见',
      dataIndex: 'defaultApprovalComment',
      editable: true,
      render: (val: any, row: any) => {
        if (row?.key === '3' || row?.key === '7') {
          return <></>;
        } else {
          return (
            <>
              {val}
              <IconEdit />
            </>
          );
        }
      }
    },
    {
      title: '审批意见必填',
      dataIndex: 'approvalCommentRequired',
      render: (val: any, row: any) => {
        if (row?.key === '3' || row?.key === '7') {
          return <></>;
        } else {
          return (
            <Switch
              onChange={(flag: boolean) => handleSwitchChange(row, 'approvalCommentRequired', flag)}
              size="small"
              checked={val}
            />
          );
        }
      }
    },
    {
      title: '批量审批',
      dataIndex: 'batchApproval',
      render: (val: any, row: any) => {
        if (row?.key === '1' || row?.key === '2') {
          return <Checkbox onChange={(flag: boolean) => handleSwitchChange(row, 'batchApproval', flag)} checked={val} />;
        } else {
          return <></>;
        }
      }
    },
    {
      title: '启用按钮',
      dataIndex: 'enabled',
      render: (val: any, row: any) => {
        if (row?.key === '4') {
          return (
            <div className="back-settings">
              <Switch onChange={(flag: boolean) => handleSwitchChange(row, 'enabled', flag)} size="small" checked={val} />
              <IconSettings onClick={() => setSettingShow(true)} />
            </div>
          );
        } else {
          return (
            <Switch onChange={(flag: boolean) => handleSwitchChange(row, 'enabled', flag)} size="small" checked={val} />
          );
        }
      }
    }
  ];

  let [settingsShow, setSettingShow] = useState(false);

  function handleSwitchChange(row: any, type: string, flag: boolean) {
    let _row = { ...row };
    _row[type] = flag;
    handleSave(_row);
  }

  function handleSave(row: any) {
    const newData = [...tbData];
    const index = newData.findIndex((item) => row.key === item.key);
    newData.splice(index, 1, { ...newData[index], ...row });
    setData(newData);
  }

  useEffect(() => {
    setApprovalConfigData(
      'buttonConfigs',
      tbData.filter((item: any) => {
        return item?.enabled;
      })
    );
  }, [tbData]);

  return (
    <div className={styles.approverConfig}>
      <div className={styles.configTitle}>操作按钮</div>
      <Table
        pagination={false}
        data={tbData}
        components={{
          body: {
            row: EditableRow,
            cell: EditableCell
          }
        }}
        columns={columns.map((column) =>
          column.editable
            ? {
                ...column,
                onCell: () => ({
                  onHandleSave: handleSave
                })
              }
            : column
        )}
        className="table-demo-editable-cell"
      />
      <SettingModal settingsShow={settingsShow} setSettingShow={setSettingShow} />
    </div>
  );
}

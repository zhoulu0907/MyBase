import React, { useState, useRef, useEffect, useContext, useCallback } from 'react';
import { Button, Table, Input, Select, Form, type FormInstance, Switch, Checkbox } from '@arco-design/web-react';
import {IconEdit, IconSettings} from '@arco-design/web-react/icon'
import SettingModal from './SettingModal';

import styles from '../approverConfig/index.module.less';
import './style.less'

const FormItem = Form.Item;
const EditableContext = React.createContext<{ getForm?: () => FormInstance }>({});

function EditableRow(props:any) {
  const { children, record, className, ...rest } = props;
  const refForm = useRef<any>(null);

  const getForm = () => refForm.current;

  return (
    <EditableContext.Provider
      value={{
        getForm,
      }}
    >
      <Form
        style={{ display: 'table-row' }}
        children={children}
        ref={refForm}
        wrapper='tr'
        wrapperProps={rest}
        className={`${className} editable-row`}
      />
    </EditableContext.Provider>
  );
}
function EditableCell(props:any) {
  const { children, className, rowData, column, onHandleSave } = props;
  const ref = useRef<any>(null);
  const refInput = useRef<any>(null);
  const { getForm } = useContext(EditableContext);
  const [editing, setEditing] = useState(false);
  const handleClick = useCallback(
    (e:any) => {
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

  const cellValueChangeHandler = (value:any) => {
    if (column.dataIndex === 'salary_select') {
      const values = {
        [column.dataIndex]: value,
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
        console.error('form error ===', form)
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
            <Input ref={refInput} onPressEnter={cellValueChangeHandler} style={{width: '86px'}}/>
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

export default function ApproverBtnConfig() {
    const [data, setData] = useState([
        {
            key: '1',
            na1me: '同意',
            name: '同意',
            salary: '同意',
            address: '32 Park Road, London',
            email: 'jane.doe@example.com',
            na2me: 'Jane Doe',
        },
        {
            key: '2',
            na1me: '拒绝',
            name: '拒绝',
            salary: '拒绝',
            address: '35 Park Road, London',
            email: 'alisa.ross@example.com',
            na2me: 'Jane Doe',
        },
        {
            key: '3',
            na1me: 'Jane Doe',
            name: 'Kevin Sandra',
            salary: '22000',
            address: '31 Park Road, London',
            na2me: 'Jane Doe',
        },
        {
            key: '4',
            na1me: '退回',
            name: '退回',
            salary: '退回',
            address: '42 Park Road, London',
            email: 'ed.hellen@example.com',
            na2me: 'Jane Doe',
        },
        {
            key: '5',
            na1me: 'Jane Doe',
            name: 'William Smith',
            salary: '27000',
            address: '62 Park Road, London',
            email: 'william.smith@example.com',
            na2me: 'Jane Doe',
        },
    ]);
    const columns = [
        {
            title: '操作按钮',
            dataIndex: 'na1me'
        },
        {
            title: '显示名称',
            dataIndex: 'name',
            editable: true,
            render: (_:any, row:any) => {
                return <>{_}<IconEdit /></>
            }
        },
        {
            title: '默认审批意见',
            dataIndex: 'salary',
            editable: true,
            render: (_:any, row:any) => {
                return <>{_}<IconEdit /></>
            }
        },
        {
            title: '审批意见必填',
            dataIndex: 'address',
            render: (_:any, row:any) => {
                return <Switch size='small'/>
            }
        },
        {
            title: '批量审批',
            dataIndex: 'email',
            render: (_:any, row:any) => {
                if (row?.key === '1' || row?.key === '2') {
                    return <Checkbox />
                } else {
                    return <></>
                }
            }
        },
        {
            title: '启用按钮',
            dataIndex: 'na2me',
            render: (_:any, row:any) => {
                if (row?.key === '4') {
                    return <div className='back-settings'><Switch size='small' /><IconSettings onClick={() => setSettingShow(true)} /></div>
                } else {
                    return <Switch size='small' />
                }
            }
        },
    ];

    let [settingsShow, setSettingShow] = useState(false)

    function handleSave(row:any) {
        const newData = [...data];
        const index = newData.findIndex((item) => row.key === item.key);
        newData.splice(index, 1, { ...newData[index], ...row });
        setData(newData);
    }

    return <div className={styles.approverConfig}>
        <div className={styles.configTitle}>审批人设置</div>
        <Table
            pagination={false}
            data={data}
            components={{
            body: {
                row: EditableRow,
                cell: EditableCell,
            },
            }}
            columns={columns.map((column) =>
            column.editable
                ? {
                    ...column,
                    onCell: () => ({
                        onHandleSave: handleSave,
                    }),
                }
                : column
            )}
            className='table-demo-editable-cell'
        />
        <SettingModal settingsShow={settingsShow} setSettingShow={setSettingShow} />
    </div>
}
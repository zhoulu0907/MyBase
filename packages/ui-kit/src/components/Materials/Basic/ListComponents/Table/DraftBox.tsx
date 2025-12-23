import clearIcon from '@/assets/images/clear.svg';
import draftBoxIcon from '@/assets/images/draft.svg';
import { Alert, Badge, Button, Divider, Form, Modal, Popconfirm, Space, Table } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { getDraftPage } from '@onebase/app';
import { useSignals } from '@preact/signals-react/runtime';
import dayjs from 'dayjs';
import React, { useMemo, useState } from 'react';

interface DraftBoxProps {
  menuId: string;
  tableName: string;
  showFromPageData?: Function;
  tableColumns?: any[];
}

/**
 * 草稿箱组件
 */
export const DraftBox: React.FC<DraftBoxProps> = ({ showFromPageData, tableColumns, menuId, tableName }) => {
  useSignals();
  const [draftForm] = Form.useForm();

  const [showDraftModal, setShowDraftModal] = useState(false);
  const [draftTableData, setDraftTableData] = useState<any[]>([]);
  const [draftTotal, setDraftTotal] = useState<number>(0);
  const [draftPageNo, setDraftPageNo] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);

  // 打开草稿箱
  const handleGetDrafts = async () => {
    const res = await getDraftPage(tableName, menuId, { pageNo: draftPageNo, pageSize });

    const { list = [], total = 0, pageNo = 1 } = res || {};

    // 参照 XTable 数据处理：补充 id/key，扁平化 data 便于列 render 使用
    const newTableData = (list || []).map((item: any) => {
      const merged = {
        ...item,
        ...(item?.data || {})
      };
      const rowId = item?.id ?? item?.data?.id;
      return {
        id: rowId,
        key: rowId,
        ...merged
      };
    });

    // console.log('newDraftTableData: ', newTableData);

    setDraftTableData(newTableData);
    draftForm.setFieldsValue({ [tableName]: newTableData });
    setDraftTotal(total);
    setDraftPageNo(pageNo);
    setShowDraftModal(true);
  };

  const handleStash = () => {
    handleGetDrafts();
  };

  // 删除草稿
  const handleDeleteDraft = () => {};

  // 一键清空草稿箱
  const handleClearAllDrafts = () => {};

  // 载入草稿
  const handleLoadDraft = (draft: any) => {
    console.log('draft:   ', draft);

    localStorage.setItem('draftData', JSON.stringify(draft));

    // 打开编辑弹窗（新增模式）
    showFromPageData?.('', true);

    // 关闭草稿箱弹窗
    setShowDraftModal(false);
  };

  //   console.log('draftTableColumns:   ', tableColumns);

  // 复用列表列的结构（保持原 render），仅去除 op/index，外侧补序号/操作
  const draftColumns = useMemo(() => {
    const baseColumns = tableColumns?.filter((col) => col.dataIndex !== 'op' && col.dataIndex !== 'index') || [];

    return [
      {
        title: '序号',
        dataIndex: 'index',
        width: 80,
        fixed: 'left',
        render: (_: any, __: any, idx: number) => {
          const pageNo = Number(draftPageNo) || 1;
          const size = Number(pageSize) || 10;
          return (pageNo - 1) * size + idx + 1;
        }
      },
      {
        title: '暂存时间',
        dataIndex: 'created_time',
        width: 180,
        render: (timestamp: number) => dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
      },
      ...baseColumns,
      {
        title: '操作',
        dataIndex: 'op',
        width: 180,
        fixed: 'right',
        align: 'center',
        headerCellStyle: { textAlign: 'center' },
        render: (_: any, record: any) => {
          return (
            <Space>
              <Button type="text" size="small" onClick={() => handleLoadDraft(record)}>
                继续编辑
              </Button>
              <Divider type="vertical" />
              <Popconfirm title="确认删除" content="删除后无法恢复，是否确认删除？" onOk={() => handleDeleteDraft()}>
                <Button type="text" size="small" status="danger" icon={<IconDelete />}>
                  删除
                </Button>
              </Popconfirm>
            </Space>
          );
        }
      }
    ];
  }, [tableColumns, draftPageNo, pageSize]);

  return (
    <>
      {/* 草稿箱按钮 */}
      <div
        onClick={handleStash}
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '14px', gap: '8px' }}
      >
        <img src={draftBoxIcon} alt="draftBox" style={{ width: '14px', height: '14px' }} />
        草稿箱
        {draftTotal > 0 ? <Badge count={draftTotal} dotStyle={{ background: '#008699', color: '#FFFFFF' }} /> : ''}
      </div>

      {/* 草稿箱弹窗 */}
      <Modal
        title="草稿箱"
        visible={showDraftModal}
        onCancel={() => setShowDraftModal(false)}
        footer={null}
        style={{ width: '1000px' }}
      >
        {/* 提示和操作栏 */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '16px',
            padding: '8px 0'
          }}
        >
          <Alert
            style={{ color: '#E8FFFE', fontSize: '12px', width: '90%' }}
            content="90天内未更新的草稿将被自动删除"
          />
          <Popconfirm title="确认清空" content="清空后无法恢复，是否确认清空所有草稿？" onOk={handleClearAllDrafts}>
            <div
              style={{ color: '#4E5969', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '4px' }}
            >
              <img src={clearIcon} alt="clear" style={{ width: '14px', height: '14px' }} />
              一键清空
            </div>
          </Popconfirm>
        </div>
        <Form form={draftForm}>
          <Table
            scroll={{ x: 'max-content' }}
            columns={draftColumns}
            data={draftTableData}
            pagination={{
              current: draftPageNo,
              total: draftTotal,
              pageSize,
              showTotal: true,
              onChange: (pageNo: number) => {
                setDraftPageNo(pageNo);
              }
            }}
          />
        </Form>
      </Modal>
    </>
  );
};

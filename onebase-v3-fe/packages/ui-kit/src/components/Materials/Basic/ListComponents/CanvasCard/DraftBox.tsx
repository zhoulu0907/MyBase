import clearIcon from '@/assets/images/clear.svg';
import draftBoxIcon from '@/assets/images/draft.svg';
import { Alert, Badge, Button, Divider, Form, Modal, Popconfirm, Space } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { deleteDraft, deleteDraftTable, getDraftPage, getDraftDetail } from '@onebase/app';
import { isRuntimeEnv } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import dayjs from 'dayjs';
import React, { useCallback, useEffect, useMemo, useState } from 'react';

interface DraftBoxProps {
  menuId: string;
  tableName: string;
  showFromPageData?: Function;
  refresh?: number;
}

export const CanvasCardDraftBox: React.FC<DraftBoxProps> = ({ showFromPageData, menuId, tableName, refresh }) => {
  useSignals();
  const [draftForm] = Form.useForm();

  const [showDraftModal, setShowDraftModal] = useState(false);
  const [draftTableData, setDraftTableData] = useState<any[]>([]);
  const [draftTotal, setDraftTotal] = useState<number>(0);
  const [draftPageNo, setDraftPageNo] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);

  const fetchDraftTotal = useCallback(async () => {
    if (!tableName || !menuId || !isRuntimeEnv()) return;
    try {
      const res = await getDraftPage(tableName, menuId, { pageNo: 1, pageSize: 1 });
      const { total = 0 } = res || {};
      setDraftTotal(total);
    } catch (error) {
      console.error('获取草稿总数失败:', error);
    }
  }, [tableName, menuId]);

  const handleGetDrafts = async () => {
    if (!tableName || !menuId || !isRuntimeEnv()) return;

    const res = await getDraftPage(tableName, menuId, { pageNo: draftPageNo, pageSize });

    const { list = [], total = 0, pageNo = 1 } = res || {};

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

    setDraftTableData(newTableData);
    draftForm.setFieldsValue({ [tableName]: newTableData });
    setDraftTotal(total);
    setDraftPageNo(pageNo);
    setShowDraftModal(true);
  };

  const handleStash = () => {
    handleGetDrafts();
  };

  useEffect(() => {
    fetchDraftTotal();
  }, [fetchDraftTotal, refresh]);

  const handleDeleteDraft = async (id: string) => {
    await deleteDraft(tableName, menuId, { id });
    handleGetDrafts();
  };

  const handleClearAllDrafts = async () => {
    await deleteDraftTable(tableName, menuId);
    handleGetDrafts();
  };

  const handleLoadDraft = async(draft: any) => {
    const param = {
      id: draft.id
    }
    const draftDetail = await getDraftDetail(tableName, menuId, param)
    localStorage.setItem('draftData', JSON.stringify(draftDetail || draft ));

    showFromPageData?.('', true);

    setShowDraftModal(false);
  };

  const draftColumns = useMemo(() => {
    const baseColumns = [
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
      }
    ];

    return [
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
              <Popconfirm
                title="确认删除"
                content="删除后无法恢复，是否确认删除？"
                onOk={() => handleDeleteDraft(record.id)}
              >
                <Button type="text" size="small" status="danger" icon={<IconDelete />}>
                  删除
                </Button>
              </Popconfirm>
            </Space>
          );
        }
      }
    ];
  }, [draftPageNo, pageSize]);

  return (
    <>
      <div
        onClick={handleStash}
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '14px', gap: '8px' }}
      >
        <img src={draftBoxIcon} alt="draftBox" style={{ width: '14px', height: '14px' }} />
        草稿箱
        {draftTotal > 0 ? <Badge count={draftTotal} dotStyle={{ background: '#008699', color: '#FFFFFF' }} /> : ''}
      </div>

      <Modal
        title="草稿箱"
        visible={showDraftModal}
        onCancel={() => {
          setShowDraftModal(false);
          fetchDraftTotal();
        }}
        footer={null}
        style={{ width: '1000px' }}
      >
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
          <div style={{ maxHeight: '600px', overflow: 'auto' }}>
            {draftTableData.map((draft: any, index: number) => (
              <div
                key={draft.id || index}
                style={{
                  border: '1px solid #E5E7EB',
                  borderRadius: '8px',
                  padding: '16px',
                  marginBottom: '12px',
                  backgroundColor: '#FAFAFA'
                }}
              >
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                  <div style={{ fontSize: '14px', fontWeight: '600', color: '#333' }}>
                    草稿 {index + 1}
                  </div>
                  <div style={{ fontSize: '12px', color: '#999' }}>
                    {dayjs(draft.created_time).format('YYYY-MM-DD HH:mm:ss')}
                  </div>
                </div>
                <div style={{ fontSize: '14px', color: '#666', lineHeight: '1.6' }}>
                  {draft.data?.title || draft.data?.content || '无标题'}
                </div>
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '8px', marginTop: '12px' }}>
                  <Button type="primary" size="small" onClick={() => handleLoadDraft(draft)}>
                    继续编辑
                  </Button>
                  <Popconfirm
                    title="确认删除"
                    content="删除后无法恢复，是否确认删除？"
                    onOk={() => handleDeleteDraft(draft.id)}
                  >
                    <Button type="text" size="small" status="danger" icon={<IconDelete />}>
                      删除
                    </Button>
                  </Popconfirm>
                </div>
              </div>
            ))}
          </div>
        </Form>
      </Modal>
    </>
  );
};

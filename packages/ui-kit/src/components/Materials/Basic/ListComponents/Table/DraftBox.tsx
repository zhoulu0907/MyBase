import clearIcon from '@/assets/images/clear.svg';
import draftBoxIcon from '@/assets/images/draft.svg';
import { usePageViewEditorSignal } from '@/signals';
import { Alert, Badge, Button, Divider, Message, Modal, Popconfirm, Space, Table } from '@arco-design/web-react';
import { IconDelete } from '@arco-design/web-react/icon';
import { getRuntimeFormCacheKey, getRuntimeFormLoadDraftKey, TokenManager } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import dayjs from 'dayjs';
import React, { useEffect, useState } from 'react';

interface DraftBoxProps {
  showFromPageData?: Function;
  tableColumns?: any[];
}

interface DraftItem {
  data: any;
  timestamp: number;
}

/**
 * 获取草稿列表
 */
const getDrafts = (viewId: string): DraftItem[] => {
  try {
    const tokenInfo = TokenManager.getTokenInfo();
    if (!tokenInfo?.userId || !viewId) {
      return [];
    }

    const userId = tokenInfo.userId;
    const cacheKey = getRuntimeFormCacheKey(userId, viewId);
    const rawDrafts = localStorage.getItem(cacheKey);

    if (!rawDrafts) {
      return [];
    }

    try {
      const parsed = JSON.parse(rawDrafts);
      const drafts = Array.isArray(parsed) ? parsed : [parsed];
      // 过滤掉不符合格式的数据（必须有 data 和 timestamp）
      const validDrafts = drafts.filter(
        (draft: any) => draft && typeof draft === 'object' && 'data' in draft && 'timestamp' in draft
      );
      return validDrafts;
    } catch {
      return [];
    }
  } catch (error) {
    console.error('获取草稿数据失败:', error);
    return [];
  }
};

/**
 * 草稿箱组件
 */
export const DraftBox: React.FC<DraftBoxProps> = ({ showFromPageData, tableColumns }) => {
  useSignals();

  const { curViewId } = usePageViewEditorSignal;
  const [showDraftModal, setShowDraftModal] = useState(false);
  const [draftList, setDraftList] = useState<DraftItem[]>([]);
  const [draftTotal, setDraftTotal] = useState<number>(0);
  const [draftPageNo, setDraftPageNo] = useState<number>(1);
  const [draftCount, setDraftCount] = useState<number>(0);

  // 更新草稿数量
  useEffect(() => {
    const drafts = getDrafts(curViewId.value);
    setDraftCount(drafts.length);
  }, [curViewId.value, showDraftModal]);

  // 打开草稿箱
  const handleStash = () => {
    const drafts = getDrafts(curViewId.value);
    setDraftList(drafts);
    setDraftTotal(drafts.length);
    setDraftPageNo(1);
    setShowDraftModal(true);
  };

  // 删除草稿
  const handleDeleteDraft = (actualIndex: number, viewId: string) => {
    try {
      const tokenInfo = TokenManager.getTokenInfo();
      if (!tokenInfo?.userId) {
        return;
      }

      const userId = tokenInfo.userId;
      const cacheKey = getRuntimeFormCacheKey(userId, viewId);
      const drafts = getDrafts(viewId);
      // 使用实际索引删除
      drafts.splice(actualIndex, 1);
      localStorage.setItem(cacheKey, JSON.stringify(drafts));
      Message.success('删除成功');
      // 刷新草稿列表
      const updatedDrafts = getDrafts(viewId);
      setDraftList(updatedDrafts);
      setDraftTotal(updatedDrafts.length);
      setDraftCount(updatedDrafts.length);
      // 如果当前页没有数据了，回到上一页
      const pageSize = 10;
      const maxPage = Math.ceil(updatedDrafts.length / pageSize);
      if (draftPageNo > maxPage && maxPage > 0) {
        setDraftPageNo(maxPage);
      } else if (updatedDrafts.length === 0) {
      }
    } catch (error) {
      console.error('删除草稿失败:', error);
      Message.error('删除失败，请重试！');
    }
  };

  // 一键清空草稿箱
  const handleClearAllDrafts = () => {
    try {
      const tokenInfo = TokenManager.getTokenInfo();
      if (!tokenInfo?.userId || !curViewId.value) {
        return;
      }

      const userId = tokenInfo.userId;
      const viewId = curViewId.value;
      const cacheKey = getRuntimeFormCacheKey(userId, viewId);

      // 清空所有草稿
      localStorage.removeItem(cacheKey);
      Message.success('已清空所有草稿');

      // 刷新草稿列表
      setDraftList([]);
      setDraftTotal(0);
      setDraftCount(0);
      setDraftPageNo(1);
    } catch (error) {
      console.error('清空草稿失败:', error);
      Message.error('清空失败，请重试！');
    }
  };

  // 载入草稿
  const handleLoadDraft = (draft: DraftItem) => {
    if (draft?.data && showFromPageData) {
      // 将草稿数据临时存储到 localStorage，供 EditRuntime 组件读取
      const tokenInfo = TokenManager.getTokenInfo();
      if (tokenInfo?.userId && curViewId.value) {
        const userId = tokenInfo.userId;
        const viewId = curViewId.value;
        const loadDraftKey = getRuntimeFormLoadDraftKey(userId, viewId);
        // 存储要载入的草稿数据
        localStorage.setItem(loadDraftKey, JSON.stringify(draft.data));

        // 打开编辑弹窗（新增模式）
        showFromPageData('', true);

        // 关闭草稿箱弹窗
        setShowDraftModal(false);
      }
    }
  };

  // 根据表格列生成草稿箱的列定义，显示所有字段
  const getFieldColumns = (columns: any[]): any[] => {
    if (!columns || columns.length === 0) {
      return [];
    }

    // 过滤掉序号、暂存时间和操作列
    const filteredColumns = columns.filter((column) => {
      const dataIndex = column.dataIndex;
      // 排除序号、暂存时间和操作列
      if (dataIndex === 'index' || dataIndex === 'timestamp' || dataIndex === 'op' || dataIndex === 'select') {
        return false;
      }
      return true;
    });

    // 返回过滤后的列，使用表格列的中文名称
    return filteredColumns.map((column) => ({
      title: column.title,
      dataIndex: column.dataIndex,
      width: column.width || 150,
      ellipsis: true,
      render: (value: any) => {
        // 处理不同类型的值
        if (value === null || value === undefined) {
          return '-';
        }

        if (typeof value === 'object') {
          // 如果是对象，尝试获取 displayValue 或 userName 等属性
          if ('displayValue' in value && typeof value.displayValue !== 'undefined') {
            return value.displayValue;
          }
          if ('userName' in value && typeof value.userName !== 'undefined') {
            return value.userName;
          }
          if ('name' in value && typeof value.name !== 'undefined') {
            return value.name;
          }
          // 如果是数组，直接返回拼接后的字符串，多个值用逗号分隔
          if (Array.isArray(value)) {
            // 如果是数组，只拼接 name 字段
            return value
              .map((item) => {
                if (item && typeof item === 'object' && 'name' in item) {
                  return item.name;
                }
                return '-';
              })
              .join(', ');
          }
          return JSON.stringify(value);
        }
        return String(value);
      }
    }));
  };

  return (
    <>
      {/* 草稿箱按钮 */}
      <div
        onClick={handleStash}
        style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '14px', gap: '8px' }}
      >
        <img src={draftBoxIcon} alt="draftBox" style={{ width: '14px', height: '14px' }} />
        草稿箱
        {draftCount > 0 ? <Badge count={draftCount} dotStyle={{ background: '#008699', color: '#FFFFFF' }} /> : ''}
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
        <Table
          scroll={{ x: 'max-content' }}
          columns={[
            {
              title: '序号',
              dataIndex: 'index',
              width: 80,
              fixed: 'left',
              render: (_: any, __: any, idx: number) => {
                const pageSize = 10;
                return (draftPageNo - 1) * pageSize + idx + 1;
              }
            },
            {
              title: '暂存时间',
              dataIndex: 'timestamp',
              width: 180,
              render: (timestamp: number) => {
                return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss');
              }
            },
            ...getFieldColumns(tableColumns || []),
            {
              title: '操作',
              dataIndex: 'op',
              width: 180,
              fixed: 'right',
              align: 'center',
              headerCellStyle: { textAlign: 'center' },
              render: (_: any, record: DraftItem, index: number) => {
                // 计算实际索引（考虑分页）
                const pageSize = 10;
                const actualIndex = (draftPageNo - 1) * pageSize + index;
                return (
                  <Space>
                    <Button type="text" size="small" onClick={() => handleLoadDraft(record)}>
                      继续编辑
                    </Button>
                    <Divider type="vertical" />
                    <Popconfirm
                      title="确认删除"
                      content="删除后无法恢复，是否确认删除？"
                      onOk={() => handleDeleteDraft(actualIndex, curViewId.value)}
                    >
                      <Button type="text" size="small" status="danger" icon={<IconDelete />}>
                        删除
                      </Button>
                    </Popconfirm>
                  </Space>
                );
              }
            }
          ]}
          data={draftList.slice((draftPageNo - 1) * 10, draftPageNo * 10).map((draft) => ({
            ...draft,
            ...draft.data // 将 data 中的字段展开到顶层，方便表格显示
          }))}
          pagination={{
            current: draftPageNo,
            total: draftTotal,
            pageSize: 10,
            showTotal: true,
            onChange: (pageNo: number) => {
              setDraftPageNo(pageNo);
            }
          }}
        />
      </Modal>
    </>
  );
};

import { Button, Dropdown, Menu, Tag, Typography } from '@arco-design/web-react';
import { IconEdit, IconMoreVertical } from '@arco-design/web-react/icon';
import { TypeCode, type ConnectInstance } from '@onebase/app';
import dayjs from 'dayjs';
import React from 'react';
import httpNodeIcon from '@/assets/flow/connect/http_node.png';
import jsNodeIcon from '@/assets/flow/connect/js_node.png';
import styles from './index.module.less';

/**
 * 根据连接器实例信息获取对应的图标
 * @param data 连接器实例数据
 * @returns 图标路径，泛微连接器返回 null（隐藏图标）
 */
const getInstanceIcon = (data: ConnectInstance): string | null => {
  // 泛微连接器：隐藏图标（通过 nodeCode 或 connectorTypeName 判断）
  if (data.nodeCode?.toLowerCase().includes('weaver') || data.connectorTypeName?.includes('泛微')) {
    return null;
  }

  // HTTP 连接器（通过 typeCode 或 nodeCode 判断）
  if (data.typeCode?.toUpperCase() === 'HTTP' ||
      data.nodeCode?.toUpperCase().includes('HTTP')) {
    return httpNodeIcon;
  }

  // JS/脚本连接器（通过 typeCode 或 nodeCode 判断）
  if (data.typeCode === TypeCode.SCRIPT ||
      data.typeCode?.toUpperCase() === 'SCRIPT' ||
      data.nodeCode?.toUpperCase().includes('SCRIPT') ||
      data.nodeCode?.toUpperCase().includes('JS')) {
    return jsNodeIcon;
  }

  // 其他连接器：返回 null（不显示图标）
  return null;
};

/**
 * FlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface CardProps {
  data: ConnectInstance;
  onEdit: Function;
  onDelete: Function;
}

const ConnectInstanceCard: React.FC<CardProps> = ({ data, onEdit, onDelete }) => {
  const handleEdit = (id: string) => {
    onEdit(id);
  };

  const handleDelete = (id: string) => {
    onDelete(id);
  };

  // 获取图标
  const icon = getInstanceIcon(data);
  const hasIcon = icon !== null;

  return (
    <div
      className={styles.card}
    >
      <div className={styles.cardHeader}>
        {hasIcon && (
          <div className={styles.cardHeaderIcon}>
            <img src={icon!} alt={data.connectorTypeName || ''} />
          </div>
        )}
        <div className={hasIcon ? styles.cardHeaderContent : styles.cardHeaderContentNoIcon}>
          <div className={styles.cardHeaderTitle}>
            <Typography.Text ellipsis={{ showTooltip: true }} style={{ fontSize: 16, fontWeight: 500 }}>
              {data.connectorName}
            </Typography.Text>
            <div className={styles.cardHeaderBadges} style={{ marginLeft: 8, display: 'inline-flex', gap: 8 }}>
              <Tag color="green" size="small">已启用</Tag>
              {/* 这里的 status 是 mock 的，后端需要返回 */}
              <Tag color={data.status === 'configured' ? 'blue' : 'gray'} size="small">
                {data.status === 'configured' ? '已配置' : '未配置'}
              </Tag>
            </div>
          </div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>关联类型</div>
          <div className={styles.cardBodyRowContent}>{data.connectorTypeName || (data.typeCode === TypeCode.SCRIPT ? 'JavaScript脚本' : data.typeCode)}</div>
        </div>

        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>环境信息</div>
          <div className={styles.cardBodyRowContent}>{data.environment || '-'}</div>
        </div>

        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}>创建时间</div>
          <div className={styles.cardBodyRowContent}>{dayjs(data.createTime).format('YYYY-MM-DD HH:mm:ss')}</div>
        </div>
      </div>
      <div
        className={styles.cardFooter}
        onClick={(e) => {
          e.stopPropagation();
        }}
      >
        <div className={styles.cardFooterRight} style={{ width: '100%', display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
          <Button
            type="text"
            size="small"
            icon={<IconEdit />}
            onClick={(e) => {
              e.stopPropagation();
              handleEdit(data.id);
            }}
          >
            编辑
          </Button>
          <div
            onClick={(e) => {
              e.stopPropagation();
            }}
          >
            <Dropdown
              position="bl"
              trigger="click"
              droplist={
                <Menu>
                  <Menu.Item
                    key="delete"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleDelete(data.id);
                    }}
                  >
                    删除
                  </Menu.Item>
                </Menu>
              }
            >
              <Button type="text" size="small" icon={<IconMoreVertical />} />
            </Dropdown>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ConnectInstanceCard;

import { Button, Dropdown, Menu, Message, Switch, Typography } from '@arco-design/web-react';
import { IconCheckCircleFill, IconEdit, IconMoreVertical } from '@arco-design/web-react/icon';
import {
  disableETLFlow,
  enableETLFlow,
  ETL_FLOW_STATUS,
  ETL_SCHEDULE_STRATEGY,
  startETLFlow,
  type ETLFlowMgmt
} from '@onebase/app';
import dayjs from 'dayjs';
import React from 'react';

import etlCardArrowDown from '@/assets/images/etl_card_arrow_down.svg';
import etlTable from '@/assets/images/etl_table.svg';
import styles from './index.module.less';
/**
 * ETLFlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface ETLFlowCardProps {
  data: ETLFlowMgmt;
  handleEdit: Function;
  handleDelete: Function;
  handlePage: Function;
  toFlowEditor: Function;
}

const ETLFlowCard: React.FC<ETLFlowCardProps> = ({ data, handleEdit, handleDelete, handlePage, toFlowEditor }) => {
  const showTriggerType = () => {
    switch (data.scheduleStrategy) {
      case ETL_SCHEDULE_STRATEGY.FIXED:
        return '定时更新';
      case ETL_SCHEDULE_STRATEGY.OBSERVE:
        return '观察更新';
      case ETL_SCHEDULE_STRATEGY.MANUALLY:
        return '手动更新';
      default:
        return '未知';
    }
  };

  const handleChangeProcessStatus = async (id: string, checked: boolean) => {
    if (checked) {
      const res = await enableETLFlow(id);
      console.log(res);
      Message.success('启用成功');
    } else {
      const res = await disableETLFlow(id);
      console.log(res);
      Message.success('禁用成功');
    }
    handlePage();
  };

  const handleStartETLFlow = async (id: string) => {
    const res = await startETLFlow(id);
    console.log(res);
    Message.success('手动执行成功');
  };

  return (
    <div className={styles.card}>
      <div
        className={styles.cardHeader}
        onClick={() => {
          toFlowEditor(data.id);
        }}
      >
        <div className={styles.cardHeaderContentLeft}>
          <Typography.Text className={styles.cardHeaderContentTitle}>{data.flowName}</Typography.Text>

          <div className={styles.cardHeaderSubTitle}>
            <div className={styles.cardHeaderTag}>
              <div
                className={
                  data.scheduleStrategy === ETL_SCHEDULE_STRATEGY.FIXED
                    ? styles.cardHeaderTagTitle
                    : styles.cardHeaderBlueTagTitle
                }
              >
                {showTriggerType()}
              </div>
            </div>

            <div className={styles.cardHeaderSubTitleItem}>
              最后更新: {dayjs(data.lastSuccessTime).format('YYYY-MM-DD mm:ss')}
            </div>
          </div>
        </div>

        <div
          className={styles.cardHeaderContentRight}
          onClick={(e) => {
            e.stopPropagation();
          }}
        >
          <span>{data.enableStatus === ETL_FLOW_STATUS.ENABLED ? '已启用' : '禁用'}</span>
          <Switch
            size="small"
            checked={data.enableStatus === ETL_FLOW_STATUS.ENABLED}
            onChange={(checked) => handleChangeProcessStatus(data.id, checked)}
          />
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <img className={styles.cardBodyRowArrowDown} src={etlCardArrowDown} alt=""></img>
          <div className={styles.cardBodyRowDataSource}>
            <span>输入源:</span>
            <img src={etlTable} alt="" />
            <span>{data.sourceTables?.join(',')}</span>
          </div>
          <div className={styles.cardBodyRowDataSource}>
            <span>输出源: </span>
            <img src={etlTable} alt="" />
            <span>{data.targetTable}</span>
          </div>
          <div className={styles.cardBodyRowSyncDone}>
            <IconCheckCircleFill style={{ fontSize: '12px', color: '#00B42A', marginRight: '4px' }} />
            数据同步完成
          </div>
        </div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}>
          <Button type="text" size="small" className={styles.cardFooterLeftBtn} onClick={() => handleEdit(data.id)}>
            更新规则
          </Button>
          <Button type="text" size="small" className={styles.cardFooterLeftBtn}>
            查看日志
          </Button>
          {data.enableStatus === ETL_FLOW_STATUS.ENABLED && (
            <Button
              type="text"
              size="small"
              className={styles.cardFooterLeftBtn}
              onClick={() => handleStartETLFlow(data.id)}
            >
              手动执行
            </Button>
          )}
        </div>
        <div className={styles.cardFooterRight}>
          <IconEdit onClick={() => toFlowEditor(data.id)} />
          <Dropdown
            position="bl"
            trigger="click"
            droplist={
              <Menu>
                <Menu.Item key="delete" onClick={() => handleDelete(data.id)}>
                  删除
                </Menu.Item>
              </Menu>
            }
          >
            <IconMoreVertical />
          </Dropdown>
        </div>
      </div>
    </div>
  );
};

export default ETLFlowCard;

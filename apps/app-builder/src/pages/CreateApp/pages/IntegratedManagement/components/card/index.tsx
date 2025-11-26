import entityIcon from '@/assets/flow/flowManage/entity.png';
import { Button, Dropdown, Menu, Message, Switch, Typography } from '@arco-design/web-react';
import { IconDown } from '@arco-design/web-react/icon';
import { disableFlowMgmt, enableFlowMgmt, ProcessStatus, TriggerType, type FlowMgmt } from '@onebase/app';
import dayjs from 'dayjs';
import React from 'react';
import styles from './index.module.less';

/**
 * FlowCard 组件
 * 用于流程管理页面的卡片展示
 */
export interface FlowCardProps {
  data: FlowMgmt;
  handleEdit: Function;
  handleDelete: Function;
  refreshList: Function;
  toFlowEditor: Function;
}

const FlowCard: React.FC<FlowCardProps> = ({ data, handleEdit, handleDelete, refreshList, toFlowEditor }) => {
  const showTriggerType = () => {
    switch (data.triggerType) {
      case TriggerType.TIME:
        return '时间触发';
      case TriggerType.FORM:
        return '界面交互触发';
      case TriggerType.DATE_FIELD:
        return '日期字段触发';
      case TriggerType.ENTITY:
        return '表单(实体)触发';
      case TriggerType.API:
        return 'API触发';
      //   case TriggerType.BPM:
      //     return '子流程触发';
      default:
        return '未知';
    }
  };

  const handleChangeProcessStatus = async (id: string, checked: boolean) => {
    if (checked) {
      await enableFlowMgmt(id);
      Message.success('启用成功');
    } else {
      await disableFlowMgmt(id);
      Message.success('禁用成功');
    }
    refreshList();
  };

  return (
    <div className={styles.card}>
      <div
        className={styles.cardHeader}
        onClick={() => {
          toFlowEditor(data.applicationId, data.id);
        }}
      >
        <div className={styles.cardHeaderIcon}>
          <img src={entityIcon} alt="" />
        </div>
        <div className={styles.cardHeaderContent}>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentTitle}>
            {data.processName}
          </Typography.Text>
          <Typography.Text ellipsis={{ showTooltip: true }} className={styles.cardHeaderContentDesc}>
            {data.id}
          </Typography.Text>
        </div>
        {/* data.triggerType  TriggerType.FORM */}
        <div className={styles.cardHeaderTag}>
          <div
            className={
              data.triggerType === TriggerType.FORM ? styles.cardHeaderTagTitle : styles.cardHeaderBlueTagTitle
            }
          >
            {showTriggerType()}
          </div>
        </div>
      </div>
      <div
        className={styles.cardBody}
        onClick={() => {
          toFlowEditor(data.applicationId, data.id);
        }}
      >
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 创建时间</div>
          <div className={styles.cardBodyRowContent}> {dayjs(data.createTime).format('YYYY-MM-DD HH:mm:ss')}</div>
        </div>

        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 最后执行</div>
          <div className={styles.cardBodyRowContent}>{/* todo 暂不展示数据 */}</div>
        </div>

        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 执行次数</div>
          <div className={styles.cardBodyRowContent}>{/* todo 暂不展示数据 */}</div>
        </div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}>
          <Switch
            checked={data.enableStatus === ProcessStatus.ENABLED}
            onChange={(checked) => handleChangeProcessStatus(data.id, checked)}
          />
          <span>{data.enableStatus === ProcessStatus.ENABLED ? '已启用' : '禁用'}</span>
        </div>
        <div className={styles.cardFooterRight}>
          <Button type="text" size="small" onClick={() => handleEdit(data.id)}>
            编辑
          </Button>
          <Button type="text" size="small" className={styles.cardFooterRightBtn}>
            调试
          </Button>
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
            <Button type="text" size="small" className={styles.cardFooterRightBtn}>
              更多
              <IconDown />
            </Button>
          </Dropdown>
        </div>
      </div>
    </div>
  );
};

export default FlowCard;

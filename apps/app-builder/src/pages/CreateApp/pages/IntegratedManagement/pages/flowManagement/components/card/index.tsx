import { Button, Dropdown, Menu, Message, Switch } from '@arco-design/web-react';
import { IconRobot } from '@arco-design/web-react/icon';
import { disableFlowMgmt, enableFlowMgmt, ProcessDefinition, ProcessStatus, type FlowMgmt } from '@onebase/app';
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
}

const FlowCard: React.FC<FlowCardProps> = ({ data, handleEdit, handleDelete, refreshList }) => {
  const showProcessDefinition = () => {
    switch (data.processDefinition) {
      case ProcessDefinition.Time:
        return '时间触发';
      case ProcessDefinition.FORM:
        return '表单触发';
      case ProcessDefinition.DATE_FIELD:
        return '日期字段触发';
      case ProcessDefinition.ENTITY:
        return '实体触发';
      case ProcessDefinition.API:
        return 'API触发';
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
      <div className={styles.cardHeader}>
        <div className={styles.cardHeaderLeft}>
          <div className={styles.cardHeaderLeftIcon}>
            <IconRobot />
          </div>
          <div className={styles.cardHeaderLeftContent}>
            <div className={styles.cardHeaderLeftContentTitle}>{data.processName}</div>
            <div className={styles.cardHeaderLeftContentDesc}>{data.id}</div>
          </div>
        </div>
        <div className={styles.cardHeaderRight}>
          <div className={styles.cardHeaderRightTitle}>{showProcessDefinition()}</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 创建时间</div>
          <div className={styles.cardBodyRowContent}> {dayjs(data.createTime).format('YYYY-MM-DD HH:mm:ss')}</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 最后执行</div>
          <div className={styles.cardBodyRowContent}> xx</div>
        </div>
      </div>
      <div className={styles.cardBody}>
        <div className={styles.cardBodyRow}>
          <div className={styles.cardBodyRowLabel}> 执行次数</div>
          <div className={styles.cardBodyRowContent}> xx</div>
        </div>
      </div>
      <div className={styles.cardFooter}>
        <div className={styles.cardFooterLeft}>
          <Switch
            checked={data.processStatus === ProcessStatus.ENABLED}
            onChange={(checked) => handleChangeProcessStatus(data.id, checked)}
          />
        </div>
        <div className={styles.cardFooterRight}>
          <Button type="text" size="small" onClick={() => handleEdit(data.id)}>
            编辑
          </Button>
          <Button type="text" size="small">
            调试
          </Button>
          <Dropdown
            position="bl"
            trigger="click"
            droplist={
              <Menu>
                {' '}
                <Menu.Item key="delete" onClick={() => handleDelete(data.id)}>
                  删除
                </Menu.Item>{' '}
              </Menu>
            }
          >
            <Button type="text" size="small">
              更多
            </Button>
          </Dropdown>
        </div>
      </div>
    </div>
  );
};

export default FlowCard;

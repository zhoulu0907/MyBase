import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Button, SideSheet } from '@douyinfe/semi-ui';
import { Checkbox, Switch, Radio, Select, Button as ArcoButton, Message } from '@arco-design/web-react';
import { IconClose } from '@douyinfe/semi-icons';
import { getEntityFieldsWithChildren, getPageSetMetaData } from '@onebase/app';
import { getUserPage, type PageParam } from '@onebase/platform-center';
import { useFlowEditorStor } from '@/store/index';
import { type GlobalConfigData } from '../../context/globalConfigContext';
import { HandlerMode, Permission, Timing, Rule, AutoApproveType } from './constants';
import styles from './index.module.less';

const RadioGroup = Radio.Group;
const Option = Select.Option;
export function GlobalConfig(props = { visible: false, onClose: () => {} }) {
  const { configData, setConfigData } = useFlowEditorStor();
  const [userOptions, setUserOptions] = useState<any[]>([]);
  const [formSummaryOptions, setFormSummaryOptions] = useState<any[]>([]);
  const [useConfigData, setUseConfigData] = useState<GlobalConfigData>(configData);
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const pageSetId = searchParams.get('pageSetId') || '';
  const initUserData = () => {
    const params: PageParam = {
      pageNo: 1,
      pageSize: 100
    };
    getUserPage(params)
      .then((res: any) => {
        if (Array.isArray(res?.list)) {
          const selectArr: any[] = [];
          res.list?.forEach((item: any) => {
            selectArr.push({
              userId: item.id,
              name: item.nickname
            });
          });
          setUserOptions(selectArr);
        }
      })
      .catch((err: any) => {
        console.info('Api getUserPage Error:', err);
      });
  };

  // 表单摘要数据获取
  const getFormSummaryData = async () => {
    const mainMetaData = await getPageSetMetaData({ pageSetId: pageSetId });
    const { parentFields } = await getEntityFieldsWithChildren(mainMetaData);
    setFormSummaryOptions(parentFields);
  };

  // 同步全局配置的值
  useEffect(() => {
    if (props.visible) {
      setUseConfigData(configData);
    }
  }, [props.visible]);

  // 关闭侧边栏
  const onClose = async () => {
    props.onClose();
  };

  // 更新到全局配置
  const onSubmit = () => {
    if (
      useConfigData.emptyApproverCfg?.handlerMode === HandlerMode.TRANSFER_MEMBER &&
      !useConfigData.emptyApproverCfg?.transferMemberId
    ) {
      Message.error('请选择转交成员');
      return;
    }

    setConfigData(useConfigData);
    onClose();
  };

  //修改当前数据
  const dataChange = (value: any, name: string) => {
    const newData = Object.assign({}, useConfigData, { [name]: value });
    setUseConfigData(newData);
  };

  useEffect(() => {
    initUserData();
    getFormSummaryData();
  }, []);
  useEffect(() => {
    setUseConfigData(configData);
  }, [configData]);
  return (
    <SideSheet
      title="Test Run"
      visible={props.visible}
      mask={false}
      motion={false}
      width={700}
      headerStyle={{
        display: 'none'
      }}
      bodyStyle={{
        padding: 0
      }}
      style={{
        background: 'none',
        boxShadow: 'none'
      }}
    >
      <div className={styles.testrunPanelContainer}>
        <div className={styles.testrunPanelHeader}>
          <div className={styles.testrunPanelHeaderTop}>
            <div className={styles.testrunPanelTitle}>全局设置</div>
            <Button
              className={styles.testrunPanelTitle}
              type="tertiary"
              icon={<IconClose />}
              size="small"
              theme="borderless"
              onClick={onClose}
            />
          </div>
          <div className={styles.testrunPanelHeaderBottom}>
            <div className={styles.label}>允许节点自定义</div>
            <Switch
              onChange={(value) => dataChange(value, 'useNodeConfig')}
              checked={useConfigData.useNodeConfig}
              style={{ marginRight: '22px' }}
            />
            <div className={styles.tips}>{useConfigData.useNodeConfig?'关闭时，字段权限跟随表单组件状态自动同步。':'开启后，节点可基于全局设置单独调整；关闭后，统一按全局设置执行。'}</div>
          </div>
        </div>
        <div className={styles.testrunPanelContent}>
          <div className={styles.testrunPanelContentInner}>
            {/* 自动审批 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>自动审批</div>
              <div className={styles.configContent}>
                <Checkbox.Group
                  value={Object.entries(useConfigData.autoApproveCfg)
                    .filter(([_, value]) => value)
                    .map(([key]) => key)}
                  onChange={(value) => {
                    const newAutoApproveCfg = {
                      [AutoApproveType.INIT_AUTO_APPROVE]: value.includes(AutoApproveType.INIT_AUTO_APPROVE),
                      [AutoApproveType.DUP_USER_AUTO_APPROVE]: value.includes(AutoApproveType.DUP_USER_AUTO_APPROVE),
                      [AutoApproveType.PREV_NODE_DUP_USER_AUTO_APPROVE]: value.includes(
                        AutoApproveType.PREV_NODE_DUP_USER_AUTO_APPROVE
                      )
                    };
                    dataChange(newAutoApproveCfg, 'autoApproveCfg');
                  }}
                >
                  {[
                    AutoApproveType.INIT_AUTO_APPROVE,
                    AutoApproveType.DUP_USER_AUTO_APPROVE,
                    AutoApproveType.PREV_NODE_DUP_USER_AUTO_APPROVE
                  ].map((item) => {
                    return (
                      <Checkbox key={item} value={item}>
                        {item === AutoApproveType.INIT_AUTO_APPROVE && (
                          <span className={styles.checkText}>
                            发起人自动审批
                            <span className={styles.checkTextTips}>（当前节点人员为发起人时，自动审批）</span>
                          </span>
                        )}
                        {item === AutoApproveType.DUP_USER_AUTO_APPROVE && (
                          <span className={styles.checkText}>
                            上级自动审批<span className={styles.checkTextTips}>（当前节点人员为上级时，自动审批）</span>
                          </span>
                        )}
                        {item === AutoApproveType.PREV_NODE_DUP_USER_AUTO_APPROVE && (
                          <span className={styles.checkText}>
                            指定人员自动审批
                            <span className={styles.checkTextTips}>（当前节点人员为指定人员时，自动审批）</span>
                          </span>
                        )}
                      </Checkbox>
                    );
                  })}
                </Checkbox.Group>
              </div>
            </div>
            {/* 审批人为空时 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>审批人为空时</div>
              <div className={styles.configContent}>
                <RadioGroup
                  className={styles.enptyApprover}
                  value={useConfigData.emptyApproverCfg?.handlerMode}
                  onChange={(value) =>
                    dataChange(
                      {
                        ...useConfigData.emptyApproverCfg,
                        handlerMode: value
                      },
                      'emptyApproverCfg'
                    )
                  }
                >
                  <Radio value={HandlerMode.PAUSE}>流程暂停（即不允许为空）</Radio>
                  <Radio value={HandlerMode.SKIP}>自动跳过节点</Radio>
                  <Radio value={HandlerMode.TRANSFER_ADMIN}>转交给应用管理员</Radio>
                  <Radio value={HandlerMode.TRANSFER_MEMBER}>转交给指定成员</Radio>
                </RadioGroup>
                {useConfigData.emptyApproverCfg?.handlerMode === HandlerMode.TRANSFER_MEMBER && (
                  <Select
                    className={styles.transferMember}
                    placeholder="选择人员"
                    allowClear
                    value={useConfigData.emptyApproverCfg?.transferMemberId}
                    onChange={(value) => {
                      dataChange(
                        {
                          ...useConfigData.emptyApproverCfg,
                          transferMemberId: value || ''
                        },
                        'emptyApproverCfg'
                      );
                    }}
                  >
                    {userOptions?.map((option: any) => (
                      <Option key={option?.userId} value={option?.userId}>
                        {option.name}
                      </Option>
                    ))}
                  </Select>
                )}
              </div>
            </div>
            {/* 流程撤回规则 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>流程撤回规则</div>
              <div className={styles.configContent}>
                <div className={styles.secondTitle}>撤回权限</div>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.withdrawRuleCfg?.permission}
                  onChange={(value) =>
                    dataChange(
                      {
                        ...useConfigData.withdrawRuleCfg,
                        permission: value
                      },
                      'withdrawRuleCfg'
                    )
                  }
                >
                  <Radio value={Permission.NONE}>不允许撤回</Radio>
                  <Radio value={Permission.INITIATION_NODE}>仅允许发起节点撤回</Radio>
                  {/* <Radio value="any">允许所有节点撤回</Radio> */}
                </RadioGroup>

                <div className={styles.secondTitle}>撤回时机</div>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.withdrawRuleCfg?.timing}
                  onChange={(value) =>
                    dataChange(
                      {
                        ...useConfigData.withdrawRuleCfg,
                        timing: value
                      },
                      'withdrawRuleCfg'
                    )
                  }
                >
                  <Radio value={Timing.UNPROCESSED}>未操作</Radio>
                  <Radio value={Timing.UNREAD}>未读</Radio>
                </RadioGroup>
              </div>
            </div>
            {/* 流程退回规则 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>流程退回规则</div>
              <div className={styles.configContent}>
                <div className={styles.secondTitle}>被回退节点重新提交时</div>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.returnRuleCfg?.rule}
                  onChange={(value) =>
                    dataChange(
                      {
                        ...useConfigData.returnRuleCfg,
                        rule: value
                      },
                      'returnRuleCfg'
                    )
                  }
                >
                  <Radio value={Rule.SEQ}>按流程顺序重新审批</Radio>
                  <Radio value={Rule.DIRECT}>直达当前节点</Radio>
                </RadioGroup>
              </div>
            </div>
            {/* 发起人终止流程权限 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>发起人终止流程权限</div>
              <div className={styles.configContent}>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.initiatorTerminateCfg?.permission}
                  onChange={(value) =>
                    dataChange(
                      {
                        ...useConfigData.initiatorTerminateCfg,
                        permission: value
                      },
                      'initiatorTerminateCfg'
                    )
                  }
                >
                  <Radio value={Permission.INITIATION_NODE}>仅可在发起节点终止</Radio>
                  <Radio value={Permission.ANY}>可在任意环节终止</Radio>
                  <Radio value={Permission.NONE}>发起人无终止权限</Radio>
                </RadioGroup>
              </div>
            </div>
            {/* 表单摘要 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>
                表单摘要
                <div className={styles.titleTips}>选取表单中1~3个字段作为摘要，帮助审批人快速了解待审批的关键内容</div>
              </div>

              <div className={styles.configContent}>
                <Select
                  mode="multiple"
                  maxTagCount={10}
                  placeholder="选择表单字段"
                  value={useConfigData.formSummaryCfg?.fieldConfigs.map((field) => field.fieldId)}
                  onChange={(value) =>
                    dataChange(
                      {
                        fieldConfigs: value.map((fieldId: string) => {
                          const field = formSummaryOptions.find((f) => f.fieldId === fieldId);
                          return {
                            fieldId,
                            fieldName: field?.fieldName || '',
                            displayName: field?.displayName || ''
                          };
                        })
                      },
                      'formSummaryCfg'
                    )
                  }
                  allowClear
                  className={styles.summarySelect}
                >
                  {formSummaryOptions.map((option) => (
                    <Option
                      key={option.fieldId}
                      value={option.fieldId}
                      disabled={
                        useConfigData.formSummaryCfg?.fieldConfigs.length >= 3 &&
                        !useConfigData.formSummaryCfg?.fieldConfigs.some((field) => field.fieldId === option.fieldId)
                      }
                    >
                      {option.displayName}
                    </Option>
                  ))}
                </Select>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.testrunPanelFooter}>
          <ArcoButton onClick={onSubmit} className={styles.submit} type="primary">
            确定
          </ArcoButton>
          <ArcoButton type="secondary" onClick={onClose}>
            取消
          </ArcoButton>
        </div>
      </div>
    </SideSheet>
  );
}

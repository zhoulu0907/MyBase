import { Button, SideSheet } from '@douyinfe/semi-ui';
import { Checkbox, Switch, Radio, Tooltip, Select, Button as ArcoButton } from '@arco-design/web-react';
import { IconQuestionCircle } from '@arco-design/web-react/icon';
import styles from './index.module.less';
import { IconClose } from '@douyinfe/semi-icons';
import { GlobalConfigContext, type GlobalConfigData } from '../../context/globalConfigContext';
import { useContext, useEffect, useState } from 'react';
const RadioGroup = Radio.Group;
const Option = Select.Option;
const options = ['Beijing', 'Shanghai', 'Guangzhou', 'Shenzhen', 'Chengdu', 'Wuhan'];
export function GlobalConfig(props = { visible: false, onClose: () => {} }) {
  const { configData, setConfigData } = useContext(GlobalConfigContext);
  const [useConfigData, setUseConfigData] = useState<GlobalConfigData>(configData);

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
    setConfigData(useConfigData);
    onClose();
  };

  //修改当前数据
  const dataChange = (value: any, name: string) => {
    const newData = Object.assign({}, useConfigData, { [name]: value });
    setUseConfigData(newData);
  };
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
              onChange={(value) => dataChange(value, 'allowNodeCustomization')}
              checked={useConfigData.allowNodeCustomization}
              style={{ marginRight: '22px' }}
            />
            <div className={styles.tips}>开启后，节点可基于全局设置单独调整；关闭后，统一按全局设置执行。</div>
          </div>
        </div>
        <div className={styles.testrunPanelContent}>
          <div className={styles.testrunPanelContentInner}>
            {/* 自动审批 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>自动审批</div>
              <div className={styles.configContent}>
                <Checkbox.Group
                  value={useConfigData.autoApproval}
                  onChange={(value) => dataChange(value, 'autoApproval')}
                >
                  {['Beijing', 'Shanghai', 'Guangzhou'].map((item) => {
                    return (
                      <Checkbox key={item} value={item}>
                        {item === 'Beijing' && (
                          <span className={styles.checkText}>
                            发起人自动审批
                            <span className={styles.checkTextTips}>（当前节点人员为发起人时，自动审批）</span>
                          </span>
                        )}
                        {item === 'Shanghai' && (
                          <span className={styles.checkText}>
                            上级自动审批<span className={styles.checkTextTips}>（当前节点人员为上级时，自动审批）</span>
                          </span>
                        )}
                        {item === 'Guangzhou' && (
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
                  value={useConfigData.emptyApprover}
                  onChange={(value) => dataChange(value, 'emptyApprover')}
                >
                  <Radio value="a">
                    流程暂停（即不允许为空）
                    <Tooltip position="right" trigger="hover" content="This is a Tooltip">
                      <IconQuestionCircle />{' '}
                    </Tooltip>
                  </Radio>
                  <Radio value="b">自动跳过节点</Radio>
                  <Radio value="c">转交给应用管理员</Radio>
                  <Radio value="d">转交给指定成员</Radio>
                </RadioGroup>
              </div>
            </div>
            {/* 流程撤回规则 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>流程撤回规则</div>
              <div className={styles.configContent}>
                <div className={styles.secondTitle}>撤回权限</div>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.permission}
                  onChange={(value) => dataChange(value, 'permission')}
                >
                  <Radio value="a">不允许撤回</Radio>
                  <Radio value="b">仅允许发起节点撤回</Radio>
                  <Radio value="c">允许所有节点撤回</Radio>
                </RadioGroup>
                <div className={styles.secondTitle}>撤回时机</div>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.timing}
                  onChange={(value) => dataChange(value, 'timing')}
                >
                  <Radio value="a">
                    未操作<span className={styles.radioTextTips}>（下一人工节点处理人未进行流程处理操作）</span>
                  </Radio>
                  <Radio value="b">
                    未读<span className={styles.radioTextTips}>（下一人工节点处理人未查看详情）</span>
                  </Radio>
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
                  value={useConfigData.returnRules}
                  onChange={(value) => dataChange(value, 'returnRules')}
                >
                  <Radio value="a">按流程顺序重新审批</Radio>
                  <Radio value="b">直达当前节点</Radio>
                </RadioGroup>
              </div>
            </div>
            {/* 发起人终止流程权限 */}
            <div className={styles.configItem}>
              <div className={styles.configTitle}>发起人终止流程权限</div>
              <div className={styles.configContent}>
                <RadioGroup
                  direction="vertical"
                  value={useConfigData.terminatePermission}
                  onChange={(value) => dataChange(value, 'terminatePermission')}
                >
                  <Radio value="a">仅可在发起节点终止</Radio>
                  <Radio value="b">可在任意环节终止</Radio>
                  <Radio value="c">发起人无终止权限</Radio>
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
                  placeholder="Select cities"
                  style={{ width: 500 }}
                  value={useConfigData.summaryFields}
                  onChange={(value) => dataChange(value, 'summaryFields')}
                  allowClear
                  className={styles.summarySelect}
                >
                  {options.map((option) => (
                    <Option key={option} value={option}>
                      {option}
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

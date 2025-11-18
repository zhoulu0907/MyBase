import { useState, useEffect, useMemo } from 'react';
import { Checkbox, Radio } from '@arco-design/web-react';
import TimeModal from './TimeModal';
import type { AdvancedConfig, AdvancedConfigType } from './constant';
import './style.less';

const CheckboxGroup = Checkbox.Group;
const RadioGroup = Radio.Group;

const autoCheckArr = [
  {
    label: '发起人自动审批（当前节点人员为发起人时，自动审批）',
    value: 'initAutoApprove'
  },
  {
    label: '重复人员自动审批（当前节点人员已在前置审批节点中进行过操作时，自动审批）',
    value: 'dupUserAutoApprove'
  },
  {
    label: '相邻节点重复人员自动审批（当前节点人员已在上一审批节点中进行过操作时，自动审批）',
    value: 'prevNodeDupUserAutoApprove'
  }
];

export default function AdvancedConfig({ setApprovalConfigData, advancedConfig }: AdvancedConfig) {
  const [timeModalShow, setTimeModalShow] = useState(false);
  const [advancedData, setAdvancedData] = useState<AdvancedConfigType>({
    autoApproveCfg: advancedConfig?.autoApproveCfg ?? {},
    emptyApproverCfg: advancedConfig?.emptyApproverCfg ?? {}
  });
  const handleCheckChange = (vals: any) => {
    const newAutoApproveCfg = {
      initAutoApprove: vals.includes('initAutoApprove'),
      dupUserAutoApprove: vals.includes('dupUserAutoApprove'),
      prevNodeDupUserAutoApprove: vals.includes('prevNodeDupUserAutoApprove')
    };
    setAdvancedData((prev: any) => ({ ...prev, autoApproveCfg: newAutoApproveCfg }));
  };
  const handleRadioChange = (val: any) => {
    setAdvancedData((prev: any) => ({
      ...prev,
      emptyApproverCfg: { handlerMode: val }
    }));
  };
  const checkedValues = useMemo(() => {
    return autoCheckArr
      .filter((item) => {
        return advancedData.autoApproveCfg?.[item.value] === true;
      })
      .map((item) => item.value);
  }, [advancedData.autoApproveCfg, autoCheckArr]);
  useEffect(() => {
    setApprovalConfigData('advancedConfig', advancedData);
  }, [advancedData]);
  return (
    <section className="heigher-config">
      <div className="title-box">自动审批</div>
      <CheckboxGroup onChange={handleCheckChange} value={checkedValues} direction="vertical" options={autoCheckArr} />
      <div className="title-box" style={{ padding: '32px 0 0' }}>
        审批人为空时
      </div>
      <RadioGroup
        onChange={handleRadioChange}
        value={advancedData?.emptyApproverCfg?.handlerMode}
        style={{ marginBottom: 20 }}
      >
        <Radio value="pause">流程暂停（即不允许为空）</Radio>
        <Radio value="skip">自动跳过节点</Radio>
        <Radio value="transfer_admin">转交给应用管理员</Radio>
        <Radio value="transfer_member">转交给指定成员</Radio>
      </RadioGroup>
      {timeModalShow && <TimeModal timeModalShow={timeModalShow} setTimeModalShow={setTimeModalShow} />}
    </section>
  );
}

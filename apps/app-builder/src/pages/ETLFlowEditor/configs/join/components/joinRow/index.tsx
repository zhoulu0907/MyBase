import { useEffect, useState } from 'react';
import { Button, Form, Grid, Popover, Select, type FormInstance } from '@arco-design/web-react';
import { useSignals } from '@preact/signals-react/runtime';
import { etlEditorSignal, ETLJoinType } from '@onebase/common';
import FullJoinIcon from '@/assets/images/etl/full_join.svg';
import LeftJoinIcon from '@/assets/images/etl/left_join.svg';
import RightJoinIcon from '@/assets/images/etl/right_join.svg';
import InnerJoinIcon from '@/assets/images/etl/inner_join.svg';
import JoinRowFields from '../joinRowFields';
import styles from '../../index.module.less';

const { Option } = Select;
const FormItem = Form.Item;

const JOINOPTIONS = [
  { key: ETLJoinType.FULL_JOIN, label: '全连接', img: FullJoinIcon },
  { key: ETLJoinType.LEFT_JOIN, label: '左连接', img: LeftJoinIcon },
  { key: ETLJoinType.RIGHT_JOIN, label: '右连接', img: RightJoinIcon },
  { key: ETLJoinType.INNER_JOIN, label: '内连接', img: InnerJoinIcon }
];

const NODETYPE = {
  LEFT: 'leftNodeId',
  RIGHT: 'rightNodeId'
};

interface JoinRowProps {
  finalNodeList: any[];
  form: FormInstance;
  payload: any;
  setPayload: (payload: any) => void;
}

const JoinRow = (props: JoinRowProps) => {
  useSignals();
  const { curNode, nodeData, graphData, setNodeData } = etlEditorSignal;
  const { finalNodeList, form, payload, setPayload } = props;

  // Join Type
  const [curSelectJoin, setCurSelectJoin] = useState(JOINOPTIONS[0].key);
  const [curSelectJoinObj, setCurSelectJoinObj] = useState<any>(JOINOPTIONS[0]);
  const [popupVisible, setPopupVisible] = useState(false);

  // FieldList
  const [leftFieldList, setLeftFieldList] = useState<any[]>([]);
  const [rightFieldList, setRightFieldList] = useState<any[]>([]);

  useEffect(() => {
    const joinType = payload?.config?.joinType || nodeData.value[curNode.value.id]?.config?.joinType;
    form.setFieldValue('joinType', joinType || JOINOPTIONS[0].key);
    setCurSelectJoin(joinType || JOINOPTIONS[0].key);
    setCurSelectJoinObj(JOINOPTIONS.find((option) => option.key === joinType) || JOINOPTIONS[0]);
  }, []);

  useEffect(() => {
    const nodeListDetail = nodeData.value;
    const curNodeConfig = payload?.config || nodeListDetail[curNode.value.id]?.config;
    if (finalNodeList.length > 0 && curNodeConfig) {
      const leftNodeId = curNodeConfig[NODETYPE.LEFT];
      const rightNodeId = curNodeConfig[NODETYPE.RIGHT];
      const leftFieldList = nodeListDetail[leftNodeId]?.output ? nodeListDetail[leftNodeId]?.output.fields : [];
      setLeftFieldList(leftFieldList);
      const rigthFieldList = nodeListDetail[rightNodeId]?.output ? nodeListDetail[rightNodeId]?.output.fields : [];
      setRightFieldList(rigthFieldList);
    }
  }, [finalNodeList]);

  const handleJoinChange = (option: any, fieldName: string) => {
    setCurSelectJoin(option.key);
    setCurSelectJoinObj(option);
    setPopupVisible(false);
    form.setFieldValue(fieldName, option.key);
    setCurNodeData();
  };

  const handleNodeChange = (nodeType: string, nodeId: string) => {
    const nodeListDetail = nodeData.value;
    const leftFieldList = nodeListDetail[nodeId]?.output ? nodeListDetail[nodeId]?.output.fields : [];
    const rigthFieldList = nodeListDetail[nodeId]?.output ? nodeListDetail[nodeId]?.output.fields : [];
    if (!nodeId) {
      const fieldPairs = form.getFieldValue('fieldPairs');
      if (fieldPairs && fieldPairs.length > 0) {
        setLeftFieldList(nodeType === NODETYPE.LEFT ? [] : leftFieldList);
        setRightFieldList(nodeType === NODETYPE.LEFT ? rigthFieldList : []);
        const finalFieldPairs = fieldPairs.map((field: any) => ({
          leftFieldFqn: nodeType === NODETYPE.LEFT ? undefined : field.leftFieldFqn,
          rightFieldFqn: nodeType === NODETYPE.LEFT ? field.rightFieldFqn : undefined
        }));
        form.setFieldValue('fieldPairs', finalFieldPairs);
      }
    } else {
      if (nodeType === NODETYPE.LEFT) {
        setLeftFieldList(leftFieldList);
      } else {
        setRightFieldList(rigthFieldList);
      }
    }
    setCurNodeData();
  };

  const setCurNodeData = () => {
    const formValue = form.getFieldsValue();
    payload.config = {
      ...payload.config,
      ...formValue
    };
    setPayload(payload);
  };

  return (
    <div>
      <Grid.Row className={styles.row}>
        <Grid.Col span={8}>
          <FormItem field={NODETYPE.LEFT} rules={[{ required: true }]} noStyle>
            <Select placeholder="请选择表单" allowClear onChange={(value) => handleNodeChange(NODETYPE.LEFT, value)}>
              {finalNodeList
                .filter((node) => node.sourceNodeID !== form.getFieldValue(NODETYPE.RIGHT))
                .map((node) => (
                  <Option key={node.sourceNodeID} value={node.sourceNodeID}>
                    {node.title}
                  </Option>
                ))}
            </Select>
          </FormItem>
        </Grid.Col>
        <Grid.Col span={2} className={styles.center}>
          <FormItem noStyle field="joinType">
            <Popover
              trigger="click"
              getPopupContainer={() => document.body}
              className={styles.switchPopover}
              popupVisible={popupVisible}
              content={
                <div className={styles.switchDiv}>
                  {JOINOPTIONS.map((option) => (
                    <div
                      className={`${styles.joinDiv} ${curSelectJoin === option.key ? styles.joinDivSelct : ''}`}
                      key={option.key}
                      onClick={() => handleJoinChange(option, 'joinType')}
                    >
                      <img src={option.img} alt={option.key} />
                      <span>{option.label}</span>
                    </div>
                  ))}
                </div>
              }
            >
              <div className={styles.switchWrap} onClick={() => setPopupVisible((v) => !v)}>
                <div className={styles.wrap} role="button">
                  <div className={styles.iconBox}>
                    <img src={curSelectJoinObj?.img} alt={curSelectJoinObj?.key} />
                  </div>
                  <div className={styles.label}>{curSelectJoinObj?.label}</div>
                </div>
              </div>
            </Popover>
          </FormItem>
        </Grid.Col>
        <Grid.Col span={8}>
          <FormItem noStyle field={NODETYPE.RIGHT}>
            <Select placeholder="请选择表单" allowClear onChange={(value) => handleNodeChange(NODETYPE.RIGHT, value)}>
              {finalNodeList
                .filter((node) => node.sourceNodeID !== form.getFieldValue(NODETYPE.LEFT))
                .map((node) => (
                  <Option key={node.sourceNodeID} value={node.sourceNodeID}>
                    {node.title}
                  </Option>
                ))}
            </Select>
          </FormItem>
        </Grid.Col>
      </Grid.Row>

      <Form.List field="fieldPairs">
        {(fields, { add, remove }) => {
          return (
            <div>
              {fields.map((item, index) => {
                return (
                  <JoinRowFields
                    key={index}
                    index={index}
                    rowField={item}
                    leftFieldList={leftFieldList}
                    rightFieldList={rightFieldList}
                    form={form}
                    payload={payload}
                    setPayload={setPayload}
                    remove={remove}
                  />
                );
              })}
              <Button
                type="text"
                onClick={() => {
                  add();
                }}
              >
                + 添加字段
              </Button>
            </div>
          );
        }}
      </Form.List>
    </div>
  );
};

export default JoinRow;

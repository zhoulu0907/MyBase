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
}

const JoinRow = (props: JoinRowProps) => {
  useSignals();
  const { curNode, nodeData, setNodeData } = etlEditorSignal;
  const { finalNodeList, form } = props;

  // Join Type
  const [curSelectJoin, setCurSelectJoin] = useState(JOINOPTIONS[0].key);
  const [curSelectJoinObj, setCurSelectJoinObj] = useState<any>(JOINOPTIONS[0]);
  const [popupVisible, setPopupVisible] = useState(false);

  // FieldList
  const [leftFieldList, setLeftFieldList] = useState<any[]>([]);
  const [rightFieldList, setRightFieldList] = useState<any[]>([]);

  useEffect(() => {
    form.setFieldValue('joinType', curSelectJoin);
  }, []);

  useEffect(() => {
    const nodeListDetail = nodeData.value;
    if (finalNodeList.length > 0) {
      const leftNodeId = form.getFieldValue(NODETYPE.LEFT);
      const rightNodeId = form.getFieldValue(NODETYPE.RIGHT);
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
    if (nodeType === NODETYPE.LEFT) {
      const leftFieldList = nodeListDetail[nodeId]?.output ? nodeListDetail[nodeId]?.output.fields : [];
      setLeftFieldList(leftFieldList);
    } else {
      const rigthFieldList = nodeListDetail[nodeId]?.output ? nodeListDetail[nodeId]?.output.fields : [];
      setRightFieldList(() => rigthFieldList);
    }
    setCurNodeData();
  };

  const setCurNodeData = () => {
    const formValue = form.getFieldsValue();
    const payload = nodeData.value[curNode.value.id];
    let fields = [];
    payload.config = {
      ...payload.config,
      ...formValue
    };
    if (formValue?.fieldPairs?.length > 0) {
      fields = generateOutputFields(formValue);
      payload.output = {
        verified: true,
        fields
      };
    } else {
      payload.output = {
        verified: false
      };
    }
    setNodeData(curNode.value.id, payload);
  };

  const generateOutputFields = (formValue: any) => {
    if (formValue.joinType === ETLJoinType.RIGHT_JOIN) {
      const rightFields = rightFieldList.map((field) => ({
        fqn: curNode.value.id + `.${field.fieldName}`,
        fieldName: field.fieldName,
        fieldType: field.fieldType
      }));

      const fieldPairsSet = new Set(formValue.fieldPairs.map((pair: any) => pair.leftFieldFqn));
      const leftFields = leftFieldList
        .filter((field: any) => !fieldPairsSet.has(field.fieldFqn))
        .map((item: any) => ({
          fqn: curNode.value.id + `.${item.fieldName}`,
          fieldName: item.fieldName,
          fieldType: item.fieldType
        }));

      return leftFields.concat(rightFields);
    } else {
      const leftFields = leftFieldList.map((field) => ({
        fqn: curNode.value.id + `.${field.fieldName}`,
        fieldName: field.fieldName,
        fieldType: field.fieldType
      }));

      const fieldPairsSet = new Set(formValue.fieldPairs.map((pair: any) => pair.rightFieldFqn));
      const rightFields = rightFieldList
        .filter((field: any) => !fieldPairsSet.has(field.fieldFqn))
        .map((item: any) => ({
          fqn: curNode.value.id + `.${item.fieldName}`,
          fieldName: item.fieldName,
          fieldType: item.fieldType
        }));

      return leftFields.concat(rightFields);
    }
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

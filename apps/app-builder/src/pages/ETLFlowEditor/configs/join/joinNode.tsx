import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Form, Grid, Layout } from '@arco-design/web-react';
import useForm from '@arco-design/web-react/es/Form/useForm';
import { useSignals } from '@preact/signals-react/runtime';
import { ETLDrawerTab, etlEditorSignal } from '@onebase/common';
import JoinRow from './components/joinRow';
import styles from './index.module.less';

export const JoinNodeConfig: React.FC = () => {
  useSignals();

  const { curNode, curDrawerTab, nodeData, graphData } = etlEditorSignal;
  const [form] = useForm();
  const [finalNodeList, setFinalNodeList] = useState<any[]>([]);

  useEffect(() => {
    const finalNodeList = generateNodeList(curNode.value.id);
    setFinalNodeList(finalNodeList);
  }, []);

  const generateNodeList = (targetNodeId: string) => {
    if (!graphData) return [];

    const { edges = [] } = graphData.value;
    const nodeDataList = nodeData.value;

    // 找出指向 targetNodeID 的所有 sourceNodeID，去重
    const sourceIds = Array.from(
      new Set(edges.filter((e: any) => e.targetNodeID === targetNodeId).map((e: any) => e.sourceNodeID))
    );

    // 从 nodeDataList 映射取 title
    return sourceIds.map((id) => {
      const node = nodeDataList[id];
      return { sourceNodeID: id, title: node.title };
    });
  };

  return (
    <Layout className={styles.joinDataConfigContainer}>
      {curDrawerTab.value === ETLDrawerTab.DATA_CONFIG && (
        <div>
          <div className={styles.joinHeader}>
            <span>添加连接字段</span>
            <Checkbox>合并连接字段</Checkbox>
          </div>

          <Form
            form={form}
            className={styles.content}
            initialValues={{ ...nodeData.value[curNode.value.id]?.config }}
            onValuesChange={(_, v) => {
              console.log(_, v);
            }}
          >
            <JoinRow finalNodeList={finalNodeList} form={form} />
            {/* <Form.List field="joinList">
              {(joinList, { add: addRow }) => {
                return (
                  <>
                    {joinList.map((item, index) => {
                      return <JoinRow key={index} row={item} finalNodeList={finalNodeList} form={form} />;
                    })}
                    <Grid.Row>
                  <Grid.Col span={18} className={styles.addConnectionBox}>
                    <Button
                      type="text"
                      onClick={() => {
                        addRow();
                      }}
                    >
                      + 添加连接
                    </Button>
                  </Grid.Col>
                </Grid.Row>
                  </>
                );
              }}
            </Form.List> */}
          </Form>
        </div>
      )}
    </Layout>
  );
};

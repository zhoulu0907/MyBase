import { Popover, Select } from '@arco-design/web-react';
import { flinkFunctionList, flinkFunctionTypeList, type FlinkFunction, type FlinkFunctionListReq } from '@onebase/app';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import React, { useEffect, useState } from 'react';
import styles from './index.module.less';

/**
 * SQL 节点的配置主界面
 * 初始化页面，渲染 SQLNodeConfig 组件
 */
const SQLConfig: React.FC = () => {
  useSignals();

  const { curNode, nodeData, graphData } = etlEditorSignal;

  const [funcList, setFuncList] = useState<FlinkFunction[]>([]);
  const [funcTypeList, setFuncTypeList] = useState<string[]>([]);

  const [selectedFuncType, setSelectedFuncType] = useState<string>('');

  useEffect(() => {
    handleGetFlinkFunctionTypeList();
  }, []);

  useEffect(() => {
    // console.log(funcTypeList, 'funcTypeList');
    handleGetFlinkFunctionList({
      type: selectedFuncType
    });
  }, [selectedFuncType]);

  const handleGetFlinkFunctionList = async (req: FlinkFunctionListReq) => {
    const res = await flinkFunctionList(req);
    console.log(res, 'res');
    setFuncList(res);
  };

  const handleGetFlinkFunctionTypeList = async () => {
    const res = await flinkFunctionTypeList();
    console.log(res, 'res');
    setFuncTypeList(res);
  };

  return (
    <div className={styles.dataConfig}>
      <div className={styles.content}>
        <div className={styles.functionListContainer}>
          <div className={styles.functionListTitle}>函数</div>
          <Select
            style={{ width: '100%', marginBottom: 16 }}
            placeholder="请选择函数类型"
            allowClear
            onChange={(value) => {
              setSelectedFuncType(value);
            }}
          >
            {funcTypeList.map((type) => (
              <Select.Option value={type} key={type}>
                {type}
              </Select.Option>
            ))}
          </Select>
          <div className={styles.functionList}>
            {funcList.map((func) => (
              <Popover key={func.functionName} content={func.functionDesc} trigger="hover" position="right">
                <div className={styles.functionItem}>{func.functionName}</div>
              </Popover>
            ))}
          </div>
        </div>
        <div className={styles.fieldList}></div>
        <div className={styles.sql}></div>
      </div>
    </div>
  );
};

export default SQLConfig;

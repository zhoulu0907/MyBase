import { placeholdersPlugin } from '@/components/FormulaEditor/utils/placeholders';
import { Popover, Select, Tree } from '@arco-design/web-react';
import {
  flinkFunctionList,
  flinkFunctionTypeList,
  type ELTColumn,
  type FlinkFunction,
  type FlinkFunctionListReq
} from '@onebase/app';
import { etlEditorSignal } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import CodeMirror, { EditorView } from '@uiw/react-codemirror';
import React, { useEffect, useState } from 'react';
import { getSourceNodeIdsByTarget } from '../utils';
import styles from './index.module.less';

/**
 * SQL 节点的配置主界面
 * 初始化页面，渲染 SQLNodeConfig 组件
 */
const SQLConfig: React.FC = () => {
  useSignals();

  const { curDrawerTab, curNode, nodeData, setNodeData, graphData } = etlEditorSignal;

  useEffect(() => {
    console.log('curDrawerTab: ', curDrawerTab.value);
  }, [curDrawerTab.value]);

  const [funcList, setFuncList] = useState<FlinkFunction[]>([]);
  const [funcTypeList, setFuncTypeList] = useState<string[]>([]);

  const [selectedFuncType, setSelectedFuncType] = useState<string>('');

  const [fieldList, setFieldList] = useState<any[]>([]);

  const [showSQLValue, setShowSQLValue] = useState<string>(
    nodeData.value[curNode.value.id]?.config?.showSQLValue || ''
  );
  const [sqlVariables, setSqlVariables] = useState<Record<string, string>>({});

  const [currentCursorPos, setCurrentCursorPos] = useState<number | null>(null);

  useEffect(() => {
    handleGetFlinkFunctionTypeList();
  }, []);

  useEffect(() => {
    handleGetFlinkFunctionList({
      type: selectedFuncType
    });
  }, [selectedFuncType]);

  useEffect(() => {
    let sourceNodeIds = getSourceNodeIdsByTarget(graphData.value, curNode.value.id);
    if (sourceNodeIds && sourceNodeIds.length > 0) {
      const tmpFieldList = [];
      const tmpSqlVariables: Record<string, string> = {};
      for (const sourceNodeId of sourceNodeIds) {
        const sourceNodeData = nodeData.value[sourceNodeId];
        tmpFieldList.push({
          title: sourceNodeData.title,
          id: sourceNodeId,
          fields: sourceNodeData.output?.fields || []
        });

        tmpSqlVariables[`${sourceNodeId}.${sourceNodeData.title}`] = sourceNodeId;

        for (const field of sourceNodeData.output?.fields || []) {
          tmpSqlVariables[`${sourceNodeId}#${field.fieldFqn}`] = field.fieldFqn;
        }
      }
      setFieldList(tmpFieldList);
      setSqlVariables(tmpSqlVariables);
    }
  }, [nodeData, curNode]);

  useEffect(() => {
    // setSqlValue(nodeData.value[curNode.value.id]?.config?.sql || '');

    let payload = nodeData.value[curNode.value.id];

    let sqlValue = showSQLValue;
    for (const [key, value] of Object.entries(sqlVariables)) {
      sqlValue = sqlValue.replaceAll(`[[${key}]]`, value);
    }

    payload.config = {
      sqlValue: sqlValue,
      sqlVariables: sqlVariables,
      showSQLValue: showSQLValue
    };

    payload.output = {
      verified: true,
      fields: []
    };

    console.log('save:  ', curNode.value.id, '-', sqlValue);
    setNodeData(curNode.value.id, payload);
  }, [showSQLValue, sqlVariables]);

  const handleGetFlinkFunctionList = async (req: FlinkFunctionListReq) => {
    const res = await flinkFunctionList(req);
    setFuncList(res);
  };

  const handleGetFlinkFunctionTypeList = async () => {
    const res = await flinkFunctionTypeList();
    setFuncTypeList(res);
  };

  const customExtensions = [
    EditorView.theme({
      '&.cm-editor.cm-focused': {
        outline: '0 solid transparent'
      }
    }),
    EditorView.updateListener.of((update) => {
      if (update.docChanged) {
        console.log('docChanged', update.state.doc.toString());
        //   handleChange(update.state.doc.toString());
      }

      // 监听焦点变化事件
      if (update.focusChanged) {
        const hasFocus = update.view.hasFocus;

        if (hasFocus) {
          console.log('编辑器获得焦点');
          // 如果提供了onFocus回调，则调用它
        } else {
          console.log('编辑器失去焦点');
          // 如果提供了onBlur回调，则调用它
        }
      }

      // 监听光标位置变化和光标是否存在
      if (update.selectionSet) {
        const selection = update.state.selection.main;

        // 检查是否存在有效光标位置
        if (selection && typeof selection.head === 'number') {
          const cursor = selection.head;
          const line = update.state.doc.lineAt(cursor);
          const position = {
            line: line.number,
            column: cursor - line.from + 1
          };

          // 判断是否有文本被选中（与光标位置相区分）
          const hasSelection = !selection.empty;

          // 更新当前光标位置
          setCurrentCursorPos(cursor);

          // 打印光标信息
          if (hasSelection) {
            console.log('有文本被选中，光标位置:', position);
          } else {
            console.log('光标位置变化:', position);
          }

          // 只有当onCursorChange存在时才调用它
          // if (onCursorChange) {
          //   onCursorChange(position);
          // }
        } else {
          // 没有有效光标时，重置光标位置
          setCurrentCursorPos(null);
          console.log('没有光标存在');
        }
      }
    }),

    EditorView.lineWrapping,
    placeholdersPlugin()
  ];

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

        <div className={styles.fieldListContainer}>
          <Tree
            defaultExpandedKeys={fieldList.length > 0 ? [fieldList[0].title] : []}
            defaultSelectedKeys={[]}
            onSelect={(value, info) => {
              const parentId = info.node?.props?.parentKey;
              let parentTitle = '';
              let displayName = '';
              if (parentId) {
                parentTitle = fieldList.find((item) => item.id === parentId)?.title || '';

                displayName = `${parentTitle}.${info.node?.props?.title}`;
              } else {
                displayName = `${info.node?.props?.title}`;
              }

              const variableId = value[0].replace('.', '#');

              const variable = `[[${variableId}.${displayName}]]`;

              console.log('variable: ', variable);
              setShowSQLValue(showSQLValue + variable);
            }}
            onExpand={(keys, info) => {
              console.log('展开节点:', keys, info);
            }}
            style={{ width: '100%' }}
          >
            {fieldList.map((item) => (
              <Tree.Node title={item.title} key={item.id}>
                {item.fields.map((field: ELTColumn) => (
                  <Tree.Node title={field.fieldName} key={field.fieldFqn} isLeaf />
                ))}
              </Tree.Node>
            ))}
          </Tree>
        </div>
        <div className={styles.sqlContainer}>
          <CodeMirror
            height="500px"
            className={styles.editor}
            extensions={customExtensions}
            value={showSQLValue}
            onChange={(value) => setShowSQLValue(value)}
          ></CodeMirror>
        </div>
      </div>
    </div>
  );
};

export default SQLConfig;

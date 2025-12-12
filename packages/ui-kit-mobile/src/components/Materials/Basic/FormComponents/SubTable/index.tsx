import { PreviewRender } from '@/components/render';
import { Cell, Collapse, Form } from '@arco-design/mobile-react';
import { IconAdd, IconDelete } from '@arco-design/mobile-react/esm/icon';
import { ITypeRules, ValidatorType } from '@arco-design/mobile-utils';
import { pagesRuntimeSignal } from '@onebase/common';
import {
  FormSchema,
  STATUS_OPTIONS,
  STATUS_VALUES,
  useEditorSignalMap,
  useFormEditorSignal,
  usePageViewEditorSignal
} from '@onebase/ui-kit';
import { useSignals } from '@preact/signals-react/runtime';
import { memo, useEffect, useState } from 'react';
import styles from './index.module.css';

type XSubTableConfig = typeof FormSchema.XSubTableSchema.config;

const XSubTable = memo(
  (props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any }) => {
    const { id, label, status, verify, layout, defaultOptionsConfig, runtime = true, detailMode, form } = props;

    useSignals();
    const { curViewId } = usePageViewEditorSignal;

    const subTableComponents = useEditorSignalMap.get(curViewId.value)?.subTableComponents.value[props.id] || [];

    const { subTableDataLength } = pagesRuntimeSignal;
    const [subTableData, setSubTableData] = useState<any[]>([]);

    useEffect(() => {
      if (!subTableData || subTableData.length === 0) {
        let newSubTableData: any[] = [];
        for (let i = 0; i < subTableDataLength.value[id]; i++) {
          newSubTableData.push({ key: i });
        }
        setSubTableData(newSubTableData);
      }
    }, [subTableDataLength.value]);

    /* 新增数据 */
    const handleAdd = () => {
      const newData = { key: subTableData[subTableData.length - 1]?.key + 1 || 0 };
      setSubTableData((prevData) => [...prevData, newData]);
    };

    /* 删除数据 */
    const handleDelete = (e: any, index: number) => {
      e.stopPropagation();

      const formData = form?.getFieldsValue();

      if (formData) {
        const filtered = Object.fromEntries(Object.entries(formData).filter(([key]) => !key.includes(`.${index}.`)));
        form?.setFieldsValue(filtered);
      }
      setSubTableData((prev) => prev.filter((v) => v.key !== index));
    };

    const rules: ITypeRules<ValidatorType.Custom>[] = [
      {
        type: ValidatorType.Custom,
        validator: (value, callback) => {
          if (!value && verify?.required) {
            callback(`${label.text}是必填项`);
          }
        }
      }
    ];

    return (
      <Form.Item
        className="inputTextWrapperOBMobile"
        field=""
        rules={rules}
        layout="vertical"
        label={label.display ? label.text : undefined}
        style={{
          pointerEvents: !runtime || detailMode ? 'none' : 'unset',
          opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.4 : 1
        }}
      >
        <>
          {subTableData.map((item, index) => (
            <Collapse
              className={styles.collapseOBMobile}
              key={item.key}
              header={
                <div className={styles.collapseHeader}>
                  #{index + 1}
                  <IconDelete onClick={(e) => handleDelete(e, item.key)} />
                </div>
              }
              value={item.key + ''}
              defaultActive
              content={
                <Cell.Group>
                  {subTableComponents.map((subTable: any) => {
                    const schema = useFormEditorSignal.pageComponentSchemas.value[subTable.id];

                    const config = {
                      ...schema.config,
                      dataField: [`${id}.${item.key}.${schema.config?.dataField?.[1] || subTable.id}`]
                    };
                    const pageSchema = { ...schema, config };

                    return (
                      <Cell label={config.cpName} key={subTable.id} style={{ padding: 0 }}>
                        <PreviewRender
                          cpId={subTable.id}
                          cpType={subTable.type}
                          detailMode={detailMode}
                          pageComponentSchema={pageSchema}
                          runtime={true}
                        // showFromPageData={showFromPageData}
                        />
                      </Cell>
                    );
                  })}
                </Cell.Group>
              }
            />
          ))}
          <div
            className={styles.onAddOBMobile}
            onClick={handleAdd}
            style={{ pointerEvents: runtime ? 'unset' : 'none' }}>
            <IconAdd style={{ marginRight: '0.16rem' }} />
            新增一项
          </div>
        </>
      </Form.Item>
    );
  }
);

export default XSubTable;

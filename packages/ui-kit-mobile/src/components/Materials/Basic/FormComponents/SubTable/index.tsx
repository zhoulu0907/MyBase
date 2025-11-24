
import { memo, useEffect, useState } from 'react';
import { Cell, Collapse } from '@arco-design/mobile-react';
import { IconAdd, IconDelete } from '@arco-design/mobile-react/esm/icon';
import { pagesRuntimeSignal } from '@onebase/common';
import { useEditorSignalMap, usePageViewEditorSignal, useFormEditorSignal } from '@/signals';
import { PreviewRender } from '@/components/render';
import type { XSubTableConfig } from './schema';
import styles from './index.module.css';

const XSubTable = memo((props: XSubTableConfig & { runtime?: boolean; detailMode?: boolean; defaultOptionsConfig?: any; form?: any }) => {
  const {
    id,
    label,
    tooltip,
    status,
    verify,
    layout,
    defaultOptionsConfig,
    runtime = true,
    detailMode,
    form
  } = props;

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

      const formData = form.getFieldsValue();

      const filtered = Object.fromEntries(
        Object.entries(formData).filter(([key]) => !key.includes(`.${index}.`))
      );

      console.log('filtered', filtered, index);

      setSubTableData((prev) => prev.filter((v) => v.key !== index));
      form.setFieldsValue(filtered);
  };

  console.log('subTableData', subTableData)

  return (
    <Cell
      label={'子表'}
      append={
        <>
          {
            subTableData.map((item, index) => (
              <Collapse
                className={styles.collapse}
                key={item.key}
                header={
                  <div className={styles.collapseHeader}>
                    #{index + 1}
                    <IconDelete onClick={(e) => handleDelete(e, item.key)} />
                  </div>
                }
                value={index + ''}
                defaultActive
                content={
                  <Cell.Group>
                    {subTableComponents.map(subTable => {
                      const schema = useFormEditorSignal.pageComponentSchemas.value[subTable.id];

                      const config = {
                        ...schema.config,
                        dataField: [`${id}.${index}.${schema.config?.dataField?.[1] || subTable.id}`]
                      };
                      const pageSchema = { ...schema, config };

                      return <Cell label={config.cpName} key={subTable.id} style={{ padding: 0 }}>
                        <PreviewRender
                          cpId={subTable.id}
                          cpType={subTable.type}
                          detailMode={detailMode}
                          pageComponentSchema={pageSchema}
                          runtime={true}
                        // showFromPageData={showFromPageData}
                        />
                      </Cell>
                    })}
                  </Cell.Group>
                }
              />
            ))}
          <div className={styles.onAdd} onClick={handleAdd}><IconAdd />新增一项</div>
        </>
      }>
    </Cell>
  );
});

export default XSubTable;
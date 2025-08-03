import { STATUS_OPTIONS, STATUS_VALUES } from "@/components/Materials/constants";
import { Button, Form, Input, Table } from "@arco-design/web-react";
import { IconDelete, IconDown, IconEdit } from "@arco-design/web-react/icon";
import { memo, useEffect, useState } from "react";

import styles from './index.module.less';
import type { XTableConfig } from "./schema";

const opearate: any = {
    title: "操作",
    dataIndex: "op",
    fixed: null,
    width: 100,
    render: () => (
        <>
            <Button
                type="text"
                style={{ marginRight: 5 }}
                icon={<IconEdit />}
            />
            <Button status="danger" type="text" icon={<IconDelete />} />
        </>
    ),
};
const XTable = memo((props: XTableConfig) => {
    const {
        label,
        status,
        defaultValue,
        searchItems,
        columns,
        hover,
        border,
        borderCell,
        showHeader,
        stripe,
        pagePosition,
        pageSize,
        showTotal,
        showOpearate,
        fixedOpearate,
    } = props;

    const [finalColumns, setFinalColumns] = useState<any[]>();

    useEffect(() => {
        if (Object.keys(columns as any).length) {
            columns?.map((v) => {
              return {
                ...v,
                ellipsis: true,
                width: v.width + 'px',
              }
            });
        }
        if (showOpearate) {
            opearate.fixed = fixedOpearate ? "right" : null;
            setFinalColumns([...columns as any, opearate]);
        } else {
            setFinalColumns((pre) => pre?.filter((v) => v.dataIndex !== "op"));
        }
    }, [showOpearate, columns, fixedOpearate]);

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <>
        <div className={styles.tableHeader}>
            <div className={styles.searchGroup}>
                {searchItems?.map((item, idx) => (
                    <Form.Item
                        key={idx}
                        className={styles.searchItem}
                        label={
                            <div style={{
                                width: '50px',
                                textAlign: 'left',
                                whiteSpace: 'nowrap',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis'
                            }}>
                                {`${item.label}`}
                            </div>
                        }
                        style={{
                            width: '200px',
                            marginBottom: 0,
                        }}
                        layout={"horizontal"}
                    >
                        <Input placeholder={`请输入${item.label}`} />
                    </Form.Item>
                ))}

            </div>

            <div className={styles.tableHeaderButton}>
                <Button type="primary">查询</Button>
                <Button type="primary">重置</Button>
                <Button type="outline"
                    style={{
                        border: 'none',
                    }}
                >
                    <IconDown />
                    <span>展开</span>
                </Button>

            </div>
        </div>
        <div>
            <Form.Item
                label={label}
                layout={"vertical"}
                style={{
                    width: "100%",
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                }}
            >
                <Table
                    scroll={{
                        x: "max-content",
                    }}
                    border={border}
                    borderCell={borderCell}
                    showHeader={showHeader}
                    stripe={stripe}
                    hover={hover}
                    columns={finalColumns}
                    data={defaultValue}
                    pagePosition={pagePosition}
                    pagination={{
                        pageSize,
                        showTotal,
                    }}
                />
            </Form.Item>
        </div>
        </>
    );
});

export default XTable;

import { memo, useEffect, useState } from "react";
import { Table, Button, Form } from "@arco-design/web-react";
import { IconEdit, IconDelete } from "@arco-design/web-react/icon";
import { STATUS_VALUES, STATUS_OPTIONS } from "@/components/Materials/constants";

import type { XTableConfig } from "./schema";

const opearate: any = {
    title: "操作",
    dataIndex: "op",
    fixed: null,
    width: 110,
    render: (_, record) => (
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
        <Form.Item
            label={label}
            layout={"vertical"}
            style={{
                width: "980px",
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
    );
});

export default XTable;

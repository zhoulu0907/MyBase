import { Table } from "@arco-design/web-react";
import { memo } from "react";
import type { XTableConfig } from "./schema";

const XTable = memo((props: XTableConfig) => {
    const { label, status, defaultValue, columns, hover, border, borderCell, showHeader, stripe, pagePosition } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Table
                scroll={{
                    x: 800,
                }}
                border={border}
                borderCell={borderCell}
                showHeader={showHeader}
                stripe={stripe}
                hover={hover}
                columns={columns}
                data={defaultValue}
                pagePosition={pagePosition}
            />
        </div>
    );
});

export default XTable;

import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Tooltip, TreeSelect } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputUserSelectConfig } from "./schema";

// TODO(Mickey): 放到schema的config中
// 示例树形结构：人员
const treeData = [
    {
        key: "node1",
        title: "Trunk",
        children: [
            {
                key: "node2",
                title: "Leaf",
            },
        ],
    },
    {
        key: "node3",
        title: "Trunk2",
        children: [
            {
                key: "node4",
                title: "Leaf",
            },
            {
                key: "node5",
                title: "Leaf",
            },
        ],
    },
];

const XUserSelect = memo((props: XInputUserSelectConfig) => {
    const {
        label,
        tooltip,
        status,
        required,
        layout,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                    margin: '0px',
                }}
            >
                <TreeSelect
                    placeholder="Select"
                    style={{ width: "100%" }}
                    allowClear
                    treeData={treeData}
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XUserSelect;

import { memo } from "react";
import { TreeSelect, Tooltip, Form } from "@arco-design/web-react";
import type { XInputDeptSelectConfig } from "./schema";
import {
    STATUS_VALUES,
    STATUS_OPTIONS,
} from "@/components/Materials/constants";

// TODO(Mickey): 放到schema的config中
// 示例树形结构：部门
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

const XDeptSelect = memo((props: XInputDeptSelectConfig) => {
    const {
        label,
        tooltip,
        status,
        defaultValue,
        required,
        layout,
        saveWithHidden,
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
                }}
            >
                <TreeSelect
                    placeholder="Select"
                    style={{ width: "100%" }}
                    allowClear
                    treeData={treeData}
                ></TreeSelect>
            </Form.Item>
        </Tooltip>
    );
});

export default XDeptSelect;

import { TreeSelect } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputUserSelectConfig } from "./schema";

// TODO(Mickey): 放到schema的config中
// 示例树形结构：人员
const treeData = [
    {
      key: 'node1',
      title: 'Trunk',
      children: [
        {
          key: 'node2',
          title: 'Leaf',
        },
      ],
    },
    {
      key: 'node3',
      title: 'Trunk2',
      children: [
        {
          key: 'node4',
          title: 'Leaf',
        },
        {
          key: 'node5',
          title: 'Leaf',
        },
      ],
    },
  ];

const XUserSelect = memo((props: XInputUserSelectConfig) => {
    const {
        label,
        status,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <TreeSelect
                placeholder="Select"
                style={{ width: "100%" }}
                allowClear
                treeData={treeData}
            >

            </TreeSelect>
        </div>
    );
});

export default XUserSelect;

import { Input, Tooltip } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputReadonlyBoxConfig } from "./schema";

const XReadonlyBox = memo((props: XInputReadonlyBoxConfig) => {
  const { label, tooltip, status } = props;
  return status === "hidden" ? null : (
    <Tooltip content={tooltip}>
      <div>
        <div>{label}</div>
        <Input
          readOnly={true}
          defaultValue={"只读展示框"}
          style={{ width: "100%" }}
        />
      </div>
    </Tooltip>
  );
});

export default XReadonlyBox;

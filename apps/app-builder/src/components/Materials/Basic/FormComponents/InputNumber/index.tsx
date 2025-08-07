import {
  STATUS_OPTIONS,
  STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, InputNumber } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputNumberConfig } from "./schema";

const XInputNumber = memo((props: XInputNumberConfig) => {
  const {
    label,
    placeholder,
    tooltip,
    status,
    defaultValue,
    required,
    align,
    min,
    max,
    step,
    precision,
    layout,
    labelColSpan = 0,
  } = props;

  return (
    <Form.Item
      label={label}
      layout={layout}
      labelCol={{
        span: labelColSpan,
      }}
      tooltip={tooltip}
      wrapperCol={{ span: 24 - labelColSpan }}
      rules={[
        {
          required,
          type: "number",
          min,
          max,
        },
      ]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents:
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? "none" : "unset",
        margin: "0px",
      }}
    >
      <InputNumber
        readOnly={status === STATUS_VALUES[STATUS_OPTIONS.READONLY]}
        defaultValue={defaultValue}
        placeholder={placeholder}
        step={step}
        min={min}
        max={max}
        precision={precision}
        style={{
          width: "100%",
          textAlignLast: align,
        }}
      />
    </Form.Item>
  );
});

export default XInputNumber;

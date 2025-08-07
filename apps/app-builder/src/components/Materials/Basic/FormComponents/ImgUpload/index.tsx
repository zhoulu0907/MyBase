import {
  STATUS_OPTIONS,
  STATUS_VALUES,
} from "@/components/Materials/constants";
import { Form, Message, Upload } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputImgUploadConfig } from "./schema";

const XImgUpload = memo((props: XInputImgUploadConfig) => {
  const {
    label,
    status,
    tooltip,
    uploadSize = 10,
    uploadLimit,
    listType,
    required,
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
      rules={[{ required }]}
      style={{
        opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
        pointerEvents:
          status === STATUS_VALUES[STATUS_OPTIONS.READONLY] ? "none" : "unset",
        margin: "0px",
      }}
    >
      <Upload
        imagePreview
        limit={uploadLimit === -1 ? undefined : Number(uploadLimit)}
        accept="image/*"
        listType={listType}
        action="/"
        beforeUpload={async (file) => {
          const fileSizeLimit = uploadSize * 1024; // 转换为kb;
          const fileSize = file.size / 1024;

          if (fileSize > fileSizeLimit) {
            Message.warning("文件大小超出限制");
            return false;
          }
        }}
        showUploadList
        style={{
          width: "100%",
        }}
      />
    </Form.Item>
  );
});

export default XImgUpload;

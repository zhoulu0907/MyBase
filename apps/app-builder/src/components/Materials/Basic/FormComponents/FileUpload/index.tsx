import { memo } from "react";
import { Form, Upload, Message, Tooltip } from "@arco-design/web-react";
import type { XInputFileUploadConfig } from "./schema";
import { STATUS_VALUES, STATUS_OPTIONS } from "@/components/Materials/constants";

const XFileUpload = memo((props: XInputFileUploadConfig) => {
    const {
        label,
        status,
        tooltip,
        uploadSize = 10,
        uploadLimit,
        // showPreview, // todo
        // showDownload, // todo
        listType,
        required,
        layout,
    } = props;

    return (
        <Tooltip content={tooltip}>
            <Form.Item
                label={label}
                layout={layout}
                rules={[{ required }]}
                style={{
                    opacity: status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? 0.5 : 1,
                    pointerEvents:
                        status === STATUS_VALUES[STATUS_OPTIONS.READONLY]
                            ? "none"
                            : "unset",
                }}
            >
                <Upload
                    limit={uploadLimit === -1 ? undefined : Number(uploadLimit)}
                    accept=".doc, .docx, .xls, .pdf, .xlsx"
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
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XFileUpload;

import { STATUS_OPTIONS, STATUS_VALUES } from "@/components/Materials/constants";
import { Form, Message, Tooltip, Upload } from "@arco-design/web-react";
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
                />
            </Form.Item>
        </Tooltip>
    );
});

export default XImgUpload;

import { Upload } from "@arco-design/web-react";
import { memo } from "react";
import type { XInputImgUploadConfig } from "./schema";

const XImgUpload = memo((props: XInputImgUploadConfig) => {
    const {
        label,
        status,
    } = props;

    return status === "hidden" ? null : (
        <div>
            <div>{label}</div>
            <Upload
                drag
                imagePreview
                listType='picture-card'
                multiple
                accept="image/*"
                action="/"
                onDrop={() => {}}
            />
        </div>
    );
});

export default XImgUpload;

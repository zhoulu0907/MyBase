import { UploadCommonComponent } from "./upload";
import { useRef } from "react";
import { FooterButton } from "./footerButton";
import { Space } from "@arco-design/web-react";

interface IUploadImageProps {
    aspect?: number;
    onUpdateUrl: (value: string) => void;
    getUploadFile: Function;
}

const UploadImageComponent:React.FC<IUploadImageProps> = ({aspect = 2 /1 , getUploadFile, onUpdateUrl}) => {
    const uploadRef = useRef(null);
    return (
        <Space direction="vertical" style={{ margin: 0 }}>
            <UploadCommonComponent 
              aspect={aspect} 
              imagePreview={true}
              onUpdateUrl={onUpdateUrl} 
              uploadRef={uploadRef} 
              getUploadFile={getUploadFile}
            />
            <FooterButton uploadRef={uploadRef}/>
        </Space>
    )
}

export { UploadImageComponent };
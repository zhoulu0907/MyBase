import { UploadCommonComponent } from "./upload";
import { useRef } from "react";
import { FooterButton } from "./footerButton";

interface IUploadImageProps {
    aspect?: number;
    onUpdateUrl: (value: string) => void;
    getUploadFile: Function;
}

const UploadImageComponent:React.FC<IUploadImageProps> = ({aspect = 2 /1 , getUploadFile, onUpdateUrl}) => {
    const uploadRef = useRef(null);
    return (
        <>
            <UploadCommonComponent 
              aspect={aspect} 
              onUpdateUrl={onUpdateUrl} 
              uploadRef={uploadRef} 
              getUploadFile={getUploadFile}
            />
            <FooterButton uploadRef={uploadRef}/>
        </>
    )
}

export { UploadImageComponent };
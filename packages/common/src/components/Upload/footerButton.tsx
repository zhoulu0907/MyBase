import { Button, Space } from "@arco-design/web-react";
import { IconUpload } from "@arco-design/web-react/icon";

interface IFooterButtonProps {
    uploadRef: any;
}

export const FooterButton:React.FC<IFooterButtonProps> = ({ uploadRef }) => {
    return (
       <Space>
            <Button
            type="outline"
            icon={<IconUpload />}
            onClick={() => {
                (uploadRef as any).current?.getRootDOMNode()?.querySelector('input[type="file"]').click();
            }}
            >
            上传图片
            </Button>
            <div style={{ color: '#999', marginTop: 4 }}>建议比例 2:1</div>
        </Space>
    )
}
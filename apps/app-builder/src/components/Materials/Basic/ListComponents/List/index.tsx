import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { List } from "@arco-design/web-react";
import { memo } from "react";
import { type XListConfig } from "./schema";


const XList = memo((props: XListConfig) => {
    const {
        status,
    } = props;



    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (

        <List
            style={{ width: '100%' }}
            size='small'
            header='List title'
            dataSource={[
                'Beijing Bytedance Technology Co., Ltd.',
                'Bytedance Technology Co., Ltd.',
                'Beijing Toutiao Technology Co., Ltd.',
                'Beijing Volcengine Technology Co., Ltd.',
                'China Beijing Bytedance Technology Co., Ltd.',
            ]}
            render={(item, index) => <List.Item key={index}>{item}</List.Item>}
        />

    );
});

export default XList;

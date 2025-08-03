import {
    STATUS_OPTIONS,
    STATUS_VALUES,
} from "@/components/Materials/constants";
import { Card } from "@arco-design/web-react";
import { memo } from "react";
import { type XInfoNoticeConfig } from "./schema";


const XInfoNotice = memo((props: XInfoNoticeConfig) => {
    const {
        status,
        content,
    } = props;

    return status === STATUS_VALUES[STATUS_OPTIONS.HIDDEN] ? null : (

        <Card style={{ width: '100%' }}>
            <h1>{content}</h1>
        </Card>

    );
});

export default XInfoNotice;

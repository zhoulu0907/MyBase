import { Button, Input, Select, Typography } from "@arco-design/web-react";
import styles from "./index.module.less";
import { IconPlus } from "@arco-design/web-react/icon";
import { statusOptions } from "../../constants";

interface topHeaderProps {
    title: string;
    type?: string,
    onAdd?: () => void;
    setSearchInputValue: (value: string)=>void;
    isBusiness?:boolean;
    className?: string;
}

export const TopHeader:React.FC<topHeaderProps> = ({type, title, className, onAdd, isBusiness = true, setSearchInputValue}) => {
    return (
      <div className={`${styles.topHeader} ${className || ''}`}>
        {/*顶部左侧 新建企业*/}
        <div className={styles.createBusiness}>
            {type !== "authorized-application" && <Button type="primary" icon={<IconPlus />} onClick={onAdd}>{title}</Button>}
            {isBusiness && <div className={styles.linkContent}>
                <span>企业用户登录地址:</span>
                <Typography.Paragraph copyable className={styles.link}>www.onebase.com/enterprise</Typography.Paragraph>
            </div>}
        </div>
        {/* 顶部右侧 搜索*/}
        <div className={styles.searchContent}>
            {isBusiness && 
                <Select
                    bordered={false}
                    defaultValue="all"
                    options={statusOptions}
                />}
            <Input.Search 
                allowClear 
                placeholder={`输入${title}名称`} 
                className={styles.searchInput} 
                onChange={(value) => setSearchInputValue(value)}
            />
        </div>
      </div>
    )
}
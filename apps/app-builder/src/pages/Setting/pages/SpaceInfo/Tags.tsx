
import { Avatar, Popover, Tag, Grid } from '@arco-design/web-react';
import styles from './index.module.less'

const { Row, Col } = Grid;

interface IProps {
  data: {
    adminUserId: string;
    adminNickName: string;
    adminUserName: string;
    adminMobile?: string;
    adminEmail?: string;
    adminDept?: string;
  }[];
}
const Tags = ({ data }: IProps) => {
  return (
    <div className={styles.tagWrapper}>
      {data.map((tag, index) => <Tag className={styles.adminTag} key={index} size='large' style={{ borderRadius: 16 }}>
        <Popover
          title={
            <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
              <Avatar size={40} style={{ marginRight: 4 }}>{tag.adminNickName?.slice(0, 1)}</Avatar>
              <div>{tag.adminNickName || '-'}</div>
            </div>
          }
          content={
            <div style={{ width: 264 }}>
              <Row gutter={24} style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <span className={styles.infoKey}>手机号</span>
                </Col>
                <Col span={12}>
                  <span>{tag.adminMobile || '-'}</span>
                </Col>
              </Row>
              <Row gutter={24} style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <span className={styles.infoKey}>邮箱</span>
                </Col>
                <Col span={12}>
                  <span>{tag.adminEmail || '-'}</span>
                </Col>
              </Row>
              <Row gutter={24}>
                <Col span={12}>
                  <span className={styles.infoKey}>所属部门</span>
                </Col>
                <Col span={12}>
                  <span>{tag.adminDept || '-'}</span>
                </Col>
              </Row>
            </div>
          }
        >
          <Avatar size={24} style={{ marginRight: 4 }}>{tag.adminNickName?.slice(0, 1)}</Avatar>{tag.adminNickName || '-'}
        </Popover>
      </Tag>)}
    </div>
  )
}

export default Tags;
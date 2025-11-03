
// import {} from 'react';
import { Avatar, Popover, Tag, Grid } from '@arco-design/web-react';
import styles from './index.module.less'

const { Row, Col } = Grid;

interface IProps {
  data: string[];
}
const Tags = ({ data }: IProps) => {
  return (
    <div className={styles.tagWrapper}>
      {data.map((tag, index) => <Tag className={styles.adminTag} key={index} size='large' style={{ borderRadius: 16 }}>
        <Popover
          title={
            <div style={{ display: 'flex', alignItems: 'center' }}>
              <Avatar size={40} style={{ marginRight: 4 }}>{index}</Avatar>
              <div>{tag}</div>
            </div>
          }
          content={
            <div style={{ width: 264 }}>
              <Row gutter={24} style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <span className={styles.infoKey}>手机号</span>
                </Col>
                <Col span={12}>
                  <span>188 6666 9999</span>
                </Col>
              </Row>
              <Row gutter={24} style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <span className={styles.infoKey}>邮箱</span>
                </Col>
                <Col span={12}>
                  <span>xxxx@xxxx.com</span>
                </Col>
              </Row>
              <Row gutter={24} style={{ marginBottom: 16 }}>
                <Col span={12}>
                  <span className={styles.infoKey}>所属部门</span>
                </Col>
                <Col span={12}>
                  <span>这是一个部门名称</span>
                </Col>
              </Row>
            </div>
          }
        >
          <Avatar size={24} style={{ marginRight: 4 }}>{index}</Avatar>{tag}
        </Popover>


      </Tag>)}
    </div>
  )
}

export default Tags;
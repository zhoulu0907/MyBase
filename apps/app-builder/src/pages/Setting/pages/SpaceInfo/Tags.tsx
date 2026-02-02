import { Avatar, Popover, Tag, Grid } from '@arco-design/web-react';
import { getFileUrlById } from '@onebase/platform-center';
import styles from './index.module.less';

const { Row, Col } = Grid;

interface TagItem {
  adminUserId: string;
  adminNickName: string;
  adminUserName: string;
  adminMobile?: string;
  adminEmail?: string;
  adminDept?: string;
  adminAvatar?: string;
  deptName?: string;
}
interface IProps {
  data: TagItem[];
}
const Tags = ({ data }: IProps) => {
  const renderTagAdminAvatar = (tag: TagItem, size: number) => {
    return (
      <div style={{display:'flex',alignItems:'center'}}>
        {tag.adminAvatar ? (
          <Avatar size={size} style={{ marginRight: 4 }}>
            <img src={getFileUrlById(tag.adminAvatar)} alt="avatar" />
          </Avatar>
        ) : (
          <Avatar size={size} style={{ marginRight: 4, backgroundColor: 'rgb(var(--primary-6))' }}>
            {tag.adminNickName?.slice(0, 1)}
          </Avatar>
        )}
        {tag.adminNickName || '-'}
      </div>
    );
  };

  return (
    <div className={styles.tagWrapper}>
      {data.map((tag, index) => (
        <Tag className={styles.adminTag} key={index} size="large" style={{ borderRadius: 16 }}>
          <Popover
            title={
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: 16 }}>
                {renderTagAdminAvatar(tag, 40)} 
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
                    <span>{tag.deptName || '-'}</span>
                  </Col>
                </Row>
              </div>
            }
          >
            {renderTagAdminAvatar(tag, 24)}
          </Popover>
        </Tag>
      ))}
    </div>
  );
};

export default Tags;

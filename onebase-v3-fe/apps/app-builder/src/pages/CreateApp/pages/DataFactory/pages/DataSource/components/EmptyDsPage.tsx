import { Link } from '@arco-design/web-react';
import styles from '../index.module.less';
const EmptyDsPage: React.FC<{ handlePageType: (tab: string) => void }> = ({ handlePageType }) => {
  const gotoCreateDs = () => {
    handlePageType('create-ds');
  };

  return (
    <div className={styles.emptyDsPage}>
      请点击
      <Link hoverable={false} onClick={gotoCreateDs}>
        创建数据源
      </Link>
    </div>
  );
};

export default EmptyDsPage;

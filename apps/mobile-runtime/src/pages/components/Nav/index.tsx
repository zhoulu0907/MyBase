import React from 'react';
import { useNavigate } from 'react-router-dom';
import { NavBar } from '@arco-design/mobile-react';
import styles from './index.module.less';

interface CustomNav {
  title: string;
  fixed?: boolean;
  hasBottomLine?: boolean;
  className?: string;
  style?: React.CSSProperties;
  onClickRight?: () => void;
}

const CustomNav: React.FC<CustomNav> = (props) => {
  const navigate = useNavigate();
  const { className = '', title, fixed = false, hasBottomLine = false, style, onClickRight } = props;

  const back = () => navigate(-1);

  return (
    <div className={styles.navWrapper}>
      <NavBar
        className={`${className} ${styles.nav}`}
        fixed={fixed}
        title={title}
        hasBottomLine={hasBottomLine}
        onClickLeft={back}
        onClickRight={onClickRight}
        style={style}
      />
    </div>
  );
};

export default CustomNav;

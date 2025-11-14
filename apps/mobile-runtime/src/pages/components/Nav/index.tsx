import React from 'react';
import { useNavigate } from 'react-router-dom';
import { NavBar } from '@arco-design/mobile-react';
import styles from './index.module.less';
import { IconArrowBack } from '@arco-design/mobile-react/esm/icon';

interface CustomNav {
  title: string;
  fixed?: boolean;
  hasBottomLine?: boolean;
  className?: string;
  hideBackIcon?: boolean;
  style?: React.CSSProperties;
  onClickRight?: () => void;
}

const CustomNav: React.FC<CustomNav> = (props) => {
  const navigate = useNavigate();
  const { className = '', title, fixed = true, hideBackIcon = false, hasBottomLine = false, style, onClickRight } = props;

  const back = () => navigate(-1);

  return (
    <div className={styles.navWrapper}>
      <NavBar
        className={`${className} ${styles.nav}`}
        fixed={fixed}
        title={title}
        leftContent={hideBackIcon ? null : <IconArrowBack />}
        hasBottomLine={hasBottomLine}
        onClickLeft={back}
        onClickRight={onClickRight}
        style={style}
      />
    </div>
  );
};

export default CustomNav;

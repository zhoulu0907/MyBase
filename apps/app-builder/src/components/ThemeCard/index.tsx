import { useState } from 'react';
import { Space } from '@arco-design/web-react';
import checkSVG from '@/assets/images/appBasic/mode_check_icon.svg';
import { appThemeColor } from '@/components/CreateApp/const';
import { defaultTheme } from '@/pages/Home/pages/EnterpriseApp/const';
import styles from './index.module.less';

interface IProps {
  themeColor: string;
  cardGap: number;
  editable?: boolean;
  onChange?: (color: string) => void;
}

const ThememCard = (props: IProps) => {
  const { themeColor, cardGap, editable = true, onChange } = props;

  const [innerColor, setInnerColor] = useState(defaultTheme);
  const currentColor = themeColor ?? innerColor;

  const handleClick = (color: string) => {
    if (!editable) return;
    setInnerColor(color);
    onChange?.(color);
  };
  return (
    <Space>
      {appThemeColor.map((color) => (
        <div
          key={color}
          className={styles.themeCard}
          style={{
            background: color,
            marginRight: `${cardGap}px`,
            cursor: editable ? 'pointer' : 'default'
          }}
          onClick={() => handleClick(color)}
        >
          {currentColor === color && <img src={checkSVG} alt="Select Theme Color" />}
        </div>
      ))}
    </Space>
  );
};

export default ThememCard;

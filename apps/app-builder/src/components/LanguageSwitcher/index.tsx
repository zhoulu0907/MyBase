import { Button, Dropdown, Menu } from '@arco-design/web-react';
import { IconLanguage } from '@arco-design/web-react/icon';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { SUPPORTED_LANGUAGES } from '../../utils/i18n';

const LanguageSwitcher: React.FC = () => {
  const { i18n } = useTranslation();

  const languages = Object.entries(SUPPORTED_LANGUAGES).map(([key, value]) => ({
    key,
    label: value.name,
    flag: value.flag
  }));

  const currentLanguage = languages.find((lang) => lang.key === i18n.language) || languages[0];

  const handleLanguageChange = (key: string) => {
    i18n.changeLanguage(key);
  };

  const menu = (
    <Menu>
      {languages.map((lang) => (
        <Menu.Item
          key={lang.key}
          onClick={() => handleLanguageChange(lang.key)}
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '8px'
          }}
        >
          <span>{lang.flag}</span>
          <span>{lang.label}</span>
        </Menu.Item>
      ))}
    </Menu>
  );

  return (
    <Dropdown droplist={menu} position="bottom">
      <Button
        type="text"
        icon={<IconLanguage />}
        style={{
          display: 'flex',
          alignItems: 'center',
          gap: '4px'
        }}
      >
        <span>{currentLanguage.flag}</span>
        <span>{currentLanguage.label}</span>
      </Button>
    </Dropdown>
  );
};

export default LanguageSwitcher;

import { useTranslation } from 'react-i18next';

export const useI18n = () => {
  const { t, i18n } = useTranslation();

  const changeLanguage = (language: string) => {
    i18n.changeLanguage(language);
  };

  const getCurrentLanguage = () => {
    return i18n.language;
  };

  const isLanguage = (language: string) => {
    return i18n.language === language;
  };

  return {
    t,
    changeLanguage,
    getCurrentLanguage,
    isLanguage,
    language: i18n.language,
    languages: i18n.languages
  };
};

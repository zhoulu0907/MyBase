interface IRegisterProps {
  appId: string;
  tenantId: string;
  mobile: string;
  onGoBack: () => void;
}

const RegisterForm: React.FC<IRegisterProps> = ({ appId, tenantId, mobile, onGoBack }) => {
  return <>RegisterForm</>;
};

export default RegisterForm;

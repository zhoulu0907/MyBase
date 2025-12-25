
interface IConfirmInfoProps {
  tenantId: string;
  appId: string;
  onGoBack: () => void;
}

const ConfirmInfoForm: React.FC<IConfirmInfoProps> =({ appId, tenantId, onGoBack})=>{
    return <>ConfirmInfoForm</>
}

export default ConfirmInfoForm;

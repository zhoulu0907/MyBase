import { Navigate, useParams } from 'react-router-dom';

const RedirectEnterprise = () => {
  // 从路由中提取 enterpriseName 参数
  const { enterpriseName, tenantId } = useParams();
  // 拼接正确的重定向路径：enterpriseName/基本信息
  return <Navigate to={`/onebase/${tenantId}/setting/enterprise/${enterpriseName}/基本信息`} replace />;
};

export default RedirectEnterprise;

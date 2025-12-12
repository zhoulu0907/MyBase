import { useI18n } from '@/hooks/useI18n';
import { formatTimestamp } from '@/utils/date';
import {
  Card,
  Descriptions,
  Message,
  Space,
  Spin,
  Table,
  Tag,
  Typography,
  Upload,
  type TableColumnProps
} from '@arco-design/web-react';
import {
  getPlatFormInfoListApi,
  getPlatformInfoApi,
  uploadPlatformLicenseApi,
  type LicenseInfo,
  type LicenseInfoList
} from '@onebase/platform-center';
import React, { useEffect, useState } from 'react';
import LicenseDetailModal from './components/LicenseDetailModal/index.tsx';
import styles from './index.module.less';

const { Title, Text } = Typography;

const PlatformInfo: React.FC = () => {
  const { t } = useI18n();
  const [licenseDetailVisible, setLicenseDetailVisible] = useState(false);
  const [licenseInfoList, setLicenseInfoList] = useState<LicenseInfoList[]>([]);
  const [licenseInfo, setLicenseInfo] = useState<LicenseInfo | null>(null);
  const [selectedLicenseInfo, setSelectedLicenseInfo] = useState<LicenseInfo | null>(null);
  const [pageLoading, setPageLoading] = useState<boolean>(true);
  const [isUploading, setIsUploading] = useState(false);
  const [loading, setLoading] = useState(false);

  const getPlatformInfo = async () => {
    const res = await getPlatformInfoApi();
    if (res.id) {
      setLicenseInfo(res);
    }
  };

  const getPlatformInfoList = async (pageNo: number = 1, pageSize: number = 10) => {
    try {
      const res = await getPlatFormInfoListApi({ pageNo, pageSize });
      if (res && Array.isArray(res.list)) {
        setLicenseInfoList(res.list);
        setPagination((prevPagination) => ({
          ...prevPagination,
          current: pageNo,
          total: res.total
        }));
      } else {
        console.warn('Invalid response format:', res);
        setLicenseInfoList([]);
        setLicenseInfo(null);
      }
    } catch (error: any) {
      Message.error(error.message || t('auth.loginFailed'));
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      setPageLoading(true);
      try {
        (await getPlatformInfoList(pagination.current, pagination.pageSize), await getPlatformInfo());
      } catch (error) {
        console.error('上传跳转报错', error);
      } finally {
        setPageLoading(false);
      }
    };
    fetchData();
  }, []);

  // 认证记录table结构
  const columns: TableColumnProps[] = [
    {
      title: '公司名称',
      dataIndex: 'enterpriseName',
      key: 'enterpriseName'
    },
    {
      title: '认证内容',
      // 认证内容
      dataIndex: 'certificationContent',
      key: 'certificationContent',
      render: (_text, record) =>  (<div>
        空间数量：{record.tenantLimit}，用户数量：{record.userLimit}
      </div>)
    },
    {
      title: '当前状态',
      dataIndex: 'status',
      key: 'status',
      render: (text) => <Tag color={text === 'enable' ? 'green' : 'red'}>{text === 'enable' ? '已启用' : '已失效'}</Tag>
    },
    {
      title: '到期时间',
      dataIndex: 'expireTime',
      key: 'expireTime',
      render: (text) => <div>{formatTimestamp(text)}</div>
    },
    {
      title: '操作',
      dataIndex: 'operation',
      key: 'operation',
      render: (_, record) => (
        <div className={styles.operation}>
          <a
            href="#"
            className={styles.btn}
            onClick={(e) => {
              e.preventDefault();
              setSelectedLicenseInfo(record);
              setLicenseDetailVisible(true);
            }}
          >
            {t('platformInfo.check')}
          </a>
        </div>
      )
    }
  ];

  // 处理文件上传变化
  const handleFileUploadChange = async (fileList: any[]) => {
    // 防止重复上传
    if (isUploading) {
      return;
    }

    // 只处理最新上传的文件，不累积处理
    if (fileList.length > 0) {
      const latestFile = fileList[fileList.length - 1];

      // 获取原始文件对象 - 关键修正
      let file = null;

      // 尝试多种方式获取文件对象
      if (latestFile.originFile) {
        file = latestFile.originFile;
      } else if (latestFile.file) {
        file = latestFile.file;
      } else if (latestFile instanceof File) {
        file = latestFile;
      } else if (latestFile.raw) {
        file = latestFile.raw;
      }

      // 验证文件对象
      if (file && (file instanceof File || file instanceof Blob)) {
        const formData = new FormData();
        if (file instanceof File) {
          formData.append('file', file, file.name || 'license.lic.sm4');
        } else {
          // 对于 Blob 类型，使用固定文件名
          formData.append('file', file, 'license.lic.sm4');
        }

        setIsUploading(true);
        setLoading(true);

        try {
          // 调用上传接口
          await uploadPlatformLicenseApi(formData);
          Message.success(t('platformInfo.uploadSuccess'));

          // 重新获取列表数据
          await Promise.all([getPlatformInfoList(), getPlatformInfo()]);
        } catch (error: any) {
          Message.error('上传license失败');
          console.error('上传license失败', error);
        } finally {
          setIsUploading(false);
          setLoading(false);
        }
      } else {
        // 如果文件对象无效，显示错误信息
        Message.error('文件上传失败,请检查文件是否正确选择');
        console.error('无效的文件对象:', file);
        console.error('latestFile的完整结构:', JSON.stringify(latestFile, null, 2));
      }
    } else {
      console.log('没有选择文件');
    }
  };

  // 分页器
  const [pagination, setPagination] = useState({
    showTotal: true,
    total: 0,
    pageSize: 10,
    current: 1,
    pageSizeChangeResetCurrent: true
  });

  const onChangeTable = async (paginationInfo: { current?: number; pageSize?: number }) => {
    setLoading(true);
    try {
      const pageNo = paginationInfo.current || 1;
      const pageSize = paginationInfo.pageSize || 10;

      // 如果页面大小改变，重置到第一页
      const actualPageNo = pageSize !== pagination.pageSize ? 1 : pageNo;

      await getPlatformInfoList(actualPageNo, pageSize);

      setPagination((prev) => ({
        ...prev,
        current: actualPageNo,
        pageSize: pageSize
      }));
    } catch (error) {
      console.error('分页加载失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.platformInfo}>
      <Spin loading={pageLoading} style={{ width: '100%' }}>
        <Space direction="vertical" size="large" style={{ width: '100%' }}>
          {/* 页面标题 */}
          <div className={styles.pageHeader}>
            <div className={styles.pageHeaderLeft}>
              <Title heading={4} className={styles.pageHeaderTitle}>
                {licenseInfo?.enterpriseName || '公司名称'}
              </Title>
              <div className={styles.companyId}>
                <Text type="secondary">{licenseInfo?.enterpriseCode || '公司编码'}</Text>
              </div>
              <div className="address">
                <Text type="secondary">{licenseInfo?.enterpriseAddress || '公司地址'}</Text>
              </div>
            </div>
            <div className={styles.pageHeaderRight}>
              <div className={styles.superAdmin}>
                <Text type="secondary">
                  {t('platformInfo.superAdmin')}：
                  <span className={styles.superAdminText}>{licenseInfo?.adminUser || '--'}</span>
                </Text>
              </div>
              <div className={styles.createdAt}>
                <Text type="secondary">
                  {t('platformInfo.createdAt')}：{formatTimestamp(licenseInfo?.createTime) || '--'}
                </Text>
              </div>
            </div>
          </div>
          {/* 平台基本信息 */}
          <Card title={t('platformInfo.basicInfo')} className={styles.infoCard}>
            <Descriptions
              column={2}
              colon=":"
              data={[
                {
                  label: t('platformInfo.platformType'),
                  value: licenseInfo?.platformType || '--'
                },
                {
                  label: t('platformInfo.authStatus'),
                  value: (
                    <span className={styles.statusRunning}>
                      {licenseInfo?.status ? t('platformInfo.enable') : t('platformInfo.disable')}
                    </span>
                  )
                },
                {
                  label: t('platformInfo.expireTime'),
                  value: formatTimestamp(licenseInfo?.expireTime)
                },
                {
                  label: t('platformInfo.version'),
                  value: 'v1.0.0'
                },
                {
                  label: t('platformInfo.tenantCount'),
                  value: (
                    <Space>
                      <span>{licenseInfo?.actualTenantCount || '--'}</span>
                      <span> / </span>
                      <span>{licenseInfo?.userLimit || '--'}</span>
                    </Space>
                  )
                }
              ]}
            />
          </Card>
          {/* 认证记录 */}
          <div className={styles.authRecord}>
            <Text>{t('platformInfo.authRecord')}</Text>
            <span>
              <Upload
                className={styles.uploadAuth}
                showUploadList={false}
                multiple={false}
                headers={{
                  authorization: 'authorization-text'
                }}
                onChange={handleFileUploadChange}
              >
                <div className={styles.uploadAuthText}>{t('platformInfo.uploadAuth')}</div>
              </Upload>
            </span>
          </div>
          <Table
            loading={loading}
            columns={columns}
            data={licenseInfoList}
            pagination={{
              ...pagination,
              // showPageSize: true,
              // sizeCanChange: true,
              className: styles.tablePagination
            }}
            onChange={onChangeTable}
            rowKey={(record) => record.id}
            border={false}
          />
        </Space>
      </Spin>

      <LicenseDetailModal
        visible={licenseDetailVisible}
        selectedLicenseInfo={selectedLicenseInfo}
        cancelDetailModal={() => setLicenseDetailVisible(false)}
      />
    </div>
  );
};

export default PlatformInfo;

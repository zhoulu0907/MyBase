// import React, { useEffect, useState } from 'react';
import { Link } from "@arco-design/web-react";
import styles from "../index.module.less";
const EmptyEntityPage: React.FC<{ handlePageType: (tab: string) => void }> = ({
  handlePageType,
}) => {
  return (
    <div className={styles["empty-entity-page"]}>
      暂无业务实体，请点击
      <Link hoverable={false} onClick={() => handlePageType("check-entity")}>
        创建业务实体
      </Link>
    </div>
  );
};

export default EmptyEntityPage;

// 数据源配置类型
export interface ListApplicationReq {
    pageNo: number;
    pageSize: number;
    name: string;
    ownerTag?: string;
    orderByTime?: 'create' | 'update';
    status?: number;
}

export interface CreateApplicationReq {
    /**
     * 应用编码
     */
    appCode: string;
    /**
     * 应用模式
     */
    appMode?: string;
    /**
     * 应用名称
     */
    appName: string;
    /**
     * 数据源ID
     */
    datasourceId: number;
    /**
     * 应用描述
     */
    description?: string;
    /**
     * 图标颜色
     */
    iconColor: string;
    /**
     * 图标类型
     */
    iconName: string;
    /**
     * 标签ID
     */
    tagIds?: number[];
    /**
     * 主题色
     */
    themeColor?: string;
}


export interface UpdateApplicationReq {
    /**
     * 应用ID
     */
    id: number;
    /**
     * 应用模式
     */
    appMode?: string;
    /**
     * 应用名称
     */
    appName: string;
    /**
     * 数据源ID
     */
    datasourceId: number;
    /**
     * 应用描述
     */
    description?: string;
    /**
     * 图标颜色
     */
    iconColor: string;
    /**
     * 图标类型
     */
    iconName: string;
    /**
     * 标签ID
     */
    tagIds?: number[];
    /**
     * 主题色
     */
    themeColor?: string;
}

export interface UpdateApplicationNameReq {
    /**
     * 应用ID
     */
    id: number;
    /**
     * 应用名称
     */
    name: string;
}

export interface DeleteApplicationReq {
    /**
         * 应用ID
         */
    id: number;
    /**
     * 应用名称
     */
    name: string;
}
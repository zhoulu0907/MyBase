export const approvalConfigVar:any = {
  approvalMode: {
    'counter_sign': '会签',
    'any_sign': '或签',
    'c_sign': '依次审批',
    'd_sign': '投票'
  }
};

export const nodeStatusVar:any = {
  // ================== 阶段1: 节点环节前 (pre) ==================
  /**
   * 待审批
   */
  "pre_approval": "待审批",

  /**
   * 待执行
   */
  "pre_exec": "待执行",

  /**
   * 自动抄送
   */
  "pre_auto_cc": "自动抄送",

  // ================== 阶段2: 节点环节中 (curr) ==================
  /**
   * 待提交
   */
  "curr_pending_submit": "待提交",

  /**
   * 审批中（蓝色字体）
   */
  "curr_in_approval": "审批中",

  /**
   * 执行中
   */
  "curr_in_exec": "执行中",

  // ================== 阶段3: 节点环节后 (post) ==================
  /**
   * 已提交（绿色字体）
   */
  "post_submitted": "已提交",

  /**
   * 已同意（绿色字体）
   */
  "post_approved": "已同意",

  /**
   * 已拒绝（红色字体）
   */
  "post_rejected": "已拒绝",

  /**
   * 已转交
   */
  "post_transferred": "已转交",

  /**
   * 已加签
   */
  "post_add_signer": "已加签",

  /**
   * 已退回（红色字体）
   */
  "post_returned": "已退回",

  /**
   * 已弃权
   */
  "post_abstained": "已弃权",

  /**
   * 已撤回（红色字体）
   */
  "post_withdrawn": "已撤回",

  /**
   * 自动通过（绿色字体）
   */
  "post_auto_approved": "自动通过",

  /**
   * 自动拒绝（红色字体）
   */
  "post_auto_rejected": "自动拒绝",

  /**
   * 自动转交
   */
  "post_auto_transferred": "自动转交",

  /**
   * 自动跳过
   */
  "post_auto_skipped": "自动跳过",

  /**
   * 自动抄送
   */
  "post_auto_cc": "自动抄送",
}

export function displayStatusMap(_status: string) {
    const _label = nodeStatusVar[_status]
    const _map: any = {
      label: '', iconClass: 'succss-box', labelColor: 'gray-label'
    }
    if (_label) {
      _map.label = _label
      switch (_status) {
        // 自动拒绝（红色字体）
        case 'post_auto_rejected':
        // 已拒绝（红色字体）
        case 'post_rejected': {
          _map.iconClass = 'refuse-box' // refuse-box样式下，label默认是红色，不用额外设置
          _map.labelColor = 'red-label'
          break;
        }
        // 已退回（红色字体）
        case 'post_returned':
        // 已撤回（红色字体）
        case 'post_withdrawn': {
          _map.iconClass = 'back-box' // back-box样式下，label默认是红色，不用额外设置
          _map.labelColor = 'red-label'
          break;
        }
        // 已提交（绿色字体）
        case 'post_submitted':
        // 已同意（绿色字体）
        case 'post_approved':
        // 自动通过（绿色字体）
        case 'post_auto_approved': {
          _map.iconClass = 'succss-box' // back-box样式下，label默认是绿色，不用额外设置
          _map.labelColor = 'green-label'
          break;
        }
        // 审批中（蓝色字体）
        case 'curr_in_approval': {
          // _map.iconClass = ''
          _map.labelColor = 'blue-label'
          break;
        }
        default: {
          // 默认是:通过succss-gray-box，label灰色
          break;
        }
      }
    } else {
      // 如果_status不在对象中，比如_status是中文
      _map.label = _status
    }
    return _map
}
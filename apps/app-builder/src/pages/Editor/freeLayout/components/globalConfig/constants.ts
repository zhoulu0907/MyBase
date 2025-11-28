// 添加枚举定义
export enum HandlerMode {
  PAUSE = 'pause',
  SKIP = 'skip',
  TRANSFER_ADMIN = 'transfer_admin',
  TRANSFER_MEMBER = 'transfer_member'
}

export enum Permission {
  NONE = 'none',
  INITIATION_NODE = 'initiation_node',
  ANY = 'any'
}

export enum Timing {
  UNPROCESSED = 'unprocessed',
  UNREAD = 'unread'
}

export enum Rule {
  SEQ = 'seq',
  DIRECT = 'direct'
}

export enum AutoApproveType {
  INIT_AUTO_APPROVE = 'initAutoApprove',
  DUP_USER_AUTO_APPROVE = 'dupUserAutoApprove',
  PREV_NODE_DUP_USER_AUTO_APPROVE = 'prevNodeDupUserAutoApprove'
}

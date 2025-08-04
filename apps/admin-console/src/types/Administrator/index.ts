export interface Admin {
  id: number;
  account: string;
  email: string;
  type: string;
  createTime: string;
}

export interface PlatformAdminProps {
  admins: Admin[];
  loading: boolean;
  onCreate: (admin: Admin) => void;
  onUpdate: (id: number, admin: Partial<Admin>) => void;
  onDelete: (id: number) => void;
}
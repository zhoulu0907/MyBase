import { LoginRequest, LoginResponse } from "../types";
import platformClient from "./clients/platform";

export const platformLoginApi = async (req: LoginRequest): Promise<LoginResponse> => {
  return platformClient.post<LoginResponse>('/auth/login', req, {
    headers: {
      'Tenant-Id': '1'
    }
  });
};
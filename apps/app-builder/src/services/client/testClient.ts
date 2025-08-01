import { HttpClient } from '@onebase/common';

// TODO for test
export const httpClient = new HttpClient({
  baseURL: 'http://192.168.115.89:48080', 
  timeout: 10000,
  prefix: '/admin-api',
  headers: {
    'Authorization': 'Bearer' + '39bdecb4325b4cd6a27947d2872cab7b'
  }
});
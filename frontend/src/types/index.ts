export interface User {
  id: number;
  username: string;
  email: string;
  role: 'VIEWER' | 'ANALYST' | 'ADMIN';
  createdAt: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role?: 'VIEWER' | 'ANALYST' | 'ADMIN';
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface RecordRequest {
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  category: string;
  date: string;
  notes?: string;
}

export interface RecordResponse {
  id: number;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  category: string;
  date: string;
  notes: string | null;
  createdAt: string;
}

export interface DashboardSummary {
  totalIncome: number;
  totalExpenses: number;
  netBalance: number;
  recordCount: number;
  periodStart: string;
  periodEnd: string;
}

export interface CategoryTotal {
  category: string;
  type: 'INCOME' | 'EXPENSE';
  total: number;
  count: number;
}

export interface TrendData {
  period: string;
  totalIncome: number;
  totalExpenses: number;
  net: number;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ErrorResponse {
  success: boolean;
  message: string;
  status: number;
  error: string;
  fieldErrors?: { field: string; message: string }[];
}

export interface UserUpdateRequest {
  email?: string;
  role?: 'VIEWER' | 'ANALYST' | 'ADMIN';
}

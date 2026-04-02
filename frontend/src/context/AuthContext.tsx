'use client';

import { createContext, useCallback, useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { User, LoginRequest, RegisterRequest, LoginResponse, ApiResponse } from '@/types';
import api from '@/lib/api';
import { getToken, setToken, removeToken, getStoredUser, setStoredUser, isTokenExpired } from '@/lib/auth';

interface AuthContextType {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  isLoading: true,
  isAuthenticated: false,
  login: async () => {},
  register: async () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const token = getToken();
    const storedUser = getStoredUser();
    if (token && storedUser && !isTokenExpired(token)) {
      setUser(storedUser);
    } else {
      removeToken();
    }
    setIsLoading(false);
  }, []);

  const login = useCallback(async (data: LoginRequest) => {
    const response = await api.post<ApiResponse<LoginResponse>>('/api/auth/login', data);
    const { token, user: userData } = response.data.data;
    setToken(token);
    setStoredUser(userData);
    setUser(userData);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    await api.post<ApiResponse<User>>('/api/auth/register', data);
  }, []);

  const logout = useCallback(() => {
    removeToken();
    setUser(null);
    router.push('/login');
  }, [router]);

  return (
    <AuthContext.Provider value={{ user, isLoading, isAuthenticated: !!user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

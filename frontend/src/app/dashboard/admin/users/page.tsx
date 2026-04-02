'use client';

import { useEffect, useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { User, PagedResponse, ApiResponse } from '@/types';
import UserTable from '@/components/admin/UserTable';
import Pagination from '@/components/ui/Pagination';
import toast from 'react-hot-toast';

export default function AdminUsersPage() {
  const { user } = useAuth();
  const router = useRouter();
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState('');

  useEffect(() => {
    if (user && user.role !== 'ADMIN') {
      toast.error('Access denied');
      router.replace('/dashboard');
    }
  }, [user, router]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({ page: page.toString(), size: '10' });
      if (search) params.append('search', search);
      const response = await api.get<ApiResponse<PagedResponse<User>>>(`/api/admin/users?${params}`);
      setUsers(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch {
      toast.error('Failed to load users');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { if (user?.role === 'ADMIN') fetchUsers(); }, [page, search, user]);

  const handleRoleChange = async (id: number, role: string) => {
    try {
      await api.put(`/api/admin/users/${id}`, { role });
      toast.success('User role updated');
      fetchUsers();
    } catch {
      toast.error('Failed to update role');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this user?')) return;
    try {
      await api.delete(`/api/admin/users/${id}`);
      toast.success('User deleted');
      fetchUsers();
    } catch {
      toast.error('Failed to delete user');
    }
  };

  if (user?.role !== 'ADMIN') return null;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">User Management</h1>
        <p className="text-gray-600">Manage users and their roles</p>
      </div>

      <div className="flex items-center gap-4">
        <input
          type="text"
          placeholder="Search users..."
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
        />
      </div>

      <UserTable users={users} loading={loading} onRoleChange={handleRoleChange} onDelete={handleDelete} />

      {totalPages > 1 && (
        <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}

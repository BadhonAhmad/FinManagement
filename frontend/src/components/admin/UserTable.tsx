'use client';

import { User } from '@/types';
import { formatDate } from '@/utils/formatters';
import LoadingSpinner from '@/components/ui/LoadingSpinner';

interface UserTableProps {
  users: User[];
  loading: boolean;
  onRoleChange: (id: number, role: string) => void;
  onDelete: (id: number) => void;
}

const roleColors: Record<string, string> = {
  ADMIN: 'bg-green-100 text-green-800',
  ANALYST: 'bg-blue-100 text-blue-800',
  VIEWER: 'bg-gray-100 text-gray-800',
};

export default function UserTable({ users, loading, onRoleChange, onDelete }: UserTableProps) {
  if (loading) return <LoadingSpinner message="Loading users..." />;

  if (users.length === 0) {
    return <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">No users found.</div>;
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Username</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Role</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Created</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {users.map((user) => (
              <tr key={user.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm font-medium text-gray-900">{user.username}</td>
                <td className="px-6 py-4 text-sm text-gray-500">{user.email}</td>
                <td className="px-6 py-4">
                  <select
                    value={user.role}
                    onChange={(e) => onRoleChange(user.id, e.target.value)}
                    className={`text-xs font-semibold rounded-full px-2 py-1 border-0 cursor-pointer ${roleColors[user.role]}`}
                  >
                    <option value="VIEWER">VIEWER</option>
                    <option value="ANALYST">ANALYST</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </td>
                <td className="px-6 py-4 text-sm text-gray-500">{formatDate(user.createdAt)}</td>
                <td className="px-6 py-4 text-right">
                  <button
                    onClick={() => onDelete(user.id)}
                    className="text-red-600 hover:text-red-800 text-sm font-medium"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

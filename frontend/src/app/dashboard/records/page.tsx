'use client';

import { useRecords } from '@/hooks/useRecords';
import { useAuth } from '@/hooks/useAuth';
import RecordFilters from '@/components/records/RecordFilters';
import RecordTable from '@/components/records/RecordTable';
import Pagination from '@/components/ui/Pagination';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function RecordsPage() {
  const { records, loading, page, setPage, totalPages, filters, setFilters, deleteRecord } = useRecords();
  const { user } = useAuth();
  const router = useRouter();
  const canEdit = user?.role === 'ADMIN';

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this record?')) return;
    try {
      await deleteRecord(id);
      toast.success('Record deleted');
    } catch {
      toast.error('Failed to delete record');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Financial Records</h1>
          <p className="text-gray-600">Manage your income and expenses</p>
        </div>
        {canEdit && (
          <Link
            href="/dashboard/records/new"
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 font-medium"
          >
            Add Record
          </Link>
        )}
      </div>

      <RecordFilters filters={filters} setFilters={(f) => { setFilters(f); setPage(0); }} />

      <RecordTable
        records={records}
        loading={loading}
        onDelete={handleDelete}
        onEdit={(id) => router.push(`/dashboard/records/${id}/edit`)}
        canEdit={canEdit}
      />

      {totalPages > 1 && (
        <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      )}
    </div>
  );
}

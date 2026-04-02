'use client';

import { RecordResponse } from '@/types';
import { formatCurrency, formatDate } from '@/utils/formatters';
import LoadingSpinner from '@/components/ui/LoadingSpinner';

interface RecordTableProps {
  records: RecordResponse[];
  loading: boolean;
  onDelete: (id: number) => void;
  onEdit: (id: number) => void;
  canEdit?: boolean;
}

export default function RecordTable({ records, loading, onDelete, onEdit, canEdit = false }: RecordTableProps) {
  if (loading) return <LoadingSpinner message="Loading records..." />;

  if (records.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-8 text-center text-gray-500">
        No records found. Create your first record to get started.
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Category</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Amount</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Notes</th>
              {canEdit && <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">Actions</th>}
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {records.map((record) => (
              <tr key={record.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 text-sm text-gray-900">{formatDate(record.date)}</td>
                <td className="px-6 py-4">
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                    record.type === 'INCOME'
                      ? 'bg-green-100 text-green-800'
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {record.type}
                  </span>
                </td>
                <td className="px-6 py-4 text-sm text-gray-900">{record.category}</td>
                <td className={`px-6 py-4 text-sm text-right font-medium ${
                  record.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                }`}>
                  {record.type === 'INCOME' ? '+' : '-'}{formatCurrency(record.amount)}
                </td>
                <td className="px-6 py-4 text-sm text-gray-500 max-w-xs truncate">{record.notes || '-'}</td>
                {canEdit && (
                  <td className="px-6 py-4 text-right space-x-2">
                    <button
                      onClick={() => onEdit(record.id)}
                      className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                    >
                      Edit
                    </button>
                    <button
                      onClick={() => onDelete(record.id)}
                      className="text-red-600 hover:text-red-800 text-sm font-medium"
                    >
                      Delete
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

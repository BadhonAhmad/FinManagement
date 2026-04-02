import { RecordResponse } from '@/types';
import { formatCurrency, formatDate } from '@/utils/formatters';

interface RecentActivityProps {
  records: RecordResponse[];
  loading: boolean;
}

export default function RecentActivity({ records, loading }: RecentActivityProps) {
  const displayRecords = records.slice(0, 5);

  return (
    <div className="rounded-lg border border-gray-200 bg-white shadow-sm">
      <div className="border-b border-gray-200 px-5 py-4">
        <h3 className="text-base font-semibold text-gray-900">Recent Activity</h3>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-gray-100 bg-gray-50">
              <th className="px-5 py-3 font-medium text-gray-500">Date</th>
              <th className="px-5 py-3 font-medium text-gray-500">Type</th>
              <th className="px-5 py-3 font-medium text-gray-500">Category</th>
              <th className="px-5 py-3 text-right font-medium text-gray-500">Amount</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              Array.from({ length: 5 }).map((_, i) => (
                <tr key={i} className="border-b border-gray-50">
                  <td className="px-5 py-3">
                    <div className="h-4 w-20 animate-pulse rounded bg-gray-200" />
                  </td>
                  <td className="px-5 py-3">
                    <div className="h-5 w-16 animate-pulse rounded-full bg-gray-200" />
                  </td>
                  <td className="px-5 py-3">
                    <div className="h-4 w-24 animate-pulse rounded bg-gray-200" />
                  </td>
                  <td className="px-5 py-3 text-right">
                    <div className="ml-auto h-4 w-20 animate-pulse rounded bg-gray-200" />
                  </td>
                </tr>
              ))
            ) : displayRecords.length === 0 ? (
              <tr>
                <td colSpan={4} className="px-5 py-8 text-center text-gray-400">
                  No recent transactions
                </td>
              </tr>
            ) : (
              displayRecords.map((record) => (
                <tr key={record.id} className="border-b border-gray-50 last:border-0 hover:bg-gray-50">
                  <td className="px-5 py-3 text-gray-600">
                    {formatDate(record.date)}
                  </td>
                  <td className="px-5 py-3">
                    <span
                      className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${
                        record.type === 'INCOME'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-red-100 text-red-700'
                      }`}
                    >
                      {record.type}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-gray-700">
                    {record.category}
                  </td>
                  <td
                    className={`px-5 py-3 text-right font-medium ${
                      record.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
                    }`}
                  >
                    {record.type === 'INCOME' ? '+' : '-'}
                    {formatCurrency(record.amount)}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

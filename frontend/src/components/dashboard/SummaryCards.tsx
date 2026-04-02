import { DashboardSummary } from '@/types';
import { formatCurrency } from '@/utils/formatters';

interface SummaryCardsProps {
  summary: DashboardSummary | null;
  loading: boolean;
}

const cards = [
  {
    key: 'totalIncome' as const,
    title: 'Total Income',
    borderColor: 'border-l-green-500',
    textColor: 'text-green-600',
    bgColor: 'bg-green-50',
  },
  {
    key: 'totalExpenses' as const,
    title: 'Total Expenses',
    borderColor: 'border-l-red-500',
    textColor: 'text-red-600',
    bgColor: 'bg-red-50',
  },
  {
    key: 'netBalance' as const,
    title: 'Net Balance',
    borderColor: 'border-l-blue-500',
    textColor: 'text-blue-600',
    bgColor: 'bg-blue-50',
  },
  {
    key: 'recordCount' as const,
    title: 'Transactions',
    borderColor: 'border-l-purple-500',
    textColor: 'text-purple-600',
    bgColor: 'bg-purple-50',
  },
];

export default function SummaryCards({ summary, loading }: SummaryCardsProps) {
  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {cards.map((card) => {
        const isNegativeNetBalance =
          card.key === 'netBalance' && summary && summary.netBalance < 0;

        return (
          <div
            key={card.key}
            className={`rounded-lg border border-gray-200 ${isNegativeNetBalance ? 'border-l-red-500' : card.borderColor} border-l-4 bg-white p-5 shadow-sm`}
          >
            <p className="text-sm font-medium text-gray-500">{card.title}</p>

            {loading ? (
              <div className="mt-2 h-8 w-3/4 animate-pulse rounded bg-gray-200" />
            ) : (
              <p
                className={`mt-2 text-2xl font-bold ${
                  isNegativeNetBalance ? 'text-red-600' : card.textColor
                }`}
              >
                {card.key === 'recordCount'
                  ? summary
                    ? summary[card.key].toLocaleString()
                    : '0'
                  : formatCurrency(summary ? summary[card.key] : 0)}
              </p>
            )}
          </div>
        );
      })}
    </div>
  );
}

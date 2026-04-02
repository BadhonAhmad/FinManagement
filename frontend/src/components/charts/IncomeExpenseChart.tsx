'use client';

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { TrendData } from '@/types';
import { formatCurrency } from '@/utils/formatters';

interface IncomeExpenseChartProps {
  data: TrendData[];
}

interface ChartPayload {
  totalIncome: number;
  totalExpenses: number;
  period: string;
}

function CustomTooltip({ active, payload, label }: { active?: boolean; payload?: ChartPayload[]; label?: string }) {
  if (!active || !payload || !payload.length) return null;

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-4 py-3 shadow-lg">
      <p className="mb-1 text-sm font-medium text-gray-700">{label}</p>
      {payload.map((entry, index) => (
        <p key={index} className="text-sm" style={{ color: entry === payload[0] ? '#10B981' : '#EF4444' }}>
          {entry === payload[0] ? 'Income' : 'Expenses'}: {formatCurrency(entry.totalIncome ?? entry.totalExpenses)}
        </p>
      ))}
    </div>
  );
}

export default function IncomeExpenseChart({ data }: IncomeExpenseChartProps) {
  return (
    <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
      <h3 className="mb-4 text-base font-semibold text-gray-900">Income vs Expenses</h3>

      <ResponsiveContainer width="100%" height={320}>
        <BarChart data={data} margin={{ top: 5, right: 20, left: 10, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
          <XAxis
            dataKey="period"
            tick={{ fontSize: 12, fill: '#6B7280' }}
            tickLine={false}
            axisLine={{ stroke: '#E5E7EB' }}
          />
          <YAxis
            tick={{ fontSize: 12, fill: '#6B7280' }}
            tickLine={false}
            axisLine={{ stroke: '#E5E7EB' }}
            tickFormatter={(value: number) => `$${(value / 1000).toFixed(0)}k`}
          />
          <Tooltip content={<CustomTooltip />} />
          <Legend
            verticalAlign="top"
            height={36}
            iconType="circle"
            formatter={(value: string) => (
              <span className="text-sm text-gray-600">{value}</span>
            )}
          />
          <Bar dataKey="totalIncome" name="Income" fill="#10B981" radius={[4, 4, 0, 0]} />
          <Bar dataKey="totalExpenses" name="Expenses" fill="#EF4444" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

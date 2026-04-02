'use client';

import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { CategoryTotal } from '@/types';
import { formatCurrency, getCategoryColor } from '@/utils/formatters';

interface CategoryPieChartProps {
  data: CategoryTotal[];
}

interface PiePayloadEntry {
  name: string;
  value: number;
}

function CustomTooltip({
  active,
  payload,
}: {
  active?: boolean;
  payload?: { payload: PiePayloadEntry }[];
}) {
  if (!active || !payload || !payload.length) return null;

  const entry = payload[0].payload;
  return (
    <div className="rounded-lg border border-gray-200 bg-white px-4 py-3 shadow-lg">
      <p className="text-sm font-medium text-gray-700">{entry.name}</p>
      <p className="text-sm text-gray-500">{formatCurrency(entry.value)}</p>
    </div>
  );
}

function renderCustomLabel(props: any) {
  const { cx, cy, midAngle, outerRadius, percent, name } = props;
  if (!percent || percent < 0.05) return null;

  const RADIAN = Math.PI / 180;
  const radius = outerRadius + 25;
  const x = cx + radius * Math.cos(-midAngle * RADIAN);
  const y = cy + radius * Math.sin(-midAngle * RADIAN);

  return (
    <text
      x={x}
      y={y}
      textAnchor={x > cx ? 'start' : 'end'}
      dominantBaseline="central"
      className="fill-gray-600 text-xs"
    >
      {name} ({(percent * 100).toFixed(0)}%)
    </text>
  );
}

export default function CategoryPieChart({ data }: CategoryPieChartProps) {
  const expenseData = data
    .filter((item) => item.type === 'EXPENSE')
    .map((item) => ({
      name: item.category,
      value: item.total,
    }));

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
      <h3 className="mb-4 text-base font-semibold text-gray-900">Expense Breakdown</h3>

      {expenseData.length === 0 ? (
        <div className="flex h-[320px] items-center justify-center text-gray-400">
          No expense data available
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={320}>
          <PieChart>
            <Pie
              data={expenseData}
              cx="50%"
              cy="50%"
              outerRadius={100}
              dataKey="value"
              label={renderCustomLabel}
              labelLine={false}
              strokeWidth={2}
              stroke="#fff"
            >
              {expenseData.map((entry) => (
                <Cell key={entry.name} fill={getCategoryColor(entry.name)} />
              ))}
            </Pie>
            <Tooltip content={<CustomTooltip />} />
            <Legend
              verticalAlign="bottom"
              iconType="circle"
              iconSize={8}
              formatter={(value: string) => (
                <span className="text-xs text-gray-600">{value}</span>
              )}
            />
          </PieChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}

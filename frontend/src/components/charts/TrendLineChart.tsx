'use client';

import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ReferenceLine,
} from 'recharts';
import { TrendData } from '@/types';
import { formatCurrency } from '@/utils/formatters';

interface TrendLineChartProps {
  data: TrendData[];
}

interface TrendPayload {
  period: string;
  net: number;
}

function CustomTooltip({ active, payload, label }: { active?: boolean; payload?: TrendPayload[]; label?: string }) {
  if (!active || !payload || !payload.length) return null;

  const net = payload[0].net;

  return (
    <div className="rounded-lg border border-gray-200 bg-white px-4 py-3 shadow-lg">
      <p className="mb-1 text-sm font-medium text-gray-700">{label}</p>
      <p className={`text-sm font-medium ${net >= 0 ? 'text-green-600' : 'text-red-600'}`}>
        Net: {formatCurrency(net)}
      </p>
    </div>
  );
}

export default function TrendLineChart({ data }: TrendLineChartProps) {
  const hasNegative = data.some((d) => d.net < 0);

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-5 shadow-sm">
      <h3 className="mb-4 text-base font-semibold text-gray-900">Net Balance Trend</h3>

      <ResponsiveContainer width="100%" height={320}>
        <AreaChart data={data} margin={{ top: 10, right: 20, left: 10, bottom: 5 }}>
          <defs>
            <linearGradient id="gradientPositive" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#10B981" stopOpacity={0.2} />
              <stop offset="95%" stopColor="#10B981" stopOpacity={0} />
            </linearGradient>
            <linearGradient id="gradientNegative" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#EF4444" stopOpacity={0.2} />
              <stop offset="95%" stopColor="#EF4444" stopOpacity={0} />
            </linearGradient>
          </defs>
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
          {hasNegative && (
            <ReferenceLine y={0} stroke="#9CA3AF" strokeDasharray="3 3" />
          )}
          <Area
            type="monotone"
            dataKey="net"
            stroke={hasNegative ? '#6B7280' : '#10B981'}
            strokeWidth={2}
            fill={hasNegative ? 'url(#gradientNegative)' : 'url(#gradientPositive)'}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}

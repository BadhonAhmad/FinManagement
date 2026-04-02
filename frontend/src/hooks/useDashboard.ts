'use client';

import { useState, useEffect, useCallback } from 'react';
import api from '@/lib/api';
import { DashboardSummary, CategoryTotal, TrendData, RecordResponse, ApiResponse } from '@/types';

export function useDashboard() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [categoryTotals, setCategoryTotals] = useState<CategoryTotal[]>([]);
  const [trends, setTrends] = useState<TrendData[]>([]);
  const [recentActivity, setRecentActivity] = useState<RecordResponse[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAll = useCallback(async () => {
    setLoading(true);
    try {
      const [summaryRes, categoryRes, trendsRes, recentRes] = await Promise.all([
        api.get<ApiResponse<DashboardSummary>>('/api/dashboard/summary'),
        api.get<ApiResponse<CategoryTotal[]>>('/api/dashboard/category-totals'),
        api.get<ApiResponse<TrendData[]>>('/api/dashboard/trends'),
        api.get<ApiResponse<RecordResponse[]>>('/api/dashboard/recent-activity?limit=5'),
      ]);
      setSummary(summaryRes.data.data);
      setCategoryTotals(categoryRes.data.data);
      setTrends(trendsRes.data.data);
      setRecentActivity(recentRes.data.data);
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchAll(); }, [fetchAll]);

  // Refetch when window regains focus (user navigates back from records)
  useEffect(() => {
    const onFocus = () => { fetchAll(); };
    window.addEventListener('focus', onFocus);
    return () => { window.removeEventListener('focus', onFocus); };
  }, [fetchAll]);

  return { summary, categoryTotals, trends, recentActivity, loading, refetch: fetchAll };
}

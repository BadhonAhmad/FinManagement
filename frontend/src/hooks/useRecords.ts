'use client';

import { useState, useEffect, useCallback } from 'react';
import api from '@/lib/api';
import { RecordResponse, PagedResponse, ApiResponse } from '@/types';

export function useRecords() {
  const [records, setRecords] = useState<RecordResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState<Record<string, string>>({});

  const fetchRecords = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({ page: page.toString(), size: '10' });
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });
      const response = await api.get<ApiResponse<PagedResponse<RecordResponse>>>(`/api/records?${params}`);
      setRecords(response.data.data.content);
      setTotalPages(response.data.data.totalPages);
    } catch (error) {
      console.error('Failed to fetch records:', error);
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

  useEffect(() => { fetchRecords(); }, [fetchRecords]);

  const deleteRecord = async (id: number) => {
    await api.delete(`/api/records/${id}`);
    fetchRecords();
  };

  return { records, loading, page, setPage, totalPages, filters, setFilters, refetch: fetchRecords, deleteRecord };
}

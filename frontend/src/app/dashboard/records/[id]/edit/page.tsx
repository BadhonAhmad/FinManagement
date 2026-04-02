'use client';

import RecordForm from '@/components/records/RecordForm';
import { RecordRequest, RecordResponse, ApiResponse } from '@/types';
import api from '@/lib/api';
import { useRouter, useParams } from 'next/navigation';
import toast from 'react-hot-toast';
import { useEffect, useState } from 'react';
import LoadingSpinner from '@/components/ui/LoadingSpinner';

export default function EditRecordPage() {
  const router = useRouter();
  const params = useParams();
  const [record, setRecord] = useState<RecordResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRecord = async () => {
      try {
        const response = await api.get<ApiResponse<RecordResponse>>(`/api/records/${params.id}`);
        setRecord(response.data.data);
      } catch {
        toast.error('Failed to load record');
        router.push('/dashboard/records');
      } finally {
        setLoading(false);
      }
    };
    fetchRecord();
  }, [params.id, router]);

  const handleSubmit = async (data: RecordRequest) => {
    try {
      await api.put(`/api/records/${params.id}`, data);
      toast.success('Record updated successfully');
      router.push('/dashboard/records');
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to update record');
      throw err;
    }
  };

  if (loading) return <LoadingSpinner message="Loading record..." />;

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Edit Record</h1>
        <p className="text-gray-600">Update financial entry details</p>
      </div>
      <div className="bg-white rounded-lg shadow p-6 max-w-2xl">
        {record && <RecordForm initialData={record} onSubmit={handleSubmit} isEditing />}
      </div>
    </div>
  );
}

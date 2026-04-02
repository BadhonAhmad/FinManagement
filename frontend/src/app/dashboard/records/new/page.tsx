'use client';

import RecordForm from '@/components/records/RecordForm';
import { RecordRequest } from '@/types';
import api from '@/lib/api';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function NewRecordPage() {
  const router = useRouter();

  const handleSubmit = async (data: RecordRequest) => {
    try {
      await api.post('/api/records', data);
      toast.success('Record created successfully');
      router.push('/dashboard/records');
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Failed to create record');
      throw err;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">New Record</h1>
        <p className="text-gray-600">Add a new financial entry</p>
      </div>
      <div className="bg-white rounded-lg shadow p-6 max-w-2xl">
        <RecordForm onSubmit={handleSubmit} />
      </div>
    </div>
  );
}

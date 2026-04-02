export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(amount);
};

export const formatDate = (date: string): string => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const formatDateTime = (date: string): string => {
  return new Date(date).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const getCategoryColor = (category: string): string => {
  const colors: Record<string, string> = {
    SALARY: '#10B981',
    FOOD: '#F59E0B',
    TRANSPORT: '#6366F1',
    ENTERTAINMENT: '#EC4899',
    UTILITIES: '#8B5CF6',
    HEALTHCARE: '#EF4444',
    EDUCATION: '#3B82F6',
    SHOPPING: '#F97316',
    RENT: '#14B8A6',
    INVESTMENT: '#84CC16',
    OTHER: '#6B7280',
  };
  return colors[category] || '#6B7280';
};

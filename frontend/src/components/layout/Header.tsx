'use client';

import { useState } from 'react';
import { useAuth } from '@/hooks/useAuth';

interface HeaderProps {
  onMenuToggle?: () => void;
}

const roleBadgeStyles: Record<string, string> = {
  ADMIN: 'bg-red-100 text-red-700',
  ANALYST: 'bg-blue-100 text-blue-700',
  VIEWER: 'bg-gray-100 text-gray-700',
};

export default function Header({ onMenuToggle }: HeaderProps) {
  const { user } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleMenuToggle = () => {
    setMobileMenuOpen((prev) => !prev);
    onMenuToggle?.();
  };

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-gray-200 bg-white px-4 sm:px-6">
      {/* Left: mobile hamburger */}
      <button
        onClick={handleMenuToggle}
        className="inline-flex items-center justify-center rounded-lg p-2 text-gray-500 hover:bg-gray-100 hover:text-gray-700 transition-colors lg:hidden"
        aria-label="Toggle menu"
      >
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
          {mobileMenuOpen ? (
            <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
          ) : (
            <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
          )}
        </svg>
      </button>

      {/* Right: user info */}
      <div className="flex items-center gap-3">
        {user && (
          <>
            <span className="hidden text-sm font-medium text-gray-700 sm:block">
              {user.username}
            </span>
            <span
              className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ${
                roleBadgeStyles[user.role] || 'bg-gray-100 text-gray-700'
              }`}
            >
              {user.role}
            </span>
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-600 text-sm font-bold text-white">
              {user.username.charAt(0).toUpperCase()}
            </div>
          </>
        )}
      </div>
    </header>
  );
}

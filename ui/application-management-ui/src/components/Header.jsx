import React from 'react';

const Header = () => {
  return (
    <header className="bg-blue-600 text-white shadow-lg">
      <div className="container mx-auto px-4 py-6">
        <h1 className="text-3xl font-bold">Application Management</h1>
        <p className="text-blue-100 mt-1">Create and manage your applications</p>
      </div>
    </header>
  );
};

export default Header;

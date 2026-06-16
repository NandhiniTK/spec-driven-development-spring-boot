import React, { useState } from 'react';
import { Toaster } from 'react-hot-toast';
import Header from './components/Header';
import ApplicationForm from './components/ApplicationForm';
import ApplicationTable from './components/ApplicationTable';

function App() {
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleApplicationCreated = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Toaster
        position="top-right"
        toastOptions={{
          success: {
            duration: 3000,
            style: {
              background: '#10B981',
              color: '#fff',
            },
          },
          error: {
            duration: 4000,
            style: {
              background: '#EF4444',
              color: '#fff',
            },
          },
        }}
      />
      
      <Header />
      
      <main className="container mx-auto px-4 py-8">
        <div className="max-w-7xl mx-auto space-y-8">
          <ApplicationForm onApplicationCreated={handleApplicationCreated} />
          <ApplicationTable refreshTrigger={refreshTrigger} />
        </div>
      </main>
      
      <footer className="bg-white border-t border-gray-200 mt-12">
        <div className="container mx-auto px-4 py-6 text-center text-gray-600 text-sm">
          <p>Application Management System - Built with React & Spring Boot</p>
        </div>
      </footer>
    </div>
  );
}

export default App;

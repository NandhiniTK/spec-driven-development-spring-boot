import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const applicationService = {
  /**
   * Create a new application
   * Maps UI customerId to API owner field
   */
  createApplication: async (applicationData) => {
    const apiPayload = {
      name: applicationData.name,
      description: applicationData.description,
      owner: applicationData.customerId, // Map customerId to owner
      version: '1.0.0', // Default version
      status: 'ACTIVE', // Default status
      environment: 'DEV' // Default environment
    };

    const response = await axios.post(
      `${API_BASE_URL}/api/v1/applications`,
      apiPayload
    );
    return response.data;
  },

  /**
   * Get all applications
   */
  getAllApplications: async () => {
    const response = await axios.get(`${API_BASE_URL}/api/v1/applications`);
    return response.data;
  }
};

export default applicationService;

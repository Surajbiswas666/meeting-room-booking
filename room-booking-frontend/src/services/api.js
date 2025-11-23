import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Auth APIs
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

// Room APIs
export const roomAPI = {
  getAll: () => api.get('/rooms'),
  getById: (id) => api.get(`/rooms/${id}`),
  create: (roomData) => api.post('/rooms', roomData),
  update: (id, roomData) => api.put(`/rooms/${id}`, roomData),
  delete: (id) => api.delete(`/rooms/${id}`),
  searchByCapacity: (capacity) => api.get(`/rooms/search/capacity?minCapacity=${capacity}`),
  searchByName: (name) => api.get(`/rooms/search/name?name=${name}`),
};

// Booking APIs
export const bookingAPI = {
  create: (bookingData) => api.post('/bookings', bookingData),
  getMyBookings: (userId) => api.get(`/bookings/my-bookings?userId=${userId}`),
  getAllBookings: () => api.get('/bookings/all'),
  getPending: () => api.get('/bookings/pending'),
  approve: (approvalData) => api.post('/bookings/approve', approvalData),
  cancel: (bookingId, userId) => api.delete(`/bookings/${bookingId}?userId=${userId}`),
  getById: (id) => api.get(`/bookings/${id}`),
};

// Recurring Booking APIs
export const recurringAPI = {
  create: (recurringData) => api.post('/recurring-bookings', recurringData),
  getMyRules: (userId) => api.get(`/recurring-bookings/my-rules?userId=${userId}`),
  delete: (ruleId, userId) => api.delete(`/recurring-bookings/${ruleId}?userId=${userId}`),
  processNow: () => api.post('/recurring-bookings/process-now'),
};

// Report APIs
export const reportAPI = {
  generatePDF: (reportData) => 
    api.post('/reports/bookings/pdf', reportData, { responseType: 'blob' }),
  getAnalytics: () => api.get('/reports/analytics/summary'),
  getRoomUtilization: () => api.get('/reports/analytics/room-utilization'),
  getCurrentMonth: () => api.get('/reports/bookings/current-month', { responseType: 'blob' }),
  getLastWeek: () => api.get('/reports/bookings/last-week', { responseType: 'blob' }),
};

export default api;
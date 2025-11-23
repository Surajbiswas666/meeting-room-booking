import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';

// Auth Components
import Login from './components/auth/Login';
import Register from './components/auth/Register';

// Employee Components
import EmployeeDashboard from './components/employee/Dashboard';
import CreateBooking from './components/employee/CreateBooking';
import MyBookings from './components/employee/MyBookings';
import RoomList from './components/employee/RoomList';

// Admin Components
import AdminDashboard from './components/admin/AdminDashboard';
import PendingApprovals from './components/admin/PendingApprovals';
import ManageRooms from './components/admin/ManageRooms';
import Reports from './components/admin/Reports';

// Protected Route Component
const ProtectedRoute = ({ children, adminOnly = false }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (adminOnly && user.role !== 'ADMIN') {
    return <Navigate to="/employee/dashboard" replace />;
  }

  return children;
};

// Auto-redirect based on role
const RoleBasedRedirect = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role === 'ADMIN') {
    return <Navigate to="/admin/dashboard" replace />;
  }

  return <Navigate to="/employee/dashboard" replace />;
};

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Toaster 
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#fff',
              color: '#363636',
            },
            success: {
              iconTheme: {
                primary: '#10B981',
                secondary: '#fff',
              },
            },
            error: {
              iconTheme: {
                primary: '#EF4444',
                secondary: '#fff',
              },
            },
          }}
        />
        
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Employee Routes */}
          <Route
            path="/employee/dashboard"
            element={
              <ProtectedRoute>
                <EmployeeDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/create-booking"
            element={
              <ProtectedRoute>
                <CreateBooking />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/my-bookings"
            element={
              <ProtectedRoute>
                <MyBookings />
              </ProtectedRoute>
            }
          />
          <Route
            path="/employee/rooms"
            element={
              <ProtectedRoute>
                <RoomList />
              </ProtectedRoute>
            }
          />

          {/* Admin Routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute adminOnly>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/pending-approvals"
            element={
              <ProtectedRoute adminOnly>
                <PendingApprovals />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/manage-rooms"
            element={
              <ProtectedRoute adminOnly>
                <ManageRooms />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/reports"
            element={
              <ProtectedRoute adminOnly>
                <Reports />
              </ProtectedRoute>
            }
          />

          {/* Default Route - Role-based redirect */}
          <Route path="/" element={<RoleBasedRedirect />} />

          {/* 404 Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
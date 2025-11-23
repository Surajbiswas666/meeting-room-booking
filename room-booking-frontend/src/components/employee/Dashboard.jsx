import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { bookingAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Calendar, Clock, CheckCircle, XCircle, AlertCircle, Plus } from 'lucide-react';
import toast from 'react-hot-toast';

export default function EmployeeDashboard() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({ pending: 0, approved: 0, rejected: 0 });

  useEffect(() => {
    fetchMyBookings();
  }, []);

  const fetchMyBookings = async () => {
    try {
      const response = await bookingAPI.getMyBookings(user.userId);
      const data = response.data.data;
      setBookings(data);
      
      // Calculate stats
      setStats({
        pending: data.filter(b => b.status === 'PENDING').length,
        approved: data.filter(b => b.status === 'APPROVED').length,
        rejected: data.filter(b => b.status === 'REJECTED').length,
      });
    } catch (error) {
      toast.error('Failed to fetch bookings');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'text-yellow-600 bg-yellow-50';
      case 'APPROVED': return 'text-green-600 bg-green-50';
      case 'REJECTED': return 'text-red-600 bg-red-50';
      case 'CANCELLED': return 'text-gray-600 bg-gray-50';
      default: return 'text-gray-600 bg-gray-50';
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'PENDING': return <AlertCircle className="w-5 h-5" />;
      case 'APPROVED': return <CheckCircle className="w-5 h-5" />;
      case 'REJECTED': return <XCircle className="w-5 h-5" />;
      default: return <Clock className="w-5 h-5" />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Welcome, {user?.fullName}!</h1>
          <p className="text-gray-600 mt-1">Manage your meeting room bookings</p>
          
          {/* Quick Navigation */}
          <div className="flex space-x-4 mt-4">
            <Link to="/employee/create-booking" className="text-blue-600 hover:text-blue-700 font-medium">
              Create Booking
            </Link>
            <Link to="/employee/my-bookings" className="text-blue-600 hover:text-blue-700 font-medium">
              My Bookings
            </Link>
            <Link to="/employee/rooms" className="text-blue-600 hover:text-blue-700 font-medium">
              Browse Rooms
            </Link>
          </div>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="card bg-gradient-to-br from-yellow-50 to-yellow-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-yellow-600 font-medium">Pending</p>
                <p className="text-3xl font-bold text-yellow-700">{stats.pending}</p>
              </div>
              <AlertCircle className="w-12 h-12 text-yellow-600 opacity-50" />
            </div>
          </div>

          <div className="card bg-gradient-to-br from-green-50 to-green-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-green-600 font-medium">Approved</p>
                <p className="text-3xl font-bold text-green-700">{stats.approved}</p>
              </div>
              <CheckCircle className="w-12 h-12 text-green-600 opacity-50" />
            </div>
          </div>

          <div className="card bg-gradient-to-br from-red-50 to-red-100">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-red-600 font-medium">Rejected</p>
                <p className="text-3xl font-bold text-red-700">{stats.rejected}</p>
              </div>
              <XCircle className="w-12 h-12 text-red-600 opacity-50" />
            </div>
          </div>

          <Link to="/employee/create-booking" className="card bg-gradient-to-br from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700 transition-all cursor-pointer">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-white font-medium">New Booking</p>
                <p className="text-3xl font-bold text-white">+</p>
              </div>
              <Plus className="w-12 h-12 text-white opacity-50" />
            </div>
          </Link>
        </div>

        {/* Recent Bookings */}
        <div className="card">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-bold text-gray-900">My Bookings</h2>
            <Link to="/employee/my-bookings" className="text-blue-600 hover:text-blue-700 font-medium">
              View All →
            </Link>
          </div>

          {loading ? (
            <div className="text-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
            </div>
          ) : bookings.length === 0 ? (
            <div className="text-center py-12">
              <Calendar className="w-16 h-16 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No bookings yet</p>
              <Link to="/employee/create-booking" className="btn-primary mt-4 inline-block">
                Create Your First Booking
              </Link>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Time</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Room</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Meeting</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {bookings.slice(0, 5).map((booking) => (
                    <tr key={booking.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {new Date(booking.bookingDate).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                        {booking.startTime} - {booking.endTime}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {booking.roomName}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {booking.meetingTitle}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex items-center space-x-1 px-3 py-1 rounded-full text-xs font-medium ${getStatusColor(booking.status)}`}>
                          {getStatusIcon(booking.status)}
                          <span>{booking.status}</span>
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { bookingAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Calendar, Clock, CheckCircle, XCircle, AlertCircle, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';

export default function MyBookings() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    fetchMyBookings();
  }, []);

  const fetchMyBookings = async () => {
    try {
      const response = await bookingAPI.getMyBookings(user.userId);
      setBookings(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleCancelBooking = async (bookingId) => {
    if (!window.confirm('Are you sure you want to cancel this booking?')) return;

    try {
      await bookingAPI.cancel(bookingId, user.userId);
      toast.success('Booking cancelled successfully');
      fetchMyBookings();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to cancel booking');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING': return 'text-yellow-600 bg-yellow-50 border-yellow-200';
      case 'APPROVED': return 'text-green-600 bg-green-50 border-green-200';
      case 'REJECTED': return 'text-red-600 bg-red-50 border-red-200';
      case 'CANCELLED': return 'text-gray-600 bg-gray-50 border-gray-200';
      default: return 'text-gray-600 bg-gray-50 border-gray-200';
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

  const filteredBookings = bookings.filter(booking => {
    if (filter === 'ALL') return true;
    return booking.status === filter;
  });

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">My Bookings</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">View and manage your meeting room bookings</p>
        </div>

        {/* Filter Tabs */}
        <div className="card mb-6">
          <div className="flex space-x-2 overflow-x-auto">
            {['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'].map((status) => (
              <button
                key={status}
                onClick={() => setFilter(status)}
                className={`px-4 py-2 rounded-lg font-medium whitespace-nowrap transition-colors ${
                  filter === status
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-100 dark:bg-gray-700 text-gray-600 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
                }`}
              >
                {status}
                {status === 'ALL' && ` (${bookings.length})`}
                {status !== 'ALL' && ` (${bookings.filter(b => b.status === status).length})`}
              </button>
            ))}
          </div>
        </div>

        {/* Bookings List */}
        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : filteredBookings.length === 0 ? (
          <div className="card text-center py-12">
            <Calendar className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">No bookings found</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6">
            {filteredBookings.map((booking) => (
              <div key={booking.id} className="card hover:shadow-lg transition-shadow">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <div className="flex items-center space-x-3 mb-2">
                      <h3 className="text-xl font-semibold text-gray-900">
                        {booking.meetingTitle}
                      </h3>
                      <span className={`inline-flex items-center space-x-1 px-3 py-1 rounded-full text-xs font-medium border ${getStatusColor(booking.status)}`}>
                        {getStatusIcon(booking.status)}
                        <span>{booking.status}</span>
                      </span>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-4">
                      <div className="flex items-center text-gray-600">
                        <Calendar className="w-5 h-5 mr-2" />
                        <span>{new Date(booking.bookingDate).toLocaleDateString('en-US', { 
                          weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' 
                        })}</span>
                      </div>

                      <div className="flex items-center text-gray-600">
                        <Clock className="w-5 h-5 mr-2" />
                        <span>{booking.startTime} - {booking.endTime}</span>
                      </div>

                      <div className="flex items-center text-gray-600">
                        <span className="font-medium mr-2">Room:</span>
                        <span>{booking.roomName}</span>
                      </div>

                      {booking.attendeesCount && (
                        <div className="flex items-center text-gray-600">
                          <span className="font-medium mr-2">Attendees:</span>
                          <span>{booking.attendeesCount}</span>
                        </div>
                      )}
                    </div>

                    {booking.description && (
                      <p className="mt-4 text-gray-600 text-sm">{booking.description}</p>
                    )}

                    {booking.approvedBy && (
                      <div className="mt-4 pt-4 border-t border-gray-200">
                        <p className="text-sm text-gray-500">
                          {booking.status === 'APPROVED' ? 'Approved' : 'Rejected'} by {booking.approvedByName} on{' '}
                          {new Date(booking.approvedAt).toLocaleString()}
                        </p>
                      </div>
                    )}
                  </div>

                  {/* Action Buttons */}
                  {(booking.status === 'PENDING' || booking.status === 'APPROVED') && (
                    <button
                      onClick={() => handleCancelBooking(booking.id)}
                      className="ml-4 p-2 text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                      title="Cancel Booking"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
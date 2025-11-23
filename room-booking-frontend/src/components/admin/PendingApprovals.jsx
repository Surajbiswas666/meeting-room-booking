import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { bookingAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { CheckCircle, XCircle, Calendar, Clock, User, Building2 } from 'lucide-react';
import toast from 'react-hot-toast';

export default function PendingApprovals() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPendingBookings();
  }, []);

  const fetchPendingBookings = async () => {
    try {
      const response = await bookingAPI.getPending();
      setBookings(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch pending bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleApproval = async (bookingId, approve) => {
    try {
      await bookingAPI.approve({
        bookingId,
        adminId: user.userId,
        approve,
      });

      toast.success(approve ? 'Booking approved!' : 'Booking rejected');
      fetchPendingBookings();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to process approval');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Pending Approvals</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">Review and approve booking requests</p>
        </div>

        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : bookings.length === 0 ? (
          <div className="card text-center py-12">
            <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-900 mb-2">All caught up!</h3>
            <p className="text-gray-500">No pending approvals at the moment</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6">
            {bookings.map((booking) => (
              <div key={booking.id} className="card hover:shadow-lg transition-shadow">
                <div className="flex flex-col md:flex-row md:items-center md:justify-between">
                  <div className="flex-1 mb-4 md:mb-0">
                    <h3 className="text-xl font-bold text-gray-900 mb-3">
                      {booking.meetingTitle}
                    </h3>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      <div className="flex items-center text-gray-600">
                        <User className="w-5 h-5 mr-2 text-blue-500" />
                        <span className="text-sm">
                          <span className="font-medium">Requested by:</span> {booking.userName}
                        </span>
                      </div>

                      <div className="flex items-center text-gray-600">
                        <Building2 className="w-5 h-5 mr-2 text-blue-500" />
                        <span className="text-sm">
                          <span className="font-medium">Room:</span> {booking.roomName}
                        </span>
                      </div>

                      <div className="flex items-center text-gray-600">
                        <Calendar className="w-5 h-5 mr-2 text-blue-500" />
                        <span className="text-sm">
                          {new Date(booking.bookingDate).toLocaleDateString('en-US', {
                            weekday: 'short',
                            year: 'numeric',
                            month: 'short',
                            day: 'numeric'
                          })}
                        </span>
                      </div>

                      <div className="flex items-center text-gray-600">
                        <Clock className="w-5 h-5 mr-2 text-blue-500" />
                        <span className="text-sm">{booking.startTime} - {booking.endTime}</span>
                      </div>
                    </div>

                    {booking.description && (
                      <div className="mt-3 p-3 bg-gray-50 rounded-lg">
                        <p className="text-sm text-gray-700">
                          <span className="font-medium">Description:</span> {booking.description}
                        </p>
                      </div>
                    )}

                    {booking.attendeesCount && (
                      <p className="text-sm text-gray-600 mt-2">
                        Expected attendees: <span className="font-medium">{booking.attendeesCount}</span>
                      </p>
                    )}

                    <p className="text-xs text-gray-500 mt-3">
                      Requested on: {new Date(booking.createdAt).toLocaleString()}
                    </p>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex md:flex-col space-x-3 md:space-x-0 md:space-y-3 md:ml-6">
                    <button
                      onClick={() => handleApproval(booking.id, true)}
                      className="flex-1 md:flex-none flex items-center justify-center space-x-2 px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors font-medium"
                    >
                      <CheckCircle className="w-5 h-5" />
                      <span>Approve</span>
                    </button>

                    <button
                      onClick={() => handleApproval(booking.id, false)}
                      className="flex-1 md:flex-none flex items-center justify-center space-x-2 px-6 py-3 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors font-medium"
                    >
                      <XCircle className="w-5 h-5" />
                      <span>Reject</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
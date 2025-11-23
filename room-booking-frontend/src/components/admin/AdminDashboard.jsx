import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { reportAPI, bookingAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { BarChart3, AlertCircle, CheckCircle, XCircle, TrendingUp, Building2, Users } from 'lucide-react';
import toast from 'react-hot-toast';

export default function AdminDashboard() {
  const [analytics, setAnalytics] = useState(null);
  const [roomStats, setRoomStats] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalytics();
    fetchRoomStats();
  }, []);

  const fetchAnalytics = async () => {
    try {
      const response = await reportAPI.getAnalytics();
      setAnalytics(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch analytics');
    } finally {
      setLoading(false);
    }
  };

  const fetchRoomStats = async () => {
    try {
      const response = await reportAPI.getRoomUtilization();
      setRoomStats(response.data.data);
    } catch (error) {
      console.error('Failed to fetch room stats');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Admin Dashboard</h1>
          <p className="text-gray-600 dark:text-gray-400 mt-1">Overview of all bookings and system statistics</p>
        </div>

        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : (
          <>
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <Link to="/admin/pending-approvals" className="card hover:shadow-lg transition-shadow bg-gradient-to-br from-yellow-50 to-yellow-100">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-yellow-600 font-medium">Pending Approvals</p>
                    <p className="text-3xl font-bold text-yellow-700">{analytics?.pendingBookings || 0}</p>
                  </div>
                  <AlertCircle className="w-12 h-12 text-yellow-600 opacity-50" />
                </div>
              </Link>

              <div className="card bg-gradient-to-br from-green-50 to-green-100">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-green-600 font-medium">Approved</p>
                    <p className="text-3xl font-bold text-green-700">{analytics?.approvedBookings || 0}</p>
                  </div>
                  <CheckCircle className="w-12 h-12 text-green-600 opacity-50" />
                </div>
              </div>

              <div className="card bg-gradient-to-br from-red-50 to-red-100">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-red-600 font-medium">Rejected</p>
                    <p className="text-3xl font-bold text-red-700">{analytics?.rejectedBookings || 0}</p>
                  </div>
                  <XCircle className="w-12 h-12 text-red-600 opacity-50" />
                </div>
              </div>

              <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-blue-600 font-medium">Total Bookings</p>
                    <p className="text-3xl font-bold text-blue-700">{analytics?.totalBookings || 0}</p>
                  </div>
                  <BarChart3 className="w-12 h-12 text-blue-600 opacity-50" />
                </div>
              </div>
            </div>

            {/* System Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <div className="card">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 font-medium">Total Rooms</p>
                    <p className="text-2xl font-bold text-gray-900">{analytics?.totalRooms || 0}</p>
                  </div>
                  <Building2 className="w-10 h-10 text-blue-500" />
                </div>
              </div>

              <div className="card">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 font-medium">Active Users</p>
                    <p className="text-2xl font-bold text-gray-900">{analytics?.activeUsers || 0}</p>
                  </div>
                  <Users className="w-10 h-10 text-blue-500" />
                </div>
              </div>

              <div className="card">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-gray-600 font-medium">Peak Time</p>
                    <p className="text-2xl font-bold text-gray-900">{analytics?.peakBookingTime || 'N/A'}</p>
                  </div>
                  <TrendingUp className="w-10 h-10 text-blue-500" />
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
              <Link
                to="/admin/pending-approvals"
                className="card hover:shadow-lg transition-shadow cursor-pointer bg-gradient-to-br from-yellow-500 to-yellow-600 text-white"
              >
                <h3 className="text-lg font-semibold mb-2">Pending Approvals</h3>
                <p className="text-sm opacity-90">Review and approve booking requests</p>
              </Link>

              <Link
                to="/admin/manage-rooms"
                className="card hover:shadow-lg transition-shadow cursor-pointer bg-gradient-to-br from-blue-500 to-blue-600 text-white"
              >
                <h3 className="text-lg font-semibold mb-2">Manage Rooms</h3>
                <p className="text-sm opacity-90">Add, edit, or remove meeting rooms</p>
              </Link>

              <Link
                to="/admin/reports"
                className="card hover:shadow-lg transition-shadow cursor-pointer bg-gradient-to-br from-green-500 to-green-600 text-white"
              >
                <h3 className="text-lg font-semibold mb-2">Generate Reports</h3>
                <p className="text-sm opacity-90">Download PDF reports and analytics</p>
              </Link>
            </div>

            {/* Room Utilization */}
            <div className="card">
              <h2 className="text-xl font-bold text-gray-900 mb-6">Room Utilization</h2>
              <div className="space-y-4">
                {roomStats.slice(0, 5).map((room) => (
                  <div key={room.roomId} className="flex items-center justify-between">
                    <div className="flex-1">
                      <p className="font-medium text-gray-900">{room.roomName}</p>
                      <p className="text-sm text-gray-600">
                        {room.approvedBookings} approved / {room.totalBookings} total bookings
                      </p>
                    </div>
                    <div className="ml-4">
                      <div className="text-right mb-1">
                        <span className="text-lg font-bold text-blue-600">
                          {room.utilizationPercentage}%
                        </span>
                      </div>
                      <div className="w-32 bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-blue-600 h-2 rounded-full"
                          style={{ width: `${room.utilizationPercentage}%` }}
                        />
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Most Booked Room */}
            {analytics?.mostBookedRoom && (
              <div className="card mt-6 bg-gradient-to-r from-purple-50 to-pink-50">
                <div className="flex items-center space-x-4">
                  <div className="p-3 bg-purple-500 rounded-full">
                    <Building2 className="w-8 h-8 text-white" />
                  </div>
                  <div>
                    <p className="text-sm text-purple-600 font-medium">Most Popular Room</p>
                    <p className="text-2xl font-bold text-purple-900">{analytics.mostBookedRoom}</p>
                  </div>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
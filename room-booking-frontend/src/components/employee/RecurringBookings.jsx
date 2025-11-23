import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { roomAPI, recurringAPI } from '../../services/api';
import Navbar from '../layout/Navbar';
import { Repeat, Trash2, Calendar, Clock } from 'lucide-react';
import toast from 'react-hot-toast';

export default function RecurringBookings() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [rooms, setRooms] = useState([]);
  const [rules, setRules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    roomId: '',
    meetingTitle: '',
    description: '',
    startDate: '',
    endDate: '',
    startTime: '',
    endTime: '',
    frequency: 'WEEKLY',
    daysOfWeek: [],
    attendeesCount: '',
  });

  useEffect(() => {
    fetchRooms();
    fetchMyRules();
  }, []);

  const fetchRooms = async () => {
    try {
      const response = await roomAPI.getAll();
      setRooms(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch rooms');
    }
  };

  const fetchMyRules = async () => {
    try {
      const response = await recurringAPI.getMyRules(user.userId);
      setRules(response.data.data);
    } catch (error) {
      toast.error('Failed to fetch recurring rules');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const recurringData = {
        ...formData,
        userId: user.userId,
        roomId: parseInt(formData.roomId),
        attendeesCount: parseInt(formData.attendeesCount) || 0,
        daysOfWeek: formData.frequency === 'WEEKLY' ? formData.daysOfWeek : null,
      };

      await recurringAPI.create(recurringData);
      toast.success('Recurring booking created! Individual bookings will be created automatically.');
      setShowModal(false);
      resetForm();
      fetchMyRules();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create recurring booking');
    }
  };

  const handleDelete = async (ruleId) => {
    if (!window.confirm('Delete this recurring booking rule?')) return;

    try {
      await recurringAPI.delete(ruleId, user.userId);
      toast.success('Recurring booking deleted');
      fetchMyRules();
    } catch (error) {
      toast.error('Failed to delete recurring booking');
    }
  };

  const resetForm = () => {
    setFormData({
      roomId: '',
      meetingTitle: '',
      description: '',
      startDate: '',
      endDate: '',
      startTime: '',
      endTime: '',
      frequency: 'WEEKLY',
      daysOfWeek: [],
      attendeesCount: '',
    });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const toggleDay = (day) => {
    setFormData((prev) => ({
      ...prev,
      daysOfWeek: prev.daysOfWeek.includes(day)
        ? prev.daysOfWeek.filter((d) => d !== day)
        : [...prev.daysOfWeek, day],
    }));
  };

  const dayNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <Navbar />
      
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Recurring Bookings</h1>
            <p className="text-gray-600 dark:text-gray-400 mt-1">Set up weekly/daily recurring meetings</p>
          </div>
          <button
            onClick={() => setShowModal(true)}
            className="btn-primary flex items-center space-x-2"
          >
            <Repeat className="w-5 h-5" />
            <span>New Recurring Booking</span>
          </button>
        </div>

        {loading ? (
          <div className="text-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          </div>
        ) : rules.length === 0 ? (
          <div className="card text-center py-12">
            <Repeat className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500 dark:text-gray-400">No recurring bookings yet</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6">
            {rules.map((rule) => (
              <div key={rule.id} className="card hover:shadow-lg transition-shadow">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                      {rule.meetingTitle}
                    </h3>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      <div className="flex items-center text-gray-600 dark:text-gray-400">
                        <Calendar className="w-5 h-5 mr-2" />
                        <span className="text-sm">
                          {new Date(rule.startDate).toLocaleDateString()} - {new Date(rule.endDate).toLocaleDateString()}
                        </span>
                      </div>

                      <div className="flex items-center text-gray-600 dark:text-gray-400">
                        <Clock className="w-5 h-5 mr-2" />
                        <span className="text-sm">{rule.startTime} - {rule.endTime}</span>
                      </div>

                      <div className="flex items-center text-gray-600 dark:text-gray-400">
                        <span className="text-sm font-medium mr-2">Frequency:</span>
                        <span className="text-sm">{rule.frequency}</span>
                      </div>

                      <div className="flex items-center text-gray-600 dark:text-gray-400">
                        <span className="text-sm font-medium mr-2">Room:</span>
                        <span className="text-sm">{rule.roomName}</span>
                      </div>
                    </div>

                    {rule.daysOfWeek && (
                      <div className="mt-3">
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-300">Days: </span>
                        <span className="text-sm text-gray-600 dark:text-gray-400">
                          {JSON.parse(rule.daysOfWeek).map((d) => dayNames[d - 1]).join(', ')}
                        </span>
                      </div>
                    )}

                    <div className="mt-3 text-sm text-gray-500 dark:text-gray-400">
                      Bookings created: {rule.bookingsCreated || 0}
                    </div>
                  </div>

                  <button
                    onClick={() => handleDelete(rule.id)}
                    className="ml-4 p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg transition-colors"
                  >
                    <Trash2 className="w-5 h-5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Create Modal */}
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50 overflow-y-auto">
            <div className="bg-white dark:bg-gray-800 rounded-2xl max-w-2xl w-full p-6 my-8">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
                Create Recurring Booking
              </h2>

              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Room *
                    </label>
                    <select
                      name="roomId"
                      value={formData.roomId}
                      onChange={handleChange}
                      className="input-field"
                      required
                    >
                      <option value="">Choose a room</option>
                      {rooms.map((room) => (
                        <option key={room.id} value={room.id}>
                          {room.name}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Meeting Title *
                    </label>
                    <input
                      type="text"
                      name="meetingTitle"
                      value={formData.meetingTitle}
                      onChange={handleChange}
                      className="input-field"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Start Date *
                    </label>
                    <input
                      type="date"
                      name="startDate"
                      value={formData.startDate}
                      onChange={handleChange}
                      className="input-field"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      End Date *
                    </label>
                    <input
                      type="date"
                      name="endDate"
                      value={formData.endDate}
                      onChange={handleChange}
                      className="input-field"
                      min={formData.startDate}
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Start Time *
                    </label>
                    <input
                      type="time"
                      name="startTime"
                      value={formData.startTime}
                      onChange={handleChange}
                      className="input-field"
                      required
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      End Time *
                    </label>
                    <input
                      type="time"
                      name="endTime"
                      value={formData.endTime}
                      onChange={handleChange}
                      className="input-field"
                      required
                    />
                  </div>

                  <div className="md:col-span-2">
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                      Frequency *
                    </label>
                    <select
                      name="frequency"
                      value={formData.frequency}
                      onChange={handleChange}
                      className="input-field"
                      required
                    >
                      <option value="DAILY">Daily</option>
                      <option value="WEEKLY">Weekly</option>
                      <option value="MONTHLY">Monthly</option>
                    </select>
                  </div>

                  {formData.frequency === 'WEEKLY' && (
                    <div className="md:col-span-2">
                      <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                        Select Days *
                      </label>
                      <div className="flex flex-wrap gap-2">
                        {dayNames.map((day, index) => (
                          <button
                            key={index}
                            type="button"
                            onClick={() => toggleDay(index + 1)}
                            className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                              formData.daysOfWeek.includes(index + 1)
                                ? 'bg-blue-600 text-white'
                                : 'bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300'
                            }`}
                          >
                            {day}
                          </button>
                        ))}
                      </div>
                    </div>
                  )}
                </div>

                <div className="flex space-x-3 pt-4">
                  <button
                    type="button"
                    onClick={() => { setShowModal(false); resetForm(); }}
                    className="flex-1 btn-secondary"
                  >
                    Cancel
                  </button>
                  <button type="submit" className="flex-1 btn-primary">
                    Create Recurring Booking
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}